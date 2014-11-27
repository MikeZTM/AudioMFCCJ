/*
 * Created on 30.05.2007
 */
package comirva.util.external;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import comirva.data.EntityTermProfile;
import comirva.data.TermsWeights;
import comirva.io.filefilter.XMLFileFilter;
import comirva.util.TermProfileUtils;

/*
 * This class was designed to
 * output terms gained from ETPs by
 * different term weighting functions.
 */
public class ETPUserStudy {
	static final boolean writeXMLIDFs = true;
	
	public static void main(String[] args) {
		// read all XML files in directory and print out list of top-10 terms for either
		// term weighting function TF, DF, and TFxIDF
		File fileXML = new File("/Research/Data/co-occurrences/C112a/cob_terms/crawl_1000_MR");
		File xmlIDFs;
		if (writeXMLIDFs)
			xmlIDFs = new File("/Research/Data/co-occurrences/C112a/cob_terms/idfs.xml");
		File[] xmlFiles = fileXML.listFiles(new XMLFileFilter());
		try {
			// create writer
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/tmp/eval_TFxIDF_1plus.txt"))); //media/POWERRAM/Research/Publications/work/SIGIR_MIR_2007/eval_TFxIDF.txt")));
			BufferedWriter bwIDFs = new BufferedWriter(new FileWriter(xmlIDFs));		
			// sum up all DFs (for calculating TFxIDF) 
			Hashtable<String, Long> htDF = new Hashtable<String, Long>();
			long totalNoWebpages = 0;		// total # of web pages in corpus
			for (int j=0; j<xmlFiles.length; j++) {
				if (xmlFiles[j].isFile()) {
					EntityTermProfile etp = TermProfileUtils.getEntityTermProfileFromXML(xmlFiles[j]);
					totalNoWebpages += etp.getNumberDocuments();
					Hashtable<String, Integer> DF = etp.getDocumentFrequency();
					Enumeration<String> e = DF.keys();
					while (e.hasMoreElements()) {
						String key = e.nextElement();
						Long df = new Long(DF.get(key).longValue());
						Long newDF;
						if (htDF.containsKey(key))
							newDF = new Long(df.longValue()+((Long)htDF.get(key)).longValue());
						else
							newDF = df;
						htDF.put(key, newDF);
//						System.out.println(key + "\tnew DF=" + newDF);
					}
				}
			}
			System.out.println("total # of web pages in corpus: " + totalNoWebpages);
			// write IDFs to XML file
			if (writeXMLIDFs) {		
				Enumeration<String> e = htDF.keys();
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					Long df = htDF.get(key);		// get DF of current term for complete corpus
					Float idf; 
					if (df.floatValue() == 0)
						idf = new Float(0);
					else
						idf = new Float(Math.log((float)totalNoWebpages/df.floatValue()));
					System.out.println(key + "\tIDF=" + df + "\t" + idf);
					bwIDFs.append("<IDF term=\"" + key + "\">" + idf.toString() + "</IDF>\n");
				}
				bwIDFs.flush();
				bwIDFs.close();
			}
if (true) {
			// process all XML-files in selected directory
			for (int j=0; j<xmlFiles.length; j++) {
				if (xmlFiles[j].isFile()) {
					bw.append(xmlFiles[j].getName()); bw.newLine();
					EntityTermProfile etp = TermProfileUtils.getEntityTermProfileFromXML(xmlFiles[j]);
					// determine top-ranked terms wrt to TF, DF, and TFxIDF
					Hashtable<String, Long> htTF = etp.getTermFrequency();
					TermsWeights twTF = etp.getMostImportantTerms(10, htTF);
					TermsWeights twDF = etp.getMostImportantTerms(10, etp.getDocumentFrequency());
					// calculate TFxIDF
					Hashtable htTFxIDF = new Hashtable();
					Enumeration<String> e = htDF.keys();
					while (e.hasMoreElements()) {
						String key = e.nextElement();
						Long tf = htTF.get(key);		// get TF of current term for current artist
						Long df = htDF.get(key);		// get DF of current term for complete corpus
						Float tfidf; 
						if (df.floatValue() == 0 || tf.floatValue() == 0)
							tfidf = new Float(0);
						else
//						tfidf = new Float(tf.floatValue() * Math.log((float)totalNoWebpages/df.floatValue()));
							tfidf = new Float((1+Math.log(tf.floatValue())) * Math.log((float)totalNoWebpages/df.floatValue()));
						System.out.println(key + "\tTFxIDF=" + tfidf.toString());
						htTFxIDF.put(key, tfidf);
					}
					TermsWeights twTFxIDF = etp.getMostImportantTerms(10, htTFxIDF);
					// create one arraylist containing all terms
//					ArrayList terms  = new ArrayList();
//					for (int u=0; u<twTF.getSize(); u++) {
//						String term = (String)twTF.getTerms().get(u);
//						if (!terms.contains(term)) terms.add(term);
//					}
//					for (int u=0; u<twDF.getSize(); u++) {
//						String term = (String)twDF.getTerms().get(u);
//						if (!terms.contains(term)) terms.add(term);
//					}
//					for (int u=0; u<twTFxIDF.getSize(); u++) {
//						String term = (String)twTFxIDF.getTerms().get(u);
//						if (!terms.contains(term)) terms.add(term);
//					}
//					for (int u=0; u<terms.size(); u++) {
//						bw.append((String)terms.get(u) + "\n");
//					}
//					bw.append("TF\t");
//					for (int u=0; u<twTF.getSize(); u++) {
//						bw.append((String)twTF.getTerms().get(u) + "\n");
//						if (u+1 < twTF.getSize()) bw.append("\t");
//					}
//					bw.append("DF\t");
//					for (int u=0; u<twTF.getSize(); u++) {
//						bw.append((String)twDF.getTerms().get(u) + "\n");
//						if (u+1 < twTF.getSize()) bw.append("\t");
//					}
					bw.append("TFxIDF\t");
					for (int u=0; u<twTFxIDF.getSize(); u++) {
						bw.append((String)twTFxIDF.getTerms().get(u) + "\n");
						if (u+1 < twTF.getSize()) bw.append("\t");
					}
					bw.newLine();
					bw.flush();
				}
			}		
			bw.flush();
			bw.close();
}
		} catch (IOException e) {
			//e.printStackTrace();
		}

	}

}
