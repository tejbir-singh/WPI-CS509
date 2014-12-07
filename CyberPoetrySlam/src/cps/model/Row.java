package cps.model;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * @author tejbir singh
 *
 */
public class Row extends Entity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Word> words = new ArrayList<Word>();
	
	/**
	 * Constructor.
	 */
	public Row(ArrayList<Word> words){
		this.x = words.get(0).x;
		this.y = words.get(0).y;
		this.words = words;
		this.setWidth();
		this.setHeight();
	}
		
	/**
	 * Check if a Word intersects the current Row.
	 * @param w Word to check
	 * @return true if there is an intersection
	 */
	protected boolean doesIntersectRow(Word w) {
		for (Word word : words) {
			if (!w.equals(word) && word.intersect(w) == true) {
				return true;
			}
		}
		return false;
	}
	
	/** Helper method to prepend value to beginning of list */
	void prepend(ArrayList<Word> rowOfWords, final Word first) {
		rowOfWords.add(0, first);
	}
	
	/**
	 * Connect a Word to the left of the Row
	 * @param w Word to connect
	 * @return true if successful
	 */
	public boolean connectWordLeft(Word w) {
		/* Prepend the word to the beginning of this row. Only makes sense
		 * if the row has been previously initialized with some words.  */
		if (doesIntersectRow(w)){
			return false;	
		}
		else{
			this.prepend(words, w );
			this.setX(this.words.get(0).x);
			this.setY(this.words.get(0).y);
			this.setWidth();
			this.setHeight();
			return true;
		}	
	}

	/**
	 * Connect a Word to the right of a Row.
	 * @param w word to connect
	 * @return true if successful
	 */
	public boolean connectWordRight(Word w) {
		/* Append new word the end of this row */	
		if (doesIntersectRow(w)){
			return false;	
		}
		else{
			this.words.add(w);
			this.setX(this.words.get(0).x);
			this.setY(this.words.get(0).y);
			this.setWidth();
			this.setHeight();
			return true;
		}	
	}

	/**
	 * Disconnect a word.
	 * @param dexw Index of the Word to remove
	 * @return true
	 */	
	public boolean disconnectWord(int dexw) {
		this.words.remove(dexw);
		if(this.words.size() > 0){
			this.setX(this.words.get(0).x);
			this.setY(this.words.get(0).y);
			this.setWidth();
			this.setHeight();
			return true;
		}else{ //the row has no words
			return false;
		}
		
		
	}
	
	// getters and setters
	public ArrayList<Word> getWords() {
		return this.words;
	}

	/**
	 * Set the width of the Row.
	 */
	public void setWidth() {
		this.width = 0;
		if (this.words.size() > 0) {
			for(Word word : words) {
				this.width += word.width;
			}
		}
	}

	/**
	 * Set the height of the Row.
	 */
	public void setHeight() {
		this.height = 0;
		if (this.words.size() > 0) {
			this.height = this.words.get(0).height;
		}
	}

	/**
	 * Set the position (x, y) of the Row.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void setPosition(int x, int y) {
		for (Word w : this.words){
			w.setPosition(w.x - (this.x - x), w.y - (this.y - y));
		}
		this.x = x;
		this.y = y;	
	}
}
