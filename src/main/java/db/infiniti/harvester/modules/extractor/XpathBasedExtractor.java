/**
 * 
 */
package db.infiniti.harvester.modules.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openqa.selenium.WebElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLAnchorElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import org.apache.xml.dtm.ref.DTMNodeList;*/

import db.infiniti.config.CrawledLinkDS;
import db.infiniti.sitedescription.DataModel;
import db.infiniti.sitedescription.WebsiteDS;
import db.infiniti.surf.Browser;

/**
 * @author mohammad
 *
 */
public class XpathBasedExtractor extends Extractor implements ExtractorInterface {

	WebsiteDS siteDes;
	CrawledLinkDS crawledLinkDS;
	List<WebElement> allReturnedElements;
	List<Node> allReturnedNodes;
	short DOCUMENT_NODE; 
	short ELEMENT_NODE = 1;
/*	final int browserBasedXpathQuery = 0;
	final int domTreeBasedXpathQuery = 1;
	int xPathQueryApproach;*/
	
	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	Document doc = null;
	Element rootElement = null;
	
	String pageSource;
	Browser sRPagesbrowser;

	/* (non-Javadoc)
	 * @see db.infiniti.harvester.modules.extractor.ExtractorInterface#setConfiguration()
	 */
	public void setConfiguration(WebsiteDS currentSiteDescription, DataModel dataModel) {//, int xPathQueryApproach
		siteDes = currentSiteDescription;
	//	this.xPathQueryApproach = xPathQueryApproach;
	//	allReturnedElements = new ArrayList<WebElement>();
		//allReturnedNodes = (NodeList) new ArrayList();
		allReturnedNodes = new ArrayList<Node>();
		sRPagesbrowser = new Browser(); 
	}

	public DataModel extractDataModel(String url, DataModel dataModel) {
		HashMap<String, String> toCrawlDataModel; 
		HashMap<String, List<String>> crawledDataModel;
		crawledDataModel = dataModel.getCrawledDataModel();
		toCrawlDataModel = dataModel.getToCrawlDataModel();
		pageSource = sRPagesbrowser.getPageSource(url);
		Document doc = this.getDOmDocumentObjFromString(pageSource);
		this.DOCUMENT_NODE = doc.getNodeType();
				
		Iterator<String> iterator = toCrawlDataModel.keySet().iterator();
		while(iterator.hasNext()){
			String itemName = iterator.next();
			String itemXpath = toCrawlDataModel.get(itemName);
			
	/*		List<WebElement> returnedElements = sRPagesbrowser.runXPathQuery(itemXpath);
			allReturnedElements.addAll(returnedElements);*/
			NodeList returnedNodes = this.runXPathOnDocumentObj(doc, itemXpath);
			
			List<String> returnedRes = new ArrayList<String>();
			boolean checkIfAllWereEmpty = true;
			
			for (int i = 0; i<returnedNodes.getLength(); i++) {
				Node tempNode = returnedNodes.item(i);
				String xPathNode = this.getCanonicalXPath(tempNode);
				String tempValue = getValue(tempNode);
				if(tempValue.equals("") || tempValue == null){
					returnedRes.add(" "); // for grouping purposes	
				}else{
					returnedRes.add(tempValue);	// TODO check on attribute selection and extraction
												//TODO check if there is difference btw not-found or empty
					checkIfAllWereEmpty = false;
				}
			}
			returnedRes.add("flag-"+checkIfAllWereEmpty);//The last item is a check flag
			crawledDataModel.put(itemName, returnedRes);
		}
		makeGroupingBasedOnDOMTree(doc);
		return dataModel;
	}
	
    public String getCanonicalXPath(Node node) {
        Node parent = node.getParentNode();
        if (parent.getNodeType() == DOCUMENT_NODE) {
            return "/" + parent.getNodeName();
        }
        return getCanonicalXPath(parent) + '/' + getXPathToken(node);
    }
    
    /**
     * Returns the XPath token for this node only.
     */
    private String getXPathToken(Node node) {
        Node parent = node.getParentNode();
        int total = 0;
        int nodeIndex = 0;
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
        	Node child = children.item(i);
            if (child.getNodeType() == ELEMENT_NODE && child.getNodeName().equals(node.getNodeName())) {//the same name, +1
                total++;
            }
            if (child == node) {
                nodeIndex = total;
            }
        }

