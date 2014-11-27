package comirva.audio.extraction;

import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;

import comirva.audio.feature.AudioFeature;
import comirva.audio.feature.MandelEllis;
import comirva.audio.util.AudioPreProcessor;
import comirva.audio.util.MFCC;
import comirva.audio.util.math.Matrix;

public class MandelEllisExtractor implements AudioFeatureExtractor {

	private AudioPreProcessor preProcessor;
  public int skipIntroSeconds = 30;       //number of seconds to skip at the beginning of the song
  public int skipFinalSeconds = 30;       //number of seconds to skip at the end of the song
  public int minimumStreamLength = 30;    //minimal number of seconds of audio data to return a vaild result

	protected MFCC mfcc;

	public MandelEllisExtractor()
  {
    this(30, 30, 30);
	}

  public MandelEllisExtractor(int skipIntro, int skipEnd, int minimumLength)
    {
      this.mfcc = new MFCC(AudioPreProcessor.DEFAULT_SAMPLE_RATE);

      if(skipIntro < 0 || skipEnd < 0 || minimumStreamLength < 1)
        throw new IllegalArgumentException("illegal parametes;");

      this.skipIntroSeconds = skipIntro;
      this.skipFinalSeconds = skipEnd;
      this.minimumStreamLength = minimumLength;
  }

	public AudioFeature calculate(Object input) throws IOException, IllegalArgumentException
  {

      //check input type
      if(input == null || !(input instanceof AudioInputStream))
        throw new IllegalArgumentException("input type for the me feature extraction process should be AudioPreProcessor and must not be null");
      else
        preProcessor = new AudioPreProcessor((AudioInputStream) input);

      //skip the intro part
      preProcessor.fastSkip((int) AudioPreProcessor.DEFAULT_SAMPLE_RATE * skipIntroSeconds);

      //pack the mfccs into a pointlist
      Vector<double[]> mfccCoefficients = mfcc.process(preProcessor);

      //check if element 0 exists
      if(mfccCoefficients.size() == 0)
        throw new IllegalArgumentException("the input stream ist to short to process;");

      //compute number of samples to skip at the end
      int skip = (int) ((skipFinalSeconds * preProcessor.getSampleRate())/(mfcc.getWindowSize()/2));

      //check if the resulting point list has the required minimum length and skip the last few samples
      if(mfccCoefficients.size() - skip > ((minimumStreamLength * preProcessor.getSampleRate())/(mfcc.getWindowSize()/2)))
        mfccCoefficients = new Vector<double[]>(mfccCoefficients.subList(0, mfccCoefficients.size() - skip - 1));
      else
        throw new IllegalArgumentException("the input stream ist to short to process;");

      //create mfcc matrix
      Matrix mfccs = new Matrix(mfccCoefficients, false);

      //create covariance matrix
      Matrix covarMatrix = mfccs.cov();

      //compute mean
      Matrix mean = mfccs.mean(1).transpose();

      //mfccs.saveAscii("C:\\temp\\mfccs.ascii");

      MandelEllis.GmmMe gmmMe = new MandelEllis.GmmMe(covarMatrix, mean);

      return new MandelEllis(gmmMe);
	}

	public int getAttributeType()
  {
		return MandelEllis.class.getName().hashCode();
	}

  public String toString()
  {
    return "Mandel Ellis";
  }
}
