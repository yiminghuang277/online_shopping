package com.shop.controller;

import com.shop.entity.Order;
import com.shop.entity.Product;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

/**
 * 管理员控制器
 * 需要 ADMIN 角色才能访问（在 SecurityConfig 中配置）
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 管理员首页
     */
    @GetMapping("")
    public String adminHome() {
        return "redirect:/admin/products";
    }
    
    // ==================== 商品管理 ====================
    
    /**
     * 商品管理页面
     */
    @GetMapping("/products")
    public String manageProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "admin/products";
    }
    
    /**
     * 显示添加商品表单
     */
    @GetMapping("/products/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form";
    }
    
    /**
     * 显示编辑商品表单
     */
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/product-form";
    }
    
    /**
     * 保存商品（新增或更新）
     */
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                             RedirectAttributes redirectAttributes) {
        try {
            productService.save(product);
            redirectAttributes.addFlashAttribute("message", "商品保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败：" + e.getMessage());
        }
        return "redirect:/admin/products";
    }
    
    /**
     * 删除商品
     */
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "商品删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败：" + e.getMessage());
        }
        return "redirect:/admin/products";
    }
    
    // ==================== 订单管理 ====================
    
    /**
     * 订单管理页面
     */
    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }
    
    /**
     * 更新订单状态
     */
    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable("id") Long id,
                                   @RequestParam("status") String status,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "订单状态已更新");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
    
    // ==================== 销售统计 ====================
    
    /**
     * 销售统计页面
     */
    @GetMapping("/stats")
    public String statistics(Model model) {
        BigDecimal totalSales = orderService.getTotalSales();
        long totalOrders = orderService.getTotalOrders();
        long paidOrders = orderService.getPaidOrderCount();
        
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("paidOrders", paidOrders);
        
        return "admin/stats";
    }
}