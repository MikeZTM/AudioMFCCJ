/*
 * Created on 28.11.2005
 */
package comirva.data;

import comirva.audio.XMLSerializable;

import java.util.*;
import java.io.*;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * This class implements a single term list of a text document.
 * It is intended to be used for text mining purposes.
 * In particular, its design reflects the main usage for HTML-files.
 *
 * @author Markus Schedl
 */
public class SingleTermList implements XMLSerializable, Serializable {
	// general variables
	Hashtable<String, Integer> frequency;	// the frequency (on line level) of the terms occurring in the text document
	File fileLocal;							// local file to text document (e.g. to crawled web page)
	// web-related variables
	String urlSource;						// original URL of the HTML-file
	String searchTerm;						// main term used for search (e.g. artist name in MIR applications)
	String crawlDetails;					// details of the settings used for the web crawl, e.g. MR, MGS
	Vector<String> audioContent = new Vector<String>();			// list containing the audio content of the HTML-file 
	Vector<String> imageContent = new Vector<String>();			// list containing the image content of the HTML-file
	Vector<String> videoContent = new Vector<String>();			// list containing the video content of the HTML-file
	
	Vector<String> extAudio, extImage, extVideo;	// file extensions to index for audio/image/video files
	
	private boolean isDebugMode = false;	// debug mode flag

	/**
	 * Creates a new SingleTermList-instance.
	 *
	 * @param fileLocal		the file for which the term list should be created/loaded
	 */
	public SingleTermList(File fileLocal) {
		this.fileLocal = fileLocal;
	}

	/**
	 * Creates a new SingleTermList-instance.
	 */
	public SingleTermList() {
		super();
	}

