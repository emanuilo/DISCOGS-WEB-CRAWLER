import db.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.persistence.FlushModeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyCrawler {
//    private static final String SEED = "https://www.discogs.com/search/?country_exact=Serbia";
    private static final String SEED = "https://www.discogs.com/search/?country_exact=Yugoslavia";
    private static final String DISCOGSCOM = "https://www.discogs.com/";
    private static final int SHORT_TIMEOUT = 500;
    private static final int LONG_TIMEOUT = 1000;
    private static final float NO_RATING_VALUE = 0.0f;
    public static int NUM_OF_404 = 0;
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
//        Thread.sleep(SHORT_TIMEOUT);
        albumLog.info("VISITED ALBUM: " + url);
        try{
            Document doc = Jsoup.connect(url).get();

            DataAccessLayer.transactional(
                    session -> {
                        session.setFlushMode(FlushModeType.AUTO);
                        // Album name
                        String albumName = doc.select("div.profile > h1 > span").next().text();
                        String ratingValue = doc.select("span.rating_value").text();
                        // Country, released, genre, style
                        String country = "";
                        String released = "";
                        Set<String> genreList = new HashSet<>();
                        Set<String> styleList = new HashSet<>();
                        Elements elements2 = doc.select("div.profile > div");
                        while (elements2.size() > 0) {
                            switch (elements2.first().text()) {
                                case "Country:":
                                    elements2 = elements2.next();
                                    country = elements2.first().text();
                                    break;
                                case "Released:":
                                case "Year:":
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
                        if (!ratingValue.equals("--") && !ratingValue.equals(""))
                            newAlbum.setRating(Float.parseFloat(ratingValue));
                        else
                            newAlbum.setRating(NO_RATING_VALUE);
                        newAlbum.setCountry(country);
                        newAlbum.setVersions(versions);
                        if (!released.equals(""))
                            newAlbum.setReleased(Integer.parseInt(released));
                        session.save(newAlbum);

                        // Artists on the album
                        Elements elements = doc.select("div.profile > h1 > span > span[title]");
                        List<Artist> artists = new ArrayList<>();
                        for (Element element : elements) {
                            String artistName = element.attr("title");
                            // getting already present artists from the database
                            Query query = session.createQuery("from Artist where name=:name");
                            query.setParameter("name", artistName);
                            List result = query.list();
                            Artist artist = null;
                            if (result.size() == 1) {
                                artist = (Artist) result.get(0);
                            } else if (result.size() == 0) {
                                String artistPageLink = element.select("a").first().attr("href");
                                artist = getNewArtist(artistPageLink, artistName);
                            }
                            artists.add(artist);
                            session.save(artist);
                            session.persist(new Album_Artist(new Album_Artist.Album_Artist_Id(newAlbum, artist)));
                        }


                        // Genres
                        for (String genreName : genreList) {
                            Query query = session.createQuery("from Genre where name=:name");
                            query.setParameter("name", genreName);
                            List result = query.list();
                            Genre genre;
                            if (result.size() == 1)
                                genre = (Genre) result.get(0);
                            else //size is 0
                                genre = new Genre(genreName);
                            Album_Genre album_genre = new Album_Genre(new Album_Genre.Album_Genre_Id(genre, newAlbum));
                            session.save(genre);
                            session.save(album_genre);
                        }

                        // Styles
                        for (String styleName : styleList) {
                            Query query = session.createQuery("from Style where name=:name");
                            query.setParameter("name", styleName);
                            List result = query.list();
                            Style style;
                            if (result.size() == 1)
                                style = (Style) result.get(0);
                            else
                                style = new Style(styleName);
                            Album_Style album_style = new Album_Style(new Album_Style.Album_Style_Id(style, newAlbum));
                            session.save(style);
                            session.save(album_style);
                        }

                        // Tracklist
                        Elements tracklist = doc.select("tr.tracklist_track");
                        List<Track> tracks = new ArrayList<>();
                        for (Element element : tracklist) {
                            String trackName = element.select("span.tracklist_track_title").text();
                            if (trackName.length() > 255) continue;
                            String durationString = element.select("td.tracklist_track_duration > span").text();
                            Track newTrack = null;
                            if (!durationString.equals("")) {
                                if(durationString.startsWith("(") && durationString.endsWith(")"))
                                    durationString = durationString.substring(1, durationString.length() - 1);
                                String[] split = durationString.split(":");
                                int duration = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
                                newTrack = new Track(trackName, duration, newAlbum);
                                tracks.add(newTrack);
                            } else {
                                newTrack = new Track(trackName, newAlbum);
                                tracks.add(newTrack);
                            }
                            session.save(newTrack);

                            Elements select = element.select("span.tracklist_extra_artist_span");
                            Set<String> lyricsBySet = new HashSet<>();
                            Set<String> arrangedBySet = new HashSet<>();
                            Set<String> musicBySet = new HashSet<>();
                            for (Element element2 : select) {
                                String text = element2.text();
                                if (text.contains("Lyrics By")) {
                                    for (Element element3 : element2.select("a")){
                                        String lyricsByArtistName = element3.text();
                                        if (lyricsBySet.add(lyricsByArtistName)){
                                            String lyricsByArtistUrl = element3.attr("href");
                                            Artist lyricsByArtist = getArtist(lyricsByArtistName, lyricsByArtistUrl, session);
                                            LyricsBy lyricsBy = new LyricsBy(new LyricsBy.LyricsBy_Id(newTrack, lyricsByArtist));
                                            session.save(lyricsBy);
                                        }
                                    }
                                }
                                if (text.contains("Arranged By")) {
                                    for (Element element3 : element2.select("a")){
                                        String arrangedByArtistName = element3.text();
                                        if (arrangedBySet.add(arrangedByArtistName)){
                                            String arrangedByArtistUrl = element3.attr("href");
                                            Artist arrangedByArtist = getArtist(arrangedByArtistName, arrangedByArtistUrl, session);
                                            ArrangedBy arrangedBy = new ArrangedBy(new ArrangedBy.ArrangedBy_Id(newTrack, arrangedByArtist));
                                            session.save(arrangedBy);
                                        }
                                    }
                                }
                                if (text.contains("Music By")) {
                                    for (Element element3 : element2.select("a")){
                                        String musicByArtistName = element3.text();
                                        if (musicBySet.add(musicByArtistName)){
                                            String musicByArtistUrl = element3.attr("href");
                                            Artist mysicByArtist = getArtist(musicByArtistName, musicByArtistUrl, session);
                                            MusicBy musicBy = new MusicBy(new MusicBy.MusicBy_Id(newTrack, mysicByArtist));
                                            session.save(musicBy);
                                        }
                                    }
                                }
                            }
                        }

                        // Credits
                        Elements creditsElements = doc.select("div.credits a");
                        Set<String> names = new HashSet<>();
                        for (Element creditsElement : creditsElements){
                            String creditsArtistName = creditsElement.text();
                            if(names.add(creditsArtistName)){
                                String creditsArtistUrl = creditsElement.attr("href");
                                Artist creditsArtist = getArtist(creditsArtistName, creditsArtistUrl, session);
                                Credits credits = new Credits(new Credits.Credits_Id(newAlbum, creditsArtist));
                                session.save(credits);
                            }
                        }

                        // Vocals
                        creditsElements = doc.select("div.credits li");
                        names = new HashSet<>();
                        for (Element creditsElement : creditsElements){
                            Element roleElement = creditsElement.selectFirst("span.role");
                            if (roleElement != null){
                                String roleString = roleElement.text();
                                if (roleString.contains("Vocals") || roleString.contains("vocals")){
                                    Elements vocalsElements = creditsElement.select("a");
                                    for (Element vocalsElement : vocalsElements){
                                        String vocalsArtistName = vocalsElement.text();
                                        if (names.add(vocalsArtistName)){
                                            String vocalsArtistUrl = vocalsElement.attr("href");
                                            Artist vocalsArtist = getArtist(vocalsArtistName, vocalsArtistUrl, session);
                                            Vocals vocals = new Vocals(new Vocals.Vocals_Id(newAlbum, vocalsArtist));
                                            session.save(vocals);
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }
            );
        } catch (HttpStatusException e) {
            System.out.println("===========================ERROR 404!================================");
            NUM_OF_404++;
        }
    }

    public Artist getArtist(String artistName, String artistPageLink, Session session) {
        Query query = session.createQuery("from Artist where name=:name");
        query.setParameter("name", artistName);
        List result = query.list();
        Artist artist = null;
        if (result.size() == 1) {
            artist = (Artist) result.get(0);
        } else if (result.size() == 0) {
            artist = getNewArtist(artistPageLink, artistName);
            session.save(artist);
        }

        return artist;
    }

    public Artist getNewArtist(String url, String artistName) {
        String website = "";
        try{
//            Thread.sleep(SHORT_TIMEOUT);
            artistLog.info("VISITED ARTIST: " + url);
            Document doc = Jsoup.connect(DISCOGSCOM + url).get();

            Elements elements = doc.select("div.profile > div");
            while(elements.size() > 0){
                if (elements.first().text().equals("Sites:")) {
                    elements = elements.next();
                    website = elements.first().selectFirst("div > a").attr("href");
                    if (website.length() > 255) website = "";
                    break;
                }
                elements = elements.next();
            }
        } catch (HttpStatusException exception){
            System.out.println("Placeholder page!");
        } catch (IOException e) {
            e.printStackTrace();
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

    public String continueVisitPage(Document document) throws IOException, InterruptedException {
        Elements elements = document.select("div#search_results > div");                                 // get the direct children of the div with id = "search_results"
        boolean found = false;
        for (Element element : elements){
            String albumUrl = element.select("a.search_result_title").attr("abs:href");       // get anchor with class = "search_result_title"
            if (found || albumUrl.equals(CONTINUE_ALBUM_URL)) {
                visitAlbumPage(albumUrl);
                found = true;
            }
        }
        return nextPage(document);
    }


    public static void setupLogger(Logger logger) throws IOException {
        FileHandler fileHandler = new FileHandler("logs/pages/pageLog" + System.currentTimeMillis() + ".log");
        logger.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.info("Logger set up!");
    }

    public static final String CONTINUE_PAGE_URL = "https://www.discogs.com/search/?page=169&country_exact=Serbia";
    public static final String CONTINUE_ALBUM_URL = "https://www.discogs.com/Various-Milan-B-Popovi%C4%87-Hroni%C4%8Dno-Neumorni/master/834449";

    public static void main(String[] args) {
        try {
            DataAccessLayer.initialize();
            Logger pageLog = Logger.getLogger("Page Logger");
            setupLogger(pageLog);
            MyCrawler crawler = new MyCrawler(pageLog);
            String url = SEED; // from the scratch
//            String url = crawler.continueVisitPage(Jsoup.connect(CONTINUE_PAGE_URL).get()); // continue from a specific album and page
            while(!url.equals("")){
                pageLog.info("VISITED PAGE: " + url);
//                Thread.sleep(LONG_TIMEOUT);
                Document doc = Jsoup.connect(url).get();
                crawler.visitPage(doc);
                url = crawler.nextPage(doc);
            }
            System.out.println(NUM_OF_404);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
