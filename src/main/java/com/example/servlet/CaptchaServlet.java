package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        int width = 120, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String captcha = generateCaptcha(4);
        request.getSession().setAttribute("captcha", captcha);

        Random random = new Random();
        for (int i = 0; i < captcha.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 20, 25);
        }

        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }

        g.dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }

    private String generateCaptcha(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < length; i++) {
            captcha.append(chars.charAt(random.nextInt(chars.length())));
        }

        return captcha.toString();
    }
}