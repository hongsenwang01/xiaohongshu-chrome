# 微信支付Native支付 API文档

## 项目概述

本项目实现了微信支付**服务商模式**的**Native支付**功能。服务商为子商户（特约商户）提供在PC端浏览器网页中使用微信支付收款的能力。

## 核心功能流程

### Native支付流程图

```
1. 前端点击"微信支付"
    ↓
2. 前端调用后端接口 /api/wechat/pay/native
    ↓
3. 后端调用微信支付API，获取code_url
    ↓
4. 后端生成二维码图片，返回给前端
    ↓
5. 前端展示二维码给用户
    ↓
6. 用户使用微信"扫一扫"扫描二维码
    ↓
7. 用户在微信中确认支付信息和金额
    ↓
8. 用户验证密码或指纹完成支付
    ↓
9. 微信返回支付结果给后端
    ↓
10. 后端记录支付结果，业务处理
```

## API文档

### 1. Native支付 - 获取二维码

#### 请求信息

- **URL**: `/api/wechat/pay/native`
- **Method**: `POST`
- **Content-Type**: `application/json`

#### 请求参数

```json
{
  "description": "商品描述（必填）",
  "amount": 100,
  "outTradeNo": "商户订单号（可选，为空则自动生成）",
  "clientIp": "用户IP地址（可选）",
  "remarks": "备注信息（可选）"
}
```

#### 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| description | String | 是 | 商品描述，例如"iPhone 15 Pro" |
| amount | Integer | 是 | 订单金额，单位为分（最小1分）|
| outTradeNo | String | 否 | 商户订单号，不提供则自动生成 |
| clientIp | String | 否 | 用户客户端IP地址 |
| remarks | String | 否 | 订单备注信息 |

#### 响应参数

成功响应 (HTTP 200):

```json
{
  "code": 0,
  "message": "支付二维码生成成功",
  "outTradeNo": "ORD1699605600123ABC12345",
  "codeUrl": "weixin://pay/BizPayUrl?...",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANS...",
  "amount": 100,
  "description": "商品描述",
  "timestamp": 1699605600000
}
```

失败响应 (HTTP 200):

```json
{
  "code": 1,
  "message": "错误描述信息",
  "timestamp": 1699605600000
}
```

#### 响应字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 0 表示成功，其他值表示失败 |
| message | String | 返回消息 |
| outTradeNo | String | 商户订单号 |
| codeUrl | String | 微信支付二维码链接（weixin://pay/...格式） |
| qrCodeImage | String | Base64编码的二维码图片，可直接用img标签显示 |
| amount | Integer | 订单金额（分） |
| description | String | 商品描述 |
| timestamp | Long | 返回时间戳 |

#### 使用示例

**cURL请求**:
```bash
curl -X POST http://localhost:8080/api/wechat/pay/native \
  -H "Content-Type: application/json" \
  -d '{
    "description": "iPhone 15 Pro",
    "amount": 99900,
    "clientIp": "127.0.0.1",
    "remarks": "订单备注"
  }'
```

**JavaScript/Fetch示例**:
```javascript
const createWechatPayOrder = async () => {
  const response = await fetch('/api/wechat/pay/native', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      description: 'iPhone 15 Pro',
      amount: 99900,
      clientIp: '127.0.0.1',
      remarks: '订单备注'
    })
  });
  
  const data = await response.json();
  
  if (data.code === 0) {
    // 显示二维码
    document.getElementById('qrcode').src = data.qrCodeImage;
    console.log('商户订单号:', data.outTradeNo);
    console.log('二维码链接:', data.codeUrl);
  } else {
    console.error('创建订单失败:', data.message);
  }
};
```

**Java/RestTemplate示例**:
```java
RestTemplate restTemplate = new RestTemplate();
WechatNativePayRequest request = new WechatNativePayRequest();
request.setDescription("iPhone 15 Pro");
request.setAmount(99900);
request.setClientIp("127.0.0.1");

WechatNativePayResponse response = restTemplate.postForObject(
  "http://localhost:8080/api/wechat/pay/native",
  request,
  WechatNativePayResponse.class
);

if (response.getCode() == 0) {
  String qrCodeImage = response.getQrCodeImage();
  // 显示二维码
}
```

---

### 2. 查询订单状态

#### 请求信息

- **URL**: `/api/wechat/pay/query`
- **Method**: `GET`

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| outTradeNo | String | 是 | 商户订单号 |

#### 响应参数

