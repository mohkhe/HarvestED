package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FreqInClueWeb {

	static HashMap<String, Integer> termFreqInClueWeb;

	static ArrayList<String> sentqueris;
	
	public static void main(String[] args){
		double averageFreq = 0;
		double howManyIn = 0;
		setTermFreqInClueWeb("querypool/wikiwebsorted");
		//3 in words-105000-freq
		//5 in words-300000-freq
		sentqueris = readLinesFromFile("querypool/sentqueries-lfl");
		for(String tempQuery : sentqueris){
			if(termFreqInClueWeb.containsKey(tempQuery)){
				int freq = termFreqInClueWeb.get(tempQuery);
				System.out.println(tempQuery + "\t" + freq);
				averageFreq = (double) freq + averageFreq;
				howManyIn++;
			}else{
				System.out.println(tempQuery + "\t" + "notinlist");
			}
		}
		System.out.println("average: " + averageFreq);
		System.out.println("howManyIn: " + howManyIn + " from: " + sentqueris.size());
		System.out.println("average: " + averageFreq/howManyIn);
		
	}
	public static ArrayList<String> readLinesFromFile(String path) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			File file = new File(path);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line = "";
			while ((line = in.readLine()) != null) {
			
					lines.add(line.trim());
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return lines;
	}
	
	public static void setTermFreqInClueWeb(String filePath) {
		termFreqInClueWeb = new HashMap<String, Integer>();
		try {
			File file = new File(filePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line;
			while ((line = in.readLine()) != null) {
				// String line = in.readLine();
				String[] a = line.split("\t");
				String query = a[0];
				int freq = Integer.parseInt(a[1]);
				// String query = line.replaceAll("[0-9]*", "").trim();
				if (!termFreqInClueWeb.containsKey(query)) {
					termFreqInClueWeb.put(query.intern(), freq);
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
