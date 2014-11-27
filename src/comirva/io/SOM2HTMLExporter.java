package comirva.io;

import java.awt.Font;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import comirva.mlearn.*;
import cp.util.*;

public class SOM2HTMLExporter {
	
	public static void exportSOM(SOM som, File file) {
		StringBuffer htmlsom = new StringBuffer();
		htmlsom.append("<html>\n<body>\n");
		htmlsom.append("<table style=\"text-align: center; font-size: 10px; font-family: arial;\" border=\"1\">\n");
		
//		 print labels
		for (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook		
			htmlsom.append("<tr>\n");
			for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook	
				htmlsom.append("<td>\n");
				// get Voronoi-Set for current map unit (if exists)
				if (som.voronoiSet != null) {
					Vector vorSet = (Vector)som.voronoiSet.elementAt(i*som.getNumberOfRows()+j);
					// accumulate labels
					Hashtable<String, Integer> accLabels = new Hashtable<String, Integer>();
					for (int k=0; k<vorSet.size(); k++) {
						Integer labelIndex = (Integer)vorSet.elementAt(k);
						String label = som.getLabel(labelIndex.intValue());
						Integer lcount = accLabels.get(label);
						int newlcount = 1;
						if (lcount != null) {
							newlcount = lcount.intValue()+1;
						}
						accLabels.put(label, newlcount);
					}
					
					// sort descending
					Vector<cp.util.helpers.ObjectComparablePair> sortLabels = new Vector<cp.util.helpers.ObjectComparablePair>();
					Iterator<String> keyit = accLabels.keySet().iterator();
					while (keyit.hasNext()) {
						String label = keyit.next();
						int c = accLabels.get(label).intValue();
						if (c>1)
							label += " (" + c + ")";
						sortLabels.addElement(new cp.util.helpers.ObjectComparablePair(label, new Integer(c)));
					}
					Collections.sort(sortLabels);
					Collections.reverse(sortLabels);
					
					// write display string
					int labelCounter = 0;
					String[] countlabels = new String[accLabels.size()];
					Iterator<cp.util.helpers.ObjectComparablePair> ocpit = sortLabels.iterator();
					while (ocpit.hasNext()) {
						String label = (String)(ocpit.next().getObject());
						countlabels[labelCounter++] = label;
					}
					// read all labels in Voronoi-Set of current map unit
					for (int k=0; k<countlabels.length; k++) {
						// print the labels
						String label = countlabels[k];
						htmlsom.append(label+"<br>\n");
					}
				}
				htmlsom.append("</td>\n");
			}
			htmlsom.append("</tr>\n");
		}

		htmlsom.append("</table>\n</body>\n</html>\n");
		TextTool.stringToFile(htmlsom.toString(), file);
	}

	public static void exportMDM(SOM som, File file) {
		StringBuffer htmlsom = new StringBuffer();
		htmlsom.append("<html>\n<body>\n");
		htmlsom.append("<table style=\"text-align: center; font-size: 10px; font-family: arial;\" border=\"1\">\n");
		
//		 print labels
		for (int j=0; j<som.getNumberOfRows(); j++) {		// for each row in codebook		
			htmlsom.append("<tr>\n");
			for (int i=0; i<som.getNumberOfColumns(); i++) {		// for each column in codebook	
				htmlsom.append("<td>\n");
				// get Voronoi-Set for current map unit (if exists)
				if (som.voronoiSet != null && som.getMDM().getLabels() != null) {
					Vector<String> unitlabels = som.getMDM().getLabels().elementAt(i*som.getNumberOfRows()+j);
					
					Iterator<String> iter = unitlabels.iterator();
					// read all labels in Voronoi-Set of current map unit
					while (iter.hasNext()) {
						// print the labels
						String labelandvalue = iter.next();
						String label = labelandvalue.substring(0, labelandvalue.lastIndexOf("_"));
						double value = 0.;
						try {
							value = Double.parseDouble(labelandvalue.substring(labelandvalue.lastIndexOf("_")+1, labelandvalue.length()));
						} catch (NumberFormatException nfe) {}
						
						// determine fontsize between 18 and 8
						int fontsize = (int)(Math.round(value*(18-8)) + 8);
						
						// set font for drawing labels
						Font labelFont = new Font("SansSerif", value>0.95?Font.BOLD:Font.PLAIN, fontsize);
						htmlsom.append("<span style=\"");
						if (value>0.95)
							htmlsom.append("font-weight: bold; ");
						htmlsom.append("font-size: "+fontsize+"px;\">"+label+"</span><br>\n");
					}					
				}
				htmlsom.append("</td>\n");
			}
			htmlsom.append("</tr>\n");
		}

		htmlsom.append("</table>\n</body>\n</html>\n");
		TextTool.stringToFile(htmlsom.toString(), file);
	}
	
}
