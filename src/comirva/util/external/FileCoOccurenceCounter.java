package comirva.util.external;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import comirva.data.DataMatrix;
import comirva.exception.SizeMismatchException;

/** 
 * @author mdopler
 * 
 * Counts in how many files (eg. html) of an artist B an artist A occurs 
 * and returns an cooccurence matrix as result.
 * 
 * under construction ;)
 */
public class FileCoOccurenceCounter {
	
	//regex
	private static final String CHARS_TO_DELETE = "[ \n-]";
	
	private static final int NUMBER_OF_FILES_TO_PARSE = 150;
	//keySet() should return an ascending ordered set, therefore java.util.TreeMap is used
	private TreeMap<String, File> bandDirectoryMapping = new TreeMap<String, File>();
	//required because directory names where mutilated with the textformatter
	private Hashtable<String, String> nameMapping = new Hashtable<String, String>();
	
	private String fileEnding = null;
	
	private int numberOfFilesToParse;
	
	public FileCoOccurenceCounter(String filePath, Vector<String> names) {
		this(filePath, names, null, 100);
	}
	
	public FileCoOccurenceCounter(String filePath, Vector<String> names, String fileEnding, int numberOfFilesToParse) {
		this.numberOfFilesToParse = numberOfFilesToParse;
		this.fileEnding = fileEnding;
		File rootDirectory = new File(filePath);
		if(!rootDirectory.isDirectory())
			throw new RuntimeException("ERROR: " + filePath + " is not an existing directory");
		for(File child: rootDirectory.listFiles()) 
			if(child.isDirectory()) 
				bandDirectoryMapping.put(child.getName(), child);
		System.out.println("Put " + bandDirectoryMapping.size() + " bands into hashtable.");
		for(String name: names) 
			nameMapping.put(TextFormatTool.removeUnwantedChars(name), name.replaceAll(CHARS_TO_DELETE, ""));
	}
	
	public DataMatrix getCoOccMatrix() {
		DataMatrix coOccMatrix = new DataMatrix("CoOccurence Matrix"); 
		try {
			int i = 0;
			for(String bandName: bandDirectoryMapping.keySet())  {
				System.out.println(bandName);
				coOccMatrix.insertRow(countCoOccurences(bandName), i++);
			}
		} catch (SizeMismatchException e) {
			e.printStackTrace();
		}		
		return coOccMatrix;
	}
	
	
	private Vector<Double> countCoOccurences(String bandName) {
		TreeMap<String, Double> countMap = new TreeMap<String, Double>();
		
		for(String band: bandDirectoryMapping.keySet()) 
			countMap.put(band, new Double(0.0));
		File[] files = bandDirectoryMapping.get(bandName).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(fileEnding == null)
					return true;
				return pathname.getName().endsWith(fileEnding);
			}			
		});
		int filesParsed = 0;
		for(File file: files) {		
			if(file.length() != 0 && filesParsed < NUMBER_OF_FILES_TO_PARSE) {
				fileIncludesBands(file, countMap);
				filesParsed++;
			}
		}
		
		Vector<Double> result = new Vector<Double>(countMap.values());
		System.out.println(bandName + " " + nameMapping.get(bandName).toLowerCase());
		System.out.println(result);
		return result;
	}
	
	/*
	 * checks if a file contains the given bands and increases the corresponding count value of the map by 1 if so
	 */
	private void fileIncludesBands(File file, TreeMap<String, Double> countMap) {
		HashSet<String> includedBands = new HashSet<String>();
		try {
			StringBuilder sbFileContent = new StringBuilder();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = in.readLine()) != null) 
				sbFileContent.append(line);
			String fileContent =  sbFileContent.toString();
			fileContent = fileContent.replaceAll(CHARS_TO_DELETE, "").toLowerCase();
			//System.out.println(fileContent);
			for(String band: bandDirectoryMapping.keySet()) {
				if(fileContent.contains(nameMapping.get(band).toLowerCase())) 
					includedBands.add(band);
			}

			for(String includedBand: includedBands) 
				countMap.put(includedBand, Double.valueOf(countMap.get(includedBand).doubleValue() + 1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfFilesToParse() {
		return numberOfFilesToParse;
	}

	public void setNumberOfFilesToParse(int numberOfFilesToParse) {
		this.numberOfFilesToParse = numberOfFilesToParse;
	}
	
	public static void main(String[] args) {
		File nameFile = new File("C:/2545/ordered_artists.dat");
		Vector<String> names = new Vector<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(nameFile));
			String line = null;
			while((line = in.readLine()) != null) {
				names.add(line);
				System.out.println("added '" + line + "'");
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FileCoOccurenceCounter counter = new FileCoOccurenceCounter("C:/2545/", names, ".html", 150);
		DataMatrix dm = counter.getCoOccMatrix();
		
		File fileData = new File("C:/2545/cooccurrences150b.dat");
		try {
			Writer ow = new BufferedWriter(new FileWriter(fileData));

			for (int i=0; i<dm.getNumberOfRows(); i++) {
				Vector<Double> row = dm.getRow(i);
				for (int j=0; j<row.size(); j++) {
					ow.write(row.elementAt(j).toString()+"\t"); 
				}
				ow.write("\n");
			}
			ow.flush();
			ow.close();
		} catch (Exception e) {
		}	
	}
}
