package com.example.hello.service.impl;

import com.example.hello.dto.VersionCheckResponse;
import com.example.hello.entity.AppVersion;
import com.example.hello.repository.AppVersionRepository;
import com.example.hello.service.AppVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 应用版本管理Service实现类
 */
@Service
public class AppVersionServiceImpl implements AppVersionService {

    private static final Logger logger = LoggerFactory.getLogger(AppVersionServiceImpl.class);

    @Autowired
    private AppVersionRepository appVersionRepository;

    @Override
    public VersionCheckResponse checkVersion(String appName, Integer currentVersionCode) {
        logger.info("检查版本更新: appName={}, currentVersionCode={}", appName, currentVersionCode);

        try {
            // 查询最新版本
            Optional<AppVersion> latestVersionOpt = appVersionRepository.findLatestVersionByAppName(appName);

            if (latestVersionOpt.isEmpty()) {
                logger.warn("未找到应用版本信息: appName={}", appName);
                return new VersionCheckResponse(1, "未找到应用版本信息");
            }

            AppVersion latestVersion = latestVersionOpt.get();
            logger.info("最新版本: versionCode={}, versionName={}", 
                       latestVersion.getVersionCode(), latestVersion.getVersionName());

            // 判断是否有更新
            boolean hasUpdate = latestVersion.getVersionCode() > currentVersionCode;

            // 构建响应
            VersionCheckResponse response = new VersionCheckResponse(
                0,
                hasUpdate ? "发现新版本" : "已是最新版本",
                hasUpdate,
                hasUpdate && latestVersion.getIsForceUpdate(),
                latestVersion.getVersionCode(),
                latestVersion.getVersionName(),
                latestVersion.getDescription(),
                latestVersion.getDownloadUrl(),
                latestVersion.getFileSize(),
                currentVersionCode
            );

            logger.info("版本检查完成: hasUpdate={}, isForceUpdate={}", 
                       response.getHasUpdate(), response.getIsForceUpdate());

            return response;

        } catch (Exception e) {
            logger.error("检查版本更新异常", e);
            return new VersionCheckResponse(1, "系统异常: " + e.getMessage());
        }
    }
}
