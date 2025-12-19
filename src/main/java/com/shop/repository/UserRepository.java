package com.shop.repository;

import com.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 继承 JpaRepository 自动获得 CRUD 方法
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * Spring Data JPA 会自动实现这个方法
     * 方法名规则：findBy + 字段名（首字母大写）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}