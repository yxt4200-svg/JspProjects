package com.example.dao;

import com.example.model.User;
import java.sql.*;

public class UserDAO {

    public User findByUsername(String username) {
        User user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/DBcm?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
            String userName = "root";
            String passWord = "123456";
            conn = DriverManager.getConnection(url, userName, passWord);

            String sql = "SELECT * FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }
}