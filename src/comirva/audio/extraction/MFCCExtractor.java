package comirva.audio.extraction;

import comirva.audio.feature.Attribute;
import comirva.audio.feature.TimbreDistribution;
import comirva.audio.util.AudioPreProcessor;
import comirva.audio.util.MFCC;
import comirva.audio.util.PointList;
import comirva.audio.util.gmm.CovarianceSingularityException;
import comirva.audio.util.gmm.GaussianMixture;
import comirva.audio.util.kmeans.KMeansClustering;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * <b>Timbre Distribution Extractor</b>
 *
 * <p>Description:</p>
 * This class supports the extraction of the "Timbre Distribution" summarizing
 * the timbre of an audio stream.<br>
 * <br>
 * This is done by computing the MFCC for each audio frame(usually between 20ms
 * and 50ms and a 50% overlap). The MFCCs are known to somehow characterize
 * the timbre of such a short audio frame. Then one estimates the distribution
 * of the MFCC vectors using a Gaussian Mixture Model.<br>
 * <br>
 * The resulting distribution is a model of the song's overall timbre and can
 * be compared to other timbre models.
 *
 *
 * [1] Aucouturier, Pachet, "Improving Timbre Similarity: How high's the sky?"
 *     Journal of Negative Results in Speech and Audio Sciences, 1(1), 2004.
 *
 * @see comirva.audio.util.gmm.GaussianMixture
 * @see comirva.audio.util.MFCC
 * @see comirva.audio.feature.TimbreDistribution
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class MFCCExtractor implements AudioFeatureExtractor
{
  public int DEFAULT_NUMBER_COMPONENTS = 3; //default number of components to use for the gmm
  public int skipIntroSeconds = 0;       //number of seconds to skip at the beginning of the song
  public int skipFinalSeconds = 0;       //number of seconds to skip at the end of the song
  public int minimumStreamLength = 8;    //minimal number of seconds of audio data to return a vaild result

  protected AudioPreProcessor preProcessor;
  protected MFCC mfcc;
  protected int numberGaussianComponents = DEFAULT_NUMBER_COMPONENTS;


  /**
   * The default constructor uses 3 gaussian components for modelling the timbre
   * distribution. For more details on the default MFCC computation take a look
   * at the <code>MFCC</code> documentation. The <code>AudioPreProcessors<code>
   * default sample rate is used.
   *
   * @see comirva.audio.util.MFCC
   * @see comirva.audio.util.AudioPreProcessor
   */
  public MFCCExtractor()
  {
    this.mfcc = new MFCC(AudioPreProcessor.DEFAULT_SAMPLE_RATE);
  }


  /**
   * This constructor in contrast to the default constructor allows to specify
   * the number of gaussian components used for modelling the timbre
   * distribution.
   *
   * @param numberGaussianComponents int number of gaussian components
   * @param skipIntro int number of seconds to skip at the beginning of the song
   * @param skipEnd int number of seconds to skip at the end of the song
   * @param minimumLength int minimum length required for processing
   */
  public MFCCExtractor(int numberGaussianComponents, int skipIntro, int skipEnd, int minimumLength)
  {
    this.mfcc = new MFCC(AudioPreProcessor.DEFAULT_SAMPLE_RATE);

    if(numberGaussianComponents < 1 || skipIntro < 0 || skipEnd < 0 || minimumStreamLength < 1)
      throw new IllegalArgumentException("illegal parametes;");

    this.numberGaussianComponents = numberGaussianComponents;
    this.skipIntroSeconds = skipIntro;
    this.skipFinalSeconds = skipEnd;
    this.minimumStreamLength = minimumLength;
  }


  /**
   * This method is used to calculate the timbre distribution for a whole song.
   * The song must be handed to this method as an <code>AudioPreProcessor</code>
   * object. All settings are set by the constructor, so this method can easily
   * be called for a large number of songs to extract this feature.
   *
   * @param input Object an object representing the input data to extract the
   *                     feature out of
   * @return Feature a feature extracted from the input data
   *
   * @throws java.io.IOException failures due to io operations are signaled by
   *                     IOExceptions
   * @throws IllegalArgumentException raised if mehtod contract is violated,
   *                                  especially if the open input type is not
   *                                  of the expected type
   */
  public Attribute calculate(Object input) throws IOException,  IllegalArgumentException
  {
     GaussianMixture gmm = null;

      //check input type
      if(input == null || !(input instanceof AudioInputStream))
        throw new IllegalArgumentException("input type for the td feature extraction process should be AudioPreProcessor and must not be null");
      else
        preProcessor = new AudioPreProcessor((AudioInputStream) input);

      //skip the intro part
      preProcessor.fastSkip((int) AudioPreProcessor.DEFAULT_SAMPLE_RATE * skipIntroSeconds);

      //pack the mfccs into a pointlist
      Vector mfccCoefficients = mfcc.process(preProcessor);

      //check if element 0 exists
      if(mfccCoefficients.size() == 0)
        throw new IllegalArgumentException("the input stream ist to short to process;");

      //create a point list with appropriate dimensions
      double[] point = (double[]) mfccCoefficients.get(0);
      PointList pl = new PointList(point.length);

      //fill pointlist and remove the last samples
      int skip = (int) ((skipFinalSeconds * preProcessor.getSampleRate())/(mfcc.getWindowSize()/2));
      for(int i = 0; i < mfccCoefficients.size()-skip; i++)
      {
        point = (double[]) mfccCoefficients.get(i);
        pl.add(point);
      }

      //check if the resulting point list has the required minimum length
      if(pl.size() < ((minimumStreamLength * preProcessor.getSampleRate())/(mfcc.getWindowSize()/2)))
        throw new IllegalArgumentException("the input stream ist to short to process;");

      try
      {
        //run k-means clustering algorithm to initialize the EM algorithem
        KMeansClustering kmeans = new KMeansClustering(numberGaussianComponents, pl, false);
        kmeans.run();

        //run EM algorithem for gaussian mixture model
        gmm = new GaussianMixture(kmeans.getClusterWeights(), kmeans.getMeans(), kmeans.getFullCovariances());
        gmm.runEM(pl);
      }
      catch(CovarianceSingularityException cse)
      {
        //try to do the whole stuff once more with the corrected pointset
        try
        {
            //run k-means clustering algorithm to initialize the EM algorithem
            KMeansClustering kmeans = new KMeansClustering(numberGaussianComponents, cse.getCorrectedPointList(), false);
            kmeans.run();

            //run EM algorithem for gaussian mixture model
            gmm = new GaussianMixture(kmeans.getClusterWeights(), kmeans.getMeans(), kmeans.getFullCovariances());
            gmm.runEM(cse.getCorrectedPointList());
        }
        catch(CovarianceSingularityException cse2)
        {
          //well at this point we give up, we don't try further
          throw new IllegalArgumentException("cannot create GMM for this song;");
        }
      }

      return new TimbreDistribution(gmm);
  }


  /**
   * Returns the type of the attribute that the class implementing this
   * interface will return as the result of its extraction process. By
   * definition this is the hash code of the attribute's class name.
   *
   * @return int an integer uniquely identifying the returned
   *             <code>Attribute</code>
   */
  public int getAttributeType()
  {
    return TimbreDistribution.class.getName().hashCode();
  }


  /**
   * Returns the feature extractors name.
   *
   * @return String name of this feature extractor
   */
  public String toString()
  {
    return "Timbre Distribution";
  }
}
