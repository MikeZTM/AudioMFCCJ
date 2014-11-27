package comirva.audio.extraction;

import java.util.Vector;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import comirva.audio.util.FFT;
import comirva.audio.util.AudioPreProcessor;
import comirva.audio.util.Sone;
import comirva.audio.util.math.Matrix;
import comirva.audio.util.math.NormalizedConvolution;
import comirva.audio.feature.Attribute;
import comirva.audio.feature.FluctuationPattern;


/**
 * <b>Title: Fluctuation Patterns</b>
 *
 * <p>Description: </p>
 * This class supports the extraction of the so-called "Fluctuation Patterns"
 * from an audio stream. The fluctuation pattern of a song is a feature
 * describing the rhythmic structure of the whole song in some way.
 *
 * Form implementation point of view <code>FluctuationPatternExtractor</code>
 * implements the <code>AudioFeatureExtractor</code> interface and returns a
 * <code>FluctuationPattern</code> object, which is an instance of a subclass
 * of the abstract <code>AudioFeature</code> class.
 *
 *
 * [1] Fastl, "Fluctuation strength and temporal masking patterns of
 *     amplitude-modulated broad-band noise", Hearing Research, 8:59-69, 1982.
 *
 * [2] Rauber, Pampalk, Merkl "Using Psycho-Acoustic Models and Self-Organizing
 *     Maps to create a Hierrarchical Structuring of Music by Sound Similarity",
 *     In proceedings of ISMIR 2002, pages 71-80, 2002.
 *
 * [3] Pampalk, Rauber "Content-based Organization and Visualization of Music
 *     Archives", In Proceedings of the ACM Multimedia 2002, pages 570-579, 2002.
 *
 * @see comirva.audio.extraction.AudioFeatureExtractor
 * @see comirva.audio.feature.FluctuationPattern
 * @author Klaus Seyerlehner
 * @version 1.1
 */

public class FluctuationPatternExtractor implements AudioFeatureExtractor
{
  //fields individualizing the object
  protected int segmentSize;
  protected int fftSize;

  //objects used for feature extraction process
  protected FFT magnitudeFFT;
  protected Sone soneProcessor;
  protected NormalizedConvolution normalizedConvolution;
  protected AudioPreProcessor preProcessor;

  //some constants/values to simplify the code
  private int maxIndex;
  private int minIndex;
  private double baseFrequency;
  private double[] fluxWeights;
  private double[] gaussFilter = {0.05, 0.1, 0.25, 0.5, 1, 0.5, 0.25, 0.1, 0.05};


  /**
   * The default constructor uses segments of size 65536 samples. This
   * corresponds to about 6 sec. of audio @11kHz.
   */
  public FluctuationPatternExtractor()
  {
    this(512, 65536);
  }

  /**
   * Constructs an individualized FluctuationPatternExtractor. The number of
   * samples to use for a segment has to be chosen with respect to an 11kHz
   * audio stream.
   * An fft size of 512 is used to get the rhythmic structure out of the sone
   * representation. This corresponds to a range of 10bpm up to 2584bpm.
   *
   * @param fftSize int number of sone values to consider
   * @param segmentSize int number of samples a segments consists of
   */
  public FluctuationPatternExtractor(int fftSize, int segmentSize)
  {
    //set fields
    this.fftSize = fftSize;
    this.segmentSize = segmentSize;

    //create sone object to compute sone representation
    soneProcessor = new Sone(AudioPreProcessor.DEFAULT_SAMPLE_RATE);

    magnitudeFFT = new FFT(FFT.FFT_MAGNITUDE, fftSize);

    //defines a normalized convolution based on a gaussFilter
    normalizedConvolution = new NormalizedConvolution(gaussFilter);

    //compute base frequency = base frequency of fft / size of sone fft
    baseFrequency = (AudioPreProcessor.DEFAULT_SAMPLE_RATE/soneProcessor.getHopSize())/fftSize;

    //get index closest to 10Hz (plus 1 for easier loop handling)
    maxIndex = (int) (10.0d/baseFrequency + 3.0d);

    //ignore first coefficient
    minIndex = 1;

    //get the fluctuation weights
    fluxWeights = getFluxWeights();
  }


