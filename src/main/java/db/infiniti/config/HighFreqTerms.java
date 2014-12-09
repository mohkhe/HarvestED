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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
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
			ArrayList<String> sentQueries) {
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
			terms = getHighFreqTerms(reader, numTerms, field, sentQueries);
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
			terms = getLowFreqTerms(reader, numTerms, field, sentQueries);
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
				termsWithTF = sortByTotalTermFreq(reader, terms);//sortAscendingByTotalTermFreq
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mostFreqTerm = termsWithTF[0].term + "";
		}
		return mostFreqTerm;
	}

	/**
	 * 
	 * @param reader
	 * @param numTerms
	 * @param field
	 * @param sentQueries
	 * @return TermStats[] ordered by terms with highest docFreq first.
	 * @throws Exception
	 */
	public TermStats[] getHighFreqTerms(IndexReader reader, int numTerms,
			String field, ArrayList<String> sentQueries) throws Exception {

		TermInfoWiTFQueueForHighFreq tiq = new TermInfoWiTFQueueForHighFreq(numTerms);
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
				if (!textEditor
						.isRefinedQueryStopWordLength(textOfTerm)
						&& !sentQueries.contains(textOfTerm)) {
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
			String field, ArrayList<String> sentQueries) throws Exception {

		TermInfoWiTFQueueLowFreq tiq = new TermInfoWiTFQueueLowFreq(numTerms);
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
				if (!textEditor
						.isRefinedQueryStopWordLength(textOfTerm)
						&& !sentQueries.contains(textOfTerm)) {
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
			TermStats[] terms) throws Exception {
		TermStats[] ts = new TermStats[terms.length]; // array for sorting
		long totalTF;
		for (int i = 0; i < terms.length; i++) {
			totalTF = getTotalTermFreq(reader, terms[i].term);
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
	
	public static long getTotalTermFreq(IndexReader reader, Term term)
			throws Exception {
		long totalTF = 0;
		TermDocs td = reader.termDocs(term);
		while (td.next()) {
			totalTF += td.freq();
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
		if(((TermStats) termInfoA).docFreq == 1){
			return true; //it should not be added to queue
		}else{
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