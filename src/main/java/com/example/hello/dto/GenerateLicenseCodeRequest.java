package com.example.hello.dto;

import com.example.hello.enums.LicenseType;

/**
 * 只生成授权码请求DTO
 */
public class GenerateLicenseCodeRequest {
    
    private LicenseType licenseType;    // 授权码类型
    private Integer months;             // 有效期（月数）：1=1个月，2=2个月，以此类推
    private String notes;               // 可选：备注

    public GenerateLicenseCodeRequest() {
    }

    public GenerateLicenseCodeRequest(LicenseType licenseType, Integer months) {
        this.licenseType = licenseType;
        this.months = months;
    }

    // Getters and Setters
    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
