package com.example.hello.dto;

/**
 * 微信Native支付响应DTO
 */
public class WechatNativePayResponse {

    /**
     * 返回状态码：0-成功, 其他-失败
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 二维码链接（code_url）- 用于生成二维码的URL
     */
    private String codeUrl;

    /**
     * 二维码图片Base64编码（可选，可在前端自己生成）
     */
    private String qrCodeImage;

    /**
     * 二维码支付链接（微信标准格式）
     */
    private String qrCodeUrl;

    /**
     * 订单状态：PENDING-待支付, SUCCESS-已支付, CLOSED-已关闭, REFUNDING-退款中, REFUNDED-已退款
     */
    private String status;

    /**
     * 订单金额（分）
     */
    private Integer amount;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 创建时间戳
     */
    private Long timestamp;

    public WechatNativePayResponse() {
    }

    public WechatNativePayResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
