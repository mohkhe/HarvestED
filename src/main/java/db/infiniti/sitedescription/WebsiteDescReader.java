package db.infiniti.sitedescription;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.File;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.mysql.jdbc.Connection;
//import com.mysql.jdbc.Statement;

public class WebsiteDescReader {

	Connection connection = null;
	ResultSet rs;
	ArrayList<WebsiteDS> listOfWebsites = new ArrayList<WebsiteDS>();

	private void getSQLServerConnection() {
		/*
		 * String url = "jdbc:mysql://trieschnigg.nl:3306/deb52794_fedweb";
		 * String username = "deb52794_fedweb"; String password = "theF@tWeb";
		 * try { System.out.println("Connecting database..."); if (connection ==
		 * null) { connection = (Connection) DriverManager.getConnection(url,
		 * username, password); System.out.println("Database connected!"); } }
		 * catch (SQLException e) { throw new
		 * RuntimeException("Cannot connect the database!", e); }
		 */

		String url = "jdbc:postgresql://teehuis.ewi.utwente.nl:5432/Vacancies";
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
			e.printStackTrace();
		}
	}

	public ArrayList<WebsiteDS> readDB() {
		getSQLServerConnection();
		Statement s;
		try {
			s = (Statement) connection.createStatement();
			s.executeQuery("SELECT  * FROM  public.\"Websites\" ORDER BY  \"engineID\" ASC"); //WHERE status LIKE '?'
			// for now it is ? but should change to OK
			rs = s.getResultSet();
			int count = 0;
			WebsiteDS siteDes;
			while (rs.next()) {
				siteDes = new WebsiteDS();
				siteDes.setEngineid(rs.getInt("engineid"));
				siteDes.setName(rs.getString("name"));
				siteDes.setAddress(rs.getString("url"));
				siteDes.setDescription(rs.getString("description"));
				siteDes.setComments(rs.getString("comments"));
				siteDes.setTemplate(rs.getString("template"));
				siteDes.setItemXPath(rs.getString("item_xp"));
				siteDes.setLinkXPath(rs.getString("link_xp"));
				siteDes.setTitleXPath(rs.getString("title_xp"));
				siteDes.setDescXPath(rs.getString("description_xp"));
				siteDes.setThumbNXPath(rs.getString("thumb_xp"));
				siteDes.setStatus(rs.getString("status"));
				String temp = rs.getString("next_page_xp");
				siteDes.setNext_page_xp(this.refineString(temp));
				listOfWebsites.add(siteDes);
				//String status = rs.getString("status");
				++count;
			}
			System.out.println(count + " rows were retrieved");
			rs.close();
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listOfWebsites;
	}

	private String refineString(String string) {
		if (string != null) {
			if (string.contains("\\")) {
				string = string.replace("\\", "");

				if (string.contains("\"")) {
					string = string.replaceAll("\"", "\'");

				}
			}

		}
		return string;

	}

	/*
	 * UPDATE "Websites" SET
	 * url='http://www.nationalevacaturebank.nl/?ref=jobtrack', template=
	 * 'http://www.nationalevacaturebank.nl/vacature/zoeken/overzicht/relevant/query/{query}/distance/30/output/html/items_per_page/15/page/1',
	 * item_xp='//li[./div/div/p/a]', link_xp='/div/div/p/a' WHERE
	 * "engineID"='8';
	 */
	/*
	 * finally { System.out.println("Closing the connection."); if (connection
	 * != null) try { connection.close(); } catch (SQLException ignore) { } }
	 */
	/*
	 * public ArrayList<WebsiteDS> readDB() { getSQLServerConnection();
	 * Statement s; try {
	 * 
	 * s = (Statement) connection.createStatement(); // s.executeQuery(
	 * "SELECT table_name FROM information_schema.tables  WHERE table_schema='deb52794_fedweb'"
	 * );//SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE table_schema =
	 * 'deb52794_fedweb'
	 * s.executeQuery("SELECT * FROM engine WHERE status LIKE 'OK'"); //
	 * s.execute(
	 * "INSERT INTO \"Websites\"(\"name\", description, url, comments, \"template\", item_xp, link_xp, title_xp, description_xp, thumb_xp, status)    VALUES ('w3schools.com', 'ICT job vacany in dutch', 'http://www.ictergezocht.nl/', '', 'http://www.ictergezocht.nl/it-vacatures/?what={query}&where=&r=&storesearch=1&submit=Zoek+Vacatures', '//div[@id=\"jobresults\"]/div[./ul/li/a]','?',  '?', '?',  '?', '?');"
	 * ); // s.executeQuery(
	 * "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'engine';"
	 * );
	 * 
	 * rs = s.getResultSet(); int count = 0; WebsiteDS siteDes; while
	 * (rs.next()) { // String name = rs.getString("table_name"); // String name
	 * = rs.getString("COLUMN_NAME");
	 * 
	 * siteDes = new WebsiteDS(); siteDes.setEngineid(rs.getInt("engineid"));
	 * siteDes.setName(rs.getString("name"));
	 * siteDes.setAddress(rs.getString("url"));
	 * siteDes.setDescription(rs.getString("description"));
	 * siteDes.setComments(rs.getString("comments"));
	 * siteDes.setTemplate(rs.getString("template"));
	 * siteDes.setItemXPath(rs.getString("item_xp"));
	 * siteDes.setLinkXPath(rs.getString("link_xp"));
	 * siteDes.setTitleXPath(rs.getString("title_xp"));
	 * siteDes.setDescXPath(rs.getString("description_xp"));
	 * siteDes.setThumbNXPath(rs.getString("thumb_xp"));
	 * siteDes.setStatus(rs.getString("status")); listOfWebsites.add(siteDes);
	 * String status = rs.getString("status"); siteDes = new WebsiteDS(
	 * rs.getInt("engineid"), rs.getString("name"), rs.getString("description"),
	 * rs.getString("url"), rs.getString("template"), rs.getString("item_xp"),
	 * rs.getString("link_xp"), rs.getString("description_xp"),
	 * rs.getString("title_xp"), rs.getString("thumb_xp")); //engineid, name,
	 * url, description, status, comments, template, item_xp, link_xp, title_xp,
	 * description_xp, thumb_xp
	 * 
	 * int idVal = rs.getInt("id"); String nameVal = rs.getString("name");
	 * String catVal = rs.getString("category"); System.out.println("id = " +
	 * idVal + ", name = " + nameVal + ", category = " + catVal);
	 * 
	 * ++count; } System.out.println(count + " rows were retrieved");
	 * rs.close(); s.close(); } catch (SQLException e) { e.printStackTrace(); }
	 * return listOfWebsites; }
	 */
	public WebsiteDS readOneDSFromDB(int index) {
		WebsiteDS siteDes = null;
		if (!listOfWebsites.isEmpty()) {
			siteDes = listOfWebsites.get(index);
		}
		return siteDes;
	}

	
	public WebsiteDS readOpenDSFile(String fileAddress, int index) {
		WebsiteDS siteDes;
		/*
		 * String name, String description, String address, String template,
		 * String itemXPath, String linkXPath, String descXPath, String
		 * titleXPath, String thumbNXPath
		 */
		boolean acceptsStopWords = false; 
		siteDes = new WebsiteDS(index, "name", "Description", "adress", "status","template", "comments",
				"itemXPath", "linkXPath", "descXPath", "titleXPath",
				"thumbNXPath", "next_page_xp", acceptsStopWords);
		// fileAddress =
		// "/home/mohammad/uni-work/almer/simpledex/sources/djoerdhiemstra.osdx";
		try {

			File fXmlFile = new File(fileAddress); // "c:\\file.xml"
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			// doc.getDocumentElement().normalize();

			siteDes.setDescription(doc.getElementsByTagName("Description")
					.item(0).getTextContent());

			NodeList nList = doc.getElementsByTagName("Url");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getAttributes().getNamedItem("type").getTextContent()
						.equals("text/html")) {
					if(nNode.getAttributes().getNamedItem("address")!=null){
						siteDes.setAddress(nNode.getAttributes()
								.getNamedItem("address").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("template")!=null){
						siteDes.setTemplate(nNode.getAttributes()
								.getNamedItem("template").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("item_xpath")!=null){
						siteDes.setItemXPath(nNode.getAttributes()
								.getNamedItem("item_xpath").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("link_xpath")!=null){
						siteDes.setLinkXPath(nNode.getAttributes()
								.getNamedItem("link_xpath").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("desc_xpath")!=null){
						siteDes.setDescXPath(nNode.getAttributes()
								.getNamedItem("desc_xpath").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("title_xpath")!=null){
						siteDes.setTitleXPath(nNode.getAttributes()
								.getNamedItem("title_xpath").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("thumbn_xpath")!=null){
						siteDes.setThumbNXPath(nNode.getAttributes()
								.getNamedItem("thumbn_xpath").getTextContent());
					}
					if(nNode.getAttributes().getNamedItem("next_page_xp")!=null){
						siteDes.setNext_page_xp(nNode.getAttributes()
								.getNamedItem("next_page_xp").getTextContent());
					}
				/*	if(nNode.getAttributes().getNamedItem("acceptsStopWords")!=null){
						siteDes.setThumbNXPath(nNode.getAttributes()
								.getNamedItem("acceptsStopWords").getTextContent());
					}*/
				}

			}

			/*
			 * siteDes.setEngineid(rs.getInt("engineid"));
			 * siteDes.setName(rs.getString("name"));
			 * siteDes.setAddress(rs.getString("url"));
			 * siteDes.setDescription(rs.getString("description"));
			 * siteDes.setComments(rs.getString("comments"));
			 * siteDes.setTemplate(rs.getString("template"));
			 * siteDes.setItemXPath(rs.getString("item_xp"));
			 * siteDes.setLinkXPath(rs.getString("link_xp"));
			 * siteDes.setTitleXPath(rs.getString("title_xp"));
			 * siteDes.setDescXPath(rs.getString("description_xp"));
			 * siteDes.setThumbNXPath(rs.getString("thumb_xp")); String status =
			 * rs.getString("status");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return siteDes;
	}
	/*
	 * public WebsiteDS readFromX(String fileAddress) { WebsiteDS siteDes =
	 * null;
	 * 
	 * return siteDes; }
	 */
}
