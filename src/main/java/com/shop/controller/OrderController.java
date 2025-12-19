package com.shop.controller;

import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.entity.User;
import com.shop.service.CartService;
import com.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shop.service.UserService;
import java.util.List;

/**
 * 订单控制器
 */
@Controller
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 确认订单页面
     * @AuthenticationPrincipal 获取当前登录用户
     */
    @GetMapping("/order/confirm")
    public String confirmOrder(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 检查购物车是否为空
        if (cartService.getCart().isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cartItems", cartService.getCart());
        model.addAttribute("cartTotal", cartService.getCartTotal());
        return "order-confirm";
    }
    
    /**
     * 提交订单
     */
    @PostMapping("/order/submit")
    public String submitOrder(@AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            // 获取当前用户
            User user = userService.findByUsername(userDetails.getUsername());
            
            // 创建订单
            Order order = orderService.createOrder(user);
            
            redirectAttributes.addFlashAttribute("message", "订单提交成功！订单号：" + order.getId());
            return "redirect:/order/" + order.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }
    
    /**
     * 订单详情页面
     */
    @GetMapping("/order/{id}")
    public String orderDetail(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        Order order = orderService.findById(id);
        
        if (order == null) {
            return "redirect:/orders";
        }
        
        // 验证订单是否属于当前用户（或是管理员）
        User user = userService.findByUsername(userDetails.getUsername());
        if (!order.getUserId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            return "redirect:/orders";
        }
        
        List<OrderItem> orderItems = orderService.findOrderItems(id);
        
        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        return "order-detail";
    }
    
    /**
     * 我的订单列表
     */
    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Order> orders = orderService.findUserOrders(user.getId());
        
        model.addAttribute("orders", orders);
        return "orders";
    }
    
    /**
     * 支付订单（模拟）
     */
    @PostMapping("/order/{id}/pay")
    public String payOrder(@PathVariable("id") Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        try {
            // 验证订单所有权
            Order order = orderService.findById(id);
            User user = userService.findByUsername(userDetails.getUsername());
            
            if (!order.getUserId().equals(user.getId())) {
                throw new RuntimeException("无权操作此订单");
            }
            
            orderService.payOrder(id);
            redirectAttributes.addFlashAttribute("message", "支付成功！");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/order/" + id;
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/order/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            // 验证订单所有权
            Order order = orderService.findById(id);
            User user = userService.findByUsername(userDetails.getUsername());
            
            if (!order.getUserId().equals(user.getId())) {
                throw new RuntimeException("无权操作此订单");
            }
            
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("message", "订单已取消");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/order/" + id;
    }
}