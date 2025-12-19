package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 密码加密器
     * BCrypt 是一种强加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    /**
     * 安全过滤链（核心配置）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 授权配置
            .authorizeHttpRequests(auth -> auth
                // 允许所有人访问（不需要登录）
                .requestMatchers("/", "/login", "/register", "/products", "/products/**", 
                               "/css/**", "/js/**", "/images/**").permitAll()
                // 管理员页面需要 ADMIN 角色
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 其他所有请求需要登录
                .anyRequest().authenticated()
            )
            
            // 2. 登录配置
            .formLogin(form -> form
                .loginPage("/login")                    // 自定义登录页面
                .loginProcessingUrl("/login")           // 登录表单提交地址
                .defaultSuccessUrl("/products", true)   // 登录成功后跳转
                .failureUrl("/login?error=true")        // 登录失败跳转
                .permitAll()
            )
            
            // 3. 注销配置
            .logout(logout -> logout
                .logoutUrl("/logout")                   // 注销地址
                .logoutSuccessUrl("/login?logout") // 注销后跳转
                .invalidateHttpSession(true)            // 清除 Session
                .deleteCookies("JSESSIONID")            // 删除 Cookie
                .permitAll()
            )
            
            // 4. 记住我功能（可选）
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")                 // 加密密钥
                .tokenValiditySeconds(86400)            // 有效期 1 天
            )
            
            // 5. Session 管理
            .sessionManagement(session -> session
                .maximumSessions(1)                     // 同一用户最多 1 个 Session
                .maxSessionsPreventsLogin(false)        // 新登录踢掉旧 Session
            );
        
        return http.build();
    }
}