package com.example.hello.service.impl;

import com.example.hello.config.WechatPayConfig;
import com.example.hello.dto.WechatNativePayRequest;
import com.example.hello.dto.WechatNativePayResponse;
import com.example.hello.entity.WechatPayOrder;
import com.example.hello.repository.WechatPayOrderRepository;
import com.example.hello.service.WechatPayService;
import com.example.hello.util.QrCodeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;
import com.example.hello.util.WechatPaySignatureUtil;
import java.io.InputStream;
import com.example.hello.dto.WechatPayNotifyRequest;
import com.example.hello.util.WechatPayDecryptUtil;

/**
 * 微信支付服务实现类
 * 实现Native支付模式的服务商下单接口
 */
@Service
public class WechatPayServiceImpl implements WechatPayService {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayServiceImpl.class);

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private WechatPayOrderRepository wechatPayOrderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String NATIVE_PAY_API = "/v3/pay/transactions/native";
    private static final String QUERY_ORDER_API = "/v3/pay/transactions/out-trade-no/{out_trade_no}";
    private static final String CLOSE_ORDER_API = "/v3/pay/transactions/out-trade-no/{out_trade_no}/close";

    @Override
    public WechatNativePayResponse nativePay(WechatNativePayRequest request) {
        try {
            // 参数校验
            if (request.getAmount() == null || request.getAmount() <= 0) {
                return buildErrorResponse("订单金额必须大于0");
            }

            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return buildErrorResponse("商品描述不能为空");
            }

            // 生成商户订单号（如果未提供）
            String outTradeNo = request.getOutTradeNo();
            if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
                outTradeNo = generateOrderNo();
            }

            // 检查订单是否已存在
            if (wechatPayOrderRepository.findByOutTradeNo(outTradeNo).isPresent()) {
                return buildErrorResponse("订单号已存在");
            }

            // 准备请求数据
            String requestBody = buildNativePayRequest(request, outTradeNo);
            logger.info("发送Native支付请求: {}", requestBody);

            // 调用微信支付API
            String responseBody = callWechatPayApi(NATIVE_PAY_API, "POST", requestBody);
            logger.info("微信支付API响应: {}", responseBody);

            // 解析响应
            JsonNode responseNode = objectMapper.readTree(responseBody);

            // 检查是否有错误
            if (responseNode.has("code")) {
                String errorMsg = responseNode.get("message").asText("未知错误");
                logger.error("微信支付API错误: {}", errorMsg);
                return buildErrorResponse("微信支付API错误: " + errorMsg);
            }

            // 提取code_url
            String codeUrl = responseNode.get("code_url").asText();
            if (codeUrl == null || codeUrl.isEmpty()) {
                return buildErrorResponse("获取code_url失败");
            }

            logger.info("获取到code_url: {}", codeUrl);

            // 生成二维码图片
            String qrCodeImage = QrCodeUtil.generateQrCodeBase64(codeUrl);

            // 保存订单到数据库
            WechatPayOrder payOrder = new WechatPayOrder();
            payOrder.setOutTradeNo(outTradeNo);
            payOrder.setDescription(request.getDescription());
            payOrder.setAmount(request.getAmount());
            payOrder.setCodeUrl(codeUrl);
            payOrder.setClientIp(request.getClientIp());
            payOrder.setStatus("PENDING");
            payOrder.setRemarks(request.getRemarks());

            wechatPayOrderRepository.save(payOrder);
            logger.info("订单已保存到数据库: {}", outTradeNo);

            // 构建成功响应
            return buildSuccessResponse(outTradeNo, codeUrl, qrCodeImage, request.getAmount(), request.getDescription());

        } catch (Exception e) {
            logger.error("Native支付异常: ", e);
            return buildErrorResponse("系统异常: " + e.getMessage());
        }
    }

    @Override
    public WechatNativePayResponse queryOrder(String outTradeNo) {
        try {
            var payOrderOpt = wechatPayOrderRepository.findByOutTradeNo(outTradeNo);
            
            if (payOrderOpt.isEmpty()) {
                return new WechatNativePayResponse(1, "订单不存在");
            }
            
            WechatPayOrder payOrder = payOrderOpt.get();
            WechatNativePayResponse response = new WechatNativePayResponse(0, "查询成功");
            response.setOutTradeNo(outTradeNo);
            response.setAmount(payOrder.getAmount());
            response.setDescription(payOrder.getDescription());
            response.setCodeUrl(payOrder.getCodeUrl());
            response.setStatus(payOrder.getStatus());  
            
            return response;
        } catch (Exception e) {
            logger.error("查询订单异常: ", e);
            return new WechatNativePayResponse(1, "查询异常: " + e.getMessage());
        }
    }

    @Override
    public boolean handlePaymentNotify(String timestamp, String nonce, String signature, String body) {
        try {
            logger.info("收到微信支付回调通知");
            logger.debug("通知体: {}", body);
            
            // 1. 验证签名
            if (!WechatPayDecryptUtil.verifySignature(timestamp, nonce, body, signature, 
                    wechatPayConfig.getApiV3Key())) {
                logger.error("签名验证失败");
                return false;
            }
            
            logger.info("签名验证成功");
            
            // 2. 解析请求体
            WechatPayNotifyRequest notifyRequest = objectMapper.readValue(body, WechatPayNotifyRequest.class);
            
            // 3. 解密回调数据
            if (notifyRequest.getResource() == null) {
                logger.error("回调资源为空");
                return false;
            }
            
            String decryptedData = WechatPayDecryptUtil.decryptNotifyData(
                    notifyRequest.getResource().ciphertext,
                    notifyRequest.getResource().associatedData,
                    notifyRequest.getResource().nonce,
                    wechatPayConfig.getApiV3Key()
            );
            
            logger.info("解密后的回调数据: {}", decryptedData);
            
            // 4. 解析解密后的数据
            com.fasterxml.jackson.databind.JsonNode dataNode = objectMapper.readTree(decryptedData);
            
            // 5. 提取关键信息
            String outTradeNo = dataNode.get("out_trade_no").asText();
            String tradeState = dataNode.get("trade_state").asText();
            String transactionId = dataNode.get("transaction_id").asText();
            
            logger.info("订单号: {}, 状态: {}, 微信订单号: {}", outTradeNo, tradeState, transactionId);
            
            // 6. 更新数据库中的订单状态
            var payOrderOpt = wechatPayOrderRepository.findByOutTradeNo(outTradeNo);
            if (payOrderOpt.isEmpty()) {
                logger.error("订单不存在: {}", outTradeNo);
                return false;
            }
            
            WechatPayOrder payOrder = payOrderOpt.get();
            
            // 根据微信返回的状态更新本地订单状态
            if ("SUCCESS".equals(tradeState)) {
                payOrder.setStatus("SUCCESS");
                payOrder.setSuccessTime(LocalDateTime.now());
                payOrder.setTransactionId(transactionId);
                logger.info("订单 {} 支付成功", outTradeNo);
            } else if ("CLOSED".equals(tradeState)) {
                payOrder.setStatus("CLOSED");
                logger.info("订单 {} 已关闭", outTradeNo);
            } else if ("REFUND".equals(tradeState)) {
                payOrder.setStatus("REFUNDING");
                logger.info("订单 {} 退款中", outTradeNo);
            } else {
                payOrder.setStatus(tradeState);
                logger.info("订单 {} 状态: {}", outTradeNo, tradeState);
            }
            
            wechatPayOrderRepository.save(payOrder);
            logger.info("订单 {} 状态已更新为: {}", outTradeNo, payOrder.getStatus());
            
            return true;
            
        } catch (Exception e) {
            logger.error("处理回调通知异常: ", e);
            return false;
        }
    }

    @Override
    public boolean closeOrder(String outTradeNo) {
        try {
            String url = CLOSE_ORDER_API.replace("{out_trade_no}", outTradeNo);
            String requestBody = buildCloseOrderRequest();

            String responseBody = callWechatPayApi(url, "POST", requestBody);
            JsonNode responseNode = objectMapper.readTree(responseBody);

            if (responseNode.has("code")) {
                logger.error("关闭订单失败: {}", responseNode.get("message").asText());
                return false;
            }

            // 更新订单状态
            var payOrderOpt = wechatPayOrderRepository.findByOutTradeNo(outTradeNo);
            if (payOrderOpt.isPresent()) {
                WechatPayOrder payOrder = payOrderOpt.get();
                payOrder.setStatus("CLOSED");
                wechatPayOrderRepository.save(payOrder);
            }

            return true;
        } catch (Exception e) {
            logger.error("关闭订单异常: ", e);
            return false;
        }
    }

    @Override
    public WechatNativePayResponse cancelOrder(String outTradeNo) {
        try {
            logger.info("用户取消订单: {}", outTradeNo);
            
            // 查找订单
            var payOrderOpt = wechatPayOrderRepository.findByOutTradeNo(outTradeNo);
            if (payOrderOpt.isEmpty()) {
                logger.warn("订单不存在: {}", outTradeNo);
                return new WechatNativePayResponse(1, "订单不存在");
            }
            
            WechatPayOrder payOrder = payOrderOpt.get();
            
            // 检查订单状态，只有待支付状态的订单才能取消
            if ("SUCCESS".equals(payOrder.getStatus())) {
                logger.warn("订单已支付，无法取消: {}", outTradeNo);
                return new WechatNativePayResponse(1, "订单已支付，无法取消");
            }
            
            if ("CANCELLED".equals(payOrder.getStatus()) || "CLOSED".equals(payOrder.getStatus())) {
                logger.warn("订单已取消或关闭: {}", outTradeNo);
                return new WechatNativePayResponse(0, "订单已取消");
            }
            
            // 更新订单状态为已取消（updatedAt 会由 @PreUpdate 自动更新）
            payOrder.setStatus("CANCELLED");
            wechatPayOrderRepository.save(payOrder);
            
            logger.info("订单取消成功: {}", outTradeNo);
            
            WechatNativePayResponse response = new WechatNativePayResponse(0, "订单取消成功");
            response.setOutTradeNo(outTradeNo);
            response.setStatus("CANCELLED");
            return response;
            
        } catch (Exception e) {
            logger.error("取消订单异常: ", e);
            return new WechatNativePayResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 构建Native支付请求体（直连模式）
     */
    private String buildNativePayRequest(WechatNativePayRequest request, String outTradeNo) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // 确定要使用的回调地址 - 优先使用客户端传入的，否则使用默认配置
        String notifyUrl = request.getNotifyUrl();
        if (notifyUrl == null || notifyUrl.trim().isEmpty()) {
            notifyUrl = wechatPayConfig.getNotifyUrl();
        }

        logger.info("使用回调地址: {}", notifyUrl);

        // 直连模式的请求体
        String requestJson = String.format(
            "{\n" +
            "  \"mchid\": \"%s\",\n" +
            "  \"out_trade_no\": \"%s\",\n" +
            "  \"appid\": \"%s\",\n" +
            "  \"description\": \"%s\",\n" +
            "  \"notify_url\": \"%s\",\n" +
            "  \"amount\": {\n" +
            "    \"total\": %d,\n" +
            "    \"currency\": \"CNY\"\n" +
            "  }\n" +
            "}",
            wechatPayConfig.getMerchantId(),
            outTradeNo,
            wechatPayConfig.getAppId(),
            request.getDescription(),
            notifyUrl,
            request.getAmount()
        );

        return requestJson;
    }

    /**
     * 构建关闭订单请求体
     */
    private String buildCloseOrderRequest() {
        return String.format("{\"mchid\": \"%s\"}", wechatPayConfig.getMerchantId());
    }

    /**
     * 调用微信支付API
     */
    private String callWechatPayApi(String apiPath, String method, String requestBody) throws Exception {
        String url = wechatPayConfig.getGatewayUrl() + apiPath;
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        try {
            // 加载证书并获取私钥
            InputStream certInputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("cert/apiclient_cert.p12");
            if (certInputStream == null) {
                throw new Exception("证书文件未找到: cert/apiclient_cert.p12");
            }
            
            java.security.PrivateKey privateKey = WechatPaySignatureUtil.loadPrivateKey(
                    certInputStream, wechatPayConfig.getMerchantId());
            
            // 生成签名信息
            String nonceStr = WechatPaySignatureUtil.generateNonce();
            long timestamp = WechatPaySignatureUtil.getCurrentTimestamp();
            
            String signature = WechatPaySignatureUtil.generateSignature(
                    method,
                    apiPath,
                    timestamp,
                    nonceStr,
                    requestBody != null ? requestBody : "",
                    privateKey
            );
            
            // 生成Authorization头
            String authorizationHeader = WechatPaySignatureUtil.generateAuthorizationHeader(
                    wechatPayConfig.getMerchantId(),
                    nonceStr,
                    timestamp,
                    wechatPayConfig.getCertificateSerialNumber(),
                    signature
            );

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Authorization", authorizationHeader);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if ("POST".equals(method) || "PATCH".equals(method)) {
                connection.setDoOutput(true);
                if (requestBody != null) {
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(requestBody.getBytes("UTF-8"));
                        os.flush();
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            logger.info("微信支付API响应码: {}", responseCode);
            
            if (responseCode >= 400) {
                // 读取错误响应
                java.io.BufferedReader errorReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getErrorStream(), "UTF-8")
                );
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                logger.error("API错误响应: {}", errorResponse.toString());
                throw new Exception("API调用失败，状态码: " + responseCode + ", 响应: " + errorResponse.toString());
            }

            // 读取成功响应
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(connection.getInputStream(), "UTF-8")
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 生成商户订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 构建成功响应
     */
    private WechatNativePayResponse buildSuccessResponse(String outTradeNo, String codeUrl, String qrCodeImage, Integer amount, String description) {
        WechatNativePayResponse response = new WechatNativePayResponse(0, "支付二维码生成成功");
        response.setOutTradeNo(outTradeNo);
        response.setCodeUrl(codeUrl);
        response.setQrCodeImage(qrCodeImage);
        response.setAmount(amount);
        response.setDescription(description);
        return response;
    }

    /**
     * 构建错误响应
     */
    private WechatNativePayResponse buildErrorResponse(String message) {
        return new WechatNativePayResponse(1, message);
    }
}
