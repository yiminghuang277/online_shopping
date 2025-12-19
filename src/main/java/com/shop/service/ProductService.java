package com.shop.service;

import com.shop.entity.Product;
import com.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 商品业务逻辑类
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * 查找所有商品
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    /**
     * 根据 ID 查找商品
     */
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    /**
     * 根据分类查找商品
     */
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    /**
     * 搜索商品（模糊匹配名称）
     */
    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }
    
    /**
     * 保存商品（新增或更新）
     */
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * 删除商品
     */
    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    /**
     * 更新库存
     */
    @Transactional
    public void updateStock(Long productId, int quantity) {
        Product product = findById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("库存不足");
        }
        
        product.setStock(newStock);
        productRepository.save(product);
    }
}