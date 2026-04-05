package com.example.hello.dto;

/**
 * 授权验证请求DTO
 */
public class VerifyRequest {
    
    private String redid;

    public VerifyRequest() {
    }

    public VerifyRequest(String redid) {
        this.redid = redid;
    }

    public String getRedid() {
        return redid;
    }

    public void setRedid(String redid) {
        this.redid = redid;
    }
}

