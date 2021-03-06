package db.infiniti.harvester.modules.querygenerator;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;

import db.infiniti.config.QueryStatisticDS;
import db.infiniti.config.TextEditor;
import db.infiniti.harvester.modules.common.Cache;

public class FeedbackBasedQueryGenerator {

	HashMap<String, QueryStatisticDS> queriesStatMap = new HashMap<String, QueryStatisticDS>();

	// HashMap<String, Integer> generalTermFreqSet = new HashMap<String,
	// Integer>();
	TObjectIntHashMap<String> generalTermFreqSet = new TObjectIntHashMap<String>();
	HashMap<Integer, HashMap<String, Integer>> docNumberTermsFrequencies = new HashMap<Integer, HashMap<String, Integer>>();
	HashMap<Integer, HashMap<String, Integer>> previousQueryDocNumberTermsFrequencies = new HashMap<Integer, HashMap<String, Integer>>();

	public TextEditor textEditor = new TextEditor();
	HashMap<String, ArrayList<Integer>> termDocumentDistribution = new HashMap<String, ArrayList<Integer>>();
	// String initialQuery = "";// it should be set
	public ArrayList<String> sentQueries = new ArrayList<String>();

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
							generalTermFreqSet.put(token.intern(),
									generalTermFreqSet.get(token)
											+ docTermFreqSet.get(token));
						} else {
							generalTermFreqSet.put(token.intern(),
									docTermFreqSet.get(token));

						}

						if (termDocumentDistribution.containsKey(token)) {
							termDocumentDistribution.get(token).add(
									searchResultReturnedNumber);
							termDocumentDistribution.get(token).trimToSize();
						} else {
							ArrayList<Integer> tempArray = new ArrayList<Integer>();
							tempArray.add(searchResultReturnedNumber);
							tempArray.trimToSize();
							termDocumentDistribution.put(token.intern(),
									tempArray);
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

	public String setNextQueryLeastFromLast(List<String> initialQuery,
			boolean isIndexed, List<String> listOfReturnedResultsPerQuery,
			Cache cache) {
		String nextQuery = null;
		int temp = 0;
		int lastPage = 0;
		Iterator<Integer> docNum;
		if (isIndexed) {
			// String textOfLastPage = "";
			String query = "";
			if(listOfReturnedResultsPerQuery == null){
				return null;
			}
			int index = listOfReturnedResultsPerQuery.size();
			boolean foundLastPageInQueryResultsPresentInCache = false;
			boolean foundQuery = false;
			while (!foundLastPageInQueryResultsPresentInCache && !foundQuery) {
				if (index > 0) {
					index = index - 1;
					foundLastPageInQueryResultsPresentInCache = cache.indexNew
							.searchIndex(
									listOfReturnedResultsPerQuery.get(index),
									"url");

				} else {
					System.out.println("no last page to get a query.");
					break;
				}
				if (foundLastPageInQueryResultsPresentInCache) {
					TermFreqVector termFreqDoc = cache.indexNew
							.searchIndexReturnFreqTerms(
									listOfReturnedResultsPerQuery.get(index),
									"url");
					if(termFreqDoc!= null && termFreqDoc.size()>0){
						query = leastFreqInTermFreqVector(termFreqDoc);
						if (!query.equalsIgnoreCase("") && query != null) {
							foundQuery = true;
							return query;
						}else{
							foundLastPageInQueryResultsPresentInCache = false;//this was empty, try aniother one
						}
					}
				}
			}

		} else {
			if (!docNumberTermsFrequencies.isEmpty()) {// no results for query
				previousQueryDocNumberTermsFrequencies.clear();
				previousQueryDocNumberTermsFrequencies
						.putAll(docNumberTermsFrequencies);

			}
			if (!previousQueryDocNumberTermsFrequencies.isEmpty()) {
				docNum = previousQueryDocNumberTermsFrequencies.keySet()
						.iterator();
				while (docNum.hasNext()) {
					temp = docNum.next();
					if (temp > lastPage) {
						lastPage = temp;
					}
				}
				while (nextQuery == null) {
					if (lastPage > 0) {
						HashMap<String, Integer> termFreqSet = previousQueryDocNumberTermsFrequencies
								.get(lastPage);
						nextQuery = textEditor.getLeastFreqQuery(termFreqSet,
								initialQuery, sentQueries);
						lastPage = lastPage - 1;
					}
				}
				QueryStatisticDS queryStat = new QueryStatisticDS(nextQuery);
				queryStat.setDocumentsQueryAppearedIn(termDocumentDistribution
						.get(nextQuery));
				queriesStatMap.put(nextQuery.intern(), queryStat);

				docNumberTermsFrequencies.clear();
				termDocumentDistribution.clear();
				sentQueries.add(nextQuery);
				return nextQuery;
			}
		}
		return null;
	}

	private String leastFreqInTermFreqVector(TermFreqVector termFreqDoc) {
		HashMap<String, Integer> totalTfv = new HashMap<String, Integer>(1024);
		if (termFreqDoc == null) {
		}
		String terms[] = termFreqDoc.getTerms();
		int termCount = terms.length;
		int freqs[] = termFreqDoc.getTermFrequencies();

		for (int t = 0; t < termCount; t++) {
			String term = terms[t];
			if (!textEditor.isRefinedQueryStopWordLength(term)) {
				int freq = freqs[t];
				Integer totalFreq = totalTfv.get(term);
				totalFreq = (totalFreq == null) ? freq : freq + totalFreq;
				totalTfv.put(term, totalFreq);
			}
		}

		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(
				totalTfv.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> e1,
					Entry<String, Integer> e2) {
				int v1 = e1.getValue(); // can be NPE when autounboxing
				int v2 = e2.getValue();
				return (v1 < v2) ? 1 : (v1 == v2) ? 0 : -1;
			}
		});
		String query = "";
		int i = entries.size();
		while (query.equalsIgnoreCase("") && i > 0) {
			i = i - 1;
			String term = entries.get(i).getKey();
			if (!sentQueries.contains(term)) {
				sentQueries.add(term.intern());
				sentQueries.trimToSize();
				query = entries.get(i).getKey();
			}
		}
	//	int freq = entries.get(i).getValue();
		return query;
	}
	public String setNextQueryIn_Correlation(TObjectIntHashMap<String> querySet,
			List<String> initialQuery, boolean isIndexed, Cache cache, int specificFreq, boolean versionOld) {
		if (!isIndexed) {
			List<String> mapKeys = new ArrayList<String>(querySet.keySet());
			// List<Integer> mapValues = new
			// ArrayList<Integer>(querySet.values());
			int[] mapValues = querySet.values();
			// Collections.sort(mapValues, Collections.reverseOrder());

			// Iterator<Integer> valueIt = mapValues.iterator();
			// TODO check if this is the most frequent and not the least
			// frequent

			int val = Integer.MIN_VALUE;
			for (int j : mapValues) {
				if (j > val) {
					val = j;
				}
			}

			// int val = (Integer) valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int comp1 = querySet.get(key);

				if (comp1 == val && !initialQuery.contains(key)) {
					sentQueries.add(key);
					sentQueries.trimToSize();
					QueryStatisticDS queryStat = new QueryStatisticDS(key);
					queryStat.setFreqInAllReturnedDocsForQuery(val);
					queryStat
							.setDocumentsQueryAppearedIn(termDocumentDistribution
									.get(key));
					queriesStatMap.put(key.intern(), queryStat);

					return key;
				}
			}
		} else {
	
			String specificFreqQuery = cache.indexNew.getSpecificFreqTermInIndex(10,
					sentQueries, specificFreq, false, versionOld); // versionOld  
			sentQueries.add(specificFreqQuery);
			sentQueries.trimToSize();
			return specificFreqQuery;
		}
		return null;
	}
	//TODO performance can be improved. remove sent from list of words
	public String setNextQueryIn_Feedback_ClueWeb(List<String> initialQuery,
			Set<String> queryArray,
			HashMap<String, Integer> termFreqInClueWeb2, boolean isIndexed,
			Cache cache) {

		Iterator<String> termInClueWeb = termFreqInClueWeb2.keySet().iterator();
		// TODO check if this is the most frequent and not the least frequent
		while (termInClueWeb.hasNext()) {
			String term = termInClueWeb.next();
			if (isIndexed) {
				if (!initialQuery.contains(term) && !sentQueries.contains(term)) {
					if (cache.contains(term)) {
						sentQueries.add(term.intern());
						sentQueries.trimToSize();
						/*
						 * QueryStatisticDS queryStat = new
						 * QueryStatisticDS(term); //
						 * queryStat.setFreqInAllReturnedDocsForQuery
						 * (querySet.get(term));
						 * queryStat.setDocumentsQueryAppearedIn
						 * (termDocumentDistribution .get(term));
						 * queriesStatMap.put(term.intern(), queryStat);
						 */

						return term;
					}
				}
			} else {

				if (!initialQuery.contains(term)
						&& !this.generalTermFreqSet.contains(term)
						&& !sentQueries.contains(term)) {
					sentQueries.add(term.intern());
					sentQueries.trimToSize();
					QueryStatisticDS queryStat = new QueryStatisticDS(term);
					// queryStat.setFreqInAllReturnedDocsForQuery(querySet.get(term));
					queryStat
							.setDocumentsQueryAppearedIn(termDocumentDistribution
									.get(term));
					queriesStatMap.put(term.intern(), queryStat);

					return term;
				}
			}
		}

		return null;
	}

	public String getMostFreqQuery(TObjectIntHashMap<String> querySet,
			List<String> initialQuery, boolean isIndexed, Cache cache) {

		if (!isIndexed) {
			List<String> mapKeys = new ArrayList<String>(querySet.keySet());
			// List<Integer> mapValues = new
			// ArrayList<Integer>(querySet.values());
			int[] mapValues = querySet.values();
			// Collections.sort(mapValues, Collections.reverseOrder());

			// Iterator<Integer> valueIt = mapValues.iterator();
			// TODO check if this is the most frequent and not the least
			// frequent

			int val = Integer.MIN_VALUE;
			for (int j : mapValues) {
				if (j > val) {
					val = j;
				}
			}

			// int val = (Integer) valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int comp1 = querySet.get(key);

				if (comp1 == val && !initialQuery.contains(key)) {
					sentQueries.add(key);
					sentQueries.trimToSize();
					QueryStatisticDS queryStat = new QueryStatisticDS(key);
					queryStat.setFreqInAllReturnedDocsForQuery(val);
					queryStat
							.setDocumentsQueryAppearedIn(termDocumentDistribution
									.get(key));
					queriesStatMap.put(key.intern(), queryStat);

					return key;
				}
			}
		} else {
			String mostFreqQuery = cache.indexNew.getMostFreqTermInIndex(10,
					sentQueries, initialQuery);
			sentQueries.add(mostFreqQuery);
			sentQueries.trimToSize();
			return mostFreqQuery;
		}
		return null;
	}

	public String getLeastFreqQuery(TObjectIntHashMap<String> querySet,
			List<String> initialQuery, boolean isIndexed, Cache cache) {
		if (!isIndexed) {
			if (querySet == null) {
				return null;
			} else if (querySet.isEmpty()) {
				return null;
			}
			List<String> mapKeys = new ArrayList<String>(querySet.keySet());
			int[] mapValues = querySet.values();
			/*
			 * if (!mapValues.contains(1)){ Collections.sort(mapValues); while
			 * (mapValues.contains(0)) { mapValues.remove(0); }
			 * Iterator<Integer> valueIt = mapValues.iterator();
			 * 
			 * // TODO check if this is the most frequent and not the least
			 * frequent val = (Integer) valueIt.next(); }else{ val = 1; }
			 */
			int val = Integer.MAX_VALUE;
			int i = 0;
			for (int j : mapValues) {
				if (j < val && j != 0) {
					val = j;
					i++;
				}
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
					queryStat
							.setDocumentsQueryAppearedIn(termDocumentDistribution
									.get(key));
					queriesStatMap.put(key.intern(), queryStat);

					return key;
				}
			}
		} else {
			String leastFreqQuery = cache.indexNew.getLeastFreqTermInIndex(20,
					sentQueries);
			sentQueries.add(leastFreqQuery);
			sentQueries.trimToSize();
			return leastFreqQuery;
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
			if (queryStat != null) {
				ArrayList<Integer> docs = queryStat
						.getDocumentsQueryAppearedIn();
				if (docs != null) {
					if (docs.isEmpty()) {
						System.out.print("No docs problem");

					} else {
						Iterator e = docs.iterator();
						System.out.println("Query appeared in these documents");
						while (e.hasNext()) {
							System.out.print(e.next() + ",");
						}
					}
				}
			}

		}
	}
}
