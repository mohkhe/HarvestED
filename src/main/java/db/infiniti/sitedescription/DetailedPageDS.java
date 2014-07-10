package db.infiniti.sitedescription;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.ErrorHandler.UnknownServerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import db.infiniti.config.CrawlingConfig;
import db.infiniti.crawling.CrawlerSellenium;
import db.infiniti.surf.Browser;

public class DetailedPageDS {

	private boolean tableExists = false;
	public HashMap<String, String> dataModel = new HashMap<String, String>();
	public HashMap<String, HashMap<String, String>> dataModels = new HashMap<String, HashMap<String, String>>();
	Connection connection;
	HashMap<String, Vector<List<List<String>>>> extractionResults = new HashMap<String, Vector<List<List<String>>>>();

	public void connectToDB(String DBName) {
		String url = "jdbc:postgresql://teehuis.ewi.utwente.nl:5432/" + DBName;
		String username = "mohammad";
		String password = "4249324";
		try {
			System.out.println("Connecting database...");
			Class.forName("org.postgresql.Driver");
			if (connection == null) {
				connection = (Connection) DriverManager.getConnection(url,
						username, password);
				System.out.println("Database connected!");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect the database!", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readItemsXpathFile(String fileName, String dataModelTable) {
		try {

			File fXmlFile = new File(fileName); // "c:\\file.xml"
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			// doc.getDocumentElement().normalize();
			String name = "";
			String value = "";
			doc.getElementsByTagName("pagetemplate").item(0).getTextContent();

			NodeList nList = doc.getElementsByTagName("Url");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				String datamodelName = "";
				Node nNode = nList.item(temp);
				dataModel.clear();
				if (nNode.getAttributes().getNamedItem("pagetemplate") != null) {
					if (nNode.getAttributes().getNamedItem("pagetemplate")
							.getTextContent().equals("resultpage")) {
						datamodelName = "resultpage";
						if (nNode.getAttributes().getNamedItem("address") != null) {
							dataModel.put("address", nNode.getAttributes()
									.getNamedItem("address").getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("template") != null) {
							dataModel.put("template", nNode.getAttributes()
									.getNamedItem("template").getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("item_xpath") != null) {
							dataModel.put("item_xpath", nNode.getAttributes()
									.getNamedItem("item_xpath")
									.getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("link_xpath") != null) {
							dataModel.put("link_xpath", nNode.getAttributes()
									.getNamedItem("link_xpath")
									.getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("desc_xpath") != null) {
							dataModel.put("desc_xpath", nNode.getAttributes()
									.getNamedItem("desc_xpath")
									.getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("title_xpath") != null) {
							dataModel.put("title_xpath", nNode.getAttributes()
									.getNamedItem("title_xpath")
									.getTextContent());
						}
						if (nNode.getAttributes().getNamedItem("thumbn_xpath") != null) {
							dataModel.put("thumbn_xpath", nNode.getAttributes()
									.getNamedItem("thumbn_xpath")
									.getTextContent());
						}
						this.dataModels.put(datamodelName, dataModel);
					} else {
						int attSize = nNode.getAttributes().getLength();
						for (int i = 0; i < attSize; i++) {
							Node tempNode = nNode.getAttributes().item(attSize);
							if (!tempNode.getNodeName().equals("pagetemplate")) {
								name = tempNode.getNodeName();
								value = tempNode.getTextContent();
								dataModel.put(name, value);
							} else {
								datamodelName = nNode.getAttributes()
										.getNamedItem("pagetemplate")
										.getTextContent();
							}

						}
						this.dataModels.put(datamodelName, dataModel);

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readItemsXpathDB(String DBName, String dataModelTable) {
		connectToDB(DBName);
		Statement s;
		ResultSet rs;
		try {
			s = (Statement) connection.createStatement();
			// s.executeQuery("SELECT  * FROM  public.\"ItemsXPATH\"");
			s.executeQuery("SELECT  * FROM  public.\"" + dataModelTable + "\"");

			rs = s.getResultSet();
			int count = 0;
			WebsiteDS siteDes;
			while (rs.next()) {
				String name = rs.getString(1);
				String value = rs.getString(2);
				dataModel.put(name, value);
			}
			System.out.println(count
					+ "ItemsXpaths - Data Model were retrieved");
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTable(String tableName,
			HashMap<String, Vector<List<List<String>>>> results) {
		// TODO check in db that table does not exist
		Statement s;
		ResultSet rs = null;
		Iterator itE = dataModel.keySet().iterator();

		String query = "	CREATE TABLE \"" + tableName + "\" ("
				+ "				\"item_ID\" serial NOT NULL";

		while (itE.hasNext()) {
			String columnName = (String) itE.next();
			query = query + "				,\"" + columnName + "\" character varying";
		}

		query = query + "	)	WITH (OIDS=FALSE);" + "				ALTER TABLE \""
				+ tableName + "\" OWNER TO mohammad;";
		try {
			s = connection.createStatement();
			tableExists = true;
			s.execute(query);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}

	public void insertGameData(String tableName) {
		// String title, String platform, String releaseDate, String publisher
		// tableName = crawling"gamecredits";
		if (!tableExists) {
			createTable(tableName, extractionResults);
		}
		Statement s;
		ResultSet rs = null;
		
		synchronized(extractionResults){
			Iterator itE = extractionResults.keySet().iterator();
			HashMap<String, Vector<List<List<WebElement>>>> storeNestedData = new HashMap<String, Vector<List<List<WebElement>>>>();
			if (itE.hasNext()) {

				try {
					String query = "INSERT INTO \"" + tableName + "\" (";
					if (itE.hasNext()) {
						query = query + itE.next();
					}
					while (itE.hasNext()) {
						query = query + ", " + itE.next();
					}
					itE = extractionResults.keySet().iterator();
					query = query + ")  VALUES (";
					while (itE.hasNext()) {
						String tempName = (String) itE.next();
						Vector<List<List<String>>> tempObj = (Vector<List<List<String>>>) extractionResults
								.get(tempName);
						if (!tempObj.isEmpty()) {
							String value = "";
							boolean newRecord = true;
							for (int i = 0; i < tempObj.size(); i++) {
								List<List<String>> tempRes = tempObj.get(i);
								if (tempRes.size() > 1) {
									if (tempRes.get(0).size() == 1
											&& tempRes.get(1).size() > 1) { // first
																			// one
																			// is a
																			// label
										String tempText;// = tempRes.get(0).get(0);
										for (int j = 0; j < tempRes.size(); j++) {
											for (int k = 0; k < tempRes.size(); k++) {
												if (j < tempRes.get(k).size() && 
														k < tempRes.size()) {
													if (k == 0) {
														tempText = tempRes.get(0)
																.get(0);
													} else {
														tempText = tempRes.get(k)
																.get(j);
													}
													if (newRecord) {
														value = value + tempText;
														newRecord = false;
													} else {
														value = value + "----"
																+ tempText; // ","
																			// sign
																			// for
																			// platform,3.5;platform,2.0;
													}
												}
											}
											value = value + ";;;;"; // TODO
											newRecord = true;
										}

									} else {
										for (int j = 0; j < tempRes.get(0).size(); j++) {
											for (int k = 0; k < tempRes.size(); k++) {
												if (j < tempRes.get(k).size()
														&& k < tempRes.size()) {
													String tempText = tempRes
															.get(k).get(j);
													if (newRecord) {
														value = value + tempText;
														newRecord = false;
													} else {
														value = value + "----"
																+ tempText; // ","
																			// sign
																			// for
																			// platform,3.5;platform,2.0;
													}
												}
											}
											value = value + ";;;;"; // TODO
											newRecord = true;
										}

									}
								} else {
									for (int j = 0; j < tempRes.get(0).size(); j++) {
										for (int k = 0; k < tempRes.size(); k++) {
											if (j < tempRes.get(k).size()
													&& k < tempRes.size()) {
												String tempText = tempRes.get(k)
														.get(j);
												if (newRecord) {
													value = value + tempText;
													newRecord = false;
												} else {
													value = value + "----"
															+ tempText; // ","
																		// sign
																		// for
																		// platform,3.5;platform,2.0;
												}
											}
										}
										value = value + ";;;;"; // TODO
										newRecord = true;
									}
								}
							}
							value = value.substring(0, value.lastIndexOf(";;;;"));
							if (value.contains("'")) {
								value = value.replaceAll("'", "\\\"");
							}
							query = query + "'" + value + "', ";
						} else {
							query = query + "'" + "empty" + "', ";
						}
					}
					query = query.substring(0, query.lastIndexOf(","));
					query = query + ")";
					if (query.contains("\n")) {
						query = query.replaceAll("\n", " ");
					}
					s = connection.createStatement();
					s.execute(query);

					// TODO deal with nested values
					/*
					 * UPDATE "Websites" SET link_xp =
					 * '//div[@id=\"jobresults\"]/div/ul/li/a' Where link_xp like
					 * '?'
					 */
					// s.executeQuery("SELECT table_name FROM information_schema.tables  WHERE table_schema='deb52794_fedweb'");//SELECT
					// * FROM INFORMATION_SCHEMA.TABLES WHERE table_schema =
					// 'deb52794_fedweb'
					// s.executeQuery("SELECT * FROM engine WHERE status LIKE 'OK'");
					// s.execute();
					// s.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'engine';");
					s.close();
					extractionResults.clear();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("No extracted data records from this website.");
			}
		}
		
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extractInfoFromDetailedPage(HashMap<String, String> dataModel,
			Browser detailedPagesbrowser) {
		// TODO Auto-generated method stub
		// save in db
		// HashMap<String, Vector<List<List<WebElement>>>> extractionResults =
		// new HashMap<String, Vector<List<List<WebElement>>>>();

		Iterator<String> namesIte = dataModel.keySet().iterator();
		while (namesIte.hasNext()) {
			String tempName = namesIte.next();
			String xPath = dataModel.get(tempName);
			/*
			 * xPath =
			 * "//*[@id=\"coreGameRank\"]/div/div/div/table/tbody/tr[./td/center/div]/td[2]/center/div -and- //*[@id=\"coreGameRank\"]/div/div/div/table/tbody/tr[./td/center/div]/td[1]"
			 * +
			 * " -or-  //*[@id=\"coreGameRank\"]/div/div/div/div[1]/div/div[1]";
			 */
			Vector xPaths = new Vector();
			xPaths.add(xPath);
			// Vector<List<List<WebElement>>> resultS = new
			// Vector<List<List<WebElement>>>();
			Vector<List<List<String>>> resultS = new Vector<List<List<String>>>();
			// List<WebElement> resultsOfXpath = new ArrayList<WebElement>();
			List<String> resultsOfXpath = new ArrayList<String>();
			if (xPath.contains("-or-")) {
				String[] temp = xPath.split("-or-");
				xPaths.clear();
				for (String tempS : temp) {
					xPaths.add(tempS.trim());
				}
			}
			for (int i = 0; i < xPaths.size(); i++) {
				if (((String) xPaths.get(i)).contains("-and-")) {
					xPaths.set(i, ((String) xPaths.get(i)).split("-and-"));
				}
			}
			List<String> xPathsList = new ArrayList<String>();
			for (int i = 0; i < xPaths.size(); i++) {

				if (xPaths.get(i).getClass() == xPath.getClass()) {
					// it is String
					if (((String) xPaths.get(i)).endsWith("img")) {

						List<WebElement> links = null;
						links = detailedPagesbrowser
								.runXPathQuery((String) xPaths.get(i));
						// TODO check
						if (!links.isEmpty()) {
							String fileName = getIMGFileName(links.get(0)
									.getAttribute("src"));

							detailedPagesbrowser.DownloadImage(links.get(0),
									"crawledData/cat.vanas.eu/images/"
											+ fileName);
							
							List<String> resultsOfXpathText = new ArrayList<String>();
							resultsOfXpathText.add(fileName);
							if (!resultsOfXpathText.isEmpty()) {
								// resultsOfXpath.addAll(tempListWebEl);
								synchronized(resultsOfXpath){
									resultsOfXpath.addAll(resultsOfXpathText);
								}
							}
						}

					} else {
						List<WebElement> tempListWebEl = detailedPagesbrowser
								.runXPathQuery((String) xPaths.get(i));
						List<String> resultsOfXpathText = getTextOfWebElement(
								(String) xPaths.get(i), tempListWebEl);
						synchronized(resultsOfXpath){
							resultsOfXpath.addAll(resultsOfXpathText);
						}
					}
				} else/* if(xPaths.get(i).getClass() == n[1]().getClass() */{
					String[] tempSA = (String[]) xPaths.get(i);
					// List<List<WebElement>> resultsOfXpathTemp = new
					// ArrayList<List<WebElement>>();
					List<List<String>> resultsOfXpathTempTexts = new ArrayList<List<String>>();

					for (String tempS : tempSA) {
						tempS = tempS.trim();
						if (tempS.trim().endsWith("img")) {

							List<WebElement> links = null;
							links = detailedPagesbrowser.runXPathQuery(tempS);
							// TODO check
							if (!links.isEmpty()) {
								String fileName = getIMGFileName(links.get(0)
										.getAttribute("src"));

								detailedPagesbrowser.DownloadImage(
										links.get(0),
										"crawledData/cat.vanas.eu/images/"
												+ fileName);
								List<String> resultsOfXpathText = new ArrayList<String>();
								resultsOfXpathText.add(fileName);
								synchronized(resultsOfXpath){
									resultsOfXpath.addAll(resultsOfXpathText);
								}
							}

						} else {
							List<WebElement> tempListWebEl = detailedPagesbrowser
									.runXPathQuery(tempS);
							List<String> tempListWebElTexts = getTextOfWebElement(
									tempS, tempListWebEl);
							if (!tempListWebElTexts.isEmpty()) {
								resultsOfXpathTempTexts.add(tempListWebElTexts);
							}
						}
					}
					if (!resultsOfXpathTempTexts.isEmpty()) {
						resultS.add(resultsOfXpathTempTexts);
					}
				}

			}
			List<List<String>> resTemp = new ArrayList<List<String>>();
			synchronized(resultsOfXpath){
				if (!resultsOfXpath.isEmpty()) {
					resTemp.add(resultsOfXpath);
					resultS.add(resTemp);
				}
				if (!resultS.isEmpty()) {
					synchronized(extractionResults){
						extractionResults.put(tempName, resultS);
					}
				}		
			}
			
		}
		// return extractionResults;
	}

	private String getIMGFileName(String attribute) {
		String fileName = attribute;
		if (fileName.contains("/")) {
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1,
					fileName.length());
		}
		return fileName;
	}

	public void extractInfo(Browser detailedPagesbrowser) {
		extractInfoFromDetailedPage(dataModel, detailedPagesbrowser);

	}

	private List<String> getTextOfWebElement(String xPath,
			List<WebElement> webElList) {
		String tempXpath = "";
		/*
		 * if(xPath.endsWith("]")){ tempXpath =
		 * xPath.substring(xPath.indexOf("[.")+2, xPath.indexOf("]")); }
		 */
		List<String> resultTextS = new ArrayList<String>();
		if(webElList!=null){
			for (WebElement webEl : webElList) {
				// List<WebElement> webElRes =
				// webEl.findElements(By.xpath("./"+tempXpath));
				String text = "";
				// if(!webElRes.isEmpty()){
				// text = webElRes.get(0).getText();
				try{
					text = webEl.getText();
				}catch(org.openqa.selenium.StaleElementReferenceException w){
					
				}catch(UnknownServerException we){
					
				}
				resultTextS.add(text);
				// }
			}
		}
		return resultTextS;
	}
}
