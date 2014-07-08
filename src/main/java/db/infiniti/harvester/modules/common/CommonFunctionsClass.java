package db.infiniti.harvester.modules.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class CommonFunctionsClass {
	HashSet<String> listOfStopWords;
	
	
	public boolean isStopWord(String query) {
		if (listOfStopWords == null) {
			setListOfStopWords();
		}
		if (listOfStopWords.contains(query)) {
			return true;
		}
		return false;
	}
	//define the set of stopwords here
	HashSet<String> setListOfStopWords() {
		listOfStopWords = new HashSet<String>();
		listOfStopWords.add("the");
		listOfStopWords.add("a");
		listOfStopWords.add("an");
		listOfStopWords.add("of");
		listOfStopWords.add("in");
		listOfStopWords.add("and");
		listOfStopWords.add("is");
		listOfStopWords.add("to");
		listOfStopWords.add("at");
		listOfStopWords.add("on");
		listOfStopWords.add("as");
		listOfStopWords.add("not");
		listOfStopWords.add("from");
		listOfStopWords.add("by");
		listOfStopWords.add("for");
		listOfStopWords.add("was");
		// [the, of, on, and, in, content, to, as, have, not, is, will, home,
		// from, by, on, wikipedia, for, was, site]
		// [the, of, on, and, in, content, to, as, have, not, is, will, home,
		// from, by, on, wikipedia, for, was, site,
		// this, contains, their, as, edit, string, with, there, page, his,
		// also, when, org, here, data, that, wikimedia, me, world, at, video,
		// page, it, powered, content, than, http, links, work, he, had,
		// article, his, back, many, state, please, an, free, are, software,
		// after, or, must, january, cache, centralauth, high, about, be,
		// posted, expires, available, all, travel, book, also, mail, public,
		// internet, right, retrieved, private, national, which, media, game,
		// last, en, text, were, store, new, hotels, search, en, see, changes,
		// has, encyclopedia]
		return listOfStopWords;
	}
	//set list of stop words with a given set
	public void setListOfStopWords(HashSet<String> listOfStopWords) {
		this.listOfStopWords = listOfStopWords;
	}
	
	public String refineText(String pageContent){
		if (pageContent.contains("\n")){
			pageContent = pageContent.replaceAll("\n", " ");
		}
/*		if (pageContent.matches("[.,!?:;'\"-]")){
			pageContent = pageContent.replaceAll("\\p{punct}+", " ");
		}
		if (pageContent.contains("\\p{Punct}")){
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}*/
		if (pageContent.contains("\\") || pageContent.contains(":") || pageContent.contains(";") || pageContent.contains(".")){
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}
		return pageContent.trim();
	}
	
	public String[] tokenizer(String content) {
		String delims = "[+\\-*/\\^ .,?!:;=()]+";
		String[] tokens = content.split(delims);
		return tokens;
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
