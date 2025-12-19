package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * 商品销售统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {
    private Long productId;          // 商品 ID
    private String productName;      // 商品名称
    private Long soldQuantity;       // 销售数量
    private BigDecimal salesAmount;  // 销售金额
}