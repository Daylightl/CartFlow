// CartFlow 公共JavaScript函数

// API基础URL
const API_BASE = '/api';

// 当前用户信息
let currentUser = null;

// 页面加载时检查登录状态
document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
});

// 检查登录状态
async function checkLoginStatus() {
    try {
        const response = await fetch(`${API_BASE}/user/info`, {
            credentials: 'include'
        });
        const result = await response.json();

        if (result.success) {
            currentUser = result.data;
            updateNavbar(true);
        } else {
            currentUser = null;
            updateNavbar(false);
        }
    } catch (error) {
        console.error('检查登录状态失败:', error);
        updateNavbar(false);
    }
}

// 更新导航栏
function updateNavbar(isLoggedIn) {
    const userElement = document.getElementById('navbar-user');
    if (!userElement) return;

    if (isLoggedIn && currentUser) {
        userElement.innerHTML = `
            <span>欢迎, ${currentUser.username}</span>
            ${currentUser.role === 'admin' ? '<a href="/admin.html">管理后台</a>' : ''}
            <a href="/order-list.html">我的订单</a>
            <a href="/cart.html">购物车</a>
            <a href="#" onclick="logout()">退出</a>
        `;
    } else {
        userElement.innerHTML = `
            <a href="/login.html">登录</a>
            <a href="/register.html">注册</a>
        `;
    }
}

// 登出
async function logout() {
    try {
        const response = await fetch(`${API_BASE}/logout`, {
            credentials: 'include'
        });
        const result = await response.json();

        if (result.success) {
            showMessage('已退出登录', 'success');
            currentUser = null;
            setTimeout(() => {
                window.location.href = '/index.html';
            }, 1000);
        }
    } catch (error) {
        console.error('退出登录失败:', error);
        showMessage('退出登录失败', 'error');
    }
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const messageEl = document.createElement('div');
    messageEl.className = `message message-${type}`;
    messageEl.textContent = message;

    document.body.appendChild(messageEl);

    setTimeout(() => {
        messageEl.remove();
    }, 3000);
}

// 格式化价格
function formatPrice(price) {
    return '¥' + price.toFixed(2);
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    return dateString.replace('T', ' ').substring(0, 19);
}

// HTTP请求封装
async function request(url, options = {}) {
    const defaultOptions = {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const finalOptions = { ...defaultOptions, ...options };

    try {
        const response = await fetch(url, finalOptions);
        return await response.json();
    } catch (error) {
        console.error('请求失败:', error);
        throw error;
    }
}

// GET请求
async function get(url) {
    return request(url, { method: 'GET' });
}

// POST请求
async function post(url, data) {
    return request(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

// PUT请求
async function put(url, data) {
    return request(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// DELETE请求
async function del(url, data = null) {
    const options = { method: 'DELETE' };
    if (data) {
        options.body = JSON.stringify(data);
    }
    return request(url, options);
}

// 检查是否登录，未登录跳转到登录页
function requireLogin() {
    if (!currentUser) {
        showMessage('请先登录', 'warning');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1000);
        return false;
    }
    return true;
}

// 检查管理员权限
function requireAdmin() {
    if (!currentUser || currentUser.role !== 'admin') {
        showMessage('需要管理员权限', 'error');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 1000);
        return false;
    }
    return true;
}

// 获取URL参数
function getUrlParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}
