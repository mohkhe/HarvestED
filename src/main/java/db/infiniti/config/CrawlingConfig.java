package db.infiniti.config;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import db.infiniti.harvester.modules.common.Cache;
import db.infiniti.harvester.modules.querygenerator.FeedbackBasedQueryGenerator;
import db.infiniti.sitedescription.DetailedPageDS;
import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.sitedescription.WebsiteDescReader;
import db.infiniti.surf.Browser;

//import db.infiniti.webtools.WebTools;

public class CrawlingConfig {
	String currentCollectionName;
	ArrayList<String> queries;
	LinkedHashMap<String, String> queryNumberofResults;
	private String openDescFilePath = "";
	private String openDescDirPath = "";
	private String queriesPath = "";
	private String linkContentSavePath = "";
	Thread crawlingTh;
	String sourceURL;
	ArrayList<String> listOfSourcesFolders;
	WebsiteDescReader descripReader = new WebsiteDescReader();
	WebsiteDS currentSiteDescription;
	String currentSRPageURL;
	// WebTools webTools;
	ArrayList<WebsiteDS> listOfWebsites;
	int queryIndex = 0;
	THashSet<String> listOfStopWords;
	Browser scrShots;
	HashMap<Browser, Boolean> detailedPagesBrowsers = new HashMap<Browser, Boolean>();
	TObjectIntHashMap<String> querySet = new TObjectIntHashMap<String>();
	//HashSet<String> queryArray = new HashSet<String>();
	Set<String> queryArray = new THashSet<String>();
	HashMap<String, Integer> termFreqInClueWeb = new HashMap<String, Integer>();

	List<String> sentQueries = new ArrayList<String>();

	boolean extractTextFromSRPages;
	boolean extractTextFromAllVisitedPages;
	public boolean SaveDSextractedInfoInFile;

	boolean firstQuery = true;
	public String query;
	public List<String> initialQuery;
	

	String initialQueryProcesses = "";

	int querySelectionApproach;
	public int PredefinedlistOfWords = 1;
	public int browsing = 2;
	public int mostFreqFeedbackText = 3;
	public int leastFromLast = 4;
	public int leastFreqFeedbackText = 5;
	public int combinedLFL_PLW = 6;
	public boolean feedbackBasedApproach = false;

	public FeedbackBasedQueryGenerator fbBasedQueryGenerator;
	public boolean extractDataFromDPageS = true;
	public int searchResultPageNumber;

	public DetailedPageDS detailedPageDS = new DetailedPageDS();
	public String tableName;
	public String outputDataBase;
	public String dataModelTable;

	ArrayList<Browser> poolOfBrowsers;

	public String pathToAllDOwnloadedPages;
	public String pathToVisitedPagesDoc;
	public String pathToSentQueriesDoc;
	public String pathToCoveredCollections;
	public String pathToLastLinkDoc;
	public String pathToURLCrawlList;
	public String pathToLastSearchResultPage;
	public String pathToNumberOfSearchResults;
	public String pathToNumberOfRepetitions;
	public String pathToVisitedPagesPerQuery;
	public String pathToNumberOfSentQueries;
	public boolean unPauseCrawl = true;
	public String crawlStatusPath;

	IndexesConfig indexA;
	IndexesConfig indexB;
	public Cache cache;
	
	
	public boolean isIndexed = false;
	private IndexesConfig indexOld;
	private IndexesConfig indexNew;
	
	public boolean isIndexed() {
		return isIndexed;
	}

	public void setIndexed(boolean isIndexed) {
		this.isIndexed = isIndexed;
	}


	public List<String> getInitialQuery() {
		return initialQuery;
	}

	public void setInitialQuery(List<String> initialQuery) {
		this.initialQuery = initialQuery;
	}

