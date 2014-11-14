package db.infiniti.crawling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.ErrorHandler.UnknownServerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLAnchorElement;

import ExcelWritePack.ExcelWriter;

/*needs htmlUnit 
 * import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
 import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
 import com.gargoylesoftware.htmlunit.html.HtmlElement;
 import com.gargoylesoftware.htmlunit.html.HtmlPage;

 import db.infiniti.config.DetailedInfoXPathDetectionDS;*/

import db.infiniti.config.CrawledLinkDS;
import db.infiniti.config.CrawlingConfig;
import db.infiniti.config.CrawlingReportDS;
import db.infiniti.config.QueryResStatistics;
import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.surf.Browser;
import db.infiniti.xpath.ResultLinkXpathFinder;

/**
 * @author mohammadreza
 * 
 */

public class CrawlerSellenium {

	ArrayList<String> listOfAllDownloadedReturnedResults = new ArrayList<String>();
	ArrayList<String> listOfAllReturnedResults;
	ArrayList<String> listOfReturnedResultsPerQuery;

	String url;
	String searchResultlink;
	String sourceURL;
	ArrayList<String> queries;

	boolean firstTryForLinkDetection = true;
	boolean firstTryForXpath = true;
	boolean firstTimeCrawl = true;

	CrawlingReportDS crawlReport;
	CrawlingConfig crawlingConfig;
	WebsiteDS siteDes;
	// CrawledLinkDS crawledLinkDS;

	// HtmlPage emptyPage;

	List<CrawledLinkDS> last10CrawledLinksDS = new ArrayList<CrawledLinkDS>();
	// TODO //last 10 crawled web pages

	// DetailedInfoXPathDetectionDS detailedInfoXpathDetector;
	String saveLinkPath;
	boolean resultsXPathIsset = false;
	String item_xp;
	String link_xp;
	String title_xp;
	String desc_xp;
	String thumb_xp;
	ArrayList<String> linkDetectionxPath = new ArrayList<String>();
	String nextResultPagexPath = null;
	ResultLinkXpathFinder resultLinkXpathFinder = new ResultLinkXpathFinder();

	Browser sRPagesbrowser;
	// Browser detailedPagesbrowser;

	String lastDetailedPageBrowsed = "";

	boolean firstRunForWebsite = true;
	boolean continueCrawl = true;
	Document doc = null;
	Element rootElement = null;
	int repeatedLinks = 0;
	int totalreturnedResForQuery = 0;
	List<Integer> totalreturnedResForQueryList = new ArrayList<Integer>(1);
	int totalreturnedRes = 0;
	List<Integer> totalreturnedResList = new ArrayList<Integer>();
	boolean stopCrawlForQuery = false;
	boolean dataModelLoaded = false;

	// to extract data from detailedpages

	public CrawlerSellenium(CrawlingConfig crawlingConfig,
			ArrayList<String> listOfReturnedResults) {
		this.crawlingConfig = crawlingConfig;
		this.listOfAllReturnedResults = listOfReturnedResults;
		crawlReport = new CrawlingReportDS();
		queries = crawlingConfig.getQueries();
		saveLinkPath = crawlingConfig.getLinkContentSavePath();
		siteDes = crawlingConfig.getCurrentSiteDescription();
		sourceURL = siteDes.getTemplate();
		item_xp = siteDes.getItemXPath();
		link_xp = siteDes.getLinkXPath();
		title_xp = siteDes.getTitleXPath();
		desc_xp = siteDes.getDescXPath();
		thumb_xp = siteDes.getThumbNXPath();
		listOfReturnedResultsPerQuery = new ArrayList<String>();
		totalreturnedResForQueryList.add(0, totalreturnedResForQuery);
		totalreturnedResList.add(0, totalreturnedRes);
	}

