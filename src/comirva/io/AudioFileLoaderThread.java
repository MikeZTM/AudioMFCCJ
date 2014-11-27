/*
 * Created on 01.06.2005
 */
package comirva.io;

import comirva.data.DataMatrix;
import comirva.exception.*;

import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.sound.sampled.*;

/**
 * This class implements a thread for loading (uncompressed) audio data files.
 *
 * @author Markus Schedl
 */
public class AudioFileLoaderThread extends Thread {

	// the file containing the matrix data
	private File fileData;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// listMatrices is needed to add the name of the data matrix to the matrix list
	private DefaultListModel listMatrices;
	// the data matrix into which the file content is loaded 
	private DataMatrix dm = new DataMatrix();
	// Vector containing the loaded matrices
	private Vector matrixList;
	
	/**
	 * Creates a MatrixDataFileLoaderThread for loading a data matrix from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 * @param ml	the Vector to which the name of the DataMatrix should be added after it has been loaded
	 * @param jl	the JLabel representing the status bar (for writing current loading progress)
	 * @param lm	the DefaultListModel to add the name of the matrix to the UI
	 */
	public AudioFileLoaderThread(File f, Vector ml, JLabel jl, DefaultListModel lm) {
		this.fileData =  f;
		this.matrixList = ml;
		this.statusBar = jl;
		this.listMatrices = lm;
	}
	/**
	 * Creates a MatrixDataFileLoaderThread for loading a data matrix from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 * @param ml	the Vector to which the name of the DataMatrix should be added after it has been loaded
	 */
	public AudioFileLoaderThread(File f, Vector ml) {
		this.fileData =  f;
		this.matrixList = ml;
	}
	/**
	 * Creates a MatrixDataFileLoaderThread for loading a data matrix from File <code>f</code>.
	 * 
	 * @param f		the File which contains the matrix data (exported from Matlab)
	 */
	public AudioFileLoaderThread(File f) {
		this.fileData = f;
	}

	/**
	 * This method is called when the thread is started.
	 * The audio file is opened, the data read and extracted.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// read data and return a DataMatrix-instance
		try {
			DataMatrix dm = getMatrixFromAudioFile();					// load matrix
			//dm.printMatrix();
		} catch (NoMatrixException nme) {
			if (statusBar != null)
					statusBar.setText("The number of columns is not constant in file "  +  fileData.getAbsolutePath() + ". Opening was terminated.");
		}
	}
	
	/**
	 * Opens the audio file and loads its content into a DataMatrix-instance which is returned thereafter.
	 * 
	 * @return the DataMatrix containing the loaded matrix
	 * @throws NoMatrixException
	 * @see comirva.data.DataMatrix
	 */
	private DataMatrix getMatrixFromAudioFile() throws NoMatrixException {
		// create a DataMatrix-instance
		DataMatrix dm = new DataMatrix();
		// counter for matrix columns and rows
		int matrixCols = 0, matrixRows = 0;
		try {
			// create audio stream
			AudioInputStream ais = AudioSystem.getAudioInputStream(fileData);
			AudioFormat origFormat = ais.getFormat();
			AudioFormat outputFormat = origFormat;
			// if audio is compressed, reinit audio stream
			if (origFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				outputFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
			            origFormat.getSampleRate(),
			            16,
			            origFormat.getChannels(),
			            origFormat.getChannels() * 2,
			            origFormat.getSampleRate(),
			            false);
				// reinitialize audio input stream to be prepared for compressed formats			
			    ais = AudioSystem.getAudioInputStream(outputFormat, ais);
			    // user info
				System.out.println(origFormat);
				System.out.println(outputFormat);
			} 
			// get information on (uncompressed) audio file
			int channels = origFormat.getChannels();
			int frameSize = origFormat.getFrameSize();
			int sampleSizeInBytes = frameSize / channels;
			float samplingRate = origFormat.getSampleRate();
//			int bitRate = origFormat.getSampleSizeInBits();
//			System.out.println("Channels: "+channels);
//			System.out.println("Sampling Rate: "+samplingRate);
//			System.out.println("Bitrate: "+bitRate);
			// read audio contents of the file
			// get output line
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, outputFormat);
			SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
			// open line
			line.open(outputFormat);
			// activate audio output line
			line.start();
			
			// audio output line is prepared now -> we can write audio data to it
			int numberBytesReadTotal = 0;		// number of total bytes write
			int numberBytesRead = 0;			// counts the number of bytes read from the audio stream in one iteration
			int audioBufferSize = (int)outputFormat.getFrameRate() * outputFormat.getFrameSize() * 1;		// 1 second output buffer
			byte[] audioData = new byte[audioBufferSize];
			// start reading audio data
			while(numberBytesRead != -1) {		// read audio data until end of audio stream is reached 
				// read audio data
				numberBytesRead = ais.read(audioData, 0, audioData.length);
				numberBytesReadTotal += numberBytesRead;	// update total bytes read counter
				// write audio data to audio output line (if number of bytes read >= 0)
				if (numberBytesRead >= 0)
					line.write(audioData, 0, numberBytesRead);
				// write audio data to data matrix
//				for (int i=0; i<numberBytesRead; i++) {
//					System.out.println(audioData[i]);
					//				dm.setValueAtPos()				
					
//				}
			}
			// wait until all data is played
			line.drain();
			// close line
			line.close();
			
