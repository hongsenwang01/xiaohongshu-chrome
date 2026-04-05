package com.example.hello.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置信息
 */
@Component
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 服务商模式下的子商户号
     */
    private String subMerchantId;

    /**
     * 微信公众号AppId
     */
    private String appId;

    /**
     * 服务商AppId
     */
    private String spAppId;

    /**
     * API v3密钥
     */
    private String apiSecretKey;

    /**
     * 证书序列号
     */
    private String certificateSerialNumber;

    /**
     * 证书路径
     */
    private String certificatePath;

    /**
     * 微信支付网关地址
     */
    private String gatewayUrl;

    /**
     * API v3密钥 (新增，用于签名)
     */
    private String apiV3Key;

    /**
     * 开发模式
     */
    private Boolean devMode = false;

    /**
     * 回调通知地址
     */
    private String notifyUrl;

    // Getters and Setters
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSubMerchantId() {
        return subMerchantId;
    }

    public void setSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSpAppId() {
        return spAppId;
    }

    public void setSpAppId(String spAppId) {
        this.spAppId = spAppId;
    }

    public String getApiSecretKey() {
        return apiSecretKey;
    }

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
    }

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getApiV3Key() {
        return apiV3Key;
    }

    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }

    public Boolean getDevMode() {
        return devMode;
    }

    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
