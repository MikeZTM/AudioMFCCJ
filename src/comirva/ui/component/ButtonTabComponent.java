package comirva.ui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

import comirva.MainUI;

/**
 * Component to be used as tabComponent;
 * Contains a JLabel to show the text,
 * a JTextField replacing the JLabel in edit mode
 * which allows the user to enter the new title and
 * a JButton to close the tab it belongs to 
 * 
 * @author Florian Marchl
 * @version 1.0
 */ 
@SuppressWarnings("serial")
public class ButtonTabComponent extends JPanel {

	/** path to icon files */
	private static final String PATH = "ui/component/";
	
	/** reference to the tabbed pane this component belongs to */
    private final JTabbedPane pane;    // used for reading tab title!
    /** reference to main UI for tab removing */
    private final MainUI adaptee;	// used for calling event methods (closing tabs, etc)
    /** references the list of tab names from the workspace */
    private final Vector<String> nameList;
    /** the label for the title text */
    JLabel nameLabel = null;		// must be public for hiding while being edited

    /** Constructor 
     * @param pane the tabbed pane this tab belongs to
     */
    public ButtonTabComponent(final JTabbedPane pane, final MainUI adaptee, final Vector<String> nameList) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        this.adaptee = adaptee;
        this.nameList = nameList;
        setOpaque(false);
        
        // make JLabel read titles from JTabbedPane
        nameLabel = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };        
        add(nameLabel);
        //add more space between the label and the button
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        // add edit button
        JPanel edit = new TabMenu();
        add(edit);
        // tab button 
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        
    }

    /** The tab name editor class:<br>
     * In normal mode a JLabel displays the title of the tab and a button
     * showing an "edit" icon (pencil). If this icon is clicked both label
     * and button are replaced by a text field where the user can enter the
     * new text. Key "ESC" cancels editing, "ENTER" commits changes. The state
     * is switched back to normal mode in both cases.
     * 
     * @author Florian Marchl
     * @version 1.0
     */
    private class TabMenu extends JPanel {
    	
    	/** The text field which lets the user edit the tab name 
    	 * @author Florian Marchl
    	 * @version 1.0
    	 */
    	private class TabMenuEditor extends JTextField implements KeyListener, FocusListener {
    		
    		/** constructor 
    		 * @param the number of columns to use to calculate the preferred
    		 *	 width;	if columns is set to zero, the preferred width will 
    		 *	 be whatever naturally results from the component implementation
    		 * @see JTextField(int)
    		 */
    		public TabMenuEditor(int columns) {
    			super(columns);
    			addKeyListener(this);
    			addFocusListener(this);
    		}
    		
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					reset(true);
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					reset(false);
				}
			}

			public void keyReleased(KeyEvent e) { /* noop */ }

			public void keyTyped(KeyEvent e) { /* noop */ }

			public void focusGained(FocusEvent e) {
				this.selectAll();
			}

			public void focusLost(FocusEvent e) {
				// quit editing on focus loss
				reset(false);
			}    		
    	}    	
    	
    	/** The "button" (in fact it's a <code>JLabel</code>) which shows
    	 *  an "edit" icon and replaces the title label and itself by a
    	 *  textfield if it has been clicked.
    	 * @author Florian Marchl
    	 * @version 1.0
    	 */
    	private class TabMenuLabel extends JLabel implements MouseListener {

    		/** constructor */
    		public TabMenuLabel() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("edit tab caption");
                setIcon(new ImageIcon(MainUI.class.getResource(PATH + "edit.gif")));	// pencil icon
                this.setHorizontalAlignment(JLabel.CENTER);
                //Make it transparent
                setOpaque(false);
                //No need to be focusable
                setFocusable(false);                
                addMouseListener(this);                
    		}

			public void mouseClicked(MouseEvent e) {
				// hide label and copy text to text field
				nameLabel.setVisible(false);
				editor.setText(nameLabel.getText());
				editor.setColumns(editor.getText().length());
				editor.requestFocus();
				editor.selectAll();
				// replace label by text field
				label.setVisible(false);
				editor.setVisible(true);
				this.updateUI();
			}

			public void mouseEntered(MouseEvent e) {
				// display border if mouse entered this
	            label.setBorder(etched);			
	            label.setForeground(Color.BLUE);
	            label.updateUI();
				this.updateUI();
			}

			public void mouseExited(MouseEvent e) {
				// hide border if mouse exited this
				label.setBorder(none);
				label.setForeground(SystemColor.controlText);
			}

			public void mousePressed(MouseEvent e) { /* noop */	}

			public void mouseReleased(MouseEvent e) { /* noop */ }    		
    	}
    	
    	/** the label as edit "button" */
    	JLabel label = new TabMenuLabel();
    	/** the real editor text field */
    	JTextField editor = new TabMenuEditor(5);
    	
    	/** etched border */
    	Border etched = BorderFactory.createEtchedBorder();
    	/** non-edged border (just to reserve the space for the etched border) */
    	Border none = BorderFactory.createEmptyBorder(2,2,2,2);
    	
    	/** constructor for the edit button */
    	public TabMenu() {
    		super(new FlowLayout(FlowLayout.LEFT));
    		label.setOpaque(false);
    		label.setBorder(none);
    		label.setVisible(true);
    		editor.setVisible(false);
    		this.setOpaque(false);
    		this.add(label);
    		this.add(editor);
    		this.updateUI();
    	}
    	
    	/** reset from editing to normal mode
    	 * 	 @param rename <code>true</code> if tab name should be changed,
    	 * 		<code>false</code> otherwise
    	 */
    	public void reset(boolean rename) {
    		if (rename) {
    			// apply title change
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    pane.setTitleAt(i, editor.getText());	// replace tab text
                    if (i >= 2)  
                    	nameList.set(i-2, editor.getText());	// replace name in workspace
                }
    		}
    		// replace text field by updated label
    		editor.setVisible(false);
    		label.setVisible(true);
    		nameLabel.setVisible(true);
    		this.updateUI();
    	}	
    }
    
    /** the class for the closing button */
    private class TabButton extends JButton implements ActionListener {
    	
    	/** constructor */
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

		public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
            	// close tab by calling adaptee's method
            	adaptee.toolbarTabRemove_actionPerformed(e);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
    
    /** a mouse listener for the close button */
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}


