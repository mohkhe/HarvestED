package db.infiniti.harvester.modules.querygenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import db.infiniti.config.QueryStatisticDS;
import db.infiniti.config.TextEditor;

public class FeedbackBasedQueryGenerator {

	HashMap<String, QueryStatisticDS> queriesStatMap = new HashMap<String, QueryStatisticDS>();

	HashMap<String, Integer> generalTermFreqSet = new HashMap<String, Integer>();
	HashMap<Integer, HashMap<String, Integer>> docNumberTermsFrequencies = new HashMap<Integer, HashMap<String, Integer>>();
	HashMap<Integer, HashMap<String, Integer>> previousQueryDocNumberTermsFrequencies = new HashMap<Integer, HashMap<String, Integer>>();

	HashMap<String, ArrayList<Integer>> termDocumentDistribution = new HashMap<String, ArrayList<Integer>>();
	//String initialQuery = "";// it should be set
	ArrayList<String> sentQueries = new ArrayList<String>();
	TextEditor textEditor = new TextEditor();

	public void prepareQuerySelection(int searchResultReturnedNumber,
			String searchResultPageContent) {

		if (!searchResultPageContent.equalsIgnoreCase("error")) {
			HashMap<String, Integer> docTermFreqSet = new HashMap<String, Integer>();
			String refinedText = textEditor.refineText(searchResultPageContent,
					true);
			docTermFreqSet = textEditor.setTermFreq(refinedText);
			if (docTermFreqSet != null && !docTermFreqSet.isEmpty()) {
				docNumberTermsFrequencies.put(searchResultReturnedNumber,
						docTermFreqSet);
			}
			Iterator<String> termsIer = docTermFreqSet.keySet().iterator();
			while (termsIer.hasNext()) {
				String token = (String) termsIer.next();
				synchronized (generalTermFreqSet) {
					if (!sentQueries.contains(token)) {
						if (generalTermFreqSet.containsKey(token)) {
							generalTermFreqSet.put(token,
									generalTermFreqSet.get(token)
											+ docTermFreqSet.get(token));
						} else {
							generalTermFreqSet.put(token,
									docTermFreqSet.get(token));

						}

						if (termDocumentDistribution.containsKey(token)) {
							termDocumentDistribution.get(token).add(
									searchResultReturnedNumber);
						} else {
							ArrayList<Integer> tempArray = new ArrayList<Integer>();
							tempArray.add(searchResultReturnedNumber);
							termDocumentDistribution.put(token, tempArray);
						}
					}
				}
			}
		}
		/*
		 * if(theLastPageNumber<searchResultReturnedNumber){ theLastPageNumber =
		 * searchResultReturnedNumber; }
		 */
	}

	public String setNextQueryLeastFromLast(List<String> initialQuery) {
		String nextQuery = null;
		int temp = 0;
		int lastPage = 0;
		Iterator<Integer> docNum;
		if (!docNumberTermsFrequencies.isEmpty()) {// no results for query
			previousQueryDocNumberTermsFrequencies.clear();
			previousQueryDocNumberTermsFrequencies.putAll(docNumberTermsFrequencies);

		}
		if(!previousQueryDocNumberTermsFrequencies.isEmpty()){
			docNum = previousQueryDocNumberTermsFrequencies.keySet().iterator();
			while (docNum.hasNext()) {
				temp = docNum.next();
				if (temp > lastPage) {
					lastPage = temp;
				}
			}
			while(nextQuery == null){
				if(lastPage>0){
					HashMap<String, Integer> termFreqSet = previousQueryDocNumberTermsFrequencies
							.get(lastPage);
					nextQuery = textEditor.getLeastFreqQuery(termFreqSet,
							initialQuery, sentQueries);
					lastPage = lastPage-1;
				}
			}
			QueryStatisticDS queryStat = new QueryStatisticDS(nextQuery);
			queryStat.setDocumentsQueryAppearedIn(termDocumentDistribution
					.get(nextQuery));
			queriesStatMap.put(nextQuery, queryStat);

			docNumberTermsFrequencies.clear();
			termDocumentDistribution.clear();
			sentQueries.add(nextQuery);
			return nextQuery;
		}
		return null;
	}

