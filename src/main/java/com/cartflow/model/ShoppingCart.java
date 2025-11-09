package com.cartflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车实体类
 */
public class ShoppingCart {
    private int userId;
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public ShoppingCart(int userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public ShoppingCart(int userId, List<CartItem> items) {
        this.userId = userId;
        this.items = items != null ? items : new ArrayList<>();
    }

    // Getters and Setters
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

    /**
     * 添加商品到购物车
     * 如果商品已存在，则增加数量
     */
    public void addItem(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getProductId() == newItem.getProductId()) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    /**
     * 从购物车删除商品
     */
    public void removeItem(int productId) {
        items.removeIf(item -> item.getProductId() == productId);
    }

    /**
     * 更新商品数量
     */
    public void updateQuantity(int productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }

        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    /**
     * 获取购物车中指定商品
     */
    public CartItem getItem(int productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 计算购物车总价
     */
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    /**
     * 清空购物车
     */
    public void clear() {
        items.clear();
    }

    /**
     * 判断购物车是否为空
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * 获取购物车商品总数
     */
    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "userId=" + userId +
                ", items=" + items.size() +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