        if (nodeIndex == 1 && total == 1) {
            return node.getNodeName();
        }
        return node.getNodeName() + '[' + nodeIndex + ']';
    }
    
/*    public String getNodeName() {
        final StringBuilder name = new StringBuilder();
        if (getPrefix() != null) {
            name.append(getPrefix() + ':');
        }
        name.append(localName_;);
        return name.toString().toLowerCase();
    }
*/
    
	private void makeGroupingBasedOnDOMTree(Document doc) {
		Document temDoc = doc;
		NodeList nodeList = temDoc.getChildNodes();
		
		for(int i = 0; i< nodeList.getLength(); i++){
			if(nodeList.item(i) != null){
				
			}
		}
	}

	private NodeList runXPathOnDocumentObj(Document doc, String xPathExpression){
		  //Evaluate XPath against Document itself
		NodeList nodes = null;
	    XPath xPath = XPathFactory.newInstance().newXPath();
	    try {
			nodes = (NodeList)xPath.evaluate(xPathExpression,
			        doc.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return nodes;
	}
	
	private Document getDOmDocumentObjFromString(String pageSource){
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
		
	    Document doc1 = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc1 = docBuilder.parse (new InputSource(new StringReader(pageSource)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return doc1;
	}
	
	private String getValue(Node hE) {//get links and hrefs
		Object tempR = hE;
		String result = "";
		if (tempR instanceof HTMLAnchorElement) {
			String name = ((HTMLAnchorElement) tempR).getName();//Attribute();
			String value = ((HTMLAnchorElement) tempR).getHref();//Attribute();
			result = ((HTMLAnchorElement) tempR).getHref();//Attribute();
//			result = hE.getAttribute("href");
			result = getLinkedURL(siteDes.getAddress(), result);//TODO check

		} else if (hE.getNodeName().equalsIgnoreCase("a")){
			result = ((Element)hE).getAttribute("href");
		}else
			result = hE.getTextContent();
		return result;
	}
	
	
	private String getValue(WebElement hE) {//get links and hrefs
		Object tempR = hE;
		String result = "";
		if (tempR instanceof HTMLAnchorElement) {
			String name = ((HTMLAnchorElement) tempR).getName();//Attribute();
			String value = ((HTMLAnchorElement) tempR).getHref();//Attribute();
			result = ((HTMLAnchorElement) tempR).getHref();//Attribute();
//			result = hE.getAttribute("href");
			result = getLinkedURL(siteDes.getAddress(), result);//TODO check

		} else {
			result = hE.getText();
		}
		return result;
	}

	Document createXMLFromSource(String source){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(false);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setExpandEntityReferences(false);
		DocumentBuilder db;
		Document temDoc = null;
		try {
			db = dbf.newDocumentBuilder();
			temDoc = db.parse(new InputSource(new StringReader(source)));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temDoc;
	}
	
	void checkXpath(String xPathExpression){
		if (xPathExpression.contains("\\")) {
			xPathExpression = xPathExpression.replace("\\", "");
		}
		if (xPathExpression.startsWith("//")) {
			xPathExpression = xPathExpression.replace("//", "/");
		}
	}

	public void saveAsXML(){
		docFactory = DocumentBuilderFactory
				.newInstance();
		boolean stopCrawlConditionIsMet = false;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("crawlResults");
			doc.appendChild(rootElement);
			
		
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * @param originalURL
	 * @param linkedPath
	 * @return completed form of URL (with http) by Victor
	 */
	public String getLinkedURL(String originalURL, String linkedPath) {
		URL origURL = null;
		try {
			origURL = new URL(originalURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (linkedPath.startsWith("/")) {
			String portSuffix = origURL.getPort() == -1 ? "" : ":"
					+ origURL.getPort();
			linkedPath = origURL.getProtocol() + "://" + origURL.getHost()
					+ portSuffix + linkedPath;
		} else if (!linkedPath.startsWith("http")) {
			String originalUrlString = origURL.toString();
			linkedPath = originalUrlString.substring(0,
					originalUrlString.lastIndexOf("/") + 1)
					+ linkedPath;
		}
		return linkedPath;
	}

}
