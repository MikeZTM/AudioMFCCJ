package comirva.util.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

public class YodaEvaluator {

	public YodaEvaluator() {
		super();
	}
	
	/**
	 * @param decadesGT			a file containing the ground truth for evaluation
	 * @param decadesYoda		a file containing the results obtained by YODA
	 */
	public static void evalYodaResults(File decadesGT, File decadesYoda) {
		double totalRecall = 0, totalPrecision = 0;
		Vector<Double> singleRecall = new Vector<Double>();
		Vector<Double> singlePrecision = new Vector<Double>();
		// open both files
		try {
	    	BufferedReader brGT = new BufferedReader(new FileReader(decadesGT));
	    	BufferedReader brYoda = new BufferedReader(new FileReader(decadesYoda));
	    	String lineGT = null, lineYoda = null;
	    	while ((lineGT = brGT.readLine()) != null && (lineYoda = brYoda.readLine()) != null) {
	    		StringTokenizer stGT = new StringTokenizer(lineGT, " ");
	    		StringTokenizer stYoda = new StringTokenizer(lineYoda, " ");
	    		Vector<Integer> decGT = new Vector<Integer>();
	    		Vector<Integer> decYoda = new Vector<Integer>();
	    		// insert tokens (decades) into vectors
	    		while (stGT.hasMoreElements())
	    			decGT.addElement(new Integer((String)stGT.nextElement()));
	    		while (stYoda.hasMoreElements())
	    			decYoda.addElement(new Integer((String)stYoda.nextElement()));
	    		// calculate recall and precision
	    		int correctlyFound = 0;
	    		// recall
	    		for (int i=0; i<decGT.size(); i++) {		
	    			if (decYoda.contains(decGT.elementAt(i)))		// correctly found decade
	    				correctlyFound++;
	    		}
//	    		if (decYoda.size() == 0)
//	    			singlePrecision.addElement(new Double(0));
////	    			System.out.print("");
//	    		else
	    			singleRecall.addElement(new Double((double)correctlyFound/(double)decGT.size()));
	    		// precision
	    		correctlyFound = 0;
	    		for (int i=0; i<decYoda.size(); i++) {		
	    			if (decGT.contains(decYoda.elementAt(i)))		// correctly found decade
	    				correctlyFound++;
	    		}
	    		// special case: no decades found by Yoda
	    		if (decYoda.size() == 0)
	    			singlePrecision.addElement(new Double(0));
//	    			System.out.print("");
	    		else
	    			singlePrecision.addElement(new Double((double)correctlyFound/(double)decYoda.size()));
//	    		System.out.println("recall: "+singleRecall.lastElement()+"\tprecision: "+singlePrecision.lastElement());
	    	}
	    	// calculate total recall and precision
	    	for (int i=0; i<singleRecall.size(); i++)
	    		totalRecall += singleRecall.elementAt(i).doubleValue()*(1/(double)singleRecall.size());
	    	for (int i=0; i<singlePrecision.size(); i++)
	    		totalPrecision += singlePrecision.elementAt(i).doubleValue()*(1/(double)singlePrecision.size());
	    	System.out.println("total recall: "+totalRecall);
	    	System.out.println("total precision: "+totalPrecision);
	    	// close buffered readers
	    	brGT.close();
	    	brYoda.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {
		YodaEvaluator.evalYodaResults(	new File("C:/Research/Data/yoda/C1321a_yoda_values.txt"),
										new File("C:/Research/Data/yoda/C1321a_yoda_output_values.txt"));
		YodaEvaluator.evalYodaResults(	new File("C:/Research/Data/yoda/C1321a_yoda_values.txt"),
										new File("C:/Research/Data/yoda/C1321a_yoda_output_NV_values.txt"));
	}
}
