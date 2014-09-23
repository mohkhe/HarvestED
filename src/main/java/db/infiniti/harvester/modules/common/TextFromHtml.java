package db.infiniti.harvester.modules.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

public class TextFromHtml {

	public String extractTextFromHtml(String htmlCode) {
		String htmlText = "";
		try {
			Source htmlSource = new Source(htmlCode);
			Segment htmlSeg = new Segment(htmlSource, 0, htmlSource.length());
			Renderer htmlRend = new Renderer(htmlSeg);
			htmlRend.setMaxLineLength(3000);
			// int i = htmlRend.getMaxLineLength();

			htmlText = htmlRend.toString();
			htmlText = htmlText.replaceAll("\\*", "").trim();
			htmlText = htmlText.replaceAll("<[^<>]+>", "").trim();

			if (htmlText.equals("") || htmlText.equals("Could not find.")) {
				System.out.println(" is empty.");
			} else {
				return htmlText;
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return htmlText;
	}

	public static void extractTextFromHtmlFilesInFolder() {
		// String htmlContent = readFromFile();
		File folder = new File("crawledData");
		File[] listOfFiles = folder.listFiles();
		FileReader fstream;
		BufferedReader in;
		String line = "";
		String htmlText = "";
		int numberOfEmptyDocs = 0;
		String allText = "";
		int numberOfProcessesDocs = 0;

		System.out.println("Salam" + "");

		for (File file : listOfFiles) {
			try {
				fstream = new FileReader(file);

				htmlText = "";

				in = new BufferedReader(fstream);
				while ((line = in.readLine()) != null) {
					htmlText = htmlText + line.trim();
				}
				Source htmlSource = new Source(htmlText);
				Segment htmlSeg = new Segment(htmlSource, 0,
						htmlSource.length());
				Renderer htmlRend = new Renderer(htmlSeg);
				htmlRend.setMaxLineLength(3000);
				// int i = htmlRend.getMaxLineLength();

				htmlText = htmlRend.toString();
				htmlText = htmlText.replaceAll("\\*", "").trim();
				htmlText = htmlText.replaceAll("<[^<>]+>", "").trim();

				if (htmlText.equals("") || htmlText.equals("Could not find.")) {
					System.out.println(file.getName() + " is empty.");
					numberOfEmptyDocs++;
				} else {
					saveStringInFile(htmlText, "extracted/" + file.getName()
							+ ".txt", true);
				}

				in.close();
				fstream.close();
				numberOfProcessesDocs++;
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
		System.out.println(numberOfEmptyDocs + " documents are empty.");
		System.out.println(numberOfProcessesDocs + " documents are processed.");
	}

	public static void saveStringInFile(String text, String filePath,
			boolean append) {
		try {
			File file = new File(filePath);
			FileWriter fstream = new FileWriter(file, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text + "\n");
			out.flush();
			out.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
