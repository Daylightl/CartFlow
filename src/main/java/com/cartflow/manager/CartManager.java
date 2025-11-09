package com.cartflow.manager;

import com.cartflow.model.CartItem;
import com.cartflow.model.Product;
import com.cartflow.model.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理类
 * 负责购物车的增删改查
 */
@Component
public class CartManager {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private ProductManager productManager;

    private List<ShoppingCart> carts;

    @PostConstruct
    public void init() {
        this.carts = dataManager.loadCarts();
        if (this.carts == null) {
            this.carts = new ArrayList<>();
        }
    }

    /**
     * 获取用户的购物车
     */
    public ShoppingCart getUserCart(int userId) {
        return carts.stream()
                .filter(c -> c.getUserId() == userId)
                .findFirst()
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart(userId);
                    carts.add(newCart);
                    return newCart;
                });
    }

    /**
     * 添加商品到购物车
     */
    public synchronized boolean addToCart(int userId, int productId, int quantity) {
        try {
            // 检查商品是否存在
            Product product = productManager.getProductById(productId);
            if (product == null || !"active".equals(product.getStatus())) {
                System.err.println("Product not found or not active: " + productId);
                return false;
            }

            // 检查库存
            if (!productManager.checkStock(productId, quantity)) {
                System.err.println("Insufficient stock for product: " + productId);
                return false;
            }

            // 获取或创建购物车
            ShoppingCart cart = getUserCart(userId);

            // 创建购物车项
            CartItem newItem = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
            );

            cart.addItem(newItem);
            dataManager.saveCarts(carts);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add to cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新购物车商品数量
     */
    public synchronized boolean updateCartItem(int userId, int productId, int quantity) {
        try {
            ShoppingCart cart = getUserCart(userId);

            if (quantity <= 0) {
                cart.removeItem(productId);
            } else {
                // 检查库存
                if (!productManager.checkStock(productId, quantity)) {
                    System.err.println("Insufficient stock for product: " + productId);
                    return false;
                }
                cart.updateQuantity(productId, quantity);
            }

            dataManager.saveCarts(carts);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update cart item: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从购物车删除商品
     */
    public synchronized boolean removeFromCart(int userId, int productId) {
        try {
            ShoppingCart cart = getUserCart(userId);
            cart.removeItem(productId);
            dataManager.saveCarts(carts);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to remove from cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * 清空购物车
     */
    public synchronized boolean clearCart(int userId) {
        try {
            ShoppingCart cart = getUserCart(userId);
            cart.clear();
            dataManager.saveCarts(carts);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to clear cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * 验证购物车（检查所有商品库存）
     */
    public boolean validateCart(int userId) {
        ShoppingCart cart = getUserCart(userId);
        for (CartItem item : cart.getItems()) {
            if (!productManager.checkStock(item.getProductId(), item.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 保存所有购物车
     */
    public synchronized void saveAll() {
        dataManager.saveCarts(carts);
    }

    /**
     * 刷新购物车列表
     */
    public void refresh() {
        this.carts = dataManager.loadCarts();
        if (this.carts == null) {
            this.carts = new ArrayList<>();
        }
    }
}
