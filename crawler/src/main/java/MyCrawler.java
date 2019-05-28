import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MyCrawler {
    private static final String seed = "https://www.discogs.com/search/?country_exact=Serbia";

    public void visitAlbumPage(String url){

    }

    public void visitPage(Document document){
        Elements elements = document.select("div#search_results > div");                                     // get the direct children of the div with id = "search_results"
        for (Element element : elements){
            String albumUrl = element.select("a.search_result_title").attr("abs:href");   // get anchor with class = "search_result_title"
            visitAlbumPage(albumUrl);
        }
    }

    public String nextPage(Document document){
        Elements elements = document.select("a.pagination_next");
        if (!elements.isEmpty())
            return elements.first().attr("abs:href");
        return "";
    }

    public static void main(String[] args) {
        MyCrawler crawler = new MyCrawler();

        try {
            String url = seed;
            while(!url.equals("")){
                System.out.println("VISITED PAGE: " + url);
                Thread.sleep(300);
                Document doc = Jsoup.connect(url).get();
                crawler.visitPage(doc);
                url = crawler.nextPage(doc);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
