package com.example.hello.dto;

import java.util.List;

/**
 * 获取主页链接列表响应DTO
 */
public class GetProfileUrlListResponse {

    /**
     * 返回状态码：0-成功, 其他-失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 主页链接列表（包含id和url）
     */
    private List<ProfileUrlItem> profileUrlList;

    /**
     * 创建时间戳
     */
    private Long timestamp;

    public GetProfileUrlListResponse() {
    }

    public GetProfileUrlListResponse(Integer code, String message) {
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

    public List<ProfileUrlItem> getProfileUrlList() {
        return profileUrlList;
    }

    public void setProfileUrlList(List<ProfileUrlItem> profileUrlList) {
        this.profileUrlList = profileUrlList;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

