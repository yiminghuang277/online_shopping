package com.shop.repository;

import com.shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 商品数据访问接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根据分类查找商品
     */
    List<Product> findByCategory(String category);
    
    /**
     * 模糊搜索商品名称（可选功能）
     * SQL: SELECT * FROM products WHERE name LIKE %keyword%
     */
    List<Product> findByNameContaining(String keyword);
    
    /**
     * 查找库存大于 0 的商品
     */
    List<Product> findByStockGreaterThan(Integer stock);
}