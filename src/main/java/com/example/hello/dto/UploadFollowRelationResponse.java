package com.example.hello.dto;

/**
 * 上传关注关系响应DTO
 */
public class UploadFollowRelationResponse {

    /**
     * 返回状态码：0-成功, 其他-失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 成功保存的记录数
     */
    private Integer successCount;

    /**
     * 跳过的记录数（重复数据）
     */
    private Integer skippedCount;

    /**
     * 失败的记录数
     */
    private Integer failedCount;

    /**
     * 创建时间戳
     */
    private Long timestamp;

    public UploadFollowRelationResponse() {
    }

    public UploadFollowRelationResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
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

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

