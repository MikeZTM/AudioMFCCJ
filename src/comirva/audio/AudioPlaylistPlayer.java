/*
 * Created on 03.06.2005
 */
package comirva.audio;

import java.util.*;
import java.io.*;

/**
 * This class implements a playlist for audio files.
 * 
 * @author Markus Schedl
 */
public class AudioPlaylistPlayer extends Thread {
	private Vector audioFiles = new Vector();	// to store the Files which are in the playlist
	private AudioPlayer ap;						// an AudioPlayer-instance to play the sound files
	private int currentTrackNo = 0;				// number of current track in playlist
	
	/**
	 * Creates a new empty playlist. 
	 */
	public AudioPlaylistPlayer() {
	}
	/**
	 * Creates a new playlist that contains the files given as arguments.
	 * 
	 * @param audioFiles	a Vector containing the files to be inserted into the playlist as <code>File</code>.
	 */
	public AudioPlaylistPlayer(Vector audioFiles) {
		this.audioFiles = audioFiles;
	}

	/**
	 * This method is called when the thread is started.
	 * It starts to play from the beginning of the playlist.
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		playCompleteList();
	}

	/**
	 * Adds a file to the playlist.
	 * 
	 * @param audioFile	the File to be added
	 */
	public void addTrack(File audioFile) {
		this.audioFiles.addElement(audioFile);
	}
	
	/**
	 * Removes the audio file at the given position in the playlist. 
	 * 
	 * @param position	the position of the audio file in the playlist that should be removed (starts with 0)
	 */
	public void removeTrack(int position) {
		if (audioFiles != null && audioFiles.size() > position)
			audioFiles.removeElementAt(position);
	}

	/**
	 * Returns the audio file at the given position in the playlist.
	 * 
	 * @param position	the position of the audio file in the playlist that should be returned (starts with 0)
	 * @return	the File at the requested position in the playlist
	 */
	public File getTrack(int position) {
		if (audioFiles != null && audioFiles.size() > position)
			return (File)audioFiles.elementAt(position);
		else
			return null;
	}
	
	/**
	 * Plays the complete playlist once.
	 */
	private void playCompleteList() {
		// pass each track in the playlist
		for (currentTrackNo=0; currentTrackNo<this.audioFiles.size(); currentTrackNo++) {
			// play current track (create audio player thread)
			this.playTrackNumber(currentTrackNo);
			// allow audio player thread to execute
			AudioPlaylistPlayer.yield();
			// wait until audio player thread has finished playing
			while (this.ap == null || !this.ap.isStopped()) {
				try {
					this.ap.join();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/**
	 * Plays the track of the playlist that is indicated by the argument <code>number</code>.
	 * 
	 * @param number	the number of the track in the playlist that is to be played
	 */
	private void playTrackNumber(int number) {
		if (number >= 0 && number < this.audioFiles.size()) {		// valid track number given?		
			// create an audio player thread
			this.ap = new AudioPlayer((File)this.audioFiles.elementAt(number));
			// start audio player thread
			this.ap.start();
		}
	}
	
	/**
	 * Plays the next track of the playlist. If the currently played track is the last one,
	 * the first track is played instead.
	 */
	public void playNext() {
		// stop playing current track, if playing is in progress
		if (this.ap != null)
			this.ap.setStopped(true);
		// increase current track counter
		if (currentTrackNo < this.audioFiles.size() - 1)	// currently not playing last track -> continue with next track
			currentTrackNo++;
		else												// currently playing last track -> continue with first track
			currentTrackNo = 0;
		// start playing next track 
		playTrackNumber(currentTrackNo);	
	}

	/**
	 * Plays the previous track of the playlist. If the currently played track is the first one,
	 * the last track is played instead.
	 */
	public void playPrevious() {
		// stop playing current track, if playing is in progress
		if (this.ap != null)
			this.ap.setStopped(true);
		// decrease current track counter
		if (currentTrackNo > 0)			// currently not playing first track -> continue with previous track
			currentTrackNo--;
		else							// currently playing first track -> continue with last track of playlist
			currentTrackNo = this.audioFiles.size() - 1;
		// start playing previous track 
		playTrackNumber(currentTrackNo);	
	}
	
	/**
	 * Pauses the audio output.
	 */
	public void pausePlaying() {
		if (this.ap != null && !this.ap.isPaused())
			this.ap.setPaused(true);
	}
	
	/**
	 * Continues the audio output if it is paused.
	 */
	public void continuePlaying() {
		if (this.ap != null && this.ap.isPaused())
			this.ap.setPaused(false);
	}

	/**
	 * Returns whether the audio output is paused.
	 * 
	 * @return a boolean indicating if the audio output is paused
	 */
	public boolean isPaused() {
		return this.ap.isPaused();
	}
	
	/**
	 * Stops the audio output. 
	 */
	public void stopPlaying() {
		// stop audio player thread and playing of playlist
		if (this.ap != null) {
			this.ap.setStopped(true);
			// the trick here is to set the current track number to a value so that the condition of the for-loop 
			// in the method "playCompleteList" does not hold and no new audio player thread is created
			currentTrackNo = this.audioFiles.size();
		}
	}
	
}
