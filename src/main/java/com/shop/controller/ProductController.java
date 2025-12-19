package com.shop.controller;

import com.shop.entity.Product;
import com.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * 商品控制器
 */
@Controller
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 商品列表页面
     * Model 用于向视图传递数据
     */
    @GetMapping("/products")
    public String productList(@RequestParam(value = "category", required = false) String category,
                             @RequestParam(value = "search", required = false) String search,
                             Model model) {
        List<Product> products;
        
        if (search != null && !search.isEmpty()) {
            // 搜索商品
            products = productService.searchByName(search);
            model.addAttribute("search", search);
        } else if (category != null && !category.isEmpty()) {
            // 按分类筛选
            products = productService.findByCategory(category);
            model.addAttribute("category", category);
        } else {
            // 显示所有商品
            products = productService.findAll();
        }
        
        model.addAttribute("products", products);
        return "products";
    }
    
    /**
     * 商品详情页面
     * @PathVariable 从 URL 路径获取参数
     * 例如：/products/1 → id = 1
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        
        if (product == null) {
            return "redirect:/products";
        }
        
        model.addAttribute("product", product);
        return "product-detail";
    }
}