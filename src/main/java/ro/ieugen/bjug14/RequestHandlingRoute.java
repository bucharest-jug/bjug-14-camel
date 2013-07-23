package ro.ieugen.bjug14;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHandlingRoute extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandlingRoute.class);

    @Override
    public void configure() throws Exception {
        LOG.info("Route started");
        from("servlet:///hello?servletName=camelServlet")
                .process(new HttpRequestProcessor());
    }

    class HttpRequestProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
            path = path.substring(path.lastIndexOf("/"));

            String charsetEncoding = exchange.getIn().getHeader(Exchange.HTTP_CHARACTER_ENCODING, String.class);
            exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/html; charset=UTF-8");
            exchange.getOut().setHeader("PATH", path);
            exchange.getOut().setBody("<b>Hello World</b>\n");
            LOG.info("Exchange is {}", exchange);
        }
    }
}
