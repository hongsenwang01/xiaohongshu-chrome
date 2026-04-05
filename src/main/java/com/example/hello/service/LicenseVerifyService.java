package com.example.hello.service;

import com.example.hello.dto.BindLicenseRequest;
import com.example.hello.dto.BindLicenseResponse;
import com.example.hello.dto.CreateLicenseRequest;
import com.example.hello.dto.CreateLicenseResponse;
import com.example.hello.dto.GenerateLicenseCodeRequest;
import com.example.hello.dto.GenerateLicenseCodeResponse;
import com.example.hello.dto.VerifyResponse;
import com.example.hello.entity.XhsLicense;
import com.example.hello.entity.XhsLicenseBind;
import com.example.hello.enums.LicenseType;
import com.example.hello.repository.XhsLicenseBindRepository;
import com.example.hello.repository.XhsLicenseRepository;
import com.example.hello.config.LicenseApiConfig;
import com.example.hello.util.SignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 授权验证服务
 */
@Service
public class LicenseVerifyService {

    @Autowired
    private XhsLicenseBindRepository licenseBindRepository;

    @Autowired
    private XhsLicenseRepository licenseRepository;

    @Autowired
    private LicenseApiConfig licenseApiConfig;

    /**
     * 验证redid的授权是否有效
     * 如果是新号（未绑定授权码），自动创建体验授权码
     * 
     * @param redid 小红书账号redid
     * @return 验证结果
     */
    @Transactional
    public VerifyResponse verifyLicense(String redid) {
        // 1. 检查redid是否为空
        if (redid == null || redid.trim().isEmpty()) {
            return VerifyResponse.fail("redid不能为空");
        }

        String trimmedRedid = redid.trim();

        // 2. 查找绑定记录
        Optional<XhsLicenseBind> bindOpt = licenseBindRepository.findByRedid(trimmedRedid);
        
        // 2.1 如果未绑定授权码，自动创建体验授权码
        if (bindOpt.isEmpty()) {
            XhsLicense trialLicense = createTrialLicense(trimmedRedid);
            return VerifyResponse.success(
                trialLicense.getCode(), 
                trialLicense.getLicenseType(), 
                trialLicense.getExpiresAt()
            );
        }

        XhsLicenseBind bind = bindOpt.get();

        // 3. 查找授权码信息
        Optional<XhsLicense> licenseOpt = licenseRepository.findById(bind.getLicenseId());
        if (licenseOpt.isEmpty()) {
            return VerifyResponse.fail("授权码不存在");
        }

        XhsLicense license = licenseOpt.get();

        // 4. 检查授权码状态
        if (license.getStatus() != 1) {
            return VerifyResponse.fail("授权码已被停用或吊销");
        }

        // 5. 检查是否过期
        LocalDateTime now = LocalDateTime.now();
        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(now)) {
            return VerifyResponse.fail("授权码已过期");
        }

        // 6. 更新最近访问时间
        licenseBindRepository.updateLastSeenAt(trimmedRedid, now);

