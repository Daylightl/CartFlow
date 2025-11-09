package com.cartflow.manager;

import com.cartflow.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理类
 * 负责用户的注册、登录和信息管理
 */
@Component
public class UserManager {

    @Autowired
    private DataManager dataManager;

    private List<User> users;

    @PostConstruct
    public void init() {
        this.users = dataManager.loadUsers();
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
    }

    /**
     * 用户注册
     */
    public synchronized User register(String username, String password, String email) {
        // 检查用户名是否已存在
        if (isUsernameExists(username)) {
            return null;
        }

        try {
            // 生成新用户ID
            int maxId = users.stream()
                    .mapToInt(User::getId)
                    .max()
                    .orElse(0);

            // 创建新用户
            User newUser = new User();
            newUser.setId(maxId + 1);
            newUser.setUsername(username);
            newUser.setPassword(encryptPassword(password));
            newUser.setEmail(email);
            newUser.setRole("user");
            newUser.setRegisterTime(getCurrentTime());

            users.add(newUser);
            dataManager.saveUsers(users);

            return newUser;
        } catch (Exception e) {
            System.err.println("Failed to register user: " + e.getMessage());
            return null;
        }
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        String encryptedPassword = encryptPassword(password);

        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .filter(u -> u.getPassword().equals(encryptedPassword))
                .findFirst()
                .orElse(null);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean isUsernameExists(String username) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * 更新用户信息
     */
    public synchronized boolean updateUser(User updatedUser) {
        try {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == updatedUser.getId()) {
                    users.set(i, updatedUser);
                    dataManager.saveUsers(users);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Failed to update user: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除用户
     */
    public synchronized boolean deleteUser(int id) {
        try {
            boolean removed = users.removeIf(u -> u.getId() == id);
            if (removed) {
                dataManager.saveUsers(users);
            }
            return removed;
        } catch (Exception e) {
            System.err.println("Failed to delete user: " + e.getMessage());
            return false;
        }
    }

    /**
     * 修改密码
     */
    public synchronized boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            String encryptedOldPassword = encryptPassword(oldPassword);
            if (!user.getPassword().equals(encryptedOldPassword)) {
                return false;
            }

            user.setPassword(encryptPassword(newPassword));
            dataManager.saveUsers(users);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to change password: " + e.getMessage());
            return false;
        }
    }

    /**
     * MD5加密密码
     */
    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 encryption failed", e);
        }
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
     * 刷新用户列表
     */
    public void refresh() {
        this.users = dataManager.loadUsers();
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
    }
}
