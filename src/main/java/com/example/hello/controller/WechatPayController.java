package com.example.hello.controller;

import com.example.hello.dto.WechatNativePayRequest;
import com.example.hello.dto.WechatNativePayResponse;
import com.example.hello.service.WechatPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 微信支付控制器
 * 提供Native支付接口，服务商模式下的子商户收款
 */
@RestController
@RequestMapping("/api/wechat/pay")
@CrossOrigin(origins = "*")
public class WechatPayController {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayController.class);

    @Autowired
    private WechatPayService wechatPayService;

    /**
     * Native支付接口 - 获取订单二维码
     * 
     * 接口地址：POST /api/wechat/pay/native
     * 
     * 请求示例：
     * {
     *   "description": "商品描述",
     *   "amount": 100,
     *   "outTradeNo": "可选的商户订单号",
     *   "clientIp": "127.0.0.1",
     *   "remarks": "备注信息"
     * }
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "支付二维码生成成功",
     *   "outTradeNo": "ORD1699605600123ABC12345",
     *   "codeUrl": "weixin://pay/...",
     *   "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANS...",
     *   "amount": 100,
     *   "description": "商品描述",
     *   "timestamp": 1699605600123
     * }
     *
     * @param request 支付请求对象
     * @return 支付响应对象，包含二维码链接和二维码图片
     */
    @PostMapping("/native")
    public WechatNativePayResponse nativePay(@RequestBody WechatNativePayRequest request) {
        logger.info("收到Native支付请求: description={}, amount={}", request.getDescription(), request.getAmount());
        
        if (request == null) {
            return new WechatNativePayResponse(1, "请求参数不能为空");
        }

        try {
            WechatNativePayResponse response = wechatPayService.nativePay(request);
            logger.info("Native支付响应: code={}, message={}", response.getCode(), response.getMessage());
            return response;
        } catch (Exception e) {
            logger.error("Native支付接口异常", e);
            return new WechatNativePayResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 查询订单状态接口
     * 
     * 接口地址：GET /api/wechat/pay/query?outTradeNo=xxx
     * 
     * @param outTradeNo 商户订单号
     * @return 订单信息
     */
    @GetMapping("/query")
    public WechatNativePayResponse queryOrder(@RequestParam String outTradeNo) {
        logger.info("查询订单: outTradeNo={}", outTradeNo);

        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            return new WechatNativePayResponse(1, "订单号不能为空");
        }

        try {
            return wechatPayService.queryOrder(outTradeNo);
        } catch (Exception e) {
            logger.error("查询订单异常", e);
            return new WechatNativePayResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 关闭订单接口（调用微信API）
     * 
     * 接口地址：POST /api/wechat/pay/close
     * 
     * 请求示例：
     * {
     *   "outTradeNo": "ORD1699605600123ABC12345"
     * }
     *
     * @param outTradeNo 商户订单号
     * @return 操作结果
     */
    @PostMapping("/close")
    public WechatNativePayResponse closeOrder(@RequestParam String outTradeNo) {
        logger.info("关闭订单: outTradeNo={}", outTradeNo);

        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            return new WechatNativePayResponse(1, "订单号不能为空");
        }

        try {
            boolean success = wechatPayService.closeOrder(outTradeNo);
            if (success) {
                return new WechatNativePayResponse(0, "订单关闭成功");
            } else {
                return new WechatNativePayResponse(1, "订单关闭失败");
            }
        } catch (Exception e) {
            logger.error("关闭订单异常", e);
            return new WechatNativePayResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 取消订单接口（用户主动放弃支付）
     * 
     * 接口地址：POST /api/wechat/pay/cancel
     * 
     * 说明：
     * - 用户在支付页面点击"放弃支付"时调用此接口
     * - 只更新本地订单状态为CANCELLED，不调用微信API
     * - 保留订单记录，便于数据分析和审计
     * 
     * 请求示例：
     * POST /api/wechat/pay/cancel?outTradeNo=ORD1699605600123ABC12345
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "订单取消成功",
     *   "outTradeNo": "ORD1699605600123ABC12345",
     *   "status": "CANCELLED"
     * }
     *
     * @param outTradeNo 商户订单号
     * @return 操作结果
     */
    @PostMapping("/cancel")
    public WechatNativePayResponse cancelOrder(@RequestParam String outTradeNo) {
        logger.info("用户取消订单: outTradeNo={}", outTradeNo);

        if (outTradeNo == null || outTradeNo.trim().isEmpty()) {
            return new WechatNativePayResponse(1, "订单号不能为空");
        }

        try {
            return wechatPayService.cancelOrder(outTradeNo);
        } catch (Exception e) {
            logger.error("取消订单异常", e);
            return new WechatNativePayResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     * 
     * 接口地址：GET /api/wechat/pay/health
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public WechatNativePayResponse health() {
        return new WechatNativePayResponse(0, "微信支付服务正常");
    }

    /**
     * 微信支付回调通知接口
     * 
     * 接口地址：POST /api/wechat/pay/callback
     * 
     * 说明：
     * - 这是微信支付主动调用的接口，用于通知支付结果
     * - 必须返回JSON格式的成功响应：{"code":"SUCCESS","message":"成功"}
     * - 需要验证来自微信的请求
     *
     * @param request HTTP请求
     * @return 回调响应
     */
    @PostMapping("/callback")
    public Object paymentCallback(HttpServletRequest request) {
        logger.info("收到微信支付回调请求");
        
        try {
            // 读取请求头
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String signature = request.getHeader("Wechatpay-Signature");
            
            logger.debug("时间戳: {}, 随机数: {}, 签名: {}", timestamp, nonce, signature);
            
            // 读取请求体
            StringBuilder body = new StringBuilder();
            try (java.io.BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            
            String bodyStr = body.toString();
            logger.debug("请求体: {}", bodyStr);
            
            // 处理回调通知
            boolean success = wechatPayService.handlePaymentNotify(timestamp, nonce, signature, bodyStr);
            
            if (success) {
                // 返回微信要求的成功响应格式
                java.util.Map<String, String> response = new java.util.HashMap<>();
                response.put("code", "SUCCESS");
                response.put("message", "成功");
                return response;
            } else {
                // 返回微信要求的失败响应格式
                java.util.Map<String, String> response = new java.util.HashMap<>();
                response.put("code", "FAIL");
                response.put("message", "失败");
                return response;
            }
            
        } catch (Exception e) {
            logger.error("处理回调异常: ", e);
            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("code", "FAIL");
            response.put("message", "系统异常");
            return response;
        }
    }
}
