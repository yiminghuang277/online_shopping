package com.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库连接测试
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("🔍 数据库连接测试");
        System.out.println("========================================");

        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ 数据库连接成功！");
            System.out.println("📊 数据库 URL: " + connection.getMetaData().getURL());
            System.out.println("👤 用户名: " + connection.getMetaData().getUserName());
            System.out.println("🗄️  数据库产品: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("📌 数据库版本: " + connection.getMetaData().getDatabaseProductVersion());

            // 测试查询
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);

            System.out.println("\n📈 数据统计：");
            System.out.println("   用户数: " + userCount);
            System.out.println("   商品数: " + productCount);

        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败！");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("========================================\n");
    }
}
