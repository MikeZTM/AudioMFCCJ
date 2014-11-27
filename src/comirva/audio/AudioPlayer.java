/*
 * Created on 02.06.2005
 */
package comirva.audio;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class implements methods for reading and playing audio files.
 * Its ability to read audio files of different formats depends
 * on the installed Java Sound implementations.
 * To obtain plug-ins for different file formats, see e.g. http://tritonus.org/
 *
 * @author Markus Schedl
 */
public class AudioPlayer extends Thread {
	private File audioFile;					// the audio file to be played
	private boolean isPaused = false;		// flag for state "paused"
	private boolean isStopped = false;		// flag for state "stopped"
	private AudioInputStream ais;

	/**
	 * Creates a new instance of the AudioPlayer.
	 *
	 * @param audioFile		the audio file to be played
	 */
	public AudioPlayer(File audioFile) {
		this.audioFile = audioFile;
	    // create audio stream
	    try
	    {
	      ais = AudioSystem.getAudioInputStream(audioFile);
	    }
	    catch(Exception e)
	    {
	      System.out.println(e.toString());
	    }
	}

  /**
   * Creates a new instance of the AudioPlayer.
   *
   * @param ais		the AudioInputStream which is played
   */
	public AudioPlayer(AudioInputStream ais) {
	    if(ais == null)
	    	throw new IllegalArgumentException("audio input stream must nut be a null value");
	    this.ais = ais;
	}

	/**
	 * This method is called when the thread is started.
	 * The audio file is opened, the content read and played.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			//get format
			AudioFormat origFormat = ais.getFormat();
			AudioFormat outputFormat = origFormat;
			// if audio is compressed, re-init audio stream
			if (origFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
				outputFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
			            origFormat.getSampleRate(),
			            16,
			            origFormat.getChannels(),
			            origFormat.getChannels() * 2,
			            origFormat.getSampleRate(),
			            false);
				// re-initialize audio input stream to be prepared for compressed formats
			    ais = AudioSystem.getAudioInputStream(outputFormat, ais);
//				System.out.println(origFormat);
//				System.out.println(outputFormat);
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
			int audioBufferSize = (int)outputFormat.getFrameRate() * outputFormat.getFrameSize() / 4;		// 1/4 second output buffer
			byte[] audioData = new byte[audioBufferSize];
			// start reading audio data
			while(numberBytesRead != -1) {		// read audio data until end of audio stream is reached
				// read audio data
				if (isStopped) {		// if state is stopped, close stream and line and exit while-loop
					ais.close();
					line.close();
					break;
				}
				if (isPaused) {			// if state is paused, pause thread
					try {
						AudioPlayer.sleep(1);
					} catch (InterruptedException ie) {
					}
				} else {				// neither paused, nor stopped -> read data
					numberBytesRead = ais.read(audioData, 0, audioData.length);
					numberBytesReadTotal += numberBytesRead;	// update total bytes read counter
					// write audio data to audio output line (if number of bytes read >= 0)
					if (numberBytesRead >= 0)
						line.write(audioData, 0, numberBytesRead);
				}
			}
			// wait until all data is played
			line.drain();
			// close line
			line.close();
			// set state to stopped
			this.setStopped(true);
//			System.out.println("total number of bytes read: " + numberBytesReadTotal);
		} catch (LineUnavailableException lue) {
		} catch (IOException ioe) {
		}
	}

	/**
	 * Returns whether the audio player is paused.
	 *
	 * @return a boolean indicating if the audio player is paused
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Sets the state of the audio player to "paused" or "not paused" depending on the
	 * value of the argument <code>isPaused</code>.
	 *
	 * @param isPaused 	a boolean indicating if the audio player should pause or not
	 */
	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * Returns whether the audio player is stopped or not.
	 *
	 * @return 	a boolean indicating if the audio player is stopped
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Sets the state of the audio player to "stopped" or "not stopped" depending on the
	 * value of the argument <code>isStopped</code>.
	 *
	 * @param isStopped 	a boolean indicating if the audio player should stop or not
	 */
	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

}
