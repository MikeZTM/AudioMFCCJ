package comirva.util.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

import comirva.io.filefilter.HTMLFileFilter;
import com.wcohen.ss.*;

import cp.util.*;

/**
 * This class evaluates automatically extracted band members.
 * 
 * @author Markus 
 */
public class BandMemberEvaluator {
	private boolean useFuzzyCharMatching = false;		// treat very similar chars as same (e.g. � and i)
	private boolean useJaroWinkler = false;
	private double jwThreshold = 0.9f;					// threshold for Jaro-Winkler string similarity
	private float minimumDF = 0.0f;						// minimum document frequency of an N-gram which is necessary to retain it (fraction on DF of term with maximum DF)

	public BandMemberEvaluator() {
	}

	public void evaluate() {
//		File amgFile = new File("/media/POWERRAM/Research/Data/band_members/Metal_private_members.txt");
//		File crawlFile = new File("/media/POWERRAM/Research/Data/band_members/Metal_private_MM_results_minDF0_with_keyboard.txt");
		File amgFile = new File("C:/Research/Data/band_members/Metal_private_members.txt");
		File crawlFile = new File("C:/Research/Data/band_members/Metal_private_LUM_results_minDF0_with_keyboard.txt"); //Metal_private_M_results.txt"); //Metal_private_M_results_minDF_0.txt

		Vector<Band> bmAMG = readAMGBM(amgFile);
		Vector<Band> bmCrawl = readCrawlBM(crawlFile);
		doDFFiltering(bmCrawl, minimumDF);
		
		int bands = 0;
		int existingBMs = 0;
		int correctBMs = 0;
		int incorrectBMs = 0;				// prediction made, but incorrect
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
				System.out.println(bCrawl.getName());
				bands++;
				try {
					for (int j=0; j<Instrument.instruments.length; j++) {
						String instrumentType = Instrument.instruments[j];
						String cMember = bCrawl.getMostProbableMemberForInstrument(new Instrument(instrumentType));
//						String aMember = bAMG.getMostProbableMemberForInstrument(new Instrument(instrumentType));
						Vector<String> aMembers =  bAMG.getMembersForInstrument(new Instrument(instrumentType));
						if (cMember != null && this.useFuzzyCharMatching)
							cMember = cp.util.HTMLCharacters.toBasicString(cMember);
						if (aMembers != null && this.useFuzzyCharMatching) {
							for (int k=0; k<aMembers.size(); k++)
								aMembers.set(k, cp.util.HTMLCharacters.toBasicString(aMembers.elementAt(k)));
						}
						if (aMembers != null) {
							existingBMs++;
							if (cMember != null) {
								System.out.println("c: "+ instrumentType + ": " + cMember);
								System.out.print("a: " + instrumentType + ": ");
								Enumeration<String> e = aMembers.elements();
								while (e.hasMoreElements())	System.out.print(e.nextElement()+ ", "); System.out.println();								
//								Jaccard jac = new Jaccard();						
//								NeedlemanWunsch nw = new NeedlemanWunsch();
//								Level2Levenstein l2l = new Level2Levenstein();
//								Level2JaroWinkler l2jw = new Level2JaroWinkler();
//								JaroWinkler jw = new JaroWinkler();
//								System.out.println("jaccard-distance: " + jac.score(cMember, aMember));
//								System.out.println("needleman/wunsch-distance: " + nw.score(cMember, aMember));
//								System.out.println("level2-levenstein-distance: " + l2l.score(cMember, aMember));
//								System.out.println("jaro/winkler-distance: " + jw.score(cMember, aMember));
//								System.out.println("level2-jaro/winkler-distance: " + l2jw.score(cMember, aMember));
								if (!this.useJaroWinkler) {			// use JaroWinkler for string similarity
									if (aMembers.contains(cMember)) {				// correct prediction
										correctBMs++;
										System.out.println("correct.");
									} else { incorrectBMs++; }						// incorrect prediction
								} else {
									Level2JaroWinkler l2jw = new Level2JaroWinkler();
									e = aMembers.elements();
									boolean similarAccToJarWinkler = false;
									double dist = 0, d = 0;
									while (e.hasMoreElements()) {
										if ((d = l2jw.score(e.nextElement(), cMember)) > jwThreshold) {
											similarAccToJarWinkler = true;
											dist = d;
										}
									}
									if (similarAccToJarWinkler) {
										correctBMs++;
										System.out.println("correct. l2jw-dist=" + dist);
									} else { incorrectBMs++; }						// incorrect prediction
								}
								
								
							}
						}
					}
				} catch (InvalidInstrumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
				System.out.println(bCrawl.getName() + " not found.");
		}
		System.out.println(bands + " bands in ground truth and found by web crawl");
		System.out.println("AMG knows " + existingBMs + " band members");
		System.out.println("From these " + correctBMs + " were correctly found by our web crawl");
		System.out.println("From these " + incorrectBMs + " were incorrectly predicted");
		System.out.println("Recall: " + (float)correctBMs/(float)existingBMs);
		System.out.println("Precision: " + (float)incorrectBMs/(float)(correctBMs+incorrectBMs));

//		for (int i=0; i<bmAMG.size(); i++) {
//		Band b = bmAMG.elementAt(i);
//		System.out.println(b.getName());
//		try {
//		System.out.println("Vocals: " + b.getMostProbableMemberForInstrument(new Instrument("Vocals")));
//		System.out.println("Guitar: " + b.getMostProbableMemberForInstrument(new Instrument("Guitar")));
//		System.out.println("Bass: " + b.getMostProbableMemberForInstrument(new Instrument("Bass")));
//		System.out.println("Drums: " + b.getMostProbableMemberForInstrument(new Instrument("Drums")));
//		} catch (InvalidInstrumentException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		}
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
			for (int n=0; n<Band.rules; n++) { 
				Vector<BandMember> bm = band.getBandMembers()[n];
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
			}
			System.out.println(band.getName() + " has a maxDF of " + maxDF);
			// discard all potential members with too low DF
			Vector<BandMember>[] newBM = new Vector[Band.rules];
			for (int n=0; n<Band.rules; n++) { 
				Vector<BandMember> bm = band.getBandMembers()[n];
				newBM[n] = new Vector<BandMember>();
				for (int i=0; i<bm.size(); i++) {
					Vector<Instrument> instr = bm.elementAt(i).getInstruments();
					Enumeration<Instrument> enumInstr = instr.elements();
					while (enumInstr.hasMoreElements()) {
						if ((double)enumInstr.nextElement().getFrequency()/(double)maxDF >= this.minimumDF && !newBM[n].contains(bm.elementAt(i)))
							newBM[n].addElement(bm.elementAt(i));
					}
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
					members = new Vector[Band.rules];
					members[1] = new Vector<BandMember>();	// for AMG, we only need one Vector (since no rules apply) 
					Band band = new Band(currentArtist, members);
					bms.addElement(band);				
				} else {		// band member found
					if ((idx = line.indexOf(":")) != -1) {
						currentBM = line.substring(0, idx);					// get band member
						String instruments = line.substring(idx+1);
						StringTokenizer st = new StringTokenizer(instruments, " ");		// get instruments
						if (st.countTokens() == 0) {
							BandMember member = new BandMember(currentBM);
							members[1].addElement(member);
						} else {
							Vector<Instrument> instr = new Vector<Instrument>(5);
							BandMember member = new BandMember(currentBM, instr);
							members[1].addElement(member);
							while (st.hasMoreElements()) {
								String instrumentName = (String)st.nextElement();
								instr.addElement(new Instrument(instrumentName));
							}							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// calculate most probable band members (those whose summed rule appliance is a maximum)
		Enumeration<Band> eb = bms.elements();
		while (eb.hasMoreElements())
			eb.nextElement().setOverallMemberMaxOcc();
		return bms;
	}

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
					currentArtist = TextTool.removeUnwantedChars(line);
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
			eb.nextElement().setOverallMemberMaxOcc();
		return bms;
	}

	public static void main(String[] args) {
		BandMemberEvaluator bme = new BandMemberEvaluator();
		bme.evaluate();
	}

}

class Band {
	private String name;												// name band's name
	private Vector<BandMember>[] bandMembers = new Vector[Band.rules];	// a Vector[] to store the band members according to each of the rules 
	// bandMembers[1] to bandMembers[7] store the bandMembers accordint to R1 to R7
	// bandMembers[0] stores the most probable overall member
	static int rules = 8;		// the number of rules wrt which band members are detected

	public Band(String name) {
		this.name = name;
		// init Vector[] links to band members
		for (int i=0; i<Band.rules; i++)
			this.bandMembers[i] = new Vector<BandMember>();
	}
	public Band(String name, Vector<BandMember>[] bm) {
		this.name = name;
		this.bandMembers = bm;
	}

	public Vector<BandMember>[] getBandMembers() {
		return bandMembers;
	}

	public void setBandMembers(Vector<BandMember>[] bandMembers) {
		this.bandMembers = bandMembers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Returns the most probable band member for a given instrument.
	 * It is assumed that setOverallMember() has already been executed.
	 * 
	 * @param i
	 * @return
	 */
	public String getMostProbableMemberForInstrument(Instrument instr) throws InvalidInstrumentException {
		String bmName;
		Hashtable<String,Integer>[] members = convertDataStructure();
		// search for instrument
		int numberInstruments = Instrument.instruments.length;
		int idxInstrument = -1;
		for (int i=0; i<numberInstruments; i++) {
			if (instr.getInstrument().compareTo(Instrument.instruments[i]) == 0) {
				idxInstrument = i;
			}
		}
		if (idxInstrument == -1)
			throw new InvalidInstrumentException();
		else {
			int maxOcc = 0;
			String maxName = new String();
			Enumeration<String> e = members[idxInstrument].keys();
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				if (maxOcc < members[idxInstrument].get(name)) {		// new max occurring member found
					maxName = name;
					maxOcc = members[idxInstrument].get(name);	
				}
			}
			if (maxOcc > 0)
				bmName = maxName;
			else
				bmName = null;
		}
		return bmName;
	}


	/**
	 * Returns the members playing the given Instrument.
	 * It is assumed that setOverallMember() has already been executed.
	 * 
	 * @param i
	 * @return
	 */
	public Vector<String> getMembersForInstrument(Instrument instr) throws InvalidInstrumentException {
		Vector<String> bmName = new Vector<String>();
		Hashtable<String,Integer>[] members = convertDataStructure();
		// search for instrument
		int numberInstruments = Instrument.instruments.length;
		int idxInstrument = -1;
		for (int i=0; i<numberInstruments; i++) {
			if (instr.getInstrument().compareTo(Instrument.instruments[i]) == 0) {
				idxInstrument = i;
			}
		}
		if (idxInstrument == -1)
			throw new InvalidInstrumentException();
		else {
			Enumeration<String> e = members[idxInstrument].keys();
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				bmName.addElement(name);
			}
		}
		if (bmName.isEmpty())
			bmName = null;
		return bmName;
	}


	/**
	 * Calculates the most probable overall band members and
	 * stores them in bandMembers[0]. For every instrument, only the
	 * member with the highest DF is taken.
	 */
	public void setOverallMemberMaxOcc() {
		Hashtable<String,Integer>[] members = convertDataStructure();
		int numberInstruments = Instrument.instruments.length;
		String instName;
		// write resulting most probable band members to bandMembers[0]
		this.bandMembers[0] = new Vector<BandMember>();
		for (int i=0; i<numberInstruments; i++) {
			Hashtable<String, Integer> ht = members[i];
			// search maximum occurrence
			int maxOcc = 0;
			String maxName = new String();
			Enumeration<String> e = ht.keys();
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				if (maxOcc < ht.get(name)) {		// new max occurring member found
					maxName = name;
					maxOcc = ht.get(name);	
				}
			}
			instName = new String();
			if (i==0)
				instName = "Vocals";
			if (i==1)
				instName = "Guitar";
			if (i==2)
				instName = "Bass";
			if (i==3)
				instName = "Drums";
			if (i==4)
				instName = "Keyboard";
			// store maximum occurring members in bandMembers[0]
			try {
				Vector<Instrument> instruments = new Vector<Instrument>();
				instruments.addElement(new Instrument(instName,maxOcc));
				this.bandMembers[0].addElement(new BandMember(maxName, instruments));
			} catch (InvalidInstrumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets the overall members to all members remaining after
	 * DF filtering. The overall members are stores in bandMembers[0].
	 */
	public void setOverallMemberAllOcc() {
		Hashtable<String,Integer>[] members = convertDataStructure();
		int numberInstruments = Instrument.instruments.length;
		String instName;
		// write resulting most probable band members to bandMembers[0]
		this.bandMembers[0] = new Vector<BandMember>();
		for (int i=0; i<numberInstruments; i++) {
			instName = new String();
			if (i==0)
				instName = "Vocals";
			if (i==1)
				instName = "Guitar";
			if (i==2)
				instName = "Bass";
			if (i==3)
				instName = "Drums";
			if (i==4)
				instName = "Keyboard";
			// store maximum occurring members in bandMembers[0]
			try {
				Hashtable<String, Integer> ht = members[i];
				Enumeration<String> e = ht.keys();
				while (e.hasMoreElements()) {				// go through all members that play current instrument
					String name = e.nextElement();			// the band member's name
					Integer occ = ht.get(name);				// the sum of rules where the band members is assigned the current instrument
					// add to bandMember[0]
					Vector<Instrument> instruments = new Vector<Instrument>();
					instruments.addElement(new Instrument(instName,occ));
					this.bandMembers[0].addElement(new BandMember(name, instruments));
				}
			} catch (InvalidInstrumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Returns a Hashtable<String,Integer>[] structure containing,
	 * for each instrument, 
	 * a Hashtable containing all possible
	 * band members (String) that play the instrument and the number
	 * of the member,instrument-occurrence (Integer).
	 * 
	 * @return
	 */
	private Hashtable<String,Integer>[] convertDataStructure() {
		int numberInstruments = Instrument.instruments.length;
		Hashtable<String,Integer>[] members = new Hashtable[numberInstruments];
		String instName;
		String memberName;
		int occurrence;
		for (int i=0; i<numberInstruments; i++)
			members[i] = new Hashtable<String,Integer>();
		// get members
		for (int i=1; i<Band.rules; i++) {
			if (this.bandMembers[i] != null && !this.bandMembers[i].isEmpty()) {
				Enumeration<BandMember> ebm = this.bandMembers[i].elements();
				while (ebm.hasMoreElements()) {
					BandMember bm = ebm.nextElement();
					memberName = bm.getName();
					Vector<Instrument> vinst = bm.getInstruments();
					if (vinst != null && !vinst.isEmpty()) {
						Enumeration<Instrument> ei = vinst.elements();
						while (ei.hasMoreElements()) {
							Instrument instr = ei.nextElement();
							instName = instr.getInstrument();
							if (instName != null) {
								occurrence = instr.getFrequency();
								int mappingInstrumentsArrayIndex = 0;	// maps instruments to array indices
								if (instName.compareTo("Vocals") == 0)
									mappingInstrumentsArrayIndex = 0;
								if (instName.compareTo("Guitar") == 0)
									mappingInstrumentsArrayIndex = 1;
								if (instName.compareTo("Bass") == 0)
									mappingInstrumentsArrayIndex = 2;
								if (instName.compareTo("Drums") == 0)
									mappingInstrumentsArrayIndex = 3;
								if (instName.compareTo("Keyboard") == 0)
									mappingInstrumentsArrayIndex = 4;
								Hashtable<String,Integer> ht = members[mappingInstrumentsArrayIndex];
								if (ht.containsKey(memberName)) {		// band member already exists?
									// add occurrence according to current rule to band member
									ht.put(memberName, ht.get(memberName).intValue()+occurrence);
								} else {			// band member does not exist
									ht.put(memberName, new Integer(occurrence));
								}	
							}
						}
					}
				}
			}
		}
		return members;
	}

}

class BandMember {
	private String name;							// the name of the band member
	private Vector<Instrument> instruments;			// instruments played by the band member

	public BandMember(String name) {
		this.name = name;
	}
	public BandMember(String name, Vector<Instrument> instruments) {
		this.name = name;
		this.instruments = instruments;
	}

	public Vector<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(Vector<Instrument> instruments) {
		this.instruments = instruments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

class Instrument {
	static String[] instruments = new String[] {"Vocals", "Guitar", "Bass", "Drums", "Keyboard"};	// possible instruments
	private String name; 
	private int frequency;							// frequency of rule appliance (this instrument for assigned band member)
	private boolean validInstrument = false;		// a valid instrument? 

	public Instrument(String name, int frequency) throws InvalidInstrumentException {
		validateInstrument(name, frequency);
	}
	public Instrument(String name) throws InvalidInstrumentException {
		validateInstrument(name, 1);
	}

	private void validateInstrument(String name, int frequency) {
		for (int i=0; i<instruments.length; i++)
			if (instruments[i].compareTo(name) == 0)
				this.validInstrument = true;
		if (this.validInstrument) {
			this.name = name;
			this.frequency = frequency;
		} else {
//			throw new InvalidInstrumentException();
		}
	}

	public boolean isValidInstrument() {
		return this.validInstrument;
	}

	public String getInstrument() {
		return this.name;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}

class InvalidInstrumentException extends Exception {
	public InvalidInstrumentException() {
		super();
	}
}