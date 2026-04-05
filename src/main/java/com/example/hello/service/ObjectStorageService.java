package com.example.hello.service;

import com.example.hello.config.ObjectStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 对象存储服务
 */
@Service
public class ObjectStorageService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ObjectStorageConfig storageConfig;

    /**
     * 下载插件文件
     * 
     * @return 文件字节数组
     * @throws IOException 如果下载失败
     */
    public byte[] downloadPlugin() throws IOException {
        String bucketName = storageConfig.getBucketName();
        String filename = storageConfig.getPluginFilename();

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            
            // 读取文件内容到字节数组
            return readAllBytes(response);
            
        } catch (S3Exception e) {
            throw new IOException("从对象存储下载文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件信息
     * 
     * @return 文件元数据
     */
    public HeadObjectResponse getPluginFileInfo() {
        String bucketName = storageConfig.getBucketName();
        String filename = storageConfig.getPluginFilename();

        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            return s3Client.headObject(headObjectRequest);
            
        } catch (S3Exception e) {
            throw new RuntimeException("获取文件信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 读取输入流的所有字节
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int bytesRead;
        
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        
        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * 检查文件是否存在
     * 
     * @return true 如果文件存在
     */
    public boolean isPluginFileExists() {
        try {
            getPluginFileInfo();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

