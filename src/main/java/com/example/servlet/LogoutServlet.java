package com.example.servlet;

import com.example.model.User;
import com.example.listener.SessionListener;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                SessionListener.removeOnlineUser(user.getUsername());
            }
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp?logout=true");
    }
}