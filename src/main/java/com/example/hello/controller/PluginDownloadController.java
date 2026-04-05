package com.example.hello.controller;

import com.example.hello.service.ObjectStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件下载控制器
 */
@RestController
@RequestMapping("/api/plugin")
public class PluginDownloadController {

    @Autowired
    private ObjectStorageService storageService;

    /**
     * 下载小红书插件
     * 
     * @return 插件 ZIP 文件
     */
    @GetMapping("/download")
    public ResponseEntity<?> downloadPlugin() {
        try {
            // 检查文件是否存在
            if (!storageService.isPluginFileExists()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "插件文件不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // 下载文件
            byte[] fileContent = storageService.downloadPlugin();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            // 设置文件名（支持中文）
            String filename = "chrome-extension.zip";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            headers.setContentDispositionFormData("attachment", encodedFilename);
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "下载文件失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 获取插件文件信息
     * 
     * @return 文件信息（大小、最后修改时间等）
     */
    @GetMapping("/info")
    public ResponseEntity<?> getPluginInfo() {
        try {
            if (!storageService.isPluginFileExists()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "插件文件不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            HeadObjectResponse fileInfo = storageService.getPluginFileInfo();

            Map<String, Object> info = new HashMap<>();
            info.put("filename", "chrome-extension.zip");
            info.put("size", fileInfo.contentLength());
            info.put("lastModified", fileInfo.lastModified().toString());
            info.put("contentType", fileInfo.contentType());
            info.put("eTag", fileInfo.eTag());

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取文件信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        
        // 检查文件是否存在
        boolean fileExists = storageService.isPluginFileExists();
        response.put("pluginFileExists", fileExists);
        
        if (fileExists) {
            try {
                HeadObjectResponse fileInfo = storageService.getPluginFileInfo();
                response.put("fileSize", fileInfo.contentLength());
            } catch (Exception e) {
                response.put("warning", "无法获取文件大小");
            }
        }
        
        return ResponseEntity.ok(response);
    }
}

