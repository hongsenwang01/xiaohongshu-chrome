package com.example.hello.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 小红书关注关系表
 */
@Entity
@Table(name = "xhs_follow_relation", 
       uniqueConstraints = @UniqueConstraint(name = "uk_owner_target", columnNames = {"owner_redid", "target_redid"}))
public class XhsFollowRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_redid", nullable = false, length = 64)
    private String ownerRedid;

    @Column(name = "target_redid", nullable = false, length = 64)
    private String targetRedid;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "profile_url", length = 255)
    private String profileUrl;

    @Column(name = "is_followed_back", nullable = false)
    private Boolean isFollowedBack = false;

    @Column(name = "follow_time", nullable = false)
    private LocalDateTime followTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (followTime == null) {
            followTime = now;
        }
        if (isFollowedBack == null) {
            isFollowedBack = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerRedid() {
        return ownerRedid;
    }

    public void setOwnerRedid(String ownerRedid) {
        this.ownerRedid = ownerRedid;
    }

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

    public Boolean getIsFollowedBack() {
        return isFollowedBack;
    }

    public void setIsFollowedBack(Boolean isFollowedBack) {
        this.isFollowedBack = isFollowedBack;
    }

    public LocalDateTime getFollowTime() {
        return followTime;
    }

    public void setFollowTime(LocalDateTime followTime) {
        this.followTime = followTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

