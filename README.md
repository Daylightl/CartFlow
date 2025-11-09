# CartFlow 电商购物车管理系统

一个基于 Spring Boot + HTML/CSS/JavaScript 开发的电商购物车管理系统，使用 JSON 文件存储数据。

## 项目简介

CartFlow 是一个完整的电商购物车系统，包含用户购物流程和管理员后台功能，适合作为 Java Web 练手项目。

## 技术栈

- **后端**: Java 8 + Spring Boot 2.7.14
- **前端**: HTML5 + CSS3 + JavaScript (原生)
- **数据存储**: JSON 文件
- **架构模式**: MVC
- **构建工具**: Maven

## 核心功能

### 用户端功能
- ✅ 用户注册与登录
- ✅ 商品浏览与搜索
- ✅ 分类筛选
- ✅ 购物车管理（增删改查）
- ✅ 订单创建与管理
- ✅ 订单详情查看

### 管理员功能
- ✅ 商品管理（增删改查）
- ✅ 库存管理
- ✅ 订单管理
- ✅ 数据统计

## 项目结构

```
CartFlow/
├── src/
│   └── main/
│       ├── java/com/cartflow/
│       │   ├── model/              # 数据模型
│       │   │   ├── Product.java
│       │   │   ├── User.java
│       │   │   ├── CartItem.java
│       │   │   ├── ShoppingCart.java
│       │   │   └── Order.java
│       │   ├── manager/            # 业务逻辑
│       │   │   ├── DataManager.java
│       │   │   ├── ProductManager.java
│       │   │   ├── UserManager.java
│       │   │   ├── CartManager.java
│       │   │   └── OrderManager.java
│       │   ├── controller/         # 控制器
│       │   │   ├── ProductController.java
│       │   │   ├── UserController.java
│       │   │   ├── CartController.java
│       │   │   └── OrderController.java
│       │   └── CartFlowApplication.java
│       └── resources/
│           ├── data/               # JSON数据文件
│           │   ├── products.json
│           │   ├── users.json
│           │   ├── carts.json
│           │   └── orders.json
│           ├── static/             # 静态资源
│           │   ├── css/
│           │   ├── js/
│           │   ├── images/
│           │   └── *.html
│           └── application.properties
├── pom.xml
└── README.md
```

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd CartFlow
```

2. **构建项目**
```bash
mvn clean package
```

3. **运行项目**
```bash
mvn spring-boot:run
```

或者直接运行编译后的 JAR 文件：
```bash
java -jar target/cartflow-1.0.0.jar
```

4. **访问应用**

打开浏览器访问: http://localhost:8080

## 测试账号

系统已预置以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin | 管理员 | 可访问管理后台 |
| zhangsan | 123456 | 普通用户 | 普通购物用户 |
| lisi | 123456 | 普通用户 | 普通购物用户 |

## 功能模块说明

### 1. 用户系统
- 注册：用户名、邮箱、密码（MD5加密）
- 登录：基于 Session 的认证
- 角色权限：普通用户 / 管理员

### 2. 商品管理
- 商品列表展示
- 按名称搜索商品
- 按分类筛选商品
- 商品详情查看
- 管理员可增删改查商品

### 3. 购物车
- 添加商品到购物车
- 修改商品数量
- 删除商品
- 清空购物车
- 库存验证

### 4. 订单管理
- 创建订单（自动生成订单号）
- 查看订单列表
- 查看订单详情
- 取消订单（仅待发货状态）
- 管理员可修改订单状态

### 5. 管理后台
- 数据统计（订单数量、销售额等）
- 商品管理
- 订单管理
- 库存管理

## API 接口

### 用户相关
- `POST /api/register` - 用户注册
- `POST /api/login` - 用户登录
- `GET /api/logout` - 用户登出
- `GET /api/user/info` - 获取用户信息

### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/categories` - 获取分类列表
- `POST /api/products` - 添加商品（管理员）
- `PUT /api/products/{id}` - 更新商品（管理员）
- `DELETE /api/products/{id}` - 删除商品（管理员）

### 购物车相关
- `GET /api/cart` - 获取购物车
- `POST /api/cart/add` - 添加到购物车
- `PUT /api/cart/update` - 更新数量
- `DELETE /api/cart/remove` - 删除商品
- `DELETE /api/cart/clear` - 清空购物车

### 订单相关
- `POST /api/orders/create` - 创建订单
- `GET /api/orders` - 获取用户订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders/{id}/cancel` - 取消订单
- `GET /api/orders/admin/all` - 获取所有订单（管理员）
- `PUT /api/orders/admin/{id}/status` - 更新订单状态（管理员）

## 数据存储

系统使用 JSON 文件存储数据，位于 `src/main/resources/data/` 目录：

- `products.json` - 商品数据（10个预置商品）
- `users.json` - 用户数据（3个测试账号）
- `carts.json` - 购物车数据
- `orders.json` - 订单数据（3个示例订单）

## 图片资源

商品图片存放在 `src/main/resources/static/images/products/thumbs/` 目录。

由于图片文件较大，需要您自行准备以下图片：
- P001_thumb.jpg (400x400px) - 用于电子产品
- P002_thumb.jpg (400x400px) - 用于配件类商品
- P003_thumb.jpg (400x400px) - 用于服装类商品

您可以使用任何占位图片服务或自己的图片文件。详见 `src/main/resources/static/images/README.md`

## 开发说明

### 添加新商品
1. 通过管理后台界面添加
2. 或直接编辑 `products.json` 文件

### 修改端口
编辑 `application.properties` 文件：
```properties
server.port=8080
```

### Session 超时设置
默认 30 分钟，可在 `application.properties` 修改：
```properties
server.servlet.session.timeout=30m
```

## 注意事项

1. **密码加密**: 系统使用 MD5 加密密码，生产环境建议使用更安全的加密方式（如 BCrypt）
2. **并发控制**: 库存操作使用了简单的 synchronized 同步，生产环境建议使用更完善的并发控制
3. **数据持久化**: 当前使用 JSON 文件存储，生产环境建议使用数据库
4. **Session 管理**: 当前使用内存 Session，分布式环境需要使用 Redis 等共享 Session

## 扩展建议

- [ ] 使用数据库（MySQL/PostgreSQL）替代 JSON 文件
- [ ] 添加商品图片上传功能
- [ ] 实现用户收藏功能
- [ ] 添加商品评论功能
- [ ] 实现支付功能
- [ ] 添加优惠券系统
- [ ] 实现订单物流跟踪
- [ ] 添加邮件通知功能
- [ ] 使用 Redis 缓存
- [ ] 前端使用 Vue.js 或 React 重构

## 常见问题

### Q: 如何重置数据？
A: 直接编辑 `src/main/resources/data/` 目录下的 JSON 文件，或删除文件后重启应用。

### Q: 忘记管理员密码？
A: 编辑 `users.json` 文件，将 admin 用户的 password 字段改为 `21232f297a57a5a743894a0e4a801fc3`（对应密码 "admin"）。

### Q: 端口被占用？
A: 修改 `application.properties` 中的 `server.port` 配置。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！