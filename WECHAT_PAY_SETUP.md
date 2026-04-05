# 微信支付Native支付 - 集成指南

## 📋 概述

本指南将帮助您快速集成微信支付Native支付功能到SpringBoot项目中。

## 🚀 快速开始

### 1. 环境要求

- JDK 17 或更高版本
- MySQL 5.7 或更高版本
- SpringBoot 3.3.2 或更高版本
- Maven 3.6 或更高版本

### 2. 依赖已包含

项目已包含所需的依赖，您无需手动添加：

```xml
<!-- 微信支付SDK -->
<dependency>
    <groupId>com.github.wechatpay-apiv3</groupId>
    <artifactId>wechatpay-java</artifactId>
    <version>0.7.2</version>
</dependency>

<!-- 二维码生成 -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>

<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.2</version>
</dependency>
```

### 3. 配置微信支付参数

编辑 `src/main/resources/application.properties` 文件：

```properties
# ====== WeChat Payment Configuration ======
# 微信商户号 (从微信商户平台获取)
wechat.pay.merchant-id=YOUR_MERCHANT_ID

# 服务商模式下的子商户号
wechat.pay.sub-merchant-id=YOUR_SUB_MERCHANT_ID

# 微信公众号AppId
wechat.pay.app-id=YOUR_APP_ID

# 服务商AppId (如果是服务商模式)
wechat.pay.sp-app-id=YOUR_SP_APP_ID

# API v3 密钥 (32个字符的密钥)
wechat.pay.api-secret-key=YOUR_API_SECRET_KEY

# 证书序列号
wechat.pay.certificate-serial-number=YOUR_CERTIFICATE_SERIAL_NUMBER

# 证书路径 (pfx格式)
wechat.pay.certificate-path=classpath:cert/apiclient_cert.pfx

# 微信支付网关
wechat.pay.gateway-url=https://api.mch.weixin.qq.com
```

### 4. 准备微信支付证书