  /**
   * This method is used to calculate the fluctuation pattern for a whole song.
   * The song must be handed to this method as an <code>AudioPreProcessor</code>
   * object. All settings are set by the constructor, so this method can easily
   * be called for a large number of songs to extract this feature.
   *
   * @param input Object an <code>AudioPreProcessor</code> representing the audio
   *                 input stream;
   * @return Feature a <code>FluctuationPattern</code> feature is returned as a
   *                 result of this feature extraction processs
   *
   * @throws IOException if there are any problems regarding the inputstream
   * @throws IllegalArgumentException raised if mehtod contract of a subroutine
   *                                  is violated
   */
  public Attribute calculate(Object input) throws IllegalArgumentException, IOException
  {
    //check input type
    if(input == null || !(input instanceof AudioInputStream))
      throw new IllegalArgumentException("input type for the fp feature extraction process should be AudioPreProcessor and must not be null");
    else
      preProcessor = new AudioPreProcessor((AudioInputStream) input);

    //get all fluctuation patterns (one for every segment)
    Vector allFluctuationPatterns = getFluctuationPatterns();

    //inferre prototypic fluctuation pattern
    Matrix fluctuationPattern = getPrototypicPattern(allFluctuationPatterns);

    //return fluctuation pattern as array
    return new FluctuationPattern(fluctuationPattern);
  }


  /**
   * Since the fluctuation pattern should somehow describe the wohle song
   * in a samll and compact way, we have to summarize all the fluctuation
   * patterns of the individual segemtns by inferring a prototypic fluctuation
   * pattern for the wohle song.
   * This is done by computing the median over all these patterns of the
   * individual segments for each coefficient.
   *
   * @param allFluctuationPatterns Vector a collections of fluctuation patterns;
   *                                      must not be a null value
   * @return Matrix the prototypic pattern representing the rhythmic structure
   *                of the whole song
   */
  private Matrix getPrototypicPattern(Vector allFluctuationPatterns)
  {
    Matrix fluctuationPattern;

    //construct a big array containing the unpackt fluctuation patterns as elements
    double[][] allTogether = new double[allFluctuationPatterns.size()][];
    for(int i = 0; i < allTogether.length; i++)
    {
        Matrix cur = (Matrix) allFluctuationPatterns.get(i);
        allTogether[i] = cur.getColumnPackedCopy();
    }

    //transpose and get matrix as array
    allTogether = ((new Matrix(allTogether)).transpose()).getArray();

    //each array holds the same coefficients of the different patterns
    double[] result = new double[allTogether.length];
    //compute the median for every array
    for(int i = 0; i < allTogether.length; i++)
      result[i] = getMedian(allTogether[i]);

    //convert the result back to matrix format
    fluctuationPattern = new Matrix(result, result.length/(maxIndex - minIndex - 1));

    //transpose matrix
    fluctuationPattern = fluctuationPattern.transpose();

    //return the prototypic pattern
    return fluctuationPattern;
  }

  /**
   * Splits the audio stream in short segments and computes a fluctuation
   * pattern for every third segment. The returend Vector contains all these
   * patterns, which altogether describe the rhythmic structure of the wohle
   * song.
   *
   * @return Vector a vector containing all the fluctuation patterns
   *
   * @throws IOException if there are any problems regarding the inputstream
   * @throws IllegalArgumentException raised if mehtod contract of a subroutine
   *                                  is violated
   */
  protected Vector getFluctuationPatterns() throws IOException, IllegalArgumentException
  {
    double[] segmentData = new double[segmentSize + soneProcessor.getHopSize()];
    double[][] soneData;
    Vector allFluctuationPatterns = new Vector();
    Matrix fluctuationData;

    //get first segment to process
    int samplesRead = preProcessor.append(segmentData, 0, segmentSize + soneProcessor.getHopSize());

    //if we cannot read the first segment, then we assume an io error
    if(samplesRead != segmentSize + soneProcessor.getHopSize())
      throw new IOException("cloudn't read enought data from input stream");

    //compute fluctuation pattern for every segment
    while(samplesRead == (segmentSize + soneProcessor.getHopSize()))
    {
      //create sone representation of the 6 second segement
      soneData = soneProcessor.process(segmentData);

      //create fluctuation pattern for every segment
      fluctuationData = createPattern(soneData);

      //store all patterns of the song
      allFluctuationPatterns.add(fluctuationData);

      //skip two segements
      preProcessor.skip(segmentSize * 2);

      //get next segment to process
      samplesRead = preProcessor.append(segmentData, 0, segmentSize + soneProcessor.getHopSize());
    }

    return allFluctuationPatterns;
  }


