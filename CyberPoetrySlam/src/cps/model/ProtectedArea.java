package cps.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Devin, Xinjie
 */
public class ProtectedArea implements Serializable {
	private static final long serialVersionUID = 906074510836395079L;
	ArrayList<Word> words;
	ArrayList<Poem> poems;
	Word w;
	// ReturnIndex ri = new ReturnIndex(-1, -1, -1, w);
	private static ProtectedArea instance;

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

	/**
	 * Add a Word or  Poem to the ProtectedArea
	 * @param word Word to add
	 * @return true if successful
	 */
	public boolean add(Entity e) {
		if (e instanceof Word) {
			// should judge if the word can be added without intersecting with
			// other words, added by Xinjie
			if (doesIntersect(e)) { // invalid move if so
				return false;
			} else
				return this.words.add((Word) e);
		} else { // add poem
			if (doesIntersect(e)) {
				return false;
			} else
				return this.poems.add((Poem) e);
		}

	}

	/**
	 * Remove a word from the ProtectedArea.
	 * @param word Word to remove
	 * @return true if successful
	 */
	public boolean remove(Word word) {
		int index = words.indexOf(word);
		if (index == -1) { // word not found
			return false;
		} else {
			words.remove(index);
			return true;
		}
	}

	/**
	 * Move an Entity to the specified x and y coordinates if it is valid.
	 * @param e Entity to move
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	public boolean moveEntity(Entity e, int x, int y) {
		int tempx = e.x; //save the previous value e.x
		int tempy = e.y; //save the previous value e.y

		if (e instanceof Word){
			((Word) e).setPosition(x, y); 								// change the location of Entity e globally
			if (doesIntersect(e) || boundaryIntersect(e)) {				// invalid move if so
				((Word) e).setPosition(tempx, tempy); 					// go back to its previous location
				return false;
			}	
		}else{
			((Poem) e).setPosition(x, y);
			if (doesIntersect(e) || boundaryIntersect(e)) {				// invalid move if so
				((Poem) e).setPosition(tempx, tempy); 					// go back to its previous location
				return false;
			}
		}

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
			// create a dummy; remove this later
			Word tmp = new Word(x, y, 0, 0, null, null);
			if (w.intersect(tmp)) {
				return w;
			}
		}
		
		for (Poem p : poems) {						// check through Poems
			for (Row r : p.getRows()) {
				for (Word w : r.getWords()) {
					Word tmp = new Word(x, y, 0, 0, null, null);
					if (w.intersect(tmp)) {
						return w;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Get a ReturnIndex based on its x and y position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return ReturnIndex at given location (null if not found)
	 */
	public ReturnIndex getWordIdx(int x, int y) {
		Word w = null;
		ReturnIndex ri = new ReturnIndex(-1, -1, -1, w);
		int poem_idx = 0;
		int row_idx = 0;
		int word_idx = 0;
		for (Poem poem : poems) {						// check through Poems
			for (Row row : poem.getRows()) {
				for (Word word : row.getWords()) {
					Word tmp = new Word(x, y, 0, 0, null, null);
					if (word.intersect(tmp)) {
						ri.idxPoem = poem_idx;
						ri.idxRow = row_idx;
						ri.idxWord = word_idx;
						ri.w = word;
						return ri;
					}
					word_idx += 1;
				}
				row_idx += 1;
				word_idx = 0;
			}
			poem_idx += 1;
			row_idx = 0;
		}
		return null;
	}

