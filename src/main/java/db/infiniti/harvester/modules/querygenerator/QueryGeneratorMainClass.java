/**
 * 
 */
package db.infiniti.harvester.modules.querygenerator;

import db.infiniti.config.CrawledLinkDS;
import db.infiniti.sitedescription.WebsiteDS;

/**
 * @author mohammad
 *
 */
public class QueryGeneratorMainClass {
	
		public static void main(String[] args){
			WebsiteDS currentSiteDescription = new WebsiteDS();
			currentSiteDescription.setAcceptsStopWords(true);
			String queriesFilePath = "querypool/wikiwebsorted";
			CrawledLinkDS crawledLinkDS = new CrawledLinkDS(); // has values set

			QueryGeneratorInterface qG = new RandomWordQueryGenerator(currentSiteDescription, queriesFilePath);
			qG.generateQueries();
			qG.nextQuery();
			
			QueryGeneratorInterface qGM = new MostFrequentWordQueryGenerator(crawledLinkDS);
			qGM.generateQueries();
			qGM.nextQuery();
		}
	
	
}
