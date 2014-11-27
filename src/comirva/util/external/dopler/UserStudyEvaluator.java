package comirva.util.external.dopler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import comirva.mlearn.GHSOM;
import comirva.mlearn.ghsom.CoOccurrencePrototypeFinder;
import comirva.mlearn.ghsom.WebCoocIndividualPrototypeFinder;

public class UserStudyEvaluator {
	
	private static final String RESULT_FOLDER = "D:/Diplomarbeit/Eval_Results/";
	private static final String GHSOM_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/reference_ghsom_13_2545_genre.ghs";
	private static final String GENRE_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/genres2545.dat";
	private static final String BAND_NAME_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/bands2545.dat";
	//private static final String DATA_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/pca30.dat";
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		HashMap<Integer, List<Integer>> fullPrototypeRankings = getPrototypeRankingsByGroups();
		HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data = checkAndLoadData(fullPrototypeRankings);
		HashMap<Integer, Integer> prototypes = getPrototypesByGroups();
		HashMap<String, TreeMap<Integer, List<Integer>>> groupPrototypes = calcPrototypeForAllGroups(data);
		TreeMap<Integer, Integer> prototypeHitsPerGroup = calculatePrototypHitsPerGroup(data, prototypes);
		TreeMap<Integer, Double> entropiesPerGroup = calculateEntropiesPerGroup(data, EvalUtil.loadMetadataMatrix(GENRE_PATH));
		
		System.out.println("direct hit percentage: " + calculateDirectHits(data, prototypes) + " baseline: 0.1");
		System.out.println("band hits: " + calculateLabelHits(data, prototypes, EvalUtil.loadMetadataMatrix(BAND_NAME_PATH)));
		System.out.println("genre hits: " + calculateLabelHits(data, prototypes, EvalUtil.loadMetadataMatrix(GENRE_PATH)));

		System.out.println("absolute ranking base line: " + calculateRankingBase(data, fullPrototypeRankings, false));
		System.out.println("absolute ranking value: " + calculateRanking(data, fullPrototypeRankings, false));
		System.out.println("relative ranking base line: " + calculateRankingBase(data, fullPrototypeRankings, true));
		System.out.println("relative ranking value: " + calculateRanking(data, fullPrototypeRankings, true));
		
