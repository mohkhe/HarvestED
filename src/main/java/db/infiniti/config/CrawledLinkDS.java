package db.infiniti.config;

import java.util.HashMap;

import org.w3c.dom.Document;

public class CrawledLinkDS {

	HashMap<String, String> crawledData; 
	HashMap<String, String> crawlDataModel; 
	
	Document doc = null;
	String link = "Could not find.";
	String searchResultlink = "Could not find.";
	String title = "Could not find.";
	String description = "Could not find.";
	String itemXML = "Could not find.";
	String thumbLink = "Could not find.";
	String comments = "Could not find.";
	String linkTextContent = "Could not find.";
	String linkHtmlContent = "Could not find.";
	String repeated;
	int numberOfDocInReturnedResults; 
	
	public int getNumberOfDocInReturnedResults() {
		return numberOfDocInReturnedResults;
	}

	public void setNumberOfDocInReturnedResults(int numberOfDocInReturnedResults) {
		this.numberOfDocInReturnedResults = numberOfDocInReturnedResults;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getRepeated() {
		return repeated;
	}

	public void setRepeated(String repeated) {
		this.repeated = repeated;
	}

	void CrawledLinkDS(){
		crawledData = new HashMap<String, String>();	
	}
	
	void setOrUpdateItemValue(String itemName, String ItemValue){
		if(crawledData.containsKey(itemName)){
			crawledData.put(itemName, ItemValue);
			//TODO exception repeated name
			System.out.println("repeated name value of an Item");
		}else {
			crawledData.put(itemName, ItemValue);
		}
	}
		
	public String getSearchResultlink() {
		return searchResultlink;
	}

	public void setSearchResultlink(String searchResultlink) {
		this.searchResultlink = searchResultlink;
	}
	
	public Document getXmlDoc() {
		return doc;
	}
	public void setXmlDoc(Document doc) {
		this.doc = doc;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getItemXML() {
		return itemXML;
	}
	public void setItemXML(String itemXML) {
		if(itemXML.isEmpty()){
			setItemXML("No XML found for this fragment.");
		}
		this.itemXML = itemXML;
	}
	public String getThumbLink() {
		return thumbLink;
	}
	public void setThumbLink(String thumbLink) {
		this.thumbLink = thumbLink;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getLinkTextContent() {
		return linkTextContent;
	}
	public void setLinkTextContent(String linkTextContent) {
		this.linkTextContent = linkTextContent;
	}
	public String getLinkHtmlContent() {
		return linkHtmlContent;
	}
	public void setLinkHtmlContent(String linkHtmlContent) {
		this.linkHtmlContent = linkHtmlContent;
	}

	
}