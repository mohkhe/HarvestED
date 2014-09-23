package db.infiniti.config;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author mohammad
 *
 */
public class QueryStatisticDS {

	public QueryStatisticDS(String query) {
		super();
		this.query = query;
	}
	
	String query;
	int freqInAllHarvestedResults;
	int freqInAllReturnedDocsForQuery;
	int freqInWeb;
	
	String documentURL;
	ArrayList<Integer> documentsQueryAppearedIn; 
	int documentLengthIfChosenFromOneDoc;
	
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getFreqInAllHarvestedResults() {
		return freqInAllHarvestedResults;
	}
	public void setFreqInAllHarvestedResults(int freqInAllHarvestedResults) {
		this.freqInAllHarvestedResults = freqInAllHarvestedResults;
	}
	public int getFreqInAllReturnedDocsForQuery() {
		return freqInAllReturnedDocsForQuery;
	}
	public void setFreqInAllReturnedDocsForQuery(int freqInAllReturnedDocsForQuery) {
		this.freqInAllReturnedDocsForQuery = freqInAllReturnedDocsForQuery;
	}
	public int getFreqInWeb() {
		return freqInWeb;
	}
	public void setFreqInWeb(int freqInWeb) {
		this.freqInWeb = freqInWeb;
	}
	public String getDocumentURL() {
		return documentURL;
	}
	public void setDocumentURL(String documentURL) {
		this.documentURL = documentURL;
	}
	public ArrayList<Integer> getDocumentsQueryAppearedIn() {
		return documentsQueryAppearedIn;
	}
	public void setDocumentsQueryAppearedIn(
			ArrayList<Integer> documentsQueryAppearedIn) {
		this.documentsQueryAppearedIn = documentsQueryAppearedIn;
	}
	public int getDocumentLengthIfChosenFromOneDoc() {
		return documentLengthIfChosenFromOneDoc;
	}
	public void setDocumentLengthIfChosenFromOneDoc(
			int documentLengthIfChosenFromOneDoc) {
		this.documentLengthIfChosenFromOneDoc = documentLengthIfChosenFromOneDoc;
	}
	
	public void printQueryStatistics() {
/*		Iterator<String> queries = this.sentQueries.iterator();
		while(queries.hasNext()){
			String query = queries.next();
			System.out.println("Query: "+query);
			int generalQueryFreq = generalTermFreqSet.get(query);
			System.out.println("Query general frequency" + generalQueryFreq);
			ArrayList<Integer> docs = termDocumentDistribution.get(query);
			Iterator e = docs.iterator();
			System.out.println("Query appeared in these documents");
			while(e.hasNext()){
				System.out.print(e.next()+",");
			}
		}*/
	}
	
}
