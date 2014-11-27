/*
 * Created on 30.11.2005
 */
package comirva.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import comirva.audio.XMLSerializable;
import comirva.io.ETPXMLExtractorThread;
import comirva.io.filefilter.XMLFileFilter;
import comirva.util.TermProfileUtils;
import comirva.util.VectorSort;


/** 
 * This class implements a term profile for entities 
 * like artist names. It is intended to be used for 
 * text mining purposes.
 * In particular, its design reflects the main usage for HTML-files. 
 */ 
public class EntityTermProfile implements XMLSerializable, Serializable {
	// general variables
	Vector<SingleTermList> singleTermLists;				// contains the term lists obtained by all documents of the entity
	Vector<String> terms;								// all terms that define the entity (extracted terms)
	Hashtable<String, Long> termFrequency;				// the frequency of the terms occurring in the entity (how often does every term occur in all documents of the entity) 
	Vector<Vector<Integer>> termOccurrenceOnDocuments;	// a Vector that stores, for all terms, another Vector with Integers that represent indices to the documents that contain the respective term   
	Hashtable<String, Integer> documentFrequency;		// stores, for every term, the number of documents of the entity on which the specific term occurs (document frequency) 	
	Hashtable<String, Double> TFxIDF;					// term frequency * inverse document frequency			
	Hashtable<String, Double> IDF;						// global inverse document frequency			
	int[][] tfDocs;										// term frequency array (matrix to store the TF value for every (term, document)-combination)
	File dirLocal;										// local directory where all documents of the entity are located
	String entityName;									// the name of the entity, e.g. artist name
	Integer numberDocuments;							// the number of documents that form the entity
	// web-related variables
	String crawlDetails;								// details of the settings used for the web crawl, e.g. MR, MGS
	// variables to perform indexing
	Vector<String> extAudio, extImage, extVideo;		// file extensions to index for audio/image/video files

	private boolean isDebugMode = false;				// debug mode flag					

	/**
	 * Creates a new EntityTermProfile-instance.
	 * 
	 * @param dirLocal		the directory where all documents belonging to the entity are stored
	 */
	public EntityTermProfile(File dirLocal) {
		this.dirLocal = dirLocal;
	}

	/**
	 * Creates a new EntityTermProfile-instance.
	 */
	public EntityTermProfile() {
		super();
	}