	public void run() {

		int posedQueiesIndex = 0;
		// TODO checkForAcceptingStopWords();
		crawlingConfig.resetForNextCollection();

		int noOfReturnedResultsPages;
		listOfAllDownloadedReturnedResults.addAll(crawlingConfig
				.readLinesFromFile(crawlingConfig.pathToAllDOwnloadedPages));

		if (crawlingConfig.unPauseCrawl) {
			listOfAllReturnedResults.addAll(crawlingConfig
					.readLinesFromFile(crawlingConfig.pathToVisitedPagesDoc));
			listOfReturnedResultsPerQuery
					.addAll(crawlingConfig
							.readLinesFromFile(crawlingConfig.pathToVisitedPagesPerQuery));
			url = crawlingConfig
					.readLastPageFromFile(crawlingConfig.pathToLastSearchResultPage);
			// crawlingConfig.setQueryIndexFromFile();
			lastDetailedPageBrowsed = crawlingConfig
					.readLastPageFromFile(crawlingConfig.pathToLastLinkDoc);
			// firstRunForWebsite = false;
			noOfReturnedResultsPages = crawlingConfig
					.readFirstNumberFromFile(crawlingConfig.pathToNumberOfSearchResults);
			crawlReport
					.setNumRepeatedLinksInGeneral(crawlingConfig
							.readFirstNumberFromFile(crawlingConfig.pathToNumberOfRepetitions));
			crawlReport
					.setCountCrawlingQueries(crawlingConfig
							.readFirstNumberFromFile(crawlingConfig.pathToNumberOfSentQueries));

		} else {

			url = crawlingConfig.setNextQuery();
			posedQueiesIndex++;
			crawlingConfig.saveCrawlStatusQuery();
			noOfReturnedResultsPages = 0;
		}
		if (url.equals("")) {
			url = crawlingConfig.setNextQuery();
		}

		// List<HtmlAnchor> nextPageResultLink = null;

		sRPagesbrowser = crawlingConfig.getScrShotBrowser();
		// detailedPagesbrowser = crawlingConfig.getDetailedPageBrowser();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;

		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("crawlResults");
			doc.appendChild(rootElement);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		int numberOfDocInReturnedResults = 0;
		while (continueCrawl) {
			noOfReturnedResultsPages++;
			crawlingConfig.setCurrentSRPageURL(url);
			System.out.println("Link of search result page: " + url);

			String pageSource = null;
			if (crawlingConfig.isExtractTextFromSRPages()) {
				pageSource = sRPagesbrowser.loadAndGetPageSource(url);
			} else if (!url.equalsIgnoreCase("javascript clicked.")) {
				// it is already set and clicked. no need to load
				// sRPagesbrowser.loadPage(url);
				sRPagesbrowser.loadPage(url);
				// two time loading just for vanas.eu
			}
			// sRPagesbrowser.loadPage(url);
			searchResultlink = url;
			// sRPagesbrowser.pressSaveKey();

			crawlReport.incNoCrawlingQueries();
			crawlingConfig.saveCrawlStatus(
					crawlReport.getCountCrawlingQueries(),
					crawlingConfig.pathToNumberOfSentQueries);
			if (crawlingConfig.isExtractTextFromSRPages()) {
				saveRetunredResultPage(pageSource, noOfReturnedResultsPages);
			}
			crawlingConfig.setSearchResultPageNumber(noOfReturnedResultsPages);

			if (crawlingConfig.getQuerySelectionApproach() == crawlingConfig.mostFreqFeedbackText) {
				// the search result page content also included
				crawlingConfig.updateQueryList(sRPagesbrowser.getPageText());
			}

			// crawlingConfig.updateQueryList(sRPagesbrowser.getPageText());

			List<WebElement> elementList = extractSearchResutlsFragments();

			final List<CrawledLinkDS> listOfLinksDetailedPages = extractLinksFromSearchResultsFragments(
					elementList, noOfReturnedResultsPages);
			// add results to return list
			// extract details
			if (crawlingConfig.unPauseCrawl) {
				// to reach the last link which had been visited last time.
				if (listOfLinksDetailedPages
						.contains(this.lastDetailedPageBrowsed)) {
					// (if the last page is included in the last result page.
					// the search engine might change it based on browser
					for (CrawledLinkDS tempLink : listOfLinksDetailedPages) {
						if (!this.lastDetailedPageBrowsed.equals(tempLink)) {
							listOfLinksDetailedPages.remove(tempLink);
						} else {
							listOfLinksDetailedPages.remove(tempLink);
							break;
						}
					}
				}
			}

			for (CrawledLinkDS crawledLinkDS : listOfLinksDetailedPages) {
				increaseReturnedResForQueryNumber();// totalreturnedResForQuery
				String resultLink = crawledLinkDS.getLink();
				crawledLinkDS
						.setNumberOfDocInReturnedResults(numberOfDocInReturnedResults);
				numberOfDocInReturnedResults++;
				// "http://www.lexology.com/library/detail.aspx?g=45038f45-8d2a-4c5b-a4bd-fcb399a59db4";//
				if (!listOfAllReturnedResults.contains(resultLink)) {
					// && crawlingConfig.cache.existsAlreadyInCache(resultLink)
					/*
					 * crawlReport.incNoCrawlingQueries();
					 * crawlingConfig.saveCrawlStatus(
					 * crawlReport.getCountCrawlingQueries(),
					 * crawlingConfig.pathToNumberOfSentQueries);
					 */
					crawledLinkDS.setRepeated("No");
					listOfAllReturnedResults.add(resultLink);

					increaseReturnedResr();// totalreturnedRes

					if (!crawlingConfig.FreeBrowserExists()) {
						crawlingConfig.waitTillOneIsFree();
					}

					Thread seperatedBrowser = new Thread(new Runnable() {
						public void run() {
							Browser detailedPagesbrowser = null;
							String resultLink = Thread.currentThread()
									.getName();
							System.out.println("Started Working on link: "
									+ resultLink + " ...");
							CrawledLinkDS crawledLinkDS = getTheRelatedDS(
									listOfLinksDetailedPages, resultLink);
							crawledLinkDS.setSearchResultlink(searchResultlink);
							detailedPagesbrowser = crawlingConfig
									.getFreeBrowser();
							if (crawlingConfig.extractDataFromDPageS == true) {
								detailedPagesbrowser.loadPage(resultLink);
								extractFromDetailedPages(
										crawlingConfig.getOutputDataBase(),
										detailedPagesbrowser);
							} else if (!crawlingConfig.cache
									.existsAlreadyInCacheOrIndex(resultLink, crawlingConfig.isIndexed())) {
								detailedPagesbrowser.loadPage(resultLink);
							}
							if (crawlingConfig.feedbackBasedApproach == true) {
								String pageContent = "";
								String pageHTMLContent = "";
								if (crawlingConfig.cache
										.existsAlreadyInCacheOrIndex(resultLink, crawlingConfig.isIndexed())) {
									pageContent = crawlingConfig.cache
											.getPageHTMLContentFromCacheOrIndex(resultLink, crawlingConfig.isIndexed);
									/*
									 * pageHTMLContent = crawlingConfig.cache
									 * .getPageHTMLContentFromCache(resultLink);
									 */
								} else {
									pageHTMLContent = detailedPagesbrowser
											.getPageSource(resultLink);
									pageContent = detailedPagesbrowser.getPageText();//.getText();
									crawlingConfig.cache.saveInCache(
											resultLink, pageContent,
											pageHTMLContent);
								}
								setTextHtmlOfLink(pageHTMLContent,
										crawledLinkDS);// it
								crawledLinkDS.setLinkTextContent(pageContent);
								crawlingConfig.updateQueryList(crawledLinkDS
										.getLinkTextContent());
								crawlingConfig.fbBasedQueryGenerator
										.prepareQuerySelection(
												crawledLinkDS
														.getNumberOfDocInReturnedResults(),
												crawledLinkDS
														.getLinkTextContent());
							}

							if (!listOfReturnedResultsPerQuery
									.contains(resultLink)) {
								listOfReturnedResultsPerQuery.add(resultLink);
							} else {
								crawlReport.incNoRepeatedLinks();
							}
							addToXmlDocumentItem(crawledLinkDS, doc);

							if (crawlingConfig.SaveDSextractedInfoInFile == true) {
								saveExtractedInfoOfLink(crawledLinkDS,
										totalreturnedRes, resultLink);
							}

							crawlingConfig.saveCrawlStatus(crawledLinkDS
									.getLink());
							crawlingConfig.freeBrowser(detailedPagesbrowser);
						}

						private CrawledLinkDS getTheRelatedDS(
								List<CrawledLinkDS> listOfLinksDetailedPages,
								String resultLink) {
							// CrawledLinkDS crawledLinkDS;
							for (CrawledLinkDS crawledLink : listOfLinksDetailedPages) {
								if (crawledLink.getLink().equals(resultLink)) {
									return crawledLink;
								}
							}
							return null;
						}

					}, resultLink);
					seperatedBrowser.start();
				} else if (listOfAllReturnedResults.contains(resultLink)) {
					repeatedLinks++;// for one query
					crawlReport.incNumRepeatedLinksInGeneral();
					crawledLinkDS.setRepeated("Yes");
					// in total
				}

				// if ((listOfAllReturnedResults.size()) >= 4000) {//total
				// 4900 // stop condition
				if ((totalreturnedResForQueryList.get(0)) >= 99) {
					// number of results for one query
					// continueCrawl = false; stopCrawlConditionIsMet = true;
					stopCrawlForQuery = true;
					break;// check. if not work
				}
			}

			crawlingConfig.waitTillAllBrowsersAreFree();
			if (posedQueiesIndex > 200) {
				continueCrawl = false;
				stopCrawlForQuery = true;
			}
			if (totalreturnedRes >= 40000) {
				// number (listOfAllReturnedResults.size())
				// results for one query
				continueCrawl = false;
				stopCrawlForQuery = true;// break;
			}
			firstRunForWebsite = false;
			if (this.nextResultPagexPath == null) {
				if (siteDes.getNext_page_xp() != null) {
					this.nextResultPagexPath = siteDes.getNext_page_xp();
				} else {
					detectNextButtonXpath();
				}
			}

			if (stopCrawlForQuery == true) {
				crawlReport.addQueryNumberofItsResults(crawlingConfig.query,
						(crawlingConfig.getQueryIndex() - 1),
						listOfAllReturnedResults.size(),
						crawlReport.getNumRepeatedLinks(), repeatedLinks,
						totalreturnedResForQuery);
				printQueriesResults("crawledData/"
						+ crawlingConfig.getCollectionName() + "/"
						+ (crawlingConfig.getQueryIndex() - 1) + "-results.xls");

				crawlReport.setNumRepeatedLinks(0);
				totalreturnedResForQuery = 0;
				totalreturnedResForQueryList.set(0, totalreturnedResForQuery);
				repeatedLinks = 0;

				url = crawlingConfig.setNextQuery();
				crawlingConfig.saveCrawlStatusQuery();
				posedQueiesIndex++;

				if (url == null) { // there is no next query or next result
									// page for query
					continueCrawl = false;
				}
				numberOfDocInReturnedResults = 0;
			} else {
				url = null;
				// if no next link, then, next query
				url = setURLNextResultPage();
				if (url != null) {
					if (!url.equalsIgnoreCase("javascript clicked.")) {
						url = getLinkedURL(sourceURL, url);
						if (url != null) {
							System.out.println("Next ResultPage");
						}
					}
				} else {
					crawlReport.addQueryNumberofItsResults(
							// queries.get(crawlingConfig.getQueryIndex() - 1),
							crawlingConfig.query,
							(crawlingConfig.getQueryIndex() - 1),
							listOfAllReturnedResults.size(),
							crawlReport.getNumRepeatedLinks(), repeatedLinks,
							totalreturnedResForQuery);

					printQueriesResults("crawledData/"
							+ crawlingConfig.getCollectionName() + "/"
							+ (crawlingConfig.getQueryIndex() - 1)
							+ "-results.xls");

					crawlReport.setNumRepeatedLinks(0);
					totalreturnedResForQuery = 0;
					totalreturnedResForQueryList.set(0,
							totalreturnedResForQuery);
					repeatedLinks = 0;

					if (this.nextResultPagexPath == null) {
						// for the web sites without any next button
						if (posedQueiesIndex > 300) {
							continueCrawl = false;
						}
					}
					url = crawlingConfig.setNextQuery();
					crawlingConfig.saveCrawlStatusQuery();
					posedQueiesIndex++;

					if (url == null) { // there is no next query or next result
										// page for query
						continueCrawl = false;
					}
				}
			}
		}

		crawlingConfig.printQueryStatistics();
		crawlingConfig.saveCrawlStatusCollectionName();
		saveCrawledResultInXML(doc);
		printQueriesResults("crawledData/" + crawlingConfig.getCollectionName()
				+ "/results.xls");

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		System.out.println("SourceUrl: " + this.sourceURL);
		System.out.println("xPath " + this.item_xp);
		stopThread();
	}

