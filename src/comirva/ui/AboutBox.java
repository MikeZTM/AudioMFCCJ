/*
 * Created on 21.10.2004
 */
package comirva.ui;

import comirva.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements an about box used to show details of CoMIRA.
 * 
 * @author Markus Schedl
 */
public class AboutBox extends JDialog implements ActionListener {
	// variables to display text
    private String program = "<html>Collection of Music Information Retrieval<br>and Visualization Applications</html>";
    private String version = "<html>Version " + MainUI.VERSION + ", " + MainUI.DATE +  "</html>";
    private String author = "<html>Author: Markus Schedl (markus.schedl@jku.at)<br>Department of Computational Perception (<a href=\"http://www.cp.jku.at\">http://www.cp.jku.at</a>)<br>Johannes Kepler University of Linz</html>";
    private String contributors = "<html>Contributors:<br>P. Knees, K. Seyerlehner, M. Straub, T. Pohle, M. Dopler, F. Marchl</html>";
    private String comments = "<html>This program is licensed under the GNU General Public License.<br><a href=\"http://www.gnu.org/copyleft/gpl.html\">http://www.gnu.org/copyleft/gpl.html</a></html>";
    // UI-elements
    private JButton btnClose = new JButton();
    private JLabel labelProgram = new JLabel();
    private JLabel labelVersion = new JLabel();
    private JLabel labelAuthor = new JLabel();
    private JLabel labelContributors = new JLabel();
    private JLabel labelComments = new JLabel();
    private JLabel labelLogo = new JLabel(new ImageIcon(AboutBox.class.getResource("AboutBox_Logo.png")), JLabel.LEFT);
    private JPanel panel = new JPanel();
    private GridLayout gridLayout = new GridLayout();
    
    /**
     * Creates a new instance of the about box and initializes it.
     * 
     * @param parent	the Frame of the parent window where the about box should be displayed 
     */
    public AboutBox(Frame parent) {
        super(parent);
        try {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initAboutBox();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    /**
     * Creates a new instance of the about box. 
     */
    public AboutBox() {
        this(null);
    }
    
    /**
     * <p> Initializes the about box by displaying all labels, setting title, creating an "OK"-button
     * and assigning an ActionListener.</p>
     * To arrange the elements of the about box, a GridLayout is used.  
     *  
     */
    protected void initAboutBox()  {
    	// assign texts to labels
    	this.setTitle("About");
    	labelProgram.setText(program);
    	labelVersion.setText(version);
    	labelAuthor.setText(author);
    	labelContributors.setText(contributors);
    	labelComments.setText(comments);
    	// set label font
    	Font infoFont = new Font("Arial", Font.PLAIN, 14);
    	Font infoBoldFont = new Font("Arial", Font.BOLD, 15);
    	labelProgram.setFont(infoBoldFont);
    	labelVersion.setFont(infoFont);
    	labelAuthor.setFont(infoFont);
    	labelContributors.setFont(infoFont);
    	labelComments.setFont(infoFont);
    	// assign text and ActionListener to "OK"-button
    	btnClose.setText("OK");
    	btnClose.addActionListener(this);
    	// set "OK"-button as default
    	this.getRootPane().setDefaultButton(btnClose);
    	// init grid layout
    	gridLayout.setColumns(1);
    	gridLayout.setRows(7);
    	gridLayout.setVgap(0);
    	// assign layout
    	panel.setLayout(gridLayout);
    	panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    	// add UI-elements
    	getContentPane().add(panel);
    	panel.add(labelLogo);
    	panel.add(labelProgram);
    	panel.add(labelVersion);
    	panel.add(labelAuthor);
    	panel.add(labelContributors);
    	panel.add(labelComments);
    	panel.add(btnClose);
    	// set default look and feel
    	this.setUndecorated(true);
	    this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    	this.setResizable(false);
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnClose)
            dispose();
    }
	
}