	/**
	 * Calculates the occurrences of the terms given as Vector<String>
	 * in the argument in the text document and stores their frequency.
	 *
	 * @param termList a Vector<String> containing a term list
	 */
	public void calculateOccurrences(Vector<String> termList) {
		// only proceed, if file was set and exists
		if (fileLocal.exists()) {
			// open text file
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(fileLocal));
				String line = br.readLine();
				// initialize hashtable
				frequency = new Hashtable<String, Integer>();
				for (int i=0; i<termList.size(); i++)
					frequency.put(termList.elementAt(i), 0);
				Vector<String> links = new Vector<String>();			// to store potential links to multimedia content
				// analyze every line
				while (line != null) {
					// if file extensions for multimedia content were given, analyze each line for links to MM content
					if (this.extAudio != null || this.extImage != null || this.extVideo != null) {
						// extract links in current line
//						links.addAll(extractLinks(line, "a", "href"));
//						links.addAll(extractLinks(line, "img", "src"));
						// search for links in the following attributes
						links.addAll(extractLinks(line, "href"));
						links.addAll(extractLinks(line, "src"));
						links.addAll(extractLinks(line, "value"));
						links.addAll(extractLinks(line, "data"));
						links.addAll(extractLinks(line, "link"));
					}
					// insert all terms in a temp Vector
					Vector<String> lineTerms = new Vector<String>();
					StringTokenizer st = new StringTokenizer(line, " \t\r\n|/,;.:?!~#%$§=_+*()[]{}&");
					while (st.hasMoreElements())
						lineTerms.addElement(st.nextToken().toLowerCase());
					// extract occurrences of all artists j on web pages of artist i
					for (int i=0; i<termList.size(); i++) {
						String term = termList.elementAt(i);		// extract term
						if (line.toLowerCase().indexOf(" "+term.toLowerCase()+" ") != -1) {		// term j occurs on current line
							// space is required before and after the word in order to exclude parts of words
// stringtokenizer						if (!cp.util.Stopwords.isStopword(term.toLowerCase()) && lineTerms.contains(term.toLowerCase())) {		// term j occurs on current line
							// modify hashtable with frequencies
							if (frequency.containsKey(term)) {		// term already in hashtable?
								// get frequency
								Integer frq = frequency.get(term);
								if (frq == null)		// if null, insert 1 as frequency
									frequency.put(term, new Integer(1));
								else					// value for the term not null, add 1 to value
									frequency.put(term, frq+1);
							} else {		// term not in hashtable
								frequency.put(term, new Integer(1));
							}
						}
					}
					line = br.readLine();
				} // end while
				// search for double entries in found links and eliminate them
				Vector<String> linksCleaned = new Vector<String>();
				if (links != null) {
					Enumeration<String> e = links.elements();
					while (e.hasMoreElements()) {
						String link = e.nextElement();
						if (!linksCleaned.contains(link))			// only add link if its not already added
							linksCleaned.addElement(link);
					}
				}
				links = linksCleaned;
				// search links for multimedia content
				if (this.extAudio != null || this.extImage != null || this.extVideo != null) {
					if (links != null && !links.isEmpty()) {
						// check for valid file extension
						for (int i=0; i<links.size(); i++) {		// all found links
							// audio file links
							boolean isLink = false;
							if (this.extAudio != null) {
								for (int j=0; j<this.extAudio.size(); j++) {		// all audio file extensions
									if (links.elementAt(i).toLowerCase().endsWith("."+this.extAudio.elementAt(j).toLowerCase()))
										isLink = true;
								}
								if (isLink) {
									this.audioContent.addElement(links.elementAt(i));
									if (isDebugMode)
										System.out.println(links.elementAt(i));
								}
							}
							// image file links
							isLink = false;
							if (this.extImage != null) {
								for (int j=0; j<this.extImage.size(); j++) {		// all image file extensions
									if (links.elementAt(i).toLowerCase().endsWith("."+this.extImage.elementAt(j).toLowerCase()))
										isLink = true;
								}
								if (isLink) {
									this.imageContent.addElement(links.elementAt(i));
									if (isDebugMode)
										System.out.println(links.elementAt(i));
								}
							}
							// video file links
							isLink = false;
							if (this.extVideo != null) {
								for (int j=0; j<this.extVideo.size(); j++) {		// all video file extensions
									if (links.elementAt(i).toLowerCase().endsWith("."+this.extVideo.elementAt(j).toLowerCase()))
										isLink = true;
								}
								if (isLink) {
									this.videoContent.addElement(links.elementAt(i));
									if (isDebugMode)
										System.out.println(links.elementAt(i));
								}
							}
						}
					}
				}
				br.close();
				if (isDebugMode) {
					for (int i=0; i<termList.size(); i++)
						System.out.println("term "+termList.elementAt(i)+" frequency: "+frequency.get(termList.elementAt(i)));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Serializes a SingleTermList-instance as XML-file.
	 *
	 * @param writer		a XMLStreamWriter that points to the XML-file.
	 *
	 * @see comirva.audio.XMLSerializable#writeXML(javax.xml.stream.XMLStreamWriter)
	 */
	public void writeXML(XMLStreamWriter writer) {
		try {
			writer.writeStartDocument();

			writer.writeStartElement("SingleTermList");

			if (this.getFileLocal() != null) {
				 writer.writeStartElement("LocalFile");
		         writer.writeCharacters(this.getFileLocal().toString());
		         writer.writeEndElement();
			}

			if (this.getUrlSource() != null) {
				 writer.writeStartElement("URLSource");
		         writer.writeCharacters(this.getUrlSource().toString());
		         writer.writeEndElement();
			}

			if (this.getSearchTerm() != null) {
				 writer.writeStartElement("SearchTerm");
		         writer.writeCharacters(this.getSearchTerm().toString());
		         writer.writeEndElement();
			}

			if (this.getCrawlDetails() != null) {
				 writer.writeStartElement("CrawlDetails");
		         writer.writeCharacters(this.getCrawlDetails().toString());
		         writer.writeEndElement();
			}

			if (this.getFrequency() != null) {
				Enumeration<String> e = getFrequency().keys();
				while (e.hasMoreElements()) {
					// look for term
					String term = e.nextElement();
					if (term != null) {
						// look for frequency of term
						Integer freq = getFrequency().get(term);
						if (freq != null) {
							writer.writeStartElement("TF");
							writer.writeAttribute("term", term);
							writer.writeCharacters(freq.toString());
							writer.writeEndElement();
						}
					}
				}
			}
			
			// indexed multimedia content
			if (this.getAudioContent() != null) {
				Enumeration<String> e = getAudioContent().elements();
				while (e.hasMoreElements()) {
					// look for url
					String type = "audio";
					String url = e.nextElement();
					if (url != null) {
						writer.writeStartElement("Content");
						writer.writeAttribute("type", type);
						writer.writeAttribute("url", url);
						writer.writeEndElement();
					}
				}
			}
			if (this.getImageContent() != null) {
				Enumeration<String> e = getImageContent().elements();
				while (e.hasMoreElements()) {
					// look for url
					String type = "image";
					String url = e.nextElement();
					if (url != null) {
						writer.writeStartElement("Content");
						writer.writeAttribute("type", type);
						writer.writeAttribute("url", url);
						writer.writeEndElement();
					}
				}
			}
			if (this.getVideoContent() != null) {
				Enumeration<String> e = getVideoContent().elements();
				while (e.hasMoreElements()) {
					// look for url
					String type = "video";
					String url = e.nextElement();
					if (url != null) {
						writer.writeStartElement("Content");
						writer.writeAttribute("type", type);
						writer.writeAttribute("url", url);
						writer.writeEndElement();
					}
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
	 * Deserializes a SingleTermList-instance from an XML-file.
	 *
	 * @param reader		a XMLStreamReader that points to the XML-file.
	 * @see comirva.audio.XMLSerializable#readXML(javax.xml.stream.XMLStreamReader)
	 */
	public void readXML(XMLStreamReader reader) {
		// reset all variables
		this.fileLocal = null;
		this.urlSource = null;
		this.searchTerm = null;
		this.crawlDetails = null;
		this.audioContent = new Vector<String>();
		this.imageContent = new Vector<String>();
		this.videoContent = new Vector<String>();
		this.frequency = new Hashtable<String, Integer>();
		// start deserialization
		try {
			reader.require(XMLStreamReader.START_DOCUMENT, null, null);
		    reader.next();
		    reader.require(XMLStreamReader.START_ELEMENT, null, "SingleTermList");
			reader.nextTag();
			// read XML-tags
			while(reader.isStartElement()) {
				String elem = reader.getLocalName();
		        if(elem.equals("LocalFile")) {
		          fileLocal = new File(reader.getElementText());
		          reader.require(XMLStreamReader.END_ELEMENT, null, "LocalFile");
		        } else if(elem.equals("URLSource")) {
		          urlSource = reader.getElementText();
		          reader.require(XMLStreamReader.END_ELEMENT, null, "URLSource");
		        } else if(elem.equals("SearchTerm")) {
		          searchTerm = reader.getElementText();
		          reader.require(XMLStreamReader.END_ELEMENT, null, "SearchTerm");
		        } else if(elem.equals("CrawlDetails")) {
		          crawlDetails = reader.getElementText();
		          reader.require(XMLStreamReader.END_ELEMENT, null, "CrawlDetails");
		        } else if(elem.equals("TF")) {
	        		String term = reader.getAttributeValue(0).toString();
	        		Integer freq = new Integer(reader.getElementText());
	        		this.frequency.put(term, freq);
	        		reader.require(XMLStreamReader.END_ELEMENT, null, "TF");
		        } else if(elem.equals("Content")) {
		        	String type = reader.getAttributeValue(0).toString();
		        	String url = reader.getAttributeValue(1).toString();
		        	if (type.equals("audio"))
		        		this.audioContent.addElement(url);
		        	if (type.equals("image"))
		        		this.imageContent.addElement(url);
		        	if (type.equals("video"))
		        		this.videoContent.addElement(url);
		        	reader.nextTag();
//		        	reader.require(XMLStreamReader.END_ELEMENT, null, "Content");
		        } else {
		        	throw new XMLStreamException("found unknown tag");
		        }
		        reader.nextTag();
			}
		    reader.require(XMLStreamReader.END_ELEMENT, null, "SingleTermList");
		    reader.next();
		    reader.require(XMLStreamReader.END_DOCUMENT, null, null);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints a list of the term frequencies.
	 */
	public void printTFs() {
		Enumeration<String> e = this.frequency.keys();
		while (e.hasMoreElements()) {
			String term = e.nextElement();
			System.out.println("term: " + term + ", tf: " + this.frequency.get(term));
		}
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
	 * @return Returns the fileLocal.
	 */
	public File getFileLocal() {
		return fileLocal;
	}

	/**
	 * @param fileLocal The fileLocal to set.
	 */
	public void setFileLocal(File fileLocal) {
		this.fileLocal = fileLocal;
	}

	/**
	 * @return Returns the searchTerm.
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	/**
	 * @param searchTerm The searchTerm to set.
	 */
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	/**
	 * @return Returns the urlSource.
	 */
	public String getUrlSource() {
		return urlSource;
	}

	/**
	 * @param urlSource The urlSource to set.
	 */
	public void setUrlSource(String urlSource) {
		this.urlSource = urlSource;
	}

	/**
	 * @return Returns the frequency.
	 */
	public Hashtable<String, Integer> getFrequency() {
		return frequency;
	}

	/**
	 * @return	Returns the URLs to the indexed audio content as Vector<String>
	 */
	public Vector<String> getAudioContent() {
		return this.audioContent;
	}
	/**
	 * @return	Returns the URLs to the indexed image content as Vector<String>
	 */
	public Vector<String> getImageContent() {
		return this.imageContent;
	}
	/**
	 * @return	Returns the URLs to the indexed video content as Vector<String>
	 */
	public Vector<String> getVideoContent() {
		return this.videoContent;
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

	
	/**
	 * Tries to extract all links from the given attribute in the given tag
	 * that occur somewhere in the passed htmlLine.
	 * 
	 * @param htmlLine		the HTML code to analyze
	 * @param hrefs			just for recursive call
	 * @param searchTag		the tag to search (e.g. "a")
	 * @param searchAttr	the attribute to search within the tag (e.g. "href")
	 * @return				a Vector<String> containing the complete URLs to the found links; null if no link was found
	 */
	public Vector<String> extractLinks(String htmlLine, Vector<String> hrefs, String searchTag, String searchAttr) {
		Vector<String> returnHrefs;
		if (hrefs != null)
			returnHrefs = hrefs;
		else
			returnHrefs = new Vector<String>();
		// search for begin of tag
		int anchorStartIdx = htmlLine.indexOf("<"+searchTag);
		if (anchorStartIdx != -1) {		// anchor tag found -> search for attribute
			// search end of found tag
			String sub = htmlLine.substring(anchorStartIdx);
			int anchorEndIdx = sub.indexOf(">");
			if (anchorEndIdx != -1) {
				sub = sub.substring(0, anchorEndIdx+1);
//				System.out.println(sub);
				// sub now contains the complete tag
				// search in sub for given attribute
				int hrefStartIdx = sub.indexOf(searchAttr+"=");
				if (hrefStartIdx != -1) {
					String href = sub.substring(hrefStartIdx+Math.min(searchAttr.length()+1, sub.length()-hrefStartIdx));
					// search for end of attribute by searching the next blank
					int hrefEndIdx = href.indexOf(" ");
					if (hrefEndIdx != -1) {
						String restLine = href.substring(hrefEndIdx);		// remember rest of analyzed line
						// recursive call
						extractLinks(restLine, returnHrefs, searchTag, searchAttr);
						href = href.substring(0, hrefEndIdx);
						href.trim();
						// remove leading/tailing quotes
						if (href.startsWith("\""))
							href = href.substring(1);
						if (href.endsWith("\""))
							href = href.substring(0, href.length()-1);
						// try to create a complete, meaningful URL
						if (this.getUrlSource() != null) {
							try {
								URL docUrl = new URL(this.getUrlSource());
								URL linkUrl = new URL(docUrl, href);
								returnHrefs.add(linkUrl.toString());
							} catch (Exception e) {
//								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return returnHrefs;			// no link found
	}
	/**
	 * Tries to extract all links from the given attribute in the given tag
	 * that occur somewhere in the passed htmlLine.
	 * 
	 * @param htmlLine		the HTML code to analyze
	 * @param searchTag		the tag to search (e.g. "a")
	 * @param searchAttr	the attribute to search within the tag (e.g. "href")
	 * @return				a Vector<String> containing the complete URLs to the found links; null if no link was found
	 */
	public Vector<String> extractLinks(String htmlLine, String searchTag, String searchAttribute) {
		return extractLinks(htmlLine, null, searchTag, searchAttribute);
	}
	/**
	 * Tries to extract all links from the given attribute (in an arbitrary tag)
	 * that occur somewhere in the passed htmlLine.
	 * 
	 * @param htmlLine		the HTML code to analyze
	 * @param hrefs			just for recursive call
	 * @param searchAttr	the attribute to search within the tag (e.g. "href")
	 * @return				a Vector<String> containing the complete URLs to the found links; null if no link was found
	 */
	public Vector<String> extractLinks(String htmlLine, Vector<String> hrefs, String searchAttr) {
		Vector<String> returnHrefs;
		if (hrefs != null)
			returnHrefs = hrefs;
		else
			returnHrefs = new Vector<String>();
		// search for given attribute 
		int hrefStartIdx = htmlLine.indexOf(searchAttr+"=");
		if (hrefStartIdx != -1) {
			String href = htmlLine.substring(hrefStartIdx+Math.min(searchAttr.length()+1, htmlLine.length()-hrefStartIdx));
			// search for end of attribute "href" by searching the next blank
			int hrefEndIdx = href.indexOf(" ");
			if (hrefEndIdx != -1) {
				String restLine = href.substring(hrefEndIdx);		// remember rest of analyzed line
				// recursive call
				extractLinks(restLine, returnHrefs, searchAttr);
				href = href.substring(0, hrefEndIdx);
				href.trim();
				// remove leading/tailing quotes
				if (href.startsWith("\""))
					href = href.substring(1);
				if (href.endsWith("\""))
					href = href.substring(0, href.length()-1);
				// try to create a complete, meaningful URL
				if (this.getUrlSource() != null) {
					try {
						URL docUrl = new URL(this.getUrlSource());
						URL linkUrl = new URL(docUrl, href);
						returnHrefs.add(linkUrl.toString());
					} catch (Exception e) {
//						e.printStackTrace();
					}
				}
			}
		}
		return returnHrefs;			// no link found
	}
	/**
	 * Tries to extract all links from the given attribute (in an arbitrary tag)
	 * that occur somewhere in the passed htmlLine.
	 * 
	 * @param htmlLine		the HTML code to analyze
	 * @param searchAttr	the attribute to search within the tag (e.g. "href", "src")
	 * @return				a Vector<String> containing the complete URLs to the found links; null if no link was found
	 */
	public Vector<String> extractLinks(String htmlLine, String searchAttribute) {
		return extractLinks(htmlLine, (Vector<String>)null, searchAttribute);
	}
}