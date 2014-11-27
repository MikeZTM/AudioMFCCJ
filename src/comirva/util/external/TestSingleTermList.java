package comirva.util.external;

import comirva.util.external.*;
import comirva.util.*;
import comirva.data.*;
import comirva.io.filefilter.*;

import java.io.*;
import java.util.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import cp.net.Webpage;
import cp.util.HashtableTool;
import cp.util.Stopwords;

public class TestSingleTermList {

	// only for testing
	public static void main(String args[]) {
		
	
	// read artist list
	// create reader to access file
	Vector<String> artists2 = new Vector<String>();
	try {
		BufferedReader readerFile = new BufferedReader(new FileReader("/home/mms/Research/Data/co-occurrences/C224a/artists_224.txt")); //C:/Research/Data/co-occurrences/C224a/terms.txt")); // "/home/mms/Research/Data/co-occurrences/C224a/artists_224.txt"));
		String artist = readerFile.readLine();
		while (artist != null) {
			artists2.addElement(TextFormatTool.removeUnwantedChars(artist));
			artist = readerFile.readLine();
		}
	} catch (EOFException eofe) {
	} catch (IOException eofe) {
	}	

	
	for (int k=0;k<artists2.size(); k++) {
		// code for extracting a term list for a given artist (given a directory where ???.html files reside)
		String basePath = "/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MB/";
		String dirName = artists2.elementAt(k);
		File dir = new File(basePath+dirName);
		File[] files = dir.listFiles(new HTMLFileFilter());
		Hashtable tf = new Hashtable();
		for (int i=0; i<files.length; i++) {
			try {
				Webpage wp = new Webpage(files[i]);
				String content = wp.getPlainText();
				HashtableTool.updateWordsOccurrences(content, tf, null, wp.delimiterstring);
				System.out.println("processing "+ files[i].toString());
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		System.out.println(HashtableTool.hashtableToString(tf));
		// write term list to file
		File termListFile = new File(basePath+dirName+"/termlist.txt");
		try {
			FileWriter fw = new FileWriter(termListFile);
			BufferedWriter bw = new BufferedWriter(fw);
			Enumeration e = tf.keys();
			while (e.hasMoreElements()) {
				bw.write((String)e.nextElement()+"\n");
				bw.flush();
				fw.flush();
			}
			bw.close();
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
	}

		
		
	if (false) {		
			// deserialize
			File xmlFile2 = new File("/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MR/ironmaiden.xml"); 
			EntityTermProfile etp2 = new EntityTermProfile(xmlFile2);
			InputStreamReader in;
			try {
				in = new InputStreamReader(new FileInputStream(xmlFile2), "UTF8");
				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLStreamReader metaReader = factory.createXMLStreamReader(in);
				etp2.readXML(metaReader);
				String basePath = "/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MR/";
				String dirName = "metallica";
				File dir = new File(basePath+dirName);
				metaReader.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}


	}	
	
	
if (false) {		
		// read term list
		// create reader to access file
		Vector<String> terms = new Vector<String>();
		try {
			BufferedReader readerFile = new BufferedReader(new FileReader("/home/mms/Research/Data/co-occurrences/C224a/terms.txt")); //C:/Research/Data/co-occurrences/C224a/terms.txt")); // "/home/mms/Research/Data/co-occurrences/C224a/artists_224.txt"));
			String term = readerFile.readLine();
			while (term != null) {
				terms.addElement(term);
				term = readerFile.readLine();
			}
		} catch (EOFException eofe) {
		} catch (IOException eofe) {
		}
		// read artist list
		// create reader to access file
		Vector<String> artists = new Vector<String>();
		try {
			BufferedReader readerFile = new BufferedReader(new FileReader("/home/mms/Research/Data/co-occurrences/C224a/artists_224.txt")); //C:/Research/Data/co-occurrences/C224a/terms.txt")); // "/home/mms/Research/Data/co-occurrences/C224a/artists_224.txt"));
			String artist = readerFile.readLine();
			while (artist != null) {
				artists.addElement(artist);
				artist = readerFile.readLine();
			}
		} catch (EOFException eofe) {
		} catch (IOException eofe) {
		}	
		
		File rootDir = new File("/home/mms/tmp/mr_1000/");
		TermProfileUtils.generateEntityTermProfiles(rootDir, terms);
		
		String baseDir = new String("/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MB/");
		long time; // to calculate time
		// every artist
		for (int i=0; i<artists.size(); i++) {
			time = System.currentTimeMillis();
			String artist = (String)artists.elementAt(i);
			File dirArtist = new File(baseDir+TextFormatTool.removeUnwantedChars(artist));
			// create entity term profile
			EntityTermProfile etp = new EntityTermProfile(dirArtist);
			etp.calculateOccurrences(terms, new HTMLFileFilter());
			etp.setEntityName(artist);		// set entity name to current artist name
			etp.setCrawlDetails("music bio");
			// serialize as XML-file
			String  xmlFileName = dirArtist.getPath() + ".xml";
			File xmlFile = new File(xmlFileName);
			OutputStreamWriter out;
			try {	
				out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF8");
				XMLOutputFactory factory = XMLOutputFactory.newInstance();
				XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
				etp.writeXML(xmlWriter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(artist+", "+(System.currentTimeMillis()-time)/1000+" s");				
		}
}
			
		

			
	
		
		
		
//		SingleTermList stl = new SingleTermList(new File("/home/mms/Research/Data/co-occurrences/C224a/crawl_1000_MR/metallica/000.html"));
//		stl.calculateOccurrences(artists);
//
//		
//		// serialize as XML-file
//		File xmlFile = new File("/home/mms/tmp/metallica.xml");
//		OutputStreamWriter out;
//		try {
//			out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF8");
//			XMLOutputFactory factory = XMLOutputFactory.newInstance();
//			XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
//			stl.writeXML(xmlWriter);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (XMLStreamException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		// deserialize
//		SingleTermList stl2 = new SingleTermList(new File("/home/mms/tmp/metallica.xml"));
//		InputStreamReader in;
//		try {
//			in = new InputStreamReader(new FileInputStream(xmlFile), "UTF8");
//			XMLInputFactory factory = XMLInputFactory.newInstance();
//			XMLStreamReader metaReader = factory.createXMLStreamReader(in);
//			stl2.readXML(metaReader);
//			metaReader.close();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (XMLStreamException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
//		stl2.printTFs();
		
	}


	
}
