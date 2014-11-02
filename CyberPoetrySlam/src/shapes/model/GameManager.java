package shapes.model;

import java.io.BufferedReader;
import java.io.FileReader;

public class GameManager {
	private ProtectedArea pa;
	private UnprotectedArea ua;
	private static GameManager instance;
	private final String wordBank = "words.txt";
	private Word selected = null;
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
		// populate the UnprotectedArea with the Words specified in words.txt
		// for now we assume that Words will be stored with a type and value
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(wordBank));
			String line;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(",");
				// generate Words (Will need to be fixed when we determine the
				// proper size Words should be)

				ua.add(new Word((int) Math.round(Math.random() * 600),
						(int) Math.round(Math.random() * (PROTECTED_AREA_HEIGHT - AREA_DIVIDER)) + AREA_DIVIDER, 
								words[0].length() * 15, 15, Type.valueOf(words[1]), words[0]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public Word getSelected() {
		return selected;
	}
	
	public void setSelected(Word selected) {
		this.selected = selected;
	}
}