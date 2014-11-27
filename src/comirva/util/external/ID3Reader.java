package comirva.util.external;

import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;

//import comirva.data.DataMatrix;
//import linzest.audiofeature.*;


/**
 * This class is intended to read ID3 tags
 * from MP3 files using the MP3SPI library.
 * 
 * @author Markus Schedl
 */
public class ID3Reader {
	private File mp3File;
	private String artist = null;
	private String album = null;
	private String title = null;
	private Integer bitrate = null;
	private Integer channels = null;
	private Long duration = null;
	private Float fps = null;
	private Integer lengthFrames = null;
	private Integer lengthBytes = null;
	private Integer framesizeBytes = null;
	
	private int bytesPerSec = 0;
	private int totalLenBytes = 0;
	private int readSec = 30;				// seconds to read from middle of audio file

	
	/**
	 * Creates a new ID3Reader instance that reads ID3 tags from
	 * a MP3 file.
	 * 
	 * @param mp3File	a File that represents the MP3 file
	 */
	public ID3Reader(File mp3File) throws Exception {
		this.mp3File = mp3File;
		AudioFileFormat baseFileFormat = null;
		AudioFormat baseFormat = null;
		try {
			baseFileFormat = AudioSystem.getAudioFileFormat(mp3File);
			} catch (UnsupportedAudioFileException e) {
				// TODO AutngthByto-generated catch block
				e.printStackTrace();
				throw new Exception("Unsupported File Format: "+mp3File.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("Error while reading "+mp3File.getName());
		}
		baseFormat = baseFileFormat.getFormat();
		// TAudioFileFormat properties
		if (baseFileFormat instanceof TAudioFileFormat) {
			Map properties = ((TAudioFileFormat)baseFileFormat).properties();
			// first, try to use "author" and "title" tags
			if (properties.containsKey("author"))
				this.artist = (String)properties.get("author");
			else
				this.artist = "";
			if (properties.containsKey("title"))
				this.title = (String)properties.get("title");
			else
				this.title = "";
			if (properties.containsKey("album"))
				this.album = (String)properties.get("album");
			else
				this.album = "";
			if (properties.containsKey("duration"))
				this.duration = (Long)properties.get("duration");
			else
				this.duration = new Long(-1);
//			if (properties.containsKey("mp3.bitrate.nominal.bps"))
//				this.bitrate = (Integer)properties.get("mp3.bitrate.nominal.bps");
			if (properties.containsKey("mp3.channels"))
				this.channels = (Integer)properties.get("mp3.channels");
			else
				this.channels = new Integer(-1);
			if (properties.containsKey("mp3.length.frames"))
				this.lengthFrames = (Integer)properties.get("mp3.length.frames");
			else
				this.lengthFrames = new Integer(-1);
			if (properties.containsKey("mp3.framerate.fps"))
				this.fps = (Float)properties.get("mp3.framerate.fps");
			else
				this.fps = new Float(-1);
			if (properties.containsKey("mp3.length.bytes"))
				this.lengthBytes = (Integer)properties.get("mp3.length.bytes");
			else
				this.lengthBytes = new Integer(-1);
			if (properties.containsKey("mp3.framesize.bytes"))
				this.framesizeBytes = (Integer)properties.get("mp3.framesize.bytes");
			else
				this.framesizeBytes = new Integer(-1);
		}
		// TAudioFormat properties
		if (baseFormat instanceof TAudioFormat) {
			Map properties = ((TAudioFormat)baseFormat).properties();
			if (properties.containsKey("bitrate"))
				this.bitrate = (Integer)properties.get("bitrate");
		}
	}


	public void play() {
	  try {
	    AudioInputStream in = AudioSystem.getAudioInputStream(this.mp3File);
	    AudioInputStream din = null;
	    AudioFormat baseFormat = in.getFormat();
	    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
	                                                                                  baseFormat.getSampleRate(),
	                                                                                  16,
	                                                                                  baseFormat.getChannels(),
	                                                                                  baseFormat.getChannels() * 2,
	                                                                                  baseFormat.getSampleRate(),
	                                                                                  false);
	    long startNano = System.nanoTime();
	    
	    din = AudioSystem.getAudioInputStream(decodedFormat, in);
	    // only take readSec secs from center if track longer than readSec secs
	    if (this.getDuration().longValue()/1000000 > readSec) {
	    	// calc how many bytes have to be skipped...
	    	// ...in the decompressed stream
	    	this.bytesPerSec = (int)(baseFormat.getChannels() * baseFormat.getSampleRate() * 16 / 8);
	    	this.totalLenBytes = (int)(baseFormat.getChannels() * baseFormat.getSampleRate() * 16 * this.getDuration().longValue()/8000000); 
	    	// ...and in the compressed
	    	int bytesPerSec = (int)(this.getLengthBytes().intValue() / (this.getDuration().longValue()/1000000));
	    	int totalLenBytes = this.getLengthBytes().intValue();
	    	// skipping with compressed values
	    	din.skip(totalLenBytes/2 - 30*bytesPerSec);
	    }
   
	    long endNano = System.nanoTime();
	    System.out.println("skipping: " + (endNano-startNano)/1000000 + " msec");
	    
	    rawplay(decodedFormat, din);
	    in.close();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
	}
	
	private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
		
		// read approx readSec sec from middle of audio stream
		int noReadBytes = Math.min(this.bytesPerSec*readSec, this.totalLenBytes);
    	int blockSize = 4096;
    	noReadBytes += blockSize - (noReadBytes % blockSize);		// correct number of read bytes
    	byte[] data = new byte[blockSize];	
		byte[] data60s = new byte[noReadBytes];
		int noBytesRead = 0;
		int totalNoBytesRead = 0;
		int curBlock = 0;
	    long startNano = System.nanoTime();
		while ((noBytesRead != -1) && (totalNoBytesRead+blockSize < noReadBytes)) {
			noBytesRead = din.read(data60s, curBlock*blockSize, blockSize);	// read audio stream
			totalNoBytesRead += noBytesRead;
			curBlock++;
//			System.out.println("block: "+ curBlock + ", bytes read: " + noBytesRead + ", total bytes read " + totalNoBytesRead + ", maximum bytes assigned " + noReadBytes);
		}
	    long endNano = System.nanoTime();
	    System.out.println("decoding 1min: " + (endNano-startNano)/1000000 + " msec");

		
//		System.out.println("read " + totalNoBytesRead + " bytes");
	  
		// play
		SourceDataLine line = getLine(targetFormat);
		if (line != null) {
			line.start();
			int nBytesWritten = 0;
			for (int i=0; i<totalNoBytesRead; i+=blockSize)
			{
				line.write(data60s, i, blockSize);
			}
			// stop
			line.drain();
			line.stop();
			line.close();
			din.close();
		}
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	}
	
	
//	private void calcFPs() {
//		File[] f = {this.mp3File};
//		SimpleFPExtractionThread sfpe = new SimpleFPExtractionThread(new ID3Reader[] {this});
//		sfpe.start();
//	}
//	
//	public static void main(String[] args) {
//		ID3Reader id3r = new ID3Reader(new File("/home/mms/Music/3songs/ramones.mp3"));
//		System.out.println("artist: " + id3r.getArtist());
//		System.out.println("title: " + id3r.getTitle());
//		System.out.println("album: " + id3r.getAlbum());
//		System.out.println("bitrate: " + id3r.getBitrate());
//		System.out.println("channels: " + id3r.getChannels());
//		System.out.println("duration: " + id3r.getDuration().longValue()/1000000 + " s");
//		System.out.println("length in frames: " + id3r.getLengthFrames());
//		System.out.println("length in bytes: " + id3r.getLengthBytes());
//		System.out.println("frames/sec: " + id3r.getFps());
//		System.out.println("framesize: " + id3r.getFramesizeBytes() + " bytes");					
//		id3r.calcFPs();
//	}
	

	
	/**
	 * @return Returns the album.
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * @return Returns the artist.
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return Returns the bitrate.
	 */
	public Integer getBitrate() {
		return bitrate;
	}

	/**
	 * @return Returns the channels (1 or 2).
	 */
	public Integer getChannels() {
		return channels;
	}

	/**
	 * @return Returns the duration in microseconds.
	 */
	public Long getDuration() {
		return duration;
	}

	/**
	 * @return Returns the mp3File.
	 */
	public File getMp3File() {
		return mp3File;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @return Returns the fps.
	 */
	public Float getFps() {
		return fps;
	}


	/**
	 * @return Returns the framesizeBytes.
	 */
	public Integer getFramesizeBytes() {
		return framesizeBytes;
	}


	/**
	 * @return Returns the lengthBytes.
	 */
	public Integer getLengthBytes() {
		return lengthBytes;
	}


	/**
	 * @return Returns the lengthFrames.
	 */
	public Integer getLengthFrames() {
		return lengthFrames;
	}
	
}
