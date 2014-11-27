package comirva.util.external;

import java.io.File;

/**
 * Analyzes a given directory, searches for subdirectories and sorts the found subdirs into
 * subdirs created from the first letter of the original subdirs.
 * 
 * @author mms
 */
public class SubDirCategorizer {
	private static String DIR = "C:/Research/Data/amg-artists/exalead/M/gospel";	
	public SubDirCategorizer() {
		super();
	}
	
	public static void main(String[] args) {
		// create storage path directory and init data structures
		try {
			// make sure that storage path ends with a slash
			if (DIR.charAt(DIR.length()-1) != '/' && DIR.charAt(DIR.length()-1) != '\\')
				DIR+="/";
			// create output directory if it does not already exist
			File fileDir = new File(DIR);
			if (fileDir.isDirectory()) {			// given DIR must be directory
				File[] files = fileDir.listFiles();
				for (int i=0; i<files.length; i++) {
					File f = files[i];
					System.out.println("Moving directory " + f.getName());
					String path = fileDir.getAbsoluteFile() + "/" + f.getName().substring(0, 1) + "/";
					File dir = new File(path);
					if (!dir.isDirectory())
						dir.mkdir();
					f.renameTo(new File(dir, f.getName()));
				}
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
