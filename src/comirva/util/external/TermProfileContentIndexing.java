/*
 * This class is an extension to the Term Profile Creator
 * in that it also indexes the multimedia content of the
 * web pages.
 */
package comirva.util.external;

import java.io.*;
import java.util.*;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import comirva.util.*;
import comirva.data.*;
import comirva.audio.XMLSerializable;
import comirva.io.filefilter.HTMLFileFilter;

public class TermProfileContentIndexing {
	private File rootDir, termFile;
	private Vector<String> terms, audioFE, imageFE, videoFE;		// lists for terms, audio/image/video file extensions

	/**
	 * @param rootDir	a File that points to the root directory
	 * @param termList	a File containing the term list that should be used for indexing
	 * @param audioFileExtensions
	 * @param imageFileExtensions
	 * @param videoFileExtensions
	 */
	public TermProfileContentIndexing(File rootDir, File termFile, File audioFileExtensions, File imageFileExtensions, File videoFileExtensions) {
		this.rootDir = rootDir;
		this.termFile = termFile;
		// read term file and convert to Vector<String>
		String content = TermProfileUtils.getFileContent(termFile);
		StringTokenizer st = new StringTokenizer(content, System.getProperty("line.separator"));
		this.terms = new Vector<String>();
		while (st.hasMoreElements())
			this.terms.add(st.nextToken());
		// extract the file extensions from the file and store them in Vector<String>
		// audio
		String fileExt = TermProfileUtils.getFileContent(audioFileExtensions);
		st = new StringTokenizer(fileExt, System.getProperty("line.separator"));
		this.audioFE = new Vector<String>();
		while (st.hasMoreElements())
			this.audioFE.add(st.nextToken());
		// image
		fileExt = TermProfileUtils.getFileContent(imageFileExtensions);
		st = new StringTokenizer(fileExt, System.getProperty("line.separator"));
		this.imageFE = new Vector<String>();
		while (st.hasMoreElements())
			this.imageFE.add(st.nextToken());
		// video
		fileExt = TermProfileUtils.getFileContent(videoFileExtensions);
		st = new StringTokenizer(fileExt, System.getProperty("line.separator"));
		this.videoFE = new Vector<String>();
		while (st.hasMoreElements())
			this.videoFE.add(st.nextToken());
	}


	/**
	 * From the root directory that contains subdirs (one for each entity/artist)
	 * and the list of terms, this methods generates EntityTermProfiles for every
	 * subdir (entity) and serializes the information as XML-files using
	 * the classes EntityTermProfile and SingleTermList.
	 */
	public void generateEntityTermProfiles() {
		if (rootDir.isDirectory()) {
			// every entity (direcArrayListtory)
			File[] dirs = rootDir.listFiles();
			for (int i=0; i<dirs.length; i++) {
				// only directories allowed
				if (dirs[i].isDirectory() && dirs[i].getName() != "." && dirs[i].getName() != "") {
					File entityDir = dirs[i];			// path to entity's html-pages
					System.out.println("processing "+entityDir.toString());
					// create entity term profile
					EntityTermProfile etp = new EntityTermProfile(entityDir);
					// set file extensions for indexing multimedia content
					etp.setExtAudio(this.audioFE);
					etp.setExtImage(this.imageFE);
					etp.setExtVideo(this.videoFE);
					// calculate co-occurrences and retrieve other information
					etp.calculateOccurrences(terms, new HTMLFileFilter());
					etp.setEntityName(entityDir.toString());		// set entity name to current artist name
//					etp.setCrawlDetails("music review");
					// serialize as XML-file
					String xmlFileName = entityDir.getPath() + ".xml";
					File xmlFile = new File(xmlFileName);
					// write XML-file
					OutputStreamWriter out;
					try {
						out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF8");
						XMLOutputFactory factory = XMLOutputFactory.newInstance();
						XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
						etp.writeXML(xmlWriter);
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		String basicPath = "/Research/Data/co-occurrences/C112a/cob_terms/"; //"E:/Research/Development/3DSunburst/_3DSunburst_pde/data/";
		TermProfileContentIndexing tp;
		tp = new TermProfileContentIndexing(
				new File(basicPath+"crawl_1000_MR/"),
				new File(basicPath+"terms.txt"),
				new File(basicPath+"fileext_audio.txt"),
				new File(basicPath+"fileext_image.txt"),
				new File(basicPath+"fileext_video.txt"));
		tp.generateEntityTermProfiles();
	}

}
