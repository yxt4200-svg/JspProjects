package com.example.servlet;

import com.example.model.User;
import com.example.dao.UserDAO;
import com.example.listener.SessionListener;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        String sessionCaptcha = (String) request.getSession().getAttribute("captcha");

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                captcha == null || captcha.trim().isEmpty()) {

            request.setAttribute("error", "所有字段都必须填写！");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            request.setAttribute("error", "验证码错误！");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            request.setAttribute("error", "用户名或密码错误！");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (SessionListener.isUserAlreadyLoggedIn(username)) {
            SessionListener.invalidatePreviousSession(username);
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        SessionListener.addOnlineUser(username, session);
        session.removeAttribute("captcha");

        // 所有用户都跳转到 herbList.jsp，但在页面中根据角色控制显示
        response.sendRedirect(request.getContextPath() + "/herbList.jsp");
    }
}