package comirva.util.external.retrieval;

import java.io.*;
import java.util.*;
import comirva.util.VectorSort;

/**
 * Analyzes the info.xml files stored for a crawl and prints out some statistical measures.
 * This class can further be used to generate subsets of the crawled artists. 
 * 
 * @author mms
 */
public class SearchResultsAnalyzer {
	public static File ROOT_DIR = new File("/raid/AGMIS/exalead/M/");							// the root path where all artists dir reside

	// parameters to perform top-X%-artist selection
	public static final boolean PERFORM_TOP_X_SELECTION = false;									// do perform the selection 
	public static final float SELECT_TOP_X_PERCENT_PER_GENRE = 2.0f;								// creates a subset of those X % of the artists for which the most pages were found per genre (those X % with highest page counts per genre) 
	public static File SUBSET_FILE = new File("/raid/AGMIS/exalead/artists_topxx%.txt");			// the file to which path information on the selected subsets is to be written

	public static final boolean OUTPUT_PC_DISTRIBUTIONS_FOR_EACH_GENRE = false;					// writes ordered sets of all PCs for each genre (in ROOT_DIR) 

	
	public SearchResultsAnalyzer() {
		super();
	}

	/**
	 * Walks through all directories under ROOT_DIR searching for crawl info filex (info.xml) and
	 * analyzes them. If desired by user (set PERFORM_TOP_X_SELECTION, a subset of the top-X%-artists is created and written to SUBSET_FILE. 
	 */
	public void analyzeSearchResults() {
		// open output file
		try {
			BufferedWriter bw;
			if (SearchResultsAnalyzer.PERFORM_TOP_X_SELECTION)
				bw = new BufferedWriter(new FileWriter(SearchResultsAnalyzer.SUBSET_FILE));
			BufferedReader br;
			String genre = "", artist = "";
//			String rank = "", url = "";
			String line = "";
			String pc = "";		// page count
			// to store all PCs
			long pcs_Total = 0;						// to sum up the total PC
			long pcs_Retrieved = 0;					// max = #retrieved Web pages (e.g. 100); like pcs_Total, but for every artist, the maximum of his/her PC is 100
			int artists_ZeroPC_Total = 0;			// counter for artists with a PC of 0
			int artists_Total_Count = 0;			// total number of artists over all genres
			long webPages_Zero_Length_Total = 0;	// total number of Web pages with length of 0
			// to store PCs of artists from current genre
			Vector artists_Genre = new Vector();
			Vector pcs_Genre = new Vector();
			Vector retrieved_Genre = new Vector();
			Vector<Integer> pcs_Overall = new Vector<Integer>();		// to store the PC of all artists (from all genres)
			// through all genre dirs
			File[] dirGenres = SearchResultsAnalyzer.ROOT_DIR.listFiles();
			for (int i=0; i<dirGenres.length; i++) {
				File dirGenre = dirGenres[i];
				artists_Genre = new Vector();			// reinitialize vector for new genre
				pcs_Genre = new Vector();				// reinitialize vector for new genre
				retrieved_Genre = new Vector();			// reinitialize vector for new genre
				int artists_ZeroPC_Genre = 0;			// counter for artists in current genre that have a PC of 0
				long pcs_Total_Genre = 0;				// to sum up the total PC of artists in current genre
				long pcs_Retrieved_Genre = 0;			// max = #retrieved Web pages (e.g. 100); like pcs_Total_Genre, but for every artist, the maximum of his/her PC is 100
				int retr_Genre = 0;
				int artists_Genre_Count = 0;			// number of artists in genre
				long webPages_Zero_Length_Genre = 0;	// number of Web pages with size 0 in current genre (retrieval error, no content)
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
									
									// the following code is just for resolving a bug that sometimes make the
									// SubsetCollectionCreation_Linux class produce recursive symbolic links in artist directories
									//
									// search for recursive symbolic links (with same name as artist)
									File[] dirFiles = dirArtist.listFiles();
									for (int m=0; m<dirFiles.length; m++) {
										if (dirFiles[m].getName().equals(artist)) { 
												System.out.println("probable recursive dir found: " + dirFiles[m].getAbsolutePath());
												Process p = Runtime.getRuntime().exec("chmod a+w " + dirFiles[m].getAbsolutePath());
												SubsetCollectionCreation_Linux.doWaitFor(p); 
												p.destroy();
												p = Runtime.getRuntime().exec("rm " + dirFiles[m].getAbsolutePath());
												SubsetCollectionCreation_Linux.doWaitFor(p); 
												p.destroy();
										}			
									}
									// end search for recursive symbolic links
									
									// parse info.xml for URLs info
									br = new BufferedReader(new FileReader(new File(dirArtist.getPath() + "/info.xml")));
									boolean pcFound = false;		// flag to exit following while-loop when <PageCount> tag was found
									while ((line=br.readLine()) !=  null && !pcFound) {
										line = line.trim();
										// read page count
										if (line.startsWith("<PageCount>")) {
											pcFound = true;
											retr_Genre = 0;
											// extract page count
											line = line.substring(11);
											int idxOfEnd = line.indexOf("</PageCount>");
											pc = line.substring(0, idxOfEnd);
											// add info about PC to vectors
											artists_Genre.addElement(artist);
											pcs_Genre.addElement(new Double(pc));
											pcs_Overall.addElement(new Integer(pc));
											artists_Total_Count++;
											artists_Genre_Count++;
											if (Integer.valueOf(pc).intValue() == 0) {
												artists_ZeroPC_Total++;
												artists_ZeroPC_Genre++;
											}
											pcs_Total += Long.valueOf(pc).longValue();
											pcs_Total_Genre += Long.valueOf(pc).longValue();
//											pcs_Retrieved += Math.min(100, Long.valueOf(pc).longValue());
//											pcs_Retrieved_Genre += Math.min(100, Long.valueOf(pc).longValue());
											// get information about 0-byte-files (non-retrievable Web content)
											// calculate number of such 0-byte-files for current artist
											for (int l=0; l<Math.min(100, Integer.valueOf(pc).intValue()); l++) {
												String pagenoTmp = Integer.toString(l+1);
												String pageno;
												switch (pagenoTmp.length()) {	
												case 1: pageno = "00" + pagenoTmp; break;
												case 2: pageno = "0" + pagenoTmp; break;
												default: pageno = pagenoTmp; break;					
												}
												File file = new File(SearchResultsAnalyzer.ROOT_DIR.getAbsolutePath() + "/" + genre + "/" + artist.substring(0,1) + "/" + artist + "/" + pageno + ".html");
												if (file.exists()) {
													pcs_Retrieved++;
													pcs_Retrieved_Genre++;
													retr_Genre++;
													if (file.length() == 0) {
														webPages_Zero_Length_Genre++;
														webPages_Zero_Length_Total++;
													}
												}
											}
											retrieved_Genre.addElement(new Double(retr_Genre));
										}
									}
									br.close();
								}
							}
//							System.gc();			// force freeing unused system resources 
						}
					}
					// current genre processed -> sort vectors
					VectorSort.sortWithMetaData(pcs_Genre, (Vector)artists_Genre.clone());
					VectorSort.sortWithMetaData(retrieved_Genre, (Vector)artists_Genre.clone());				
					// extract top-X%-artists with highest page counts
					System.out.println("Genre: " + genre);
					if (SearchResultsAnalyzer.PERFORM_TOP_X_SELECTION) {
						System.out.println("Top-ranked artists: ");
						for (int l=0; l<artists_Genre.size()*((float)SearchResultsAnalyzer.SELECT_TOP_X_PERCENT_PER_GENRE/100.0f); l++) {
							if (l<10)
								System.out.println(artists_Genre.elementAt(l) + "\t" + Math.round(((Double)pcs_Genre.elementAt(l)).doubleValue()));
							artist = (String)artists_Genre.elementAt(l);
							// add subset to SUBSET_FILE
							bw.append("/raid/AGMIS/exalead/M/"+genre+"/"+artist.substring(0,1)+"/"+artist+"\n");
							bw.flush();
						}
					}
					// write PC distributions if specified
					if (SearchResultsAnalyzer.OUTPUT_PC_DISTRIBUTIONS_FOR_EACH_GENRE) {
						BufferedWriter bwDistr = new BufferedWriter(new FileWriter(SearchResultsAnalyzer.ROOT_DIR + "/" + "pc_distribution_" + genre + ".dat"));
						for (int l=0; l<pcs_Genre.size(); l++)
							bwDistr.append(pcs_Genre.elementAt(l)+"\n");
						bwDistr.flush();
						bwDistr.close();
					}
					System.out.println("Number of artists in genre: "+artists_Genre_Count);
					System.out.println("Number of 0-PC-artists in genre: "+artists_ZeroPC_Genre);
					System.out.println("Number of retrieved artist Web pages in genre: "+pcs_Retrieved_Genre);
					System.out.println("Web pages of zero-length in genre (retrieval error or no content): "+webPages_Zero_Length_Genre);
					System.out.println("Median of PCs in genre: "+Math.round(((Double)pcs_Genre.elementAt(Math.round(artists_Genre_Count/2))).doubleValue()));
					System.out.println("Mean of PCs in genre: "+Math.round(pcs_Total_Genre/artists_Genre_Count));
					System.out.println("Median  of PCs of actually retrieved pages: "+Math.round(((Double)retrieved_Genre.elementAt(Math.round(artists_Genre_Count/2))).doubleValue()));
					System.out.println("Mean of PCs of actually retrieved pages: "+pcs_Retrieved_Genre/artists_Genre_Count);
				}
			}
			// all genres processed -> calcuate statistical measures
			Collections.sort(pcs_Overall);
			System.out.println("Total number of artists: "+artists_Total_Count);
			System.out.println("Total number of 0-PC-artists: "+artists_ZeroPC_Total);		
			System.out.println("Total number of retrieved artist Web pages: "+pcs_Retrieved);
			System.out.println("Total number of Web pages of zero-length (retrieval error or no content): "+webPages_Zero_Length_Total);
			System.out.println("Overall median of PCs: "+Math.round((pcs_Overall.elementAt(Math.round(pcs_Overall.size()/2)))));
			System.out.println("Overall mean of PCs: "+pcs_Total/artists_Total_Count);					
			System.out.println("Overall mean of PCs of actually retrieved pages: "+pcs_Retrieved/artists_Total_Count);					
			// close SUBSET_FILE
			if (SearchResultsAnalyzer.PERFORM_TOP_X_SELECTION)
				bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}		
	}


	public static void main(String[] args) {
		SearchResultsAnalyzer sra = new SearchResultsAnalyzer();
		sra.analyzeSearchResults();
	}

}
