import db.Artist;
import db.DataAccessLayer;
import db.TransactionalCode;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public void visitAlbumPage(String url) throws InterruptedException, IOException {
        Thread.sleep(SHORT_TIMEOUT);
        albumLog.info("VISITED ALBUM: " + url);
        Document doc = Jsoup.connect(url).get();

//        DataAccessLayer.transactional(new TransactionalCode<Void>() {
//
//            @Override
//            public Void run(Session session) {
                // Artist name
                Elements elements = doc.select("div.profile > h1 > span > span[title]");
                Artist artist = null;
                for (Element element : elements) {
                    String artistName = element.attr("title");
                    artist = new Artist(artistName);
                }

//                Query query = session.createQuery("from Artist where name=:name");
//                query.setParameter("name", artist.getName());
//                List artists = query.list();
//                if (artists.size() == 1){
//                        artist = (Artist) artists.get(0);
//                }


                // Album name
                String albumName = doc.select("div.profile > h1 > span").next().text();
                String ratingValue = doc.select("span.rating_value").text();
                // Country, released, genre, style
                Elements elements2 = doc.select("div.profile > div");
                while(elements2.size() > 0){
                    switch (elements2.first().text()){
                        case "Country:":
                            elements2 = elements2.next();
                            String country = elements2.first().text();
                            break;
                        case "Released:":
                            elements2 = elements2.next();
                            String released = elements2.first().text();
                            break;
                        case "Genre:":
                            elements2 = elements2.next();
                            Elements genres = elements2.first().select("a");
                            for (Element genre : genres) {
                                String genreName = genre.text();
                            }
                            break;
                        case "Style:":
                            elements2 = elements2.next();
                            Elements styles = elements2.first().select("a");
                            for (Element style : styles) {
                                String styleName = style.text();
                            }
                            break;
                    }
                    elements2 = elements2.next();
                }

                // Tracklist
                Elements tracklist = doc.select("span.tracklist_track_title");
                for (Element element : tracklist) {
                    String trackName = element.text();
                }

//                return null;
//            }
//        });



    }

    public void visitPage(Document document) throws InterruptedException, IOException {
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
            DataAccessLayer.initialize();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
