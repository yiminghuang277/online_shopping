package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 对应数据库 products 表
 */
@Data
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")  // 长文本类型
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)  // DECIMAL(10,2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock = 0;  // 默认库存为 0
    
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}