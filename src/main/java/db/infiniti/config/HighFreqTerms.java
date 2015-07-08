package db.infiniti.config;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.util.PriorityQueue;

/**
 * <code>HighFreqTerms class extracts the top n most frequent terms
 * (by document frequency ) from an existing Lucene index and reports their
 * document frequency.  If used with the -t flag it also reports their 
 * total tf (total number of occurences) in order of highest total tf
 */

public class HighFreqTerms {

	// The top numTerms will be displayed
	public static final int DEFAULTnumTerms = 1;
	// public int numTerms = DEFAULTnumTerms;
	// HashSet<String> listOfStopWords;

	ArrayList<String> listURLsNotDownloadedButInCache = new ArrayList<String>();

	TextEditor textEditor = new TextEditor();

	public String HighFreqTerms(Directory indexDirectory,
			StandardAnalyzer analyzer, IndexReader reader, int numTerms,
			ArrayList<String> sentQueries, List<String> initialQuery) {
		// indexDirectory = FSDirectory.getDirectory("index/pages");
		// indexDirectory.setLockFactory(new
		// NativeFSLockFactory("index/pages"));
		// w = new IndexWriter(indexDirectory, analyzer,
		// IndexWriter.MaxFieldLength.UNLIMITED);
		String field = "text";
		boolean IncludeTermFreqs = false;
		// setListOfStopWords();
		String mostFreqTerm = "";
		try {
			reader = IndexReader.open(indexDirectory, true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TermStats[] terms = null;
		try {
			terms = getHighFreqTerms(reader, numTerms, field, sentQueries, initialQuery);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Insert logic so it will only lookup totaltf if right arg also change
		 * names as in flex
		 */
		if (terms != null) {
			// default HighFreqTerms behavior

			TermStats[] termsWithTF = null;
			try {
				termsWithTF = sortByTotalTermFreq(reader, terms);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mostFreqTerm = termsWithTF[0].term + "";
			if (mostFreqTerm.contains(":")) {
				mostFreqTerm = mostFreqTerm.substring(
						mostFreqTerm.indexOf(":") + 1, mostFreqTerm.length());
			}
		}
		return mostFreqTerm;
	}

	public String LowFreqTerms(Directory indexDirectory,
			StandardAnalyzer analyzer, IndexReader reader, int numTerms,
			ArrayList<String> sentQueries) {
		// indexDirectory = FSDirectory.getDirectory("index/pages");
		// indexDirectory.setLockFactory(new
		// NativeFSLockFactory("index/pages"));
		// w = new IndexWriter(indexDirectory, analyzer,
		// IndexWriter.MaxFieldLength.UNLIMITED);
		String field = "text";
		boolean IncludeTermFreqs = false;
		// setListOfStopWords();
		String leasttFreqTerm = "";
		try {
			reader = IndexReader.open(indexDirectory, true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TermStats[] terms = null;
		try {
			terms = getLowFreqTerms(reader, numTerms, field, sentQueries, sentQueries);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Insert logic so it will only lookup totaltf if right arg also change
		 * names as in flex
		 */
		if (terms != null) {
			// default HighFreqTerms behavior

			TermStats[] termsWithTF = null;
			try {
				termsWithTF = sortByTotalTermFreq(reader, terms);// sortAscendingByTotalTermFreq
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			leasttFreqTerm = termsWithTF[0].term + "";
			if (leasttFreqTerm.contains(":")) {
				leasttFreqTerm = leasttFreqTerm.substring(
						leasttFreqTerm.indexOf(":") + 1,
						leasttFreqTerm.length());
			}
		}
		return leasttFreqTerm;
	}

	public String SpecificFreqTerms(Directory indexDirectory,
			StandardAnalyzer analyzer, IndexReader reader, int numTerms,
			ArrayList<String> sentQueries, int specificFrec, boolean allranges, boolean versionOld) {
		// indexDirectory = FSDirectory.getDirectory("index/pages");
		// indexDirectory.setLockFactory(new
		// NativeFSLockFactory("index/pages"));
		// w = new IndexWriter(indexDirectory, analyzer,
		// IndexWriter.MaxFieldLength.UNLIMITED);
		String field = "text";
		boolean IncludeTermFreqs = false;
		// setListOfStopWords();
		String specificFreqTerm = "";
		try {
			reader = IndexReader.open(indexDirectory, true);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TermStats[] terms = null;
		try {
			terms = getLowerHigherEqualSpecificFreqTerms(reader, numTerms,
					field, sentQueries, specificFrec, allranges);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (terms == null || terms.length == 0) {
			try {
				terms = getLowerHigherEqualSpecificFreqTerms(reader, numTerms,
						field, sentQueries, specificFrec, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * Insert logic so it will only lookup totaltf if right arg also change
		 * names as in flex
		 */
		if (terms != null && terms.length > 0) {
			// default HighFreqTerms behavior
		//	boolean selectTermMethod = false;
			if (versionOld) {
				TermStats[] termsWithTF = null;
				try {
					termsWithTF = sortByTotalTermFreq(reader, terms);// sortAscendingByTotalTermFreq
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (termsWithTF.length > 0) {
					specificFreqTerm = termsWithTF[0].term + "";
					int i = 0;
					while (i < termsWithTF.length) {
						System.out.println(termsWithTF[i].term + " -> \t"
								+ termsWithTF[i].docFreq);
						i++;
					}
				} else {
					System.out.println("no specificFreqTerm.");
				}

				if (specificFreqTerm.contains(":")) {
					specificFreqTerm = specificFreqTerm.substring(
							specificFreqTerm.indexOf(":") + 1,
							specificFreqTerm.length());
				}
			} else {//version1 - correlationbased
				String term = getTheBestMatchingTerm(reader, terms, sentQueries, sentQueries);
				specificFreqTerm = term;
			}
		}
		return specificFreqTerm;
	}

	private String getTheBestMatchingTerm(IndexReader indexReader,
			TermStats[] terms, ArrayList<String> sentQueries, ArrayList<String> initialQuery) {
		String specificFreqTerm = "";
		if(sentQueries.size()==0 && terms.length>0){
			String temp = terms[0].term + "";
			temp = temp.substring(temp.indexOf(":") + 1,
					temp.length());	
			return temp;
		}
		
		HashMap<String, Integer> averageOfEach = new HashMap<String, Integer>();
		for (int i = 0; i < terms.length; i++) {
			String temp = terms[i].term + "";
			String queryATerm = temp.substring(temp.indexOf(":") + 1,
					temp.length());
			try {
				// indexReader = IndexReader.open(indexDirectory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				// int n = w.numDocs();
				BooleanQuery bq = new BooleanQuery();
				int totalNumberOfResults = 0;
				// double average = 0;
				for (String queryB : sentQueries) {
					Term term1 = new Term("text" , queryATerm);
					TermQuery query1 = new TermQuery(term1);
					Term term2 = new Term("text", queryB);
					TermQuery query2 = new TermQuery(term2);

					bq.add(query1, BooleanClause.Occur.MUST);
					bq.add(query2, BooleanClause.Occur.MUST);
					int numberOfResults = 0;
					HitCollector results;
					int n = indexReader.maxDoc();
					TopDocs hits = indexSearcher.search(bq, n);

				/*	if (hits.totalHits > 0 ){
						System.out.println();
					}*/
					totalNumberOfResults = totalNumberOfResults
							+ hits.totalHits;
				}
				averageOfEach.put(queryATerm, totalNumberOfResults);
				// average = (double) totalNumberOfResults / (double)
				// sentQueries.size();

			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Set<Entry<String, Integer>> set = averageOfEach.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
				set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		System.out.println("sorted by relevance to previous submitted queries: ");
		for (Map.Entry<String, Integer> entry : list) {
			System.out.println(entry.getKey() + " ==== " + entry.getValue());
		}
		int i = list.size()-1;
		while(i>=0){
			specificFreqTerm = list.get(i).getKey();
			if(!initialQuery.contains(specificFreqTerm)){
				return specificFreqTerm;
			}else{
				i--;
			}
		}
		
		return specificFreqTerm;
	}

	private TermStats[] getLowerHigherEqualSpecificFreqTerms(
			IndexReader reader, int numTerms, String field,
			ArrayList<String> sentQueries, int specificFreq, boolean allranges)
			throws Exception {
		// TODO Auto-generated method stub
		TermInfoLowerFreqThanX tiqLower = new TermInfoLowerFreqThanX(numTerms,
				specificFreq);
		TermInfoHigherFreqThanX tiqHigher = new TermInfoHigherFreqThanX(
				numTerms, specificFreq);
		TermInfoEqualFreqThanX tiqEqual = new TermInfoEqualFreqThanX(numTerms,
				specificFreq);
		if (field != null) {
			Document aDoc = reader.document(0);
			// reader.getTermFreqVector(0, field);
			TermEnum terms = reader.terms(new Term(field));

			do {
				if (terms != null && terms.term() != null) {
					String textOfTerm = terms.term().text();

					if (terms.term().field().equals(field)) {
						if (!textEditor
								.isRefinedQueryStopWordLength(textOfTerm)
								&& !sentQueries.contains(textOfTerm)) {
							tiqLower.insertWithOverflow(new TermStats(terms
									.term(), terms.docFreq()));
							tiqHigher.insertWithOverflow(new TermStats(terms
									.term(), terms.docFreq()));
							if (terms.docFreq() == specificFreq) {
								tiqEqual.insertWithOverflow(new TermStats(terms
										.term(), terms.docFreq()));
							}
						}
					}
				}
			} while (terms.next());

		} else {
			TermEnum terms = reader.terms();
			while (terms.next()) {
				String textOfTerm = terms.term().text();
				if (!textEditor.isRefinedQueryStopWordLength(textOfTerm)
						&& !sentQueries.contains(textOfTerm)) {
					tiqLower.insertWithOverflow(new TermStats(terms.term(),
							terms.docFreq()));
					tiqHigher.insertWithOverflow(new TermStats(terms.term(),
							terms.docFreq()));
					if (terms.docFreq() == specificFreq) {
						tiqEqual.insertWithOverflow(new TermStats(terms.term(),
								terms.docFreq()));
					}
				}
			}
		}
		TermStats[] result;
		int count;
		if (allranges) {
			result = new TermStats[tiqLower.size() + tiqHigher.size()
					+ tiqEqual.size()];
			count = tiqHigher.size() - 1 + tiqLower.size() + tiqEqual.size();
			while (tiqHigher.size() != 0) {
				result[count] = (TermStats) tiqHigher.pop();
				count--;
			}
			count = tiqLower.size() + tiqEqual.size() - 1;
			while (tiqEqual.size() != 0) {
				result[count] = (TermStats) tiqEqual.pop();
				count--;
			}
			count = tiqLower.size() - 1;
			while (tiqLower.size() != 0) {
				result[count] = (TermStats) tiqLower.pop();
				count--;
			}
		} else {
			result = new TermStats[tiqEqual.size()];
			count = tiqEqual.size() - 1;
			while (tiqEqual.size() != 0) {
				result[count] = (TermStats) tiqEqual.pop();
				count--;
			}
		}

		// we want highest first so we read the queue and populate the array
		// starting at the end and work backwards

		return result;
	}

	/**
	 * 
	 * @param reader
	 * @param numTerms
	 * @param field
	 * @param sentQueries
	 * @param initialQuery 
	 * @return TermStats[] ordered by terms with highest docFreq first.
	 * @throws Exception
	 */
	public TermStats[] getHighFreqTerms(IndexReader reader, int numTerms,
			String field, ArrayList<String> sentQueries, List<String> initialQuery) throws Exception {

		TermInfoWiTFQueueForHighFreq tiq = new TermInfoWiTFQueueForHighFreq(
				numTerms);
		if (field != null) {
			Document aDoc = reader.document(0);
			// reader.getTermFreqVector(0, field);
			TermEnum terms = reader.terms(new Term(field));

			do {
				if (terms != null && terms.term() != null) {
					String textOfTerm = terms.term().text();

					if (terms.term().field().equals(field)) {
						if (!textEditor
								.isRefinedQueryStopWordLength(textOfTerm)
								&& !sentQueries.contains(textOfTerm) && !initialQuery.contains(textOfTerm)) {
							tiq.insertWithOverflow(new TermStats(terms.term(),
									terms.docFreq()));
						}
					}
				}
			} while (terms.next());

		} else {
			TermEnum terms = reader.terms();
			while (terms.next()) {
				String textOfTerm = terms.term().text();
				if (!textEditor.isRefinedQueryStopWordLength(textOfTerm)
						&& !sentQueries.contains(textOfTerm)  && !initialQuery.contains(textOfTerm)) {
					tiq.insertWithOverflow(new TermStats(terms.term(), terms
							.docFreq()));
				}
			}
		}

		TermStats[] result = new TermStats[tiq.size()];

		// we want highest first so we read the queue and populate the array
		// starting at the end and work backwards
		int count = tiq.size() - 1;
		while (tiq.size() != 0) {
			result[count] = (TermStats) tiq.pop();
			count--;
		}
		return result;
	}

	public TermStats[] getLowFreqTerms(IndexReader reader, int numTerms,
			String field, ArrayList<String> sentQueries,  List<String> initialQuery) throws Exception {

		TermInfoWiTFQueueLowFreq tiq = new TermInfoWiTFQueueLowFreq(numTerms);
		if (field != null) {
			// Document aDoc = reader.document(0);
			// reader.getTermFreqVector(0, field);
			TermEnum terms = reader.terms(new Term(field));

			do {
				if (terms != null && terms.term() != null) {
					String textOfTerm = terms.term().text();

					if (terms.term().field().equals(field)) {
						if (!textEditor
								.isRefinedQueryStopWordLength(textOfTerm)
								&& !sentQueries.contains(textOfTerm) && !initialQuery.contains(textOfTerm) ) {
							tiq.insertWithOverflow(new TermStats(terms.term(),
									terms.docFreq()));
						}
					}
				}
			} while (terms.next());

		} else {
			TermEnum terms = reader.terms();
			while (terms.next()) {
				String textOfTerm = terms.term().text();
				if (!textEditor.isRefinedQueryStopWordLength(textOfTerm)
						&& !sentQueries.contains(textOfTerm) && !initialQuery.contains(textOfTerm)) {
					tiq.insertWithOverflow(new TermStats(terms.term(), terms
							.docFreq()));
				}
			}
		}

		TermStats[] result = new TermStats[tiq.size()];

		// we want highest first so we read the queue and populate the array
		// starting at the end and work backwards
		int count = tiq.size() - 1;
		while (tiq.size() != 0) {
			result[count] = (TermStats) tiq.pop();
			count--;
		}
		return result;
	}

	/**
	 * Takes array of TermStats. For each term looks up the tf for each doc
	 * containing the term and stores the total in the output array of
	 * TermStats. Output array is sorted by highest total tf.
	 * 
	 * @param reader
	 * @param terms
	 *            TermStats[]
	 * @return TermStats[]
	 * @throws Exception
	 */

	public static TermStats[] sortByTotalTermFreq(IndexReader reader,
			TermStats[] terms) {
		TermStats[] ts = new TermStats[terms.length]; // array for sorting
		long totalTF = 0;
		for (int i = 0; i < terms.length; i++) {
			try {
				totalTF = getTotalTermFreq(reader, terms[i].term);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ts[i] = new TermStats(terms[i].term, terms[i].docFreq, totalTF);
		}

		Comparator<TermStats> c = new TotalTermFreqComparatorSortDescending();
		Arrays.sort(ts, c);

		return ts;
	}

	public static TermStats[] sortAscendingByTotalTermFreq(IndexReader reader,
			TermStats[] terms) throws Exception {
		TermStats[] ts = new TermStats[terms.length]; // array for sorting
		long totalTF;
		for (int i = 0; i < terms.length; i++) {
			totalTF = getTotalTermFreq(reader, terms[i].term);
			ts[i] = new TermStats(terms[i].term, terms[i].docFreq, totalTF);
		}

		Comparator<TermStats> c = new TotalTermFreqComparatorSortAscending();
		Arrays.sort(ts, c);

		return ts;
	}

	public static long getTotalTermFreq(IndexReader reader, Term term) {
		long totalTF = 0;
		TermDocs td;
		try {
			td = reader.termDocs(term);
			if (td != null) {
				while (td.next()) {
					totalTF += td.freq();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return totalTF;
	}
}

final class TermStats {
	public Term term;
	public int docFreq;
	public long totalTermFreq;

	public TermStats(Term t, int df) {
		this.term = t;
		this.docFreq = df;
	}

	public TermStats(Term t, int df, long tf) {
		this.term = t;
		this.docFreq = df;
		this.totalTermFreq = tf;
	}
}

/**
 * Priority queue for TermStats objects ordered by TermStats.docFreq
 **/
final class TermInfoEqualFreqThanX extends PriorityQueue {
	int specificFreq;

	TermInfoEqualFreqThanX(int size, int specificFreq) {
		this.specificFreq = specificFreq;
		initialize(size);
	}

	@Override
	protected boolean lessThan(Object termInfoA, Object termInfoB) {
		return ((TermStats) termInfoA).docFreq == specificFreq;
	}
}

final class TermInfoHigherFreqThanX extends PriorityQueue {
	int specificFreq;

	TermInfoHigherFreqThanX(int size, int specificFreq) {
		this.specificFreq = specificFreq;
		initialize(size);
	}

	@Override
	protected boolean lessThan(Object termInfoA, Object termInfoB) {
		return ((TermStats) termInfoA).docFreq < specificFreq;
	}
}

final class TermInfoLowerFreqThanX extends PriorityQueue {
	int specificFreq;

	TermInfoLowerFreqThanX(int size, int specificFreq) {
		this.specificFreq = specificFreq;
		initialize(size);
	}

	@Override
	protected boolean lessThan(Object termInfoA, Object termInfoB) {
		/*
		 * if(((TermStats) termInfoA).docFreq == 1){ return true; //it should
		 * not be added to queue }else{
		 */
		return ((TermStats) termInfoA).docFreq > specificFreq;
		// }
	}
}

/**
 * Priority queue for TermStats objects ordered by TermStats.docFreq
 **/
final class TermInfoWiTFQueueForHighFreq extends PriorityQueue {
	TermInfoWiTFQueueForHighFreq(int size) {
		initialize(size);
	}

	@Override
	protected boolean lessThan(Object termInfoA, Object termInfoB) {
		return ((TermStats) termInfoA).docFreq < ((TermStats) termInfoB).docFreq;
	}
}

final class TermInfoWiTFQueueLowFreq extends PriorityQueue {
	TermInfoWiTFQueueLowFreq(int size) {
		initialize(size);
	}

	@Override
	protected boolean lessThan(Object termInfoA, Object termInfoB) {
		if (((TermStats) termInfoA).docFreq == 1) {
			return true; // it should not be added to queue
		} else {
			return ((TermStats) termInfoA).docFreq > ((TermStats) termInfoB).docFreq;
		}
	}
}

/**
 * Comparator
 * 
 * Reverse of normal Comparator. i.e. returns 1 if a.totalTermFreq is less than
 * b.totalTermFreq So we can sort in descending order of totalTermFreq
 */
final class TotalTermFreqComparatorSortDescending implements
		Comparator<TermStats> {

	public int compare(TermStats a, TermStats b) {
		if (a.totalTermFreq < b.totalTermFreq) {
			return 1;
		} else if (a.totalTermFreq > b.totalTermFreq) {
			return -1;
		} else {
			return 0;
		}
	}
}

final class TotalTermFreqComparatorSortAscending implements
		Comparator<TermStats> {

	public int compare(TermStats a, TermStats b) {
		if (a.totalTermFreq > b.totalTermFreq) {
			return 1;
		} else if (a.totalTermFreq < b.totalTermFreq) {
			return -1;
		} else {
			return 0;
		}
	}
}