/*
 * Created on 17.03.2004
 *
 */
package comirva.util.external;

import java.awt.Color;
import java.io.*;
import java.util.*;

/**
 * This class implements some tools for converting and formating
 * text (especially HTML).
 * 
 * @author Peter Knees
 */
public class TextFormatTool {
	
	public static String colorToHtml(Color col) {
		return "#"+	leadingZero(Integer.toHexString(col.getRed()))+
					leadingZero(Integer.toHexString(col.getGreen()))+
					leadingZero(Integer.toHexString(col.getBlue()));
	}
	
	public static Color htmlToColor(String col) {
		if (!col.matches("#[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]"))
			return Color.WHITE;
		int red		= Integer.parseInt(col.substring(1,3), 16);
		int green	= Integer.parseInt(col.substring(3,5), 16);
		int blue	= Integer.parseInt(col.substring(5,7), 16);
		return new Color(red, green, blue);
	}
	
	public static String removeUnwantedChars(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isLetterOrDigit(c))
				sb.append(c);
		}
		return sb.toString().toLowerCase();
	}
	
	public static String leadingZero(String s) {
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
	
	public static String leadingDoubleZero(String s) {
		if (s.length() == 1)
			s = "00" + s;
		else if (s.length() == 2)
			s = "0" + s;
		return s;
	}
	
	public static void stringToFile(String content, File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		}
		catch (Exception ioe) {
			System.err.println("error: invalid file.");
			return;
		}
		try {
			PrintWriter pw = new PrintWriter(fw);
			pw.println(content);
			pw.flush();
			pw.close();		
			fw.close();
		}
		catch (Exception ioe) {
			System.err.println("error: printing or closing error.");
		}
	}
	
	public static void intArrayToFile(int[] intarray, File file) {
		StringBuffer out = new StringBuffer();
		for (int i=0; i<intarray.length; i++) {
			out.append(intarray[i]+"\r\n");
		}
		stringToFile(out.toString(), file);
	}
	
	public static String readInFile(File file) throws Exception {
		if (!file.canRead()) {
			throw new Exception("can't read file.");
		}
		FileReader fr;
		try {
			fr = new FileReader(file);
		}
		catch (FileNotFoundException fnfe) {
			throw new Exception("file not found.");
		}
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + '\n');
			}
		}
		catch (IOException e) {
			throw new Exception("error when reading file.");
		}
		fr.close();
		return sb.toString();
	}
	
	public static String[] readInStringArray(File file) throws Exception {
		String array = readInFile(file);
		Vector strs = new Vector();
		StringTokenizer st = new StringTokenizer(array, "\r\n");
		while (st.hasMoreTokens()) {
			String s = st.nextToken().trim();
			if (!s.equals("")) strs.addElement(s);
		}
		String[] r = new String[strs.size()];
		for (int i=0; i<r.length; i++) {
			r[i] = (String)(strs.elementAt(i));
		}
		return r;
	}
	
	public static int[] readInIntArray(File file) throws Exception {
		String array = readInFile(file);
		Vector ints = new Vector();
		StringTokenizer st = new StringTokenizer(array, "\r\n");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			try {
				int n = Integer.parseInt(t);
				ints.addElement(new Integer(n));
			}
			catch (NumberFormatException nfe) {
				continue;
			}
		}
		int[] r = new int[ints.size()];
		for (int i=0; i<r.length; i++) {
			r[i] = ((Integer)(ints.elementAt(i))).intValue();
		}
		return r;
	}
}
