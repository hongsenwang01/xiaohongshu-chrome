package com.example.hello.dto;

import com.example.hello.enums.LicenseType;

/**
 * 创建授权码请求DTO
 */
public class CreateLicenseRequest {
    
    private String redid;           // 小红书账号redid
    private Integer months;         // 有效期（月数）：1=1个月，2=2个月，以此类推
    private Integer maxBindings;    // 可选：最大绑定数，默认为1（因为创建时直接绑定）
    private LicenseType licenseType; // 可选：授权码类型，默认为普通授权码
    private String notes;           // 可选：备注

    public CreateLicenseRequest() {
    }

    public CreateLicenseRequest(String redid, Integer months) {
        this.redid = redid;
        this.months = months;
    }

    // Getters and Setters
    public String getRedid() {
        return redid;
    }

    public void setRedid(String redid) {
        this.redid = redid;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getMaxBindings() {
        return maxBindings;
    }

    public void setMaxBindings(Integer maxBindings) {
        this.maxBindings = maxBindings;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }
}

