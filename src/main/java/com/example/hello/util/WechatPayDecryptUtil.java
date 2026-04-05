package com.example.hello.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * 微信支付 API v3 回调解密和签名验证工具
 */
public class WechatPayDecryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayDecryptUtil.class);
    
    // 缓存平台公钥
    private static PublicKey platformPublicKey = null;

    /**
     * 解密回调数据（AES-128-GCM）
     * 
     * @param ciphertext 加密的数据（Base64编码）
     * @param associatedData 关联数据
     * @param nonce 随机数
     * @param apiV3Key API v3密钥（32个字符）
     * @return 解密后的JSON字符串
     */
    public static String decryptNotifyData(String ciphertext, String associatedData, 
                                          String nonce, String apiV3Key) throws Exception {
        // Base64解码
        byte[] cipherBytes = Base64.getDecoder().decode(ciphertext);
        byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
        byte[] aadBytes = associatedData.getBytes(StandardCharsets.UTF_8);

        // 创建SecretKey
        SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), 0, 
                                             apiV3Key.getBytes(StandardCharsets.UTF_8).length, "AES");

        // 创建GCM参数规范
        GCMParameterSpec spec = new GCMParameterSpec(128, nonceBytes);

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        cipher.updateAAD(aadBytes);

        // 解密
        byte[] plaintext = cipher.doFinal(cipherBytes);
        
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    /**
     * 验证签名（使用微信支付平台公钥）
     * 
     * @param timestamp 时间戳（请求头中的 Wechatpay-Timestamp）
     * @param nonce 随机数（请求头中的 Wechatpay-Nonce）
     * @param body 请求体
     * @param signature 签名（请求头中的 Wechatpay-Signature）
     * @param apiV3Key API v3密钥（保留参数兼容性，实际不使用）
     * @return 是否验证成功
     */
    public static boolean verifySignature(String timestamp, String nonce, String body, 
                                         String signature, String apiV3Key) throws Exception {
        // 加载微信支付平台公钥
        if (platformPublicKey == null) {
            platformPublicKey = loadPlatformPublicKey();
        }
        
        // 构造待验签名串
        String message = timestamp + "\n" + nonce + "\n" + body + "\n";
        
        logger.debug("待验签名串: {}", message);
        logger.debug("接收的签名: {}", signature);
        
        // 使用 SHA256withRSA 算法验证签名
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(platformPublicKey);
        sign.update(message.getBytes(StandardCharsets.UTF_8));
        
        // Base64 解码签名
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        
        // 验证签名
        boolean result = sign.verify(signatureBytes);
        
        logger.debug("签名验证结果: {}", result);
        
        return result;
    }
    
    /**
     * 加载微信支付平台公钥
     * 
     * @return 平台公钥
     * @throws Exception 加载异常
     */
    private static PublicKey loadPlatformPublicKey() throws Exception {
        logger.info("开始加载微信支付平台公钥");
        
        // 从 classpath 读取公钥文件
        InputStream inputStream = WechatPayDecryptUtil.class.getClassLoader()
                .getResourceAsStream("cert/pub_key.pem");
        
        if (inputStream == null) {
            throw new Exception("找不到平台公钥文件: cert/pub_key.pem");
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // 读取公钥内容，去掉 BEGIN/END 标记和换行符
            String publicKeyPEM = reader.lines()
                    .filter(line -> !line.startsWith("-----"))
                    .collect(Collectors.joining());
            
            logger.debug("读取到的公钥内容长度: {}", publicKeyPEM.length());
            
            // Base64 解码
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);
            
            // 生成公钥对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            logger.info("微信支付平台公钥加载成功");
            
            return publicKey;
        }
    }
}
