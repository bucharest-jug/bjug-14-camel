package ro.ieugen.bjug14;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelStart {

    private static final Logger LOG = LoggerFactory.getLogger(CamelStart.class);

    public static void main(String[] args) throws Exception {
        CamelContext camelContext = new DefaultCamelContext();

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                LOG.info("Route started");
                from("servlet:///hello?servletName=camelServlet")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
                                path = path.substring(path.lastIndexOf("/"));

                                // assert camel http header
                                String charsetEncoding = exchange.getIn().getHeader(Exchange.HTTP_CHARACTER_ENCODING, String.class);
                                exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/html; charset=UTF-8");
                                exchange.getOut().setHeader("PATH", path);
                                exchange.getOut().setBody("<b>Hello World</b>\n");
                                LOG.info("Exchange is {}", exchange);
                            }
                        });
            }
        });

        camelContext.start();

        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder("camelServlet", new CamelHttpTransportServlet()), "/services/*");

        server.start();
        server.join();

        camelContext.stop();
    }
}
