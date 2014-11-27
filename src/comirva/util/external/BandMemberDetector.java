package comirva.util.external;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.lang.Character;

import javax.swing.JLabel;

import cp.net.Webpage;
import cp.util.HashtableTool;

import comirva.util.*;
import comirva.util.external.PlainTextExtractor;
import comirva.io.filefilter.HTMLFileFilter;

/**
 * This class tries to find the band members based on
 * web-documents.
 * 
 */
public class BandMemberDetector {

	private static File dir = new File("/Research/Data/band_members/Metal_private_M/"); //"C:/Research/Data/co-occurrences/C224a/crawl_1000_MR/anthrax"); // "C:/temp/anthrax_mr"); //"C:/Research/Data/band members/HM_crawl_M/accept"); //C:/Research/Data/co-occurrences/C224a/crawl_1000_MR/badreligion"); //"C:/temp/anthrax_mr"); //"C:/Research/Data/co-occurrences/C224a/crawl_1000_MR/anthrax"); // "C:/temp/metallica_mr"); // "C:/temp/metallica_mr"); // "C:/temp/metallica_mr"); //"C:/Research/Data/co-occurrences/C224a/crawl_1000_MR/metallica"); //new File("C:/Research/Data/co-occurrences/C224a/crawl_1000_MB/metallica"); 
	private static File outputFile = new File(dir.getAbsolutePath() + File.separator + "Metal_private_M_results_minDF0_with_keyboard.txt"); //"Metal_private_LUM_results_noinstr_DF0.txt"); //"Metal_private_MM_results_minDF0_with_keyboard.txt");
	private static File wordList = new File("/Research/Data/ispell-wordlist.txt");	// dictionary file
	private int searchDistanceBandMemberFilter = 50;	// how many terms between potential band member and string "band members"
	private int lengthContext = 5;						// terms stored before and after potential band member
	private float minimumDF = 0.0f;						// minimum document frequency of an N-gram which is necessary to retain it (fraction on DF of term with maximum DF)
	private static boolean onlyBMdetection = false;		// only perform band member detection (NED+filtering), no rule-based analysis is performed
	
	// some definitions for special artists (instruments)
	private Vector<String> synonymsSinger = new Vector<String>(Arrays.asList("singer", "vocalist", "voice", "chanter", "chanteuse", "choralist", "chorister", "crooner", "minstrel"));		// synonyms for "singer" - www.thesaurus.com
	private Vector<String> synonymsGuitarist = new Vector<String>(Arrays.asList("guitarist"));
	private Vector<String> synonymsBassist = new Vector<String>(Arrays.asList("bassist", "bass guitarist"));
	private Vector<String> synonymsDrummer = new Vector<String>(Arrays.asList("drummer", "percussionist"));
	private Vector<String> synonymsKeyboardist = new Vector<String>(Arrays.asList("keyboardist", "keyboarder", "keyboard player"));
	private Vector<String> synonymsVocals = new Vector<String>(Arrays.asList("vocal", "vocals", "voice", "voices"));
	private Vector<String> synonymsGuitar = new Vector<String>(Arrays.asList("guitar", "guitars"));
	private Vector<String> synonymsBass = new Vector<String>(Arrays.asList("bass", "bass guitar"));
	private Vector<String> synonymsDrums = new Vector<String>(Arrays.asList("drum", "drums", "percussion", "percussions"));
	private Vector<String> synonymsKeyboard = new Vector<String>(Arrays.asList("keyboard", "keyboards", "key-board", "key-boards"));
		
	// number of rules to test
	int noRules = 7;

	// potential artists (band members) and count how often rule applies
	private Hashtable<String, Integer> singer[] = new Hashtable[noRules];
	private Hashtable<String, Integer> guitarist[] = new Hashtable[noRules];
	private Hashtable<String, Integer> bassist[] = new Hashtable[noRules];
	private Hashtable<String, Integer> drummer[] = new Hashtable[noRules];
	private Hashtable<String, Integer> keyboardist[] = new Hashtable[noRules];

