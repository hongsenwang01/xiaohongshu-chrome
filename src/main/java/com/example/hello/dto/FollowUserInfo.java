package com.example.hello.dto;

/**
 * 关注用户信息
 */
public class FollowUserInfo {

    /**
     * 被关注人的小红书号
     */
    private String targetRedid;

    /**
     * 被关注人昵称
     */
    private String nickname;

    /**
     * 被关注人头像地址
     */
    private String avatarUrl;

    /**
     * 被关注人主页链接
     */
    private String profileUrl;

    // Getters and Setters
    public String getTargetRedid() {
        return targetRedid;
    }

    public void setTargetRedid(String targetRedid) {
        this.targetRedid = targetRedid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}

