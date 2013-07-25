package ro.ieugen.bjug14;

import org.apache.camel.CamelContext;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelStart {

    private static final Logger LOG = LoggerFactory.getLogger(CamelStart.class);

    public static void main(String[] args) throws Exception {
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));

        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new SimpleTest());
        camelContext.start();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/services");
        context.addServlet(new ServletHolder("camelServlet", new CamelHttpTransportServlet()), "/*");

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase(".");

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{context, resource_handler, new DefaultHandler()});

        server.setHandler(handlerList);

        server.start();
        server.join();

        camelContext.stop();
    }
}
