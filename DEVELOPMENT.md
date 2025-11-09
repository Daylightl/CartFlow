# CartFlow 项目说明

## 问题修复记录

### CORS配置问题 (已修复)

**问题**: 应用启动时报错
```
When allowCredentials is true, allowedOrigins cannot contain the special value "*"
```

**原因**: Spring Boot 2.4+ 不允许在`@CrossOrigin`中同时使用`origins = "*"`和`allowCredentials = "true"`

**解决方案**: 将`origins`改为`originPatterns`

修复的文件:
- `src/main/java/com/cartflow/controller/UserController.java:19`
- `src/main/java/com/cartflow/controller/CartController.java:19`
- `src/main/java/com/cartflow/controller/OrderController.java:22`

**修改内容**:
```java
// 修改前
@CrossOrigin(origins = "*", allowCredentials = "true")

// 修改后
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
```

## 运行项目

### 方式1: 使用启动脚本（推荐）
```bash
./start.sh
```

### 方式2: 手动运行
```bash
# 构建项目
mvn clean package

# 运行
mvn spring-boot:run

# 或运行jar文件
java -jar target/cartflow-1.0.0.jar
```

### 方式3: IDE运行
在IDEA或Eclipse中直接运行`CartFlowApplication.java`的main方法

## 访问应用

- **首页**: http://localhost:8080
- **商品列表**: http://localhost:8080/product-list.html
- **购物车**: http://localhost:8080/cart.html
- **管理后台**: http://localhost:8080/admin.html (需要管理员权限)

## 测试账号

| 用户名 | 密码 | 角色 | 功能 |
|--------|------|------|------|
| admin | admin | 管理员 | 可访问管理后台，管理商品和订单 |
| zhangsan | 123456 | 普通用户 | 可购物、下单 |
| lisi | 123456 | 普通用户 | 可购物、下单 |

## 功能测试流程

### 用户端测试
1. 注册新用户或使用测试账号登录
2. 浏览商品列表，使用搜索和分类筛选
3. 查看商品详情
4. 添加商品到购物车
5. 在购物车中修改数量、删除商品
6. 提交订单（需要输入收货地址）
7. 查看订单列表和详情
8. 取消待发货订单

### 管理员测试
1. 使用admin账号登录
2. 访问管理后台
3. 查看数据统计
4. 添加、编辑、删除商品
5. 修改商品库存
6. 查看所有订单
7. 修改订单状态

## 数据文件位置

所有数据存储在`src/main/resources/data/`目录：
- `products.json` - 商品数据
- `users.json` - 用户数据
- `carts.json` - 购物车数据
- `orders.json` - 订单数据

可以直接编辑这些JSON文件来修改数据，重启应用后生效。

## 图片资源

商品图片需要放置在`src/main/resources/static/images/products/thumbs/`目录下：
- P001_thumb.jpg (400x400px)
- P002_thumb.jpg (400x400px)
- P003_thumb.jpg (400x400px)

如果没有图片文件，可以使用占位图服务生成：
```bash
cd src/main/resources/static/images/products/thumbs/
curl "https://via.placeholder.com/400/409EFF/FFFFFF?text=Product1" > P001_thumb.jpg
curl "https://via.placeholder.com/400/67C23A/FFFFFF?text=Product2" > P002_thumb.jpg
curl "https://via.placeholder.com/400/E6A23C/FFFFFF?text=Product3" > P003_thumb.jpg
```

## 常见问题

### Q: 端口8080被占用？
A: 修改`src/main/resources/application.properties`中的`server.port`

### Q: 数据库连接错误？
A: 本项目不使用数据库，使用JSON文件存储数据

### Q: 找不到商品图片？
A: 按照上述说明添加占位图片，或修改`products.json`中的`imageUrl`字段

### Q: 登录后无法访问某些功能？
A: 检查Session是否过期（默认30分钟），或清除浏览器Cookie重新登录

## 开发建议

如需进一步开发，建议：
1. 使用数据库（MySQL/PostgreSQL）替代JSON文件
2. 添加单元测试和集成测试
3. 实现商品图片上传功能
4. 添加支付接口对接
5. 使用Redis缓存热门商品
6. 实现搜索引擎优化（如Elasticsearch）
7. 前端改用Vue.js或React框架

## 技术栈

- Java 8
- Spring Boot 2.7.14
- Maven 3.6+
- Gson 2.10.1
- HTML5 + CSS3 + JavaScript (原生)

## 项目统计

- Java类: 17个
- HTML页面: 9个
- 总代码量: 约5200行
- API接口: 20+个

## 许可证

MIT License
