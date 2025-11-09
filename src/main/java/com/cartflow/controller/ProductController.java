package com.cartflow.controller;

import com.cartflow.manager.ProductManager;
import com.cartflow.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 处理商品相关的HTTP请求
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductManager productManager;

    /**
     * 获取所有商品
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<Product> products;

        if (search != null && !search.trim().isEmpty()) {
            products = productManager.searchProducts(search);
        } else if (category != null && !category.trim().isEmpty()) {
            products = productManager.getProductsByCategory(category);
        } else {
            products = productManager.getActiveProducts();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable int id) {
        Product product = productManager.getProductById(id);

        Map<String, Object> response = new HashMap<>();
        if (product != null && "active".equals(product.getStatus())) {
            response.put("success", true);
            response.put("data", product);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "商品不存在或已下架");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<String> categories = productManager.getAllCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    /**
     * 添加商品（管理员）
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "商品名称不能为空");
            return ResponseEntity.ok(response);
        }

        if (product.getPrice() <= 0) {
            response.put("success", false);
            response.put("message", "商品价格必须大于0");
            return ResponseEntity.ok(response);
        }

        product.setStatus("active");
        boolean result = productManager.addProduct(product);

        if (result) {
            response.put("success", true);
            response.put("message", "添加成功");
            response.put("data", product);
        } else {
            response.put("success", false);
            response.put("message", "添加失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 更新商品（管理员）
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable int id,
            @RequestBody Product product) {

        Map<String, Object> response = new HashMap<>();

        product.setId(id);
        boolean result = productManager.updateProduct(product);

        if (result) {
            response.put("success", true);
            response.put("message", "更新成功");
        } else {
            response.put("success", false);
            response.put("message", "更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 删除商品（管理员）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();

        boolean result = productManager.deleteProduct(id);

        if (result) {
            response.put("success", true);
            response.put("message", "删除成功");
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 更新库存（管理员）
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable int id,
            @RequestBody Map<String, Integer> request) {

        Map<String, Object> response = new HashMap<>();

        Integer stock = request.get("stock");
        if (stock == null || stock < 0) {
            response.put("success", false);
            response.put("message", "库存数量无效");
            return ResponseEntity.ok(response);
        }

        boolean result = productManager.updateStock(id, stock);

        if (result) {
            response.put("success", true);
            response.put("message", "库存更新成功");
        } else {
            response.put("success", false);
            response.put("message", "库存更新失败");
        }

        return ResponseEntity.ok(response);
    }
}
