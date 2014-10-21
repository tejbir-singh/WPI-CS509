package entity;

import java.util.ArrayList;

/**
 * @author tejbir singh
 *
 */
public class Row extends Entity{
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


	/**
	 * @param xarg
	 * @param yarg
	 * @param words
	 * @param poem
	 */
	
	
	public Row(int xarg, int yarg,  ArrayList<Word> words_arg) 
	{
		x = xarg;
		y = yarg;
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
	}
	
	/*// get the width of the row, added by Xinjie
	public int getWidth() {
		int w = 0;
		for(int i = 0; i < this.words.size(); i++)
			w += this.words.get(i).width;
		return w;
	}
		
	//get the height of the row(equals to the height of one word), added by Xinjie
	public int getHeight() {
		return this.words.get(0).height;
	}*/
	
	// set the position(x,y) of the row, added by Xinjie
	public boolean setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		return true;	
	}
		
	/*
	 * 
	 */
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
		this.prepend(words, w );	
		return true;
	}

	/**
	 * @param w
	 * @return
	 */
	public boolean connectWordRight(Word w) 
	{
		/* Append new word the end of this row */		
		return words.add(w);
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
		return true;
		
	}
}
