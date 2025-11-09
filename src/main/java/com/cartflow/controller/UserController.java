package com.cartflow.controller;

import com.cartflow.manager.UserManager;
import com.cartflow.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserManager userManager;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");

        // 验证输入
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "用户名不能为空");
            return ResponseEntity.ok(response);
        }

        if (password == null || password.length() < 6) {
            response.put("success", false);
            response.put("message", "密码至少6位");
            return ResponseEntity.ok(response);
        }

        if (email == null || !email.contains("@")) {
            response.put("success", false);
            response.put("message", "邮箱格式不正确");
            return ResponseEntity.ok(response);
        }

        // 注册用户
        User user = userManager.register(username, password, email);

        if (user != null) {
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", sanitizeUser(user));
        } else {
            response.put("success", false);
            response.put("message", "用户名已存在");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "用户名或密码不能为空");
            return ResponseEntity.ok(response);
        }

        User user = userManager.login(username, password);

        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", sanitizeUser(user));
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     */
    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        User user = userManager.getUserById(userId);

        if (user != null) {
            response.put("success", true);
            response.put("data", sanitizeUser(user));
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 修改密码
     */
    @PostMapping("/user/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.ok(response);
        }

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            response.put("success", false);
            response.put("message", "新密码至少6位");
            return ResponseEntity.ok(response);
        }

        boolean result = userManager.changePassword(userId, oldPassword, newPassword);

        if (result) {
            response.put("success", true);
            response.put("message", "密码修改成功");
        } else {
            response.put("success", false);
            response.put("message", "原密码错误");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 去除敏感信息
     */
    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> sanitized = new HashMap<>();
        sanitized.put("id", user.getId());
        sanitized.put("username", user.getUsername());
        sanitized.put("email", user.getEmail());
        sanitized.put("role", user.getRole());
        sanitized.put("registerTime", user.getRegisterTime());
        return sanitized;
    }
}
