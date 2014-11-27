
package comirva.io.web;

import cp.net.search.*;
import cp.util.*;

import java.util.*;
import java.io.*;


/**
 * 
 * @author peter knees
 */
public class WebTermExtractionThread extends Thread implements SearchengineConfig {
	private Hashtable<String, int[]> artistsAndTermVectors = new Hashtable<String, int[]>();
	private String[] artists;
	private String queryconstraint;
	
	private Vector<ThreadListener> threadlisteners = new Vector<ThreadListener>();
	
	public WebTermExtractionThread(String[] artists, String queryconstraint) {
		// check input
		if (artists == null)
			throw new IllegalArgumentException("no artists given");
		this.artists = artists;
		this.queryconstraint = queryconstraint;
	}
	
	public void addThreadListener(ThreadListener threadlistener) {
		threadlisteners.addElement(threadlistener);
	}
	
	
	/**
	 * When an object implementing interface <code>Runnable</code> is used to
	 * create a thread, starting the thread causes the object's <code>run</code>
	 * method to be called in that separately executing thread.
	 */
	public void run() {
		// use pre-crawled info, if existing
		File storedArtistVectorsHash = new File("./artistTermVecs.hash");
		if (storedArtistVectorsHash.canRead()) {
			try {
				artistsAndTermVectors = (Hashtable<String, int[]>)(new ObjectInputStream(new FileInputStream(storedArtistVectorsHash)).readObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("can't read serialized artist term vectors file.");
		}
		
		// for each artist extract dictionary vector
		for (int i=0; i<artists.length; i++) {
			if (artistsAndTermVectors.containsKey(artists[i]))
				continue;
			try {
				Searchengine se = new Searchengine(this, "\""+artists[i]+"\" "+queryconstraint);
//				String cleancontent = PlainTextExtractor.extractPlainText(se.getContent());
				
				int[] arttermvec = MusicDictionary.getVectorRepresentation(se.getContent());//cleancontent);
				artistsAndTermVectors.put(artists[i], arttermvec);
//				System.out.println("retrieved page for artist: "+artists[i]+
//						" ("+Stat.sum(arttermvec)+" term occs)");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//	write to file
		if (storedArtistVectorsHash.getParentFile().canWrite()) {
			try {
				new ObjectOutputStream(new FileOutputStream(storedArtistVectorsHash)).writeObject(artistsAndTermVectors);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("can't write serialized artist term vectors file.");
		}
		
		for (Enumeration e=threadlisteners.elements(); e.hasMoreElements(); ) {
			ThreadListener tl = (ThreadListener)(e.nextElement());
			tl.threadEnded();
		}
		
	}
	
	public Hashtable<String, int[]> getArtistsAndTermVectors() {
		return artistsAndTermVectors;
	}
	
	
	// search engine config stuff
	public int getNumberOfRetries() {
		return 3;
	}
	public int getIntervalBetweenRetries() {
		return 1000;
	}
	public int getNumberOfRequestedPages() {
		return 100;
	}
	public String getSearchengineURL() {
		return "http://www.google.com";
	}
}
