package com.example.hello.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 许可证API配置信息
 */
@Component
@ConfigurationProperties(prefix = "license.api")
public class LicenseApiConfig {

    /**
     * API Token（用于保护管理接口）
     */
    private String token;

    // Getter and Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
