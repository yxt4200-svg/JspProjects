<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.example.model.CartItem" %>
<%@ page import="com.example.model.User" %>
<%
    // 登录检查
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 检查是否是管理员
    boolean isAdmin = "admin".equals(user.getRole());
%>
<html>
<head>
    <meta charset="UTF-8">
    <title>购物车详情</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            margin: 5px;
        }
        .btn-back {
            background: #6c757d;
            color: white;
        }
        .btn-back:hover {
            background: #5a6268;
        }
        .btn-clear {
            background: #f44336;
            color: white;
        }
        .btn-clear:hover {
            background: #da190b;
        }
        .btn-continue {
            background: #2196F3;
            color: white;
        }
        .btn-continue:hover {
            background: #0b7dda;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 20px 0;
            background: white;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #4CAF50;
            color: white;
            font-weight: bold;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        .total-row {
            background-color: #e8f5e8 !important;
            font-weight: bold;
            font-size: 16px;
        }
        .quantity-form {
            display: flex;
            gap: 5px;
            align-items: center;
        }
        .quantity-input {
            width: 60px;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .update-btn {
            background: #ff9800;
            color: white;
            border: none;
            padding: 4px 8px;
            border-radius: 3px;
            cursor: pointer;
        }
        .update-btn:hover {
            background: #e68a00;
        }
        .remove-btn {
            background: #f44336;
            color: white;
            border: none;
            padding: 4px 8px;
            border-radius: 3px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .remove-btn:hover {
            background: #da190b;
        }
        .message {
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
            text-align: center;
        }
        .success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .empty-cart {
            text-align: center;
            padding: 40px;
            color: #666;
            font-style: italic;
        }
        .price {
            color: #e74c3c;
            font-weight: bold;
        }
        .btn-container {
            text-align: center;
            margin: 20px 0;
        }
        .user-info {
            text-align: right;
            margin-bottom: 10px;
            padding: 10px;
            background: white;
            border-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .logout-link {
            margin-left: 10px;
            color: #f44336;
            text-decoration: none;
        }
        .logout-link:hover {
            text-decoration: underline;
        }
        .admin-notice {
            background: #fff3cd;
            color: #856404;
            padding: 15px;
            border-radius: 4px;
            border: 1px solid #ffeaa7;
            margin: 10px 0;
            text-align: center;
        }
    </style>
</head>
<body>
<h2>购物车详情</h2>

<!-- 用户信息和退出链接 -->
<div class="user-info">
        <span style="color: #666;">
            欢迎，<strong><%= user.getUsername() %></strong>
            <% if (isAdmin) { %>
                (管理员)
            <% } else { %>
                (用户)
            <% } %>
        </span>
    <a href="logout" class="logout-link">退出登录</a>
</div>

<a href="herbList.jsp" class="btn btn-back">← 继续购物</a>

<%
    // 显示错误消息
    String error = (String) session.getAttribute("error");
    if (error != null) {
        out.println("<div class='message error'>" + error + "</div>");
        session.removeAttribute("error");
    }

    // 显示操作消息
    String message = (String) session.getAttribute("message");
    if (message != null) {
        out.println("<div class='message success'>" + message + "</div>");
        session.removeAttribute("message");
    }
%>

<!-- 管理员提示信息 -->
<% if (isAdmin) { %>
<div class="admin-notice">
    <strong>管理员提示：</strong>您当前以管理员身份登录，无法查看购物车内容。如需购物，请使用普通用户账号登录。
</div>
<% } else { %>
<%
    List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

    if (cart == null || cart.isEmpty()) {
%>
<div class="empty-cart">
    <h3>购物车为空</h3>
    <p>您还没有添加任何商品到购物车</p>
    <a href="herbList.jsp" class="btn btn-continue">去选购商品</a>
</div>
<%
} else {
    double totalAmount = 0.0;
%>
<table>
    <tr>
        <th>商品名称</th>
        <th>单价</th>
        <th>数量</th>
        <th>小计</th>
        <th>操作</th>
    </tr>
    <%
        for (CartItem item : cart) {
            double itemTotal = item.getTotalPrice();
            totalAmount += itemTotal;
    %>
    <tr>
        <td><%= item.getName() %></td>
        <td class="price">¥<%= String.format("%.2f", item.getPrice()) %></td>
        <td>
            <form action="cart" method="post" class="quantity-form">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="<%= item.getId() %>">
                <input type="number" name="quantity" value="<%= item.getQuantity() %>" min="1" max="100" class="quantity-input" required>
                <button type="submit" class="update-btn">更新</button>
            </form>
        </td>
        <td class="price">¥<%= String.format("%.2f", itemTotal) %></td>
        <td>
            <form action="cart" method="post" style="display:inline;">
                <input type="hidden" name="action" value="remove">
                <input type="hidden" name="id" value="<%= item.getId() %>">
                <button type="submit" class="remove-btn" onclick="return confirm('确定要移除这个商品吗？')">移除</button>
            </form>
        </td>
    </tr>
    <%
        }
    %>
    <tr class="total-row">
        <td colspan="3" style="text-align: right;"><strong>总计：</strong></td>
        <td class="price" colspan="2"><strong>¥<%= String.format("%.2f", totalAmount) %></strong></td>
    </tr>
</table>

<div class="btn-container">
    <form action="cart" method="post" style="display:inline;">
        <input type="hidden" name="action" value="clear">
        <button type="submit" class="btn btn-clear" onclick="return confirm('确定要清空购物车吗？')">清空购物车</button>
    </form>
    <a href="herbList.jsp" class="btn btn-continue">继续购物</a>
    <button class="btn" style="background:#4CAF50; color:white;">结算付款</button>
</div>
<%
    }
%>
<% } %>
</body>
</html>