package db.infiniti.sitedescription;

import java.util.HashMap;
import java.util.List;

public class DataModel {

	HashMap<String, String> toCrawlDataModel;
	HashMap<String, List<String>> crawledDataModel;

	public DataModel() {
		toCrawlDataModel = new HashMap<String, String>();//List<String>
		crawledDataModel = new HashMap<String, List<String>>();
	}

	public void setToCrawlDataModel(HashMap<String, String> toCrawlDataModel) {
		this.toCrawlDataModel = toCrawlDataModel;
	}

	public void setCrawledDataModel(HashMap<String, List<String>> crawledDataModel) {
		this.crawledDataModel = crawledDataModel;
	}

	public HashMap<String, String> getToCrawlDataModel() {//List<String>
		return toCrawlDataModel;
	}

	public HashMap<String, List<String>> getCrawledDataModel() {
		return crawledDataModel;
	}

}