        // 7. 返回成功结果
        return VerifyResponse.success(license.getCode(), license.getLicenseType(), license.getExpiresAt());
    }

    /**
     * 为新用户创建体验授权码
     * 
     * @param redid 小红书账号redid
     * @return 创建的体验授权码
     */
    private XhsLicense createTrialLicense(String redid) {
        // 1. 生成体验授权码
        String licenseCode = generateLicenseCode(LicenseType.TRIAL);
        
        // 2. 设置体验期限（默认3天）
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(3);
        
        // 3. 创建授权码记录
        XhsLicense license = new XhsLicense();
        license.setCode(licenseCode);
        license.setLicenseType(LicenseType.TRIAL);
        license.setMaxBindings(1);  // 体验账号只能绑定1个
        license.setBoundCount(1);   // 创建时就绑定
        license.setExpiresAt(expiresAt);
        license.setStatus(1);
        license.setNotes("自动创建的体验授权码");
        
        // 保存授权码
        license = licenseRepository.save(license);
        
        // 4. 创建绑定记录
        XhsLicenseBind bind = new XhsLicenseBind();
        bind.setLicenseId(license.getId());
        bind.setRedid(redid);
        bind.setLastSeenAt(LocalDateTime.now());
        licenseBindRepository.save(bind);
        
        return license;
    }

    /**
     * 创建授权码并绑定到redid
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @Transactional
    public CreateLicenseResponse createLicense(CreateLicenseRequest request) {
        // 1. 参数验证
        if (request.getRedid() == null || request.getRedid().trim().isEmpty()) {
            return CreateLicenseResponse.fail("redid不能为空");
        }
        if (request.getMonths() == null || request.getMonths() <= 0) {
            return CreateLicenseResponse.fail("有效期月数必须大于0");
        }

        String redid = request.getRedid().trim();

        // 2. 检查该redid是否已经绑定了授权码
        Optional<XhsLicenseBind> existingBind = licenseBindRepository.findByRedid(redid);
        if (existingBind.isPresent()) {
            return CreateLicenseResponse.fail("该账号已绑定授权码，不能重复绑定");
        }

        // 3. 确定授权码类型（默认为普通授权码）
        LicenseType licenseType = request.getLicenseType() != null ? request.getLicenseType() : LicenseType.STANDARD;

        // 4. 根据授权码类型设置默认的最大绑定数
        int defaultMaxBindings;
        switch (licenseType) {
            case TRIAL:
                defaultMaxBindings = 1;  // 体验授权码：1个
                break;
            case PREMIUM:
                defaultMaxBindings = 10; // 高级授权码：10个
                break;
            case STANDARD:
            default:
                defaultMaxBindings = 3;  // 普通授权码：3个
                break;
        }

        // 5. 生成唯一的授权码（根据类型生成不同格式）
        String licenseCode = generateLicenseCode(licenseType);

        // 6. 计算到期时间（体验授权码固定3天，其他类型按月数计算）
        LocalDateTime expiresAt;
        if (licenseType == LicenseType.TRIAL) {
            expiresAt = LocalDateTime.now().plusDays(3);  // 体验授权码：3天
        } else {
            expiresAt = LocalDateTime.now().plusMonths(request.getMonths());  // 其他类型：按月数
        }

        // 7. 创建授权码记录
        XhsLicense license = new XhsLicense();
        license.setCode(licenseCode);
        license.setMaxBindings(request.getMaxBindings() != null ? request.getMaxBindings() : defaultMaxBindings);
        license.setBoundCount(1);  // 创建时就绑定，所以初始为1
        license.setExpiresAt(expiresAt);
        license.setStatus(1);      // 默认启用
        license.setLicenseType(licenseType);
        license.setNotes(request.getNotes());
        
        // 保存授权码
        license = licenseRepository.save(license);

        // 8. 创建绑定记录
        XhsLicenseBind bind = new XhsLicenseBind();
        bind.setLicenseId(license.getId());
        bind.setRedid(redid);
        bind.setLastSeenAt(LocalDateTime.now());
        
        // 保存绑定记录
        licenseBindRepository.save(bind);

        // 9. 返回成功结果
        return CreateLicenseResponse.success(
            licenseCode, 
            redid, 
            license.getLicenseType(),
            expiresAt, 
            license.getCreatedAt()
        );
    }

    /**
     * 绑定授权码到redid
     * 
     * @param request 绑定请求
     * @return 绑定结果
     */
    @Transactional
    public BindLicenseResponse bindLicense(BindLicenseRequest request) {
        // 1. 参数验证
        if (request.getLicenseCode() == null || request.getLicenseCode().trim().isEmpty()) {
            return BindLicenseResponse.fail("授权码不能为空");
        }
        if (request.getRedid() == null || request.getRedid().trim().isEmpty()) {
            return BindLicenseResponse.fail("redid不能为空");
        }

        String licenseCode = request.getLicenseCode().trim();
        String redid = request.getRedid().trim();

        // 2. 查找授权码
        Optional<XhsLicense> licenseOpt = licenseRepository.findByCode(licenseCode);
        if (licenseOpt.isEmpty()) {
            return BindLicenseResponse.fail("授权码不存在");
        }

        XhsLicense license = licenseOpt.get();

        // 3. 检查授权码状态
        if (license.getStatus() != 1) {
            return BindLicenseResponse.fail("授权码已被停用或吊销");
        }

        // 4. 检查是否过期
        LocalDateTime now = LocalDateTime.now();
        if (license.getExpiresAt() != null && license.getExpiresAt().isBefore(now)) {
            return BindLicenseResponse.fail("授权码已过期");
        }

        // 5. 检查该redid是否已经绑定了其他授权码
        Optional<XhsLicenseBind> existingBind = licenseBindRepository.findByRedid(redid);
        if (existingBind.isPresent()) {
            // 检查是否绑定的是同一个授权码
            if (existingBind.get().getLicenseId().equals(license.getId())) {
                return BindLicenseResponse.fail("该账号已绑定此授权码，无需重复绑定");
            } else {
                // 该账号已绑定其他授权码，自动更换授权码（常见场景：从体验版升级到正式版）
                Long oldLicenseId = existingBind.get().getLicenseId();
                
                // 5.1 处理旧授权码
                Optional<XhsLicense> oldLicenseOpt = licenseRepository.findById(oldLicenseId);
                if (oldLicenseOpt.isPresent()) {
                    XhsLicense oldLicense = oldLicenseOpt.get();
                    
                    // 判断旧授权码类型
                    if (oldLicense.getLicenseType() == LicenseType.TRIAL) {
                        // 体验授权码：直接标记为已过期
                        oldLicense.setStatus(2);
                        licenseRepository.save(oldLicense);
                    } else {
                        // 正式授权码（STANDARD/PREMIUM）：检查是否过期
                        if (oldLicense.getExpiresAt() != null && oldLicense.getExpiresAt().isBefore(now)) {
                            // 已过期：标记为已过期
                            oldLicense.setStatus(2);
                            licenseRepository.save(oldLicense);
                        } else {
                            // 未过期：减少绑定数量（保留授权码供其他账号使用）
                            int oldBoundCount = Math.max(0, oldLicense.getBoundCount() - 1);
                            oldLicense.setBoundCount(oldBoundCount);
                            licenseRepository.save(oldLicense);
                        }
                    }
                }
                
                // 5.2 检查新授权码的绑定数量是否已达上限
                int currentBoundCount = licenseBindRepository.countByLicenseId(license.getId());
                if (currentBoundCount >= license.getMaxBindings()) {
                    return BindLicenseResponse.fail("新授权码绑定数量已达上限（" + license.getMaxBindings() + "个）");
                }
                
                // 5.3 更新绑定记录到新授权码
                XhsLicenseBind bind = existingBind.get();
                bind.setLicenseId(license.getId());
                bind.setLastSeenAt(now);
                licenseBindRepository.save(bind);
                
                // 5.4 更新新授权码的绑定数量
                license.setBoundCount(currentBoundCount + 1);
                licenseRepository.save(license);
                
                // 5.5 返回成功结果（更换授权码成功）
                return BindLicenseResponse.success(
                    licenseCode,
                    redid,
                    license.getLicenseType(),
                    currentBoundCount + 1,
                    license.getMaxBindings(),
                    license.getExpiresAt(),
                    "授权码已更换成功（旧授权码已自动解绑）"
                );
            }
        }

        // 6. 检查授权码的绑定数量是否已达上限（新绑定的情况）
        int currentBoundCount = licenseBindRepository.countByLicenseId(license.getId());
        if (currentBoundCount >= license.getMaxBindings()) {
            return BindLicenseResponse.fail("授权码绑定数量已达上限（" + license.getMaxBindings() + "个）");
        }

        // 7. 创建绑定记录（首次绑定）
        XhsLicenseBind bind = new XhsLicenseBind();
        bind.setLicenseId(license.getId());
        bind.setRedid(redid);
        bind.setLastSeenAt(now);
        licenseBindRepository.save(bind);

        // 8. 更新授权码的绑定数量
        license.setBoundCount(currentBoundCount + 1);
        licenseRepository.save(license);

        // 9. 返回成功结果
        return BindLicenseResponse.success(
            licenseCode,
            redid,
            license.getLicenseType(),
            currentBoundCount + 1,
            license.getMaxBindings(),
            license.getExpiresAt()
        );
    }

    /**
     * 生成唯一的授权码
     * 根据授权码类型生成不同格式：
     * - 体验授权码: XHS-TY-XXXXXXXX
     * - 普通授权码: XHS-XXXXXXXX
     * - 高级授权码: XHS-GJ-XXXXXXXX
     * 
     * @param licenseType 授权码类型
     * @return 授权码
     */
    private String generateLicenseCode(LicenseType licenseType) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String code = uuid.substring(0, 8);
        
        switch (licenseType) {
            case TRIAL:
                return "XHS-TY-" + code;   // 体验授权码
            case PREMIUM:
                return "XHS-GJ-" + code;   // 高级授权码
            case STANDARD:
            default:
                return "XHS-" + code;      // 普通授权码
        }
    }

    /**
     * 只生成授权码（不绑定）
     * 
     * @param request 生成请求
     * @return 生成结果
     */
    @Transactional
    public GenerateLicenseCodeResponse generateLicenseCodeOnly(GenerateLicenseCodeRequest request) {
        // 1. 参数验证 - 检查业务参数
        if (request.getLicenseType() == null) {
            return GenerateLicenseCodeResponse.fail("授权码类型不能为空");
        }
        if (request.getMonths() == null || request.getMonths() <= 0) {
            return GenerateLicenseCodeResponse.fail("有效期月数必须大于0");
        }

        LicenseType licenseType = request.getLicenseType();

        // 2. 根据授权码类型设置最大绑定数
        int maxBindings;
        switch (licenseType) {
            case TRIAL:
                maxBindings = 1;  // 体验授权码：1个
                break;
            case PREMIUM:
                maxBindings = 10; // 高级授权码：10个
                break;
            case STANDARD:
            default:
                maxBindings = 3;  // 普通授权码：3个
                break;
        }

        // 5. 生成唯一的授权码（根据类型生成不同格式）
        String licenseCode = generateLicenseCode(licenseType);

        // 6. 计算到期时间（体验授权码固定3天，其他类型按月数计算）
        LocalDateTime expiresAt;
        if (licenseType == LicenseType.TRIAL) {
            expiresAt = LocalDateTime.now().plusDays(3);  // 体验授权码：3天
        } else {
            expiresAt = LocalDateTime.now().plusMonths(request.getMonths());  // 其他类型：按月数
        }

        // 7. 创建授权码记录
        XhsLicense license = new XhsLicense();
        license.setCode(licenseCode);
        license.setMaxBindings(maxBindings);  // 根据授权码类型设置最大绑定数
        license.setBoundCount(0);             // 未绑定，所以初始为0
        license.setExpiresAt(expiresAt);
        license.setStatus(1);                 // 默认启用
        license.setLicenseType(licenseType);
        license.setNotes(request.getNotes());
        
        // 保存授权码
        license = licenseRepository.save(license);

        // 6. 返回成功结果
        return GenerateLicenseCodeResponse.success(
            licenseCode, 
            license.getLicenseType(),
            expiresAt, 
            license.getCreatedAt()
        );
    }
}

