package entity;

import java.util.ArrayList;


public class UnprotectedArea {
	public static UnprotectedArea instance;
	ArrayList<Word> words;
	
	/**
	 * Constructor.
	 */
	private UnprotectedArea() {
		
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
	 * @param word Word to add
	 * @return true if successful
	 */
	public boolean remove(Word word) {
		int index = words.indexOf(word);
		if (index == -1) {							// word not found
			// do something
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
	public ArrayList<Word> filter(ArrayList<Types> types) {
		ArrayList<Word> validWords = new ArrayList<Word>();
		for (Types t : types) {
			for (Word word : words) {
				if (word.type == t) {
					validWords.add(word);
				}
			}
		}
		return validWords;
	}
}
