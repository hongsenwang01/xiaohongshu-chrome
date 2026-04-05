package com.example.hello.dto;

/**
 * 批量操作响应
 */
public class BatchOperationResponse extends SimpleResponse {
    
    private Integer successCount;
    private Integer failedCount;

    public BatchOperationResponse() {
        super();
    }

    public BatchOperationResponse(Integer code, String message) {
        super(code, message);
    }

    public BatchOperationResponse(Integer code, String message, Integer successCount, Integer failedCount) {
        super(code, message);
        this.successCount = successCount;
        this.failedCount = failedCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    @Override
    public String toString() {
        return "BatchOperationResponse{" +
                "code=" + getCode() +
                ", message='" + getMessage() + '\'' +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}

