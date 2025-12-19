package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库 users 表
 */
@Data  // Lombok: 自动生成 getter/setter/toString 等方法
@Entity  // JPA: 标记为实体类
@Table(name = "users")  // 指定表名
public class User {
    
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)  // 非空、唯一、最大长度
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false, length = 20)
    private String role = "USER";  // 默认角色
    
    @Column(name = "created_at", updatable = false)  // 创建时间，不允许更新
    private LocalDateTime createdAt;
    
    /**
     * 插入数据前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}