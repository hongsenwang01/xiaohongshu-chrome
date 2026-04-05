package com.example.hello.dto;

import com.example.hello.enums.LicenseType;
import java.time.LocalDateTime;

/**
 * 授权验证响应DTO
 */
public class VerifyResponse {
    
    private boolean valid;              // 是否有效
    private String message;             // 提示信息
    private String licenseCode;         // 授权码
    private LicenseType licenseType;    // 授权码类型
    private LocalDateTime expiresAt;    // 到期时间
    
    public VerifyResponse() {
    }
    
    public VerifyResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public static VerifyResponse success(String licenseCode, LicenseType licenseType, 
                                         LocalDateTime expiresAt) {
        VerifyResponse response = new VerifyResponse(true, "授权验证成功");
        response.setLicenseCode(licenseCode);
        response.setLicenseType(licenseType);
        response.setExpiresAt(expiresAt);
        return response;
    }
    
    public static VerifyResponse fail(String message) {
        return new VerifyResponse(false, message);
    }

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }
}

