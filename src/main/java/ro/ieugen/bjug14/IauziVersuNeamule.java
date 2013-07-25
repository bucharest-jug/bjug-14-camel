package ro.ieugen.bjug14;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IauziVersuNeamule extends RouteBuilder {

    public static final Logger LOG = LoggerFactory.getLogger(IauziVersuNeamule.class);

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
                "unseen=false&" +
                "disconnect=true&" +
                "mapMailMessage=false&" +
                "searchTerm.subjectOrBody=Shazam")
                .convertBodyTo(String.class)
                .process(new ExtractSongAndArtistProcessor())
                .to("seda:request-song-lyrics");

        from("seda:request-song-lyrics")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("ahc://http://www.youtube.com/results")
                .convertBodyTo(String.class)
                .process(new YoutubeLinkExtractProcessor())
                .to("smtps://smtp.gmail.com?" +
                        "username=bjug.demo@gmail.com&" +
                        "password=Demo#1234");
    }
}
