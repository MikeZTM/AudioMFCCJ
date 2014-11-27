/*
 * Created on 15.11.2006
 */
package comirva.util;

import comirva.config.PCAConfig;
import comirva.data.DataMatrix;
import comirva.Workspace;

import javax.swing.JLabel;


/**
 * This class implements a thread for
 * calculating a PCA. Since this may take
 * a while, it would freeze the program if
 * not executed as thread.
 * 
 * @author Markus Schedl
 */
public class PCACalculationThread extends Thread {
	// the input data for the PCA to be trained
	private DataMatrix inputDM;
	// the configuration for the PCA to be calculated
	private PCAConfig pcaCfg;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// the Workspace of CoMIRVA
	private Workspace workspace;
	
	/**
	 * Creates a PCACalculationThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 * 
	 * @param dm 		a DataMatrix holding the input data
	 * @param pcaCfg	a PCAConfig representing the configuration for the PCA-calculation
	 */
	public PCACalculationThread(DataMatrix dm, PCAConfig pcaCfg) {
		this.inputDM = dm;
		this.pcaCfg = pcaCfg;
	}
	
	/**
	 * Creates a PCACalculationThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 * 
	 * @param dm 		a DataMatrix holding the input data
	 * @param pcaCfg	a PCAConfig representing the configuration for the PCA-calculation
	 * @param sb		a JLabel representing the status bar of CoMIRVA's UI
	 * @param ws		the Workspace of CoMIRVA (data matrices and meta-data vectors)
	 */
	public PCACalculationThread(DataMatrix dm, PCAConfig pcaCfg, JLabel sb, Workspace ws) {
		this.inputDM = dm;
		this.pcaCfg = pcaCfg;
		this.statusBar = sb;
		this.workspace = ws;
	}

	
	/**
	 * This method is called when the thread is started.
	 * The PCA is initialized and trained and the user 
	 * is informed when training is finished (if the status bar was specified).
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (pcaCfg != null && workspace != null) {
			if (statusBar != null)
				statusBar.setText("Calculating a PCA and data projection onto "+pcaCfg.getUsedEigenvectors()+ " dimensions.");
			// calculate PCA
			PCA pca = new PCA(this.inputDM, this.pcaCfg.getUsedEigenvectors());
			// get resulting matrices
			DataMatrix pcaCompressed = pca.getPCATransformedDataAsDataMatrix();
			DataMatrix eigVal = pca.getEigenvaluesAsDataMatrix();
			DataMatrix eigVec = pca.getEigenvectorsAsDataMatrix();
			DataMatrix means = pca.getMeansAsDataMatrix();
			// add PCA-data to UI
			workspace.addMatrix(pcaCompressed, pcaCompressed.getName()+" (" + pcaCompressed.getNumberOfRows() + "x"+pcaCompressed.getNumberOfColumns()+")");
			workspace.addMatrix(eigVec, eigVec.getName()+" (" + eigVec.getNumberOfRows() + "x"+eigVec.getNumberOfColumns()+")");
			workspace.addMatrix(eigVal, eigVal.getName()+" (" + eigVal.getNumberOfRows() + "x"+eigVal.getNumberOfColumns()+")");
			workspace.addMatrix(means, means.getName()+" (" + means.getNumberOfRows() + "x"+means.getNumberOfColumns()+")");
			// inform user
			if (statusBar != null)
				statusBar.setText("Calculation of PCA and data projection finished.");
		}
	}
}
