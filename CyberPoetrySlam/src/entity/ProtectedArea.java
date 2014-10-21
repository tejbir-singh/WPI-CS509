package entity;

import java.util.ArrayList;

public class ProtectedArea {
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
	 * Add a Word to the ProtectedArea.
	 * @param word Word to add
	 * @return true if successful
	 */
	public boolean add(Word word) {
		// should judge if the word can be added without intersecting with other words, added by Xinjie
		if (doesIntersect(word)) {				// invalid move if so
			return false;
		}
		else
			return words.add(word);
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

		
		e.x = x;
		e.y = y;
		if (doesIntersect(e)) {				// invalid move if so
			e.x = tempx; // go back to its previous location, added by Xinjie
			e.y = tempy; // go back to its previous location, added by Xinjie
			return false;
		}
		
		if (e instanceof Word) {
			((Word) e).setPosition(x, y); // change the location of Entity e globally,added by Xinjie
		}
		else {									// must be a poem
			((Poem) e).setPosition(x, y);
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
		for (Word word : words) {
			if (!e.equals(word) && word.intersect(e) == true) {
				return true;
			}
		}
		
		for (Poem poem : poems) {
			for (Row row : poem.rows){
				for (Word word : row.words){
					if (!e.equals(row) && word.intersect(e) == true) {
					return true;
				    }
				
                }
			}
		}
		
		return false;
	}
	
	public boolean connectPoemTop(Poem p, Poem top){
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
		
	}
	
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
