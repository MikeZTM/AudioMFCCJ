package comirva.audio.extraction;

import java.io.File;

import javax.swing.JLabel;

import comirva.Workspace;
import comirva.data.DataMatrix;
import comirva.audio.feature.MandelEllis;

public class MandelEllisExtractionThread extends AudioFeatureExtractionThread {

	public MandelEllisExtractionThread(File[] files, Workspace ws, JLabel statusBar) {
		super(new MandelEllisExtractor(0,0,20), files, ws, statusBar);
	}


	protected void createDataMatrix() {
		MandelEllis a, b;

		// DataMatrix to hold FPs of repository
		DataMatrix dm = new DataMatrix("Mandel Ellis");

		//compute distance matrix
		for(int i = 0; i < features.size(); i++)
		{
			a = (MandelEllis) features.get(i);
			//compute new row
			for(int j = 0; j < features.size(); j++)
			{
				if(i==j)
					dm.addValue(new Double(1));
				else if(j < i)
					dm.addValue(dm.getValueAtPos(j+1, i));
				else
				{
					b = (MandelEllis) features.get(j);
					dm.addValue(new Double(a.getDistance(b)));
				}
			}

			//new row
			dm.startNewRow();
		}

		dm.removeLastAddedElement();
		// add dimension of matrix to its name
		dm.setName("GMM-MEs ("+dm.getNumberOfRows()+"x"+dm.getNumberOfColumns()+")");
		// add matrix to UI
		ws.addMatrix(dm, dm.getName());
		//add meta data to UI
		ws.addMetaData(meta, "tracks for GMM-MEs (" + meta.size() + ")");
	}

}
