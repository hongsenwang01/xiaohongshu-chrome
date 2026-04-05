package com.example.hello.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 微信支付回调通知请求 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WechatPayNotifyRequest {

    /**
     * 通知ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 通知创建时间
     */
    @JsonProperty("create_time")
    private String createTime;

    /**
     * 通知类型
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * 回调资源信息
     */
    @JsonProperty("resource")
    private Resource resource;

    /**
     * 资源内部的数据
     */
    public static class Resource {
        /**
         * 加密算法类型
         */
        @JsonProperty("algorithm")
        public String algorithm;

        /**
         * 加密数据
         */
        @JsonProperty("ciphertext")
        public String ciphertext;

        /**
         * 原始类型
         */
        @JsonProperty("original_type")
        public String originalType;

        /**
         * 随机数
         */
        @JsonProperty("associated_data")
        public String associatedData;

        /**
         * 认证标签
         */
        @JsonProperty("nonce")
        public String nonce;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
