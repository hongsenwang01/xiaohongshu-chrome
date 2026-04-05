package com.example.hello.dto;

import java.util.List;

/**
 * 上传关注关系请求DTO
 */
public class UploadFollowRelationRequest {

    /**
     * 号主的小红书号（谁的关注列表）
     */
    private String ownerRedid;

    /**
     * 关注用户列表
     */
    private List<FollowUserInfo> followList;

    // Getters and Setters
    public String getOwnerRedid() {
        return ownerRedid;
    }

    public void setOwnerRedid(String ownerRedid) {
        this.ownerRedid = ownerRedid;
    }

    public List<FollowUserInfo> getFollowList() {
        return followList;
    }

    public void setFollowList(List<FollowUserInfo> followList) {
        this.followList = followList;
    }
}

