package shapes.model;

public class GameManager {
	private ProtectedArea pa;
	private UnprotectedArea ua;
	private static GameManager instance;
	
	/**
	 * Constructor.
	 */
	private GameManager() {
		pa = ProtectedArea.getInstance();
		ua = UnprotectedArea.getInstance();
	}
	
	/**
	 * Reset Board to the state encoded by the Memento.
	 * @param m Memento to restore to
	 */
	public void restore(GameManagerMemento m) {
		pa = m.storedPa;
		ua = m.storedUa;
	}

	public GameManagerMemento getState() {
		return new GameManagerMemento(pa, ua);
	}

	/**
	 * Singleton pattern implementation.
	 * @return the instance of GameManager
	 */
	public static GameManager getInstance() {
		if (instance == null) {
			instance = new GameManager();
		}
		return instance;
	}
		
	/**
	 * Add a Word to the ProtectedArea and remove it from the UnprotectedArea.
	 * @param word Word to protect
	 * @return true if successful
	 */
	public boolean protect(Word word) {
		return (ua.remove(word) && pa.add(word));
	}
	
	/**
	 * Add a Word to the UnrotectedArea and remove it from the ProtectedArea.
	 * @param word Word to unprotect
	 * @return true if successful
	 */
	public boolean release(Word word) {
		return (pa.remove(word) && ua.add(word));
	}

	// Getters and setters 
	public ProtectedArea getPa() {
		return pa;
	}

	public void setPa(ProtectedArea pa) {
		this.pa = pa;
	}

	public UnprotectedArea getUa() {
		return ua;
	}

	public void setUa(UnprotectedArea ua) {
		this.ua = ua;
	}
}