<%@ page language="java" import="java.sql.*" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
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
    <title>中药材商城</title>
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
        .cart-btn {
            background: #ff9800;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin-bottom: 20px;
        }
        .cart-btn:hover {
            background: #e68a00;
        }
        .admin-btn {
            background: #4CAF50;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin-bottom: 20px;
            margin-left: 10px;
        }
        .admin-btn:hover {
            background: #45a049;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin: 10px 0;
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
        .add-to-cart-form {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        .quantity-input {
            width: 60px;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .add-btn {
            background: #2196F3;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            cursor: pointer;
        }
        .add-btn:hover {
            background: #0b7dda;
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
        .error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .price {
            color: #e74c3c;
            font-weight: bold;
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
<h2>中药材商城</h2>

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
    <strong>管理员提示：</strong>您当前以管理员身份登录，无法进行购物操作。如需购物，请使用普通用户账号登录。
</div>
<% } %>

<!-- 购物车链接（仅对普通用户显示） -->
<% if (!isAdmin) { %>
<a href="cartDetail.jsp" class="cart-btn">查看购物车</a>
<% } %>


<%
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/DBcm?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String userName = "root";
        String passWord = "123456";
        conn = DriverManager.getConnection(url,userName,passWord);
        stmt = conn.createStatement();
        String sql = "select * from medicine";
        rs = stmt.executeQuery(sql);
%>

<table>
    <tr>
        <th>编号</th>
        <th>中药名</th>
        <th>别名</th>
        <th>价格</th>
        <th>操作</th>
    </tr>
    <%
        // 模拟价格数据
        java.util.Map<String, Double> priceMap = new java.util.HashMap<>();
        priceMap.put("人参", 150.0);
        priceMap.put("枸杞", 80.0);
        priceMap.put("当归", 120.0);
        priceMap.put("黄芪", 90.0);
        priceMap.put("金银花", 60.0);
        priceMap.put("蒲公英", 45.0);

        while( rs.next() ) {
            int id = rs.getInt("id");
            String number = rs.getString("number");
            String name = rs.getString("name");
            String alias = rs.getString("alias");

            Double price = priceMap.get(name);
            if (price == null) {
                price = 50.0;
            }
    %>
    <tr>
        <td><%=number %></td>
        <td><strong><%=name %></strong></td>
        <td><%=alias != null ? alias : "无" %></td>
        <td class="price">¥<%=String.format("%.2f", price) %></td>
        <td>
            <% if (!isAdmin) { %>
            <form action="cart" method="post" class="add-to-cart-form">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="id" value="<%=id %>">
                <input type="hidden" name="name" value="<%=name %>">
                <input type="hidden" name="price" value="<%=price %>">
                <input type="number" name="quantity" value="1" min="1" max="100" class="quantity-input" required>
                <button type="submit" class="add-btn">加入购物车</button>
            </form>
            <% } else { %>
            <span style="color: #999; font-style: italic;">管理员无法购物</span>
            <% } %>
        </td>
    </tr>
    <%
            }
        } catch(Exception e) {
            out.println("<div class='message error'>错误: " + e.getMessage() + "</div>");
            e.printStackTrace();
        } finally {
            if(rs != null) try { rs.close(); } catch(Exception e) {}
            if(stmt != null) try { stmt.close(); } catch(Exception e) {}
            if(conn != null) try { conn.close(); } catch(Exception e) {}
        }
    %>
</table>
</body>
</html>