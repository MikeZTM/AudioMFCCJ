package comirva.audio.extraction;

import java.io.File;
import java.util.Iterator;
import javax.swing.JLabel;
import comirva.Workspace;
import comirva.data.DataMatrix;
import comirva.audio.feature.FluctuationPattern;


/**
 * <b>Fluctuation Pattern Extraction Thread</b>
 *
 * <p>Description: </p>
 * Represents a batch job for extracting <i>Fluctuation Patterns</i> form audio
 * files.
 *
 * @see comirva.audio.feature.FluctuationPattern
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class FluctuationPatternExtractionThread extends AudioFeatureExtractionThread
{

    /**
     * Constructs a <code>FluctuationPatternExtractionThread</code>.
     *
     * @param files File[] the audio files to process
     * @param ws Workspace the workspace object to store the result in
     * @param statusBar JLabel the status bar to show some progress information
     */
    public FluctuationPatternExtractionThread(File[] files, Workspace ws, JLabel statusBar)
  {
    super(new FluctuationPatternExtractor(), files, ws, statusBar);
  }


  /**
   * This template method creates a <code>DataMatrix</code> containing the
   * <i>Fluctuation Patterns</i> represented as row vectors of the matrix.
   * Each <i>Fluctuation Pattern</i> can be interpreted as a point in a
   * n-dimensional space.
   */
  protected void createDataMatrix()
  {
    // DataMatrix to hold FPs of repository
    DataMatrix dmFP = new DataMatrix("FPs");

    Iterator iter = features.iterator();

    while(iter.hasNext())
    {
      FluctuationPattern fp = (FluctuationPattern) iter.next();

      //convert fp to a vector for the DataMatrix
      double[] data = fp.getAsArray();
      for (int j = 0; j < data.length; j++)
        dmFP.addValue(new Double(data[j]));

      //add to FP matrix
      dmFP.startNewRow();
    }

    dmFP.removeLastAddedElement();
    // add dimension of matrix to its name
    dmFP.setName("FPs ("+dmFP.getNumberOfRows()+"x"+dmFP.getNumberOfColumns()+")");
    // add matrix to UI
    ws.addMatrix(dmFP, dmFP.getName());
    //add meta data to UI
    ws.addMetaData(meta, "tracks for FPs (" + meta.size() + ")");
  }
}
