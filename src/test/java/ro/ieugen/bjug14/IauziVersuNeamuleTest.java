package ro.ieugen.bjug14;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Ignore
public class IauziVersuNeamuleTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new IauziVersuNeamule();
    }


    @Test
    public void testEMailIsRead() throws Exception {
        TimeUnit.SECONDS.sleep(10);
    }

}
