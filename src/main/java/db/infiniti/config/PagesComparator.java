package db.infiniti.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import utils.diff_match;
import utils.diff_match.Diff;

public class PagesComparator {
	String pageOnehtml;
	String pageOneText;
	String pageTwohtml;
	String pageTwoText;
	diff_match diff;

	public void setPageOne(String pageOnehtml, String pageOnetext) {
		this.pageOnehtml = pageOnehtml;
		this.pageOneText = pageOnetext;
	}

	public void setPageTwo(String pageTwohtml, String pageTwotext) {
		this.pageTwohtml = pageTwohtml;
		this.pageTwoText = pageTwotext;
	}

	// are you simply checking for the presence of specific fields or other UI
	// elements? Is their order significant? Is their appearance significant?
	// Do you want to compare the DOM, the text, or what?
	public boolean changedMethodTEXT() {// text
		if (pageTwoText.equalsIgnoreCase(pageOneText)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean changedMethod2HTML() {// html
		if (pageTwohtml.equalsIgnoreCase(pageOnehtml)) {
			return false;
		} else {
			return true;
		}
	}

	public Integer changedResults(String query, List<String> recentResults,
			HashMap<String, List<String>> previousQueryResults) {
		List<String> previousResults;
		//HashMap<String, Integer> queryNumberOfResChanges = new HashMap<String, Integer>();
		int numberOfDifferences = 0;
		if (previousQueryResults.containsKey(query)) {
			previousResults = previousQueryResults.get(query);
			numberOfDifferences = compareLists(previousResults,
					recentResults);
		} else {
			System.out
					.println("Query not found in the previous query results list.");
		}
		return numberOfDifferences;// html

	}

	private int compareLists(List<String> previousResults,
			List<String> recentResults) {
		int numberOfchanges = 0;
		for (int i = 0; i < previousResults.size(); i++) {
			String url = previousResults.get(i);
			if (!recentResults.contains(url)) {
				numberOfchanges++;
			}
		}
		return numberOfchanges;
	}

	public boolean changedMethod_diff() {// extra package to compare html pages
		LinkedList<Diff> linkedListDiff = diff.diff_main(pageOneText,
				pageTwoText);
		return true;
	}

	public boolean changedMethod_daisy() {// extra package to compare html pages

		return true;
	}

}
