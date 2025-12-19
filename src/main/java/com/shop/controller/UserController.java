package com.shop.controller;

import com.shop.entity.User;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * 用户设置控制器
 */
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户设置页面
     */
    @GetMapping("/user/settings")
    public String settings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user-settings";
    }
    
    /**
     * 注销账号
     */
    @PostMapping("/user/delete-account")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest request,
                               HttpServletResponse response,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            
            // 删除账号
            userService.deleteAccount(user.getId());
            
            // 登出
            new SecurityContextLogoutHandler().logout(request, response, 
                SecurityContextHolder.getContext().getAuthentication());
            
            redirectAttributes.addFlashAttribute("message", "账号已成功注销");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/settings";
        }
    }
}