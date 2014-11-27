package comirva.util.external.dopler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Calculates the Shannon Entropy for a list of Strings
 *
 */
public class EntropyCalculator {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		/*
		for(int i = 0; i < 43; i++)
			list.add("Metal");
		for(int i = 0; i < 388; i++)
			list.add("Electronica");
		for(int i = 0; i < 139; i++)
			list.add("Rap");
		for(int i = 0; i < 15; i++)
			list.add("Punk-Rock");
		for(int i = 0; i < 32; i++)
			list.add("Folk-Rock");
		for(int i = 0; i < 35; i++)
			list.add("Italian");
		for(int i = 0; i < 58; i++)
			list.add("Jazz");
		for(int i = 0; i < 7; i++)
			list.add("Celtic");
		for(int i = 0; i < 116; i++)
			list.add("A Capella");
		for(int i = 0; i < 148; i++)
			list.add("Bossa Nova");
		for(int i = 0; i < 19; i++)
			list.add("Acid Jazz");*/

		list.add("E");
		list.add("P");
		list.add("P");
		list.add("P");
		list.add("P");
		list.add("P");
		list.add("F");
		list.add("F");
		list.add("F");
		list.add("M");
		System.out.println(calculateEntropy(list));

	}
	
	public static double calculateEntropy(List<String> inputStrings) {
		HashMap<String, Integer> stringCount = new HashMap<String, Integer>();
		for(String inputString: inputStrings) {
			if(stringCount.containsKey(inputString))
				stringCount.put(inputString, 
						Integer.valueOf(stringCount.get(inputString).intValue() + 1));
			else
				stringCount.put(inputString, Integer.valueOf(1));
		}
		double result = 0.0;

		for(String inputString: stringCount.keySet()) {
			double occProbability = (double) stringCount.get(inputString).intValue() / inputStrings.size();
			result += occProbability * log2(occProbability);
			//System.out.print(inputString + ": " + occProbability + "; ");
		}

		return - result;
	}
	
	private static double log2(double input) {
		return Math.log(input)/Math.log(2);
	}

}