	@SuppressWarnings("unused")
	private int getSize(ArrayList<String> listOfAllReturnedResults) {
		int size = 0;
		synchronized (listOfAllReturnedResults) {
			size = listOfAllReturnedResults.size();
		}
		return size;
	}

	private void increaseReturnedResForQueryNumber() {
		synchronized (totalreturnedResForQueryList) {
			totalreturnedResForQuery = totalreturnedResForQuery + 1;
			totalreturnedResForQueryList.set(0, totalreturnedResForQuery);
		}
	}

	private void increaseReturnedResr() {
		synchronized (totalreturnedResList) {
			totalreturnedRes = totalreturnedRes + 1;
			totalreturnedResList.set(0, totalreturnedRes);
		}
	}

	/*
	 * private boolean VisitedBefore(String resultLink) { synchronized (this) {
	 * if (!listOfAllReturnedResults.contains(resultLink)) {
	 * listOfAllReturnedResults.add(resultLink); return false; } else { return
	 * true; } }saveRetunredResultPage }
	 */

	/*
	 * private boolean FreeBrowserExists() { // TODO Auto-generated method stub
	 * return false; }
	 */

	@SuppressWarnings("unused")
	private void extractFromDetailedPages(String database,
			Browser detailedPagesbrowser) {

		if (crawlingConfig.extractDataFromDPageS == true) {
			if (dataModelLoaded == false) {
				crawlingConfig.setDetailedPageDS(database);// "Games" //DataBase
															// // not // table
				// name
				dataModelLoaded = true;
			}
			synchronized (this) {
				crawlingConfig.detailedPageDS.extractInfo(detailedPagesbrowser);
				crawlingConfig.detailedPageDS.insertGameData(crawlingConfig
						.getTableName());
			}
		}

	}

