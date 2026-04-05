package com.example.hello.repository;

import com.example.hello.entity.WechatPayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 微信支付订单Repository
 */
@Repository
public interface WechatPayOrderRepository extends JpaRepository<WechatPayOrder, Long> {

    /**
     * 根据商户订单号查找
     */
    Optional<WechatPayOrder> findByOutTradeNo(String outTradeNo);

    /**
     * 根据微信支付订单号查找
     */
    Optional<WechatPayOrder> findByTransactionId(String transactionId);
}
