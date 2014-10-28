package entity;

import java.io.Serializable;

public class GameManagerMemento implements Serializable {
	protected ProtectedArea storedPa = ProtectedArea.getInstance();
	protected UnprotectedArea storedUa = UnprotectedArea.getInstance();
	
	public GameManagerMemento(ProtectedArea pa, UnprotectedArea ua) {
		this.storedPa = pa;
		this.storedUa = ua;	
	}
	
	private static final long serialVersionUID = 5474513144228020460L;
	
}