	public String setNextQueryIn_Feedback_ClueWeb(List<String> initialQuery, HashMap<String, Integer> querySet, HashMap<String, Integer> termFreqInClueWeb2) {

		Iterator<String> termInClueWeb = termFreqInClueWeb2.keySet().iterator();
		// TODO check if this is the most frequent and not the least frequent
		while (termInClueWeb.hasNext()) {
			String term = termInClueWeb.next();
			if (!initialQuery.contains(term) && querySet.containsKey(term) && !sentQueries.contains(term)) {
				sentQueries.add(term);
				QueryStatisticDS queryStat = new QueryStatisticDS(term);
				queryStat.setFreqInAllReturnedDocsForQuery(querySet.get(term));
				queryStat.setDocumentsQueryAppearedIn(termDocumentDistribution
						.get(term));
				queriesStatMap.put(term, queryStat);

				return term;
			}
		}

		return null;
	}
	
	public String getMostFreqQuery(HashMap<String, Integer> querySet, List<String> initialQuery) {

		List<String> mapKeys = new ArrayList<String>(querySet.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(querySet.values());
		Collections.sort(mapValues, Collections.reverseOrder());

		Iterator<Integer> valueIt = mapValues.iterator();
		// TODO check if this is the most frequent and not the least frequent
		int val = (Integer) valueIt.next();
		Iterator<String> keyIt = mapKeys.iterator();
		while (keyIt.hasNext()) {
			String key = (String) keyIt.next();
			int comp1 = querySet.get(key);

			if (comp1 == val && !initialQuery.contains(key)) {
				sentQueries.add(key);

				QueryStatisticDS queryStat = new QueryStatisticDS(key);
				queryStat.setFreqInAllReturnedDocsForQuery(val);
				queryStat.setDocumentsQueryAppearedIn(termDocumentDistribution
						.get(key));
				queriesStatMap.put(key, queryStat);

				return key;
			}
		}

		return null;
	}

	public String getLeastFreqQuery(HashMap<String, Integer> querySet, List<String> initialQuery) {

		if(querySet == null){
			return null;
		}else if (querySet.isEmpty()){
			return null;
		}
		List<String> mapKeys = new ArrayList<String>(querySet.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(querySet.values());
		int val;
		if (!mapValues.contains(1)){
			Collections.sort(mapValues);
			while (mapValues.contains(0)) {
				mapValues.remove(0);
			}
			Iterator<Integer> valueIt = mapValues.iterator();
	
			// TODO check if this is the most frequent and not the least frequent
			val = (Integer) valueIt.next();
		}else{
			val = 1;
		}
		// set the lowest frequency
		Iterator<String> keyIt = mapKeys.iterator();
		while (keyIt.hasNext()) { // we set value to 0 not to use it later
			String key = (String) keyIt.next();
			int comp1 = querySet.get(key);

			if (comp1 == val && comp1 != 0 && !initialQuery.contains(key)) {
				sentQueries.add(key);
				QueryStatisticDS queryStat = new QueryStatisticDS(key);
				queryStat.setFreqInAllReturnedDocsForQuery(val);
				queryStat.setDocumentsQueryAppearedIn(termDocumentDistribution
						.get(key));
				queriesStatMap.put(key, queryStat);

				return key;
			}
		}

		return null;
	}

	public HashMap<String, Integer> ranking() {

		return null;
	}

	public void printQueryStatistics() {

		Iterator<String> queries = this.sentQueries.iterator();
		while (queries.hasNext()) {
			String query = queries.next().toLowerCase().trim();
			QueryStatisticDS queryStat = this.queriesStatMap.get(query);
			System.out.println("Query: " + query);
			if (generalTermFreqSet.containsKey(query)) {
				int generalQueryFreq = generalTermFreqSet.get(query);
				System.out
						.println("Query general frequency" + generalQueryFreq);
			} else {
				System.out.println("Query general frequency not present");
			}
			ArrayList<Integer> docs = queryStat.getDocumentsQueryAppearedIn();
			if(docs.isEmpty()){
				System.out.print("No docs problem");

			}else{
				Iterator e = docs.iterator();
				System.out.println("Query appeared in these documents");
				while (e.hasNext()) {
					System.out.print(e.next() + ",");
				}
			}
			
		}
	}
}
