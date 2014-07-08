/**
 * 
 */
package db.infiniti.harvester.modules.querygenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import db.infiniti.sitedescription.WebsiteDS;

/**
 * @author mohammad
 * 
 */
public class RandomWordQueryGenerator extends QueryGenerator implements
		QueryGeneratorInterface {

	WebsiteDS currentSiteDescription;
	String queriesFilePath = "";

	public RandomWordQueryGenerator(WebsiteDS currentSiteDescription,
			String queriesFilePath) {
		super();
		this.currentSiteDescription = currentSiteDescription;
		this.queriesFilePath = queriesFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * db.infiniti.harvester.modules.QueryGeneratorInterface#generateQueries()
	 */
	public void generateQueries() {
		// TODO Auto-generated method stub
		queries = new ArrayList<String>();
		queries = readQueriesFromFile(queries, queriesFilePath);
		sentQueries =  new ArrayList<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see db.infiniti.harvester.modules.QueryGeneratorInterface#nextQuery()
	 */
	public String nextQuery() {
		if (queryIndex < queries.size()) {
			currentQuery = queries.get(queryIndex);
			queryIndex++;
			if (!currentSiteDescription.isAcceptsStopWords()) {
				if (functions.isStopWord(currentQuery)) {
					return nextQuery();
				} else if (sentQueries.contains(currentQuery)) {// repeated word
					return nextQuery();
				}
			} else {// if website accepts stopwords
				sentQueries.add(currentQuery);
				return currentQuery;
			}
		} else {
			// TODO
			// throw exception no more query
		}
		return currentQuery;
	}

	private List<String> readQueriesFromFile(List<String> queries,
			String filePath) {
		try {
			File file = new File(filePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			for (int i = 0; i < 100; i++) {
				String line = in.readLine();
				String query = line.replaceAll("[0-9]*", "").trim();
				if (!queries.contains(query)) {
					queries.add(query);
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return queries;
	}

	public void resetForNextCollection() {
		this.queryIndex = 0;
	}

}
