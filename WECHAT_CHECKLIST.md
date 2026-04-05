# 微信支付配置检查清单

## ✅ 配置已完成的部分

### 1️⃣ 后端代码实现
- ✅ Native支付接口 (`/api/wechat/pay/native`)
- ✅ API v3 签名认证机制
- ✅ 订单管理（保存、查询、关闭）
- ✅ 二维码生成

### 2️⃣ 配置文件 (application.properties)
```properties
wechat.pay.merchant-id=1730009291                    ✅ 你的商户号
wechat.pay.app-id=wx030d3f4e63c87ed3                ✅ 你的AppId
wechat.pay.certificate-serial-number=387216555...  ✅ 证书序列号
wechat.pay.api-v3-key=PUB_KEY_ID_0117...            ✅ API v3密钥
```

---

## 🔧 还需要完成的步骤

### 1️⃣ 放置证书文件 (必须)

**位置**: `src/main/resources/cert/apiclient_cert.p12`

你下载的证书包里有这个文件，需要放到这个目录。

```
项目根目录/
└── src/main/resources/
    └── cert/
        └── apiclient_cert.p12    ⬅️ 放这里
```

### 2️⃣ 启动应用

```bash
cd /home/devbox/project
mvn clean spring-boot:run
```

### 3️⃣ 测试接口

```bash
# 健康检查
curl http://localhost:8080/api/wechat/pay/health

# 创建支付订单（生成二维码）
curl -X POST http://localhost:8080/api/wechat/pay/native \
  -H "Content-Type: application/json" \
  -d '{
    "description": "测试商品",
    "amount": 100,
    "clientIp": "127.0.0.1"
  }'
```

---

## 📋 配置参数说明

| 参数 | 值 | 来源 |
|------|-----|------|
| merchant-id | 1730009291 | 微信商户平台 → 账户中心 → 基本信息 |
| app-id | wx030d3f4e63c87ed3 | 微信公众平台 → 开发 → 基本配置 |
| certificate-serial-number | 387216555FEC... | 微信商户平台 → 账户中心 → API安全 |
| api-v3-key | PUB_KEY_ID_011... | 微信商户平台 → 账户中心 → API安全 |
| certificate-path | classpath:cert/apiclient_cert.p12 | 微信商户平台下载 |

---

## 🎯 测试流程

1. ✅ 启动应用
2. ✅ 调用 `/api/wechat/pay/native` 接口
3. ✅ 获得 `qrCodeImage` (Base64编码)
4. ✅ 前端显示二维码
5. ✅ 用户用微信扫描
6. ✅ 支付成功

---

## ⚠️ 常见错误

| 错误 | 原因 | 解决方案 |
|------|------|---------|
| 证书文件未找到 | cert/apiclient_cert.p12 不存在 | 确认证书已放在正确位置 |
| 401 Unauthorized | 证书序列号或密钥错误 | 再次检查配置 |
| 502 Bad Gateway | 微信API不可达 | 检查网络连接 |

---

## 📞 下一步

1. 将 `apiclient_cert.p12` 放到 `src/main/resources/cert/` 目录
2. 启动应用
3. 测试接口
4. 根据响应调试

有问题吗？查看完整文档：`WECHAT_PAY_API.md`
