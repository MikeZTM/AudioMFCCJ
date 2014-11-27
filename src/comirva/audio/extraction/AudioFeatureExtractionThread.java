package comirva.audio.extraction;

import java.io.File;
import javax.swing.JLabel;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import comirva.Workspace;
import comirva.audio.feature.AudioFeature;

/**
 * This thread is intended to do a feature extraction batch job. Each file
 * handed to the constructor will be process the given feature extractor. The
 * priority of the thread is low, such that it may run in the background. Any
 * kind of exception during the batch process are reported to the standard
 * log and ignored.<br>
 * <br>
 * This class is abstract because it has one template method, which should
 * create a <code>DataMatrix</code> object by using the extracted features.
 * Depending on the feature a specialised feature extraction thread may for
 * example return a similarity matrix or a matrix containing vectors.
 *
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public abstract class AudioFeatureExtractionThread extends Thread
{
  File[] files;
  AudioFeatureExtractor featureExtractor;
  Vector meta = new Vector();
  List features;
  Workspace ws;
  JLabel statusBar;			// the status bar of CoMIRVA's GUI


  /**
   * Creates a new audio feature extraction thread.
   *
   * @param featureExtractor AudioFeatureExtractor the extractor, defining the
   *                                               extraction process
   * @param files File[] the audio files to process
   * @param ws Workspace the workspace object to store the result in
   * @param statusBar JLabel the status bar to show some progress information
   */
  protected AudioFeatureExtractionThread(AudioFeatureExtractor featureExtractor, File[] files, Workspace ws, JLabel statusBar)
  {
    //check input
    if (files == null || featureExtractor == null)
      throw new IllegalArgumentException(
          "featureExtractor and files must not be null arrays");

    for (int i = 0; i < files.length; i++)
    {
      if (files[i] == null)
        throw new IllegalArgumentException(
            "the files array must not contain any null value");
    }

    //set fields
    this.featureExtractor = featureExtractor;
    this.files = files;
    this.ws = ws;
    this.setPriority(Thread.MIN_PRIORITY);
    this.features = new ArrayList(files.length);
    this.statusBar = statusBar;
  }


  /**
   * Starts processing the feature extraction batch job.
   */
  public void run()
  {
    AudioFeature audioFeature = null;
    AudioInputStream in;


    // determine name of the feature which is calculated
    String featureName = "";
    if (featureExtractor instanceof FluctuationPatternExtractor)
    	featureName = "FP";


    //process all files
    for(int i = 0; i < files.length; i++)
    {
       try
       {

    	 //open audio file as stream
         File is = files[i];

         // user info
         this.statusBar.setText("<html>Calculating audio features " + featureName + " for <strong>" + is.getCanonicalPath() + "</strong></html>");

         //extract the feature from the audio file
         in = AudioSystem.getAudioInputStream(is);
         audioFeature = (AudioFeature) featureExtractor.calculate(in);
         in.close();

    	 // user info
    	 this.statusBar.setText("<html>Finished calculation of audio features " + featureName + " for <strong>" + is.getCanonicalPath() + "</strong></html>");

         //add to feature list
         features.add(audioFeature);

         //cool down (cpu overheating protection)
         Thread.currentThread().sleep(3000);

         // file name to the meta data vector
         meta.add(files[i].getCanonicalPath());
       }
       catch (Exception e)
       {
         System.out.println("An error occurred while processing:'" + files[i].getAbsolutePath() + "';");
         e.printStackTrace();
       }

    }

    //create specific data matrix
    createDataMatrix();

    // user info
    this.statusBar.setText("<html>Audio features " + featureName + " have been calculated.</html>");
  }


  /**
   * Template method creating the audio feature specific <code>DataMatrix</code>.
   */
  abstract protected void createDataMatrix();
}
