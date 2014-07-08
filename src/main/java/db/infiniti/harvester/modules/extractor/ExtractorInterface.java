package db.infiniti.harvester.modules.extractor;

import db.infiniti.sitedescription.DataModel;
import db.infiniti.sitedescription.WebsiteDS;

public interface ExtractorInterface {

	void setConfiguration(WebsiteDS currentSiteDescription, DataModel dataModel);
	DataModel extractDataModel(String url, DataModel dataModel);

}