	private void addToXmlDocumentItem(CrawledLinkDS crawledLinkDS, Document doc) {
		Node lastSRNode = rootElement.getLastChild();
		Element resultElem = doc.createElement("Result");
		if (lastSRNode != null) {
			lastSRNode.appendChild(resultElem);
			// ressaveRetunredResultPageultElem.appendChild(resultElem);
		}
		Element aElem = doc.createElement("a");
		resultElem.appendChild(aElem);
		aElem.setAttribute("href", crawledLinkDS.getLink());
		aElem.setAttribute("repeated", crawledLinkDS.getRepeated());

		Element titleElem = doc.createElement("title");
		resultElem.appendChild(titleElem);
		titleElem.appendChild(doc.createTextNode(crawledLinkDS.getTitle()));

		Element textElem = doc.createElement("text");
		resultElem.appendChild(textElem);
		textElem.appendChild(doc.createTextNode(crawledLinkDS
				.getLinkTextContent()));

		Element htmlElem = doc.createElement("htmlcontent");
		resultElem.appendChild(htmlElem);
		htmlElem.appendChild(doc.createTextNode(crawledLinkDS
				.getLinkHtmlContent()));

		Element descElem = doc.createElement("description");
		resultElem.appendChild(descElem);
		descElem.appendChild(doc.createTextNode(crawledLinkDS.getDescription()));

		Element thumbElem = doc.createElement("thumbnail");
		resultElem.appendChild(thumbElem);
		thumbElem.appendChild(doc.createTextNode(crawledLinkDS.getThumbLink()));

	}

	private List<CrawledLinkDS> extractLinksFromSearchResultsFragments(
			List<WebElement> elementList, int noOfReturnedResultsPages) {

		List<CrawledLinkDS> returnedListOFLinks = new ArrayList<CrawledLinkDS>();

		Element srElem = doc.createElement("SearchResultPage");
		rootElement.appendChild(srElem);
		srElem.setAttribute("id", noOfReturnedResultsPages + "");
		if (!elementList.isEmpty()) {
			// int idInXmalAttribute = 0;
			for (WebElement HE : elementList) {
				if (continueCrawl != false) {
					// idInXmalAttribute++;
					CrawledLinkDS crawledLinkDS = new CrawledLinkDS();
					if (!link_xp.equals("?") && !link_xp.equals("")) {

						if (link_xp.contains("\\")) {
							link_xp = link_xp.replace("\\", "");
						}
						if (link_xp.startsWith("//")) {
							link_xp = link_xp.replace("//", "/");
						}
						String resultLink = returnLinkInItem(HE, link_xp);
						resultLink = getLinkedURL(this.sourceURL, resultLink);
						// complete the incomplete links

						// System.out.println("Extracted link: " + resultLink);
						crawledLinkDS.setLink(resultLink);

						if (title_xp != null && !title_xp.equals("")
								&& !title_xp.equals("?")) {
							String title = findTitle(HE, title_xp);
							crawledLinkDS.setTitle(title);
						} else {
							crawledLinkDS.setTitle("No title_xpath.");
						}

						if (desc_xp != null && !desc_xp.equals("")
								&& !desc_xp.equals("?")) {
							String desc = findDesc(HE, crawledLinkDS, desc_xp);
							crawledLinkDS.setDescription(desc);

						} else {
							crawledLinkDS
									.setDescription("No description xpath.");
						}

						// what to return for thumbXP
						// TODO how to deal with Thumbnail

						if (!thumb_xp.equals("") && !thumb_xp.equals("")
								&& !thumb_xp.equals("?")) {
							crawledLinkDS.setThumbLink(this.findThumbnailLink(
									HE, thumb_xp));

						} else {
							crawledLinkDS.setThumbLink("No thumb_xpath.");

						}

					}
					returnedListOFLinks.add(crawledLinkDS);
				}
			}
		} else if (firstRunForWebsite && this.item_xp != null) {
			siteDes.setAcceptsStopWords(false);
		}

		return returnedListOFLinks;
	}

