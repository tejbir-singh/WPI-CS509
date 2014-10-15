package entity;

import java.util.ArrayList;

/**
 * @author tejbir singh
 *
 */
public class Row {
	/**
	 * width:
	 * height:
	 * x	: 
	 * y	:
	 */
	int width; /* track the width of the row */
	int height; /* track the height of the row */
	int x; /* row's associated x-coordinate on the board */
	int y; /* row's associated y-coordinate on the board */
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
	public boolean disconnectWord(Word w) 
	{
		/*[TODO]: Think the effect of this operation on row, structurally.
		 * Group question:
		 * */	
		return words.remove(w);		
	}
}
