package comirva.mlearn;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import comirva.config.GHSOMConfig;

/**
 * Thread for training a GHSOM
 */
public class GHSOMTrainingThread extends Thread {

	// the GHSOM to be trained
	GHSOM ghsom;
	// the configuration for the GHSOM to be trained
	GHSOMConfig ghsomCfg;
	// the label representing the status bar of the calling MainUI-instance (for updating the status bar)
	private JLabel statusBar;
	// progressbar indication working action
	private JProgressBar progressBar;
	
	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 * 
	 * @param som	the SOM that should be trained
	 */
	public GHSOMTrainingThread(GHSOM som) {
		this.ghsom = som;
	}
	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized.
	 *
	 * @param som	the SOM that should be trained
	 * @param somCfg	the configuration of the SOM
	 * @param sb	a JLabel representing the status bar (for updating the UI while training) 
	 */
	public GHSOMTrainingThread(GHSOM som, GHSOMConfig ghsomCfg, JLabel sb) {
		this.ghsom = som;
		this.ghsomCfg = ghsomCfg;
		this.statusBar = sb;
	}

	/**
	 * Creates a SOMTrainingThread for training the SOM <code>som</code> which has already
	 * been initialized. During calculation the progress bar will be set to indeterminate
	 * 
	 * @param som		the SOM that should be trained
	 * @param ghsomCfg	the configuration fo the SOM
	 * @param sb		a JLabel representing the status bar
	 * @param pb		a JProgressBar representing the progress bar (for progress indication)
	 * 
	 * @see #GHSOMTrainingThread(GHSOM, GHSOMConfig, JLabel)
	 */
	public GHSOMTrainingThread(GHSOM som, GHSOMConfig ghsomCfg, JLabel sb, JProgressBar pb) {
		this(som, ghsomCfg, sb);
		this.progressBar = pb;
	}
	
	/**
	 * This method is called when the thread is started.
	 * The SOM is initialized and trained and the user 
	 * is informed when training is finished (if the status bar was specified).
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// animate progressbar
		if (progressBar != null)
			progressBar.setIndeterminate(true);
		// init SOM
		if (statusBar != null)
			statusBar.setText("Initializing the GHSOM");
		if (ghsomCfg != null) {
			ghsom.setGrowThreshold(ghsomCfg.getGrowThreshold());
			ghsom.setExpandThreshold(ghsomCfg.getExpandThreshold());
			ghsom.setInitMethod(ghsomCfg.getInitMethod());
			ghsom.setInitNumberOfColumns(ghsomCfg.getMapUnitsInColumn());
			ghsom.setInitNumberOfRows(ghsomCfg.getMapUnitsInRow());
			ghsom.setCircular(ghsomCfg.isCircular());
			ghsom.setOnlyFirstCircular(ghsomCfg.isOnlyFirstCircular());
			ghsom.setOrientated(ghsomCfg.isOrientated());
			if(ghsomCfg.getMaxSize() != GHSOM.NA_MAX_SIZE)
				ghsom.setMaxSize(ghsomCfg.getMaxSize());
			if(ghsomCfg.getMaxDepth() != GHSOM.NA_MAX_DEPTH)
				ghsom.setMaxDepth(ghsomCfg.getMaxDepth());
		}
		// train SOM
		if (statusBar != null)
			statusBar.setText("Training the GHSOM");
		ghsom.train(SOM.TRAIN_SEQ, ghsomCfg.getTrainingLength());

		if (statusBar != null)
			statusBar.setText("GHSOM-Training finished");
		// stop progress bar animation
		if (progressBar != null)
			progressBar.setIndeterminate(false);		
	}
}
