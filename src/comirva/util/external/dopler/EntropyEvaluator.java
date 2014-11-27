package comirva.util.external.dopler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import comirva.mlearn.GHSOM;

public class EntropyEvaluator {

	private final static String RESULT_FILE_PATH = "D:/Diplomarbeit/Results/entropies_combined.dat";
	//private static final String GHSOM_PATH = "C:/Dokumente und Einstellungen/Maxwell/Eigene Dateien/reference_ghsom_13_2545_genre.ghs";
	
	public static void main(String[] args) {
		//calcListEntropies();
		calcListEntropies();
		evaluateEntropyOfSubSOMs(20, 4);
	}
	
	private static void calcListEntropies() {
		List<GHSOM> ghsoms = EvalUtil.getTestGHSOMs(5);
		//calculate entropy for all numbers of consecutive pieces
		//therefore use more ghsoms to get a more accurate result
		Map<Integer, Double> sumMap = new TreeMap<Integer, Double>();
		double temp = 0.0;
		for(GHSOM ghsom: ghsoms) {
			List<String> sequentialList = ghsom.getSequentialList();
			
			for(int j = 2; j < 500; j++) {
				temp = calcShortTermEntropy(sequentialList, j);
				if(sumMap.get(Integer.valueOf(j)) == null)
					sumMap.put(Integer.valueOf(j), Double.valueOf(temp));
				else
					sumMap.put(Integer.valueOf(j), Double.valueOf(temp
							+ sumMap.get(Integer.valueOf(j)).doubleValue()));
			}
		}
		for(Integer key: sumMap.keySet()) {
			sumMap.put(key, Double.valueOf(
					sumMap.get(key).doubleValue()
					/ ghsoms.size()));
		}
		StringBuilder sb = new StringBuilder();
		for(Integer key: sumMap.keySet()) 
			sb.append(key + " " + sumMap.get(key) + "\n");
		EvalUtil.stringToFile(sb.toString(), RESULT_FILE_PATH);
	}
	
	private static void evaluateEntropyOfSubSOMs(int ghsomCount, int levels) {
		List<GHSOM> testSOMs = EvalUtil.getTestGHSOMs(ghsomCount);
		for(int i = 0; i < levels; i++) 
			System.out.println("mean entropy on level " + i + " " + calcEntropyOfSubSOMS(testSOMs, i));
	}
	
	private static double calcEntropyOfSubSOMS(List<GHSOM> motherShips, int level) {
		double meanEntropy = 0.0;
		double meanEvaluatedSubSOMs = 0.0;
		for(GHSOM motherGHSOM: motherShips) {
			GHSOM baseNode = motherGHSOM.getSubSOM(0);
			int motherSize = baseNode.getSequentialList().size();
			double weightedSum = 0.0;
			int evaluatedSubSOMS = 0;
			List<GHSOM> subsomsToEvaluate = new ArrayList<GHSOM>();
			if(level == 0)
				subsomsToEvaluate.add(baseNode);
			else 
				subsomsToEvaluate.addAll(getGHSOMsOfLevel(baseNode, level));

			for(GHSOM ghsom: subsomsToEvaluate) {
				evaluatedSubSOMS++;
				weightedSum += (double) ghsom.getSequentialList().size() / motherSize * EntropyCalculator.calculateEntropy(ghsom.getSequentialList());
				//System.out.println(ghsom.getSequentialList().size() + " " + EntropyCalculator.calculateEntropy(ghsom.getSequentialList()));
			}
			//System.out.println("result:" + weightedSum);
			//System.out.println(evaluatedSubSOMS);
			meanEntropy += weightedSum / motherShips.size();
			meanEvaluatedSubSOMs += (double) evaluatedSubSOMS / motherShips.size();
		}
		System.out.println("mean evaluated subsoms: " + meanEvaluatedSubSOMs);
		return meanEntropy;
	}
	private static Collection<GHSOM> getGHSOMsOfLevel(GHSOM ghsom, int level) {
		if(level == 0)
			throw new IllegalArgumentException("level 0 not allowed");
		if(level == 1) 
			return ghsom.getChildren();
		List<GHSOM> list = new ArrayList<GHSOM>();
		for(GHSOM child: ghsom.getChildren())
			list.addAll(getGHSOMsOfLevel(child, level - 1));
		return list;
	}
	
	private static double calcShortTermEntropy(List<String> list, int consecutiveSongs) {
		int size = list.size();
		double sum = 0.0;
		//
		List<String> evalList = new ArrayList<String>(list);
		evalList.addAll(list);
		int i = 0;
		for(i = 0; i < size; i++) {
			sum += EntropyCalculator.calculateEntropy(
					evalList.subList(i, i + consecutiveSongs));
		}
		
		return sum / i;
	}

}
