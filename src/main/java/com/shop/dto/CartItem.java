package com.shop.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车项 DTO
 * 不对应数据库表，存储在 Session 中
 */
@Data
public class CartItem {
    
    private Long productId;      // 商品 ID
    private String productName;  // 商品名称
    private BigDecimal price;    // 商品价格
    private Integer quantity;    // 购买数量
    private String imageUrl;     // 商品图片
    
    /**
     * 计算小计
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}