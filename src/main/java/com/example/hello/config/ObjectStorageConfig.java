package com.example.hello.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * 对象存储配置类
 */
@Configuration
public class ObjectStorageConfig {

    @Value("${object.storage.access-key}")
    private String accessKey;

    @Value("${object.storage.secret-key}")
    private String secretKey;

    @Value("${object.storage.endpoint}")
    private String endpoint;

    @Value("${object.storage.bucket-name}")
    private String bucketName;

    @Value("${object.storage.plugin-filename}")
    private String pluginFilename;

    /**
     * 创建 S3 客户端
     */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_1)  // 对于兼容 S3 的对象存储，region 可以是任意值
                .forcePathStyle(true)       // 使用路径风格的访问（bucket-name 在路径中而不是子域名）
                .build();
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getPluginFilename() {
        return pluginFilename;
    }
}

