package entity;

import java.util.ArrayList;


public class UnprotectedArea {
	public static UnprotectedArea instance;
	ArrayList<Word> words;
	
	private UnprotectedArea() {
		
	}
	
	public UnprotectedArea getInstance() {
		if (instance == null) {
			instance = new UnprotectedArea();
		}
		return instance;
	}
	
	public boolean addToUnprotected(Word word) {
		words.add(word);
		return true;
	}
	
	public boolean removeFromUnprotected(Word word) {
		int index = words.indexOf(word);
		if (index == -1) {							// word not found
			// do something
		}
		else {
			words.remove(index);
		}
		return true;
	}
	
	public ArrayList<Word> searchUnprotectedArea(String str) {
		ArrayList<Word> validWords = new ArrayList<Word>();
		for (Word word : words) {
			if (word.value.contains(str)) {
				validWords.add(word);
			}
		}
		return validWords;
	}
	
	public ArrayList<Word> filterUnprotectedArea(Types type) {
		ArrayList<Word> validWords = new ArrayList<Word>();
		for (Word word : words) {
			if (word.type == type) {
				validWords.add(word);
			}
		}
		return validWords;
	}
}
