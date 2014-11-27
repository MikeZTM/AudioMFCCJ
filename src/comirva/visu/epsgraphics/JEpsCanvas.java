package comirva.visu.epsgraphics;

import javax.swing.JComponent;

/**
 * Implements the EpsGraphics2D in a JComponent so it can be used toegether with lightweight swing components.
 * @author    Florian Marchl
 * @version    1.0
 * @uml.dependency   supplier="at.jku.cp.epsgraphics.EpsGraphics2D"
 */

public class JEpsCanvas extends JComponent {

	/**	serial version UID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * eps graphics device. It can be accessed via a getter method
	 * @uml.property   name="epsGraphics"
	 */
	private EpsGraphics2D epsGraphics;
	
	public JEpsCanvas() {
		super();
		epsGraphics = new EpsGraphics2D();
	}

	/**
	 * return eps graphics device 
	 * @return  epsGraphics
	 * @uml.property  name="epsGraphics"
	 */
	public EpsGraphics2D getEpsGraphics() {
		return epsGraphics;
	}
	
}
