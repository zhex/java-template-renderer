package me.zhex;

import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.util.Collections;
import java.util.List;


public class Application {

    static final String INCLUDE_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    static final String JSP_PATTERN = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$";

    static {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
    }

    public static void main(String[] args) throws Exception {
        final int port = Integer.getInteger("server.port", 8080);
        String viewPath = System.getProperty("viewpath");

        Server server = createServer(viewPath, port);

        server.start();
        server.join();
    }

    public static Server createServer(String viewPath, int port) {
        Server server = new Server(port);

        final ServletContextHandler entryContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        entryContext.setContextPath("/");
        entryContext.addServlet(new ServletHolder(new CustomServlet()), "/render");

        final ContextHandler vmContext = new ContextHandler("/vm");
        vmContext.setHandler(new VelocityHandler(viewPath));

        WebAppContext jspContext = new WebAppContext(viewPath, "/jsp");

        final URL url = Application.class.getProtectionDomain().getCodeSource().getLocation();
        if (url != null) {
            jspContext.getMetaData().addWebInfJar(Resource.newResource(url));
            String descriptor = "jar:" + url + "!/empty-web.xml";
            jspContext.setDescriptor(descriptor);
        }
        jspContext.setAttribute(INCLUDE_JAR_PATTERN, JSP_PATTERN);
        jspContext.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        jspContext.addBean(new ServletContainerInitializersStarter(jspContext), true);

        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{
                entryContext,
                vmContext,
                jspContext
        });
        server.setHandler(handlers);

        return server;
    }

    private static List<ContainerInitializer> jspInitializers() {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        return Collections.singletonList(initializer);
    }


}
