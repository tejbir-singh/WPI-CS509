package shapes.model;

import java.io.Serializable;
import java.util.ArrayList;


public class UnprotectedArea implements Serializable {
	private static final long serialVersionUID = -9203701553091519628L;
	private static UnprotectedArea instance;
	ArrayList<Word> words;
	
	/**
	 * Constructor.
	 */
	private UnprotectedArea() {
		words = new ArrayList<Word>();
	}
	
	/**
	 * Singleton implementation.
	 * @return the UnprotectedArea instance
	 */
	public static UnprotectedArea getInstance() {
		if (instance == null) {
			instance = new UnprotectedArea();
		}
		return instance;
	}
	

	/**
	 * Add a Word to the UnprotectedArea.
	 * @param word Word to add
	 * @return true if successful
	 */
	public boolean add(Word word) {
		words.add(word);
		return true;
	}
	
	/**
	 * Remove a Word from the UnprotectedArea.
	 * @param word Word to remove
	 * @return true if successful
	 */
	public boolean remove(Word word) {
		int index = words.indexOf(word);
		if (index == -1) {							// word not found
			return false;
		}
		else {
			words.remove(index);
		}
		return true;
	}
	
	/**
	 * Search the UnprotectedArea for a list of Words based on an input string.
	 * @param str String to search for
	 * @return ArrayList of Words that match the criteria
	 */
	public ArrayList<Word> search(String str) {
		ArrayList<Word> validWords = new ArrayList<Word>();
		for (Word word : words) {
			if (word.value.contains(str)) {
				validWords.add(word);
			}
		}
		return validWords;
	}
	
	/**
	 * @param type Types 
	 * @return ArrayList of Words that match the criteria
	 */
	public ArrayList<Word> filter(ArrayList<Type> types) {
		ArrayList<Word> validWords = new ArrayList<Word>();
		for (Type t : types) {
			for (Word word : words) {
				if (word.type == t) {
					validWords.add(word);
				}
			}
		}
		return validWords;
	}
	
	/**
	 * Get a Word based on its x and y position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return Word at given location (null if not found)
	 */
	public Word getWord(int x, int y) {
		for (Word w : words) {
			// create a dummy; remove this later
			Word tmp = new Word(x, y, 0, 0, null, null);
			if (w.intersect(tmp)) {
				return w;
			}
		}
		return null;
	}
	
	/**
	 * Move a Word to the specified x and y coordinates if it is valid.
	 * @param e	Entity to move
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	public boolean moveEntity(Entity e, int x, int y) {
		e.x = x;
		e.y = y;
		return true;
	}
	
	// Getters and setters
	public ArrayList<Word> getWords() {
		return words;
	}

	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}
}
