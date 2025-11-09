package com.cartflow.manager;

import com.cartflow.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据持久化管理类
 * 负责JSON文件的读写操作
 */
@Component
public class DataManager {
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.json";
    private static final String USERS_FILE = DATA_DIR + "users.json";
    private static final String CARTS_FILE = DATA_DIR + "carts.json";
    private static final String ORDERS_FILE = DATA_DIR + "orders.json";

    private final Gson gson;

    public DataManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initializeDataDirectory();
    }

    /**
     * 初始化数据目录
     */
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    /**
     * 保存商品列表
     */
    public synchronized void saveProducts(List<Product> products) {
        saveToFile(PRODUCTS_FILE, products);
    }

    /**
     * 加载商品列表
     */
    public List<Product> loadProducts() {
        Type listType = new TypeToken<List<Product>>(){}.getType();
        List<Product> products = loadFromFile(PRODUCTS_FILE, listType);
        return products != null ? products : new ArrayList<>();
    }

    /**
     * 保存用户列表
     */
    public synchronized void saveUsers(List<User> users) {
        saveToFile(USERS_FILE, users);
    }

    /**
     * 加载用户列表
     */
    public List<User> loadUsers() {
        Type listType = new TypeToken<List<User>>(){}.getType();
        List<User> users = loadFromFile(USERS_FILE, listType);
        return users != null ? users : new ArrayList<>();
    }

    /**
     * 保存购物车列表
     */
    public synchronized void saveCarts(List<ShoppingCart> carts) {
        saveToFile(CARTS_FILE, carts);
    }

    /**
     * 加载购物车列表
     */
    public List<ShoppingCart> loadCarts() {
        Type listType = new TypeToken<List<ShoppingCart>>(){}.getType();
        List<ShoppingCart> carts = loadFromFile(CARTS_FILE, listType);
        return carts != null ? carts : new ArrayList<>();
    }

    /**
     * 保存订单列表
     */
    public synchronized void saveOrders(List<Order> orders) {
        saveToFile(ORDERS_FILE, orders);
    }

    /**
     * 加载订单列表
     */
    public List<Order> loadOrders() {
        Type listType = new TypeToken<List<Order>>(){}.getType();
        List<Order> orders = loadFromFile(ORDERS_FILE, listType);
        return orders != null ? orders : new ArrayList<>();
    }

    /**
     * 通用保存方法
     */
    private <T> void saveToFile(String filename, T data) {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save data to " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 通用加载方法
     */
    private <T> T loadFromFile(String filename, Type type) {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("Failed to load data from " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String filename) {
        return new File(filename).exists();
    }

    /**
     * 获取数据文件路径
     */
    public String getProductsFilePath() {
        return PRODUCTS_FILE;
    }

    public String getUsersFilePath() {
        return USERS_FILE;
    }

    public String getCartsFilePath() {
        return CARTS_FILE;
    }

    public String getOrdersFilePath() {
        return ORDERS_FILE;
    }
}
