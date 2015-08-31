package me.zhex;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class VelocityHandler extends AbstractHandler {

    private final VelocityEngine engine;

    public VelocityHandler(String viewPath) {
        Properties props = new Properties();
        props.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, viewPath);

        engine = new VelocityEngine();
        engine.init(props);
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        baseRequest.setHandled(true);

        response.setContentType("text/html");
        response.setCharacterEncoding(StringUtil.__UTF8);

        Enumeration<String> attrs = request.getAttributeNames();
        final VelocityContext context = new VelocityContext();

        while (attrs.hasMoreElements()) {
            String name = attrs.nextElement();
            if (!name.startsWith("javax.servlet")) {
                context.put(name, request.getAttribute(name));
            }
        }

        final Template template = engine.getTemplate(target, StringUtil.__UTF8);;
        template.merge(context, response.getWriter());
    }
}
