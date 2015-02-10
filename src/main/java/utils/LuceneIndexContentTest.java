package utils;

import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NativeFSLockFactory;


public class LuceneIndexContentTest {

	public static void main(String[] args) {
		searchIndexReturnFreqTerms("crawledData/google.com.vitol/indexes/index/pages");
	}

	// Directory indexDirectory;
	static IndexReader indexReader;
	static Directory indexDirectory;
	
	public static void searchIndexReturnFreqTerms(String indexDirectoryPath) {
		// Directory directory = FSDirectory.getDirectory();
		// IndexReader indexReader = null;
		TermFreqVector termFreqDoc = null;
		try {
			indexDirectory = FSDirectory.getDirectory(indexDirectoryPath);
			indexDirectory.setLockFactory(new NativeFSLockFactory(
					indexDirectoryPath));
			indexReader = IndexReader.open(indexDirectory);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// "index/pages"
		IndexSearcher searcher = new IndexSearcher(indexReader);
		HashSet<String> test = new HashSet<String>();

		try {
			// indexReader = DirectoryReader.open(FSDirectory.open(new
			// File(indexDirectoryPath)));
			int num = indexReader.numDocs();
			// textOfURL = doc.get("text");
			// sourceCodeOfURL = doc.get("html");
			// this.docId = docID;
			int temp = searcher.maxDoc();
			String url;
			String text = "";
			int docID = 0;
			if (docID < indexReader.numDocs()) {
				Document doc = searcher.doc(docID);
				for (int j = 0; j < indexReader.numDocs(); j++) {
					doc = searcher.doc(j);
					url = doc.get("url");
					System.out.println(url);
					text = doc.get("text");
					if (test.contains(text)) {
						System.out.println("repetitionintext.");
					} else {
						test.add(text);
					}
					System.out.println(url);

					url = "";
					text = "";
				}
				termFreqDoc = indexReader.getTermFreqVector(docID, "text");
				/*
				 * text = doc.get("text"); url = doc.get("url");
				 * System.out.println(url);
				 * System.out.println("enter the label"); BufferedReader br =
				 * new BufferedReader(new InputStreamReader( System.in)); String
				 * label = br.readLine();
				 */

				if (test.contains(text)) {
					System.out.println("repetitionintext.");
				} else {
					test.add(text);
				}
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
