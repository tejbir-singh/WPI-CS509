package cps.model;

import java.io.Serializable;

/**
 * Used to store the game state.
 * @author Devin
 *
 */
public class GameManagerMemento implements Serializable {
	protected ProtectedArea storedPa = ProtectedArea.getInstance();
	protected UnprotectedArea storedUa = UnprotectedArea.getInstance();
	
	/**
	 * Constructor.
	 * @param pa ProtectedArea
	 * @param ua UnprotectedArea
	 */
	public GameManagerMemento(ProtectedArea pa, UnprotectedArea ua) {
		this.storedPa = pa;
		this.storedUa = ua;
	}
	
	private static final long serialVersionUID = 5474513144228020460L;
	
}
