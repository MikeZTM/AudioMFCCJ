package comirva.util.external.dopler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import comirva.mlearn.GHSOM;
import comirva.mlearn.ghsom.WebCoocIndividualPrototypeFinder;

public class PrototypeEvaluationCreator {

	private static final int NUMBER_OF_EVALUTION_SHEETS = 20;
	private static final int NUMBER_OF_RANDOM_TRACKS = 9;
	private static final String GHSOM_PATH = "H:/Evaluierung/reference_ghsom_13_2545.ghs";
	private static final String TARGET_DIRECTORY = "H:/Evaluierung/";
	private static final String MUSIC_FILE_DIRECTORY = "H:/Musik/";
	private static final String README_FILE_URL_DIRECTORY = "H:/Evaluierung/";
	
	private static int sameSetNumber = -1;
	private static List<Integer> sameSetForAll = new ArrayList<Integer>();

	public static void main(String[] args) throws IOException {
		GHSOM ghsom = EvalUtil.loadGHSOM(GHSOM_PATH);

		if (ghsom != null) {
			Random randomizer = new Random(System.currentTimeMillis());
			WebCoocIndividualPrototypeFinder prototypeFinder = new WebCoocIndividualPrototypeFinder();
			GHSOM currentSOM = ghsom.getSubSOM(0);
			
			//create one group, which contains the same songs for all evaluation sheets
			sameSetNumber = randomizer.nextInt(currentSOM.voronoiSet.size());
			System.out.println("Testset " + sameSetNumber + " is the same for all.");
			
			// for every evaluation sheet
			for (int i = 0; i < NUMBER_OF_EVALUTION_SHEETS; i++) {
				// create own directory
				String directoryString = TARGET_DIRECTORY + "eval_sheet_" + getIntegerString(i,2) + "/";
				File directory = new File(directoryString);
				directory.mkdir();
				
				copyFileIntoDirectory(README_FILE_URL_DIRECTORY, "readme.txt", directoryString);
				
				File resultCVS = new File(directoryString + "result.csv");
				resultCVS.createNewFile();
				FileWriter writer = new FileWriter(resultCVS);
				writer.write("sheet;testset;songid;songname;prototype;div\n");
				writer.write(";;;;;\n");

				// for every node of level 1
				for (int j = 0; j < currentSOM.voronoiSet.size(); j++) {
					writer.write(";;;;;\n");
					// create own directory
					String subDirectoryString = directoryString + "testset_" + getIntegerString(j,2) + "/";
					File subDirectory = new File(subDirectoryString);
					subDirectory.mkdir();

					
					if(sameSetNumber == j && !sameSetForAll.isEmpty()) {
						int pos = 0;
						for(Integer songId: sameSetForAll) {
							copyFileIntoDirectory(MUSIC_FILE_DIRECTORY, 
									currentSOM.getLabel(songId.intValue()) + ".mp3", subDirectoryString, pos++);
							writer.write(i+ ";" + j + ";" + songId.intValue() + ";" + currentSOM.getLabel(songId.intValue()) + ";;;\n");
						}
						
					} else {
						int randomPosOfPrototype = randomizer.nextInt(NUMBER_OF_RANDOM_TRACKS + 1);
						// get the most representative track
						int prototypeIndex = prototypeFinder.getIndexOfPrototype(currentSOM, j);
						copyFileIntoDirectory(MUSIC_FILE_DIRECTORY, 
								currentSOM.getLabel(prototypeIndex) + ".mp3", subDirectoryString, randomPosOfPrototype);
						
						Vector<Integer> currentVoronoiSet = currentSOM.voronoiSet.get(j);
						Set<Integer> randomedIndices = new HashSet<Integer>();
						int trackNumber = 0;
						for(int k = 0; k < NUMBER_OF_RANDOM_TRACKS; k++) {
					
							if(trackNumber == randomPosOfPrototype) {
								writer.write(i+ ";" + j + ";" + prototypeIndex + ";" + currentSOM.getLabel(prototypeIndex) + ";;;\n");
								if(sameSetNumber == j)
									sameSetForAll.add(new Integer(prototypeIndex));
								trackNumber++;
							}
							
							int randIndex = currentVoronoiSet.get(randomizer.nextInt(currentVoronoiSet.size())).intValue();
							while (randIndex == prototypeIndex || randomedIndices.contains(new Integer(randIndex))) 
								randIndex = currentVoronoiSet.get(randomizer.nextInt(currentVoronoiSet.size())).intValue();
							randomedIndices.add(new Integer(randIndex));
							copyFileIntoDirectory(MUSIC_FILE_DIRECTORY, currentSOM.getLabel(randIndex) + ".mp3", subDirectoryString, trackNumber);
							if(sameSetNumber == j)
								sameSetForAll.add(new Integer(randIndex));
							writer.write(i+ ";" + j + ";" + randIndex + ";" + currentSOM.getLabel(randIndex) + ";;;\n");
							trackNumber++;
						}
						
						if(trackNumber == NUMBER_OF_RANDOM_TRACKS) {
							writer.write(i+ ";" + j + ";" + prototypeIndex + ";" + currentSOM.getLabel(prototypeIndex) + ";;;\n");
							if(sameSetNumber == j)
								sameSetForAll.add(new Integer(prototypeIndex));
							trackNumber++;
						}
					}

				}
				writer.flush();
				writer.close();
			}
		}
	}

	private static String getIntegerString(int value, int numberOfDigits) {
		StringBuilder sb = new StringBuilder(Integer.toString(value));
		while(sb.length() < numberOfDigits)
			sb.insert(0, "0");
		return sb.toString();
	}

	private static void copyFileIntoDirectory(String existingDirectory,
			String existingFileName, String targetDirectory, int trackNumber) {
		File in = null;
		File out = null;
		try {
			in = new File(existingDirectory + existingFileName);
			out = new File(targetDirectory + trackNumber + "_" + existingFileName.replace("/", "__"));
			out.createNewFile();
			copyFile(in, out);
		} catch (IOException e) {
			if(out != null)
				System.out.println(out.toString());
			e.printStackTrace();
		}
	}
	
	private static void copyFileIntoDirectory(String existingDirectory,
			String existingFileName, String targetDirectory) {
		File in = null;
		File out = null;
		try {
			in = new File(existingDirectory + existingFileName);
			out = new File(targetDirectory + existingFileName);
			out.createNewFile();
			copyFile(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}
}
