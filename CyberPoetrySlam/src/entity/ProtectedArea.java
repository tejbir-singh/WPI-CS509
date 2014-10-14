package entity;

import java.util.ArrayList;

public class ProtectedArea {
	ArrayList<Word> words;
	ArrayList<Poem> poems;
	public static ProtectedArea instance;
	
	private ProtectedArea() {
		words = new ArrayList<Word>();
		poems = new ArrayList<Poem>();
	}
	
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
	
	// false if unsuccessful; true otherwise 
	public boolean moveEntity(Entity e, int x, int y) {
		if (doesIntersect(e)) {				// invalid
			return false;
		}
		e.x = x;
		e.y = y;
		return true;
	}
	
	public Word getWord(String value) {
		for (Word w : words) {
			if (w.value.equals(value)) {
				return w;
			}
		}
		return null;
	}
	
	private boolean doesIntersect(Entity e) {
		// compare it based on location and area of e
		for (Word word : words) {		// lazy; maybe do cleaner
			if (word.intersect(e.x, e.y) == true) {
				return true;
			}
		}
		
		for (Poem poem : poems) {		// lazy; maybe do cleaner
			if (poem.intersect(e.x, e.y) == true) {
				return true;
			}
		}
		/* For use in Word and Poem; preliminary version which does not return direction
		 intersect(Entity e) {
		 	if ((((e.x >= this.x) && (e.x < (this.x + this.width))) || ((this.x >= e.x) && (this.x < (e.x + e.width)))) &&
				  	(((e.y >= this.y) && (e.y < (this.y + this.height))) || ((this.y >= e.y) && (this.y < (e.y + e.height))))) {
				return true;
			}
		 */
		return false;
	}
	
}
