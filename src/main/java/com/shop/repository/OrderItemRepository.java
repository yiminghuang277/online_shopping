package com.shop.repository;

import com.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 查找某个订单的所有明细
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 统计商品销售情况（只统计已支付、已发货、已完成的订单）
     * 返回：商品ID、商品名称、销售数量、销售金额
     */
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status IN ('PAID', 'SHIPPED', 'COMPLETED') " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getProductSalesStatistics();
}