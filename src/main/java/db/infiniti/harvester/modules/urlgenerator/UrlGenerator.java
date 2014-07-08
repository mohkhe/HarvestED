/**
 * 
 */
package db.infiniti.harvester.modules.urlgenerator;

/**
 * @author mohammad
 *
 */
public class UrlGenerator implements UrlGeneratorInterface {

	/* (non-Javadoc)
	 * @see db.infiniti.harvester.modules.urlgenerator.UrlGeneratorInterface#generateURL(java.lang.String, java.lang.String)
	 */
	public String generateURL(String tempUrl, String query) {

		String url = null;
		if (tempUrl.contains("{q}")) {
			url = tempUrl.replace("{q}", query);
		} else if (tempUrl.contains("{searchTerms}")) {
			url = tempUrl.replace("{searchTerms}", query);
		} else if (tempUrl.contains("{query}")) {
			url = tempUrl.replace("{query}", query);
		}
		return url = url.replace("amp;", "");
	}

}
