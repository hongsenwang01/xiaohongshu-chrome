package com.example.hello.dto;

/**
 * 版本检查响应
 */
public class VersionCheckResponse {

    /**
     * 响应码：0=成功，1=失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 是否有新版本
     */
    private Boolean hasUpdate;

    /**
     * 是否强制更新
     */
    private Boolean isForceUpdate;

    /**
     * 最新版本号（整数）
     */
    private Integer latestVersionCode;

    /**
     * 最新版本名称（如1.0.0）
     */
    private String latestVersionName;

    /**
     * 版本描述
     */
    private String description;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 当前版本号
     */
    private Integer currentVersionCode;

    // 构造函数
    public VersionCheckResponse() {
    }

    public VersionCheckResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public VersionCheckResponse(Integer code, String message, Boolean hasUpdate, Boolean isForceUpdate,
                                Integer latestVersionCode, String latestVersionName, String description,
                                String downloadUrl, Long fileSize, Integer currentVersionCode) {
        this.code = code;
        this.message = message;
        this.hasUpdate = hasUpdate;
        this.isForceUpdate = isForceUpdate;
        this.latestVersionCode = latestVersionCode;
        this.latestVersionName = latestVersionName;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.fileSize = fileSize;
        this.currentVersionCode = currentVersionCode;
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(Boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public Boolean getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(Boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public Integer getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(Integer latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public String getLatestVersionName() {
        return latestVersionName;
    }

    public void setLatestVersionName(String latestVersionName) {
        this.latestVersionName = latestVersionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getCurrentVersionCode() {
        return currentVersionCode;
    }

    public void setCurrentVersionCode(Integer currentVersionCode) {
        this.currentVersionCode = currentVersionCode;
    }
}