	/**
	 * Check if an Entity is intersecting any others in the ProtectedArea.
	 * @param e Entity to check
	 * @return true if it intersects
	 */
	public boolean doesIntersect(Entity e) {
		// compare e to each existing Entity
		if (e instanceof Word) {
			for (Word word : words) {
				if (!e.equals(word) && word.intersect(e) == true) {
					return true;
				}
			}

			for (Poem poem : poems) {
				for (Row row : poem.rows) {
					if (row.intersect(e)) {
						return true;
					}
				}
			}

			return false;
		} else { // e instanceof Poem
			for (Word word : words) {
				for (Row rowe : ((Poem) e).rows) {
					for (Word worde : rowe.words) {
						if (word.intersect(worde) == true) {
							return true;
						}
					}
				}
			}

			for (Poem poem : poems) {
				for (Row row : poem.rows) {
					for (Row rowe : ((Poem) e).rows) {
						if (!poem.equals((Poem) e)
								&& row.intersect(rowe) == true) {
							return true;
						}
					}
				}
			}

			return false;
		}

	}

	/**
	 * Check if an Entity is intersecting any others in the ProtectedArea.
	 * 
	 * @param e Entity to check
	 * @return the word that intersects with Entity e, and in order to save the
	 *         word's index information of which poem's which row's which word,
	 *         stored in ReturnIndex. if the index of poem equals to -1, that
	 *         means it is a word instead of belonging to a Poem.
	 *
	 */
	public ReturnIndex entityIntersect(Entity e) {
		// compare e to each existing Entity
		Word w = null;
		ReturnIndex ri = new ReturnIndex(-1, -1, -1, w);
		int dp = 0;
		int dr = 0;
		int dw = 0;
		if(e instanceof Word){
			for (Word word : words) {
				if (!e.equals(word) && word.intersect(e) == true) {
					ri.idxPoem = -1;
					ri.idxRow = -1;
					ri.idxWord = -1;
					ri.w = word;
				return ri;
				}
			}
		
			for (Poem poem : poems) {
				dr = 0;
				for (Row row : poem.rows){
					dw = 0;
					for (Word word : row.words){
						if (!e.equals(word) && word.intersect(e) == true) {
							ri.idxPoem = dp;
							ri.idxRow = dr;
							ri.idxWord = dw;
							ri.w = word;
						return ri;
						}
						dw += 1;
					}
					dr += 1;
				}
				dp += 1;
			}
			return null;
		} else{   //e instanceof Poem
			for (Word word : words) {
				for (Row rowe : ((Poem) e).rows){
					for(Word worde : rowe.words){
						if (word.intersect(worde) == true) {
							ri.idxPoem = -1;
							ri.idxRow = -1;
							ri.idxWord = -1;
							ri.w = word;
						return ri;
						}
					}	
				}	
			}
			for (Poem poem : poems) {
				dr = 0;
				for (Row row : poem.rows){
					dw = 0;
					for (Word word : row.words){
						for (Row rowe : ((Poem) e).rows){
							for (Word worde : rowe.words){
								if (!word.equals(worde) && word.intersect(worde) == true) {
									ri.idxPoem = dp;
									ri.idxRow = dr;
									ri.idxWord = dw;
									ri.w = word;
								return ri;
								}
							}	
						}
						dw += 1;
					}
					dr += 1;
				}
				dp += 1;
			}
			
			return null;
		}
		
	}

