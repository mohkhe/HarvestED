package db.infiniti.config;

import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor {

	TObjectIntHashMap<String>  setTermFreq = new TObjectIntHashMap<String>();
	THashSet<String> listOfStopWords;

	public HashMap<String, Integer> setTermFreq(String pageContent) {
		HashMap<String, Integer> termFreqSet = new HashMap<String, Integer>();
		pageContent = this.refineText(pageContent, true);
		String[] tokens = tokenizer(pageContent);
		for (String token : tokens) {
			if (!token.equalsIgnoreCase("") && !this.isRefinedQueryStopWordLength(token)) {
				// it is not used before
				if (termFreqSet.containsKey(token)) {
					termFreqSet.put(token.intern(), termFreqSet.get(token) + 1);
				} else {
					termFreqSet.put(token.intern(), 1);
				}
			}
		}
		return termFreqSet;
	}

	public String getMostFreqQuery(
			HashMap<String, Integer> set, String initialQuery, ArrayList<String> sentQueries) {
		if(!set.isEmpty()){
			List<String> mapKeys = new ArrayList<String>(set.keySet());
			List<Integer> mapValues = new ArrayList<Integer>(set.values());
			Collections.sort(mapValues);
		
			Iterator<Integer> valueIt = mapValues.iterator();
			// TODO check if this is the most frequent and not the least frequent
			int val = (Integer) valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int comp1 = set.get(key);

				if (comp1 == val && comp1 != 0 && !initialQuery.contains(key) && !sentQueries.contains(key)) {
					return key;
				}
			}
		}
		return null;
	}
	
	public String getLeastFreqQuery(HashMap<String, Integer> set, List<String> initialQuery, ArrayList<String> sentQueries) {

		if(set != null){
			if(set.isEmpty() == false){
				List<String> mapKeys = new ArrayList<String>(set.keySet());
				List<Integer> mapValues = new ArrayList<Integer>(set.values());
				Collections.sort(mapValues);
				
				Iterator<Integer> valueIt = mapValues.iterator();
				// TODO check if this is the most frequent and not the least frequent
				int val = (Integer) valueIt.next();
				Iterator<String> keyIt = mapKeys.iterator();
				while (keyIt.hasNext()) { // we set value to 0 not to use it later
					String key = (String) keyIt.next();
					int comp1 = set.get(key);
		
					if (val == comp1 && comp1 != 0 && !initialQuery.contains(key) && !sentQueries.contains(key)) {
						return key;
					}
				}
			}
		}
		return null;
	}
	
	public String refineText(String pageContent, boolean loweCase) {

		if (pageContent.contains("\n")) {
			pageContent = pageContent.replaceAll("\n", " ");
		}
		if (pageContent.contains("\\") || pageContent.contains(":")
				|| pageContent.contains(";") || pageContent.contains(".")) {
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}
		if(loweCase){
			pageContent = pageContent.toLowerCase();
		}
		return pageContent.trim();
	}


	// if numbers => stopword
	// if term contains numbers => stopword
	// if numbers of less than 2 => stopword
	public boolean isRefinedQueryStopWordLength(String query) {
		if (listOfStopWords == null) {
			setListOfStopWords();
		}
		if (query.length() < 3) {
			return true;
		}
		if (query.matches(".*\\d.*")) {
			return true;
		}
		if (query.contains(".*\\d.*")) {
			return true;
		}

		if (listOfStopWords.contains(query)) {
			return true;
		}
		
		if(query.contains(",")
				|| query.contains(".")
				|| query.contains("'")
				|| query.contains("_")
				|| query.contains("+")
				|| query.contains("=")
				|| query.contains(")")
				|| query.contains("(")
				|| query.contains("*")
				|| query.contains("&")
				|| query.contains("^")
				|| query.contains("%")
				|| query.contains("$")
				|| query.contains("#")
				|| query.contains("@")
				|| query.contains("!")
				|| query.contains("~")
				|| query.contains("`")
				|| query.contains("?")
				|| query.contains("\\")
				|| query.contains("/")
				|| query.contains("\\.")
				|| query.contains("/")
				|| query.contains("/")
				|| query.contains("/")
				|| query.contains("/")
				|| query
						.matches("[_@#$%&!~`+\\-*/\\^ \\.,?!:;=()\\']+")){
			return true;
		}
		if(query.contains("(.)*(.)\\1{2,}(.)*")){
			return true;
		}
		Pattern pattern = Pattern.compile("(.)*\\1{2,}(.)*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if(m.matches()){
			return true;
		}
		if(query.matches("(.)\\1{2,}")){
			return true;
		}
		return false;
	}

	public String refineText(String pageContent) {

		if (pageContent.contains("\n")) {
			pageContent = pageContent.replaceAll("\n", " ");
		}
		/*
		 * if (pageContent.matches("[.,!?:;'\"-]")){ pageContent =
		 * pageContent.replaceAll("\\p{punct}+", " "); } if
		 * (pageContent.contains("\\p{Punct}")){ pageContent =
		 * pageContent.replaceAll("\\p{Punct}", " "); }
		 */
		if (pageContent.contains("\\") || pageContent.contains(":")
				|| pageContent.contains(";") || pageContent.contains(".")) {
			pageContent = pageContent.replaceAll("\\p{Punct}", " ");
		}
		return pageContent.trim();
	}

	public String[] tokenizer(String content) {
		String delims = "[+\\-*/\\^ .,?!:;=()]+";
		String[] tokens = content.split(delims);
		return tokens;
	}

	public Set<String> setListOfStopWords() {
		listOfStopWords = new THashSet<String>();
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
		listOfStopWords.add("you");
		listOfStopWords.add("your");
		listOfStopWords.add("one");
		listOfStopWords.add("up");
		listOfStopWords.add("us");
		listOfStopWords.add("other");
		listOfStopWords.add("others");
		listOfStopWords.add("over");
		listOfStopWords.add("home");
		listOfStopWords.add("our");
		listOfStopWords.add("first");
		
		listOfStopWords.add("het");
		listOfStopWords.add("de");
		listOfStopWords.add("en");
		listOfStopWords.add("met");
		listOfStopWords.add("andere");
		listOfStopWords.add("tussen");
		listOfStopWords.add("van");
		listOfStopWords.add("een");
		listOfStopWords.add("pagina");
		listOfStopWords.add("deze");
		
		listOfStopWords.add("can");
		listOfStopWords.add("could");
		listOfStopWords.add("have");
		listOfStopWords.add("had");
		listOfStopWords.add("will");
		listOfStopWords.add("would");
		listOfStopWords.add("there");
		listOfStopWords.add("with");
		listOfStopWords.add("wikipedia");
		listOfStopWords.add("wikimedia");
		listOfStopWords.add("also");
		listOfStopWords.add("org");
		listOfStopWords.add("here");
		listOfStopWords.add("there");
		listOfStopWords.add("data");
		listOfStopWords.add("that");
		listOfStopWords.add("this");
		listOfStopWords.add("these");
		listOfStopWords.add("those");
		listOfStopWords.add("me");
		listOfStopWords.add("her");
		listOfStopWords.add("his");
		listOfStopWords.add("world");
		listOfStopWords.add("at");
		listOfStopWords.add("was");
		listOfStopWords.add("were");
		listOfStopWords.add("page");
		listOfStopWords.add("new");
		listOfStopWords.add("all");
		listOfStopWords.add("also");
		listOfStopWords.add("public");
		listOfStopWords.add("next");
		listOfStopWords.add("last");
		listOfStopWords.add("book");
		listOfStopWords.add("than");
		listOfStopWords.add("which");
		listOfStopWords.add("when");
		listOfStopWords.add("see");
		listOfStopWords.add("many");
		listOfStopWords.add("has");
		listOfStopWords.add("are");
		listOfStopWords.add("com");
		listOfStopWords.add("or");
		listOfStopWords.add("more");
		listOfStopWords.add("be");
		listOfStopWords.add("its");
		listOfStopWords.add("data");
		listOfStopWords.add("please");
		listOfStopWords.add("http");
		listOfStopWords.add("links");
		listOfStopWords.add("their");
		listOfStopWords.add("page");
		listOfStopWords.add("about");
		listOfStopWords.add("high");
		listOfStopWords.add("must");
		listOfStopWords.add("see");
		listOfStopWords.add("book");
		/*
		 * String[] a = new String[]{"the", "of", "on", "and", "in", "content",
		 * "to", "as", "have","not", "is", "will", "home", "from", "by", "on",
		 * "wikipedia", "for", "was", "site", "this", "contains", "their", "as",
		 * "edit", "term", "with", "there", "page", "his", also, when, org,
		 * here, data, that, wikimedia, me, world, at, video, page, it, powered,
		 * content, than, http, links, work, he, had, article, his, back, many,
		 * state, please, an, free, are, software, after, or, must, january,
		 * cache, centralauth, high, about, be, posted, expires, available, all,
		 * travel, book, also, mail, public, internet, right, retrieved,
		 * private, national, which, media, game, last, en, text, were, store,
		 * new, hotels, search, en, see, changes, has, encyclopedia};
		 */
		return listOfStopWords;
	}
}
