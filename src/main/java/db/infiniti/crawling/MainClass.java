package db.infiniti.crawling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.infiniti.config.CrawlingConfig;

public class MainClass extends Thread {
	ArrayList<String> listOfSources;
	static CrawlingConfig crawlingConfig;

	public static void main(String[] args) {
		String dataModelTable = "";
		String dataOutputTable = "";
		int numberOfBrowsers = 1;
		int numberOfSelectedSourceEngineIDtoCrawl = 0;
		;
		if (args.length > 2) {
			dataModelTable = args[0];
			dataOutputTable = args[1];
			try {
				numberOfSelectedSourceEngineIDtoCrawl = Integer
						.parseInt(args[2]);
			} catch (NumberFormatException eNum) {
				// TODO stop, say to enter a number
			}
			if (args.length > 3) {
				try {
					numberOfBrowsers = Integer.parseInt(args[3]);
				} catch (NumberFormatException eNum) {
					numberOfBrowsers = 1;
				}
			}
		} else {
			dataModelTable = "dsItemsModelXPATH";
			dataOutputTable = "dsItemsOutputMohtry";
			numberOfSelectedSourceEngineIDtoCrawl = 21;// 0;
			numberOfBrowsers = 2;
		}

		ArrayList<String> listOfReturnedResults = new ArrayList<String>();

		String queryPoolPath = "querypool/words-15000-freq";
		String termFreqClueWebPath = "querypool/words-15000-freq";// "querypool/wikiwebsorted";
		// "querypool/words-2000-3000-freq";
		// "querypool/words-4500-5000-freq";
		// "querypool/words-15000-freq";
		// complete list in /media/DATA/pool/webwords/wikiwebsorted
		// not needed if reading from DB
		String openDescFileDirPath = "websources/DT01/";
		// not needed if reading from DB
		// /home/mohammad/uni-work/DJOERD SOURCES/DT01/
		boolean readFromDB = true; // read from openFIleDS or DB

		// DetailedInfoXPathDetectionDS detailedInfoXpathDetector;

		crawlingConfig = new CrawlingConfig();

		crawlingConfig.setTermFreqInClueWeb(termFreqClueWebPath);
		// crawlingConfig.setWebTools(new WebTools());
		// to extract pages and the content

		crawlingConfig.setUnPauseCrawl(false);// restart crawl

		crawlingConfig.setExtractTextFromAllVisitedPages(false);
		crawlingConfig.setExtractTextFromSRPages(false);

		crawlingConfig.setExtractDataFromDPageS(false);
		crawlingConfig.setSaveDSextractedInfoInFile(false);
		crawlingConfig.setOutputDataBase("mydatafactory");
		crawlingConfig.setTableName(dataOutputTable);
		// data output table name
		crawlingConfig.setDataModelTable(dataModelTable);// "simpledatamodel");//dsItemsModelXPATH");//dsItemsModelXPATH
		// data model table to extract detailed pages
		crawlingConfig
				.setQuerySelectionApproach(crawlingConfig.leastFreqFeedbackText);
		// PredefinedlistOfWords - mostFreqFeedbackText - browsing -
		// leastFromLast - leastFreqFeedbackText - combinedLFL_PLW
		crawlingConfig.setQueries(queryPoolPath);
		// crawlingConfig.setInitialQuery(Arrays.asList("brinksma"//,"company")
		// ));
		crawlingConfig.setInitialQuery(Arrays.asList("vitol"));
		crawlingConfig.setIndexed(true);
		crawlingConfig.setHave_words_in_memory(false);

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

		// FedwebCrawler crawlingThread;
		CrawlerSellenium crawlingThreadTest;
		for (int numberOfCrawledSources = numberOfSelectedSourceEngineIDtoCrawl; numberOfCrawledSources < numberOfSelectedSourceEngineIDtoCrawl + 1; numberOfCrawledSources++) {// totalNumOfWebsites
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
				// crawlingConfig.setCollectionName("google.com.brinksma");
			}
			// crawlingConfig.setWebTools(new WebTools());// to extract pages
			// and
			// the content
			System.out.println("Collection name: "
					+ crawlingConfig.getCollectionName());
			crawlingConfig.setScrShotBrowser(
					crawlingConfig.getCollectionName(), "crawledData/"
							+ crawlingConfig.getCollectionName() + "/"
							+ "popupsaved/");
			crawlingConfig.setDetailedPageBrowsers(numberOfBrowsers);

			makeCrawledDataFolder();
			crawlingConfig
					.setLinkContentSavePath("crawledData" + "/"
							+ crawlingConfig.getCollectionName() + "/"
							+ "crawledData/");
			crawlingConfig
					.setCrawlStatusPath("crawledData" + "/"
							+ crawlingConfig.getCollectionName() + "/"
							+ "crawlstatus/");

			crawlingConfig.setCache(
					"crawledData/" + crawlingConfig.getCollectionName() + "/"
							+ "cache/", crawlingConfig.isIndexed());

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

			crawlingConfig.setQueriesSearchResults();
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

		file = new File(filePath + "/" + crawlingConfig.getCollectionName());
		if (file.exists()) {
		} else if (file.mkdir()) {
			System.out.println("CollectionName folder is set.");
		} else {
			System.err.println("CollectionName folder could not be set.");
		}

	}

}
