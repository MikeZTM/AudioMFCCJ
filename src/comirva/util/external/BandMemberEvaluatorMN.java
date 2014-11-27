package comirva.util.external;

import java.io.*;
import java.util.*;

import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.Level2JaroWinkler;

import comirva.io.filefilter.HTMLFileFilter;
import cp.util.*;

/**
 * This class evaluates automatically extracted band members.
 * 
 * @author Markus 
 */
public class BandMemberEvaluatorMN {
	private boolean useFuzzyCharMatching = false;		// treat very similar chars as same (e.g. � and i)
	private boolean useJaroWinkler = true;
	private float jwThreshold = 0.9f;
	private static float minimumDF = 0.25f;				// minimum document frequency of an N-gram which is necessary to retain it (fraction on DF of term with maximum DF)
	private static File amgFile = new File("C:/Research/Data/band_members/Metal_private_members_formers.txt"); //Metal_private_members.txt");
	private static File crawlFile = new File("C:/Research/Data/band_members/Metal_private_MM_results_minDF0_with_keyboard.txt");
//	File crawlFile = new File("E:/Research/Data/band_members/Metal_private_LUM_results_noinstr_DF0.txt"); //Metal_private_M_results_minDF_0.txt");
//	File amgFile = new File("/Research/Data/band_members/Metal_private_members_formers.txt");
//	File crawlFile = new File("C:/Research/Data/band_members/Metal_private_MR_results_noinstr_DF0.txt");
	
	public BandMemberEvaluatorMN() {
	}
	
	public void evaluate() {
		Vector<Band> bmAMG = readAMGBM(amgFile);
		Vector<Band> bmCrawl = readCrawlBM(crawlFile);
//		Vector<Band> bmCrawl = readGTBM(crawlFile);
		doDFFiltering(bmCrawl, minimumDF);
		
		int bands = 0;
		float overallPrec = 0, overallRec = 0; 		// overall precision and recall
		for (int i=0; i<bmCrawl.size(); i++) {
			Band bCrawl = bmCrawl.elementAt(i);
			Band bAMG = null;
			// search band in ground truth
			for (int j=0; j<bmAMG.size(); j++) {
				if (bmAMG.elementAt(j).getName().equals(bCrawl.getName())) {
					bAMG = bmAMG.elementAt(j);
				}
			}
			// only proceed if band was found in ground truth
			if (bAMG != null) {
//				System.out.println(bCrawl.getName() + " " + bAMG.getName());			
				bands++;
				
				Vector<String> amgMembers = getMembers(bAMG);
				Vector<String> crawlMembers = getMembers(bCrawl);
			
				float prec = getPrecision(amgMembers, crawlMembers);
				float rec = getRecall(amgMembers, crawlMembers);
				
				
//				System.out.println("prec: " + prec + "\trec: " + rec);
				
				overallPrec += prec/bmCrawl.size();
				overallRec += rec/bmCrawl.size();

			} 
//			else
//				System.out.println(bCrawl.getName() + " not found.");
		}
//		System.out.println(bands + " bands in ground truth and found by web crawl");
//		System.out.println("overall prec: " + overallPrec + "\toverall rec: " + overallRec);

		// println results in a format to create a diagram from them (for diferent t_DF values)
//		System.out.println("[" + overallPrec + "," + overallRec + "]"); // + " " + minimumDF);
		System.out.println(Math.round(overallPrec*10000)/100.0f + " / " + Math.round(overallRec*10000)/100.0f);
	}
	
	
	
	/**
	 * Filter out all potential band members whose DF is below threshold.  
	 * 
	 * @param b
	 * @param minDF
	 */
	private void doDFFiltering(Vector<Band> b, float minDF) {
		for(int m=0; m<b.size(); m++) {
			// get maximum DF for current band
			int maxDF = 0;
			Band band = b.elementAt(m);
			Vector<BandMember> bm = band.getBandMembers()[0];
			Enumeration<BandMember> ebm = bm.elements();
			while (ebm.hasMoreElements()) {
				BandMember bmem = ebm.nextElement();
				// get maximum DF for instrument
				Vector<Instrument> instr = bmem.getInstruments();
				Enumeration<Instrument> enumInstr = instr.elements();
				while (enumInstr.hasMoreElements()) {
					int df = enumInstr.nextElement().getFrequency();
					if (df > maxDF)
						maxDF = df;						
				}
			}
//			System.out.println(band.getName() + " has a maxDF of " + maxDF);
			// discard all potential members with too low DF
			Vector<BandMember>[] newBM = new Vector[1];
			newBM[0] = new Vector<BandMember>();
			for (int i=0; i<bm.size(); i++) {
				Vector<Instrument> instr = bm.elementAt(i).getInstruments();
				Enumeration<Instrument> enumInstr = instr.elements();
				while (enumInstr.hasMoreElements()) {
					if ((double)enumInstr.nextElement().getFrequency()/(double)maxDF >= this.minimumDF && !newBM[0].contains(bm.elementAt(i)))
						newBM[0].addElement(bm.elementAt(i));
				}
			}			
			band.setBandMembers(newBM);
			// remaining terms
//			for (int i=0; i<newBM[0].size(); i++) {
//				System.out.println(newBM[0].elementAt(i).getName());
//			}
//			System.out.println("---");
		}
	}
	