			System.out.println(numberBytesReadTotal);
			
			
			
			
			
//			// create reader to access file
//			BufferedReader readerFile = new BufferedReader(new FileReader(fileData));
//			// read from file as long as no exception (EOF) is thrown
//			while (true) {
//				// read one line
//				String strDataFileLine = new String(readerFile.readLine());
//				// get tokens from the line read
//				StringTokenizer tokenizerMatrixFile = new StringTokenizer(strDataFileLine);
//				// make sure, that current line has same number of tokens as previous
//				// otherwise, its no matrix -> throw NoMatrixException
//				if ((tokenizerMatrixFile.countTokens() != matrixCols) && (tokenizerMatrixFile.countTokens() != 0) && matrixCols != 0) {
//					throw new NoMatrixException();
//				}
//				// continue reading only if line is not empty
//				if (tokenizerMatrixFile.countTokens() > 0) {
//					// reset column-counter after new line was read 
//					matrixCols = 0;
//					while (tokenizerMatrixFile.hasMoreElements()) {
//						// get next token
//						String strDataFileMatrixValue = tokenizerMatrixFile.nextToken();
//						// convert String to Double
//						Double dblMatrixValue= new Double(strDataFileMatrixValue);
//						// insert read value into the DataMatrix-instance
//						dm.addValue(dblMatrixValue);
//						// increase column-counter
//						matrixCols++;
//					}
//					// increase row-counter
//					matrixRows++;
//					// start new row in DataMatrix-instance
//					dm.startNewRow();
//				}
//				if (statusBar != null)
//					statusBar.setText("Loading data matrix from file: "  +  fileData.getAbsoluteFile() + " (" + matrixRows + "x" + matrixCols + ")");
//			} 
		} catch (UnsupportedAudioFileException uafe) {
			if (statusBar != null)
				statusBar.setText("Error reading audio file " + fileData.getAbsolutePath() + ". It seems to be no valid audio file.");
		} catch (LineUnavailableException lue) {
			if (statusBar != null)
				statusBar.setText("Error reading audio file " + fileData.getAbsolutePath() + ". Cannot open audio output line.");
		} catch (NumberFormatException nfe) {
			if (statusBar != null)
				statusBar.setText("Error reading file " + fileData.getAbsolutePath() + ". Read value cannot be converted into Double.");
		} catch (EOFException eof) {
			// end of file reached
			if (statusBar != null)
				statusBar.setText("Data matrix (" + matrixRows + "x" + matrixCols + ") extracted from file: "  +  fileData.getAbsolutePath());
			dm.setName(fileData.getName() + " (" + matrixRows + "x" + matrixCols + ")");
			dm.removeLastAddedElement();
			if (listMatrices != null)	
				listMatrices.addElement(dm.getName());				// add name of matrix to matrix list in UI, if possible
			if (matrixList != null)
				matrixList.addElement(dm);							// add DataMatrix-instance to matrixList-Vector, if possible
			return dm;
		} catch (NullPointerException npe) {
			// empty line read
			if (statusBar != null)
				statusBar.setText("Data matrix (" + matrixRows + "x" + matrixCols + ") extracted from file: "  +  fileData.getAbsolutePath());
			dm.setName(fileData.getName() + " (" + matrixRows + "x" + matrixCols + ")");
			dm.removeLastAddedElement();
			if (listMatrices != null)	
				listMatrices.addElement(dm.getName());				// add name of matrix to matrix list in UI, if possible
			if (matrixList != null)
				matrixList.addElement(dm);							// add DataMatrix-instance to matrixList-Vector, if possible
			return dm;
		} catch (IOException ioe) {
			if (statusBar != null)
				statusBar.setText("I/O-Exception while accessing file " + fileData.getAbsolutePath());
		}
		if (listMatrices != null)	
			listMatrices.addElement(dm.getName());				// add name of matrix to matrix list in UI, if possible
		if (matrixList != null)
			matrixList.addElement(dm);							// add DataMatrix-instance to matrixList-Vector, if possible
		dm.setName(fileData.getName() + " (" + matrixRows + "x" + matrixCols + ")");
		// inform user
		if (statusBar != null)
			statusBar.setText("Audio data matrix (" + matrixRows + "x" + matrixCols + ") extracted from file: "  +  fileData.getAbsolutePath());		
		return dm;
	}
	
	/**
	 * Returns the DataMatrix-instance into which the audio file content is loaded.
	 *
	 * @return the DataMatrix containing the loaded matrix
	 * @see	comirva.data.DataMatrix
	 */
	public DataMatrix getDataMatrix() {
		return this.dm;
	}

}