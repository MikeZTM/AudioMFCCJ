package comirva.util.external.dopler;

import java.util.HashMap;

public class TestRegex {

	/**
	 * @param args
	 */
	private static final String CHARS_TO_DELETE = "[ \n-_&]";
	
	public static void main(String[] args) {
		String bla = "bad religion";
		System.out.println(bla.replaceAll("[ \n-_&]", ""));
		System.out.println(bla.replaceAll(CHARS_TO_DELETE, ""));
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(new Integer(1), new Double(1 / (1 + Math.log(1 + 0))));
		normalize(map);
		for(Integer i: map.keySet()) {
			System.out.println(map.get(i));
		}
	}

	private static HashMap<Integer, Double> normalize(HashMap<Integer, Double> songProximityValues) {
		double tempValue = 0;
		//normalize results
		//1.get smallest value
		double minimum = Double.POSITIVE_INFINITY;
		for(Double value: songProximityValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue < minimum)
				minimum = tempValue;
		}
		for(Integer i: songProximityValues.keySet()) {
			System.out.println(songProximityValues.get(i));
		}
		//subtract all values by the minimum
		for(Integer key: songProximityValues.keySet()) 
			songProximityValues.put(key, 
					Double.valueOf(songProximityValues.get(key).doubleValue() - minimum));
		for(Integer i: songProximityValues.keySet()) {
			System.out.println(songProximityValues.get(i));
		}
		//replace infinite number by 0
		for(Integer key: songProximityValues.keySet()) {
			tempValue = songProximityValues.get(key).doubleValue();
			if(tempValue == Double.NEGATIVE_INFINITY || tempValue == Double.POSITIVE_INFINITY)
				songProximityValues.put(key, new Double(0));
		}
		for(Integer i: songProximityValues.keySet()) {
			System.out.println(songProximityValues.get(i));
		}
		//normalise by division by the maximum value
		double maximum = Double.NEGATIVE_INFINITY;
		for(Double value: songProximityValues.values()) {
			tempValue = value.doubleValue();
			if(tempValue != Double.NEGATIVE_INFINITY 
					&& tempValue > maximum)
				maximum = tempValue;
		}
		for(Integer key: songProximityValues.keySet()) 
			songProximityValues.put(key, 
					Double.valueOf(songProximityValues.get(key).doubleValue() / maximum));
		for(Integer i: songProximityValues.keySet()) {
			System.out.println(songProximityValues.get(i));
		}
		return songProximityValues;
	}
}
