package com.shop.service;

import com.shop.entity.User;
import com.shop.repository.OrderRepository;
import com.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务逻辑类
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // Spring Security 提供的密码加密器
    
    @Autowired
    private OrderRepository orderRepository;
    /**
     * 用户注册
     * @Transactional 标记事务：如果出错自动回滚
     */
    @Transactional
    public User register(String username, String password, String email) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 2. 检查邮箱是否已存在
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 3. 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // 加密密码
        user.setEmail(email);
        user.setRole("USER");  // 默认角色
        
        // 4. 保存到数据库
        return userRepository.save(user);
    }
    
    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    /**
    * 根据 ID 查找用户
    */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * 注销账号（软删除或硬删除）
     * 这里使用硬删除，如果需要软删除可以添加 deleted 字段
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 检查是否有未完成的订单
        long pendingOrders = orderRepository.countByUserIdAndStatus(userId, "PENDING");
        if (pendingOrders > 0) {
            throw new RuntimeException("您还有待支付的订单，无法注销账号");
        }

        // 删除用户（会级联删除相关订单，因为设置了 ON DELETE CASCADE）
        userRepository.deleteById(userId);
    }
}