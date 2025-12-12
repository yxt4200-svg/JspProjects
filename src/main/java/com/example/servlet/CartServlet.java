package com.example.servlet;

import com.example.model.CartItem;
import com.example.model.User;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 检查管理员权限
        if (checkAdminPermission(request, response)) {
            return;
        }
        request.getRequestDispatcher("/cartDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 检查管理员权限
        if (checkAdminPermission(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addToCart(request, response);
        } else if ("update".equals(action)) {
            updateCart(request, response);
        } else if ("remove".equals(action)) {
            removeFromCart(request, response);
        } else if ("clear".equals(action)) {
            clearCart(request, response);
        } else {
            request.getRequestDispatcher("/cartDetail.jsp").forward(request, response);
        }
    }

    // 检查管理员权限
    private boolean checkAdminPermission(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null && "admin".equals(user.getRole())) {
            session.setAttribute("error", "管理员账号不能进行购物操作，请使用普通用户账号购物。");
            response.sendRedirect(request.getContextPath() + "/herbList.jsp");
            return true;
        }
        return false;
    }

    // 其他方法保持不变...
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            boolean found = false;
            for (CartItem item : cart) {
                if (item.getId() == id) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                cart.add(new CartItem(id, name, price, quantity));
            }

            session.setAttribute("message", "商品已成功添加到购物车！");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "输入数据格式错误！");
        }

        response.sendRedirect("herbList.jsp");
    }

    private void updateCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

            if (cart != null) {
                for (CartItem item : cart) {
                    if (item.getId() == id) {
                        if (quantity <= 0) {
                            cart.remove(item);
                        } else {
                            item.setQuantity(quantity);
                        }
                        break;
                    }
                }
            }

            session.setAttribute("message", "购物车已更新！");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "输入数据格式错误！");
        }

        response.sendRedirect("cartDetail.jsp");
    }

    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

            if (cart != null) {
                cart.removeIf(item -> item.getId() == id);
            }

            session.setAttribute("message", "商品已从购物车移除！");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "输入数据格式错误！");
        }

        response.sendRedirect("cartDetail.jsp");
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        session.setAttribute("message", "购物车已清空！");

        response.sendRedirect("cartDetail.jsp");
    }

    public static double getTotalAmount(List<CartItem> cart) {
        if (cart == null) return 0.0;

        double total = 0.0;
        for (CartItem item : cart) {
            total += item.getTotalPrice();
        }
        return total;
    }
}