	public void Cache() {

	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(String cachePath, boolean isIndexed) {
		if(!isIndexed){
			this.cache = new Cache();
			cache.setCacheMapFilePath(cachePath);
			cache.prepareCacheReadWrite();
			cache.readCacheMap();
		}else if (isIndexed){
			cache.indexOld = new IndexesConfig("index/pages");
			cache.indexNew = new IndexesConfig("newindex/pages");
		}
	}

	public int getSearchResultPageNumber() {
		return searchResultPageNumber;
	}

	public void setSearchResultPageNumber(int searchResultPageNumber) {
		this.searchResultPageNumber = searchResultPageNumber;
	}

	public String getOutputDataBase() {
		return outputDataBase;
	}

	public void setOutputDataBase(String outputDataBase) {
		this.outputDataBase = outputDataBase;
	}

	public boolean isExtractTextFromSRPages() {
		return extractTextFromSRPages;
	}

	public void setExtractTextFromSRPages(boolean extractTextFromSRPages) {
		this.extractTextFromSRPages = extractTextFromSRPages;
	}

	public boolean isExtractTextFromAllVisitedPages() {
		return extractTextFromAllVisitedPages;
	}

	public void setExtractTextFromAllVisitedPages(
			boolean extractTextFromAllVisitedPages) {
		this.extractTextFromAllVisitedPages = extractTextFromAllVisitedPages;
	}

	public String getPathToAllDOwnloadedPages() {
		return pathToAllDOwnloadedPages;
	}

	public void setPathToAllDOwnloadedPages(String pathToAllDOwnloadedPages) {
		this.pathToAllDOwnloadedPages = pathToAllDOwnloadedPages;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDataModelTable() {
		return dataModelTable;
	}

	public void setDataModelTable(String dataModelTable) {
		this.dataModelTable = dataModelTable;
	}

	public void setDetailedPageDS(String DBName) {
		this.detailedPageDS.readItemsXpathDB(DBName, this.getDataModelTable());
	}

	public boolean isExtractDataFromDPageS() {
		return extractDataFromDPageS;
	}

	public void setExtractDataFromDPageS(boolean extractDataFromDPageS) {
		this.extractDataFromDPageS = extractDataFromDPageS;
	}

	public String getCrawlStatusPath() {
		return crawlStatusPath;
	}

	public String getPathToNumberOfRepetitions() {
		return pathToNumberOfRepetitions;
	}

	public void setPathToNumberOfRepetitions(String pathToNumberOfRepetitions) {
		this.pathToNumberOfRepetitions = pathToNumberOfRepetitions;
	}

	public String getPathToLastSearchResultPage() {
		return pathToLastSearchResultPage;
	}

	public void setPathToLastSearchResultPage(String pathToLastSearchResultPage) {
		this.pathToLastSearchResultPage = pathToLastSearchResultPage;
	}

	public String getPathToVisitedPagesPerQuery() {
		return pathToVisitedPagesPerQuery;
	}

	public void setPathToVisitedPagesPerQuery(String pathToVisitedPagesPerQuery) {
		this.pathToVisitedPagesPerQuery = pathToVisitedPagesPerQuery;
	}

	public void setAllSitesDescription() {
		listOfWebsites = descripReader.readDB();
	}

	public void setCurrentSiteDescription(boolean readFromDB, int index) {
		if (readFromDB) {
			currentSiteDescription = descripReader.readOneDSFromDB(index);
		} else {
			currentSiteDescription = descripReader.readOpenDSFile(
					openDescFilePath + currentCollectionName, index); // from
			// openFileDescription
		}
	}

	public String getCollectionName() {
		return currentCollectionName;
	}

	public void setCollectionName() {
		String collectionName = null;
		File file = new File(openDescFilePath);
		if (file.isDirectory()) {
			String[] list = file.list();
			if (list.length > 1) {// to remove effect of .svn
				collectionName = list[1];
			} else {
				collectionName = list[0];
			}
			// there is only one openDescFile in each directory
		}
		this.currentCollectionName = collectionName;
	}

	public void setCollectionName(String name) {
		this.currentCollectionName = name;
	}

	public ArrayList<String> getQueries() {
		return queries;
	}

	public void setQueries(ArrayList<String> queries) {
		this.queries = queries;
	}

	public LinkedHashMap<String, String> getQueryNumberofResults() {
		return queryNumberofResults;
	}

	public void setQueryNumberofResults(
			LinkedHashMap<String, String> queryNumberofResults) {
		this.queryNumberofResults = queryNumberofResults;
	}

	public String getOpenDescFilePath() {
		return openDescFilePath;
	}

	public void setOpenDescFilePath(int numberOfCrawledSoerces) {
		this.openDescFilePath = this.openDescDirPath
				+ this.getListOfSourcesFolders().get(numberOfCrawledSoerces)
				+ "/";
	}

	public String getQueriesPath() {
		return queriesPath;
	}

	public void setQueriesPath(String queriesPath) {
		this.queriesPath = queriesPath;
	}

	public String getLinkContentSavePath() {
		return linkContentSavePath;
	}

	public void setLinkContentSavePath(String linkContentSavePath) {
		String filePath = linkContentSavePath;

		File file = new File(filePath);
		if (file.exists()) {
			this.linkContentSavePath = linkContentSavePath;
		} else if (file.mkdir()) {
			this.linkContentSavePath = linkContentSavePath;
			System.out.println("Save link path is set.");
		} else {
			System.err
					.println("Could not create the path to save the content of links returned by serch engine.");
		}
	}

	public void setCrawlStatusPath(String linkContentSavePath) {
		String filePath = linkContentSavePath;

		File file = new File(filePath);
		if (file.exists()) {
			this.crawlStatusPath = linkContentSavePath;
		} else if (file.mkdir()) {
			this.crawlStatusPath = linkContentSavePath;
			System.out.println("Crawl status path is set.");
		} else {
			System.err
					.println("Could not create the path to save the crawl status.");
		}
	}

	public Thread getCrawlingTh() {
		return crawlingTh;
	}

	public void setCrawlingTh(Thread crawlingTh) {
		this.crawlingTh = crawlingTh;
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public void setQueries(String queriesFilePath) {
		if (this.getQuerySelectionApproach() == this.PredefinedlistOfWords) {
			this.queriesPath = queriesFilePath;
			queries = new ArrayList<String>();
			/*
			 * queries.add("vitol"); queries.add("vitol+oil");
			 * queries.add("vitol+trading"); queries.add("vitol+foundation");
			 * queries.add("vitol+group");
			 */
			// TODO for now to put everything in cache
			fbBasedQueryGenerator = new FeedbackBasedQueryGenerator();
			queries = readQueriesFromFile(queries, queriesPath);
			// //hazf-> queries = readQueriesFromFile(queries, queriesFilePath);
		} else if (this.feedbackBasedApproach) {
			if (fbBasedQueryGenerator == null) {
				fbBasedQueryGenerator = new FeedbackBasedQueryGenerator();
			}
		}
		/*
		 * else if (this.getQuerySelectionApproach() == this.crawledTextBased) {
		 * } // do nothing }
		 */
	}

	// [the, of, on, and, in, content, to, as, have, not, is, will, home, from,
	// by, on, wikipedia, for, was, site]
	private ArrayList<String> readQueriesFromFile(ArrayList<String> queries,
			String filePath) {
		try {
			File file = new File(filePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line;
			while ((line = in.readLine()) != null) {
				// String line = in.readLine();
				String query = line.split("\t")[0];
				// String query = line.replaceAll("[0-9]*", "").trim();
				if (!queries.contains(query)) {
					queries.add(query.intern());
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return queries;
	}

	public ArrayList<String> getListOfSourcesFolders() {
		return listOfSourcesFolders;
	}

	public ArrayList<String> setListOfSourcesFolders() {
		listOfSourcesFolders = new ArrayList<String>();
		File file = new File(openDescDirPath);
		if (file.isDirectory()) {
			String[] list = file.list();
			for (String fileDir : list) {
				listOfSourcesFolders.add(fileDir);
			}
			if (listOfSourcesFolders.contains(".svn")) {
				listOfSourcesFolders.remove(".svn");
			}
		}
		return listOfSourcesFolders;
	}

	public void updateQueryList(String pageContent) {
		pageContent = this.refineText(pageContent);
		String[] tokens = tokenizer(pageContent);

		for (String token : tokens) {
			if (!token.equalsIgnoreCase("") && !this.isStopWord(token)
					&& !initialQuery.contains(token)
					&& !sentQueries.contains(token)) {// it is not used before
				// !initialQuery.contains(token) to avoid having
				// "vitol+company+vitol"
				if (this.querySelectionApproach == this.combinedLFL_PLW) {
					
				/*	synchronized (queryArray) {
						if(termFreqInClueWeb.containsKey(token)){
							queryArray.add(token.intern());
						}
					}*/
				} else {
					synchronized (querySet) {
						if (querySet.containsKey(token)) {
							querySet.put(token.intern(),
									querySet.get(token) + 1);

						} else {
							querySet.put(token.intern(), 1);

						}
					}
				}
			}
		}
	}

	public String refineText(String pageContent) {

		if (pageContent.contains("\n")) {
			pageContent = pageContent.replaceAll("\n", " ");
		}
		/*
		 * if (pageContent.matches("[.,!?:;'\"-]")){ pageContent =
		 * pageContent.replaceAll("\\p{punct}+", " "); } if
		 * (pageContent.contains("\\p{Punct}")){ pageContent =
		 * pageContent.replaceAll("\\p{Punct}", " "); }
		 */
		if (pageContent.contains("\\") || pageContent.contains(":")
				|| pageContent.contains(";") || pageContent.contains(".")) {
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}
		return pageContent.trim();
	}

	private String[] tokenizer(String content) {
		String delims = "[+\\-*/\\^ .,?!:;=()]+";
		String[] tokens = content.split(delims);
		return tokens;
	}

	public String setNextQuery() {
		String url = null;
		if (this.querySelectionApproach == this.mostFreqFeedbackText
				|| this.querySelectionApproach == this.leastFromLast
				|| this.querySelectionApproach == this.leastFreqFeedbackText) {// this.feedbackBasedApproach
																				// ==
																				// true)
																				// {
			if (firstQuery) {// set first query
				query = "";
				for (String part : initialQuery) {
					initialQueryProcesses = initialQueryProcesses + "+\""
							+ part + "\"";
				}
				initialQueryProcesses = initialQueryProcesses.replaceFirst(
						"\\+", "");
				query = initialQueryProcesses; // only for vitol website
				sentQueries.add(query.intern());
				querySet.put(query.intern(), 0); // not to use later
				firstQuery = false;
			} else if (this.querySelectionApproach == this.leastFromLast) {
				query = fbBasedQueryGenerator
						.setNextQueryLeastFromLast(initialQuery);
				if (query != null) {
					sentQueries.add(query.intern());
					querySet.put(query.intern(), 0);
					query = initialQueryProcesses + "+\"" + query + "\"";
				} else {
					return null;
				}
			} else if (this.querySelectionApproach == this.mostFreqFeedbackText) {
				query = fbBasedQueryGenerator.getMostFreqQuery(querySet,
						initialQuery);// .getLeastFreqQuery(querySet,
										// initialQuery);
				sentQueries.add(query.intern());
				querySet.put(query.intern(), 0);
				query = initialQueryProcesses + "+\"" + query + "\"";
				saveMostFreqTable();
			} else if (this.querySelectionApproach == this.leastFreqFeedbackText) {
				query = fbBasedQueryGenerator.getLeastFreqQuery(querySet,
						initialQuery);// .getLeastFreqQuery(querySet,
										// initialQuery);
				sentQueries.add(query.intern());
				querySet.put(query.intern(), 0);
				query = initialQueryProcesses + "+\"" + query + "\"";
				;
				// saveMostFreqTable();
			}
			// not to use later//it grows again
			// // for vitol

			// querySet.put(query, 0); // not to use later

			/*
			 * String tempUrl = currentSiteDescription.getTemplate();
			 * 
			 * if (tempUrl.contains("{q}")) { url =
			 * currentSiteDescription.getTemplate() .replace("{q}", query); }
			 * else if (tempUrl.contains("{searchTerms}")) { url =
			 * currentSiteDescription.getTemplate().replace( "{searchTerms}",
			 * query); } else if (tempUrl.contains("{query}")) { url =
			 * currentSiteDescription.getTemplate().replace("{query}", query); }
			 * else {// in case of browsing url =
			 * currentSiteDescription.getTemplate(); }
			 * 
			 * currentSiteDescription.getTemplate().replace( "{searchTerms}",
			 * query);
			 * 
			 * url = url.replace("amp;", "");
			 * System.out.println("New query, number " + queryIndex + " : " +
			 * query); queryIndex++; return url;
			 */
		} else if (this.querySelectionApproach == this.PredefinedlistOfWords) {
			if (firstQuery) {// set first query
				query = "";
				for (String part : initialQuery) {
					initialQueryProcesses = initialQueryProcesses + "+\""
							+ part + "\"";
				}
				initialQueryProcesses = initialQueryProcesses.replaceFirst(
						"\\+", "");
				query = initialQueryProcesses; // only for vitol website
				sentQueries.add(query.intern());
				querySet.put(query.intern(), 0); // not to use later
				firstQuery = false;
			} else if (queryIndex < queries.size()) {
				query = queries.get(queryIndex);
				if (!currentSiteDescription.isAcceptsStopWords()) {
					if (isStopWord(query)) {
						queryIndex++;
						return setNextQuery();
					}
				} else {
					System.out.println("End of Query List");
					return null;
				}
				sentQueries.add(query.intern());
				querySet.put(query.intern(), 0); // not to use later
				query = initialQueryProcesses + "+\"" + query + "\"";
			}
		} else if (this.querySelectionApproach == this.browsing) {
			if (url == null) {
				url = currentSiteDescription.getTemplate();
			}
		} else if (this.querySelectionApproach == combinedLFL_PLW) {
			if (firstQuery) {// set first query
				query = "";
				for (String part : initialQuery) {
					// initialQueryProcesses = initialQueryProcesses + "+\"" +
					// part+ "\"";
					initialQueryProcesses = initialQueryProcesses + "+" + part
							+ "";

				}
				initialQueryProcesses = initialQueryProcesses.replaceFirst(
						"\\+", "");
				query = initialQueryProcesses; // only for vitol website
				sentQueries.add(query.intern());
				// queryArray.add(query.intern(), 0); // not to use later
				firstQuery = false;
			} else {
				query = fbBasedQueryGenerator.setNextQueryIn_Feedback_ClueWeb(
						initialQuery, queryArray, this.termFreqInClueWeb);
				if (query != null) {
					sentQueries.add(query.intern());
					querySet.put(query.intern(), 0);
					// query = initialQueryProcesses + "+\"" + query + "\"";
					query = initialQueryProcesses + "+" + query + "";

				} else {
					return null;
				}
			}
		}
		String tempUrl = currentSiteDescription.getTemplate();
		if (tempUrl.contains("{q}")) {
			url = currentSiteDescription.getTemplate().replace("{q}", query);
		} else if (tempUrl.contains("{searchTerms}")) {
			url = currentSiteDescription.getTemplate().replace("{searchTerms}",
					query);
		} else if (tempUrl.contains("{query}")) {
			url = currentSiteDescription.getTemplate()
					.replace("{query}", query);
		}
		/*
		 * currentSiteDescription.getTemplate().replace( "{searchTerms}",
		 * query);
		 */
		url = url.replace("amp;", "");
		System.out.println("New query, number " + queryIndex + " : " + query);
		queryIndex++;
		return url;
	}

	private void saveMostFreqTable() {
		try {
			File file = new File(this.getCrawlStatusPath() + "tableofmostfreq");
			FileWriter fstream = new FileWriter(file, false);
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<String> keyIt = querySet.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int comp1 = querySet.get(key);
				out.write(key + "\t" + comp1 + "\n");
				out.flush();

			}
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	/*
	 * private void readMostFreqTableFromFile() { try { File file = new
	 * File(this.getCrawlStatusPath()+"tableofmostfreq"); Reader fstream = new
	 * FileW(file, false); BufferedReader in = new BufferedReader(fstream);
	 * Iterator<String> keyIt = querySet.keySet().iterator(); while
	 * (keyIt.hasNext()) { String key = (String) keyIt.next(); int comp1 =
	 * querySet.get(key); out.write(key+"\t"+comp1+"\n"); out.flush();
	 * 
	 * } out.close(); fstream.close(); } catch (Exception e) {// Catch exception
	 * if any System.err.println("Error: " + e.getMessage()); }
	 * 
	 * }
	 */

	/*
	 * private String getMostFreqQuery() {
	 * 
	 * List<String> mapKeys = new ArrayList<String>(querySet.keySet());
	 * List<Integer> mapValues = new ArrayList<Integer>(querySet.values());
	 * Collections.sort(mapValues, Collections.reverseOrder());
	 * 
	 * Iterator<Integer> valueIt = mapValues.iterator(); // TODO check if this
	 * is the most frequent and not the least frequent int val = (Integer)
	 * valueIt.next(); Iterator<String> keyIt = mapKeys.iterator(); while
	 * (keyIt.hasNext()) { String key = (String) keyIt.next(); int comp1 =
	 * querySet.get(key);
	 * 
	 * if (comp1 == val && !initialQuery.contains(key)) { return key; } }
	 * 
	 * return null; }
	 * 
	 * private String getLeastFreqQuery() {
	 * 
	 * List<String> mapKeys = new ArrayList<String>(querySet.keySet());
	 * List<Integer> mapValues = new ArrayList<Integer>(querySet.values());
	 * Collections.sort(mapValues);
	 * 
	 * while (mapValues.contains(0)) { mapValues.remove(0); } Iterator<Integer>
	 * valueIt = mapValues.iterator();
	 * 
	 * // TODO check if this is the most frequent and not the least frequent int
	 * val = (Integer) valueIt.next(); // set the lowest frequency
	 * Iterator<String> keyIt = mapKeys.iterator(); while (keyIt.hasNext()) { //
	 * we set value to 0 not to use it later String key = (String) keyIt.next();
	 * int comp1 = querySet.get(key);
	 * 
	 * if (comp1 == val && comp1 != 0 && !initialQuery.contains(key)) { return
	 * key; } }
	 * 
	 * return null; }
	 */
	private boolean isStopWord(String query) {
		if (listOfStopWords == null) {
			setListOfStopWords();
		}
		if (query.length() < 2) {
			return true;
		}
		if (query.matches(".*\\d.*")) {
			return true;
		}
		if (listOfStopWords.contains(query)) {
			return true;
		}
		return false;
	}

	public String setTestQuery(String query) {
		String url = currentSiteDescription.getTemplate().replace("{q}", query);
		url = url.replace("amp;", "");
		System.out.println("Test query: " + query);
		return url;
	}

	public void resetForNextCollection() {
		this.queryIndex = 0;
		this.querySet.clear();
		this.firstQuery = true;
	}

	public String getCurrentSRPageURL() {
		return currentSRPageURL;
	}

	public void setCurrentSRPageURL(String currentURL) {
		this.currentSRPageURL = currentURL;
	}

	/*
	 * public WebTools getWebTools() { return webTools; }
	 * 
	 * public void setWebTools(WebTools webTools) { this.webTools = webTools; }
	 */

	public String getOpenDescDirPath() {
		return openDescDirPath;
	}

	public void setOpenDescDirPath(String openDescDirPath) {
		this.openDescDirPath = openDescDirPath;
	}

	public WebsiteDS getCurrentSiteDescription() {
		return currentSiteDescription;
	}

	public void setCurrentSiteDescription(WebsiteDS currentSiteDescription) {
		this.currentSiteDescription = currentSiteDescription;
	}

	public ArrayList<WebsiteDS> getListOfWebsites() {
		return listOfWebsites;
	}

	public int getQueryIndex() {
		return queryIndex;
	}

	public void setQueryIndex(int queryIndex) {
		this.queryIndex = queryIndex;
	}

	public Set<String> setListOfStopWords() {
		listOfStopWords = new THashSet<String>();
		listOfStopWords.add("the");
		listOfStopWords.add("a");
		listOfStopWords.add("an");
		listOfStopWords.add("of");
		listOfStopWords.add("in");
		listOfStopWords.add("and");
		listOfStopWords.add("is");
		listOfStopWords.add("to");
		listOfStopWords.add("at");
		listOfStopWords.add("on");
		listOfStopWords.add("as");
		listOfStopWords.add("not");
		listOfStopWords.add("from");
		listOfStopWords.add("by");
		listOfStopWords.add("for");

		listOfStopWords.add("het");
		listOfStopWords.add("de");
		listOfStopWords.add("en");
		listOfStopWords.add("met");
		listOfStopWords.add("andere");
		listOfStopWords.add("tussen");
		listOfStopWords.add("van");
		listOfStopWords.add("een");
		listOfStopWords.add("pagina");
		listOfStopWords.add("deze");

		listOfStopWords.add("have");
		listOfStopWords.add("had");
		listOfStopWords.add("will");
		listOfStopWords.add("would");
		listOfStopWords.add("there");
		listOfStopWords.add("with");
		listOfStopWords.add("wikipedia");
		listOfStopWords.add("wikimedia");
		listOfStopWords.add("also");
		listOfStopWords.add("org");
		listOfStopWords.add("here");
		listOfStopWords.add("there");
		listOfStopWords.add("data");
		listOfStopWords.add("that");
		listOfStopWords.add("this");
		listOfStopWords.add("these");
		listOfStopWords.add("those");
		listOfStopWords.add("me");
		listOfStopWords.add("her");
		listOfStopWords.add("his");
		listOfStopWords.add("world");
		listOfStopWords.add("at");
		listOfStopWords.add("was");
		listOfStopWords.add("were");
		listOfStopWords.add("page");
		listOfStopWords.add("new");
		listOfStopWords.add("all");
		listOfStopWords.add("also");
		listOfStopWords.add("public");
		listOfStopWords.add("next");
		listOfStopWords.add("last");
		listOfStopWords.add("book");
		listOfStopWords.add("than");
		listOfStopWords.add("which");
		listOfStopWords.add("when");
		listOfStopWords.add("see");
		listOfStopWords.add("many");
		listOfStopWords.add("has");
		listOfStopWords.add("are");
		listOfStopWords.add("com");
		listOfStopWords.add("or");
		listOfStopWords.add("more");
		listOfStopWords.add("be");
		listOfStopWords.add("its");
		listOfStopWords.add("data");
		listOfStopWords.add("please");
		listOfStopWords.add("http");
		listOfStopWords.add("links");
		listOfStopWords.add("their");
		listOfStopWords.add("page");
		listOfStopWords.add("about");
		listOfStopWords.add("high");
		listOfStopWords.add("must");
		listOfStopWords.add("see");
		listOfStopWords.add("book");
		/*
		 * String[] a = new String[]{"the", "of", "on", "and", "in", "content",
		 * "to", "as", "have","not", "is", "will", "home", "from", "by", "on",
		 * "wikipedia", "for", "was", "site", "this", "contains", "their", "as",
		 * "edit", "string", "with", "there", "page", "his", also, when, org,
		 * here, data, that, wikimedia, me, world, at, video, page, it, powered,
		 * content, than, http, links, work, he, had, article, his, back, many,
		 * state, please, an, free, are, software, after, or, must, january,
		 * cache, centralauth, high, about, be, posted, expires, available, all,
		 * travel, book, also, mail, public, internet, right, retrieved,
		 * private, national, which, media, game, last, en, text, were, store,
		 * new, hotels, search, en, see, changes, has, encyclopedia};
		 */
		return listOfStopWords;
	}

	public void setListOfStopWords(THashSet<String> listOfStopWords) {

		this.listOfStopWords = listOfStopWords;
	}

	public Browser getScrShotBrowser() {
		return scrShots;
	}

	public void setScrShotBrowser(String collectionName, String savePOPUP) {
		this.scrShots = new Browser();
		scrShots.setCollectionName(collectionName);
		scrShots.setSavePoPUPPath(savePOPUP);
		scrShots.setDriver();
	}

	public void stopFXDriver() {
		this.scrShots.stopFXDriver();
		Iterator<Browser> iter = this.detailedPagesBrowsers.keySet().iterator();
		while (iter.hasNext()) {
			((Browser) iter.next()).stopFXDriver();
		}
	}

	public HashMap<Browser, Boolean> getDetailedPageBrowser() {
		return detailedPagesBrowsers;
	}

	public void setDetailedPageBrowsers(int numberOfBrowsers) {
		for (int i = 0; i < numberOfBrowsers; i++) {
			Browser detailedPages = new Browser();
			detailedPages.setDriver();
			this.detailedPagesBrowsers.put(detailedPages, true); // true means
																	// it is
																	// free
		}

	}

	public int getQuerySelectionApproach() {
		return querySelectionApproach;
	}

	public void setQuerySelectionApproach(int querySelectionApproach) {
		this.querySelectionApproach = querySelectionApproach;
		if (querySelectionApproach == this.PredefinedlistOfWords
				|| querySelectionApproach == this.browsing) {
			feedbackBasedApproach = true;
		} else {
			feedbackBasedApproach = true;
		}
	}

	public void saveCrawlStatus(ArrayList<String> listOfReturnedResultsOfSRPage) {
		synchronized (this) {

			this.saveLists(listOfReturnedResultsOfSRPage,
					this.pathToVisitedPagesDoc);
		}
	}

	public void saveCrawlStatus(String crawledPage) {// ,
		// synchronized (this) {

		this.saveStringInFile(crawledPage, this.pathToVisitedPagesDoc, true);
		/*
		 * this.saveStringInFile(crawledPage, this.pathToVisitedPagesPerQuery,
		 * true); this.saveStringInFile(this.currentSRPageURL,
		 * this.pathToLastSearchResultPage, false);
		 * 
		 * this.saveStringInFile(crawledPage, this.pathToLastLinkDoc, false); //
		 * }
		 */}

	public void saveCrawlStatus(int crawledPage, String path) {// ,
		// synchronized (this) {
		this.saveStringInFile(crawledPage + "", path, false);
		// }
	}

	public void saveCrawlStatusCollectionName() {// ,
		this.saveStringInFile(this.currentCollectionName,
				this.pathToCoveredCollections, true);
	}

	public void saveCrawlStatusQuery() {// ,
		if (this.queryIndex == 1) {// To replace if it is repeated for first
									// time
			this.saveStringInFile(query, this.pathToSentQueriesDoc, false);
		} else {
			this.saveStringInFile(query, this.pathToSentQueriesDoc, true);
		}
		this.saveStringInFile("", this.pathToVisitedPagesPerQuery, false);// remove
																			// the
																			// urls
																			// entered
																			// for
																			// previous
																			// query
	}

	public void saveLists(ArrayList<String> list, String filePath) {
		try {
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String element : list) {
				out.write(element + "\n");
				out.flush();
			}
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public void saveListsofQueries(Set<String> list, String filePath) {
		try {
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String element : list) {
				out.write(element + "\n");
				out.flush();
			}
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public void saveStringInFile(String link, String filePath, boolean append) {
		try {
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(link + "\n");
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public void saveStringInFile(String dataPart1, String dataPart2,
			String filePath, boolean append) {
		try {
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(dataPart1 + "\t" + dataPart2 + "\n");
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public ArrayList<String> readLinesFromFile(String path) {
		ArrayList<String> visitedPagesInLastCrawl = new ArrayList<String>();
		try {
			File file = new File(path);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line = "";
			while ((line = in.readLine()) != null) {
				if (line.startsWith("http")) {
					visitedPagesInLastCrawl.add(line.trim());
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return visitedPagesInLastCrawl;
	}

	public int readFirstNumberFromFile(String path) {
		String line = "0";
		try {
			File file = new File(path);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			ArrayList<String> visitedPagesInLastCrawl = new ArrayList<String>();
			line = in.readLine();
			if (line != null) {
				line = line.trim();
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		int foo = Integer.parseInt(line);
		return foo;
	}

	public String readLastPageFromFile(String path) {
		String line = "";
		try {
			File file = new File(path);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			ArrayList<String> visitedPagesInLastCrawl = new ArrayList<String>();
			line = in.readLine();
			if (line != null) {
				line = line.trim();
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return line;
	}

	public void setQueryIndexFromFile() {
		this.queryIndex = this.lastQueryIndex();
		if (queryIndex != 0) {
			this.query = this.queries.get(queryIndex - 1);
		}
	}

	public int lastQueryIndex() {
		String line = "";
		int indexValue = 0;
		ArrayList<String> tempSentQueries = new ArrayList<String>();
		try {
			File file = new File(this.pathToSentQueriesDoc);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			while ((line = in.readLine()) != null) {
				tempSentQueries.add(line.trim());
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		if (!tempSentQueries.isEmpty()) {
			if (this.queries.contains(tempSentQueries.get(tempSentQueries
					.size() - 1))) {
				indexValue = queries.indexOf(tempSentQueries
						.get(tempSentQueries.size() - 1)) + 1;// everytime the
																// index is
																// increased
																// after
																// selecting
																// query. to set
																// it to that
																// situation, +1
																// is done
			}
		} else {
			indexValue = 0;
		}
		return indexValue;
	}

	public String getPathToURLCrawlList() {
		return pathToURLCrawlList;
	}

	public void setPathToURLCrawlList(String pathToURLCrawlList) {
		this.pathToURLCrawlList = pathToURLCrawlList;
	}

	public String getPathToVisitedPagesDoc() {
		return pathToVisitedPagesDoc;
	}

	public void setPathToVisitedPagesDoc(String pathToVisitedPagesDoc) {
		this.pathToVisitedPagesDoc = pathToVisitedPagesDoc;
	}

	public String getPathToSentQueriesDoc() {
		return pathToSentQueriesDoc;
	}

	public void setPathToSentQueriesDoc(String pathToSentQueriesDoc) {
		this.pathToSentQueriesDoc = pathToSentQueriesDoc;
	}

	public String getPathToCoveredCollections() {
		return pathToCoveredCollections;
	}

	public void setPathToCoveredCollections(String pathToCoveredCollections) {
		this.pathToCoveredCollections = pathToCoveredCollections;
	}

	public String getPathToLastLinkDoc() {
		return pathToLastLinkDoc;
	}

	public void setPathToLastLinkDoc(String pathToLastLinkDoc) {
		this.pathToLastLinkDoc = pathToLastLinkDoc;
	}

	public boolean isUnPauseCrawl() {
		return unPauseCrawl;
	}

	public void setUnPauseCrawl(boolean unPauseCrawl) {
		this.unPauseCrawl = unPauseCrawl;
	}

	public void setPathToNumberOfSearchResults(String string) {
		pathToNumberOfSearchResults = string;
	}

	public void setPathToNumberOfSentQueries(String string) {
		// TODO Auto-generated method stub
		pathToNumberOfSentQueries = string;
	}

	public boolean FreeBrowserExists() {
		Iterator<Browser> iter = this.detailedPagesBrowsers.keySet().iterator();
		while (iter.hasNext()) {
			boolean ifFree = this.detailedPagesBrowsers.get((Browser) iter
					.next());
			if (ifFree) {
				return true;
			}
		}
		return false;// non of them is free
	}

	/*
	 * public void waitTillFreeBrowser() { boolean anyFreeBrowser = false; while
	 * (!anyFreeBrowser) { Iterator<Browser> iter =
	 * this.detailedPagesBrowsers.keySet() .iterator(); while (iter.hasNext()) {
	 * boolean ifFree = this.detailedPagesBrowsers.get((Browser) iter .next());
	 * if (ifFree) { anyFreeBrowser = true; } } } }
	 */

	public Browser getFreeBrowser() {
		synchronized (this) {
			Iterator<Browser> iter = detailedPagesBrowsers.keySet().iterator();
			while (iter.hasNext()) {
				Browser tempBrowser = (Browser) iter.next();
				boolean ifFree = detailedPagesBrowsers.get(tempBrowser);
				if (ifFree) {
					detailedPagesBrowsers.put(tempBrowser, false);
					// true means it is free
					return tempBrowser;
				}
			}
			waitTillOneIsFree();
			return getFreeBrowser();
		}
	}

	public void waitTillOneIsFree() {
		boolean oneBrowsersIsFree = false;
		long startTime = System.currentTimeMillis();
		while (!oneBrowsersIsFree) {
			Iterator<Browser> iter = this.detailedPagesBrowsers.keySet()
					.iterator();
			boolean oneBusy = false;
			while (iter.hasNext()) {
				boolean ifFree = this.detailedPagesBrowsers.get((Browser) iter
						.next());
				if (ifFree) {
					oneBusy = true;
					break;
				}
			}
			if (oneBusy == true) {
				oneBrowsersIsFree = true;
			}
		}

	}

	public void freeBrowser(Browser browser) {
		// synchronized(this){
		detailedPagesBrowsers.put(browser, true);// true means it is free
		// }

	}

	public void waitTillAllBrowsersAreFree() {
		boolean allBrowsersAreFree = false;
		while (!allBrowsersAreFree) {
			Iterator<Browser> iter = this.detailedPagesBrowsers.keySet()
					.iterator();
			boolean noBusyBrowser = true;
			while (iter.hasNext()) {
				Browser br = (Browser) iter
						.next();
				boolean ifFree = this.detailedPagesBrowsers.get(br);
				if (!ifFree) {
					noBusyBrowser = false;
					break;
				}
			}
			if (noBusyBrowser == true) {
				allBrowsersAreFree = true;
			}
		}

	}

	public void setTermFreqInClueWeb(String filePath) {
		this.termFreqInClueWeb = new HashMap<String, Integer>();
		try {
			File file = new File(filePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line;
			while ((line = in.readLine()) != null) {
				// String line = in.readLine();
				String[] a = line.split("\t");
				String query = a[0];
				int freq = Integer.parseInt(a[1]);
				// String query = line.replaceAll("[0-9]*", "").trim();
				if (!termFreqInClueWeb.containsKey(query)) {
					termFreqInClueWeb.put(query.intern(), freq);
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void setSaveDSextractedInfoInFile(boolean b) {
		this.SaveDSextractedInfoInFile = b;
	}

	public void printQueryStatistics() {
		// TODO Auto-generated method stub
		if (fbBasedQueryGenerator != null) {
			this.fbBasedQueryGenerator.printQueryStatistics();

		}
	}

}
