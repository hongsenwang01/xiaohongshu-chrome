# 微信支付Native支付 - 快速开始

## ⚡ 5分钟快速启动

### 1️⃣ 修改配置 (1分钟)

编辑 `src/main/resources/application.properties`：

```properties
wechat.pay.merchant-id=YOUR_MERCHANT_ID
wechat.pay.app-id=YOUR_APP_ID
wechat.pay.api-secret-key=YOUR_SECRET_KEY
# 其他配置...
```

### 2️⃣ 创建数据库表 (1分钟)

```bash
mysql -h localhost -u root -p your_database < src/main/resources/db/schema.sql
```

或在MySQL客户端中执行：
```sql
CREATE TABLE IF NOT EXISTS `wechat_pay_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `out_trade_no` VARCHAR(32) NOT NULL UNIQUE,
    `transaction_id` VARCHAR(32),
    `description` VARCHAR(255),
    `amount` INT NOT NULL,
    `code_url` LONGTEXT,
    `payer_openid` VARCHAR(128),
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `success_time` DATETIME,
    `client_ip` VARCHAR(50),
    `remarks` LONGTEXT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3️⃣ 启动应用 (1分钟)

```bash
cd /home/devbox/project
mvn clean spring-boot:run
```

### 4️⃣ 测试接口 (1分钟)

```bash
# 健康检查
curl http://localhost:8080/api/wechat/pay/health

# 创建支付订单
curl -X POST http://localhost:8080/api/wechat/pay/native \
  -H "Content-Type: application/json" \
  -d '{
    "description": "测试商品",
    "amount": 100,
    "clientIp": "127.0.0.1"
  }'
```

### 5️⃣ 集成到前端 (1分钟)

```javascript
// 创建支付订单
const response = await fetch('/api/wechat/pay/native', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    description: '商品名称',
    amount: 100,  // 金额，单位：分
    clientIp: '127.0.0.1'
  })
});

const data = await response.json();

if (data.code === 0) {
  // 显示二维码
  document.getElementById('qrcode').src = data.qrCodeImage;
}
```

## 📋 核心API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/wechat/pay/native` | POST | 生成支付二维码 |
| `/api/wechat/pay/query` | GET | 查询订单状态 |
| `/api/wechat/pay/close` | POST | 关闭订单 |
| `/api/wechat/pay/health` | GET | 健康检查 |

## 📁 关键文件

```
src/main/java/com/example/hello/
├── config/WechatPayConfig.java          # ⚙️  配置类
├── controller/WechatPayController.java  # 🌐 API入口
├── service/impl/WechatPayServiceImpl.java # 💼 业务逻辑
├── entity/WechatPayOrder.java           # 📊 数据模型
└── util/QrCodeUtil.java                 # 🔲 二维码工具

src/main/resources/
├── application.properties                # ⚙️  配置文件
├── db/schema.sql                         # 📦 数据库脚本
└── cert/apiclient_cert.pfx              # 🔐 支付证书
```

## 🔍 请求/响应示例

### 请求

```json
POST /api/wechat/pay/native
Content-Type: application/json

{
  "description": "iPhone 15 Pro",
  "amount": 99900,
  "clientIp": "192.168.1.1",
  "remarks": "订单备注"
}
```

### 响应 (成功)

```json
{
  "code": 0,
  "message": "支付二维码生成成功",
  "outTradeNo": "ORD1699605600123ABC12345",
  "codeUrl": "weixin://pay/BizPayUrl?...",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANS...",
  "amount": 99900,
  "description": "iPhone 15 Pro",
  "timestamp": 1699605600000
}
```

### 响应 (失败)

```json
{
  "code": 1,
  "message": "订单金额必须大于0",
  "timestamp": 1699605600000
}
```

## 💡 常用命令

```bash
# 编译
mvn clean compile

# 构建
mvn clean package

# 运行
mvn clean spring-boot:run

# 查看日志
tail -f target/logs/application.log

# 测试API
curl -X POST http://localhost:8080/api/wechat/pay/native \
  -H "Content-Type: application/json" \
  -d '{"description":"test","amount":100}'

# 查询订单
curl "http://localhost:8080/api/wechat/pay/query?outTradeNo=ORD1699605600123ABC12345"
```

## 🚨 常见错误处理

| 错误 | 解决方案 |
|------|---------|
| 数据库连接失败 | 检查MySQL连接配置 |
| 二维码显示不出来 | 检查qrCodeImage是否为有效Base64 |
| 微信API返回401 | 检查商户号、AppId和API密钥 |
| 订单保存失败 | 确保数据库表已创建 |

## 📚 完整文档

- **详细API文档**: `WECHAT_PAY_API.md`
- **安装配置指南**: `WECHAT_PAY_SETUP.md`
- **实现总结**: `IMPLEMENTATION_SUMMARY.md`

## 🎯 下一步

1. ✅ 根据本指南完成快速启动
2. 📖 阅读 `WECHAT_PAY_API.md` 了解完整API
3. 🔧 集成前端代码
4. 🧪 测试支付流程
5. 📱 在实际环境中测试

## 💬 需要帮助？

查看详细文档或项目README获取更多信息。

---

**准备好了吗？** 现在开始按照上面的步骤启动你的微信支付Native支付系统！
