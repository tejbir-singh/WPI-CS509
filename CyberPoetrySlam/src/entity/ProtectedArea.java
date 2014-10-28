package entity;

import java.io.Serializable;
import java.util.ArrayList;

public class ProtectedArea implements Serializable {
	private static final long serialVersionUID = 906074510836395079L;
	ArrayList<Word> words;
	ArrayList<Poem> poems;
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
	 * Add a Word to the ProtectedArea.  %%% or add a poem to the ProtectedArea, added by Xinjie
	 * @param word Word to add
	 * @return true if successful
	 */
	public boolean add(Entity e) {
		if (e instanceof Word){
			// should judge if the word can be added without intersecting with other words, added by Xinjie
			if (doesIntersect(e)) {				// invalid move if so
				return false;
			}
			else
				return this.words.add((Word) e);
		}
		else{   // add poem
			if (doesIntersect(e)){
				return false;
			}
			else
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
		if (index == -1) {							// word not found
			return false;
		}
		else {
			words.remove(index);
			return true;
		}
	}
	
	/**
	 * Move an Entity to the specified x and y coordinates if it is valid.
	 * @param e	Entity to move
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	// modified by Xinjie, the original one didn't give e's width and height to tmp 
	public boolean moveEntity(Entity e, int x, int y) {
		int tempx = e.x; //save the previous value e.x, added by Xinjie
		int tempy = e.y; //save the previous value e.y, added by Xinjie
		// create temporary copy for manipulation

		if (e instanceof Word){
			((Word) e).setPosition(x, y); // change the location of Entity e globally,added by Xinjie
			if (doesIntersect(e)) {				// invalid move if so
				((Word) e).setPosition(tempx, tempy); // go back to its previous location, added by Xinjie
				return false;
			}	
		}else{
			((Poem) e).setPosition(x, y);
			if (doesIntersect(e)) {				// invalid move if so
				((Poem) e).setPosition(tempx, tempy); // go back to its previous location, added by Xinjie
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
			if (w.x == x && w.y == y) {
				return w;
			}
		}
		return null;
	}
	
	/**
	 * Check if an Entity is intersecting any others in the ProtectedArea.
	 * @param e Entity to check
	 * @return true if it intersects
	 */
	protected boolean doesIntersect(Entity e) {
		// compare e to each existing Entity
		if(e instanceof Word){
			for (Word word : words) {
				if (!e.equals(word) && word.intersect(e) == true) {
				return true;
				}
			}
		
			for (Poem poem : poems) {
				for (Row row : poem.rows){
					if (row.intersect(e) == true){
						return true;
					}
				}	
			}
		
			return false;
		} else{   //e instanceof Poem
			for (Word word : words) {
				for (Row rowe : ((Poem) e).rows){
					for(Word worde : rowe.words){
						if (word.intersect(worde) == true) {
							return true;
						}
					}
					
				}
				
			}
			for (Poem poem : poems) {
				for (Row row : poem.rows){
					for (Row rowe : ((Poem) e).rows){
						if (!poem.equals((Poem) e) && row.intersect(rowe) == true) {
							return true;
						}
					}
				}	
			}
			
			return false;
		}
		
	}
	
	/**
	 * Connect Word wleft to the left of Word w, 
	 * it will create a new poem(added to ArrayList poems) with one row, two words,
	 * and the ArrayList words will delete w and wleft
	 * @param w Word to add to
	 * @param wleft Word to add to w
	 * @return true if successful
	 */
	public boolean connectWordLeftWord(Word w, Word wleft){
		if(this.moveEntity(wleft, w.x - wleft.width, w.y)){ // check intersection while connecting
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
		}
		else
			return false;
	}
	
	// Similar to connectWordLeftWord, connect Word right to the right of Word w, 
	//it will create a new poem(added to ArrayList poems) with one row, tow words,
	//and the ArrayList words will delete w and right, added by Xinjie
	public boolean connectWordRightWord(Word w, Word wright){
		if(this.moveEntity(wright, w.x + w.width, w.y)){
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
		}
		else
			return false;
	}
	
	// Connect Word w to the Poem p's No.dexr's row's left edge
	// added by Xinjie
	public boolean connectWordLeftPoem(Poem p, Word w, int dexr){
		if(this.moveEntity(w, p.rows.get(dexr).x - w.width, p.rows.get(dexr).y)){
			p.rows.get(dexr).connectWordLeft(w);
			p.setX(p.rows.get(0).x);
			p.setY(p.rows.get(0).y);
			this.remove(w);
			return true;
		}
		return false;
	}
	
	// Connect Word w to the Poem p's No.dexr's row's right edge
	// added by Xinjie
	public boolean connectWordRightPoem(Poem p,Word w, int dexr){
		if(this.moveEntity(w, p.rows.get(dexr).x + p.rows.get(dexr).width, p.rows.get(dexr).y)){
			p.rows.get(dexr).connectWordRight(w);
			this.remove(w);
			return true;
		}
		return false;
	}
	
	//disconnect ProtectedArea's No.dexp's poem's No.dexr row's No.dexw's word, and move the word to (x, y) without intersecting
	public boolean disconnectWord(int dexp, int dexr, int dexw, int x, int y){
		if(this.moveEntity(this.poems.get(dexp).rows.get(dexr).words.get(dexw), x, y)){
			this.words.add(this.poems.get(dexp).rows.get(dexr).words.get(dexw));
			this.poems.get(dexp).disconnectEdgeWord(dexr, dexw);
			return true;
		}
		else
			return false;
	}
	
	/*public boolean connectPoemTop(Poem p, Poem top){
		p.setPosition(top.x, top.y);
		for(int i = p.rows.size() - 1; i >= 0; i--)
			p.rows.add(0, top.rows.get(i));
		return true;
	}
	
	public boolean connectPoemBottom(Poem p, Poem bottom){
		for(int i = 0; i < p.rows.size(); i++)
			p.rows.add(p.rows.size(), bottom.rows.get(i));
		return true;
	}
	
	public boolean disconnectRow(ArrayList<Poem> p, int dexp, int dexr, int newx, int newy){
		// dexp is the index of the selected poem, dexr is the index of the selected row, (newx, newy) is the position of being dragged to
		if(p.get(dexp).rows.size() == 1)
			return false;
		else{
			if(dexr == 0){ // disconnect first row
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist.add(p.get(dexp).rows.get(dexr));
				Poem newpoem = new Poem(newrowlist);
				newpoem.setPosition(newx, newy);
				p.add(newpoem);
				
				// modify original poem
				p.get(dexp).setPosition(p.get(dexp).rows.get(1).x, p.get(dexp).rows.get(1).y);
				p.get(dexp).rows.remove(dexr);
			}
			else if(dexr == p.get(dexp).rows.size() - 1){ //disconnect last row
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist1 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist1.add(p.get(dexp).rows.get(dexr));
				Poem newpoem1 = new Poem(newrowlist1);
				newpoem1.setPosition(newx, newy);
				p.add(newpoem1);
				
				// modify original poem
				p.get(dexp).rows.remove(p.get(dexp).rows.get(dexr));
			}
			else{ //disconnect middle row
				// the lower rows construct a new poem
				ArrayList<Row> newrowlist2 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				for(int i = dexr + 1; i < p.get(dexp).rows.size(); i++)
					newrowlist2.add(p.get(dexp).rows.get(i));
				Poem newpoem2 = new Poem(newrowlist2);
				newpoem2.setPosition(p.get(dexp).rows.get(dexr + 1).x, p.get(dexp).rows.get(dexr + 1).y);
				p.add(newpoem2);
				
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist3 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist3.add(p.get(dexp).rows.get(dexr));
				Poem newpoem3 = new Poem(newrowlist3);
				newpoem3.setPosition(newx, newy);
				p.add(newpoem3);
				
				// modify the original poem
				for (int i = dexr; i < p.get(dexp).rows.size(); i++ )
					p.get(dexp).rows.remove(p.get(dexp).rows.get(i));	
			}
		}	
				
		return true;
		
	}*/
	
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
