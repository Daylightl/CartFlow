#!/bin/bash

# CartFlow 快速启动脚本

echo "========================================="
echo "   CartFlow 电商购物车系统"
echo "========================================="
echo ""

# 检查Java版本
echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请先安装JDK 8或更高版本"
    exit 1
fi

java_version=$(java -version 2>&1 | grep version | awk -F'"' '{print $2}' | cut -d'.' -f1)
echo "Java版本: $java_version"

if [ "$java_version" -lt 8 ]; then
    echo "错误: 需要Java 8或更高版本"
    exit 1
fi

# 检查Maven
echo "检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

mvn_version=$(mvn -version | head -n 1)
echo "$mvn_version"
echo ""

# 清理并构建
echo "开始构建项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "构建失败，请检查错误信息"
    exit 1
fi

echo ""
echo "构建成功！"
echo ""
echo "========================================="
echo "启动应用..."
echo "访问地址: http://localhost:8080"
echo ""
echo "测试账号:"
echo "  管理员: admin / admin"
echo "  用户1: zhangsan / 123456"
echo "  用户2: lisi / 123456"
echo "========================================="
echo ""

# 运行应用
mvn spring-boot:run
