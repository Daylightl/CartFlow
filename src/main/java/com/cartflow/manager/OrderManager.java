package com.cartflow.manager;

import com.cartflow.model.Order;
import com.cartflow.model.ShoppingCart;
import com.cartflow.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理类
 * 负责订单的创建、查询和管理
 */
@Component
public class OrderManager {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private ProductManager productManager;

    private List<Order> orders;

    @PostConstruct
    public void init() {
        this.orders = dataManager.loadOrders();
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
    }

    /**
     * 创建订单
     */
    public synchronized String createOrder(int userId, ShoppingCart cart, String address) {
        try {
            // 检查购物车是否为空
            if (cart == null || cart.isEmpty()) {
                System.err.println("Cannot create order: cart is empty");
                return null;
            }

            // 检查库存并扣减
            for (CartItem item : cart.getItems()) {
                if (!productManager.checkStock(item.getProductId(), item.getQuantity())) {
                    System.err.println("Insufficient stock for product: " + item.getProductName());
                    return null;
                }
            }

            // 扣减库存
            for (CartItem item : cart.getItems()) {
                if (!productManager.reduceStock(item.getProductId(), item.getQuantity())) {
                    System.err.println("Failed to reduce stock for product: " + item.getProductName());
                    // 这里应该回滚之前的库存扣减，为简化处理暂不实现
                    return null;
                }
            }

            // 生成订单号
            String orderId = generateOrderId(userId);

            // 创建订单对象
            Order order = new Order();
            order.setOrderId(orderId);
            order.setUserId(userId);
            order.setItems(new ArrayList<>(cart.getItems()));
            order.setTotalAmount(cart.getTotalPrice());
            order.setAddress(address != null ? address : "");
            order.setStatus("pending");
            order.setCreateTime(getCurrentTime());

            orders.add(order);
            dataManager.saveOrders(orders);

            return orderId;
        } catch (Exception e) {
            System.err.println("Failed to create order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取用户的所有订单
     */
    public List<Order> getUserOrders(int userId) {
        return orders.stream()
                .filter(o -> o.getUserId() == userId)
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 根据订单号获取订单
     */
    public Order getOrderById(String orderId) {
        return orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有订单（管理员用）
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * 更新订单状态
     */
    public synchronized boolean updateOrderStatus(String orderId, String newStatus) {
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                return false;
            }

            order.setStatus(newStatus);
            dataManager.saveOrders(orders);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update order status: " + e.getMessage());
            return false;
        }
    }

    /**
     * 取消订单
     */
    public synchronized boolean cancelOrder(String orderId) {
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                return false;
            }

            // 只能取消待发货的订单
            if (!"pending".equals(order.getStatus())) {
                System.err.println("Cannot cancel order with status: " + order.getStatus());
                return false;
            }

            // 恢复库存
            for (CartItem item : order.getItems()) {
                productManager.addStock(item.getProductId(), item.getQuantity());
            }

            order.setStatus("cancelled");
            dataManager.saveOrders(orders);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to cancel order: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除订单
     */
    public synchronized boolean deleteOrder(String orderId) {
        try {
            boolean removed = orders.removeIf(o -> o.getOrderId().equals(orderId));
            if (removed) {
                dataManager.saveOrders(orders);
            }
            return removed;
        } catch (Exception e) {
            System.err.println("Failed to delete order: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取订单统计信息
     */
    public OrderStatistics getStatistics() {
        OrderStatistics stats = new OrderStatistics();
        stats.totalOrders = orders.size();
        stats.totalRevenue = orders.stream()
                .filter(o -> !"cancelled".equals(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.pendingOrders = (int) orders.stream()
                .filter(o -> "pending".equals(o.getStatus()))
                .count();
        stats.completedOrders = (int) orders.stream()
                .filter(o -> "completed".equals(o.getStatus()))
                .count();
        return stats;
    }

    /**
     * 订单统计信息类
     */
    public static class OrderStatistics {
        public int totalOrders;
        public double totalRevenue;
        public int pendingOrders;
        public int completedOrders;
    }

    /**
     * 生成订单号
     * 格式: 时间戳 + 用户ID + 随机数
     */
    private String generateOrderId(int userId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        int random = (int) (Math.random() * 1000);
        return timestamp + userId + String.format("%03d", random);
    }

    /**
     * 获取当前时间
     */
    private String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * 刷新订单列表
     */
    public void refresh() {
        this.orders = dataManager.loadOrders();
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
    }
}
