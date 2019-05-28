import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    public static void _main(String[] args) throws Exception {
        final int MAX_CRAWL_DEPTH = 3;
        final int NUMBER_OF_CRAWLERS = 2;
        final int POLITENESS_DELAY = 1000;
        final int MAX_PAGES_TO_FETCH = 5;
        final String CRAWL_STORAGE = "/data/crawl/root";

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWL_STORAGE);
        config.setPolitenessDelay(POLITENESS_DELAY);
        config.setMaxDepthOfCrawling(MAX_CRAWL_DEPTH);
        config.setMaxPagesToFetch(MAX_PAGES_TO_FETCH);
//        config.setIncludeBinaryContentInCrawling(false);        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc

        // Do you need to set a proxy? If so, you can use:
        config.setProxyHost("proxyserver.example.com");
        config.setProxyPort(8080);

        // If your proxy also needs authentication:
        // config.setProxyUsername(username); config.getProxyPassword(password);

        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("https://www.discogs.com/search/?country_exact=Yugoslavia");

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<_MyCrawler> factory = () -> new _MyCrawler();

        controller.start(factory, NUMBER_OF_CRAWLERS);
    }
}
