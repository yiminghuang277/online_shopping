package com.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 */
@Controller
public class HomeController {
    
    /**
     * 首页
     * 返回值是模板文件名（不带 .html）
     * Spring Boot 会自动找 templates/index.html
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}