```json
{
  "code": 0,
  "message": "查询成功",
  "outTradeNo": "ORD1699605600123ABC12345",
  "timestamp": 1699605600000
}
```

#### 使用示例

```bash
curl "http://localhost:8080/api/wechat/pay/query?outTradeNo=ORD1699605600123ABC12345"
```

---

### 3. 关闭订单

#### 请求信息

- **URL**: `/api/wechat/pay/close`
- **Method**: `POST`

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| outTradeNo | String | 是 | 商户订单号 |

#### 响应参数

```json
{
  "code": 0,
  "message": "订单关闭成功",
  "timestamp": 1699605600000
}
```

#### 使用示例

```bash
curl -X POST "http://localhost:8080/api/wechat/pay/close?outTradeNo=ORD1699605600123ABC12345"
```

---

### 4. 健康检查

#### 请求信息

- **URL**: `/api/wechat/pay/health`
- **Method**: `GET`

#### 响应参数

```json
{
  "code": 0,
  "message": "微信支付服务正常",
  "timestamp": 1699605600000
}
```

---

## 配置说明

### application.properties 配置

```properties
# 微信支付配置
wechat.pay.merchant-id=你的商户号
wechat.pay.sub-merchant-id=子商户号（服务商模式）
wechat.pay.app-id=你的AppId
wechat.pay.sp-app-id=服务商AppId
wechat.pay.api-secret-key=API v3密钥
wechat.pay.certificate-serial-number=证书序列号
wechat.pay.certificate-path=classpath:cert/apiclient_cert.pfx
wechat.pay.gateway-url=https://api.mch.weixin.qq.com
```

### 数据库建表

执行以下SQL语句创建订单表：

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

---

## 前端集成示例

### HTML前端示例

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>微信支付Native支付示例</title>
    <style>
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        
        .container {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin: 15px 0;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        
        button {
            background-color: #09B981;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
        }
        
        button:hover {
            background-color: #079970;
        }
        
        #qrCodeContainer {
            text-align: center;
            margin-top: 30px;
            display: none;
        }
        
        #qrCodeImage {
            max-width: 300px;
            border: 1px solid #ddd;
            padding: 10px;
        }
        
        .error {
            color: #E11D48;
            margin-top: 10px;
        }
        
        .success {
            color: #09B981;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>微信支付 Native 支付</h1>
        
        <form id="paymentForm">
            <div class="form-group">
                <label for="description">商品描述 *</label>
                <input type="text" id="description" name="description" 
                       placeholder="例如：iPhone 15 Pro" required>
            </div>
            
            <div class="form-group">
                <label for="amount">金额（元）*</label>
                <input type="number" id="amount" name="amount" 
                       placeholder="例如：999.00" min="0.01" step="0.01" required>
            </div>
            
            <div class="form-group">
                <label for="outTradeNo">订单号（可选）</label>
                <input type="text" id="outTradeNo" name="outTradeNo" 
                       placeholder="自动生成（留空）或输入自定义订单号">
            </div>
            
            <div class="form-group">
                <label for="remarks">备注信息（可选）</label>
                <input type="text" id="remarks" name="remarks" 
                       placeholder="订单备注信息">
            </div>
            
            <button type="button" onclick="createPayment()">生成支付二维码</button>
        </form>
        
        <div id="message"></div>
        
        <div id="qrCodeContainer">
            <h2>扫描二维码支付</h2>
            <img id="qrCodeImage" src="" alt="支付二维码">
            <p>
                <strong>订单号:</strong> <span id="orderNo"></span><br>
                <strong>金额:</strong> <span id="orderAmount"></span> 元<br>
                <strong>商品:</strong> <span id="orderDesc"></span>
            </p>
            <button type="button" onclick="queryOrderStatus()">查询支付状态</button>
            <button type="button" onclick="resetForm()">返回</button>
        </div>
    </div>

    <script>
        function createPayment() {
            const description = document.getElementById('description').value;
            const amount = parseFloat(document.getElementById('amount').value);
            const outTradeNo = document.getElementById('outTradeNo').value;
            const remarks = document.getElementById('remarks').value;
            const messageDiv = document.getElementById('message');
            
            // 验证输入
            if (!description || !amount || amount <= 0) {
                messageDiv.innerHTML = '<div class="error">请填写正确的商品描述和金额</div>';
                return;
            }
            
            messageDiv.innerHTML = '<div style="color: #666;">正在生成二维码...</div>';
            
            // 准备请求数据
            const requestData = {
                description: description,
                amount: Math.round(amount * 100), // 转换为分
                clientIp: '127.0.0.1'
            };
            
            if (outTradeNo) {
                requestData.outTradeNo = outTradeNo;
            }
            
            if (remarks) {
                requestData.remarks = remarks;
            }
            
            // 调用后端API
            fetch('/api/wechat/pay/native', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            })
            .then(response => response.json())
            .then(data => {
                if (data.code === 0) {
                    // 显示二维码
                    document.getElementById('qrCodeImage').src = data.qrCodeImage;
                    document.getElementById('orderNo').textContent = data.outTradeNo;
                    document.getElementById('orderAmount').textContent = (data.amount / 100).toFixed(2);
                    document.getElementById('orderDesc').textContent = data.description;
                    document.getElementById('qrCodeContainer').style.display = 'block';
                    messageDiv.innerHTML = '<div class="success">二维码生成成功！请使用微信扫描二维码</div>';
                } else {
                    messageDiv.innerHTML = '<div class="error">错误：' + data.message + '</div>';
                }
            })
            .catch(error => {
                messageDiv.innerHTML = '<div class="error">请求失败：' + error.message + '</div>';
            });
        }
        
        function queryOrderStatus() {
            const outTradeNo = document.getElementById('orderNo').textContent;
            const messageDiv = document.getElementById('message');
            
            fetch('/api/wechat/pay/query?outTradeNo=' + outTradeNo)
            .then(response => response.json())
            .then(data => {
                if (data.code === 0) {
                    messageDiv.innerHTML = '<div class="success">支付状态查询成功！</div>';
                    // 可根据状态显示不同信息
                } else {
                    messageDiv.innerHTML = '<div class="error">查询失败：' + data.message + '</div>';
                }
            })
            .catch(error => {
                messageDiv.innerHTML = '<div class="error">查询请求失败：' + error.message + '</div>';
            });
        }
        
        function resetForm() {
            document.getElementById('paymentForm').reset();
            document.getElementById('qrCodeContainer').style.display = 'none';
            document.getElementById('message').innerHTML = '';
        }
    </script>
