package com.shop.repository;

import com.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 订单明细数据访问接口
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 查找某个订单的所有明细
     */
    List<OrderItem> findByOrderId(Long orderId);
}