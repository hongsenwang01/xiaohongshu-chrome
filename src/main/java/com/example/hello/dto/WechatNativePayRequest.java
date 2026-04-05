package com.example.hello.dto;

/**
 * 微信Native支付请求DTO
 */
public class WechatNativePayRequest {

    /**
     * 商品描述（必填）
     */
    private String description;

    /**
     * 订单金额，单位为分（必填）
     */
    private Integer amount;

    /**
     * 商户订单号（可选，为空则自动生成）
     */
    private String outTradeNo;

    /**
     * 用户客户端IP（可选）
     */
    private String clientIp;

    /**
     * 回调通知URL（可选，使用默认配置）
     */
    private String notifyUrl;

    /**
     * 备注信息（可选）
     */
    private String remarks;

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
