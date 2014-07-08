package db.infiniti.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CrawlingReportDS {

	private int countCrawlingQueries;
	private LinkedHashMap<String, QueryResStatistics> queryNumberofItsResults;
	private int numRepeatedLinksInThisQuery;
	private int numRepeatedLinksInGeneral;
	List<Integer> numRepeatedLinksInQueryList = new ArrayList<Integer>();
	
	public CrawlingReportDS() {
		super();
		this.countCrawlingQueries = 0;
		numRepeatedLinksInQueryList.add(0);
		this.queryNumberofItsResults = new LinkedHashMap<String, QueryResStatistics>();
	}


	public int getCountCrawlingQueries() {
		return countCrawlingQueries;
	}

	public void setCountCrawlingQueries(int countCrawlingQueries) {
		this.countCrawlingQueries = countCrawlingQueries;
	}

	public LinkedHashMap<String, QueryResStatistics> getQueryNumberofItsResults() {
		return queryNumberofItsResults;
	}

	public void setQueryNumberofItsResults(LinkedHashMap<String, QueryResStatistics> queryNumberofItsResults) {
		this.queryNumberofItsResults = queryNumberofItsResults;
	}
	
	public void addQueryNumberofItsResults(String query, int qPosedIndex, int uniqResults, int RepeatedResultsLocally, int RepeatedResultsGeneral, int totalResultsEachQuery){
		
		this.queryNumberofItsResults.put(query, new QueryResStatistics(qPosedIndex, uniqResults, RepeatedResultsLocally, RepeatedResultsGeneral, totalResultsEachQuery));
	}
	
	public void incNoCrawlingQueries() {
		this.countCrawlingQueries = this.countCrawlingQueries+1;
	}

	public int getNumRepeatedLinksInGeneral() {
		return numRepeatedLinksInGeneral;
	}

	public void setNumRepeatedLinksInGeneral(int numRepeatedLinks) {
		this.numRepeatedLinksInGeneral = numRepeatedLinks;
	}

	public void incNumRepeatedLinksInGeneral() {
		this.numRepeatedLinksInGeneral = this.numRepeatedLinksInGeneral+1;
	}
	
	public int getNumRepeatedLinks() {
		return numRepeatedLinksInQueryList.get(0);
	}

	public void setNumRepeatedLinks(int numRepeatedLinks) {
		numRepeatedLinksInThisQuery = numRepeatedLinks;
		numRepeatedLinksInQueryList.set(0, 0);
	}

	public void incNoRepeatedLinks() {
		synchronized(numRepeatedLinksInQueryList){
			numRepeatedLinksInThisQuery = numRepeatedLinksInThisQuery+1;
			numRepeatedLinksInQueryList.set(0, numRepeatedLinksInThisQuery);
		}
	}
}