	/**
	 * Returns, for a given Band b, all of its members in a Vector<String>.
	 * 
	 * @param b
	 * @return
	 */
	private Vector<String> getMembers(Band b) {
		Vector<String> memberNames =  new Vector<String>();
		if (b != null && b.getBandMembers() != null) {
			for (int i=0; i < b.getBandMembers().length; i++) {
				if (b.getBandMembers()[i] != null) {
					Vector<BandMember> vbm = b.getBandMembers()[i];
					for (int j=0; j<vbm.size(); j++) {
						BandMember bm = vbm.elementAt(j);
						String memberName = new String();
						if (!this.useFuzzyCharMatching)
							memberName = bm.getName();
						else
							memberName = cp.util.HTMLCharacters.toBasicString(bm.getName());
						if (!memberNames.contains(memberName)) {		// only add if not already in list
							memberNames.addElement(memberName);
						}
					}					
				}
			}
		}
		return memberNames;
	}

	private float getPrecision(Vector<String> gTruth, Vector<String> crawl) {
		int gtSize = gTruth.size();
		int crawlSize = crawl.size();
		int correctFound = 0;
		for (int i=0; i < crawlSize; i++) {
			if (!this.useJaroWinkler) {
				if (gTruth.contains(crawl.elementAt(i)))
					correctFound++;
			} else {
				boolean jwAccepted = false;
				for (int j=0; j<gTruth.size(); j++) {
					Level2JaroWinkler l2jw = new Level2JaroWinkler();
					if (l2jw.score(crawl.elementAt(i), gTruth.elementAt(j)) >= this.jwThreshold)
						jwAccepted = true;
				}
				if (jwAccepted)
					correctFound++;
			}
		}
		return (float)correctFound/(float)crawlSize;
	}
	
	private float getRecall(Vector<String> gTruth, Vector<String> crawl) {
		int gtSize = gTruth.size();
		int crawlSize = crawl.size();
		int recalled = 0;
		for (int i=0; i < gtSize; i++) {
			if (!this.useJaroWinkler) {
				if (crawl.contains(gTruth.elementAt(i)))
					recalled++;
			} else {
				boolean jwAccepted = false;
				for (int j=0; j<crawl.size(); j++) {
					Level2JaroWinkler l2jw = new Level2JaroWinkler();
					if (l2jw.score(gTruth.elementAt(i), crawl.elementAt(j)) >= this.jwThreshold)
						jwAccepted = true;
				}
				if (jwAccepted)
					recalled++;
			}
		}
		return (float)recalled/(float)gtSize;
	}
	
