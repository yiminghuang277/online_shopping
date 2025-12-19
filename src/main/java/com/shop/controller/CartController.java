package com.shop.controller;

import com.shop.dto.CartItem;
import com.shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * 购物车控制器
 */
@Controller
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 查看购物车
     */
    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<CartItem> cart = cartService.getCart();
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal());
        return "cart";
    }
    
    /**
     * 添加商品到购物车
     */
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                           @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(productId, quantity);
            redirectAttributes.addFlashAttribute("message", "商品已添加到购物车");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/products";
    }
    
    /**
     * 更新购物车商品数量
     */
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("message", "购物车已更新");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    /**
     * 从购物车删除商品
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("productId") Long productId,
                                RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(productId);
        redirectAttributes.addFlashAttribute("message", "商品已从购物车移除");
        return "redirect:/cart";
    }
    
    /**
     * 清空购物车
     */
    @PostMapping("/cart/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        cartService.clearCart();
        redirectAttributes.addFlashAttribute("message", "购物车已清空");
        return "redirect:/cart";
    }
}