1. 登录 [微信商户平台](https://pay.weixin.qq.com)
2. 进入"账户中心" -> "API安全"
3. 下载 API v3 证书 (apiclient_cert.pfx)
4. 将证书文件放在 `src/main/resources/cert/` 目录下

### 5. 创建数据库表

执行以下SQL语句创建订单表：

```sql
CREATE TABLE IF NOT EXISTS `wechat_pay_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `out_trade_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '商户订单号',
    `transaction_id` VARCHAR(32) COMMENT '微信支付订单号',
    `description` VARCHAR(255) COMMENT '商品描述',
    `amount` INT NOT NULL COMMENT '订单金额（单位：分）',
    `code_url` LONGTEXT COMMENT '二维码链接',
    `payer_openid` VARCHAR(128) COMMENT '付款用户的openid',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    `success_time` DATETIME COMMENT '支付成功时间',
    `client_ip` VARCHAR(50) COMMENT '用户客户端IP',
    `remarks` LONGTEXT COMMENT '备注信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信支付Native订单记录表';
```

### 6. 启动应用

```bash
mvn clean spring-boot:run
```

### 7. 验证服务

访问健康检查接口：

```bash
curl http://localhost:8080/api/wechat/pay/health
```

成功响应：

```json
{
  "code": 0,
  "message": "微信支付服务正常",
  "timestamp": 1699605600000
}
```

## 📁 项目结构

```
src/main/java/com/example/hello/
├── config/
│   └── WechatPayConfig.java                 # 微信支付配置类
├── controller/
│   └── WechatPayController.java             # 支付控制器
├── dto/
│   ├── WechatNativePayRequest.java          # 请求DTO
│   └── WechatNativePayResponse.java         # 响应DTO
├── entity/
│   └── WechatPayOrder.java                  # 订单实体
├── repository/
│   └── WechatPayOrderRepository.java        # 订单仓储
├── service/
│   ├── WechatPayService.java                # 服务接口
│   └── impl/
│       └── WechatPayServiceImpl.java         # 服务实现
└── util/
    └── QrCodeUtil.java                      # 二维码工具类

src/main/resources/
├── application.properties                   # 应用配置
├── cert/
│   └── apiclient_cert.pfx                  # 微信支付证书
└── db/
    └── schema.sql                           # 数据库脚本
```

## 🔑 核心API接口

### 1. Native支付 (获取二维码)

```
POST /api/wechat/pay/native
```

请求示例：

```bash
curl -X POST http://localhost:8080/api/wechat/pay/native \
  -H "Content-Type: application/json" \
  -d '{
    "description": "iPhone 15 Pro",
    "amount": 99900,
    "clientIp": "127.0.0.1"
  }'
```

响应示例：

```json
{
  "code": 0,
  "message": "支付二维码生成成功",
  "outTradeNo": "ORD1699605600123ABC12345",
  "codeUrl": "weixin://pay/BizPayUrl?...",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANSU...",
  "amount": 99900,
  "description": "iPhone 15 Pro",
  "timestamp": 1699605600000
}
```

### 2. 查询订单状态

```
GET /api/wechat/pay/query?outTradeNo=xxx
```

### 3. 关闭订单

```
POST /api/wechat/pay/close?outTradeNo=xxx
```

详细API文档请参考 `WECHAT_PAY_API.md`

## 💡 使用示例

### JavaScript/React示例

```javascript
async function createPaymentOrder() {
  try {
    const response = await fetch('/api/wechat/pay/native', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        description: '商品名称',
        amount: 100,  // 单位：分
        clientIp: '127.0.0.1'
      })
    });

    const data = await response.json();

    if (data.code === 0) {
      // 显示二维码
      console.log('二维码图片:', data.qrCodeImage);
      console.log('订单号:', data.outTradeNo);
      
      // 展示二维码给用户
      displayQRCode(data.qrCodeImage);
      
      // 轮询查询支付状态
      pollPaymentStatus(data.outTradeNo);
    } else {
      console.error('创建订单失败:', data.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}

function displayQRCode(qrCodeImage) {
  const img = document.createElement('img');
  img.src = qrCodeImage;
  document.getElementById('qrcode-container').appendChild(img);
}

function pollPaymentStatus(outTradeNo) {
  const interval = setInterval(async () => {
    const response = await fetch(`/api/wechat/pay/query?outTradeNo=${outTradeNo}`);
    const data = await response.json();

    if (data.code === 0) {
      // 支付成功
      console.log('支付成功！');
      clearInterval(interval);
      // 处理支付成功逻辑
    }
  }, 2000);
}
```

### Java示例

```java
@Autowired
private WechatPayService wechatPayService;

public void handlePayment() {
    WechatNativePayRequest request = new WechatNativePayRequest();
    request.setDescription("商品描述");
    request.setAmount(10000);  // 100元
    request.setClientIp("192.168.1.1");

    WechatNativePayResponse response = wechatPayService.nativePay(request);

    if (response.getCode() == 0) {
        String qrCodeImage = response.getQrCodeImage();
        String orderNo = response.getOutTradeNo();
        
        // 保存订单信息
        // 返回二维码给前端
    }
}
```

## 🔧 故障排除

### 问题1: "证书文件未找到"

**解决方案**:
1. 确保证书文件位于 `src/main/resources/cert/apiclient_cert.pfx`
2. 检查 `application.properties` 中的证书路径配置是否正确

### 问题2: "微信API返回401错误"

**解决方案**:
1. 验证商户号和AppId是否正确
2. 检查API密钥是否正确（必须是32个字符）
3. 确认证书序列号是否正确

### 问题3: "二维码显示不正确"

**解决方案**:
1. 检查 `qrCodeImage` 字段是否为有效的Base64字符串
2. 确保二维码内容（codeUrl）格式正确
3. 尝试在浏览器控制台打印响应数据

### 问题4: "订单保存失败"

**解决方案**:
1. 确保数据库表已创建
2. 检查数据库连接配置
3. 查看应用日志中的详细错误信息

## 📚 相关文档

- [微信支付API文档](WECHAT_PAY_API.md)
- [微信商户平台](https://pay.weixin.qq.com)
- [微信支付开发文档](https://pay.weixin.qq.com/wiki)

## 🔒 安全建议

1. **不要将敏感信息提交到版本控制系统**
   ```bash
   # 添加到 .gitignore
   src/main/resources/cert/
   src/main/resources/application-prod.properties
   ```

2. **生产环境使用HTTPS**
   ```properties
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=password
   server.ssl.key-store-type=PKCS12
   ```

3. **实现请求签名验证**
   - 验证所有来自微信的回调通知
   - 确认请求的数字签名

4. **日志安全**
   - 不要在日志中记录敏感信息
   - 实现日志脱敏

## 📞 支持

如有问题，请参考：
- 项目文档目录
- 微信支付官方文档
- 查看应用日志了解详细错误信息

## 📝 更新日志

### v1.0.0 (2024年)
- ✅ 实现Native支付基础功能
- ✅ 集成二维码生成
- ✅ 实现订单管理
- ✅ 提供完整API接口
- ✅ 提供前端集成示例
