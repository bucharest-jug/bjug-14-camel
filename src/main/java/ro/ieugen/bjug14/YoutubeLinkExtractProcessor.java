package ro.ieugen.bjug14;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class YoutubeLinkExtractProcessor implements Processor {

    public static final Logger LOG = LoggerFactory.getLogger(YoutubeLinkExtractProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String youtubeResponse = exchange.getIn().getBody(String.class);
        String songAndArtist = exchange.getProperty(ExtractSongAndArtistProcessor.SONG_AND_ARTIST, String.class);
        String replyPath = exchange.getProperty(ExtractSongAndArtistProcessor.RETURN_PATH, String.class);

        Document parsedPage = Jsoup.parse(youtubeResponse);
        Elements searchResultLinks = parsedPage.select("div.yt-lockup2-content a.yt-uix-tile-link");
        List<String> links = extractLinksFromPage(searchResultLinks);

        Map<String, Object> map = buildEmailHeadersMap(songAndArtist, replyPath);
        StringBuilder body = buildEmailResponseBody(links);

        exchange.getIn().getHeaders().putAll(map);
        exchange.getIn().setBody(body.toString());
    }

    private StringBuilder buildEmailResponseBody(List<String> links) {
        StringBuilder body = new StringBuilder("Hello,\n\n<br/>Here is a list of links you might be interested:\n\n<br/>");
        int count = 1;
        for (String link : links) {
            body.append(String.format("%3d. ", count++)).append(link).append("\n<br/>");
        }
        return body;
    }

    private Map<String, Object> buildEmailHeadersMap(String songAndArtist, String replyPath) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("Subject", "Youtube links for " + songAndArtist);
        map.put("To", replyPath);
        map.put("From", "bjug.demo@gmail.com");
        return map;
    }

    private List<String> extractLinksFromPage(Elements searchResultLinks) {
        List<String> links = Lists.newArrayList();
        for (Element element : searchResultLinks) {
            if (element.hasAttr("href") && element.hasAttr("title")) {
                String title = element.attr("title");
                String href = element.attr("href");
                links.add(String.format("Listen to %s at http://www.youtube.com/%s", title, href));
            }
        }
        return links;
    }
}