  /**
   * Computes the fluctuation pattern for a short piece of audio. The
   * fluctuation pattern characterizes the rhythmic structure of the short audio
   * segment. The input data are Sone (Specific Loudness Sensation) values.
   *
   * @param sone double[][] sone values; must not be a null value;
   * @return Matrix fluctuation pattern
   */
  protected Matrix createPattern(double[][] sone)
  {
      Matrix fluctuation;

      //get weighted magnitude coefficients representing the rhythmic structure
      fluctuation = performFFT(sone);

      //calculate the difference from one column to the next (gradients)
      fluctuation.diffEquals();

      //blur the resulting gradient
      fluctuation = blur(fluctuation);

      return fluctuation;
  }


  /**
   * Performs a magnitude FFT on the Sone data und weights the resulting
   * coefficients according to the <i>fluctuation strength</i> model.
   *
   * @param sone double[][] sone values for a segment of music; must not be a
   *                        null value;
   * @return Matrix a matrix containing the weighted coefficients
   */
  private Matrix performFFT(double[][] sone)
  {
      double[][] fftData = new double[sone[0].length][maxIndex - minIndex];
      double[] curBand = new double[fftSize];

      //for each bark band
      for(int i = 0; i < sone[0].length; i++)
      {

          //copy the band into the buffer
          for(int j = 0; j < sone.length; j++)
              curBand[j] = sone[j][i];

          //perform magnitude fft for this band
          magnitudeFFT.transform(curBand, null);

          //create array for output data
          fftData[i] = new double[maxIndex - minIndex];

          //weight the coefficients
          for(int j = minIndex, k = 0; j < maxIndex; j++, k++)
            fftData[i][k] = curBand[j] * fluxWeights[k];
      }

      //create and return matrix
      return new Matrix(fftData);
  }


  /**
   * Returns weights for the amplitude modulation coefficients based on the
   * psychoacoustic model of the <i>fluctuation strength</i>. The number of
   * the weights depends on the base frequency.
   *
   * For details take a look at [1].
   *
   * @return double[] weights for the amplitude modulation coefficients
   */
  public double[] getFluxWeights()
  {
      double[] weights = new double[maxIndex - minIndex];

      for(int i = minIndex, j = 0; i < maxIndex; i++, j++)
          weights[j] = 1.0d/((baseFrequency * i)/4.0d + 4.0d/(baseFrequency * i));

      return  weights;
  }


  /**
   * A gaussian filter is used to blur the Matrix m in both dimensions.
   * This is done by computing the convolution using the normalized filter.
   *
   * @param m Matrix matrix to blur; must not be a null value;
   * @return Matrix blurred matrix
   */
  private Matrix blur(Matrix m)
  {
      //blur the first dimension
      m = normalizedConvolution.convolute(m, true);
      //blur the second dimension
      m = normalizedConvolution.convolute(m, false);

      return m;
  }


  /**
   * Computes the median of all the elements in the array. The elements in the
   * array are sorted after a calling this method.
   *
   * @param array double[] array of values; must not be a null value and must
   *                       contain at least one element;
   * @return double the median of all the elements of the given array
   *
   * @throws IllegalArgumentException raised if mehtod contract is violated
   */
  public static double getMedian(double[] array) throws IllegalArgumentException
  {
    double result;

    //check the array
    if(array == null || array.length < 1)
      throw new IllegalArgumentException("the array must not be a null value and contain at least one element");

    //first sort the array
    sortArray(array);

    if(array.length%2 == 1)
      //the median is either the element in the middle of the array
      result = array[array.length/2];
    else
      //or the mean of the two elements in the middle of the array
      result = (array[array.length/2] + array[(array.length/2) - 1]) / 2;

    return result;
  }


  /**
   * Sorts all elements of the given array. The order of the elements in the
   * array will be changed. The algorithm in use is Bubble Sort due to the
   * simple implementation and a the assumption of a low number of elements
   * in the array.
   *
   * @param array double[] array of values; must not be a null value and must
   *                       contain at leat one element;
   */
  protected static void sortArray(double[] array)
  {
    double tmp;

    //bubble sort
    for (int i = 0; i < array.length; i++)
    {
      for(int j = array.length - 1; j > i; j--)
      {
        if(array[j] < array[j-1])
        {
          //exchange
          tmp = array[j-1];
          array[j-1] = array[j];
          array[j] = tmp;
        }
      }
    }
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
    return FluctuationPattern.class.getName().hashCode();
  }


  /**
   * Returns the feature extractors name.
   *
   * @return String name of this feature extractor
   */
  public String toString()
  {
    return "Fluctuation Pattern";
  }
}
