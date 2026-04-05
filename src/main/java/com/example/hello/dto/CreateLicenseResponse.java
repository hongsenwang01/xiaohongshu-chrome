package com.example.hello.dto;

import com.example.hello.enums.LicenseType;
import java.time.LocalDateTime;

/**
 * 创建授权码响应DTO
 */
public class CreateLicenseResponse {
    
    private boolean success;            // 是否成功
    private String message;             // 提示信息
    private String licenseCode;         // 生成的授权码
    private String redid;               // 绑定的redid
    private LicenseType licenseType;    // 授权码类型
    private LocalDateTime expiresAt;    // 到期时间
    private LocalDateTime createdAt;    // 创建时间

    public CreateLicenseResponse() {
    }

    public CreateLicenseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static CreateLicenseResponse success(String licenseCode, String redid, 
                                                 LicenseType licenseType, LocalDateTime expiresAt, 
                                                 LocalDateTime createdAt) {
        CreateLicenseResponse response = new CreateLicenseResponse(true, "授权码创建成功");
        response.setLicenseCode(licenseCode);
        response.setRedid(redid);
        response.setLicenseType(licenseType);
        response.setExpiresAt(expiresAt);
        response.setCreatedAt(createdAt);
        return response;
    }

    public static CreateLicenseResponse fail(String message) {
        return new CreateLicenseResponse(false, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public String getRedid() {
        return redid;
    }

    public void setRedid(String redid) {
        this.redid = redid;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }
}

