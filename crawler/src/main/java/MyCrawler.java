import db.*;
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
    private static final String SEED = "https://www.discogs.com/search/?country_exact=Serbia";
    private static final String DISCOGSCOM = "https://www.discogs.com/";
    private static final int SHORT_TIMEOUT = 500;
    private static final int LONG_TIMEOUT = 1000;

    private Logger albumLog;
    private Logger artistLog;

    public MyCrawler(Logger logger) throws IOException {
//        this.log = logger;
        albumLog = Logger.getLogger("Album Logger");
        FileHandler fileHandler = new FileHandler("logs/albums/albumLog" + System.currentTimeMillis() + ".log");
        albumLog.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
        albumLog.info("Logger set up!");

        artistLog = Logger.getLogger("Artist Logger");
        FileHandler fileHandler2 = new FileHandler("logs/artists/artistLog" + System.currentTimeMillis() + ".log");
        artistLog.addHandler(fileHandler2);
        fileHandler.setFormatter(new SimpleFormatter());
        artistLog.info("Logger set up!");
    }

    public void visitAlbumPage(String url) throws InterruptedException, IOException {
        Thread.sleep(SHORT_TIMEOUT);
        albumLog.info("VISITED ALBUM: " + url);
        Document doc = Jsoup.connect(url).get();

        DataAccessLayer.transactional(new TransactionalCode<Void>() {

            @Override
            public Void run(Session session) {
                try {
                    // Artists on the album
                    Elements elements = doc.select("div.profile > h1 > span > span[title]");
                    List<Artist> artists = new ArrayList<>();
                    for (Element element : elements) {
                        String artistName = element.attr("title");
                        // getting already present artists from the database
                        Query query = session.createQuery("from Artist where name=:name");
                        query.setParameter("name", artistName);
                        List result = query.list();
                        if (result.size() == 1){
                            artists.add((Artist) result.get(0));
                        }
                        else if (result.size() == 0){
                            String artistPageLink = element.select("a").first().attr("href");
                            try {
                                artists.add(getNewArtist(artistPageLink, artistName));
                            } catch (InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            throw new Exception("More than one artist with the same name in the database!");
                        }
                    }


                    // Album name
                    String albumName = doc.select("div.profile > h1 > span").next().text();
                    String ratingValue = doc.select("span.rating_value").text();
                    // Country, released, genre, style
                    String country = "";
                    String released = "";
                    List<String> genreList = new ArrayList<>();
                    List<String> styleList = new ArrayList<>();
                    Elements elements2 = doc.select("div.profile > div");
                    while(elements2.size() > 0){
                        switch (elements2.first().text()){
                            case "Country:":
                                elements2 = elements2.next();
                                country = elements2.first().text();
                                break;
                            case "Released:": case "Year":
                                elements2 = elements2.next();
                                String date = elements2.first().text();
                                String[] s = date.split(" ");
                                released = s[s.length - 1];
                                break;
                            case "Genre:":
                                elements2 = elements2.next();
                                Elements genres = elements2.first().select("a");
                                for (Element genre : genres) {
                                    String genreName = genre.text();
                                    genreList.add(genreName);
                                }
                                break;
                            case "Style:":
                                elements2 = elements2.next();
                                Elements styles = elements2.first().select("a");
                                for (Element style : styles) {
                                    String styleName = style.text();
                                    styleList.add(styleName);
                                }
                                break;
                        }
                        elements2 = elements2.next();
                    }


                    // Versions
                    Elements elements_versions = doc.select("table#versions > tbody > tr");
                    int versions = Math.max(elements_versions.size() - 1, 0);

                    Album newAlbum = new Album();
                    newAlbum.setName(albumName);
                    newAlbum.setRating(Float.parseFloat(ratingValue));
                    newAlbum.setCountry(country);
                    newAlbum.setVersions(versions);
                    newAlbum.setReleased(Integer.parseInt(released));


                    // Genres
                    for (String genreName : genreList){
                        Query query = session.createQuery("from Genre where name=:name");
                        query.setParameter("name", genreName);
                        List result = query.list();
                        if (result.size() == 1){
                            Genre genre = (Genre) result.get(0);
                            //todo
                        }
                        else if (result.size() == 0){
                            Genre genre = new Genre(genreName);
                            // todo
                        }

                    }



                    // Tracklist
                    Elements tracklist = doc.select("tr.tracklist_track");
                    List<Track> tracks = new ArrayList<>();
                    for (Element element : tracklist) {
                        String trackName = element.select("span.tracklist_track_title").text();
                        String durationString = element.select("td.tracklist_track_duration > span").text();
                        String[] split = durationString.split(":");
                        int duration = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
                        tracks.add(new Track(trackName, duration, newAlbum));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return null;
            }
        });
    }

    public Artist getNewArtist(String url, String artistName) throws InterruptedException, IOException {
        Thread.sleep(SHORT_TIMEOUT);
        artistLog.info("VISITED ARTIST: " + url);
        Document doc = Jsoup.connect(DISCOGSCOM + url).get();

        Elements elements = doc.select("div.profile > div");
        String website = "";
        while(elements.size() > 0){
            if (elements.first().text().equals("Sites:")) {
                elements = elements.next();
                String href = elements.first().selectFirst("div > a").attr("href");
                break;
            }
            elements = elements.next();
        }

        return new Artist(artistName, website);
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
            String url = SEED;
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
