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
	
	public Word getWord(String value) {
		for (Word w : words) {
			if (w.value.equals(value)) {
				return w;
			}
		}
		return null;
	}
	
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
