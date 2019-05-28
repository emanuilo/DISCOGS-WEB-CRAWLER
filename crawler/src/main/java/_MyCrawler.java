import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.http.Header;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class _MyCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz))$");
    private static int visitedPages = 1;
    private static final String PATTERN1 = "https://www.discogs.com/search/?page=";
    private static final String PATTERN2 = "&country_exact=yugoslavia";


    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        System.out.println("shouldVisit: " + url.getURL().toLowerCase());
        String href = url.getURL().toLowerCase();

        if (href.startsWith("https://www.discogs.com/search/?page=") && extractPageNumber(href) > visitedPages){
            System.out.println("URL Should Visit");
            visitedPages++;
            return true;
        }

        System.out.println("URL Should not Visit");
        return false;
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("---------------------------------------------------------");
            System.out.println("Page URL: " + url);
            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
            System.out.println("---------------------------------------------------------");
        }
//        page.setRedirect(true);
//        page.setRedirectedToUrl("https://www.discogs.com/search/?page=2&country_exact=Yugoslavia");

    }

    private int extractPageNumber(String href){
        String regex = Pattern.quote(PATTERN1) + "([0-9]*?)" + Pattern.quote(PATTERN2);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(href);
        if (matcher.find())
            return Integer.parseInt(matcher.group(1));
        return -1;
    }
}
