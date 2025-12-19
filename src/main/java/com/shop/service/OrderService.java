package com.shop.service;
import java.util.ArrayList;
import java.util.Arrays;
import com.shop.dto.CartItem;
import com.shop.dto.ProductSalesDTO;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.entity.User;
import com.shop.repository.OrderRepository;
import com.shop.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单业务逻辑类
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * 创建订单（从购物车）
     */
    @Transactional
    public Order createOrder(User user) {
        // 1. 获取购物车
        List<CartItem> cart = cartService.getCart();
        if (cart.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        // 2. 创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalPrice(cartService.getCartTotal());
        order.setStatus("PENDING");
        
        // 3. 保存订单（先保存获得订单 ID）
        order = orderRepository.save(order);
        
        // 4. 创建订单明细
        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            
            orderItemRepository.save(orderItem);
            
            // 5. 减少库存
            productService.updateStock(cartItem.getProductId(), -cartItem.getQuantity());
        }
        
        // 6. 清空购物车
        cartService.clearCart();
        
        return order;
    }
    
    /**
     * 查找用户的所有订单
     */
    public List<Order> findUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 根据 ID 查找订单
     */
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    /**
     * 查找订单明细
     */
    public List<OrderItem> findOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public void updateStatus(Long orderId, String status) {
        Order order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        orderRepository.save(order);
    }
    
    /**
     * 模拟支付
     */
    @Transactional
    public void payOrder(Long orderId) {
        updateStatus(orderId, "PAID");
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只能取消待支付的订单
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付的订单");
        }
        
        // 恢复库存
        List<OrderItem> items = findOrderItems(orderId);
        for (OrderItem item : items) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }
        
        // 更新状态
        updateStatus(orderId, "CANCELLED");
    }
    
    /**
     * 查找所有订单（管理员用）
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    
    /**
     * 统计总销售额
     */
    public BigDecimal getTotalSales() {
        BigDecimal total = orderRepository.getTotalSales();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * 统计订单数
     */
    public long getTotalOrders() {
        return orderRepository.count();
    }
    
    /**
     * 统计已支付订单数
     */
    public long getPaidOrderCount() {
        return orderRepository.countByStatusIn(Arrays.asList("PAID", "SHIPPED", "COMPLETED"));
    }
    
    /**
     * 统计某个状态的订单金额
     */
    public BigDecimal getSalesByStatus(String status) {
        return orderRepository.getSalesByStatus(status);
    }

    /**
     * 统计本月订单数
     */
    public long getOrdersThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return orderRepository.countOrdersThisMonth(startOfMonth);
    }

    /**
     * 统计本月销售额
     */
    public BigDecimal getSalesThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return orderRepository.getSalesThisMonth(startOfMonth);
    }

    /**
     * 获取商品销售统计
     */
    public List<ProductSalesDTO> getProductSalesStatistics() {
        List<Object[]> results = orderItemRepository.getProductSalesStatistics();
        List<ProductSalesDTO> statistics = new ArrayList<>();

        for (Object[] result : results) {
            ProductSalesDTO dto = new ProductSalesDTO();
            dto.setProductId(((Number) result[0]).longValue());
            dto.setProductName((String) result[1]);
            dto.setSoldQuantity(((Number) result[2]).longValue());
            dto.setSalesAmount((BigDecimal) result[3]);
            statistics.add(dto);
        }

        return statistics;
    }
    
    /**
     * 统计某个状态的订单数
     */
    public long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }
}