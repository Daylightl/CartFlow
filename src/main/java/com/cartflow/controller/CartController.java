package com.cartflow.controller;

import com.cartflow.manager.CartManager;
import com.cartflow.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 购物车控制器
 * 处理购物车相关的HTTP请求
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CartController {

    @Autowired
    private CartManager cartManager;

    /**
     * 获取购物车
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        ShoppingCart cart = cartManager.getUserCart(userId);
        response.put("success", true);
        response.put("data", cart);

        return ResponseEntity.ok(response);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody Map<String, Integer> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        Integer productId = request.get("productId");
        Integer quantity = request.get("quantity");

        if (productId == null || quantity == null || quantity <= 0) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        boolean result = cartManager.addToCart(userId, productId, quantity);

        if (result) {
            response.put("success", true);
            response.put("message", "添加成功");
            ShoppingCart cart = cartManager.getUserCart(userId);
            response.put("data", cart);
        } else {
            response.put("success", false);
            response.put("message", "添加失败，可能库存不足");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestBody Map<String, Integer> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        Integer productId = request.get("productId");
        Integer quantity = request.get("quantity");

        if (productId == null || quantity == null || quantity < 0) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        boolean result = cartManager.updateCartItem(userId, productId, quantity);

        if (result) {
            response.put("success", true);
            response.put("message", "更新成功");
            ShoppingCart cart = cartManager.getUserCart(userId);
            response.put("data", cart);
        } else {
            response.put("success", false);
            response.put("message", "更新失败，可能库存不足");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @RequestBody Map<String, Integer> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        Integer productId = request.get("productId");

        if (productId == null) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        boolean result = cartManager.removeFromCart(userId, productId);

        if (result) {
            response.put("success", true);
            response.put("message", "删除成功");
            ShoppingCart cart = cartManager.getUserCart(userId);
            response.put("data", cart);
        } else {
            response.put("success", false);
            response.put("message", "删除失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        boolean result = cartManager.clearCart(userId);

        if (result) {
            response.put("success", true);
            response.put("message", "购物车已清空");
        } else {
            response.put("success", false);
            response.put("message", "清空失败");
        }

        return ResponseEntity.ok(response);
    }
}
