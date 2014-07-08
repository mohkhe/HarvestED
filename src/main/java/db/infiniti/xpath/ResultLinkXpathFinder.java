package db.infiniti.xpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/*import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;*/

public class ResultLinkXpathFinder {
/*	ArrayList<String> linkDetectionxPath = new ArrayList<String>();
	LinkedHashMap<HtmlElement, ArrayList<HtmlElement>> candidatesForEachElement = new LinkedHashMap<HtmlElement, ArrayList<HtmlElement>>();

	*//**
	 * @param addressComponent
	 * @param xPathSource
	 * @param numOfTries
	 * @return
	 *//*
	public ArrayList<String> findResultLinkxPath(HtmlElement addressComponent,
			String xPathSource, int numOfTries) {
		String componentxPath = addressComponent.getCanonicalXPath();

		Iterable<HtmlElement> iterChild = addressComponent
				.getElementsByTagName("a");
		Iterator<HtmlElement> aChildren = iterChild.iterator();

		ArrayList<HtmlElement> resultLinkCandidates = new ArrayList<HtmlElement>();
		while (aChildren.hasNext()) {
			HtmlElement aChild = (HtmlElement) aChildren.next();

			if (aChild.hasAttribute("href")) { //&& checkIfParentIsCorrect(aChild, xPathSource)
				// String resultLinkCandidate = aChild.getAttribute("href");
				resultLinkCandidates.add(aChild);
			}
		}
		candidatesForEachElement.put(addressComponent, resultLinkCandidates);
		// choose the first one
		if (numOfTries < resultLinkCandidates.size()) {
			linkDetectionxPath.add(resultLinkCandidates.get(numOfTries)
					.getCanonicalXPath().replace(componentxPath, ""));
		} else {
			if (resultLinkCandidates.size() == 0) {
				System.err.println("No <a href > in the HtmlElement, No candidates.");
			}
			System.err.println("no option for link detection in HtmlElement.");
		}

		return linkDetectionxPath;
	}

	*//**
	 * @param aChild
	 * @param xPathSource
	 *            checks if the parents of a has the tag in //tr[./td/a],
	 *            parents of a till tr are checked to be td or not
	 * @return
	 *//*
	private boolean checkIfParentIsCorrect(HtmlElement aChild,
			String xPathSource) {
		boolean aHasCorrectParentTag = false;
		String parentTagOfAnchor = getParentNodeTagOfA(xPathSource);
		if (!parentTagOfAnchor.equals("")) {// it has a dad
			String endTagOfComponentXpath = getEndTag(xPathSource);
			DomNode parentNode = aChild.getParentNode();
			String parentNodeName = parentNode.getNodeName();
			while (!parentNodeName.equalsIgnoreCase(endTagOfComponentXpath)) {
				if (parentNodeName.equalsIgnoreCase(parentTagOfAnchor)) {
					aHasCorrectParentTag = true;
					break;
				} else {
					parentNode = parentNode.getParentNode();
					parentNodeName = parentNode.getNodeName();
				}
			}
		} else {
			aHasCorrectParentTag = true;
		}
		return aHasCorrectParentTag;
	}

	// extract li from //li[./div/a]
	private String getEndTag(String xPathSource) {
		
		 * String endTagOfComponentXpath = xPathSource.substring(
		 * xPathSource.lastIndexOf("//") + 2, xPathSource.indexOf("["));
		 
		// //li[./div/a]
		String endTagOfComponentXpath = xPathSource.substring(0,
				xPathSource.indexOf("[")+1);
		endTagOfComponentXpath = endTagOfComponentXpath.substring(
				endTagOfComponentXpath.lastIndexOf("/") + 1, endTagOfComponentXpath.indexOf("["));

		return endTagOfComponentXpath;
	}

	*//**
	 * @param xPathSource
	 *            prepares the xath to be added to the ResultComponent xPath
	 *            finds the parent node tag of the <a>
	 * @return
	 *//*
	private String getParentNodeTagOfA(String xPathSource) {
		System.out.println("Xpath to be refined: " + xPathSource);
		xPathSource = xPathSource.replaceFirst("//[a-z]*.", "");
		xPathSource = xPathSource.replace("[", "");
		xPathSource = xPathSource.replace("]", "");
		xPathSource = xPathSource.replace(".", "");
		// //div/p[./a]
		if (!xPathSource.equals("//a")) {
			String parentOfAnchor = xPathSource.substring(
					xPathSource.indexOf("/") + 1, xPathSource.lastIndexOf("/"));
			return parentOfAnchor;
		} else {
			return "";
		}

	}

	public ArrayList<String> findResultLinkxPathThroughUserInteraction(
			String sourceURL, HtmlElement addressComponent, String xPathSource,
			boolean findxPath) {
		String componentxPath = addressComponent.getCanonicalXPath();
		if (findxPath) {
			String refinedxPathSource = refinexPath(xPathSource);
			Iterable<HtmlElement> iterChild = addressComponent
					.getElementsByTagName("a");
			Iterator<HtmlElement> aChildren = iterChild.iterator();
			while (aChildren.hasNext() && findxPath == true) {
				HtmlElement aChild = (HtmlElement) aChildren.next();
				String resultLinkCandidate = aChild.getAttribute("href");
				String userAnswer = CheckLinkWithUser(resultLinkCandidate);
				if (userAnswer.equalsIgnoreCase("yes")) {// returns yes or no
					linkDetectionxPath.add(aChild.getCanonicalXPath().replace(
							componentxPath, ""));
					// firstTryForLinkDetection = false;
					findxPath = false;
				}
			}
		}
		return linkDetectionxPath;
	}

	*//**
	 * @param xPathSource
	 * @return
	 *//*
	private String refinexPath(String xPathSource) {
		xPathSource = xPathSource.replaceFirst("//[a-z]*.", "");
		xPathSource = xPathSource.replace("[", "");
		xPathSource = xPathSource.replace("]", "");
		xPathSource = xPathSource.replace(".", "");
		return xPathSource;
	}

	*//**
	 * @param link
	 * @return
	 *//*
	private String CheckLinkWithUser(String link) {
		System.out.println("Is this the correct link for a rsult: " + link
				+ "\n Is this the correct one?(Y/N)");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (line.equalsIgnoreCase("y")) {
			return "yes";
		} else {
			return "no";
		}
	}

	public LinkedHashMap<HtmlElement, ArrayList<HtmlElement>> getCandidatesForEachElement() {
		return candidatesForEachElement;
	}

	public void setCandidatesForEachElement(
			LinkedHashMap<HtmlElement, ArrayList<HtmlElement>> candidatesForEachElement) {
		this.candidatesForEachElement = candidatesForEachElement;
	}*/
}
