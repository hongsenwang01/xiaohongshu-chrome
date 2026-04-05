package com.example.hello.dto;

/**
 * 主页链接项DTO（包含id和url）
 */
public class ProfileUrlItem {

    /**
     * 记录ID（原表主键）
     */
    private Long id;

    /**
     * 主页链接
     */
    private String profileUrl;

    public ProfileUrlItem() {
    }

    public ProfileUrlItem(Long id, String profileUrl) {
        this.id = id;
        this.profileUrl = profileUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}

