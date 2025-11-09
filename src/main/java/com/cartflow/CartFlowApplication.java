package com.cartflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CartFlow电商购物车系统
 * Spring Boot主程序
 */
@SpringBootApplication
public class CartFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartFlowApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("   CartFlow 购物车系统启动成功!");
        System.out.println("   访问地址: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
