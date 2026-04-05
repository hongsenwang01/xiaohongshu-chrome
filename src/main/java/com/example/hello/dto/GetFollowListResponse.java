package com.example.hello.dto;

import java.util.List;

/**
 * 获取关注列表响应DTO
 */
public class GetFollowListResponse {

    /**
     * 返回状态码：0-成功, 其他-失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 昵称列表
     */
    private List<String> nicknameList;

    /**
     * 创建时间戳
     */
    private Long timestamp;

    public GetFollowListResponse() {
    }

    public GetFollowListResponse(Integer code, String message) {
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

    public List<String> getNicknameList() {
        return nicknameList;
    }

    public void setNicknameList(List<String> nicknameList) {
        this.nicknameList = nicknameList;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

