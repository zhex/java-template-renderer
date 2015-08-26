package me.zhex;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;


public class Application {

    static final String INCLUDE_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    static final String JSP_PATTERN = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$";


    public static void main(String[] args) throws Exception {
        final int port = Integer.getInteger("server.port", 8080);
        String viewPath = System.getProperty("viewpath");

        Server server = createServer(viewPath, port);

        server.start();
        server.join();
    }

    public static Server createServer(String viewPath, int port) {
        Server server = new Server(port);

        final ServletContextHandler servletContextHandler = new ServletContextHandler(null, "/", false, false);
        servletContextHandler.addServlet(new ServletHolder(new CustomServlet()), "/");
//        final URL url = Application.class.getProtectionDomain().getCodeSource().getLocation();

//        File tempDir = new File(System.getProperty("java.io.tmpdir"));
//        File scratchDir = new File(tempDir.toString(), "jtr");
//        if (!scratchDir.exists()) {
//            if (!scratchDir.mkdirs()) {
//                throw new RuntimeException("Unable to create scratch directory: " + scratchDir);
//            }
//        }


        WebAppContext context = new WebAppContext(viewPath, "/r/");

//        if (url != null) {
//            context.getMetaData().addWebInfJar(Resource.newResource(url));
//            String descriptor = "jar:" + url + "!/empty-web.xml";
//            context.setDescriptor(descriptor);
//        }


        context.setAttribute(INCLUDE_JAR_PATTERN, JSP_PATTERN);
//        context.setAttribute("javax.servlet.context.tempdir", scratchDir);
        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.addBean(new ServletContainerInitializersStarter(context), true);
//        context.setClassLoader(new URLClassLoader(new URL[0], Application.class.getClassLoader()));

        final HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{
                servletContextHandler,
                context
        });
        server.setHandler(handlers);

        return server;
    }

    static List<ContainerInitializer> jspInitializers() {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        return Collections.singletonList(initializer);
    }


}
