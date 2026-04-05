package com.example.hello.controller;

import com.example.hello.annotation.RequireApiToken;
import com.example.hello.dto.BindLicenseRequest;
import com.example.hello.dto.BindLicenseResponse;
import com.example.hello.dto.CreateLicenseRequest;
import com.example.hello.dto.CreateLicenseResponse;
import com.example.hello.dto.GenerateLicenseCodeRequest;
import com.example.hello.dto.GenerateLicenseCodeResponse;
import com.example.hello.dto.VerifyRequest;
import com.example.hello.dto.VerifyResponse;
import com.example.hello.service.LicenseVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 授权验证控制器
 */
@RestController
@RequestMapping("/api/license")
@CrossOrigin(origins = "*")
public class LicenseVerifyController {

    @Autowired
    private LicenseVerifyService licenseVerifyService;

    /**
     * 验证授权接口
     * 
     * 接口地址：POST /api/license/verify
     * 请求示例：{"redid": "xxx"}
     * 
     * @param request 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public VerifyResponse verify(@RequestBody VerifyRequest request) {
        return licenseVerifyService.verifyLicense(request.getRedid());
    }

    /**
     * 简化的验证接口（使用GET方法）
     * 
     * 接口地址：GET /api/license/verify?redid=xxx
     * 
     * @param redid 小红书账号redid
     * @return 验证结果
     */
    @GetMapping("/verify")
    public VerifyResponse verifyByGet(@RequestParam String redid) {
        return licenseVerifyService.verifyLicense(redid);
    }

    /**
     * 创建授权码并绑定接口
     * 
     * 接口地址：POST /api/license/create
     * 请求示例：{"redid": "xxx", "months": 1, "notes": "备注"}
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public CreateLicenseResponse createLicense(@RequestBody CreateLicenseRequest request) {
        return licenseVerifyService.createLicense(request);
    }

    /**
     * 绑定授权码接口
     * 
     * 接口地址：POST /api/license/bind
     * 请求示例：{"licenseCode": "XHS-12345678", "redid": "xxx"}
     * 
     * @param request 绑定请求
     * @return 绑定结果
     */
    @PostMapping("/bind")
    public BindLicenseResponse bindLicense(@RequestBody BindLicenseRequest request) {
        return licenseVerifyService.bindLicense(request);
    }

    /**
     * 只生成授权码接口（不绑定）
     * 
     * 接口地址：POST /api/license/generate
     * 请求示例：{"licenseType": "STANDARD", "months": 1, "notes": "备注"}
     * 请求头：X-API-Token: your-token-value
     * 
     * @param request 生成请求
     * @return 生成结果
     */
    @RequireApiToken
    @PostMapping("/generate")
    public GenerateLicenseCodeResponse generateLicenseCode(@RequestBody GenerateLicenseCodeRequest request) {
        return licenseVerifyService.generateLicenseCodeOnly(request);
    }
}

