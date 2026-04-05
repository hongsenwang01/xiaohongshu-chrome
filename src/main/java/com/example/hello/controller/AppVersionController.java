package com.example.hello.controller;

import com.example.hello.dto.VersionCheckResponse;
import com.example.hello.service.AppVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 应用版本管理控制器
 * 提供版本检查接口，用于客户端查询是否有新版本
 */
@RestController
@RequestMapping("/api/version")
@CrossOrigin(origins = "*")
public class AppVersionController {

    private static final Logger logger = LoggerFactory.getLogger(AppVersionController.class);

    @Autowired
    private AppVersionService appVersionService;

    /**
     * 检查版本更新接口
     * 
     * 接口地址：GET /api/version/check?appName=xxx&versionCode=100
     * 
     * 说明：
     * - 客户端传入应用名称和当前版本号
     * - 服务端返回是否有新版本及相关信息
     * - 如果有新版本，会返回版本详情、下载地址等
     * - 支持强制更新标识
     * 
     * 请求参数示例：
     * appName=MyApp&versionCode=100
     * 
     * 响应示例（有新版本）：
     * {
     *   "code": 0,
     *   "message": "发现新版本",
     *   "hasUpdate": true,
     *   "isForceUpdate": false,
     *   "latestVersionCode": 101,
     *   "latestVersionName": "1.0.1",
     *   "description": "修复了若干bug，优化了性能",
     *   "downloadUrl": "https://example.com/app-v1.0.1.apk",
     *   "fileSize": 52428800,
     *   "currentVersionCode": 100
     * }
     * 
     * 响应示例（无新版本）：
     * {
     *   "code": 0,
     *   "message": "已是最新版本",
     *   "hasUpdate": false,
     *   "isForceUpdate": false,
     *   "latestVersionCode": 100,
     *   "latestVersionName": "1.0.0",
     *   "description": null,
     *   "downloadUrl": null,
     *   "fileSize": null,
     *   "currentVersionCode": 100
     * }
     *
     * @param appName 应用名称
     * @param versionCode 当前版本号
     * @return 版本检查响应
     */
    @GetMapping("/check")
    public VersionCheckResponse checkVersion(
            @RequestParam String appName,
            @RequestParam Integer versionCode) {
        
        logger.info("收到版本检查请求: appName={}, versionCode={}", appName, versionCode);

        // 参数校验
        if (appName == null || appName.trim().isEmpty()) {
            return new VersionCheckResponse(1, "应用名称不能为空");
        }

        if (versionCode == null || versionCode < 0) {
            return new VersionCheckResponse(1, "版本号无效");
        }

        try {
            VersionCheckResponse response = appVersionService.checkVersion(appName, versionCode);
            logger.info("版本检查响应: code={}, message={}, hasUpdate={}", 
                       response.getCode(), response.getMessage(), response.getHasUpdate());
            return response;
        } catch (Exception e) {
            logger.error("版本检查接口异常", e);
            return new VersionCheckResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * 
     * 接口地址：GET /api/version/health
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public VersionCheckResponse health() {
        return new VersionCheckResponse(0, "版本管理服务正常");
    }
}
