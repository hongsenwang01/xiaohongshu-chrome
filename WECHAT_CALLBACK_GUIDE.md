# 微信支付 - 回调通知完整指南

## 🔄 完整的支付流程

```
前端                              后端                          微信支付服务器
  │                                │                               │
  ├─→ POST /api/wechat/pay/native →│                               │
  │                                ├─→ 调用微信API下单             │
  │                                │←─ 返回code_url               │
  │   ← 返回二维码 ←───────────────│                               │
  │                                │                               │
  ├─→ 显示二维码给用户                                              │
  │                                │                               │
  └─→ 用户扫码支付 ────────────────────────────────────────────────→ │
                                   │                               │ 用户在微信中
                                   │                      支付成功  │ 确认支付
                                   │←───────────────────────────────│
                                   │                               │
                   ← 微信主动回调 ←────────────────────────────────│
  POST /api/wechat/pay/callback   │
  {timestamp, nonce, signature,   │
   encrypted data}                │
                                   │
                 验证签名 ← 用API v3密钥
                 解密数据
                 更新订单状态 → SUCCESS
                                   │
                 ← 返回SUCCESS ──────→ │
                                   │
  轮询查询订单状态                  │
  GET /api/wechat/pay/query        │
  ← 返回状态: SUCCESS             │
  
显示支付成功页面
```

---

## 📋 需要配置的内容

### 1. 在微信商户平台配置回调URL

登录 [微信商户平台](https://pay.weixin.qq.com)：

1. **账户中心** → **API安全**
2. 找到 **支付成功回调地址**
3. 填写你的回调接口地址：
   ```
   https://yourdomain.com/api/wechat/pay/callback
   ```

### 2. 配置 application.properties

```properties
wechat.pay.merchant-id=1730009291
wechat.pay.app-id=wx030d3f4e63c87ed3
wechat.pay.api-v3-key=你的API_v3密钥
wechat.pay.certificate-serial-number=你的证书序列号
wechat.pay.certificate-path=classpath:cert/apiclient_cert.p12
wechat.pay.gateway-url=https://api.mch.weixin.qq.com
```

---

## 🔐 回调通知的安全验证

### 验证步骤

1. **验证签名**
   - 获取请求头中的：`Wechatpay-Timestamp`、`Wechatpay-Nonce`、`Wechatpay-Signature`
   - 用 API v3密钥 计算签名
   - 比对是否一致

2. **解密数据**
   - 使用 AES-128-GCM 算法
   - 用 API v3密钥 作为加密密钥
   - 解密得到真实的订单数据

---

## 📱 完整的前后端集成

### 后端已实现的接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/wechat/pay/native` | POST | 下单获取二维码 |
| `/api/wechat/pay/query` | GET | 查询订单状态（新增：从本地数据库读取）|
| `/api/wechat/pay/callback` | POST | 微信回调接口（新增） |
| `/api/wechat/pay/health` | GET | 健康检查 |

### 前端轮询逻辑

```javascript
/**
 * 轮询支付状态
 */
async function pollPaymentStatus() {
    const maxAttempts = 120; // 最多轮询2分钟
    let attempts = 0;
    
    const interval = setInterval(async () => {
        attempts++;
        
        if (attempts > maxAttempts) {
            clearInterval(interval);
            alert('轮询超时，请手动确认支付');
            return;
        }
        
        try {
            const response = await fetch(`/api/wechat/pay/query?outTradeNo=${orderNo}`);
            const data = await response.json();
            
            if (data.code === 0) {
                const status = data.qrCodeUrl; // 临时用qrCodeUrl字段返回状态
                
                if (status === 'SUCCESS') {
                    clearInterval(interval);
                    alert('支付成功！');
                    // 跳转到成功页面
                    location.href = '/payment-success';
                } else if (status === 'CLOSED' || status === 'REFUNDED') {
                    clearInterval(interval);
                    alert('订单已关闭');
                }
            }
        } catch (error) {
            console.error('轮询失败:', error);
        }
    }, 1000); // 每秒轮询一次
}
```

---

## 🧪 测试步骤

### 测试环境

微信支付提供了测试账户，可以在沙箱环境测试：

1. 登录微信商户平台
2. **账户中心** → **API安全** → **沙箱测试**
3. 获取测试的商户号和密钥

### 真实支付测试

1. 确保配置都正确
2. 运行应用：`mvn clean spring-boot:run`
3. 访问前端页面，填写金额和商品
4. 点击"生成支付二维码"
5. 用微信扫描二维码
6. 在微信中确认支付
7. 支付成功后：
   - 微信会调用后端的 `/api/wechat/pay/callback` 接口
   - 后端验证签名并解密数据
   - 后端更新订单状态为 SUCCESS
   - 前端轮询发现状态变为 SUCCESS，显示成功页面

---

## 📊 查看回调日志

启用 DEBUG 日志可以看到详细的回调处理过程：

编辑 `src/main/resources/application.properties`：

```properties
logging.level.com.example.hello=DEBUG
logging.level.com.example.hello.util=DEBUG
logging.level.com.example.hello.service=DEBUG
```

这样可以在日志中看到：
- 收到的回调通知
- 签名验证过程
- 解密后的订单数据
- 订单状态更新

---

## 🔍 常见问题

### Q1: 回调接口一直收不到微信的请求

**检查清单：**
1. 域名是否能被微信服务器访问？
2. 回调URL是否正确配置在微信商户平台？
3. 防火墙是否允许来自微信IP的请求？
4. 应用是否正在运行？

### Q2: 签名验证失败

**原因可能：**
- API v3密钥配置错误
- 请求体被修改（如添加了BOM标记）
- 时间戳验证失败

**解决：**
- 在日志中启用DEBUG级别看计算的签名和收到的签名
- 确认API v3密钥是否正确

### Q3: 解密失败

**原因：**
- API v3密钥不正确
- AES算法参数错误
- 加密数据损坏

**解决：**
- 检查日志中的错误信息
- 验证API v3密钥

---

## 📝 关键代码位置

```
src/main/java/com/example/hello/
├── controller/WechatPayController.java
│   └── paymentCallback() - 回调接口入口
├── service/impl/WechatPayServiceImpl.java
│   └── handlePaymentNotify() - 回调处理逻辑
├── util/WechatPayDecryptUtil.java
│   ├── verifySignature() - 签名验证
│   └── decryptNotifyData() - 数据解密
└── dto/WechatPayNotifyRequest.java
    └── 回调请求数据结构
```

---

## ✅ 完整的支付流程检查清单

- [ ] 配置API v3密钥
- [ ] 配置证书序列号
- [ ] 放置p12证书文件
- [ ] 在微信商户平台配置回调URL
- [ ] 启动应用
- [ ] 前端调用 `/api/wechat/pay/native` 获取二维码
- [ ] 用微信扫描二维码
- [ ] 完成支付
- [ ] 后端收到回调（检查日志）
- [ ] 订单状态更新为 SUCCESS
- [ ] 前端轮询查询到状态变化
- [ ] 显示成功页面

---

## 🚀 下一步优化

1. **改进查询接口**：返回更详细的订单状态信息
2. **实现退款功能**：处理用户退款请求
3. **缓存优化**：使用Redis缓存订单状态
4. **异步处理**：使用消息队列处理回调
5. **多商户支持**：扩展为支持多个商户

---

## 📞 技术支持

遇到问题时：
1. 检查应用日志
2. 验证所有配置参数
3. 查看微信商户平台的错误日志
4. 参考微信支付官方文档
