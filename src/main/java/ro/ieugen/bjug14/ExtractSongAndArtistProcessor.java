package ro.ieugen.bjug14;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

public class ExtractSongAndArtistProcessor implements Processor {

    public static final Logger LOG = LoggerFactory.getLogger(ExtractSongAndArtistProcessor.class);

    public static final String OUT_NAME = "FileOutName";
    public static final String SONG_AND_ARTIST = "BJUGSongAndArtist";
    public static final String RETURN_PATH = "BJUGSongServiceReplyAddress";

    private Set<String> processed;

    public ExtractSongAndArtistProcessor(Set<String> processed) {
        this.processed = processed;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String header = exchange.getIn().getHeader("Subject", String.class);
        String returnPath = exchange.getIn().getHeader("Return-Path", String.class);

        exchange.setProperty(RETURN_PATH, returnPath); // TODO: sanitization !!
        exchange.setProperty(OUT_NAME, header + ".txt");// TODO: sanitization !!
        exchange.setProperty(SONG_AND_ARTIST, header); // TODO: sanitization !!
//        logHeaders(exchange);

        String key = buildKey(returnPath, header);
        LOG.info("Key is {}", key);
        if (processed.contains(key)) {
            LOG.info("Already processed key {}", key);
            exchange.getIn().setHeader(IauziVersuNeamule.ROUTING_SLIP_HEADER, IauziVersuNeamule.LOGGING_ROUTE);
        } else {
            LOG.info("Processing key {}", key);
            processed.add(key);
            exchange.getIn().setHeader(IauziVersuNeamule.ROUTING_SLIP_HEADER, IauziVersuNeamule.LINKS_ROUTE);
            String request = buildRequest(header);
            exchange.getIn().setHeader(Exchange.HTTP_QUERY, request);
            exchange.getIn().setBody(null);
        }

    }

    private String buildKey(String returnPath, String songAndArtist) {
        return returnPath + songAndArtist;
    }

    private String buildRequest(String header) throws URISyntaxException {
        String urlEncodedSearchTerm = new URI("http", header, null).toASCIIString().substring(4);
        return "search_query=" + urlEncodedSearchTerm + "&oq=" + urlEncodedSearchTerm;
    }

    private void logHeaders(Exchange exchange) {
        for (Map.Entry<String, Object> headers : exchange.getIn().getHeaders().entrySet()) {
            LOG.info("Header {} -> {}", headers.getKey(), headers.getValue());
        }
    }
}
