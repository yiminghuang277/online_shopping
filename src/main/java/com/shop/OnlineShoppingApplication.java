package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 在线购物网站 - 启动类
 * 
 * @SpringBootApplication 是组合注解，包含：
 * - @Configuration: 标记为配置类
 * - @EnableAutoConfiguration: 自动配置 Spring Boot
 * - @ComponentScan: 自动扫描当前包及子包的组件
 */
@SpringBootApplication
public class OnlineShoppingApplication {
    
    /**
     * 主方法 - 程序入口
     */
    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingApplication.class, args);
        System.out.println("========================================");
        System.out.println("在线购物网站启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}