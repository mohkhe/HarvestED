/**
 * 
 */
package db.infiniti.harvester.modules.querygenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import db.infiniti.config.CrawledLinkDS;

/**
 * @author mohammad
 * 
 */
public class MostFrequentWordQueryGenerator extends QueryGenerator implements
		QueryGeneratorInterface {

	boolean isFirstQuery = true;
	String firstQuery = "content";// seen in trials that always return sth
	HashMap<String, Integer> setQueryFreq = new HashMap<String, Integer>();
	CrawledLinkDS crawledLinkDS;
	
	public MostFrequentWordQueryGenerator(CrawledLinkDS crawledLinkDS) {
		super();
		this.crawledLinkDS = crawledLinkDS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * db.infiniti.harvester.modules.QueryGeneratorInterface#generateQueries()
	 */
	public void generateQueries() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see db.infiniti.harvester.modules.QueryGeneratorInterface#nextQuery()
	 */
	public String nextQuery() {
		if (isFirstQuery) {// set first query
			currentQuery = firstQuery;
			isFirstQuery = false;
		} else {
			currentQuery = getMostFreqQuery();
		}
		setQueryFreq.put(currentQuery, 0); // not to use later

		System.out.println("New query, number " + queryIndex + " : "
				+ currentQuery);
		queryIndex++;
		return currentQuery;
	}

	private String getMostFreqQuery() {
		if(!setQueryFreq.isEmpty()){
			List<String> mapKeys = new ArrayList<String>(setQueryFreq.keySet());
			List<Integer> mapValues = new ArrayList<Integer>(setQueryFreq.values());
			Collections.sort(mapValues, Collections.reverseOrder());//sorting on frequencies

			Iterator<Integer> valueIt = mapValues.iterator();
			// TODO check if this is the most frequent and not the least frequencies
			int val = (Integer) valueIt.next();//the first one in frequencies
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int comp1 = setQueryFreq.get(key);
				if (comp1 == val) {
					return key;
				}
			}
			return null;//TODO change null return
		}else{
			//TODO through exception 'no queries in set'
			return null;//TODO change null return
		}
	}

	public void updateQueryList() { //String pageContent
		//TODO check this.crawledLinkDS.getLinkTextContent()
		if(!this.crawledLinkDS.getLinkTextContent().equals("Could not find.")){//not set
			String pageContent = functions.refineText(this.crawledLinkDS.getLinkTextContent());
			String[] tokens = functions.tokenizer(pageContent);
			for (String token : tokens) {
				if (!token.equalsIgnoreCase("")) {
					if (this.setQueryFreq.containsKey(token)) {
						if (setQueryFreq.get(token) != 0) {// not used before
							setQueryFreq.put(token, setQueryFreq.get(token) + 1);
						}
					} else {
						setQueryFreq.put(token, 1);
					}
				}
			}
		}
		
	}

	public void resetForNextCollection() {
		this.queryIndex = 0;
		this.setQueryFreq.clear();
		this.isFirstQuery = true;
	}

}
