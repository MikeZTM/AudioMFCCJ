/*
 * Created on 28.10.2004
 */
package comirva.mlearn;

import comirva.config.SOMConfig;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * This class implements a thread for
 * training the SOM. Since this may take
 * a while, it would freeze the program if
 * not executed as thread.
 * 
 * @author Markus Schedl
 */
public class SOMTrainingThread extends Thread {
	// the SOM to be trained
	SOM som;
	// the configuration for the SOM to be trained
	SOMConfig somCfg;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// progressbar of MainUI-instance for indication working action
	private JProgressBar progressBar;
	
	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 * 
	 * @param som	the SOM that should be trained
	 */
	public SOMTrainingThread(SOM som) {
		this.som = som;
	}
	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 *
	 * @param som	the SOM that should be trained
	 * @param somCfg	the configuration of the SOM
	 * @param sb	a JLabel representing the status bar (for updating the UI while training) 
	 */
	public SOMTrainingThread(SOM som, SOMConfig somCfg, JLabel sb) {
		this.som = som;
		this.somCfg = somCfg;
		this.statusBar = sb;
	}

	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 *
	 * @param som		the SOM that should be trained
	 * @param somCfg	the configuration of the SOM
	 * @param sb		a JLabel representing the status bar
	 * @param pb		a JProgressBar representing the progress bar
	 * @see #SOMTrainingThread(SOM, SOMConfig, JLabel)
	 */
	public SOMTrainingThread(SOM som, SOMConfig somCfg, JLabel sb, JProgressBar pb) {
		this(som, somCfg, sb);
		this.progressBar = pb;
	}
	
	/**
	 * This method is called when the thread is started.
	 * The SOM is initialized and trained and the user 
	 * is informed when training is finished (if the status bar was specified).
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (progressBar != null)
			progressBar.setIndeterminate(true);
		// init SOM
		if (statusBar != null)
			statusBar.setText("Initializing the SOM");
		if (somCfg != null && somCfg.getInitMethod() != -1) 
			som.init(somCfg.getInitMethod());
		if (somCfg != null)
			som.setCircular(somCfg.isCircular());
		// train SOM
		if (statusBar != null)
			statusBar.setText("Training the SOM");
		if (somCfg != null && somCfg.getTrainingMethod() != -1)
			som.train(somCfg.getTrainingMethod(), somCfg.getTrainingLength());
		// create VoronoiSet of SOM
		if (statusBar != null)
			statusBar.setText("Calculating Voronoi set");
		som.createVoronoiSet();
		if (statusBar != null)
			statusBar.setText("SOM-Training finished");
		if (progressBar != null)
			progressBar.setIndeterminate(false);
	}
}