</body>
</html>
```

---

## 错误处理

常见错误码说明：

| 错误码 | 说明 | 处理建议 |
|--------|------|---------|
| 1 | 通用错误 | 根据message字段查看具体错误信息 |
| - | 订单金额必须大于0 | 检查amount是否正确 |
| - | 商品描述不能为空 | 提供有效的商品描述 |
| - | 订单号已存在 | 使用不同的订单号 |
| - | 获取code_url失败 | 检查微信支付配置和网络连接 |
| - | 微信支付API错误 | 查看message字段了解详细信息 |

---

## 支付状态说明

订单状态字段说明：

| 状态 | 说明 |
|------|------|
| PENDING | 待支付 - 二维码已生成，等待用户扫描支付 |
| SUCCESS | 已支付 - 用户已完成支付，资金已到账 |
| CLOSED | 已关闭 - 订单已被主动关闭 |
| REFUNDING | 退款中 - 订单退款处理中 |
| REFUNDED | 已退款 - 订单已全额退款 |

---

## 安全建议

1. **配置管理**: 
   - 不要在代码中硬编码敏感信息
   - 使用环境变量或配置文件管理API密钥
   - 定期轮换API密钥

2. **HTTPS**: 
   - 生产环境必须使用HTTPS加密通信
   - 所有支付相关接口都应该使用HTTPS

3. **签名验证**: 
   - 验证来自微信的回调通知签名
   - 确保请求来自真正的微信服务器

4. **数据安全**: 
   - 不要在日志中记录敏感信息
   - 定期备份数据库
   - 使用数据库加密

5. **速率限制**: 
   - 实现API请求的速率限制
   - 防止恶意重复请求

---

## 常见问题

**Q: 如何获取微信支付配置信息？**
A: 需要向微信商户平台申请，获取商户号、AppId、API密钥等信息。

**Q: 二维码显示不出来怎么办？**
A: 检查response中的qrCodeImage是否为有效的Base64编码。可以在浏览器控制台中查看response对象。

**Q: 支付回调通知怎么处理？**
A: 需要另外实现一个接收微信支付回调的接口，验证签名后更新订单状态。

**Q: 如何处理订单超时未支付？**
A: 可以定期查询订单状态，或调用关闭订单接口主动关闭过期订单。

---

## 更新日志

- v1.0.0 (2024-01-01) - 初始版本，实现Native支付基础功能
