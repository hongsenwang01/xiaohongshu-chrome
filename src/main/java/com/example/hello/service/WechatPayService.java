package com.example.hello.service;

import com.example.hello.dto.WechatNativePayRequest;
import com.example.hello.dto.WechatNativePayResponse;
import com.example.hello.dto.WechatPayNotifyRequest;

/**
 * 微信支付服务接口
 */
public interface WechatPayService {

    /**
     * Native支付-生成订单二维码
     *
     * @param request 支付请求
     * @return 支付响应
     */
    WechatNativePayResponse nativePay(WechatNativePayRequest request);

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号
     * @return 支付响应（包含状态）
     */
    WechatNativePayResponse queryOrder(String outTradeNo);

    /**
     * 关闭订单（调用微信API）
     *
     * @param outTradeNo 商户订单号
     * @return 是否成功
     */
    boolean closeOrder(String outTradeNo);

    /**
     * 取消订单（用户主动放弃支付）
     * 只更新本地订单状态，不调用微信API
     *
     * @param outTradeNo 商户订单号
     * @return 支付响应
     */
    WechatNativePayResponse cancelOrder(String outTradeNo);

    /**
     * 处理微信支付回调通知
     *
     * @param timestamp 请求头中的时间戳
     * @param nonce 请求头中的随机数
     * @param signature 请求头中的签名
     * @param body 请求体
     * @return 是否处理成功
     */
    boolean handlePaymentNotify(String timestamp, String nonce, String signature, String body);
}
