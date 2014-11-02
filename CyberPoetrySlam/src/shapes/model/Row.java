package shapes.model;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * @author tejbir singh
 *
 */
public class Row extends Entity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * width:
	 * height:
	 * x	: 
	 * y	:
	 */
	
	 /*
	 * [TODO]: Cleanup the following commented code before final submission
	int width;  track the width of the row 
	int height;  track the height of the row 
	int x;  row's associated x-coordinate on the board 
	int y;  row's associated y-coordinate on the board 
	*/
	
	public ArrayList<Word> words = new ArrayList<Word>();


	/* 
	 * [TODO]: Code cleanup: remove the following constructor before final submission
	 */
/*	public Row(int xarg, int yarg,  ArrayList<Word> words_arg) 
	{
		this.x = xarg;
		this.y = yarg;
		// method passes reference to the words vector  
		for (Word w: words_arg) {
			words.add(w);
		}
	}*/
		
	
		
	/* Each row belongs to a poem and will need to be added to the poem.
	* [TODO]: Add logic to add this row to the referenced poem. 
	* Poem is a collection of rows. What's the collection type of a poem. That affects the methods available. 	
    */		
	
	// another constructor of Row, added by Xinjie
	public Row(ArrayList<Word> words_arg){
		this.x = words_arg.get(0).x;
		this.y = words_arg.get(0).y;
		this.words = words_arg;
		this.setWidth();
		this.setHeight();
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	// set the width of the row, added by Xinjie
	public void setWidth() {
		this.width = 0;
		if (this.words.size() > 0){
			for(Word word : words)
				this.width += word.width;
		}
	}
		
	//set the height of the row(equals to the height of one word), added by Xinjie
	public void setHeight() {
		this.height = 0;
		if (this.words.size() > 0){
			this.height = this.words.get(0).height;
		}
	}
	
	// set the position(x,y) of the row, added by Xinjie
	public void setPosition(int x, int y) {
		for (Word w : this.words){
			w.setPosition(w.x - (this.x - x), w.y - (this.y - y));
		}
		this.x = x;
		this.y = y;	
	}
		
	/*
	 * Checks if the word intersects the current Row
	 */
	protected boolean doesIntersectRow(Word w) {
		for (Word word : words) {
			if (!w.equals(word) && word.intersect(w) == true) {
				return true;
			}
		}
		
		return false;
	}
	
	/* Helper method to prepend value to beginning of list */
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
		 * if the row has been previously initialised with some words.  */
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
		this.setX(this.words.get(0).x);
		this.setY(this.words.get(0).y);
		this.setWidth();
		this.setHeight();
		return true;
	}
	
	// getters
	public ArrayList<Word> getWords() {
		return this.words;
	}
}
