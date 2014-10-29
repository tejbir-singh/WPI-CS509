package shapes.model;

import java.util.ArrayList;

/**
 * @author tejbir singh
 *
 */
public class Row extends Entity {
	private static final long serialVersionUID = 1L;
	/**
	 * width:
	 * height:
	 * x	: 
	 * y	:
	 */
	/*int width;  track the width of the row 
	int height;  track the height of the row 
	int x;  row's associated x-coordinate on the board 
	int y;  row's associated y-coordinate on the board */
	ArrayList<Word> words = new ArrayList<Word>();


	public Row(int xarg, int yarg,  ArrayList<Word> words_arg) 
	{
		this.x = xarg;
		this.y = yarg;
		/* method passes reference to the words vector  */
		for (Word w: words_arg) {
			words.add(w);
		}
		
	/* Each row belongs to a poem and will need to be added to the poem.
	* [TODO]: Add logic to add this row to the referenced poem. 
	* Poem is a collection of rows. What's the collection type of a poem. That affects the methods available. 	
    */		
	}
	
	// another constructor of Row, added by Xinjie
	public Row(ArrayList<Word> words_arg){
		this.x = words_arg.get(0).x;
		this.y = words_arg.get(0).y;
		this.words = words_arg;
		this.setWidth();
		this.setHeight();
	}
	
	public boolean setX(int x){
		this.x = x;
		return true;
	}
	
	public boolean setY(int y){
		this.y = y;
		return true;
	}
	
	// set the width of the row, added by Xinjie
	public boolean setWidth() {
		this.width = 0;
		if (this.words.size() > 0){
			for(Word word : words)
				this.width += word.width;
		}
		
		return true;
	}
		
	//set the height of the row(equals to the height of one word), added by Xinjie
	public boolean setHeight() {
		this.height = 0;
		if (this.words.size() > 0){
			this.height = this.words.get(0).height;
		}
		
		return true;
	}
	
	// set the position(x,y) of the row, added by Xinjie
	public boolean setPosition(int x, int y) {
		for (Word w : this.words){
			w.setPosition(w.x - (this.x - x), w.y - (this.y - y));
		}
		this.x = x;
		this.y = y;
		return true;	
	}
		
	/*
	 * 
	 */
	protected boolean doesIntersectRow(Word w) {
		for (Word word : words) {
			if (!w.equals(word) && word.intersect(w) == true) {
				return true;
			}
		}
		
		return false;
	}
	
	void prepend(ArrayList<Word> rowOfWords, final Word first) {
		rowOfWords.add(0, first);
	}
	
	/**
	 * @param w
	 * @return
	 */
	public boolean connectWordLeft(Word w) 
	{
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
	 * @param w
	 * @return
	 */
	public boolean connectWordRight(Word w) 
	{
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
	 * @param w
	 * @return
	 */	
	//modified by Xinjie on 10/19/2014
	public boolean disconnectWord(int dexw) 
	{
		/*[TODO]: Think the effect of this operation on row, structurally.
		 * Group question:
		 * */	
		this.words.remove(dexw);
		this.setX(this.words.get(0).x);
		this.setY(this.words.get(0).y);
		this.setWidth();
		this.setHeight();
		return true;
		
	}
}
