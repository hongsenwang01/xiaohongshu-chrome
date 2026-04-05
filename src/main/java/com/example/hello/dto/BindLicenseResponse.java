package com.example.hello.dto;

import com.example.hello.enums.LicenseType;
import java.time.LocalDateTime;

/**
 * 绑定授权码响应DTO
 */
public class BindLicenseResponse {
    
    private boolean success;            // 是否成功
    private String message;             // 提示信息
    private String licenseCode;         // 授权码
    private String redid;               // 绑定的redid
    private LicenseType licenseType;    // 授权码类型
    private Integer boundCount;         // 当前已绑定数量
    private Integer maxBindings;        // 最大绑定数量
    private LocalDateTime expiresAt;    // 到期时间

    public BindLicenseResponse() {
    }

    public BindLicenseResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static BindLicenseResponse success(String licenseCode, String redid, 
                                               LicenseType licenseType, Integer boundCount, 
                                               Integer maxBindings, LocalDateTime expiresAt) {
        BindLicenseResponse response = new BindLicenseResponse(true, "绑定成功");
        response.setLicenseCode(licenseCode);
        response.setRedid(redid);
        response.setLicenseType(licenseType);
        response.setBoundCount(boundCount);
        response.setMaxBindings(maxBindings);
        response.setExpiresAt(expiresAt);
        return response;
    }

    public static BindLicenseResponse success(String licenseCode, String redid, 
                                               LicenseType licenseType, Integer boundCount, 
                                               Integer maxBindings, LocalDateTime expiresAt,
                                               String customMessage) {
        BindLicenseResponse response = new BindLicenseResponse(true, customMessage);
        response.setLicenseCode(licenseCode);
        response.setRedid(redid);
        response.setLicenseType(licenseType);
        response.setBoundCount(boundCount);
        response.setMaxBindings(maxBindings);
        response.setExpiresAt(expiresAt);
        return response;
    }

    public static BindLicenseResponse fail(String message) {
        return new BindLicenseResponse(false, message);
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

    public Integer getBoundCount() {
        return boundCount;
    }

    public void setBoundCount(Integer boundCount) {
        this.boundCount = boundCount;
    }

    public Integer getMaxBindings() {
        return maxBindings;
    }

    public void setMaxBindings(Integer maxBindings) {
        this.maxBindings = maxBindings;
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

