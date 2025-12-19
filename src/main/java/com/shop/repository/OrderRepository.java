package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Order> findByUserIdAndStatus(Long userId, String status);
    
    long countByUserId(Long userId);
    
    /**
     * 计算总销售额（已支付、已发货、已完成）
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'COMPLETED')")
    BigDecimal getTotalSales();
    
    /**
     * 统计某个状态的订单数
     */
    long countByStatus(String status);
    
    /**
     * 统计多个状态的订单数
     */
    long countByStatusIn(List<String> statuses);
    
    /**
     * 统计某个状态的订单金额
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status")
    BigDecimal getSalesByStatus(@Param("status") String status);
    
    /**
     * 统计本月订单数
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startDate")
    long countOrdersThisMonth(@Param("startDate") LocalDateTime startDate);
    
    /**
     * 统计本月订单金额（已支付、已发货、已完成）
     */
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'COMPLETED') AND o.createdAt >= :startDate")
    BigDecimal getSalesThisMonth(@Param("startDate") LocalDateTime startDate);
    
    /**
     * 查询所有订单（用于统计）
     */
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrders();
    
    /**
     * 统计用户某个状态的订单数
     */
    long countByUserIdAndStatus(Long userId, String status);
}