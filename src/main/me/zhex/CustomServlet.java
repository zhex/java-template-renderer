package me.zhex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        doRequest(req, res);
    }

    private void doRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        final String template = req.getParameter("template");
        final String data = req.getParameter("data");

        JSONObject d = JSON.parseObject(data);
        for (String key : d.keySet()) {
            req.setAttribute(key, d.get(key));
        }

        req.getServletContext()
                .getContext("/r/")
                .getRequestDispatcher(template)
                .forward(req, res);
    }
}
