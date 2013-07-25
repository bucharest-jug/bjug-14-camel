package ro.ieugen.bjug14;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class ExtractSongAndArtistProcessor implements Processor {

    public static final String OUT_NAME = "FileOutName";
    public static final String SONG_AND_ARTIST = "BJUGSongAndArtist";
    public static final String RETURN_PATH = "BJUGSongServiceReplyAddress";

    public static final Logger LOG = LoggerFactory.getLogger(ExtractSongAndArtistProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String header = exchange.getIn().getHeader("Subject", String.class);
        String returnPath = exchange.getIn().getHeader("Return-Path", String.class);

        exchange.setProperty(RETURN_PATH, returnPath); // TODO: sanitization !!
        exchange.setProperty(OUT_NAME, header + ".txt");// TODO: sanitization !!
        exchange.setProperty(SONG_AND_ARTIST, header); // TODO: sanitization !!
//        logHeaders(exchange);

        String urlEncodedSearchTerm = new URI("http", header, null).toASCIIString().substring(4);
        String request = "search_query=" + urlEncodedSearchTerm + "&oq=" + urlEncodedSearchTerm;
        LOG.debug("Sending request {}", request);

        exchange.getIn().setHeader(Exchange.HTTP_QUERY, request);
        exchange.getIn().setBody(null);
    }

    private void logHeaders(Exchange exchange) {
        for (Map.Entry<String, Object> headers : exchange.getIn().getHeaders().entrySet()) {
            LOG.info("Header {} -> {}", headers.getKey(), headers.getValue());
        }
    }
}
