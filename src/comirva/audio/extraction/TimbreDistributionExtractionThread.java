package comirva.audio.extraction;

import java.io.File;
import javax.swing.JLabel;
import comirva.Workspace;
import comirva.audio.feature.TimbreDistribution;
import comirva.data.DataMatrix;

/**
 * <b>Timbre Distribution Extraction Thread</b>
 *
 * <p>Description: </p>
 * Represents a batch job for extracting the <i>Timbre Distribution</i> form
 * audio files.
 *
 * @see comirva.audio.feature.TimbreDistribution
 * @author Klaus Seyerlehner
 * @version 1.0
 */
public class TimbreDistributionExtractionThread extends AudioFeatureExtractionThread
{

  /**
   * Constructs a <code>TimbreDistributionExtractionThread</code>.
   *
   * @param files File[] the audio files to process
   * @param ws Workspace the workspace object to store the result in
   * @param statusBar JLabel the status bar to show some progress information
   */
  public TimbreDistributionExtractionThread(File[] files, Workspace ws, JLabel statusBar)
  {
    super(new TimbreDistributionExtractor(), files, ws, statusBar);
  }


  /**
   * This template method creates a <code>DataMatrix</code> containing the
   * distance matrix based on the trimbre distribution metric.
   */
  protected void createDataMatrix()
  {
    TimbreDistribution a, b;

    // DataMatrix to hold FPs of repository
    DataMatrix dm = new DataMatrix("MFCCs");

    //compute distance matrix
    for(int i = 0; i < features.size(); i++)
    {
      a = (TimbreDistribution) features.get(i);
      //compute new row
      for(int j = 0; j < features.size(); j++)
      {
        if(i==j)
          dm.addValue(new Double(1)); // TODO shouldn't the self-distance be zero?
        else if(j < i)
          dm.addValue(dm.getValueAtPos(j+1, i));
        else
        {
          b = (TimbreDistribution) features.get(j);
          dm.addValue(new Double(a.getDistance(b)));
        }
      }

      //new row
      dm.startNewRow();
    }

    dm.removeLastAddedElement();
    // add dimension of matrix to its name
    dm.setName("MFCCs ("+dm.getNumberOfRows()+"x"+dm.getNumberOfColumns()+")");
    // add matrix to UI
    ws.addMatrix(dm, dm.getName());
    //add meta data to UI
    ws.addMetaData(meta, "tracks for MFCCs (" + meta.size() + ")");
  }
}
