package db.infiniti.harvester.modules.querygenerator;

import java.util.List;

import db.infiniti.harvester.modules.common.CommonFunctionsClass;
import db.infiniti.sitedescription.WebsiteDS;

public class QueryGenerator {

	List<String> queries;
	List<String> sentQueries;
	int queryIndex = 0;//index for generated queries and checked. not sent queries
	String currentQuery = "";
	CommonFunctionsClass functions = new CommonFunctionsClass();
	//WebsiteDS currentSiteDescription;

	void updateIndex(){
		this.queryIndex++;
	}
/*	
	void setCurrentSiteDescription(WebsiteDS currentSiteDescription){
		this.currentSiteDescription = currentSiteDescription;
	}
*/
	
}
