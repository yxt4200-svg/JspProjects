package com.example.listener;

import com.example.model.User;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class SessionListener implements HttpSessionListener {

    private static final ConcurrentHashMap<String, HttpSession> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session创建: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            onlineUsers.remove(user.getUsername());
            System.out.println("用户 " + user.getUsername() + " 的session已销毁");
        }
    }

    public static boolean isUserAlreadyLoggedIn(String username) {
        return onlineUsers.containsKey(username);
    }

    public static void invalidatePreviousSession(String username) {
        HttpSession previousSession = onlineUsers.get(username);
        if (previousSession != null) {
            previousSession.invalidate();
            onlineUsers.remove(username);
        }
    }

    public static void addOnlineUser(String username, HttpSession session) {
        onlineUsers.put(username, session);
    }

    public static void removeOnlineUser(String username) {
        onlineUsers.remove(username);
    }
}