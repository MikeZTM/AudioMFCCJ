package comirva.util.external.retrieval;

import java.io.*;
import java.net.URL;

/**
 * This class analyzes the crawling.txt and writes the file processed_idx.txt,
 * containing all the indices (wrt the crawling.txt) of URLs that really has been
 * retrieved, by analyzing if the files reside on the HDD.
 * 
 * @author mms
 */
public class GoldenRetriever_ProcessedIndexCorrector {
	public static File PROCESSED_IDX_FILE = new File("E:/exalead/processed_idx_correct.txt");	// a text file containing the indices of the URLs already retrieved (indices wrt file URL_FILE)

	public static void main(String[] args) {
		try {
			String line;
			int currentIdx = 0;		// current index in list of URLs
			BufferedReader brUrl = new BufferedReader(new FileReader(GoldenRetriever.URL_FILE));							// all urls to retrieve
			BufferedWriter bwProcessedIdx = new BufferedWriter(new FileWriter(GoldenRetriever_ProcessedIndexCorrector.PROCESSED_IDX_FILE));
			String genre, artist, pageno, pagenoTmp, url;		// the parts to be extracted from the URL_FILE
			File file;			
			int idx;
			while ((line = brUrl.readLine()) != null) {
				// extract information from the line
				// genre
				idx = line.indexOf(",");
				genre = line.substring(0,idx);
				line = line.substring(idx+1);
				// artist
				idx = line.indexOf(",");
				artist = line.substring(0,idx);
				line = line.substring(idx+1);
				// page number
				idx = line.indexOf(",");
				pagenoTmp = line.substring(0,idx);
				switch (pagenoTmp.length()) {	
				case 1: pageno = "00" + pagenoTmp; break;
				case 2: pageno = "0" + pagenoTmp; break;
				default: pageno = pagenoTmp; break;					
				}
				// remaining part is url
				url = line.substring(idx+1);
				// construct file name from the parts extracted
				file = new File(GoldenRetriever.ROOT_DIR.getAbsolutePath() + "/" + genre + "/" + artist.substring(0,1) + "/" + artist + "/" + pageno + ".html");				
				// add file and url to vector (if host name is not in black list)
				URL urlU = new URL(url);
				if (file.exists()) {
					bwProcessedIdx.append(currentIdx + "\n");
					bwProcessedIdx.flush();
					System.out.println(currentIdx + ": " + file + " exists.");
				} else {
//					System.out.println(currentIdx + ": " + file + " does not exist.");
				}
				currentIdx++;
			}
			brUrl.close();
			bwProcessedIdx.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
