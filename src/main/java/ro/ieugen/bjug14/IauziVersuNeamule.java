package ro.ieugen.bjug14;


import com.google.common.collect.Sets;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class IauziVersuNeamule extends RouteBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(IauziVersuNeamule.class);
    public static final String ROUTING_SLIP_HEADER = "BJUG_DESTINATION";
    public static final String LINKS_ROUTE = "seda:request-youtube-links";
    public static final String LOGGING_ROUTE = "seda:just-log-this-request";

    private Set<String> processed = Sets.newHashSet();


    @Override
    public void configure() throws Exception {

        onException(javax.mail.FolderClosedException.class)
                .handled(true)
                .log("Got folder closed exception again")
                .logHandled(false);

        onException(Exception.class)
                .handled(true)
                .logHandled(true);

        from("imaps://bjug.demo@imap.gmail.com?" +
                "username=bjug.demo&" +
                "password=Demo#1234&" +
                "delete=true&" +
                "mapMailMessage=false&" +
                "searchTerm.subjectOrBody=Shazam")
                .convertBodyTo(String.class)
                .process(new ExtractSongAndArtistProcessor(processed))
                .routingSlip(header(ROUTING_SLIP_HEADER));

        from(LINKS_ROUTE)
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("ahc://http://www.youtube.com/results")
                .convertBodyTo(String.class)
                .process(new YoutubeLinkExtractProcessor())
                .to("smtps://smtp.gmail.com?" +
                        "username=bjug.demo@gmail.com&" +
                        "password=Demo#1234");

        from(LOGGING_ROUTE)
                .log("Already processed email");
    }
}
