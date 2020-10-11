package cn.he.com;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Bbsservlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("************ >> Bbsservlet doGet ....");

        String value = req.getServletContext().getInitParameter("param_01");
        System.out.println(value);

        String id = req.getSession().getId();
        System.out.println("SessionId: " + id);

        int i = 1 / 0;

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("************ >> Bbsservlet doPost ....");
    }
}
