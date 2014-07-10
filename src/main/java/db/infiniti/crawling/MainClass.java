package db.infiniti.crawling;

import java.io.File;
import java.util.ArrayList;

import db.infiniti.config.CrawlingConfig;

public class MainClass extends Thread {
	ArrayList<String> listOfSources;
	static CrawlingConfig crawlingConfig;

	public static void main(String[] args) {

		ArrayList<String> listOfReturnedResults = new ArrayList<String>();

		String queryPoolPath = "querypool/wikiwebsorted";
		// complete list in /media/DATA/pool/webwords/wikiwebsorted
		// not needed if reading from DB
		String openDescFileDirPath = "websources/DT01/";
		// not needed if reading from DB
		// /home/mohammad/uni-work/DJOERD SOURCES/DT01/
		boolean readFromDB = true; // read from openFIleDS or DB

		// DetailedInfoXPathDetectionDS detailedInfoXpathDetector;

		crawlingConfig = new CrawlingConfig();
		crawlingConfig.setScrShotBrowser();
		crawlingConfig.setDetailedPageBrowsers(4);
		
	//	crawlingConfig.setWebTools(new WebTools());
		// to extract pages and the content

		crawlingConfig.setUnPauseCrawl(true);// restart crawl

		crawlingConfig.setExtractTextFromAllVisitedPages(false);
		crawlingConfig.setExtractTextFromSRPages(false);
		
		crawlingConfig.setExtractDataFromDPageS(true);
		crawlingConfig.setOutputDataBase("mydatafactory");
		crawlingConfig.setTableName("dsItemsOutputMohtry");
		// data output table name
		crawlingConfig.setDataModelTable("dsItemsModelXPATH");//"simpledatamodel");//dsItemsModelXPATH");//dsItemsModelXPATH
		// data model table to extract detailed pages

		@SuppressWarnings("unused")
		int totalNumOfWebsites = 0;
		if (!readFromDB) {// read from openFIleDS or DB
			crawlingConfig.setOpenDescDirPath(openDescFileDirPath);
			crawlingConfig.setListOfSourcesFolders();
			totalNumOfWebsites = crawlingConfig.getListOfSourcesFolders()
					.size();
		} else if (readFromDB) {
			crawlingConfig.setAllSitesDescription();
			totalNumOfWebsites = crawlingConfig.getListOfWebsites().size();
		}
		crawlingConfig.setQuerySelectionApproach(crawlingConfig.browsing);
		/*crawlingConfig.setQuerySelectionApproach(crawlingConfig.crawledTextBased);
		crawlingConfig
				.setQuerySelectionApproach(crawlingConfig.mostFrequentWebWords);*/

		crawlingConfig.setQueries(queryPoolPath);

		//FedwebCrawler crawlingThread;
		CrawlerSellenium crawlingThreadTest;
		for (int numberOfCrawledSources = 22; numberOfCrawledSources < 23; numberOfCrawledSources++) {//totalNumOfWebsites
			System.out.println("Number Of Crawled Sources: "
					+ numberOfCrawledSources);

			if (!readFromDB) {// setting for each of the websources
				crawlingConfig.setOpenDescFilePath(numberOfCrawledSources);
				crawlingConfig.setCollectionName();
				crawlingConfig.setCurrentSiteDescription(readFromDB,
						numberOfCrawledSources);
			} else {
				crawlingConfig.setCurrentSiteDescription(readFromDB,
						numberOfCrawledSources);
				crawlingConfig.setCollectionName(crawlingConfig
						.getCurrentSiteDescription().getName());
			}
		//	crawlingConfig.setWebTools(new WebTools());// to extract pages and
														// the content
			System.out.println("Collection name: "
					+ crawlingConfig.getCollectionName());
			makeCrawledDataFolder();
			crawlingConfig
					.setLinkContentSavePath("crawledData" + "/"
							+ crawlingConfig.getCollectionName() + "/"
							+ "crawledData/");
			crawlingConfig
					.setCrawlStatusPath("crawledData" + "/"
							+ crawlingConfig.getCollectionName() + "/"
							+ "crawlstatus/");

			crawlingConfig.setPathToVisitedPagesDoc(crawlingConfig
					.getCrawlStatusPath() + "visited-pages");
			crawlingConfig.setPathToAllDOwnloadedPages(crawlingConfig
					.getCrawlStatusPath() + "all-downloaded-pages");
			crawlingConfig.setPathToSentQueriesDoc(crawlingConfig
					.getCrawlStatusPath() + "sent-queries");
			crawlingConfig.setPathToCoveredCollections(crawlingConfig
					.getCrawlStatusPath() + "covered-collections");
			crawlingConfig.setPathToLastLinkDoc(crawlingConfig
					.getCrawlStatusPath() + "last-link-detailed-page");
			crawlingConfig.setPathToURLCrawlList(crawlingConfig
					.getCrawlStatusPath() + "list-of-links-to-crawl");
			crawlingConfig.setPathToLastSearchResultPage(crawlingConfig
					.getCrawlStatusPath() + "last-result-page");
			crawlingConfig.setPathToVisitedPagesPerQuery(crawlingConfig
					.getCrawlStatusPath() + "visited-pages-per-query");
			crawlingConfig.setPathToNumberOfSearchResults(crawlingConfig
					.getCrawlStatusPath() + "number-search-results");
			crawlingConfig.setPathToNumberOfRepetitions(crawlingConfig
					.getCrawlStatusPath() + "number-repetitions");
			crawlingConfig.setPathToNumberOfSentQueries(crawlingConfig
					.getCrawlStatusPath() + "number-sent-queries");
			// crawlingThread = new FedwebCrawler(crawlingConfig,
			// listOfReturnedResults);
			// crawlingThreadTest = new CrawlerWithHtmlUnit(crawlingConfig,
			// listOfReturnedResults);
			crawlingThreadTest = new CrawlerSellenium(crawlingConfig,
					listOfReturnedResults);

			crawlingThreadTest.run();
			// crawlingThread.run();
			// crawlingThread.start();
			/*
			 * try { crawlingThread.join(); } catch (InterruptedException ie) {
			 * ie.printStackTrace(); }
			 */
			System.out.println("End of Crawling for this collection");
			System.out.println("======================");
		}
		crawlingConfig.stopFXDriver();
	}

	private static void makeCrawledDataFolder() {

		String filePath = "crawledData";

		File file = new File(filePath);
		if (file.exists()) {
		} else if (file.mkdir()) {
			System.out.println("crawledData folder is set.");
		} else {
			System.err.println("crawledData folder is not present.");
		}
		
		file = new File(filePath+"/"+crawlingConfig.getCollectionName());
		if (file.exists()) {
		} else if (file.mkdir()) {
			System.out.println("CollectionName folder is set.");
		} else {
			System.err.println("CollectionName folder could not be set.");
		}

	}

}
