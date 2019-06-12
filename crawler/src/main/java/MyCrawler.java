import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyCrawler {
    private static final String seed = "https://www.discogs.com/search/?country_exact=Serbia";
    private static final int SHORT_TIMEOUT = 500;
    private static final int LONG_TIMEOUT = 500;

    private Logger albumLog;

    public MyCrawler(Logger logger) throws IOException {
//        this.log = logger;
        albumLog = Logger.getLogger("Album Logger");
        FileHandler fileHandler = new FileHandler("logs/albums/albumLog" + System.currentTimeMillis() + ".log");
        albumLog.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
        albumLog.info("Logger set up!");
    }

    public void visitAlbumPage(String url) throws InterruptedException {
        Thread.sleep(SHORT_TIMEOUT);
        albumLog.info("VISITED ALBUM: " + url);
    }

    public void visitPage(Document document) throws InterruptedException {
        Elements elements = document.select("div#search_results > div");                                 // get the direct children of the div with id = "search_results"
        for (Element element : elements){
            String albumUrl = element.select("a.search_result_title").attr("abs:href");       // get anchor with class = "search_result_title"
            visitAlbumPage(albumUrl);
        }
    }

    public String nextPage(Document document){
        Elements elements = document.select("a.pagination_next");
        if (!elements.isEmpty())
            return elements.first().attr("abs:href");
        return "";
    }

    public static void setupLogger(Logger logger) throws IOException {
        FileHandler fileHandler = new FileHandler("logs/pages/pageLog" + System.currentTimeMillis() + ".log");
        logger.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.info("Logger set up!");
    }

    public static void main(String[] args) {
        try {
            Logger pageLog = Logger.getLogger("Page Logger");
            setupLogger(pageLog);
            MyCrawler crawler = new MyCrawler(pageLog);
            String url = seed;
            while(!url.equals("")){
                pageLog.info("VISITED PAGE: " + url);
//                Thread.sleep(LONG_TIMEOUT);
                Document doc = Jsoup.connect(url).get();
                crawler.visitPage(doc);
                url = crawler.nextPage(doc);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
