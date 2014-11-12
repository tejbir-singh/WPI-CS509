package cps.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Stack;

public class GameManager {
	private ProtectedArea pa;
	private UnprotectedArea ua;
	private Stack<Manipulation> manipulations;
	private Stack<Manipulation> prevUndos;
	private static GameManager instance;
	private final String wordBank = "words.txt";
	private Entity selected = null;
	ReturnIndex selectedidx = null;
	public static final int AREA_DIVIDER = 320;
	public static final int PROTECTED_AREA_X = 0;
	public static final int PROTECTED_AREA_Y = 0;
	public static final int PROTECTED_AREA_WIDTH = 650;
	public static final int PROTECTED_AREA_HEIGHT = 550;
	
	
	/**
	 * Constructor.
	 */
	private GameManager() {
		pa = ProtectedArea.getInstance();
		ua = UnprotectedArea.getInstance();
		manipulations = new Stack<Manipulation>();
		prevUndos = new Stack<Manipulation>();
		// populate the UnprotectedArea with the Words specified in words.txt
		// for now we assume that Words will be stored with a type and value
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(wordBank));
			String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(",");
				// generate Words
				ua.add(new Word((int) Math.round(Math.random() * 600),
						(int) Math.round(Math.random() * 100) + AREA_DIVIDER + 20, 
								words[0].length() * 15, 15, Type.valueOf(words[1]), words[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * Reset Board to the state encoded by the Memento.
	 * @param m Memento to restore to
	 */
	public void restore(GameManagerMemento m) {
		pa = m.storedPa;
		ua = m.storedUa;
	}

	/**
	 * Retrieve the state from a GameManagerMemento.
	 * @return GameManagerMemento with the state
	 */
	public GameManagerMemento getState() {
		return new GameManagerMemento(pa, ua);
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
	
	/**
	 * Find a Word at a given x- and y-coordinate.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return Word at the location (null if not found)
	 */
	public Word findWord(int x, int y) {
		Word w = ua.getWord(x, y);
		if (w == null) {
			w = pa.getWord(x, y);
		}
		return w;
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

	public Entity getSelected() {
		return selected;
	}
	
	public void setSelected(Entity selected) {
		this.selected = selected;
	}
	
	public ReturnIndex getSelectedIdx() {
		return selectedidx;
	}
	
	public void setSelectedIdx(ReturnIndex selectedidx) {
		this.selectedidx = selectedidx;
	}
	
	public Stack<Manipulation> getManipulations() {
		return this.manipulations;
	}
	
	public Stack<Manipulation> getPrevUndos() {
		return this.prevUndos;
	}
}
