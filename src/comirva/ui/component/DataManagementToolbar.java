package comirva.ui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import comirva.MainUI;

public class DataManagementToolbar extends JToolBar {
	
	/** the action for tab renaming */
	private class ToolbarRenameTab_Action implements ActionListener {
		MainUI adaptee;
		
		/** constructor
		 * @param adaptee the main UI
		 */
		public ToolbarRenameTab_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.toolbarTabRename_actionPerformed(actionEvent);
		}
	}
	
	/** the action for tab removing */
	private class ToolbarRemoveTab_Action implements ActionListener {
		MainUI adaptee;
		
		/** constructor
		 * @param adaptee the main UI
		 */
		public ToolbarRemoveTab_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.toolbarTabRemove_actionPerformed(actionEvent);
		}
	}
	
	/** the action for item removing */
	private class ToolbarDeleteItem_Action implements ActionListener{
		MainUI adaptee;
		
		public ToolbarDeleteItem_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.menuFileDeleteSelectedItem_actionPerformed(actionEvent);
		}
	}
	
	/** the action for option */
	private class ToolbarOptions_Action implements ActionListener {
		MainUI adaptee;
		
		/** constructor
		 * @param adaptee the main UI
		 */
		public ToolbarOptions_Action(MainUI adaptee) {
			this.adaptee = adaptee;
		}
		
		public void actionPerformed(ActionEvent actionEvent) {
			// call adaptee's method
			adaptee.toolbarOptions_actionPerformed(actionEvent);
		}
	}
	
	/** path to icon files */
	private static final String PATH = "ui/component/";
	
	/** icon for tab renaming */
	private static final ImageIcon iconRenameTab = new ImageIcon(MainUI.class.getResource(PATH + "ren_tab.gif"));
	/** icon for tab removing */
	private static final ImageIcon iconRemoveTab = new ImageIcon(MainUI.class.getResource(PATH + "rem_tab.gif"));
	/** icon for delete list item */
	private static final ImageIcon iconDeleteItem = new ImageIcon(MainUI.class.getResource(PATH + "cell_delete.gif"));
	/** icon for tab options */
	private static final ImageIcon iconOptions = new ImageIcon(MainUI.class.getResource(PATH + "opt.gif"));
	
	/** the toolbar botton for tab renaming */
	protected JButton buttonRenameTab = new JButton(iconRenameTab);
	/** the toolbar button for tab removing */
	protected JButton buttonRemoveTab = new JButton(iconRemoveTab);
	/** the toolbar button for deleting list item */
	protected JButton buttonDeleteItem = new JButton(iconDeleteItem);
	/** the toolbar button for tab options */
	protected JButton buttonOptions = new JButton(iconOptions);
	
	/** construct toolbar */
	public DataManagementToolbar(MainUI adaptee) {
		// configure buttons
		this.setName("Data Management Tools");
		buttonRenameTab.setToolTipText("Rename current selected Tab");
		buttonRemoveTab.setToolTipText("Remove current selected Tab");
		buttonDeleteItem.setToolTipText("Remove selected item from list");
		buttonOptions.setToolTipText("Configure data management panel");
		
		// apply actions to buttons
		buttonRenameTab.addActionListener(new ToolbarRenameTab_Action(adaptee));
		buttonRemoveTab.addActionListener(new ToolbarRemoveTab_Action(adaptee));
		buttonDeleteItem.addActionListener(new ToolbarDeleteItem_Action(adaptee));
		buttonOptions.addActionListener(new ToolbarOptions_Action(adaptee));
		
		// add buttons to toolbar
		add(buttonRenameTab);
		add(buttonRemoveTab);
		this.addSeparator();
		add(buttonDeleteItem);
		this.addSeparator();
		add(buttonOptions);
	}
}
