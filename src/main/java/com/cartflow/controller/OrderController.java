package com.cartflow.controller;

import com.cartflow.manager.CartManager;
import com.cartflow.manager.OrderManager;
import com.cartflow.model.Order;
import com.cartflow.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private CartManager cartManager;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }

        String address = request.get("address");

        // 获取购物车
        ShoppingCart cart = cartManager.getUserCart(userId);

        if (cart.isEmpty()) {
            response.put("success", false);
            response.put("message", "购物车为空");
            return ResponseEntity.ok(response);
        }

        // 验证购物车
        if (!cartManager.validateCart(userId)) {
            response.put("success", false);
            response.put("message", "部分商品库存不足");
            return ResponseEntity.ok(response);
        }

        // 创建订单
        String orderId = orderManager.createOrder(userId, cart, address);

        if (orderId != null) {
            // 清空购物车
            cartManager.clearCart(userId);

            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("orderId", orderId);

            // 返回订单详情
            Order order = orderManager.getOrderById(orderId);
            response.put("data", order);
        } else {
            response.put("success", false);
            response.put("message", "订单创建失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserOrders(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        List<Order> orders = orderManager.getUserOrders(userId);

        response.put("success", true);
        response.put("data", orders);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(
            @PathVariable String orderId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        Order order = orderManager.getOrderById(orderId);

        if (order == null) {
            response.put("success", false);
            response.put("message", "订单不存在");
            return ResponseEntity.ok(response);
        }

        // 检查订单是否属于当前用户（管理员可以查看所有订单）
        String role = (String) session.getAttribute("role");
        if (order.getUserId() != userId && !"admin".equals(role)) {
            response.put("success", false);
            response.put("message", "无权查看此订单");
            return ResponseEntity.ok(response);
        }

        response.put("success", true);
        response.put("data", order);

        return ResponseEntity.ok(response);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable String orderId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        Order order = orderManager.getOrderById(orderId);

        if (order == null) {
            response.put("success", false);
            response.put("message", "订单不存在");
            return ResponseEntity.ok(response);
        }

        // 检查订单是否属于当前用户
        if (order.getUserId() != userId) {
            response.put("success", false);
            response.put("message", "无权取消此订单");
            return ResponseEntity.ok(response);
        }

        boolean result = orderManager.cancelOrder(orderId);

        if (result) {
            response.put("success", true);
            response.put("message", "订单已取消");
        } else {
            response.put("success", false);
            response.put("message", "订单取消失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有订单（管理员）
     */
    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllOrders(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String role = (String) session.getAttribute("role");

        if (!"admin".equals(role)) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        List<Order> orders = orderManager.getAllOrders();

        response.put("success", true);
        response.put("data", orders);

        return ResponseEntity.ok(response);
    }

    /**
     * 更新订单状态（管理员）
     */
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        String role = (String) session.getAttribute("role");

        if (!"admin".equals(role)) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        String status = request.get("status");

        if (status == null) {
            response.put("success", false);
            response.put("message", "参数错误");
            return ResponseEntity.ok(response);
        }

        boolean result = orderManager.updateOrderStatus(orderId, status);

        if (result) {
            response.put("success", true);
            response.put("message", "状态更新成功");
        } else {
            response.put("success", false);
            response.put("message", "状态更新失败");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 获取订单统计（管理员）
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String role = (String) session.getAttribute("role");

        if (!"admin".equals(role)) {
            response.put("success", false);
            response.put("message", "无权限");
            return ResponseEntity.ok(response);
        }

        OrderManager.OrderStatistics stats = orderManager.getStatistics();

        response.put("success", true);
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }
}