	public Vector<String> extractProbableBandMembersFromDocuments(File dir, File wordList, FileFilter filter) {
		Vector<String> terms = new Vector<String>();		// list of terms to return
		// get files matching the file filter
		File[] files = dir.listFiles(filter);

		// store N-grams with index in document
		Vector<Hashtable<String, Integer>> nounsNgrams = new Vector<Hashtable<String, Integer>>();
		// store preceding and subsequent context (terms) of potential band member
		Vector<Vector<EntityContext>> contextNgrams = new Vector<Vector<EntityContext>>();

		// Hashtable to store document frequencies
		Hashtable<String, Integer> DF = new Hashtable<String, Integer>();

		// init entity hashtables
		for (int i=0; i<noRules; i++) {
			singer[i] = new Hashtable<String, Integer>();
			guitarist[i] = new Hashtable<String, Integer>();
			bassist[i] = new Hashtable<String, Integer>();
			drummer[i] = new Hashtable<String, Integer>();
			keyboardist[i] = new Hashtable<String, Integer>();
		}

		// read dictionary (for excluding words)
		Vector<String> dictWords = new Vector<String>();
		BufferedReader readerFile;
		try {
			readerFile = new BufferedReader(new FileReader(wordList));
			String dictWord = readerFile.readLine();
			while (dictWord != null) {
				dictWords.addElement(dictWord);
				dictWord = readerFile.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		// ****************
		// 1.) extract N-grams of every term that seems to be a noun (1st upper case, rest lower)
		// ****************

		// get content of every file
		for (int i=0; i<files.length; i++) {
			try {
				Vector<String> tokenizerBuffer = new Vector<String>();		// for buffering content of web page
				// create new data structures for current HTML-document
				nounsNgrams.addElement(new Hashtable<String, Integer>()); 
				contextNgrams.addElement(new Vector<EntityContext>()); 

				// get "plaintext" of webpage
				Webpage wp = new Webpage(files[i]);
				String content = PlainTextExtractor.extractPlainText(wp.getContent());

				// get every single word and write them to buffer 
				StringTokenizer stContent = new StringTokenizer(content);
				System.out.println(files[i].getName()+" contains "+stContent.countTokens()+" tokens");
				while (stContent.hasMoreElements()) {
					String next = stContent.nextToken().trim();
					tokenizerBuffer.addElement(next);		// add token to buffer
				}

				// analyze N-grams
				analyzeNgrams(2, i, tokenizerBuffer, dictWords, DF, nounsNgrams, contextNgrams);
				analyzeNgrams(3, i, tokenizerBuffer, dictWords, DF, nounsNgrams, contextNgrams);
				analyzeNgrams(4, i, tokenizerBuffer, dictWords, DF, nounsNgrams, contextNgrams);


			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		// !!!!!!!!!!!!!!!!!!!
		// the following code is only to be used for entity/BM detection (without instruments)
		// !!!!!!!!!!!!!!!!!!!
		if (BandMemberDetector.onlyBMdetection) {	
			// get maximum element
			Enumeration<String> keys = DF.keys();
			int mDF = 0;
			while (keys.hasMoreElements()) {
				String nGram = keys.nextElement(); 
				int df = DF.get(nGram).intValue();
				mDF = Math.max(mDF, df);
			}		
			// only retain those terms with DF above threshold
//			Hashtable<String, Integer> DFpruned = new Hashtable<String, Integer>();
			keys = DF.keys();
			while (keys.hasMoreElements()) {
				String nGram = keys.nextElement(); 
				int df = DF.get(nGram).intValue();
				if ((double)df/(double)mDF >= minimumDF) {
					System.out.println(nGram + "\t" + df);
//					DFpruned.put(nGram, new Integer(df));
					writeResult(nGram + ": " + df + "\n");
				}
			}	
			System.out.println("finished.");
		}
		

		
		if (!BandMemberDetector.onlyBMdetection) {		// do perform rule-based analysis
			// ****************
			// 2.) check that document frequency of every N-gram is above treshold and delete all N-grams where this is not the case
			// ****************
			Enumeration<String> DFkeys = DF.keys();		
			int maxDF = 0;
			// find maximum document frequency
			while (DFkeys.hasMoreElements()) {
				String nGram = DFkeys.nextElement();
				Integer docFreq;
				int df = DF.get(nGram).intValue();
				for (int i=0; i<files.length; i++) {
					for (int j=0; j<noRules; j++) {
						if (singer[j] != null && (docFreq = singer[j].get(nGram)) != null)
							if (docFreq.intValue() > maxDF)
								maxDF = docFreq.intValue();
						if (guitarist[j] != null && (docFreq = guitarist[j].get(nGram)) != null)
							if (docFreq.intValue() > maxDF)
								maxDF = docFreq.intValue();
						if (bassist[j] != null && (docFreq = bassist[j].get(nGram)) != null)
							if (docFreq.intValue() > maxDF)
								maxDF = docFreq.intValue();
						if (drummer[j] != null && (docFreq = drummer[j].get(nGram)) != null)
							if (docFreq.intValue() > maxDF)
								maxDF = docFreq.intValue();
						if (keyboardist[j] != null && (docFreq = keyboardist[j].get(nGram)) != null)
							if (docFreq.intValue() > maxDF)
								maxDF = docFreq.intValue();		
					}
				}
			}
			// delete all potential band members below threshold
			DFkeys = DF.keys();
			while (DFkeys.hasMoreElements()) {
				String nGram = DFkeys.nextElement(); 
				int df = DF.get(nGram).intValue();
				if ((double)df/(double)maxDF >= minimumDF) {
					System.out.println(nGram + "\t" + df);
				} else {			// DF-value too small
					// delete all potential band members for which DF is below treshold
					for (int i=0; i<files.length; i++) {
						nounsNgrams.elementAt(i).remove(nGram);
						for (int j=0; j<noRules; j++) {
							singer[j].remove(nGram);
							guitarist[j].remove(nGram);
							bassist[j].remove(nGram);
							drummer[j].remove(nGram);
							keyboardist[j].remove(nGram);
						}
					}
				}
			}


			int maxFreq = 0;
			String singerName = "";
			for (int j=0; j<noRules; j++) {
				maxFreq = 0;
				Enumeration<String> keysSinger = singer[j].keys();
				while (keysSinger.hasMoreElements()) {
					String name = keysSinger.nextElement();
					int freq = singer[j].get(name).intValue();
					if (freq > maxFreq) {
						singerName = name;
						maxFreq = freq;
					}
				}
				if (!singer[j].isEmpty()) {
					System.out.println("singer acc to rule " + (j+1) + ": " + singerName + " " + maxFreq);
					writeResult("singer acc to rule " + (j+1) + ": " + singerName + " " + maxFreq + "\n");
				}
			}

			maxFreq = 0;
			String guitaristName = "";
			for (int j=0; j<noRules; j++) {
				maxFreq = 0;
				Enumeration<String> keysGuitarist = guitarist[j].keys();
				while (keysGuitarist.hasMoreElements()) {
					String name = keysGuitarist.nextElement();
					int freq = guitarist[j].get(name).intValue();
					if (freq > maxFreq) {
						guitaristName = name;
						maxFreq = freq;
					}
				}
				if (!guitarist[j].isEmpty()) {
					System.out.println("guitarist acc to rule " + (j+1) + ": " + guitaristName + " " + maxFreq);
					writeResult("guitarist acc to rule " + (j+1) + ": " + guitaristName + " " + maxFreq + "\n");
				}
			}

			maxFreq = 0;
			String bassistName = "";
			for (int j=0; j<noRules; j++) {
				maxFreq = 0;
				Enumeration<String> keysBassist = bassist[j].keys();
				while (keysBassist.hasMoreElements()) {
					String name = keysBassist.nextElement();
					int freq = bassist[j].get(name).intValue();
					if (freq > maxFreq) {
						bassistName = name;
						maxFreq = freq;
					}
				}
				if (!bassist[j].isEmpty()) {
					System.out.println("bassist acc to rule " + (j+1) + ": " + bassistName + " " + maxFreq);
					writeResult("bassist acc to rule " + (j+1) + ": " + bassistName + " " + maxFreq + "\n");
				}
			}

			maxFreq = 0;
			String drummerName = "";
			for (int j=0; j<noRules; j++) {
				maxFreq = 0;
				Enumeration<String> keysDrummer = drummer[j].keys();
				while (keysDrummer.hasMoreElements()) {
					String name = keysDrummer.nextElement();
					int freq = drummer[j].get(name).intValue();
					if (freq > maxFreq) {
						drummerName = name;
						maxFreq = freq;
					}
				}
				if (!drummer[j].isEmpty()) {
					System.out.println("drummer acc to rule " + (j+1) + ": " + drummerName + " " + maxFreq);
					writeResult("drummer acc to rule " + (j+1) + ": " + drummerName + " " + maxFreq + "\n");
				}
			}
			
			maxFreq = 0;
			String keyboardistName = "";
			for (int j=0; j<noRules; j++) {
				maxFreq = 0;
				Enumeration<String> keysKeyboardist = keyboardist[j].keys();
				while (keysKeyboardist.hasMoreElements()) {
					String name = keysKeyboardist.nextElement();
					int freq = keyboardist[j].get(name).intValue();
					if (freq > maxFreq) {
						keyboardistName = name;
						maxFreq = freq;
					}
				}
				if (!keyboardist[j].isEmpty()) {
					System.out.println("keyboardist acc to rule " + (j+1) + ": " + keyboardistName + " " + maxFreq);
					writeResult("keyboardist acc to rule " + (j+1) + ": " + keyboardistName + " " + maxFreq + "\n");
				}
			}

		}

//		System.out.println("singer: " + singerName);
//		System.out.println("guitarist: " + guitaristName);
//		System.out.println("bassist: " + bassistName);
//		System.out.println("drummer: " + drummerName);

//		// put extracted terms in a Vector<String> and return it
//		if (!tf.isEmpty()) {				// if at least 1 term could be extracted -> copy them to Vector<String>
//		Enumeration e = tf.keys();
//		while (e.hasMoreElements())
//		terms.addElement((String)e.nextElement());
//		} else				// if not a single term could be extracted -> return null
//		terms = null;
		// return term list
		return terms;
	}

	private boolean isNoun(String term) {
		boolean noun = true;
		if (term.length() == 0)
			return false;
		else {
			if (Character.isUpperCase(term.charAt(0))) {	// first char must be upper case
				for (int j=1; j<term.length(); j++) {
					if (Character.isUpperCase(term.charAt(j)))
						noun = false;
				}
			} else {		// first char is lower case -> no noun
				return false;
			}
		}
		return noun;
	}

	private void analyzeNgrams(int n, int i, Vector<String> tokenizerBuffer, Vector<String> dictWords, Hashtable<String, Integer> DF, Vector<Hashtable<String, Integer>> nounsNgrams, Vector<Vector<EntityContext>> contextNgrams) {
		// go through tokenize buffer and analyse words and context
		for (int index=0; index<tokenizerBuffer.size(); index++) {
			String nextN[] = new String[n];
			String potentialBandMember = new String();
			for (int k=0; k<n; k++) {
				if (index+k<tokenizerBuffer.size()) {
					nextN[k] = tokenizerBuffer.elementAt(index+k);
					potentialBandMember = potentialBandMember + nextN[k] + " ";
				}
			}
			potentialBandMember = potentialBandMember.trim();
			// analyze some knock-out criterion for entity name
			boolean isUpperCase = true;				
			boolean isCommonSpeech = false;
			boolean isOnly1Char = true;
			for (int k=0; k<n; k++) {
				// exclude every N-gram where at least one gram is not a valid noun 
				if (nextN[k] != null && !isNoun(nextN[k])) 
					isUpperCase = false;
				// exclude every N-gram where at least one gram is a common speech word
				if (nextN[k] != null && dictWords.contains(nextN[k].toLowerCase()))
					isCommonSpeech = true;
				// exclude every N-gram where every gram has only 1 char
				if (nextN[k] != null && nextN[k].length() > 1)
					isOnly1Char = false;
			}
			// only proceed if this is the case			
			if (isUpperCase && !isCommonSpeech && !isOnly1Char) {
				// entity (potential band member) found

//				// insert potential band member in hashtable of potential band members
//				// of all web pages of current artist
//				if (nounsNgrams.elementAt(i).get(potentialBandMember) == null) {

				// add preceding context of potential band member to context2-Vector of current artist
				String precedingContext = new String();
				if (index>=this.lengthContext)
					for (int j=index-this.lengthContext; j<index; j++)
						precedingContext = precedingContext + " " + tokenizerBuffer.elementAt(j);
				else
					for (int j=0; j<index; j++)
						precedingContext = precedingContext + " " + tokenizerBuffer.elementAt(j);
				precedingContext = precedingContext.trim();
				// add succeeding content of potential band member to context2-Vector of current artist
				String succeedingContext = new String();
				if (index+2+this.lengthContext<=tokenizerBuffer.size())
					for (int j=index+n; j<index+n+this.lengthContext; j++)
						succeedingContext = succeedingContext + " " + tokenizerBuffer.elementAt(j);
				else
					for (int j=index+n; j<tokenizerBuffer.size(); j++)
						succeedingContext = succeedingContext + " " + tokenizerBuffer.elementAt(j);
				succeedingContext = succeedingContext.trim();

				EntityContext context = new EntityContext(potentialBandMember, precedingContext, succeedingContext);
				contextNgrams.elementAt(i).addElement(context);
//				System.out.println("context: " + precedingContext + " --- " + potentialBandMember + " --- " + succeedingContext);

				// update document frequency of potential band member 
				if (DF.containsKey(potentialBandMember))	// N-gram already contained in hashtable?
					// yes -> increase DF counter
					DF.put(potentialBandMember, new Integer(DF.get(potentialBandMember).intValue()+1));
				else
					// no -> insert N-gram with DF of 1
					DF.put(potentialBandMember, new Integer(1));

				// test for appliance of rules R1-R7
				if (!BandMemberDetector.onlyBMdetection)
					testRules(context, synonymsSinger, synonymsGuitarist, synonymsBassist, synonymsDrummer, synonymsKeyboardist, synonymsVocals, synonymsGuitar, synonymsBass, synonymsDrums, synonymsKeyboard, singer, guitarist, bassist, drummer, keyboardist);
//				}									
				// insert in N-gram Vector of current web page
				nounsNgrams.elementAt(i).put(potentialBandMember, new Integer(index));

			}


		} // end N-gram-nouns

	}


	/**
	 * Tests a context containing a potential band member for appliance of rules. 
	 */
	private void testRules(EntityContext context, Vector<String> synonymsSinger, Vector<String> synonymsGuitarist, Vector<String> synonymsBassist,	Vector<String> synonymsDrummer, Vector<String> synonymsKeyboardist, Vector<String> synonymsVocals, Vector<String> synonymsGuitar, Vector<String> synonymsBass, Vector<String> synonymsDrums, Vector<String> synonymsKeyboard, Hashtable<String, Integer>[] singer, Hashtable<String, Integer>[] guitarist, Hashtable<String, Integer>[] bassist, Hashtable<String, Integer>[] drummer, Hashtable<String, Integer>[] keyboardist) {
		String potentialBandMember = context.getPotentialBandMember();
		String precedingContext = context.getprecedingContext();
		String succeedingContext = context.getPreceedingContext();
		// test if rule R1 "X plays the ([vocals|drums|guitar|bass|...])" applies 
		for (int j=0; j<synonymsVocals.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("plays the "+synonymsVocals.elementAt(j))) {
				if (singer[0].containsKey(potentialBandMember))
					singer[0].put(potentialBandMember, new Integer(singer[0].get(potentialBandMember).intValue()+1));
				else
					singer[0].put(potentialBandMember, new Integer(1));
				System.out.println("R1: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitar.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("plays the "+synonymsGuitar.elementAt(j))) {
				if (guitarist[0].containsKey(potentialBandMember))
					guitarist[0].put(potentialBandMember, new Integer(guitarist[0].get(potentialBandMember).intValue()+1));
				else
					guitarist[0].put(potentialBandMember, new Integer(1));
				System.out.println("R1: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBass.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("plays the "+synonymsBass.elementAt(j))) {
				if (bassist[0].containsKey(potentialBandMember))
					bassist[0].put(potentialBandMember, new Integer(bassist[0].get(potentialBandMember).intValue()+1));
				else
					bassist[0].put(potentialBandMember, new Integer(1));
				System.out.println("R1: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrums.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("plays the "+synonymsDrums.elementAt(j))) {
				if (drummer[0].containsKey(potentialBandMember))
					drummer[0].put(potentialBandMember, new Integer(drummer[0].get(potentialBandMember).intValue()+1));
				else
					drummer[0].put(potentialBandMember, new Integer(1));
				System.out.println("R1: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboard.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("plays the "+synonymsKeyboard.elementAt(j))) {
				if (keyboardist[0].containsKey(potentialBandMember))
					keyboardist[0].put(potentialBandMember, new Integer(keyboardist[0].get(potentialBandMember).intValue()+1));
				else
					keyboardist[0].put(potentialBandMember, new Integer(1));
				System.out.println("R1: keyboardist " + potentialBandMember);
			}
		
		// test if rule R2 "X who plays the ([vocals|drums|guitar|bass|...])" applies 
		for (int j=0; j<synonymsVocals.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("who plays the "+synonymsVocals.elementAt(j))) {
				if (singer[1].containsKey(potentialBandMember))
					singer[1].put(potentialBandMember, new Integer(singer[1].get(potentialBandMember).intValue()+1));
				else
					singer[1].put(potentialBandMember, new Integer(1));
				System.out.println("R2: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitar.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("who plays the "+synonymsGuitar.elementAt(j))) {
				if (guitarist[1].containsKey(potentialBandMember))
					guitarist[1].put(potentialBandMember, new Integer(guitarist[1].get(potentialBandMember).intValue()+1));
				else
					guitarist[1].put(potentialBandMember, new Integer(1));
				System.out.println("R2: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBass.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("who plays the "+synonymsBass.elementAt(j))) {
				if (bassist[1].containsKey(potentialBandMember))
					bassist[1].put(potentialBandMember, new Integer(bassist[1].get(potentialBandMember).intValue()+1));
				else
					bassist[1].put(potentialBandMember, new Integer(1));
				System.out.println("R2: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrums.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("who plays the "+synonymsDrums.elementAt(j))) {
				if (drummer[1].containsKey(potentialBandMember))
					drummer[1].put(potentialBandMember, new Integer(drummer[1].get(potentialBandMember).intValue()+1));
				else
					drummer[1].put(potentialBandMember, new Integer(1));
				System.out.println("R2: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboard.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("who plays the "+synonymsKeyboard.elementAt(j))) {
				if (keyboardist[1].containsKey(potentialBandMember))
					keyboardist[1].put(potentialBandMember, new Integer(keyboardist[1].get(potentialBandMember).intValue()+1));
				else
					keyboardist[1].put(potentialBandMember, new Integer(1));
				System.out.println("R2: keyboardist " + potentialBandMember);
			}
		
		// test if rule R3 "[singer|drummer|guitarist|bassist|...] X" applies 
		for (int j=0; j<synonymsSinger.size(); j++)
			if (precedingContext != null && precedingContext.endsWith(synonymsSinger.elementAt(j))) {
				if (singer[2].containsKey(potentialBandMember))
					singer[2].put(potentialBandMember, new Integer(singer[2].get(potentialBandMember).intValue()+1));
				else
					singer[2].put(potentialBandMember, new Integer(1));
				System.out.println("R3: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitarist.size(); j++)
			if (precedingContext != null && precedingContext.endsWith(synonymsGuitarist.elementAt(j))) {
				if (guitarist[2].containsKey(potentialBandMember))
					guitarist[2].put(potentialBandMember, new Integer(guitarist[2].get(potentialBandMember).intValue()+1));
				else
					guitarist[2].put(potentialBandMember, new Integer(1));
				System.out.println("R3: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBassist.size(); j++)										
			if (precedingContext != null && precedingContext.endsWith(synonymsBassist.elementAt(j))) {
				if (bassist[2].containsKey(potentialBandMember))
					bassist[2].put(potentialBandMember, new Integer(bassist[2].get(potentialBandMember).intValue()+1));
				else
					bassist[2].put(potentialBandMember, new Integer(1));
				System.out.println("R3: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrummer.size(); j++)
			if (precedingContext != null && precedingContext.endsWith(synonymsDrummer.elementAt(j))) {
				if (drummer[2].containsKey(potentialBandMember))
					drummer[2].put(potentialBandMember, new Integer(drummer[2].get(potentialBandMember).intValue()+1));
				else
					drummer[2].put(potentialBandMember, new Integer(1));
				System.out.println("R3: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboardist.size(); j++)
			if (precedingContext != null && precedingContext.endsWith(synonymsKeyboardist.elementAt(j))) {
				if (keyboardist[2].containsKey(potentialBandMember))
					keyboardist[2].put(potentialBandMember, new Integer(keyboardist[2].get(potentialBandMember).intValue()+1));
				else
					keyboardist[2].put(potentialBandMember, new Integer(1));
				System.out.println("R3: keyboardist " + potentialBandMember);
			}
		
		// test if rule R4 "X is the [singer|drummer|guitarist|bassist|...]" applies 
		for (int j=0; j<synonymsSinger.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("is the "+synonymsSinger.elementAt(j))) {
				if (singer[3].containsKey(potentialBandMember))
					singer[3].put(potentialBandMember, new Integer(singer[3].get(potentialBandMember).intValue()+1));
				else
					singer[3].put(potentialBandMember, new Integer(1));
				System.out.println("R4: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitarist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("is the "+synonymsGuitarist.elementAt(j))) {
				if (guitarist[3].containsKey(potentialBandMember))
					guitarist[3].put(potentialBandMember, new Integer(guitarist[3].get(potentialBandMember).intValue()+1));
				else
					guitarist[3].put(potentialBandMember, new Integer(1));
				System.out.println("R4: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBassist.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("is the "+synonymsBassist.elementAt(j))) {
				if (bassist[3].containsKey(potentialBandMember))
					bassist[3].put(potentialBandMember, new Integer(bassist[3].get(potentialBandMember).intValue()+1));
				else
					bassist[3].put(potentialBandMember, new Integer(1));
				System.out.println("R4: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrummer.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("is the "+synonymsDrummer.elementAt(j))) {
				if (drummer[3].containsKey(potentialBandMember))
					drummer[3].put(potentialBandMember, new Integer(drummer[3].get(potentialBandMember).intValue()+1));
				else
					drummer[3].put(potentialBandMember, new Integer(1));
				System.out.println("R4: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboardist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("is the "+synonymsKeyboardist.elementAt(j))) {
				if (keyboardist[3].containsKey(potentialBandMember))
					keyboardist[3].put(potentialBandMember, new Integer(keyboardist[3].get(potentialBandMember).intValue()+1));
				else
					keyboardist[3].put(potentialBandMember, new Integer(1));
				System.out.println("R4: keyboardist " + potentialBandMember);
			}

		// test if rule R5 "X, the [singer|drummer|guitarist|bassist|...]" applies 
		for (int j=0; j<synonymsSinger.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("the "+synonymsSinger.elementAt(j))) {
				if (singer[4].containsKey(potentialBandMember))
					singer[4].put(potentialBandMember, new Integer(singer[4].get(potentialBandMember).intValue()+1));
				else
					singer[4].put(potentialBandMember, new Integer(1));
				System.out.println("R5: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitarist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("the "+synonymsGuitarist.elementAt(j))) {
				if (guitarist[4].containsKey(potentialBandMember))
					guitarist[4].put(potentialBandMember, new Integer(guitarist[4].get(potentialBandMember).intValue()+1));
				else
					guitarist[4].put(potentialBandMember, new Integer(1));
				System.out.println("R5: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBassist.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("the "+synonymsBassist.elementAt(j))) {
				if (bassist[4].containsKey(potentialBandMember))
					bassist[4].put(potentialBandMember, new Integer(bassist[4].get(potentialBandMember).intValue()+1));
				else
					bassist[4].put(potentialBandMember, new Integer(1));
				System.out.println("R5: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrummer.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("the "+synonymsDrummer.elementAt(j))) {
				if (drummer[4].containsKey(potentialBandMember))
					drummer[4].put(potentialBandMember, new Integer(drummer[4].get(potentialBandMember).intValue()+1));
				else
					drummer[4].put(potentialBandMember, new Integer(1));
				System.out.println("R5: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboardist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith("the "+synonymsKeyboardist.elementAt(j))) {
				if (keyboardist[4].containsKey(potentialBandMember))
					keyboardist[4].put(potentialBandMember, new Integer(keyboardist[4].get(potentialBandMember).intValue()+1));
				else
					keyboardist[4].put(potentialBandMember, new Integer(1));
				System.out.println("R5: keyboardist " + potentialBandMember);
			}
		
		// test if rule R6 "X ([vocals|drums|guitar|bass|...])" applies 
		for (int j=0; j<synonymsVocals.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsVocals.elementAt(j))) {
				if (singer[5].containsKey(potentialBandMember))
					singer[5].put(potentialBandMember, new Integer(singer[5].get(potentialBandMember).intValue()+1));
				else
					singer[5].put(potentialBandMember, new Integer(1));
				System.out.println("R6: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitar.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsGuitar.elementAt(j))) {
				if (guitarist[5].containsKey(potentialBandMember))
					guitarist[5].put(potentialBandMember, new Integer(guitarist[5].get(potentialBandMember).intValue()+1));
				else
					guitarist[5].put(potentialBandMember, new Integer(1));
				System.out.println("R6: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBass.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsBass.elementAt(j))) {
				if (bassist[5].containsKey(potentialBandMember))
					bassist[5].put(potentialBandMember, new Integer(bassist[5].get(potentialBandMember).intValue()+1));
				else
					bassist[5].put(potentialBandMember, new Integer(1));
				System.out.println("R6: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrums.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsDrums.elementAt(j))) {
				if (drummer[5].containsKey(potentialBandMember))
					drummer[5].put(potentialBandMember, new Integer(drummer[5].get(potentialBandMember).intValue()+1));
				else
					drummer[5].put(potentialBandMember, new Integer(1));
				System.out.println("R6: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboard.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsKeyboard.elementAt(j))) {
				if (keyboardist[5].containsKey(potentialBandMember))
					keyboardist[5].put(potentialBandMember, new Integer(keyboardist[5].get(potentialBandMember).intValue()+1));
				else
					keyboardist[5].put(potentialBandMember, new Integer(1));
				System.out.println("R6: keyboardist " + potentialBandMember);
			}
		
		// test if rule R7 "X ([singer|drummer|guitarist|bassist|...])" applies 
		for (int j=0; j<synonymsSinger.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsSinger.elementAt(j))) {
				if (singer[6].containsKey(potentialBandMember))
					singer[6].put(potentialBandMember, new Integer(singer[6].get(potentialBandMember).intValue()+1));
				else
					singer[6].put(potentialBandMember, new Integer(1));
				System.out.println("R7: singer " + potentialBandMember);
			}
		for (int j=0; j<synonymsGuitarist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsGuitarist.elementAt(j))) {
				if (guitarist[6].containsKey(potentialBandMember))
					guitarist[6].put(potentialBandMember, new Integer(guitarist[6].get(potentialBandMember).intValue()+1));
				else
					guitarist[6].put(potentialBandMember, new Integer(1));
				System.out.println("R7: guitarist " + potentialBandMember);
			}
		for (int j=0; j<synonymsBassist.size(); j++)										
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsBassist.elementAt(j))) {
				if (bassist[6].containsKey(potentialBandMember))
					bassist[6].put(potentialBandMember, new Integer(bassist[6].get(potentialBandMember).intValue()+1));
				else
					bassist[6].put(potentialBandMember, new Integer(1));
				System.out.println("R7: bassist " + potentialBandMember);
			}
		for (int j=0; j<synonymsDrummer.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsDrummer.elementAt(j))) {
				if (drummer[6].containsKey(potentialBandMember))
					drummer[6].put(potentialBandMember, new Integer(drummer[6].get(potentialBandMember).intValue()+1));
				else
					drummer[6].put(potentialBandMember, new Integer(1));
				System.out.println("R7: drummer " + potentialBandMember);
			}
		for (int j=0; j<synonymsKeyboardist.size(); j++)
			if (succeedingContext != null && succeedingContext.toLowerCase().startsWith(synonymsKeyboardist.elementAt(j))) {
				if (keyboardist[6].containsKey(potentialBandMember))
					keyboardist[6].put(potentialBandMember, new Integer(keyboardist[6].get(potentialBandMember).intValue()+1));
				else
					keyboardist[6].put(potentialBandMember, new Integer(1));
				System.out.println("R7: keyboardist " + potentialBandMember);
			}
	}

	/**
	 * Appends text to result file.
	 * 
	 * @param text
	 */
	private void writeResult(String text) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(BandMemberDetector.outputFile, true));
			bw.write(text);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BandMemberDetector bmd = new BandMemberDetector();
		File dir = BandMemberDetector.dir;
		File[] files = dir.listFiles();
		for (int i=0; i<files.length; i++) {
			if (files[i].isDirectory()) {
				bmd.writeResult(files[i].getName()+ "\n");
				bmd.extractProbableBandMembersFromDocuments(files[i], BandMemberDetector.wordList, new HTMLFileFilter());
			}
		}
	}
}


class EntityContext {
	String precedingContext;
	String subsequentContext;
	String potentialBandMember;

	public EntityContext(String potentialBandMember, String precedingContext, String subsequentContext) {
		super();
		this.potentialBandMember = potentialBandMember;
		this.precedingContext = precedingContext;
		this.subsequentContext = subsequentContext;
	}

	/**
	 * @return Returns the precedingContext.
	 */
	public String getprecedingContext() {
		return precedingContext;
	}

	/**
	 * @param precedingContext The precedingContext to set.
	 */
	public void setprecedingContext(String precedingContext) {
		this.precedingContext = precedingContext;
	}

	/**
	 * @return Returns the subsequentContext.
	 */
	public String getPreceedingContext() {
		return subsequentContext;
	}

	/**
	 * @param subsequentContext The subsequentContext to set.
	 */
	public void setSubsequentContext(String subsequentContext) {
		this.subsequentContext = subsequentContext;
	}

	/**
	 * @return Returns the potentialBandMember.
	 */
	public String getPotentialBandMember() {
		return potentialBandMember;
	}

	/**
	 * @param potentialBandMember The potentialBandMember to set.
	 */
	public void setPotentialBandMember(String potentialBandMember) {
		this.potentialBandMember = potentialBandMember;
	}
	
}