	// read files like Metal_private_M_results_noinstr_DF0.txt (after potential band member extraction, before rule appliance)
	private Vector<Band> readGTBM(File f) {
		Vector<Band> bms = new Vector<Band>();
		Vector<BandMember>[] members = new Vector[Band.rules];
		BufferedReader readerFile;
		try {
			readerFile = new BufferedReader(new InputStreamReader(new FileInputStream(f), "Unicode"));
			String line;
			String currentArtist, currentBM;
			int idx;
			while ((line = readerFile.readLine()) != null) {
				// parse line read from file
				if ((idx = line.indexOf(":")) == -1) {		// no ":" -> band name found
					currentArtist = TextTool.removeUnwantedChars(line.toLowerCase());
					// init data structures
					members = new Vector[1];		// for ground truth, we only need one Vector (since no rules apply) 
					members[0] = new Vector<BandMember>();	
					Band band = new Band(currentArtist, members);
					bms.addElement(band);				
				} else {		// band member found
					currentBM = line.substring(0, idx);							// get band member
					int freq = new Integer(line.substring(idx+2)).intValue(); 	// get DF
					Vector<Instrument> instr = new Vector<Instrument>(1);
					BandMember member = new BandMember(currentBM, instr);
					members[0].addElement(member);
					Instrument instru = new Instrument("Vocals", freq); 
					if (instru.isValidInstrument())
						instr.addElement(instru);							
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		// calculate most probable band members (those whose summed rule appliance is a maximum)
//		Enumeration<Band> eb = bms.elements();
//		while (eb.hasMoreElements())
//			eb.nextElement().setOverallMember();
		return bms;
	}
	
	// read allmusic-ground truth
	private Vector<Band> readAMGBM(File f) {
		Vector<Band> bms = new Vector<Band>();
		Vector<BandMember>[] members = new Vector[Band.rules];
		BufferedReader readerFile;
		try {
			readerFile = new BufferedReader(new InputStreamReader(new FileInputStream(f), "Unicode"));
			String line;
			String currentArtist, currentBM;
			int idx;
			while ((line = readerFile.readLine()) != null) {
				// parse line read from file
				if (line.startsWith("---")) {		// band name found
					currentArtist = TextTool.removeUnwantedChars(line.substring(3).toLowerCase());
					// init data structures
					members = new Vector[1];		// for ground truth, we only need one Vector (since no rules apply) 
					members[0] = new Vector<BandMember>();	
					Band band = new Band(currentArtist, members);
					bms.addElement(band);				
				} else {		// band member found
					if ((idx = line.indexOf(":")) != -1) {
						currentBM = line.substring(0, idx);					// get band member
						String instruments = line.substring(idx+1);
						StringTokenizer st = new StringTokenizer(instruments, " ");		// get instruments
						if (st.countTokens() == 0) {
							BandMember member = new BandMember(currentBM);
							members[0].addElement(member);
						} else {
							Vector<Instrument> instr = new Vector<Instrument>(4);
							BandMember member = new BandMember(currentBM, instr);
							members[0].addElement(member);
							while (st.hasMoreElements()) {		// for all instruments
								String instrumentName = (String)st.nextElement();
								Instrument instru = new Instrument(instrumentName); 
								if (instru.isValidInstrument())
									instr.addElement(instru);
							}							
						}
					} else {			// no ":" after band member - insert complete line as band member
						currentBM = line;
						BandMember member = new BandMember(currentBM);
						members[0].addElement(member);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		// calculate most probable band members (those whose summed rule appliance is a maximum)
//		Enumeration<Band> eb = bms.elements();
//		while (eb.hasMoreElements())
//			eb.nextElement().setOverallMember();
		return bms;
	}
	
	// read result of analysis 
	private Vector<Band> readCrawlBM(File f) {
		Vector<Band> bms = new Vector<Band>();
		Vector<BandMember>[] members = new Vector[Band.rules];
		BufferedReader readerFile;
		try {
			readerFile = new BufferedReader(new InputStreamReader(new FileInputStream(f), "Unicode"));
			String line;
			String currentArtist, currentBM;
			int idx;
			while ((line = readerFile.readLine()) != null) {
				// parse line read from file
				if (!line.startsWith("singer acc to rule") && !line.startsWith("guitarist acc to rule") && !line.startsWith("bassist acc to rule") && !line.startsWith("drummer acc to rule") && !line.startsWith("keyboardist acc to rule")) {		// band name found
					currentArtist = TextTool.removeUnwantedChars(line.toLowerCase());
					// init data structures
					members = new Vector[Band.rules];
					for (int i=0; i<Band.rules; i++)
						members[i] = new Vector<BandMember>(); 
					Band band = new Band(currentArtist, members);
					bms.addElement(band);				
				} else {		// band member found
					// check for rule appliance
					if ((idx = line.indexOf(":")) != -1) {
						// get instrument
						String instrument = new String();
						if (line.startsWith("singer acc to rule"))
							instrument = "Vocals";
						if (line.startsWith("guitarist acc to rule"))
							instrument = "Guitar";
						if (line.startsWith("bassist acc to rule"))
							instrument = "Bass";
						if (line.startsWith("drummer acc to rule"))
							instrument = "Drums";	
						if (line.startsWith("keyboardist acc to rule"))
							instrument = "Keyboard";	
						int rule = new Integer(line.substring(idx-1, idx)).intValue();
						currentBM = line.substring(idx+2, idx+line.substring(idx+1).lastIndexOf(" ")+1);
						int freq = new Integer(line.substring(idx+2+line.substring(idx+1).lastIndexOf(" "))).intValue();
//						System.out.println(" R" + rule + " " + currentBM + " F"+freq);
						Vector<Instrument> instr = new Vector<Instrument>(5);
						instr.addElement(new Instrument(instrument, freq));
						BandMember member = new BandMember(currentBM, instr);
						members[rule].addElement(member);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// calculate most probable band members (those whose summed rule appliance is a maximum)
		Enumeration<Band> eb = bms.elements();
		while (eb.hasMoreElements())
			eb.nextElement().setOverallMemberAllOcc();
		return bms;
	}
	
	public static void main(String[] args) {
		BandMemberEvaluatorMN bme = new BandMemberEvaluatorMN();
		bme.evaluate();
		
//		// experiments for different t_DF values
//		float minDFInc = 0.01f;
//		float minDF = 0.0f;
//		while (minDF <= 1.0f) {
//			BandMemberEvaluatorMN.minimumDF = minDF;
//			bme.evaluate();
//			minDF += minDFInc;
//		}
	}
	
}
