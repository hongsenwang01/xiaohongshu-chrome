package com.example.hello.dto;

import com.example.hello.enums.LicenseType;
import java.time.LocalDateTime;

/**
 * 只生成授权码响应DTO
 */
public class GenerateLicenseCodeResponse {
    
    private boolean success;            // 是否成功
    private String message;             // 提示信息
    private String licenseCode;         // 生成的授权码
    private LicenseType licenseType;    // 授权码类型
    private LocalDateTime expiresAt;    // 到期时间
    private LocalDateTime createdAt;    // 创建时间

    public GenerateLicenseCodeResponse() {
    }

    public GenerateLicenseCodeResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static GenerateLicenseCodeResponse success(String licenseCode, 
                                                      LicenseType licenseType, 
                                                      LocalDateTime expiresAt, 
                                                      LocalDateTime createdAt) {
        GenerateLicenseCodeResponse response = new GenerateLicenseCodeResponse(true, "授权码生成成功");
        response.setLicenseCode(licenseCode);
        response.setLicenseType(licenseType);
        response.setExpiresAt(expiresAt);
        response.setCreatedAt(createdAt);
        return response;
    }

    public static GenerateLicenseCodeResponse fail(String message) {
        return new GenerateLicenseCodeResponse(false, message);
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