	/**
	 * Check if an Entity is intersecting any boundary in the ProtectedArea.
	 * @param e Entity to check
	 * @return true if it intersecting boundary
	 */
	public boolean boundaryIntersect(Entity e) {
		if (e instanceof Word) {
			if (e.x < GameManager.PROTECTED_AREA_X
					|| e.y < GameManager.PROTECTED_AREA_Y
					|| e.x + e.width > GameManager.PROTECTED_AREA_WIDTH
					|| e.y + e.height > GameManager.AREA_DIVIDER - 10) {
				return true;
			} else {
				return false;
			}
		} else if (e instanceof Poem) {
			for (Row row : ((Poem) e).rows) {
				if (row.x < GameManager.PROTECTED_AREA_X
						|| row.y < GameManager.PROTECTED_AREA_Y
						|| row.x + e.width > GameManager.PROTECTED_AREA_WIDTH
						|| row.y + row.height > GameManager.AREA_DIVIDER - 10) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * Find the Poem which the Word is a part of.
	 * @param w Word to check
	 * @return Poem which the Word belongs to (null if none)
	 */
	public Poem belongsToPoem(Word w) {
		for (Poem p : poems) {
			for (Row r : p.getRows()) {
				if (r.getWords().contains(w)) {
					return p;
				}
			}
		}
		return null;
	}


	/**
	 * Connect Word wleft to the left of Word w, it will create a new poem(added
	 * to ArrayList poems) with one row, two words, and the ArrayList words will
	 * delete w and wleft
	 * @param w Word to add to
	 * @param wleft Word to add to w
	 * @return true if successful
	 */
	public boolean connectWordLeftWord(Word w, Word wleft) {
		if (this.moveEntity(wleft, w.x - wleft.width, w.y)) { // check for intersection while connecting
			// create a new poem and add it to the ArrayList poems and delete w and wleft
			ArrayList<Word> aw1 = new ArrayList<Word>();
			aw1.add(wleft);
			aw1.add(w);
			Row row1 = new Row(aw1);
			ArrayList<Row> ar1 = new ArrayList<Row>();
			ar1.add(row1);
			Poem poem1 = new Poem(ar1);
			this.remove(w);
			this.remove(wleft);
			this.add(poem1);

			return true;
		} else
			return false;
	}

	
	/**
	 * Connect Word wright to the left of Word w, it will create a new poem(added
	 * to ArrayList poems) with one row, two words, and the ArrayList words will
	 * delete w and wright
	 * @param w Word to add to
	 * @param wleft Word to add to w
	 * @return true if successful
	 */
	public boolean connectWordRightWord(Word w, Word wright) {
		if (this.moveEntity(wright, w.x + w.width, w.y)) {
			ArrayList<Word> aw1 = new ArrayList<Word>();
			aw1.add(w);
			aw1.add(wright);
			Row row1 = new Row(aw1);
			ArrayList<Row> ar1 = new ArrayList<Row>();
			ar1.add(row1);
			Poem poem1 = new Poem(ar1);
			this.remove(w);
			this.remove(wright);
			this.add(poem1);

			return true;
		} else
			return false;
	}

	/**
	 * Connect a Word to a Poem at the specified row index's left edge
	 * @param p Poem to add the Word to
	 * @param w Word to add
	 * @param rowIndex index of the Row in the Poem to add the Word to
	 * @return true if successful
	 */
	public boolean connectWordLeftPoem(Poem p, Word w, int rowIndex) {
		if (this.moveEntity(w, p.rows.get(rowIndex).x - w.width, p.rows.get(rowIndex).y)) {
			p.rows.get(rowIndex).connectWordLeft(w);
			p.setX(p.rows.get(0).x);
			p.setY(p.rows.get(0).y);
			this.remove(w);
			return true;
		}
		return false;
	}

	/**
	 * Connect a Word to a Poem at the specified row index's right edge
	 * @param p Poem to add the Word to
	 * @param w Word to add
	 * @param rowIndex index of the Row in the Poem to add the Word to
	 * @return true if successful
	 */
	public boolean connectWordRightPoem(Poem p, Word w, int dexr) {
		if (this.moveEntity(w, p.rows.get(dexr).x + p.rows.get(dexr).width,
				p.rows.get(dexr).y)) {
			p.rows.get(dexr).connectWordRight(w);
			this.remove(w);
			return true;
		}
		return false;
	}

	/**
	 * Disconnect a Word from a Poem.
	 * @param poemIndex index of the Poem in the ProtectedArea's ArrayList
	 * @param rowIndex index of the Row in the Poem's ArrayList
	 * @param wordIndex index of the Word in the Row's ArrayList
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	public boolean disconnectWord(int poemIndex, int rowIndex, int wordIndex, int x, int y) {
		if (this.moveEntity(this.poems.get(poemIndex).rows.get(rowIndex).words.get(wordIndex), x, y)) {
			this.words.add(this.poems.get(poemIndex).rows.get(rowIndex).words.get(wordIndex));
			this.poems.get(poemIndex).disconnectEdgeWord(rowIndex, wordIndex);
			if (this.poems.get(poemIndex).rows.size() == 1
					&& this.poems.get(poemIndex).rows.get(0).words.size() == 1) {
				this.words.add(this.poems.get(poemIndex).rows.get(0).words.get(0));
				this.poems.remove(poemIndex);
			}
			return true;
		} else
			return false;
	}

	/*
	 * public boolean connectPoemTop(Poem p, Poem top){ p.setPosition(top.x,
	 * top.y); for(int i = p.rows.size() - 1; i >= 0; i--) p.rows.add(0,
	 * top.rows.get(i)); return true; }
	 * 
	 * public boolean connectPoemBottom(Poem p, Poem bottom){ for(int i = 0; i <
	 * p.rows.size(); i++) p.rows.add(p.rows.size(), bottom.rows.get(i)); return
	 * true; }
	 * 
	 * public boolean disconnectRow(ArrayList<Poem> p, int dexp, int dexr, int
	 * newx, int newy){ // dexp is the index of the selected poem, dexr is the
	 * index of the selected row, (newx, newy) is the position of being dragged
	 * to if(p.get(dexp).rows.size() == 1) return false; else{ if(dexr == 0){ //
	 * disconnect first row // the disconnected row constructs a new poem
	 * ArrayList<Row> newrowlist = new ArrayList<Row>(); //it's up to Rej's
	 * constructor of class Row newrowlist.add(p.get(dexp).rows.get(dexr)); Poem
	 * newpoem = new Poem(newrowlist); newpoem.setPosition(newx, newy);
	 * p.add(newpoem);
	 * 
	 * // modify original poem
	 * p.get(dexp).setPosition(p.get(dexp).rows.get(1).x,
	 * p.get(dexp).rows.get(1).y); p.get(dexp).rows.remove(dexr); } else if(dexr
	 * == p.get(dexp).rows.size() - 1){ //disconnect last row // the
	 * disconnected row constructs a new poem ArrayList<Row> newrowlist1 = new
	 * ArrayList<Row>(); //it's up to Rej's constructor of class Row
	 * newrowlist1.add(p.get(dexp).rows.get(dexr)); Poem newpoem1 = new
	 * Poem(newrowlist1); newpoem1.setPosition(newx, newy); p.add(newpoem1);
	 * 
	 * // modify original poem
	 * p.get(dexp).rows.remove(p.get(dexp).rows.get(dexr)); } else{ //disconnect
	 * middle row // the lower rows construct a new poem ArrayList<Row>
	 * newrowlist2 = new ArrayList<Row>(); //it's up to Rej's constructor of
	 * class Row for(int i = dexr + 1; i < p.get(dexp).rows.size(); i++)
	 * newrowlist2.add(p.get(dexp).rows.get(i)); Poem newpoem2 = new
	 * Poem(newrowlist2); newpoem2.setPosition(p.get(dexp).rows.get(dexr + 1).x,
	 * p.get(dexp).rows.get(dexr + 1).y); p.add(newpoem2);
	 * 
	 * // the disconnected row constructs a new poem ArrayList<Row> newrowlist3
	 * = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
	 * newrowlist3.add(p.get(dexp).rows.get(dexr)); Poem newpoem3 = new
	 * Poem(newrowlist3); newpoem3.setPosition(newx, newy); p.add(newpoem3);
	 * 
	 * // modify the original poem for (int i = dexr; i <
	 * p.get(dexp).rows.size(); i++ )
	 * p.get(dexp).rows.remove(p.get(dexp).rows.get(i)); } }
	 * 
	 * return true;
	 * 
	 * }
	 */

	// Getters and setters
	public ArrayList<Word> getWords() {
		return words;
	}

	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}

	public ArrayList<Poem> getPoems() {
		return poems;
	}

	public void setPoems(ArrayList<Poem> poems) {
		this.poems = poems;
	}
}
