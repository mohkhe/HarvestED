package db.infiniti.config;

public class QueryResStatistics {

	int qPosedIndex;
	int uniqResults;
	int RepeatedResultsLocally;
	int RepeatedResultsGeneral;
	int totalResultsEachQuery; 
	
	QueryResStatistics(int qPosedIndex,	int uniqResults,	int RepeatedResults, int RepeatedResultsGeneral, int totalResultsEachQuery){
		this.qPosedIndex = qPosedIndex;
		this.uniqResults = uniqResults;
		this.RepeatedResultsLocally = RepeatedResults;
		this.RepeatedResultsGeneral = RepeatedResultsGeneral;
		this.totalResultsEachQuery = totalResultsEachQuery;
	}

	public int getqPosedIndex() {
		return qPosedIndex;
	}

	public void setqPosedIndex(int qPosedIndex) {
		this.qPosedIndex = qPosedIndex;
	}

	public int getUniqResults() {
		return uniqResults;
	}

	public void setUniqResults(int uniqResults) {
		this.uniqResults = uniqResults;
	}

	public int getRepeatedResults() {
		return RepeatedResultsLocally;
	}

	public void setRepeatedResultsGeneral(int repeatedResults) {
		RepeatedResultsGeneral = repeatedResults;
	}
	public int getRepeatedResultsGeneral() {
		return RepeatedResultsGeneral;
	}

	public void setRepeatedResults(int repeatedResults) {
		RepeatedResultsLocally = repeatedResults;
	}
	public int gettotalResultsEachQuery() {
		return totalResultsEachQuery;
	}

	public void settotalResultsEachQuery(int repeatedResults) {
		totalResultsEachQuery = repeatedResults;
	}
	
}
