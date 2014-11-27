package comirva.util.external.retrieval;

import java.io.*;

/**
 * Creates a list of all URLs to be crawled from the info.xml files. 
 * 
 * @author mms
 */
public class CrawlListCreator {
	public static File ROOT_DIR = new File("C:/Research/Data/amg-artists/exalead/M/");						// the root path where all artists dir reside
	public static File OUTPUT_FILE = new File("E:/exalead/crawling_test.txt");		// the text file into which all crawling information will be written

	public CrawlListCreator() {
		super();
	}

	/**
	 * Walks through all directories under ROOT_DIR searching for URLs + artist info and
	 * write everything into OUTPUT_FILE.
	 */
	public void createCrawlingInfoFile() {
		// open output file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(CrawlListCreator.OUTPUT_FILE, true));
			BufferedReader br;
			String genre = "", artist = "";
			String rank = "", url = "";
			String line = "";
			// through all genre dirs
			File[] dirGenres = CrawlListCreator.ROOT_DIR.listFiles();
			for (int i=0; i<dirGenres.length; i++) {
				File dirGenre = dirGenres[i];
				if (dirGenre.isDirectory()) {
					genre = dirGenre.getName();
					// skip next level in directory structure (a-z0-9 dirs)
					File[] dirFirstLetterDirs = dirGenre.listFiles();
					for (int j=0; j<dirFirstLetterDirs.length; j++) {
						File dirFirstLetterDir = dirFirstLetterDirs[j];
						if (dirFirstLetterDir.isDirectory()) {
							// all artist directories of current genre
							File[] dirArtists = dirFirstLetterDir.listFiles();
							for (int k=0; k<dirArtists.length; k++) {
								File dirArtist = dirArtists[k];
								if (dirArtist.isDirectory()) {
									artist = dirArtist.getName();
									System.out.println(artist+","+genre);
									// parse info.xml for URLs info
									br = new BufferedReader(new FileReader(new File(dirArtist.getPath() + "/info.xml")));
									while ((line=br.readLine()) !=  null) {
										line = line.trim();
										if (line.startsWith("<URL rank=")) {
											// extract rank
											line = line.substring(11);
											int idxOfEndRank = line.indexOf("\">");
											rank = line.substring(0, idxOfEndRank);
//											System.out.println(rank);
											// extract url
											line = line.substring(idxOfEndRank+2);
											url = line.substring(0, line.length()-6);
//											System.out.println(url);
											bw.append(genre+","+artist+","+rank+","+url+"\n");
											// write info extracted from info.xml to crawling list
											bw.flush();
										}
									}
								}
							}
							System.gc();			// force freeing unused system resources 
						}
					}
				}
			}
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	public static void main(String[] args) {
		CrawlListCreator clc = new CrawlListCreator();
		clc.createCrawlingInfoFile();
	}

}
