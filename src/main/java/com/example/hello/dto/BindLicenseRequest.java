package com.example.hello.dto;

/**
 * 绑定授权码请求DTO
 */
public class BindLicenseRequest {
    
    private String licenseCode;     // 授权码
    private String redid;           // 小红书账号redid

    public BindLicenseRequest() {
    }

    public BindLicenseRequest(String licenseCode, String redid) {
        this.licenseCode = licenseCode;
        this.redid = redid;
    }

    // Getters and Setters
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
}

