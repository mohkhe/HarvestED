package db.infiniti.harvester.modules.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class Cache {

	HashMap<String, Integer> cachedURLsFileNames = new HashMap<String, Integer>();
	BufferedWriter out;
	FileWriter fstream;
	String cacheMapDirectoryPath;
	String cacheMapFilePath ;

	int lastChachedPageFileNumber = 0;

	public String getCacheMapFilePath() {
		return cacheMapDirectoryPath;
	}

	public void setCacheMapFilePath(String path) {
		this.cacheMapDirectoryPath = path;
		File file = new File(cacheMapDirectoryPath);
		if (!file.exists()) {
			file.mkdir();
		}
		cacheMapFilePath = cacheMapDirectoryPath+"cashedurlsfilenames.txt";
	}

	public void prepareCacheReadWrite() {
		
		try {
			fstream = new FileWriter(cacheMapFilePath, true);
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readCacheMap() {
		try {
			File file = new File(cacheMapFilePath);
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line = "";
			while ((line = in.readLine()) != null) {
				String[] temp = line.split("\t");
				if (temp != null && temp.length > 1) {
					int fileNumberInCache = Integer.parseInt(temp[1]);
					cachedURLsFileNames.put(temp[0], fileNumberInCache);
					if (fileNumberInCache > lastChachedPageFileNumber) {
						lastChachedPageFileNumber = fileNumberInCache;
					}
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void saveAllDataInCacheFile() {
		// the map
		try {
			Iterator<String> keyIt = cachedURLsFileNames.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = (String) keyIt.next();
				int fileNumberInCache = cachedURLsFileNames.get(key);
				out.write(key + "\t" + fileNumberInCache + "\n");
				out.flush();
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void saveInCache(String URL, String pageContent, String pageHTML) {
		try {
			this.cachedURLsFileNames.put(URL, lastChachedPageFileNumber);
			out.write(URL + "\t" + lastChachedPageFileNumber + "\n");
			out.flush();
			
			FileWriter fstreamFile = new FileWriter(cacheMapDirectoryPath+lastChachedPageFileNumber+".txt", false);
			BufferedWriter outFile = new BufferedWriter(fstreamFile);
			outFile.write(pageContent);
			outFile.flush();
			outFile.close();
			fstreamFile.close();
			
			fstreamFile = new FileWriter(cacheMapDirectoryPath+lastChachedPageFileNumber+".html", false);
			outFile = new BufferedWriter(fstreamFile);
			outFile.write(pageHTML);
			outFile.flush();
			outFile.close();
			fstreamFile.close();
			
			lastChachedPageFileNumber++;
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public boolean existsAlreadyInCache(String url) {
		if (this.cachedURLsFileNames.containsKey(url)) {
			return true;
		} else {
			return false;
		}
	}

	public String getPageTextContentFromCache(String url) {
		//alreadychecked if it exists
		int fileNumberInCache = cachedURLsFileNames.get(url);
		String pageContent = "";
		try {
			long time = System.currentTimeMillis();
			if(fileNumberInCache == 6549 || fileNumberInCache == 5379){
				return pageContent; 
			}
			File file = new File(cacheMapDirectoryPath+fileNumberInCache+".txt");
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line = "";
			while ((line = in.readLine()) != null) {
				pageContent = pageContent + line;
				if((System.currentTimeMillis()-time)>4000){
					break;
				}
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return pageContent;
	}
	
	public String getPageHTMLContentFromCache(String url) {
		//alreadychecked if it exists
		int fileNumberInCache = cachedURLsFileNames.get(url);
		String pageContent = "";
		try {
			File file = new File(cacheMapDirectoryPath+fileNumberInCache+".html");
			FileReader fstream = new FileReader(file);
			BufferedReader in = new BufferedReader(fstream);
			String line = "";
			while ((line = in.readLine()) != null) {
				pageContent = pageContent + line;
			}
			in.close();
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return pageContent;
	}
	
	
	public void closeCache(){
		try {
			this.out.close();
			this.fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}
