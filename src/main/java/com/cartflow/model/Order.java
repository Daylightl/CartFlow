package com.cartflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
public class Order {
    private String orderId;
    private int userId;
    private List<CartItem> items;
    private double totalAmount;
    private String address;
    private String status;  // pending, shipped, completed, cancelled
    private String createTime;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String orderId, int userId, List<CartItem> items, double totalAmount,
                 String address, String status, String createTime) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
        this.createTime = createTime;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取订单状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case "pending": return "待发货";
            case "shipped": return "已发货";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return "未知状态";
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId=" + userId +
                ", items=" + items.size() +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
