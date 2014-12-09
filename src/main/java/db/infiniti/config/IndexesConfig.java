package db.infiniti.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NativeFSLockFactory;

public class IndexesConfig {
	ArrayList<String> listURLsNotDownloadedButInCache = new ArrayList<String>();
	public Directory indexDirectory;
	// public String indexDirectoryPath = "";

	public StandardAnalyzer analyzer = new StandardAnalyzer();
	public IndexWriter w;
	public HashMap<String, String> urlsFileNames;
	public HashMap<String, String> emptyFiles;
	public String textOfURL = "";
	public String sourceCodeOfURL = "";
	public int docId = 0;
	public String indexName = "";
	public HighFreqTerms freqTermsFinderInIndex= new HighFreqTerms();
	
	// CorruptIndexException, LockObtainFailedException, IOException,
	// ParseException
	public IndexesConfig(String indexDirectoryPath) {
		// create some index
		// we could also create an index in our ram ...
		// Directory index = new RAMDirectory();
		indexName = indexDirectoryPath;
		try {
			indexDirectory = FSDirectory.getDirectory(indexDirectoryPath);// "index/pages"
			indexDirectory.setLockFactory(new NativeFSLockFactory(
					indexDirectoryPath));
			w = new IndexWriter(indexDirectory, analyzer,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// searchIndex("vitol");
		urlsFileNames = new HashMap<String, String>();
		emptyFiles = new HashMap<String, String>();

	}

	public void searchGetDocIndex() {
		String url = "";
		if (url != null) {
			if (searchIndex(url, "url")) {
				String htmlCode = textOfURL;
				String textFromHtml = sourceCodeOfURL;

			} else {
				if (!textOfURL.equals("empty-noindex")) {
					index(url, textOfURL, sourceCodeOfURL);
				}
				// System.out.println("Already in the index");
			}

		}
		// closeindex();
		// saveListsofQueries(emptyFiles);
	}

	public static void saveListsofQueries(HashMap<String, String> list) {
		try {
			File file = new File("crawl/emptyfiles.txt");
			FileWriter fstream = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(fstream);
			Iterator<String> iter = list.keySet().iterator();
			while (iter.hasNext()) {
				String element = iter.next();
				out.write(list.get(element) + "\t" + element + "\n");
				out.flush();
			}
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static String readHTMlFile(File file) {
		String htmlCode = "";
		try {
			FileReader fReader = new FileReader(file);
			BufferedReader in = new BufferedReader(fReader);
			String line = "";
			while ((line = in.readLine()) != null) {
				htmlCode = htmlCode + line.trim().toLowerCase();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return htmlCode;
	}

	public void closeindex() {
		try {
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("index generated");
	}

	public void index(String url, String text, String htmlCode) {
		Document doc = new Document();
		doc.add(new Field("url", url, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("text", text, Field.Store.YES, Field.Index.ANALYZED,
				Field.TermVector.YES));
		doc.add(new Field("html", htmlCode, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		// TODO change for other queries
		doc.add(new Field("query", "vitol", Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		try {
			w.addDocument(doc);
			w.commit();
			w.flush();
			int n = w.numDocs();
			System.out.print(n);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("index generated");
	}

	public String searchIndexDocTextReturn(String searchString,
			String termString) {
		System.out.println("Searching for '" + searchString + "'");
		// Directory directory = FSDirectory.getDirectory();
		IndexReader indexReader;
		try {
			indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			int n = w.numDocs();

			Term term = new Term(termString, searchString);
			TermQuery query = new TermQuery(term);
			TopDocs topDocs = indexSearcher.search(query, 10);
			if (topDocs.scoreDocs.length > 0) {
				// while(it.hasNext()){
				int docID = topDocs.scoreDocs[0].doc;
				Document doc = indexSearcher.doc(docID);
				textOfURL = doc.get("text");
				// sourceCodeOfURL = doc.get("html");
				this.docId = docID;
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return textOfURL;
	}

	public TermFreqVector searchIndexReturnFreqTerms(String searchString,
			String termString) {
		System.out.println("Searching for '" + searchString + "'");
		// Directory directory = FSDirectory.getDirectory();
		IndexReader indexReader;
		TermFreqVector termFreqDoc = null;
		try {
			indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			Term term = new Term(termString, searchString);
			TermQuery query = new TermQuery(term);
			TopDocs topDocs = indexSearcher.search(query, 10);
			if (topDocs.scoreDocs.length > 0) {
				// while(it.hasNext()){
				int docId = topDocs.scoreDocs[0].doc;
				Document doc = indexSearcher.doc(docId);
			//	textOfURL = doc.get("text");
				// sourceCodeOfURL = doc.get("html");
			//	this.docId = docID;
				termFreqDoc = indexReader.getTermFreqVector(docId, "text");
		        
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
		return termFreqDoc;
	}
	
	public boolean searchIndex(String searchString, String termString) {
		System.out.println("Searching for '" + searchString + "'");
		// Directory directory = FSDirectory.getDirectory();
		IndexReader indexReader;
		try {
			indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			int n = w.numDocs();

			Term term = new Term(termString, searchString);
			TermQuery query = new TermQuery(term);
			TopDocs topDocs = indexSearcher.search(query, 10);
			if (topDocs.scoreDocs.length > 0) {
				// while(it.hasNext()){
				int docID = topDocs.scoreDocs[0].doc;
				Document doc = indexSearcher.doc(docID);
				textOfURL = doc.get("text");
				sourceCodeOfURL = doc.get("html");
				this.docId = docID;
				return true;
			} else
				return false;
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

		/*
		 * BooleanQuery bquery = new BooleanQuery(); bquery.add(query,
		 * BooleanClause.Occur.SHOULD); // query.add(new Term("url",
		 * searchString)); Hits hits = indexSearcher.search(bquery); PhraseQuery
		 * phrassQ = new PhraseQuery(); phrassQ.add(term); topDocs =
		 * indexSearcher.search(phrassQ, 10);
		 * 
		 * Analyzer analyzer = new KeywordAnalyzer(); QueryParser queryParser =
		 * new QueryParser("url", analyzer); Query query2 =
		 * queryParser.parse("\""+searchString+"\""); topDocs =
		 * indexSearcher.search(query2, 10);
		 */
		// System.out.println("Number of hits: " + topDocs.totalHits);

		// Iterator<Hit> it = hits.iterator();

	}

	public void queryIndex() {
		Query q;
		try {
			q = new MultiFieldQueryParser(new String[] { "title", "name" },
					analyzer).parse("s*");
			// searching ...
			int hitsPerPage = 10;
			IndexSearcher searcher = new IndexSearcher(indexDirectory);
			TopDocCollector collector = new TopDocCollector(hitsPerPage);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// output results
			System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("name") + ": "
						+ d.get("title"));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String extractTextFromHTML(String htmlCode) {
		String textFromHTMl = "";
		try {
			Source htmlSource = new Source(htmlCode);
			Segment htmlSegment = new Segment(htmlSource, 0, htmlCode.length());
			Renderer htmlRend = new Renderer(htmlSegment);
			textFromHTMl = htmlRend.toString();
			textFromHTMl = textFromHTMl.replaceAll("\\*", "");
			textFromHTMl = textFromHTMl.replaceAll("<[^<>]+>", "").trim();
			if (textFromHTMl.isEmpty() || textFromHTMl.equals("")
					|| textFromHTMl.equalsIgnoreCase("could not find.")) {
				// TODO
				return "empty-noindex";
			}
		} catch (java.lang.StackOverflowError e) {
			System.out.println("StackOverflowError error in Jericho");
			// e.printStackTrace();
		}

		return textFromHTMl;
	}
	
	public String getMostFreqTermInIndex(int KIntopK, ArrayList<String> sentQueries){
		IndexReader indexReader = null;
		try {
			indexReader = IndexReader.open(indexDirectory);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String mostFrerqTerm = "";
		try {
			mostFrerqTerm = freqTermsFinderInIndex.HighFreqTerms(indexDirectory, analyzer, indexReader, KIntopK, sentQueries);
			indexReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mostFrerqTerm;
	}
	
	public String getLeastFreqTermInIndex(int KIntopK, ArrayList<String> sentQueries){
		IndexReader indexReader = null;
		try {
			indexReader = IndexReader.open(indexDirectory);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lowFrerqTerm = "";
		try {
			lowFrerqTerm = freqTermsFinderInIndex.LowFreqTerms(indexDirectory, analyzer, indexReader, KIntopK, sentQueries);
			indexReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lowFrerqTerm;
	}

}