	/**
	 * Calculates the occurrences of the terms given as Vector<String>
	 * in the argument in all text documents of the entity and stores their 
	 * frequency and information on their occurrences in the entity's documents.
	 * 
	 * @param termList 				a Vector<String> containing a term list
	 * @param documentFileFilter	a FileFilter for the documents that should be searched for the terms in the term list
	 */
	public void calculateOccurrences(Vector<String> termList, FileFilter documentFileFilter) {
		// make sure that File-instance dirLocal is a directory
		if (this.dirLocal.isDirectory()) {
			// init Vector of single term lists
			this.singleTermLists = new Vector<SingleTermList>(); 

			// search for urls.dat which should contain the original URLs
			File urlsFile = new File(this.dirLocal, "urls.dat");
			Vector<String> urls = null;
			if (urlsFile.exists()) {
				urls = new Vector<String>();
				String urlFileContent = TermProfileUtils.getFileContent(urlsFile);
				StringTokenizer st = new StringTokenizer(urlFileContent, System.getProperty("line.separator"));
				while (st.hasMoreElements())
					urls.add(st.nextToken());
			}		
			// process every document of the entity
			if (isDebugMode)
				System.out.println("extracting single term lists");
			File[] entityFiles = dirLocal.listFiles(documentFileFilter);
			this.numberDocuments = new Integer(entityFiles.length);			// store number of documents that form the entity
			for (int j=0; j<entityFiles.length; j++) {
				if (isDebugMode)
					System.out.println("processing "+entityFiles[j]);
				// create a term list for every document 
				SingleTermList stl = new SingleTermList(entityFiles[j]);
				// set file extensions to perform indexing
				stl.setExtAudio(this.extAudio);
				stl.setExtImage(this.extImage);
				stl.setExtVideo(this.extVideo);
				// add orignal URL of current document to STP
				if (urls != null && urls.size() == entityFiles.length)
					stl.setUrlSource(urls.elementAt(j));
				// calculate occurrences
				stl.calculateOccurrences(termList);
				// serialize as XML-file
				String xmlFileName = entityFiles[j].getPath() + ".xml";
				File xmlFile = new File(xmlFileName);
				OutputStreamWriter out;
				try {
					out = new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF8");
					XMLOutputFactory factory = XMLOutputFactory.newInstance();
					XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(out);
					stl.writeXML(xmlWriter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// add to single term lists
				this.singleTermLists.add(stl);				
			}

			// construct a complete term list from all single term lists 
			if (isDebugMode) 
				System.out.println("constructing entity term list");
			this.terms = new Vector<String>();
			for (int i=0; i<this.singleTermLists.size(); i++){
				SingleTermList stl = this.singleTermLists.elementAt(i);
				Enumeration<String> enumTerms = stl.getFrequency().keys();
				while (enumTerms.hasMoreElements()) {
					String term = enumTerms.nextElement();
					// add term if not already contained in term list
					if (!this.terms.contains(term))
						this.terms.addElement(term);
				}
			}

			// init structure for mapping term -> indices of documents containing the term
			this.termOccurrenceOnDocuments = new Vector<Vector<Integer>>();
			for (int i=0; i<this.terms.size(); i++)
				this.termOccurrenceOnDocuments.addElement(new Vector<Integer>());

			// init Hashtable of term frequencies
			this.termFrequency = new Hashtable<String, Long>();
			for (int i=0; i<this.terms.size(); i++)
				this.termFrequency.put(this.terms.elementAt(i), new Long(0));

			// calculate term frequency and term occurrence over all documents of the entity
			if (isDebugMode) 
				System.out.println("calculating entity's term frequencies");
			this.tfDocs = new int[termList.size()][entityFiles.length];		// init TF matrix
			for (int i=0; i<this.singleTermLists.size(); i++){
				SingleTermList stl = this.singleTermLists.elementAt(i);
				// for all terms relevant to the entity
				for (int j=0; j<this.terms.size(); j++) {
					String term = this.terms.elementAt(j);
					// check for occurrence of current term in current document (single term list) 
					Hashtable<String, Integer> ht = stl.getFrequency();
					if (ht.containsKey(term)) {			// current term contained in current single term list?
						Integer freq = ht.get(term);	// get term frequency in current single term list
						this.tfDocs[j][i] = freq.intValue();	// store TF value in TF (terms,docs)-matrix
						if (freq.intValue() != 0) {		// if frequency > 0 -> add to term frequency of entity
							// term frequency
							// term frequency for at least one document already stored? 
							if (this.termFrequency.containsKey(term))		// yes -> add frequency
								this.termFrequency.put(term, new Long(this.termFrequency.get(term).intValue()+freq.intValue()));
							else										// no -> add term as key and frequency as value
								this.termFrequency.put(term, new Long(freq.intValue()));
							// occurrence on documents
							Vector<Integer> curTerm = this.termOccurrenceOnDocuments.elementAt(j);
							curTerm.addElement(new Integer(i));		
						}
					}
				}
			}

			// generate hashtable that stores, for every term of the entity, the number of documents that contain this term    
			if (isDebugMode) 
				System.out.println("calculating entity's document frequencies");
			this.documentFrequency = new Hashtable<String, Integer>();
			for (int i=0; i<this.termOccurrenceOnDocuments.size(); i++)
				this.documentFrequency.put(this.terms.elementAt(i), new Integer(this.termOccurrenceOnDocuments.elementAt(i).size())); 

			// calculate tf*idf
			if (isDebugMode) 
				System.out.println("calculating entity's TFxIDFs");
			this.TFxIDF = new Hashtable<String, Double>();
			for (int i=0; i<this.terms.size(); i++) {
				String term = this.terms.elementAt(i);
				long tf = this.termFrequency.get(term).longValue();
				long df = this.documentFrequency.get(term).longValue();
				Double tfidf;
				if (df != 0)		// prevent div by zero errors
					tfidf= new Double((double)tf*Math.log(((double)this.numberDocuments/(double)df)));
				else
					tfidf = new Double(0);
				this.TFxIDF.put(term, tfidf);
			}

			// output tfidf-values
			Vector keys = new Vector();
			Vector values = new Vector();
			Enumeration<String> e = TFxIDF.keys();
			while (e.hasMoreElements()) {
				String term = e.nextElement();
				Double tfidf = TFxIDF.get(term);
				keys.addElement(term);
				values.addElement(tfidf);
			}

			VectorSort.sortWithMetaData(values, keys);

			if (isDebugMode)
				for (int i=0; i<keys.size(); i++)
					System.out.println(keys.elementAt(i)+"\t"+values.elementAt(i));
		}	
	}

	/**
	 * Serializes an EntityTermProfile-instance as XML-file. 
	 * 
	 * @param writer		a XMLStreamWriter that points to the XML-file.
	 *
	 * @see comirva.audio.XMLSerializable#writeXML(javax.xml.stream.XMLStreamWriter)
	 */
	public void writeXML(XMLStreamWriter writer) {
		try {
			writer.writeStartDocument();

			writer.writeStartElement("EntityTermProfile");

			if (this.getEntityName() != null) {
				writer.writeStartElement("EntityName");
				writer.writeCharacters(this.getEntityName().toString());
				writer.writeEndElement();
			}

			if (this.getDirLocal() != null) {
				writer.writeStartElement("LocalDirectory");
				writer.writeCharacters(this.getDirLocal().toString());
				writer.writeEndElement();
			}

			if (this.getNumberDocuments() != null) {
				writer.writeStartElement("NumberDocuments");
				writer.writeCharacters(this.getNumberDocuments().toString());
				writer.writeEndElement();
			}

			if (this.getCrawlDetails() != null) {
				writer.writeStartElement("CrawlDetails");
				writer.writeCharacters(this.getCrawlDetails().toString());
				writer.writeEndElement();
			}

			if (this.getTermFrequency() != null) {
				Enumeration<String> e = getTermFrequency().keys();
				while (e.hasMoreElements()) {
					// look for term
					String term = e.nextElement();
					if (term != null) {			
						// look for frequency of term
						Long freq = getTermFrequency().get(term);
						if (freq != null) {		
							writer.writeStartElement("TF");
							writer.writeAttribute("term", term);
							writer.writeCharacters(freq.toString());
							writer.writeEndElement();
						}
					}
				}
			}

			if (this.getDocumentFrequency() != null) {
				Enumeration<String> e = getDocumentFrequency().keys();
				while (e.hasMoreElements()) {
					// look for term
					String term = e.nextElement();
					if (term != null) {		
						// look for occurrence of term
						Integer occ = getDocumentFrequency().get(term);
						if (occ != null) {		
							writer.writeStartElement("DF");			// document frequency
							writer.writeAttribute("term", term);
							writer.writeCharacters(occ.toString());
							writer.writeEndElement();
						}
					}
				}
			}

			if (this.getTermOccurrenceOnDocuments() != null) {
				Vector<Vector<Integer>> tood = getTermOccurrenceOnDocuments();
				for (int i=0; i<tood.size(); i++) {
					// look for documents that contain term
					Vector<Integer> occDocs = tood.elementAt(i);
					if (occDocs != null) {
						writer.writeStartElement("TermOccurrenceOnDocuments");
						// write term as attribute if possible
						if (this.terms != null)
							writer.writeAttribute("term", this.terms.elementAt(i));
						// process all terms
						Enumeration<Integer> eO = occDocs.elements();
						while (eO.hasMoreElements()) {
							Integer idxDoc = eO.nextElement();
							writer.writeStartElement("DocumentIndex");
							writer.writeAttribute("TF", Integer.toString(this.tfDocs[i][idxDoc.intValue()]));
							writer.writeCharacters(idxDoc.toString());
							writer.writeEndElement();
						}	
						writer.writeEndElement();
					}

				}
			}

			if (this.getTFxIDF() != null) {
				Enumeration<String> e = getTFxIDF().keys();
				while (e.hasMoreElements()) {
					// look for term
					String term = e.nextElement();
					if (term != null) {		
						// look for tfidf of term
						Double tfidf = getTFxIDF().get(term);
						if (tfidf != null) {		
							writer.writeStartElement("TFxIDF");			// tfidf
							writer.writeAttribute("term", term);
							writer.writeCharacters(tfidf.toString());
							writer.writeEndElement();
						}
					}
				}
			}

			if (this.singleTermLists != null) {
				Enumeration<SingleTermList> e = this.singleTermLists.elements();
				while (e.hasMoreElements()) {
					// look for term
					String fileName = e.nextElement().getFileLocal()+".xml";
					if (fileName != null) {		
						writer.writeStartElement("SingleTermListFile");
						writer.writeCharacters(fileName);
						writer.writeEndElement();
					}
				}
			}

			// write information on original URL & indexed multimedia content (if available)
			if (this.singleTermLists != null) {
				Enumeration<SingleTermList> e = this.singleTermLists.elements();
				int docNo = 0;
				while (e.hasMoreElements()) {
					SingleTermList stl = e.nextElement();
					// look for url source
					String urlSource = stl.getUrlSource();
					if (urlSource != null) {		
						writer.writeStartElement("DocumentInfo");
						writer.writeAttribute("no", Integer.toString(docNo));
						writer.writeAttribute("url", urlSource);
						// write multimedia content
						Vector<String> audioUrl = stl.getAudioContent();
						if (audioUrl != null) {
							for (int i=0; i<audioUrl.size(); i++) {
								writer.writeStartElement("Content");
								writer.writeAttribute("type", "audio");
								writer.writeAttribute("url", audioUrl.elementAt(i));
								writer.writeEndElement();
							}
						}
						Vector<String> imageUrl = stl.getImageContent();
						if (imageUrl != null) {
							for (int i=0; i<imageUrl.size(); i++) {
								writer.writeStartElement("Content");
								writer.writeAttribute("type", "image");
								writer.writeAttribute("url", imageUrl.elementAt(i));
								writer.writeEndElement();
							}
						}
						Vector<String> videoUrl = stl.getVideoContent();
						if (videoUrl != null) {
							for (int i=0; i<videoUrl.size(); i++) {
								writer.writeStartElement("Content");
								writer.writeAttribute("type", "video");
								writer.writeAttribute("url", videoUrl.elementAt(i));
								writer.writeEndElement();
							}
						}
						writer.writeEndElement();
					}
					docNo++;
				}
			}

			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserializes an EntityTermProfile-instance from an XML-file.
	 * 
	 * @param reader		a XMLStreamReader that points to the XML-file.
	 * @see comirva.audio.XMLSerializable#readXML(javax.xml.stream.XMLStreamReader)
	 */
	public void readXML(XMLStreamReader reader) {
		// reset all variables
		this.singleTermLists = null;
		this.terms = null;
		this.termFrequency = null; 
		this.termOccurrenceOnDocuments = null;   
		this.documentFrequency = null; 	
		this.TFxIDF = null;								
		this.dirLocal = null;
		this.entityName = null;
		this.numberDocuments = null;
		this.crawlDetails = null;
		this.singleTermLists = new Vector<SingleTermList>();
		this.terms = new Vector<String>();
		this.termFrequency = new Hashtable<String, Long>(); 
		this.termOccurrenceOnDocuments = new Vector<Vector<Integer>>();   
		this.documentFrequency = new Hashtable<String, Integer>(); 	
		this.TFxIDF = new Hashtable<String, Double>();			

		// start deserialization
		try {
			reader.require(XMLStreamReader.START_DOCUMENT, null, null);
			reader.next();
			reader.require(XMLStreamReader.START_ELEMENT, null, "EntityTermProfile");
			reader.nextTag();
			// read XML-tags
			while(reader.isStartElement()) {
				String elem = reader.getLocalName();
				if(elem.equals("EntityName")) {
					this.entityName = new String(reader.getElementText());
					reader.require(XMLStreamReader.END_ELEMENT, null, "EntityName");
				} else if(elem.equals("LocalDirectory")) {
					this.dirLocal = new File(reader.getElementText());
					reader.require(XMLStreamReader.END_ELEMENT, null, "LocalDirectory");
				} else if(elem.equals("NumberDocuments")) {
					this.numberDocuments = new Integer(reader.getElementText());
					reader.require(XMLStreamReader.END_ELEMENT, null, "NumberDocuments");
				} else if(elem.equals("CrawlDetails")) {
					this.crawlDetails = reader.getElementText();
					reader.require(XMLStreamReader.END_ELEMENT, null, "CrawlDetails");
				} else if(elem.equals("TF")) {
					String term = reader.getAttributeValue(0).toString();
					Long freq = new Long(reader.getElementText());
					this.termFrequency.put(term, freq);
					reader.require(XMLStreamReader.END_ELEMENT, null, "TF");
				} else if(elem.equals("DF")) {
					String term = reader.getAttributeValue(0).toString();
					Integer df = new Integer(reader.getElementText());
					this.documentFrequency.put(term, df);
					reader.require(XMLStreamReader.END_ELEMENT, null, "DF");
				} else if(elem.equals("TFxIDF")) {
					String term = reader.getAttributeValue(0).toString();
					Double tfidf = new Double(reader.getElementText());
					this.TFxIDF.put(term, tfidf);
					reader.require(XMLStreamReader.END_ELEMENT, null, "TFxIDF");
				} else if(elem.equals("TermOccurrenceOnDocuments")) {
					String term = reader.getAttributeValue(0).toString();
					// add term to term list
					this.terms.addElement(term);
					// Vector for term occurrences
					Vector<Integer> termOccs = new Vector<Integer>();
					// get document occurrences
					// document frequency of current term
					int df = this.documentFrequency.get(term).intValue();
					// read df <DocumentIndex>-tags
					// note that <DF>-tags must already be read !!!
					for (int i=0; i<df; i++) {
						reader.nextTag();
						String elem2 = reader.getLocalName();
						if(elem2.equals("DocumentIndex"))
							termOccs.addElement(new Integer(reader.getElementText()));
						else
							throw new XMLStreamException("number of <DocumentIndex>-tags does not equal the required one according to document frequency of term "+term);	
					}
					reader.nextTag();
					// add term occurrence Vector 
					this.termOccurrenceOnDocuments.addElement(termOccs);
					reader.require(XMLStreamReader.END_ELEMENT, null, "TermOccurrenceOnDocuments");
				} else if(elem.equals("SingleTermListFile")) {
					String xmlFileName = reader.getElementText();
					File xmlFile = new File(xmlFileName);
					// create new single term list and deserialze the appropriate SingeTermList-instance
					SingleTermList stl = new SingleTermList();
					InputStreamReader in;
					try {
						in = new InputStreamReader(new FileInputStream(xmlFile), "UTF8");
						XMLInputFactory factory = XMLInputFactory.newInstance();
						XMLStreamReader metaReader = factory.createXMLStreamReader(in);
						stl.readXML(metaReader);
						metaReader.close();
						in.close();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (XMLStreamException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					singleTermLists.add(stl);
					reader.require(XMLStreamReader.END_ELEMENT, null, "SingleTermListFile");
				} else if(elem.equals("DocumentInfo") || elem.equals("Content")) {
					reader.nextTag();			// ignore document info5
				} else {
					throw new XMLStreamException("found unknown tag");
				}
				reader.nextTag();
			}
			reader.require(XMLStreamReader.END_ELEMENT, null, "EntityTermProfile");
			reader.next();
			reader.require(XMLStreamReader.END_DOCUMENT, null, null);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TermsWeights getMostImportantTerms(int maxNoTerms, Hashtable termWeightings) {
		ArrayList terms = new ArrayList();                   // to store terms and weights temporarily (for sorting)
		ArrayList weights = new ArrayList();
		String term;            // the term for the term weighting
		ArrayList alIndices;    // pointers to documents where key occur
		Enumeration e;			// temporary enumeration
		e = termWeightings.keys();            // all elements in hashtable
		while (e.hasMoreElements()) {
			term = (String)e.nextElement();
			Float weight;
//			if (termWeightings.get(term) instanceof Long)	// weighting function is TF
			weight = Float.valueOf(termWeightings.get(term).toString());
			terms.add(term);
			weights.add(weight);
		}
		// retain only those maxNoTerms entries with highest weights
		// the ArrayLists keys and weights contain the data
		TermsWeights tw = new TermsWeights(new ArrayList(), new ArrayList());  // to store terms with their weighting
		for (int j=0; j<maxNoTerms && weights.size() > 0; j++) {
			// find largest element
			float maxWeight = 0;
			for (int i=0; i<weights.size(); i++)
				maxWeight = Math.max(maxWeight, ((Float)weights.get(i)).floatValue());
			int idxMaxWeight = weights.indexOf(new Float(maxWeight));
			// copy from keys, values to hl and delete from keys, values
			term = (String)terms.get(idxMaxWeight);
			terms.remove(idxMaxWeight);
			weights.remove(idxMaxWeight);
			tw.add(term, new Float(maxWeight));      
			//      println(term + ": " + maxWeight);
		}
		return tw;   // return terms with weightings   
	}

	/**
	 * @return Returns the crawlDetails.
	 */
	public String getCrawlDetails() {
		return crawlDetails;
	}

	/**
	 * @param crawlDetails The crawlDetails to set.
	 */
	public void setCrawlDetails(String crawlDetails) {
		this.crawlDetails = crawlDetails;
	}

	/**
	 * @return Returns the dirLocal.
	 */
	public File getDirLocal() {
		return dirLocal;
	}

	/**
	 * @param dirLocal The dirLocal to set.
	 */
	public void setDirLocal(File dirLocal) {
		this.dirLocal = dirLocal;
	}

	/**
	 * @return Returns the entityName.
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName The entityName to set.
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return Returns the termFrequency.
	 */
	public Hashtable<String, Long> getTermFrequency() {
		return termFrequency;
	}

	/**
	 * @return Returns the documentFrequency.
	 */
	public Hashtable<String, Integer> getDocumentFrequency() {
		return documentFrequency;
	}

	/**
	 * @return Returns the termOccurrenceOnDocuments.
	 */
	public Vector<Vector<Integer>> getTermOccurrenceOnDocuments() {
		return termOccurrenceOnDocuments;
	}

	/**
	 * @return Returns the terms.
	 */
	public Vector<String> getTerms() {
		return terms;
	}

	/**
	 * @return Returns the numberDocuments.
	 */
	public Integer getNumberDocuments() {
		return numberDocuments;
	}

	/**
	 * @return Returns the tFxIDF.
	 */
	public Hashtable<String, Double> getTFxIDF() {
		return TFxIDF;
	}

	/**
	 * @return Returns the SingleTermLists.
	 */
	public Vector<SingleTermList> getSingleTermLists() {
		return singleTermLists;
	}	

	/**
	 * @param extAudio		a Vector<String> containing possible file extensions for audio files
	 */
	public void setExtAudio(Vector<String> extAudio) {
		this.extAudio = extAudio;
	}

	/**
	 * @param extImage		a Vector<String> containing possible file extensions for image files
	 */
	public void setExtImage(Vector<String> extImage) {
		this.extImage = extImage;
	}

	/**
	 * @param extVideo		a Vector<String> containing possible file extensions for video files
	 */
	public void setExtVideo(Vector<String> extVideo) {
		this.extVideo = extVideo;
	}
}