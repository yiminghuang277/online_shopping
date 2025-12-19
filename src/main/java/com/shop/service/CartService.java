package com.shop.service;

import com.shop.dto.CartItem;
import com.shop.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车业务逻辑类
 * 购物车存储在 Session 中，不保存到数据库
 */
@Service
public class CartService {
    
    @Autowired
    private ProductService productService;
    
    private static final String CART_SESSION_KEY = "SHOPPING_CART";
    
    /**
     * 获取当前 Session
     */
    private HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) 
            RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession();
    }
    
    /**
     * 获取购物车
     */
    @SuppressWarnings("unchecked")
    public List<CartItem> getCart() {
        HttpSession session = getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        
        return cart;
    }
    
    /**
     * 添加商品到购物车
     */
    public void addToCart(Long productId, Integer quantity) {
        // 1. 查找商品
        Product product = productService.findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 2. 检查库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        // 3. 获取购物车
        List<CartItem> cart = getCart();
        
        // 4. 检查购物车中是否已有该商品
        CartItem existingItem = cart.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            // 已存在，更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // 不存在，新建购物车项
            CartItem newItem = new CartItem();
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(quantity);
            newItem.setImageUrl(product.getImageUrl());
            cart.add(newItem);
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    public void updateQuantity(Long productId, Integer quantity) {
        List<CartItem> cart = getCart();
        
        CartItem item = cart.stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
        
        if (item != null) {
            if (quantity <= 0) {
                cart.remove(item);  // 数量为 0，删除
            } else {
                item.setQuantity(quantity);
            }
        }
    }
    
    /**
     * 从购物车删除商品
     */
    public void removeFromCart(Long productId) {
        List<CartItem> cart = getCart();
        cart.removeIf(item -> item.getProductId().equals(productId));
    }
    
    /**
     * 清空购物车
     */
    public void clearCart() {
        HttpSession session = getSession();
        session.removeAttribute(CART_SESSION_KEY);
    }
    
    /**
     * 计算购物车总价
     */
    public BigDecimal getCartTotal() {
        List<CartItem> cart = getCart();
        return cart.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 获取购物车商品数量
     */
    public int getCartItemCount() {
        List<CartItem> cart = getCart();
        return cart.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}