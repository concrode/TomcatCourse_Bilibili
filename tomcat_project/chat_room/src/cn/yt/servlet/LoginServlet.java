package cn.yt.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "loginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final String PASSWORD = "123456";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set uft-8 encoding
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Map resultMap = new HashMap<>();

        if (PASSWORD.equals(password)) {
            resultMap.put("success", true);
            resultMap.put("message", "login success");

            req.getSession().setAttribute("username", username);
        } else {
            resultMap.put("success", false);
            resultMap.put("message", "login failed");
        }

        resp.getWriter().write(JSON.toJSONString(resultMap));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
