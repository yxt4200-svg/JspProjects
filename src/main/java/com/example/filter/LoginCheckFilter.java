package com.example.filter;

import com.example.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter({"/userInfo.jsp", "/cartDetail.jsp", "/herbList.jsp", "/cart"})
public class LoginCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String requestURI = req.getRequestURI();

        // 检查用户是否登录
        if (session == null || session.getAttribute("user") == null) {
            // 未登录，跳转到登录页面
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 已登录，继续执行
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}