package me.zhex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomServlet extends HttpServlet {

    private String ctx;

    public CustomServlet() {
        ctx = "/jsp";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doRequest(req, res);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doRequest(req, res);
    }

    private void doRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        final String template = req.getParameter("template");
        final String data = req.getParameter("data");

        final String ext = template.substring(template.lastIndexOf("."));
        if (ext.equals(".vm")) ctx = "/vm";


        JSONObject d = JSON.parseObject(data);
        if (d != null) {
            for (String key : d.keySet()) {
                req.setAttribute(key, d.get(key));
            }
        }

        res.setContentType("text/html");
        res.setCharacterEncoding(StringUtil.__UTF8);

        req.getServletContext()
                .getContext(ctx)
                .getRequestDispatcher(template)
                .forward(req, res);
    }
}
