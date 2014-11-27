package comirva.util.external.retrieval;

import java.io.*;
import java.util.*;

/**
 * Creates a subset of the complete artist collection's retrieved Web pages 
 * given a text file with the complete paths to the crawl dirs
 * of each artist that should be included. To this end, symbolic links to the
 * original file locations are used.
 * 
 * ! Note that this implementation only works under Linux !
 * 
 * @author mms
 */
public class SubsetCollectionCreation_Linux {
	public static final File ROOT_DIR = new File("/raid/AGMIS/exalead/M");							// the root path where all artists dir reside (where the symlinks will point to)
	public static final File SUBSET_DIR = new File("/raid/AGMIS/exalead/M_top01%");					// the directory under which the symbolic links will be created
	public static final File PATHS = new File("/raid/AGMIS/exalead/artists_top01%.txt");	 		// text file with one complete path of one artist directory (per line) to be included    
	
	public SubsetCollectionCreation_Linux() {
		super();
	}

	/**
	 * Walks through all directories under ROOT_DIR 
	 * creating symlinks under SUBSET_DIR according to PATHS.
	 */
	public void createSubset() {
		// create SUBSET_DIR if not existant
		SubsetCollectionCreation_Linux.SUBSET_DIR.mkdir();
		int error = 0;		// error counter
		int dups = 0;		// duplicates (already existent dirs/symlinks)
		try {
			BufferedReader br = new BufferedReader(new FileReader(SubsetCollectionCreation_Linux.PATHS));
			String line;
			while ((line=br.readLine()) != null) {
				// parse path from PATHS relative to ROOT_DIR
				if (line.indexOf(SubsetCollectionCreation_Linux.ROOT_DIR.getPath()) != -1) {
					String lineRelativePath = line.replace(SubsetCollectionCreation_Linux.ROOT_DIR.getPath(), "");
					File f = new File(SUBSET_DIR, lineRelativePath);
					String newPath = f.getAbsolutePath();
					StringTokenizer st = new StringTokenizer(newPath, "/");
					// tokenize new path
					String dir = "/";
					int noTokens = st.countTokens();
					for (int i=0; i<noTokens-3; i++)
						dir += st.nextToken() + "/";
					// create subdir for genre
					String genre = st.nextToken();
					dir += genre + "/";
					File createDir = new File(dir);
					if (!createDir.exists())
						createDir.mkdir();
					// create subdir for first letter of artist
					String firstLetterOfArtist = st.nextToken();
					dir += firstLetterOfArtist + "/";
					createDir = new File(dir);
					if (!createDir.exists())
						createDir.mkdir();
					// create symbolic link on deepest level (artist-dir-level)
					String artist = st.nextToken();
					dir += artist;
					createDir = new File(dir);
					if (createDir.exists()) {
						System.out.println("directory " + dir + " already exists");
						dups++;
					} else {
						System.out.println("creating symbolic link "+dir);
						Process p = Runtime.getRuntime().exec("ln -s " + line + " " + dir);
						doWaitFor(p); 
						p.destroy();
					}
				} else {
					System.out.println("!!! mismatch error between " + line + " and " + SubsetCollectionCreation_Linux.ROOT_DIR.getPath());
					error++;
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println(error + " errors occurred");
		System.out.println(dups + " links were not created, because directories with the same name already had existed");
	}

	/** 
	 * Method to perform a "wait" for a process and return its exit value.
	 * This is a workaround for <CODE>process.waitFor()</CODE> never returning.
	 */
	public static int doWaitFor(Process p) {
		int exitValue = -1;  // returned to caller when p is finished
		try {
			InputStream in  = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; 		// set to true when p is finished
			while (!finished) {
				try {
					while (in.available() > 0) {
						// get the output of the system call
						Character c = new Character((char)in.read());
//						System.out.print(c);
					}
					while (err.available() > 0) {
						// get the output of the system call
						Character c = new Character((char)err.read());
//						System.out.print(c);
					}
					// Ask the process for its exitValue. If the process
					// is not finished, an IllegalThreadStateException
					// is thrown. If it is finished, we fall through and
					// the variable finished is set to true.
					exitValue = p.exitValue();
					finished  = true;
				} 
				catch (IllegalThreadStateException e) {
					// Process is not finished yet;
					// Sleep a little to save on CPU cycles
					Thread.currentThread().sleep(50);
				}
			}
		} 
		catch (Exception e) {
			// unexpected exception!  print it out for debugging...
			System.err.println( "doWaitFor(): unexpected exception - " + e.getMessage());
		}
		// return completion status to caller
		return exitValue;
	}


	public static void main(String[] args) {
		SubsetCollectionCreation_Linux scc_l = new SubsetCollectionCreation_Linux();
		scc_l.createSubset();
	}

}