		System.out.println("\ngroup ranking base line: 0.5");
		System.out.println("group ranking value: " + calculateRankingOnGroups(data, groupPrototypes));
		System.out.println("\nmean genre entropy of eval groups: " + meanEntropy(data, EvalUtil.loadMetadataMatrix(GENRE_PATH)));
		System.out.println("\n\ngroupHits\n\n");
		for(Integer key: prototypeHitsPerGroup.keySet())
			System.out.println(key + " " + prototypeHitsPerGroup.get(key) + " " + entropiesPerGroup.get(key));
	}
	






	private static TreeMap<Integer, Integer> calculatePrototypHitsPerGroup(
			HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data,
			HashMap<Integer, Integer> prototypes) {
		TreeMap<Integer, Integer> result = new TreeMap<Integer, Integer>();

		
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			int groupNumber = 0;
			for(HashMap<Integer, Boolean> groupResult: fileResult.values()) {
				if(result.get(Integer.valueOf(groupNumber)) == null)
					result.put(Integer.valueOf(groupNumber), Integer.valueOf(0));
				if(groupResult.get(prototypes.get(Integer.valueOf(groupNumber))).booleanValue()) 
					result.put(Integer.valueOf(groupNumber), Integer.valueOf(result.get(Integer.valueOf(groupNumber)).intValue() + 1));
				groupNumber++;
			}
		}
		return result;
	}



	@SuppressWarnings({ "unused", "unchecked" })
	private static HashMap<String, TreeMap<Integer, List<Integer>>> calcPrototypeForAllGroups(
			HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data) {
		HashMap<String, TreeMap<Integer, List<Integer>>> result = new HashMap<String, TreeMap<Integer, List<Integer>>>();
		Vector<String> bandNames = EvalUtil.loadMetadataMatrix(BAND_NAME_PATH);

		GHSOM refGhsom = EvalUtil.loadGHSOM(GHSOM_PATH);
		CoOccurrencePrototypeFinder coOccurrencePrototypeFinder = new CoOccurrencePrototypeFinder(refGhsom.getCoOccMatrixLabels(), refGhsom.getCoOccMatrix());
		for(String fileName: data.keySet()) {
			TreeMap<Integer, HashMap<Integer, Boolean>> fileResult = data.get(fileName);
			TreeMap<Integer, List<Integer>> groupResult = new TreeMap<Integer, List<Integer>>();
			for(Integer groupKey: fileResult.keySet()) {
				HashMap<Integer, Boolean> groupStructure = fileResult.get(groupKey);
				
				Set<String> labelList = new HashSet<String>();
				HashMap<Integer, Vector<Double>>  groupAudioData = new HashMap<Integer, Vector<Double>>();
				//load required data
				for(Integer key: groupStructure.keySet()) {
					labelList.add(bandNames.get(key.intValue()));
					groupAudioData.put(key, refGhsom.data.getRow(key.intValue()));	
				}
				//calc mean, find web prototype
				Vector<Vector<Double>> groupAudioDataValues = new Vector<Vector<Double>>();
				groupAudioDataValues.addAll(groupAudioData.values());
				Vector<Double> mean = GHSOM.calculateMean(groupAudioDataValues);
				HashMap<Integer, Double> songProximityValues = WebCoocIndividualPrototypeFinder.calculateSongProximityValue(mean, groupAudioData);
				//calc prototype list
				TreeMap<Double, Integer> songId_Result = new TreeMap<Double, Integer>();
				HashMap<String, Double> coocRankingValues = coOccurrencePrototypeFinder.calcutateRankingsOf(labelList);
				for(Integer songId: groupAudioData.keySet()) {
					//workaround to guarantee distinct values
					Double songValue = Double.valueOf(
							coocRankingValues.get(refGhsom.getAltLabel(songId.intValue())).doubleValue() *
							songProximityValues.get(songId).doubleValue());
					while(songId_Result.containsKey(songValue))
						songValue = Double.valueOf(songValue.doubleValue() + 0.000001);
					
					songId_Result.put(songValue, songId);
				}
				
				List<Integer> groupRanking = new ArrayList<Integer>(); 
				groupRanking.addAll(songId_Result.values());
				Collections.reverse(groupRanking);
				groupResult.put(groupKey, groupRanking);
			}
			result.put(fileName, groupResult);
		}
		return result;
	}

	private static double calculateRankingBase(HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, HashMap<Integer, List<Integer>> fullPrototypeRankings, boolean relative) {
		double result = 0.0;
		
		int numberOfGroups = 0;
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			for(Integer groupKey: fileResult.keySet()) {
				HashMap<Integer, Boolean> groupResult = fileResult.get(groupKey);
				numberOfGroups++;
				double sum = 0.0;
				int count = 0;
				List<Integer> groupRankings = fullPrototypeRankings.get(groupKey);
				if(relative)
					groupRankings = getRelativeRanking(groupResult, groupRankings);
				for(Integer key : groupResult.keySet()) {
					sum += groupRankings.indexOf(key);
					count++;
				}
				result += sum / count / groupRankings.size() * ((double) groupRankings.size() / (groupRankings.size() - 1));

			}
		}
		return 1 - result/numberOfGroups;
	}
	
	private static List<Integer> getRelativeRanking(
			HashMap<Integer, Boolean> groupResult, List<Integer> groupRankings) {
		List<Integer> result = new ArrayList<Integer>();
		Set<Integer> groupKeys = groupResult.keySet();
		for(Integer rankedKey : groupRankings) {
			if(groupKeys.contains(rankedKey))
				result.add(rankedKey);
		}
		return result;
	}

	private static double calculateRanking(HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, HashMap<Integer, List<Integer>> fullPrototypeRankings, boolean relative) {
		double result = 0.0;
		
		int numberOfGroups = 0;
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			for(Integer groupKey: fileResult.keySet()) {
				HashMap<Integer, Boolean> groupResult = fileResult.get(groupKey);
				numberOfGroups++;
				List<Integer> groupRankings = fullPrototypeRankings.get(groupKey);
				if(relative)
					groupRankings = getRelativeRanking(groupResult, groupRankings);
				for(Integer key : groupResult.keySet()) {
					if(groupKey.intValue() == 6 && relative)
						System.out.println("groupRankings!" + groupRankings);
					if(groupResult.get(key).booleanValue()) {
						result += (double) groupRankings.indexOf(key) / (groupRankings.size() - 1);
					}
				}
			}
		}
		return 1 - result/numberOfGroups;
	}
	
	private static double calculateRankingOnGroups(
			HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data,
			HashMap<String, TreeMap<Integer, List<Integer>>> allGroupPrototypes) {
		double result = 0.0;
		
		int numberOfGroups = 0;
		for(String fileName: data.keySet()) {
			TreeMap<Integer, HashMap<Integer, Boolean>> fileResult = data.get(fileName);
			TreeMap<Integer, List<Integer>> groupPrototypes = allGroupPrototypes.get(fileName);
			for(Integer groupKey: fileResult.keySet()) {
				HashMap<Integer, Boolean> groupResult = fileResult.get(groupKey);
				numberOfGroups++;
				List<Integer> groupRankings = groupPrototypes.get(groupKey);
				for(Integer key : groupResult.keySet()) {
					if(groupResult.get(key).booleanValue()) {
						result += (double) groupRankings.indexOf(key) / groupRankings.size() * ((double) groupRankings.size() / (groupRankings.size() - 1));
					}
				}
			}
		}
		return 1 - result/numberOfGroups;
	}
	
	
	private static HashMap<Integer, List<Integer>> getPrototypeRankingsByGroups() {
		HashMap<Integer, List<Integer>> prototypes = new HashMap<Integer, List<Integer>>();
		GHSOM baseSOM = EvalUtil.loadGHSOM(GHSOM_PATH).getSubSOM(0);
		WebCoocIndividualPrototypeFinder prototypeFinder = new WebCoocIndividualPrototypeFinder();
		int sum = 0;
		for(int i = 0; i < baseSOM.voronoiSet.size(); i++) {
			prototypes.put(Integer.valueOf(i), prototypeFinder.getRankOfAllSongs(baseSOM, i));
			sum += prototypeFinder.getRankOfAllSongs(baseSOM, i).size();
		}
		return prototypes;
	}

	private static HashMap<Integer, Integer> getPrototypesByGroups() {
		HashMap<Integer, Integer> prototypes = new HashMap<Integer, Integer>();
		GHSOM baseSOM = EvalUtil.loadGHSOM(GHSOM_PATH).getSubSOM(0);
		WebCoocIndividualPrototypeFinder prototypeFinder = new WebCoocIndividualPrototypeFinder();
		for(int i = 0; i < baseSOM.voronoiSet.size(); i++) 
			prototypes.put(Integer.valueOf(i), Integer.valueOf(prototypeFinder.getIndexOfPrototype(baseSOM, i)));
		return prototypes;
	}
	
	private static double calculateDirectHits(HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, HashMap<Integer, Integer> prototypes) {
		int sum = 0;
		int count = 0;
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			int groupNumber = 0;
			for(HashMap<Integer, Boolean> groupResult: fileResult.values()) {
				if(groupResult.get(prototypes.get(Integer.valueOf(groupNumber))).booleanValue())
					sum++;
				count++;
				groupNumber++;
			}
		}
		return (double) sum/count;
	}	
	
	private static double calculateLabelHits(HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, HashMap<Integer, Integer> prototypes, Vector<String> labels) {
		int sum = 0;
		int samebands = 0;
		int count = 0;
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			int groupNumber = 0;
			for(HashMap<Integer, Boolean> groupResult: fileResult.values()) {
				String prototypeLabel = labels.get(prototypes.get(Integer.valueOf(groupNumber)).intValue());
				for(Integer key: groupResult.keySet()) {
					if(labels.get(key.intValue()).equals(prototypeLabel)) {
						samebands++;
						if(groupResult.get(key).booleanValue())
							sum++;
					}
				}
				count++;
				groupNumber++;
			}
		}
		System.out.println("base line: " + (double) samebands/10/count);
		return (double) sum/count;
	}
	
	private static double meanEntropy(HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, Vector<String> labels) {
		double sum = 0;
		int count = 0;
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			for(HashMap<Integer, Boolean> groupResult: fileResult.values()) {
				List<String> labelList = new ArrayList<String>();
				for(Integer key : groupResult.keySet()) {
					labelList.add(labels.get(key.intValue()));
				}
				sum += EntropyCalculator.calculateEntropy(labelList);
				count++;
			}
		}
		System.out.println("overall entropy: " +sum + " / " + count);
		return sum/count;
	}
	
	private static TreeMap<Integer, Double> calculateEntropiesPerGroup(
			HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data, Vector<String> labels) {
		
		TreeMap<Integer, Double> result = new TreeMap<Integer, Double>();
		
		for(TreeMap<Integer, HashMap<Integer, Boolean>> fileResult: data.values()) {
			int groupNumber = 0;
			for(HashMap<Integer, Boolean> groupResult: fileResult.values()) {
				if(result.get(Integer.valueOf(groupNumber)) == null)
					result.put(Integer.valueOf(groupNumber), Double.valueOf(0.0));
				List<String> labelList = new ArrayList<String>();

				for(Integer key : groupResult.keySet()) 
					labelList.add(labels.get(key.intValue()));
				
				result.put(Integer.valueOf(groupNumber), Double.valueOf(result.get(Integer.valueOf(groupNumber)).doubleValue() + 
						EntropyCalculator.calculateEntropy(labelList)));
				groupNumber++;
			}
		}
		
		for(Integer key: result.keySet()) 
			result.put(key, Double.valueOf(result.get(key).doubleValue() / (data.values().size())));
		return result;
	}
	
	
	

	
	
	private static HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> checkAndLoadData(HashMap<Integer, List<Integer>> fullPrototypeRankings) {
		HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> data = loadData();
		for(String fileName: data.keySet()) {
			TreeMap<Integer, HashMap<Integer, Boolean>> fileResult = data.get(fileName);
			int groupNumber = 0;
			for(Integer groupKey: fileResult.keySet()) {
				HashMap<Integer, Boolean> groupResult = fileResult.get(groupKey);
				boolean hasPrototype = false;
				if(groupResult.keySet().size() != 10)
					throw new RuntimeException(fileName + " " + groupNumber + " not valid");
				List<Integer> prototypeRanking = fullPrototypeRankings.get(groupKey);
				for(Integer key: groupResult.keySet()) {
					if(!hasPrototype)
						hasPrototype = groupResult.get(key).booleanValue();
					else if(groupResult.get(key).booleanValue())
						throw new RuntimeException(fileName + " " + groupNumber + " has more than one prototype");
					//check if every song is included in prototype-ranking
					if(prototypeRanking.indexOf(key) == -1) {
						System.err.println("key: " + key + "; groupKey: " + groupKey + "; filename: " + fileName);
					}
						
						
				}
				if(!hasPrototype)
					throw new RuntimeException(fileName + " " + groupNumber + " has no prototype");
				groupNumber++;
			}
		}
		return data;
	}
	
	//brute force loadin' :D
	private static HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> loadData() {
		HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>> resultList = new HashMap<String, TreeMap<Integer, HashMap<Integer, Boolean>>>();
		for(String csvFile: loadResultFiles()) {
			TreeMap<Integer, HashMap<Integer, Boolean>> fileResult = new TreeMap<Integer, HashMap<Integer, Boolean>>();
			try {
				BufferedReader in = new BufferedReader(new FileReader(RESULT_FOLDER + csvFile));
				String line = null;
				int currentset = -1;
				HashMap<Integer, Boolean> groupResult = new HashMap<Integer, Boolean>();
				while((line = in.readLine()) != null) {
					String[] parts = line.split(";");

					if(parts != null && parts.length > 1 && !"testset".equals(parts[1])) {
						if(currentset != Integer.parseInt(parts[1])) {
							if(currentset != -1) {
								fileResult.put(Integer.valueOf(currentset), groupResult);
							}
							groupResult = new HashMap<Integer, Boolean>();
							currentset = Integer.parseInt(parts[1]);
						}
						
						if(parts.length > 4 && parts[4].length() > 0)
							groupResult.put(Integer.valueOf(parts[2]), Boolean.TRUE);
						else
							groupResult.put(Integer.valueOf(parts[2]), Boolean.FALSE);
					} 
				} 
				fileResult.put(Integer.valueOf(currentset), groupResult);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			resultList.put(csvFile, fileResult);
		}
		return resultList;
	}

	private static String[] loadResultFiles() {
		File folder = new File(RESULT_FOLDER);
		return folder.list(new FilenameFilter() {
			@Override
			public boolean accept(@SuppressWarnings("unused")
			File dir, String name) {
				if(name.endsWith(".csv"))
					return true;
				return false;
			}		
		});
	}

}