	private List<WebElement> extractSearchResutlsFragments() {
		List<WebElement> elementList = null;
		if (item_xp == null) {
			System.err.println("Unable to determine SearchResult xPath for: "
					+ url);
			System.err.flush();
			this.stopThread();
		} else {
			// this.item_xp = "//table[./tbody/tr/td/a]";
			// extract results as html-elements by using the given xpath
			if (item_xp.contains("\\")) {
				item_xp = item_xp.replace("\\", "");
			}
			elementList = (List<WebElement>) sRPagesbrowser
					.runXPathQuery(this.item_xp);
		}
		return elementList;

	}

	private void saveCrawledResultInXML(Document doc) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File file = new File("crawledData/"
					+ crawlingConfig.getCollectionName()
					+ "/crawledresults.xml");
			if (!file.exists()) {
				file.mkdir();
			}
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	// makes directory if not exist
	// capture Screen shots
	private void saveRetunredResultPage(String pageAsXML,
			int noOfReturnedResultsPages) {
		try {
			File saveDir = new File(saveLinkPath + "resultPages" + "/");
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			String filePath = saveLinkPath + "resultPages" + "/"
					+ noOfReturnedResultsPages;
			crawlingConfig.getScrShotBrowser().captureSc(filePath);
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			/*
			 * out.write("<number> \n" + noOfReturnedResultsPages +
			 * "\n</number>\n"); out.flush(); out.write("<html>\n" + pageAsXML +
			 * "\n</html>\n");
			 */
			out.write(pageAsXML);
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	private String findTitle(WebElement wE, String title_xp) {
		String result = "";
		if (!title_xp.equals(".")) {

		} else {
			try {
				result = wE.getText();
			} catch (org.openqa.selenium.StaleElementReferenceException we) {

			} catch (UnknownServerException we) {

			}
			return result;
		}
		if (title_xp.startsWith(".")) {
			title_xp = title_xp.replaceFirst(".", "");
		}
		if (title_xp.endsWith(";")) {
			title_xp = title_xp.replaceFirst(";", "");
		} else if (title_xp.startsWith("/")) {
			title_xp = title_xp.replaceFirst("/", "");
		}
		WebElement links = null;
		try {
			links = wE.findElement(By.xpath("./" + title_xp));
		} catch (org.openqa.selenium.StaleElementReferenceException e) {

		} catch (UnknownServerException ew) {

		}

		if (links == null) {
			System.out.println("Could not find the titleby the xpath: ");
		} else {
			try {
				result = links.getText();
			} catch (org.openqa.selenium.StaleElementReferenceException we) {

			} catch (UnknownServerException we) {

			}
		}
		return result;
	}

	/*
	 * private void checkForAcceptingStopWords() {
	 * 
	 * HtmlPage stopWordPage = crawlingConfig.getWebTools().getThePage(
	 * crawlingConfig.setTestQuery("the")); if (isEmptyPage(stopWordPage)) {//
	 * has the same number of nodes as the // empty page
	 * System.out.println("Not accepting stop words.");
	 * siteDes.setAcceptsStopWords(false); } else {
	 * siteDes.setAcceptsStopWords(true); } }
	 */

	// check if the page is empty based on DOM tree nodes number
	/*
	 * private boolean isEmptyPage(HtmlPage page) { // String textFromTheQuery =
	 * page.asXml();// .replaceAll("the", ""); if (emptyPage == null) {
	 * emptyPage = crawlingConfig .getWebTools() .getThePage( crawlingConfig
	 * .setTestQuery(
	 * "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"
	 * )); } int numNodesInThePage = page.getByXPath("//*").size(); int
	 * numNodesInEmptyPage = emptyPage.getByXPath("//*").size();
	 * 
	 * if (areCloseEnough(numNodesInEmptyPage, numNodesInThePage)) { return
	 * true; } else { return false; } }
	 */

	// to check if the two values are in each others' vicinity- vicinity
	// threshold (10)
	/*
	 * private boolean areCloseEnough(int numNodesInEmptyPage, int
	 * numNodesInThePage) { double a = ((double) numNodesInThePage - (double)
	 * numNodesInThePage); if ((numNodesInThePage - numNodesInEmptyPage) >= -10
	 * && (numNodesInThePage - numNodesInEmptyPage) <= 10) { return true; }
	 * return false; }
	 */

	protected String findDesc(WebElement addressComponent,
			CrawledLinkDS crawledLinkDS, String descXPath) {
		String result = "";
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
		// numOfTries is zero for the first time, if cannot find xpath in
		// first try, increased
		if (descXPath.equals(".")) {// remove the title from whole text and

			// return the remaining as description
			try {
				result = addressComponent.getText();
			} catch (org.openqa.selenium.StaleElementReferenceException we) {

			} catch (UnknownServerException we) {

			}

			if (result.contains(crawledLinkDS.getTitle())) {
				result.replace(crawledLinkDS.getTitle(), "");
			}
		} else {
			if (descXPath.startsWith(".")) {
				descXPath = descXPath.replaceFirst(".", "");
			}
			/*
			 * if (descXPath.startsWith("//")){ descXPath =
			 * descXPath.replaceFirst("//", "/"); }
			 */
			if (descXPath.endsWith(";")) {
				descXPath = descXPath.replaceFirst(";", "");
			} else if (descXPath.startsWith("/")) {
				descXPath = descXPath.replaceFirst("/", "");
			}
			WebElement links = null;
			try {
				links = addressComponent.findElement(By.xpath("./" + desc_xp));
			} catch (org.openqa.selenium.StaleElementReferenceException e) {

			} catch (UnknownServerException ew) {

			}

			if (links == null) {
				System.out
						.println("Could not find the description by the xpath: ");
			} else {
				// result = links.get(0).getNodeValue();
				try {
					result = links.getText();
				} catch (org.openqa.selenium.StaleElementReferenceException we) {

				} catch (UnknownServerException we) {

				}
			}
		}
		return result;
	}

	protected String findThumbnailLink(WebElement addressComponent,
			String thumbXPath) {
		String result = "";
		/*
		 * Iterable<HtmlElement> iterChild=
		 * addressComponent.getHtmlElementDescendants();
		 */
		// numOfTries is zero for the first time, if cannot find xpath in
		// first try, increased

		if (thumbXPath.startsWith(".")) {
			thumbXPath = thumbXPath.replaceFirst(".", "");
		}
		/*
		 * if (descXPath.startsWith("//")){ descXPath =
		 * descXPath.replaceFirst("//", "/"); }
		 */
		if (thumbXPath.endsWith(";")) {
			thumbXPath = thumbXPath.replaceFirst(";", "");
		}
		WebElement links = null;
		links = addressComponent.findElement(By.xpath("./" + thumbXPath));
		// TODO check
		this.sRPagesbrowser.DownloadImage(links, "D:\\Download\\image.png");

		if (links == null) {
			System.out.println("Could not find thumbnail by the xpath: ");
		} else {
			// result = links.get(0).getNodeValue();
			result = links.getText();
		}
		return result;
	}

	public void stopThread() {
		// listOfReturnedResults reset
		// represents links extracted only for one web source
		listOfAllReturnedResults.clear();
		this.nextResultPagexPath = null;
	}

	/**
	 * @param nextPageResultLink
	 *            if no nextResultsPage, set next query
	 * @return url
	 */
	private String setURLNextResultPage() {
		List<WebElement> nextPageResultLink = null;
		if (nextResultPagexPath != null) {
			/*
			 * nextPageResultLink = (List<HtmlAnchor>) page
			 * .getByXPath(nextResultPagexPath);
			 */
			nextPageResultLink = (List<WebElement>) sRPagesbrowser
					.runXPathQuery(this.nextResultPagexPath);
		}
		if (nextPageResultLink != null) {
			if (nextPageResultLink.size() > 0) {
				WebElement tempO = nextPageResultLink.get(0);

				if (tempO instanceof HTMLAnchorElement) {
					HTMLAnchorElement a = (HTMLAnchorElement) nextPageResultLink
							.get(0);
					url = a.getHref();// .getHrefAttribute();
				} else if (((WebElement) tempO).getTagName().equalsIgnoreCase(
						"a")) {
					url = ((WebElement) tempO).getAttribute("href");
				}
				if (url.contains("javascript:")) {
					/*
					 * String onClick = ((WebElement)
					 * tempO).getAttribute("href")
					 * ;//tempO.getAttribute("onclick"); onClick =
					 * onClick.replace("Page$2", "Page$200"); //
					 * url.replace(url.substring(url.indexOf("javascript:"),
					 * url.length()), "");
					 * 
					 * ((JavascriptExecutor)
					 * sRPagesbrowser.driver).executeScript("" + "<a href=" +
					 * onClick + "style=\"color:Black;\">3</a>.click();" );
					 */
					try {
						tempO.click();
					} catch (org.openqa.selenium.StaleElementReferenceException we) {

					} catch (UnknownServerException we) {

					}
					url = "javascript clicked.";
				}
				/*
				 * url = nextPageResultLink.get(0).getAttribute("href");
				 */
			}
		}
		if (url != null) {
			if (url.equals("")) {
				return null;
			}
		}
		return url;
	}

	/**
	 * @param page
	 *            sets nextResultPagexPath
	 * @return list of candidates for
	 */
	private void detectNextButtonXpath() {
		List<WebElement> nextPageResultLink;
		if (nextResultPagexPath == null) {
			/*
			 * nextPageResultLink = (List<HtmlAnchor>) page
			 * .getByXPath("//a[contains(lower-case(.), 'next')]");
			 */

			/*
			 * nextPageResultLink = (List<WebElement>) sRPagesbrowser
			 * .runXPathQuery(
			 * "//a[contains(translate(@href,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next')]"
			 * );
			 */
			nextPageResultLink = (List<WebElement>) sRPagesbrowser
					.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next')]");
			// only for xpath 1.0 => "//a[contains(lower-case(.), '')]"
			if (!nextPageResultLink.isEmpty()) {
				nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'next')]";
				// String attValue= FindAttValueTemplate(crawlingConfig.query,
				// nextPageResultLink.get(0).getText());
			} else {
				/*
				 * nextPageResultLink = (List<HtmlAnchor>) page
				 * .getByXPath("//a[contains(lower-case(.), 'volgende')]");
				 */
				nextPageResultLink = (List<WebElement>) sRPagesbrowser
						.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'volgende')]");
				// only for xpath 1.0 =>
				// "//a[contains(lower-case(.), 'volgende')]"
				if (!nextPageResultLink.isEmpty()) {
					nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'volgende')]";
				} else {
					/*
					 * nextPageResultLink = (List<HtmlAnchor>) page
					 * .getByXPath("//a[contains(lower-case(.), 'previous')]");
					 */
					nextPageResultLink = (List<WebElement>) sRPagesbrowser
							.runXPathQuery("//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'previous')]"); // for
																																					// djoerd
																																					// website
					if (!nextPageResultLink.isEmpty()) {
						nextResultPagexPath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'previous')]";
					}
				}
			}
			if (nextPageResultLink.isEmpty()) {
				System.out
						.println("Could not detect the next page xpath and link for next page of results.");
			} else {// it might be the cxase to have a page without any next
					// button in the first try
				firstTimeCrawl = false;
			}
		}
	}

	@SuppressWarnings("unused")
	private String FindAttValueTemplate(String query, String currentURL) {
		String result = currentURL
				.substring(0, currentURL.indexOf("=" + query));
		result = result.substring(result.lastIndexOf("&"), result.length());
		return result;
	}

	/**
	 * 
	 */
	private void printQueriesResults(String path) {
		System.out
				.println("Total number of queries sent to the search engine - crawling: "
						+ crawlReport.getCountCrawlingQueries());
		Iterator<String> e = crawlReport.getQueryNumberofItsResults().keySet()
				.iterator();
		System.out.println("Index:			" + "  Query:			"
				+ "		#TotalCrawled Results (excluding repeated)" + "      "
				+ "    #RepeatedInTotal" + "    #RepeatedOnlyInQuery"
				+ "    #ReturnedResForQuery");

		ExcelWriter ExcelWr;
		ExcelWr = new ExcelWriter();
		ExcelWr.setting(path, crawlingConfig.getCollectionName());
		ExcelWr.setRow(4);
		ExcelWr.setColumn(4);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"Index");
		ExcelWr.setColumn(ExcelWr.column + 1);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"Query");
		ExcelWr.setColumn(ExcelWr.column + 1);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"#Crawled Results");
		ExcelWr.setColumn(ExcelWr.column + 1);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"#RepeatedInOnlyQueyResults");
		ExcelWr.setColumn(ExcelWr.column + 1);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"#RepeatedInAllResults");

		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"#ReturnedResForQuery");
		ExcelWr.setColumn(ExcelWr.column + 1);
		ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
				"#ReturnedResForQuery");

		ExcelWr.setColumn(4);
		ExcelWr.setRow(ExcelWr.row + 1);

		while (e.hasNext()) {
			String query = (String) e.next();
			QueryResStatistics qResStat = crawlReport
					.getQueryNumberofItsResults().get(query);
			System.out.println(qResStat.getqPosedIndex() + " \t\t\t" + query
					+ "			\t\t\t" + qResStat.getUniqResults() + "    \t\t\t"
					+ qResStat.getRepeatedResults());
			ExcelWr.addNumber(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					qResStat.getqPosedIndex());
			ExcelWr.setColumn(ExcelWr.column + 1);
			ExcelWr.addLabel(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					query);
			ExcelWr.setColumn(ExcelWr.column + 1);
			ExcelWr.addNumber(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					qResStat.getUniqResults());

			ExcelWr.setColumn(ExcelWr.column + 1);
			ExcelWr.addNumber(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					qResStat.getRepeatedResultsGeneral());

			ExcelWr.setColumn(ExcelWr.column + 1);
			ExcelWr.addNumber(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					qResStat.getRepeatedResults());
			ExcelWr.setColumn(ExcelWr.column + 1);
			ExcelWr.addNumber(ExcelWr.excelSheet, ExcelWr.column, ExcelWr.row,
					qResStat.gettotalResultsEachQuery());

			ExcelWr.setColumn(4);

			ExcelWr.setRow(ExcelWr.row + 1);
		}
		ExcelWr.writeSheetInFile();
		ExcelWr.closeExcel();
	}

	protected String returnLinkInItem(WebElement addressComponent,
			String link_xp) {
		String result = "";
		String linkXP = link_xp;
		WebElement links = null;
		if (linkXP.startsWith(".")) {
			linkXP = linkXP.replaceFirst(".", "");
		}
		try {
			links = addressComponent.findElement(By.xpath("./" + linkXP));
		} catch (org.openqa.selenium.StaleElementReferenceException e) {

		} catch (UnknownServerException ew) {

		}

		if (links == null) {
			System.out
					.println("Could not find the link of the result for the xpath: ");
			siteDes.setComments(siteDes.getComments() + "\n"
					+ "Could not find the link of the result for the xpath");
		} else {
			Object tempR = links;
			if (tempR instanceof HTMLAnchorElement) {
				// String name = ((HTMLAnchorElement) tempR).getName();//
				// .getNameAttribute();
				// String value = ((HTMLAnchorElement) tempR).getHref();//
				// .getHrefAttribute();
				result = ((HTMLAnchorElement) tempR).getHref();// .getHrefAttribute();
			} else {
				// String name = links.get(0).getName();
				// String value = links.getText();
				try {
					result = links.getAttribute("href");
				} catch (org.openqa.selenium.StaleElementReferenceException e) {

				} catch (UnknownServerException er) {

				}
			}
		}
		return result;
	}

	/* removed because of htmlunit htmlElement *//**
	 * @param sourceURL
	 * @param addressComponent
	 * @param xPathSource
	 * @param findxPath
	 * @return
	 */
	/*
	 * @SuppressWarnings("unchecked") protected String
	 * findURLsOfResults(HtmlElement addressComponent, String xPathSource,
	 * boolean findxPath, int numOfTries) { String result = ""; String
	 * componentxPath = addressComponent.getCanonicalXPath();
	 * 
	 * Iterable<HtmlElement> iterChild=
	 * addressComponent.getHtmlElementDescendants();
	 * 
	 * if (findxPath) { // numOfTries is zero for the first time, if cannot find
	 * xpath in // first try, increased linkDetectionxPath =
	 * resultLinkXpathFinder.findResultLinkxPath( addressComponent, item_xp,
	 * numOfTries); firstTryForLinkDetection = false; }
	 * 
	 * List<HtmlElement> links = null; for (String linkxPath :
	 * linkDetectionxPath) { links = (List<HtmlElement>) addressComponent
	 * .getByXPath(componentxPath + linkxPath); if (!links.isEmpty()) { break; }
	 * }
	 * 
	 * if (links == null) { System.out
	 * .println("Could not find the link address by the xpath: (" +
	 * componentxPath + " ) , ( " + linkDetectionxPath);
	 * 
	 * numOfTries++; if ( numOfTries <
	 * resultLinkXpathFinder.getCandidatesForEachElement
	 * ().get(addressComponent).size()){ result =
	 * findURLsOfResults(addressComponent, xPathSource, true, numOfTries); //
	 * adds another xpath }
	 * 
	 * } else { result = links.get(0).getAttribute("href"); } return result; }
	 */

	/**
	 * @param link
	 * @return
	 */
	@SuppressWarnings("unused")
	private String CheckLinkWithUser(String link) {
		System.out.println("Is this the correct link for a rsult: " + link
				+ "\n Is this the correct one?(Y/N)");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line.equalsIgnoreCase("y")) {
			return "yes";
		} else {
			return "no";
		}
	}

	/**
	 * @param xPathSource
	 * @return
	 */
	@SuppressWarnings("unused")
	private String refinexPath(String xPathSource) {
		xPathSource = xPathSource.replaceFirst("//[a-z]*.", "");
		xPathSource = xPathSource.replace("[", "");
		xPathSource = xPathSource.replace("]", "");
		xPathSource = xPathSource.replace(".", "");
		return xPathSource;
	}

	/**
	 * @param originalURL
	 * @param linkedPath
	 * @return completed form of URL (with http) by Victor
	 */
	public String getLinkedURL(String originalURL, String linkedPath) {
		URL origURL = null;
		try {
			origURL = new URL(originalURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (linkedPath.startsWith("/")) {
			String portSuffix = origURL.getPort() == -1 ? "" : ":"
					+ origURL.getPort();
			linkedPath = origURL.getProtocol() + "://" + origURL.getHost()
					+ portSuffix + linkedPath;
		} else if (!linkedPath.startsWith("http")) {
			String originalUrlString = origURL.toString();
			linkedPath = originalUrlString.substring(0,
					originalUrlString.lastIndexOf("/") + 1)
					+ linkedPath;
		}
		return linkedPath;
	}

	/**
	 * @param textContentOfPage
	 * @param url
	 * @param index
	 */
	private void saveExtractedInfoOfLink(CrawledLinkDS crawledLinkDS,
			int numberOfLink, String URl) {
		/*
		 * <?xml version="1.0" encoding="ISO-8859-1"?> <html
		 * xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
		 */
		try {
			String filePath = saveLinkPath + "link" + numberOfLink;
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<title> \n" + crawledLinkDS.getTitle() + "\n</title>\n");
			out.flush();
			out.write("<description>\n" + crawledLinkDS.getDescription()
					+ "\n</description>\n");
			out.flush();
			out.write("<link>\n " + crawledLinkDS.getLink() + "\n</link>\n");
			out.flush();
			out.write("<text>\n " + crawledLinkDS.getLinkTextContent()
					+ "\n</text>\n");
			out.flush();
			out.write("<html> \n" + crawledLinkDS.getLinkHtmlContent()
					+ "\n</html>\n");
			out.flush();
			out.write("<thumblink> \n" + crawledLinkDS.getThumbLink()
					+ "\n</thumblink>\n");
			out.flush();
			out.write("<itemXML>\n " + crawledLinkDS.getItemXML()
					+ "\n</itemXML>\n");

			// out.write(crawledLinkDS.getLinkHtmlContent());
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @param url
	 * @return
	 */
	private String setTextHtmlOfLink(String content, CrawledLinkDS crawledLinkDS) {
		String textContent = null;
		crawledLinkDS.setLinkHtmlContent(content);

		/*
		 * try { crawledLinkDS.setLinkHtmlContent(content); } catch
		 * (FailingHttpStatusCodeException e) { e.printStackTrace(); } catch
		 * (com.gargoylesoftware.htmlunit.ScriptException e) { textContent =
		 * "ScriptException. Could not extract the text." + e.toString();
		 * System.out.println("ScriptException. Could not extract the text."); }
		 */
		return textContent;
	}
}
