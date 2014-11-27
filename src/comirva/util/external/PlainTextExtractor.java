/*
 * Created on 25.09.2003
 */

package comirva.util.external;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * @author Peter Knees
 */

public class PlainTextExtractor {

	public static String delimiterstring = " -\t\r\n\"|/,;.:?!~#%$§=_+*()[]{}&";

	public static String extractPlainText(String content) {
		String plaintext = "";
		// found this bug in webpages - problematic!
		content = content.replace("<a>", "</a>");
		content = content.replace("<A>", "</A>");
		
		String lowercontent = content; //.toLowerCase();
		// remove all <script ...> ... </script>
		int scriptcount = 0;
		StringBuffer sb = new StringBuffer();
		int index = 0;
		int index1 = 0;
		int index2 = 0;
		while ((index1 = lowercontent.indexOf("<script", index)) != -1 &&
			   (index2 = lowercontent.indexOf("</script>", index1)) != -1) {
			sb.append(lowercontent.substring(index, index1));
			index = index2 + 9;
			scriptcount++;
		}
		sb.append(lowercontent.substring(index, lowercontent.length()));
		plaintext = sb.toString();
		
		// remove all <style ...> ... </style>
		int stylecount = 0;
		sb = new StringBuffer();
		index = 0;
		index1 = 0;
		index2 = 0;
		while ((index1 = plaintext.indexOf("<style", index)) != -1 &&
			   (index2 = plaintext.indexOf("</style>", index1)) != -1) {
			sb.append(plaintext.substring(index, index1));
			index = index2 + 8;
			stylecount++;
		}
		sb.append(plaintext.substring(index, plaintext.length()));
		plaintext = sb.toString();
		
		// replace all html newlines and horizontal lines with java newlines
		plaintext = plaintext.replace("<br>", " $ ");
		plaintext = plaintext.replace("<br />", " $ ");
		plaintext = plaintext.replace("<hr>", " $ ");
		// ???
		plaintext = plaintext.replace("\n", " ");
		plaintext = plaintext.replace("\r", " ");
		
		// remove all other HTML tags
		sb = new StringBuffer();
		index = 0;
		index1 = 0;
		index2 = 0;
		while ((index1 = plaintext.indexOf("<", index)) != -1 &&
			   (index2 = plaintext.indexOf(">", index1)) != -1) {
			sb.append(plaintext.substring(index, index1));
			sb.append(" ");
			index = index2 + 1;
		}
		sb.append(plaintext.substring(index, plaintext.length()));
		plaintext = sb.toString();
		
		// convert html special chars (from &xxxx; to char)
//		Hashtable chars = HTMLCharacters.getHTMLHashtable();
//		for (Enumeration e=chars.keys(); e.hasMoreElements();) {
//			String key = (String)(e.nextElement());
//			String val = (String)(chars.get(key));
//			plaintext = plaintext.replace(key, val);
//		}
		
		// remove single-quotes
		plaintext = plaintext.replace("´", " ");
		plaintext = plaintext.replace("`", " ");
		plaintext = plaintext.replace("'", " ");
		
		plaintext = plaintext.replace("-", " ");
		
		// delete all chars that are no letter, no digit or not in the delimiter string
		sb = new StringBuffer();
		for (int i=0; i<plaintext.length(); i++) {
			char c = plaintext.charAt(i);
			if (((int)c) == 0) continue;
			if (Character.isLetterOrDigit(c) || (delimiterstring+"&<>").indexOf(c+"") != -1) {
				sb.append(c);
			}		
			// replace other whitespace
			if (Character.isWhitespace(c)) {
				sb.append(" ");
			}
		}
		plaintext = sb.toString();
		
		// replace all multiple blanks with a single one (tabs too)
		sb = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(plaintext, delimiterstring+"<>");
		while (tok.hasMoreTokens()) {
			String key = tok.nextToken().trim();
			// ignore words longer than 20 letters
			if (key.length() > 20 || key.length()==0)
				continue;			
			sb.append(key + " ");
		}
		plaintext = sb.toString();
		return plaintext;
	}

}

