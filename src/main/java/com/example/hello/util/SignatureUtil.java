package com.example.hello.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 授权码接口签名验证工具
 * 使用 HMAC-SHA256 算法
 */
public class SignatureUtil {

    private static final String ALGORITHM = "HmacSHA256";

    /**
     * 生成签名
     * 
     * @param appKey 应用密钥
     * @param appSecret 应用密钥（用于HMAC）
     * @param params 请求参数（Map格式）
     * @return Base64编码的签名
     */
    public static String generateSignature(String appKey, String appSecret, Map<String, Object> params) throws Exception {
        String message = buildSignatureMessage(appKey, params);
        return hmacSha256(message, appSecret);
    }

    /**
     * 验证签名
     * 
     * @param appKey 应用密钥
     * @param appSecret 应用密钥（用于HMAC）
     * @param params 请求参数
     * @param signature 客户端提供的签名
     * @return 签名是否有效
     */
    public static boolean verifySignature(String appKey, String appSecret, Map<String, Object> params, 
                                          String signature) throws Exception {
        if (signature == null || signature.trim().isEmpty()) {
            return false;
        }
        
        String expectedSignature = generateSignature(appKey, appSecret, params);
        return expectedSignature.equals(signature);
    }

    /**
     * 构建签名消息
     * 格式: appKey=xxx&param1=value1&param2=value2&...（按字母顺序排序）
     * 
     * @param appKey 应用密钥
     * @param params 请求参数
     * @return 签名消息
     */
    private static String buildSignatureMessage(String appKey, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("appKey=").append(appKey);
        
        // 按参数名排序
        params.entrySet().stream()
              .sorted(Map.Entry.comparingByKey())
              .forEach(entry -> {
                  if (entry.getValue() != null) {
                      sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                  }
              });
        
        return sb.toString();
    }

    /**
     * HMAC-SHA256 签名
     * 
     * @param message 待签名消息
     * @param secret 密钥
     * @return Base64编码的签名
     */
    private static String hmacSha256(String message, String secret) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(keySpec);
        byte[] signature = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature);
    }

    /**
     * 生成示例签名（用于测试和文档）
     * 
     * @param appKey 应用密钥
     * @param appSecret 应用秘钥
     * @return 签名示例
     */
    public static String generateExampleSignature(String appKey, String appSecret) throws Exception {
        Map<String, Object> exampleParams = new LinkedHashMap<>();
        exampleParams.put("licenseType", "STANDARD");
        exampleParams.put("months", 1);
        return generateSignature(appKey, appSecret, exampleParams);
    }
}
