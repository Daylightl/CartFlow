package com.cartflow.manager;

import com.cartflow.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品管理类
 * 负责商品的增删改查和库存管理
 */
@Component
public class ProductManager {

    @Autowired
    private DataManager dataManager;

    private List<Product> products;

    @PostConstruct
    public void init() {
        this.products = dataManager.loadProducts();
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
    }

    /**
     * 获取所有商品
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * 获取所有上架商品
     */
    public List<Product> getActiveProducts() {
        return products.stream()
                .filter(p -> "active".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取商品
     */
    public Product getProductById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * 搜索商品（按名称或描述）
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveProducts();
        }

        String lowerKeyword = keyword.toLowerCase().trim();
        return products.stream()
                .filter(p -> "active".equals(p.getStatus()))
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword) ||
                           p.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * 按分类获取商品
     */
    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getActiveProducts();
        }

        return products.stream()
                .filter(p -> "active".equals(p.getStatus()))
                .filter(p -> category.equals(p.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        return products.stream()
                .filter(p -> "active".equals(p.getStatus()))
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 添加商品
     */
    public synchronized boolean addProduct(Product product) {
        try {
            // 生成新ID
            int maxId = products.stream()
                    .mapToInt(Product::getId)
                    .max()
                    .orElse(0);
            product.setId(maxId + 1);

            products.add(product);
            dataManager.saveProducts(products);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add product: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新商品
     */
    public synchronized boolean updateProduct(Product updatedProduct) {
        try {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId() == updatedProduct.getId()) {
                    products.set(i, updatedProduct);
                    dataManager.saveProducts(products);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Failed to update product: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除商品（软删除，设置为inactive）
     */
    public synchronized boolean deleteProduct(int id) {
        try {
            Product product = getProductById(id);
            if (product != null) {
                product.setStatus("inactive");
                dataManager.saveProducts(products);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Failed to delete product: " + e.getMessage());
            return false;
        }
    }

    /**
     * 物理删除商品
     */
    public synchronized boolean removeProduct(int id) {
        try {
            boolean removed = products.removeIf(p -> p.getId() == id);
            if (removed) {
                dataManager.saveProducts(products);
            }
            return removed;
        } catch (Exception e) {
            System.err.println("Failed to remove product: " + e.getMessage());
            return false;
        }
    }

    /**
     * 检查库存是否充足
     */
    public boolean checkStock(int productId, int quantity) {
        Product product = getProductById(productId);
        return product != null && product.getStock() >= quantity && "active".equals(product.getStatus());
    }

    /**
     * 减少库存
     */
    public synchronized boolean reduceStock(int productId, int quantity) {
        try {
            Product product = getProductById(productId);
            if (product == null) {
                return false;
            }

            if (product.getStock() < quantity) {
                return false;
            }

            product.setStock(product.getStock() - quantity);
            dataManager.saveProducts(products);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to reduce stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * 增加库存
     */
    public synchronized boolean addStock(int productId, int quantity) {
        try {
            Product product = getProductById(productId);
            if (product == null) {
                return false;
            }

            product.setStock(product.getStock() + quantity);
            dataManager.saveProducts(products);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to add stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新库存
     */
    public synchronized boolean updateStock(int productId, int newStock) {
        try {
            Product product = getProductById(productId);
            if (product == null) {
                return false;
            }

            product.setStock(newStock);
            dataManager.saveProducts(products);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * 刷新商品列表
     */
    public void refresh() {
        this.products = dataManager.loadProducts();
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
    }
}
