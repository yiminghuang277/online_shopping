package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 查找某个用户的所有订单（按时间倒序）
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 查找某个用户某个状态的订单
     */
    List<Order> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 统计某个用户的订单总数
     */
    long countByUserId(Long userId);
    
    /**
     * 自定义 JPQL 查询：计算总销售额
     * JPQL 使用实体类名和属性名，不是表名和字段名
     */
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status IN ('PAID','SHIPPED','COMPLETED')")
    BigDecimal getTotalSales();
    
    /**
     * 统计已支付订单数
     */
    long countByStatus(String status);
     /**
     * 统计多个状态的订单数
     */
    long countByStatusIn(List<String> statuses);
}