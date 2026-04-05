package com.example.hello.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 授权码-账号绑定表
 */
@Entity
@Table(name = "xhs_license_bind")
public class XhsLicenseBind {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_id", nullable = false)
    private Long licenseId;

    @Column(name = "redid", nullable = false, unique = true, length = 128)
    private String redid;

    @Column(name = "bound_at", nullable = false, updatable = false)
    private LocalDateTime boundAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id", insertable = false, updatable = false)
    private XhsLicense license;

    @PrePersist
    protected void onCreate() {
        boundAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Long licenseId) {
        this.licenseId = licenseId;
    }

    public String getRedid() {
        return redid;
    }

    public void setRedid(String redid) {
        this.redid = redid;
    }

    public LocalDateTime getBoundAt() {
        return boundAt;
    }

    public void setBoundAt(LocalDateTime boundAt) {
        this.boundAt = boundAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public XhsLicense getLicense() {
        return license;
    }

    public void setLicense(XhsLicense license) {
        this.license = license;
    }
}

