/**
 * 
 */
package db.infiniti.harvester.modules.extractor;

import java.util.HashMap;

import db.infiniti.sitedescription.DataModel;
import db.infiniti.sitedescription.WebsiteDS;


/**
 * @author mohammad
 *
 */
public class ExtractorMainClass {

	public static void main(String[] args){
		WebsiteDS currentSiteDescription = new WebsiteDS();
		currentSiteDescription.setAcceptsStopWords(true);
		currentSiteDescription.setAddress("www.mobygames.com");
		DataModel searchResultsDataModel = new DataModel();
		HashMap<String, String> toCrawlDataModel = new HashMap<String, String>();
		
		toCrawlDataModel.put("links", "//*[@id=\"mof_object_list\"]/tbody/tr/td[1]/a");
		searchResultsDataModel.setToCrawlDataModel(toCrawlDataModel);
		
		ExtractorInterface extractor = new XpathBasedExtractor();
		extractor.setConfiguration(currentSiteDescription, searchResultsDataModel);
		extractor.extractDataModel("http://www.mobygames.com/browse/games/xbox/list-games/", searchResultsDataModel); // result in searchResultsDataModel
		
		
		DataModel detailedPagesDataModel = new DataModel();
		toCrawlDataModel = new HashMap<String, String>();
		toCrawlDataModel.put("", "");
		detailedPagesDataModel.setToCrawlDataModel(toCrawlDataModel);
		extractor.extractDataModel("address of detailed or returned page", detailedPagesDataModel);//result in detailedPagesDataModel
	}
}
