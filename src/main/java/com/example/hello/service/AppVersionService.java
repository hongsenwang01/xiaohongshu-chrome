package com.example.hello.service;

import com.example.hello.dto.VersionCheckResponse;

/**
 * 应用版本管理Service
 */
public interface AppVersionService {

    /**
     * 检查版本更新
     * 
     * @param appName 应用名称
     * @param currentVersionCode 当前版本号
     * @return 版本检查响应
     */
    VersionCheckResponse checkVersion(String appName, Integer currentVersionCode);
}
