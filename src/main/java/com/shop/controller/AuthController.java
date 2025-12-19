package com.shop.controller;

import com.shop.entity.User;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 认证控制器（登录、注册）
 */
@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        if (logout != null) {
            model.addAttribute("message", "您已成功退出登录");
        }
        return "login";
    }
    
    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    /**
     * 处理注册请求
     * @RequestParam 从表单获取参数
     * RedirectAttributes 用于重定向时传递消息
     */
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("confirmPassword") String confirmPassword,
                          @RequestParam(value = "email", required = false) String email,
                          RedirectAttributes redirectAttributes) {
        try {
            // 1. 验证密码确认
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "两次密码输入不一致");
                return "redirect:/register";
            }
            
            // 2. 验证密码长度
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "密码长度至少 6 位");
                return "redirect:/register";
            }
            
            // 3. 调用 Service 注册
            userService.register(username, password, email);
            
            // 4. 注册成功，跳转到登录页
            redirectAttributes.addFlashAttribute("message", "注册成功，请登录");
            return "redirect:/login";
            
        } catch (Exception e) {
            // 5. 注册失败，返回注册页并显示错误
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}