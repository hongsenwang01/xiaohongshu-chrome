package com.example.hello.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

/**
 * 微信支付API v3 签名工具类
 */
public class WechatPaySignatureUtil {

    private static final Logger logger = LoggerFactory.getLogger(WechatPaySignatureUtil.class);

    /**
     * 从证书文件加载私钥
     * 
     * @param certificateInputStream 证书输入流
     * @param password 证书密码（通常是商户号）
     * @return 私钥
     */
    public static PrivateKey loadPrivateKey(InputStream certificateInputStream, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(certificateInputStream, password.toCharArray());
        
        String alias = keyStore.aliases().nextElement();
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    /**
     * 生成签名
     * 
     * @param method HTTP方法
     * @param path API路径（包括query参数）
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param body 请求体
     * @param privateKey 私钥
     * @return 签名字符串
     */
    public static String generateSignature(String method, String path, long timestamp, String nonce, 
                                          String body, PrivateKey privateKey) throws Exception {
        // 构造签名消息
        String message = method + "\n" +
                         path + "\n" +
                         timestamp + "\n" +
                         nonce + "\n" +
                         body + "\n";
        
        // 使用SHA256withRSA签名
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        
        // 返回Base64编码的签名
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 生成Authorization头
     * 
     * @param merchantId 商户号
     * @param nonceStr 随机字符串
     * @param timestamp 时间戳
     * @param serialNumber 证书序列号
     * @param signature 签名
     * @return Authorization头值
     */
    public static String generateAuthorizationHeader(String merchantId, String nonceStr, 
                                                     long timestamp, String serialNumber, 
                                                     String signature) {
        return "WECHATPAY2-SHA256-RSA2048 mchid=\"" + merchantId + "\",nonce_str=\"" + nonceStr + 
               "\",timestamp=\"" + timestamp + "\",serial_no=\"" + serialNumber + 
               "\",signature=\"" + signature + "\"";
    }

    /**
     * 生成随机字符串
     */
    public static String generateNonce() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    /**
     * 获取当前时间戳
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
