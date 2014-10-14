package entity;

import java.util.ArrayList;

public class ProtectedArea {
	ArrayList<Word> words;
	ArrayList<Poem> poems;
	public static ProtectedArea instance;
	
	/**
	 * Constructor.
	 */
	private ProtectedArea() {
		words = new ArrayList<Word>();
		poems = new ArrayList<Poem>();
	}
	
	/**
	 * Singleton implementation.
	 * @return the instance of the class
	 */
	public static ProtectedArea getInstance() {
		if (instance == null) {
			instance = new ProtectedArea();
		}
		return instance;
	}
	
	public boolean add(Word word) {
		return words.add(word);
	}
	
	public boolean remove(Word word) {
		int index = words.indexOf(word);
		if (index == -1) {							// word not found
			return false;
		}
		else {
			words.remove(index);
			return true;
		}
	}
	
	/**
	 * Move an Entity to the specified x and y coordinates if it is valid.
	 * @param e	Entity to move
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	public boolean moveEntity(Entity e, int x, int y) {
		Entity tmp;
		// create temporary copy for manipulation
		if (e instanceof Word) {
			tmp = new Word();
			tmp.x = x;
			tmp.y = y;
		}
		else {									// must be a poem
			tmp = new Poem();
			tmp.x = x;
			tmp.y = y;
		}
		
		tmp.x = x;
		tmp.y = y;
		if (doesIntersect(tmp)) {				// invalid move if so
			return false;
		}
		e.x = x;
		e.y = y;
		return true;
	}
	
	/**
	 * Get a Word based on its x and y position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return Word at given location (null if not found)
	 */
	public Word getWord(int x, int y) {
		for (Word w : words) {
			if (w.x == x && w.y == y) {
				return w;
			}
		}
		return null;
	}
	
	/**
	 * Check if an Entity is intersecting any others in the ProtectedArea.
	 * @param e Entity to check
	 * @return true if it intersects
	 */
	protected boolean doesIntersect(Entity e) {
		// compare e to each existing Entity location
		for (Word word : words) {
			if (e != word && word.intersect(e) == true) {
				return true;
			}
		}
		
		for (Poem poem : poems) {
			if (e != poem && poem.intersect(e) == true) {
				return true;
			}
		}
		
		return false;
	}
	
}
