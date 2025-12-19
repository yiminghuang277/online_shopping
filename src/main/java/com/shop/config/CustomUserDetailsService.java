package com.shop.config;

import com.shop.entity.User;
import com.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * 自定义用户认证服务
 * Spring Security 通过这个类从数据库加载用户信息
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 根据用户名加载用户
     * Spring Security 登录时自动调用此方法
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查找用户
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        
        // 2. 将数据库的 User 转换为 Spring Security 的 UserDetails
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())  // 已加密的密码
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())  // 角色必须加 ROLE_ 前缀
            ))
            .build();
    }
}