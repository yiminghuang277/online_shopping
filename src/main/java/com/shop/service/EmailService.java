package com.shop.service;

import com.shop.entity.Order;
import com.shop.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务
 */
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送订单状态变更通知
     */
    public void sendOrderStatusChangeEmail(User user, Order order, String oldStatus, String newStatus) {
        // 检查用户是否设置了邮箱
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("用户未设置邮箱，跳过邮件发送");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("【在线购物网站】订单状态变更通知");
            
            String statusText = getStatusText(newStatus);
            String content = buildEmailContent(user, order, oldStatus, newStatus, statusText);
            
            message.setText(content);
            mailSender.send(message);
            
            System.out.println("邮件发送成功：" + user.getEmail());
            
        } catch (Exception e) {
            System.err.println("邮件发送失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(User user, Order order, String oldStatus, String newStatus, String statusText) {
        return String.format("""
            尊敬的 %s，您好！
            
            您的订单状态已更新：
            
            ━━━━━━━━━━━━━━━━━━━━━━
            订单号：%d
            订单金额：¥%.2f
            原状态：%s
            新状态：%s
            更新时间：%s
            ━━━━━━━━━━━━━━━━━━━━━━
            
            %s
            
            如有疑问，请登录网站查看订单详情。
            
            ————————————————
            在线购物网站
            此邮件由系统自动发送，请勿回复
            """,
            user.getUsername(),
            order.getId(),
            order.getTotalPrice(),
            getStatusText(oldStatus),
            statusText,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            getStatusMessage(newStatus)
        );
    }
    
    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        return switch (status) {
            case "PENDING" -> "待支付";
            case "PAID" -> "已支付";
            case "SHIPPED" -> "已发货";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }
    
    /**
     * 获取状态说明
     */
    private String getStatusMessage(String status) {
        return switch (status) {
            case "PAID" -> "您的订单已支付成功，我们将尽快为您发货。";
            case "SHIPPED" -> "您的订单已发货，请注意查收。";
            case "COMPLETED" -> "您的订单已完成，感谢您的购买！";
            case "CANCELLED" -> "您的订单已取消，如有疑问请联系客服。";
            default -> "订单状态已更新。";
        };
    }
}