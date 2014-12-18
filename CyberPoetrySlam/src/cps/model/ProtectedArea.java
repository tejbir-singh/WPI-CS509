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
			} else{
				return this.words.add((Word) e);
			}
				
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
	public boolean remove(Entity e) {
		int index;
		if (e instanceof Word){
			index = words.indexOf(e);
			if (index == -1) { // word not found
				return false;
			} else {
				words.remove(index);
				return true;
			}
		}else if (e instanceof Poem){
			index = poems.indexOf(e);
			if (index == -1){ // poem not found
				return false;
			} else {
				poems.remove(index);
				return true;
			}
			}
			return false;
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
		}
		else{
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
	 * @author Xinjie
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
						ri.p = belongsToPoem(word);
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
					if (!row.getWords().contains(e) && row.intersect(e)) {
						return true;
					}
				}
			}

			return false;
		} else if (e instanceof Poem){ 					// e instanceof Poem
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
						if (!poem.equals((Poem) e) && row.intersect(rowe) == true) {
							return true;
						}
					}
				}
			}

			return false;
		}
		else { // TODO: e instance of row; only happens when checking intersection of two rows
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
	 * @author Xinjie
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
				if (!e.equals(word) && word.intersect(e)
						&& !((Word) e).getValue().equals("TEMP TEST " + word.getValue())) {
					ri.idxPoem = -1;
					ri.idxRow = -1;
					ri.idxWord = -1;
					ri.w = word;
					ri.p = belongsToPoem(ri.w);
					return ri;
				}
			}
		
			for (Poem poem : poems) {
				dr = 0;
				for (Row row : poem.rows){
					dw = 0;
					for (Word word : row.words){
						if (!e.equals(word) && word.intersect(e) 
								&& !((Word) e).getValue().equals("TEMP TEST " + word.getValue())) {
							ri.idxPoem = dp;
							ri.idxRow = dr;
							ri.idxWord = dw;
							ri.w = word;
							ri.p = belongsToPoem(ri.w);
							return ri;
						}
						dw += 1;
					}
					dr += 1;
				}
				dp += 1;
			}
			return null;
		} else {   				// e instanceof Poem
			for (Word word : words) {
				for (Row rowe : ((Poem) e).rows){
					for(Word worde : rowe.words){
						if (word.intersect(worde) == true) {
							ri.idxPoem = -1;
							ri.idxRow = -1;
							ri.idxWord = -1;
							ri.w = word;
							ri.p = belongsToPoem(ri.w);
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
									ri.p = belongsToPoem(ri.w);
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
	 * @author Xinjie
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
						|| row.x + row.width > GameManager.PROTECTED_AREA_WIDTH
						|| row.y + row.height > GameManager.AREA_DIVIDER - 10) {
					return true;
				} 
			}
			return false;
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
	 * @author Xinjie
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
	 * @author Xinjie
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
	 * @author Xinjie
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
	 * @author Xinjie
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
	 * @param poemidx index of the Poem in the ProtectedArea's ArrayList
	 * @param rowidx index of the Row in the Poem's ArrayList
	 * @param wordidx index of the Word in the Row's ArrayList
	 * @param x x-coordinate of Word been disconnected to
	 * @param y y-coordinate of Word been disconnected to
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean disconnectWord(int poemidx, int rowidx, int wordidx, int x, int y) {
		if (this.moveEntity(this.poems.get(poemidx).rows.get(rowidx).words.get(wordidx), x, y) 
				&& this.poems.get(poemidx).rows.get(rowidx).words.size() > 1 ) {
			if (this.poems.get(poemidx).rows.size() == 1){ // if the Poem has only one row
				this.words.add(this.poems.get(poemidx).rows.get(rowidx).words.get(wordidx));
				this.poems.get(poemidx).disconnectEdgeWord(rowidx, wordidx);
				if (this.poems.get(poemidx).rows.size() == 1
						&& this.poems.get(poemidx).rows.get(0).words.size() == 1) {	// no longer a Poem
					this.words.add(this.poems.get(poemidx).rows.get(0).words.get(0));
					this.poems.remove(poemidx);
				}
				return true;
			} else{
				Word tempword = this.poems.get(poemidx).rows.get(rowidx).words.get(wordidx);
				if(!isPoemSplit(this.poems.get(poemidx), rowidx, wordidx)){
					if(this.poems.get(poemidx).disconnectEdgeWord(rowidx, wordidx) == true){
						this.words.add(tempword);
						if (this.poems.get(poemidx).rows.size() == 1
								&& this.poems.get(poemidx).rows.get(0).words.size() == 1) {	// no longer a Poem
							this.words.add(this.poems.get(poemidx).rows.get(0).words.get(0));
							this.poems.remove(poemidx);
							
						}
						return true;	
					}else{
						return false;
					}
				}else{
					return false;
				}	
			}
		
		} else
			return false;
	}
	
	/**
	 * Check if the poem will be split when disconnect word from it.
	 * @param p the Poem which will be check
	 * @param rowidx the word's row index
	 * @param wordidx the word's word index
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean isPoemSplit(Poem p, int rowidx, int wordidx){
		if(p.rows.size() < 2 || p.rows.get(rowidx).words.size() == 1){
			return false;
		}
		if(rowidx + 1 < p.rows.size()){ // check with row below
			if(wordidx == 0){
				if(p.rows.get(rowidx).words.get(wordidx + 1).x > 
						p.rows.get(rowidx + 1).words.get(0).x + p.rows.get(rowidx + 1).width){
					return true;
				}
			}else{
				if(p.rows.get(rowidx).words.get(wordidx).x < p.rows.get(rowidx + 1).x){
					return true;
				}
			}
		}
		if(rowidx -1 >= 0){ // check with row above
			if(wordidx == 0){
				if(p.rows.get(rowidx).words.get(wordidx + 1).x >
						p.rows.get(rowidx - 1).words.get(0).x + p.rows.get(rowidx - 1).width){
					return true;
				}
			}else{
				if(p.rows.get(rowidx).words.get(wordidx).x < p.rows.get(rowidx - 1).x){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Connect a Poem on the top of another Poem and set its last row's x-coordinate with x.
	 * @param p the Poem which will be connected on top of
	 * @param ptop the Poem to be connected on the top of p
	 * @param bottomrow_x the x-coordinate of the last row of ptop when connects to p
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean connectPoemTop(Poem p, Poem ptop, int bottomrow_x){
		int ptop_x = bottomrow_x - (ptop.getRows().get(ptop.getRows().size() - 1).getX() - ptop.getRows().get(0).getX());
		int ptop_y = p.getRows().get(0).getY() - ptop.getHeight();
		if (this.moveEntity(ptop, ptop_x, ptop_y)) {
			for(int i = ptop.getRows().size() - 1; i >= 0; i --){
				p.connectRowTop(ptop.getRows().get(i));
			}
			p.setX(ptop_x);
			p.setY(ptop_y);
			this.remove(ptop);
			return true;
		}
		return false;
	}
	
	/**
	 * Connect a Poem under the bottom of another Poem and set its x-coordinate with x.
	 * @param p the Poem which will be connected under the bottom of
	 * @param pbottom the Poem to be connected under the bottom of p
	 * @param toprow_x the x-coordinate of pbottom when connects to p
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean connectPoemBottom(Poem p, Poem pbottom, int toprow_x){
		int pbottom_x = toprow_x;
		int pbottom_y = p.getRows().get(p.getRows().size() - 1).getY() + p.getRows().get(p.getRows().size() - 1).getHeight();
		if (this.moveEntity(pbottom, pbottom_x, pbottom_y)) {
			for(Row r: pbottom.getRows()){
				p.connectRowBottom(r);
			}
			this.remove(pbottom);
			return true;
		}
		return false;
	}
	
	/**
	 * Connect a Word on the top of another Poem and set its x-coordinate with x.
	 * @param p the Poem which will be connected on the top of
	 * @param w the Word to be connected on the top of p
	 * @param x the x-coordinate of w when connects to p
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean connectWordTopPoem(Poem p, Word w, int x){
		int w_x = x;
		int w_y = p.getRows().get(0).y - w.getHeight();
		if(this.moveEntity(w, w_x, w_y)){
			ArrayList<Word> aw1 = new ArrayList<Word>();
			aw1.add(w);
			Row row1 = new Row(aw1);
			p.getRows().add(0, row1);
			p.setX(w_x);
			p.setY(w_y);
			this.remove(w);
			return true;
		}
		return false;
	}
	
	/**
	 * Connect a Word under the bottom of another Poem and set its x-coordinate with x.
	 * @param p the Poem which will be connected under the bottom of
	 * @param w the Word to be connected under the bottom of p
	 * @param x the x-coordinate of w when connects to p
	 * @return true if successful
	 * @author Xinjie
	 */
	public boolean connectWordBottomPoem(Poem p, Word w, int x){
		int w_x = x;
		int w_y = p.getY() + p.getHeight();
		if(this.moveEntity(w, w_x, w_y)){
			ArrayList<Word> aw1 = new ArrayList<Word>();
			aw1.add(w);
			Row row1 = new Row(aw1);
			p.rows.add(row1);
			this.remove(w);
			return true;
		}
		return false;
	}
	
	/**
	 * Disconnect a Row from a Poem and set its coordinate to (x, y).
	 * @param poemidx index of the Poem in ProtectedArea
	 * @param rowidx index of the Row been disconnected
	 * @param x the x-coordinate of Row been disconnected to
	 * @return y the y-coordinate of Row been disconnected to
	 * @author Xinjie
	 */
	public boolean disconnectRow(int poemidx, int rowidx, int x, int y){
		Poem targetpoem = this.getPoems().get(poemidx);
		Row targetrow = targetpoem.getRows().get(rowidx);
		
		if(targetpoem.getRows().size() == 1){
			return false;
		} else{
			if(rowidx == 0 || rowidx == targetpoem.getRows().size() - 1){ // disconnect first or last row
				if(targetrow.getWords().size() > 1){ // the row has multiple words
					ArrayList<Row> ar1 = new ArrayList<Row>();
					ar1.add(targetrow);
					Poem newpoem = new Poem(ar1);
					targetpoem.removeRow(rowidx);
					if(this.moveEntity(newpoem, x, y)){
						this.getPoems().add(newpoem);
						if (targetpoem.rows.size() == 1 && targetpoem.rows.get(0).words.size() == 1){
							this.getWords().add(targetpoem.rows.get(0).words.get(0));
							this.getPoems().remove(targetpoem);
						}
						return true;
					}else{
						targetpoem.getRows().add(rowidx, targetrow);
						targetpoem.setX(targetpoem.getRows().get(0).x);
						targetpoem.setY(targetpoem.getRows().get(0).y);
					}
					
				}else{ // the row has only one word
					if(this.moveEntity(targetrow.getWords().get(0), x, y)){
						this.getWords().add(targetrow.getWords().get(0));
						targetpoem.removeRow(rowidx);
						if (targetpoem.rows.size() == 1 && targetpoem.rows.get(0).words.size() == 1){
							this.getWords().add(targetpoem.rows.get(0).words.get(0));
							this.getPoems().remove(targetpoem);
						}
						return true;
					}
				}
			} else{ //disconnect middle row
				if(targetrow.getWords().size() > 1){ // the row has multiple words
					ArrayList<Row> ar1 = new ArrayList<Row>();
					ar1.add(targetrow);
					Poem ap1 = new Poem(ar1);
					targetpoem.removeRow(rowidx);
					if(this.moveEntity(ap1, x, y)){
						this.getPoems().add(ap1);
						ArrayList<Row> ar2 = new ArrayList<Row>();
						for(int i = rowidx; i < targetpoem.getRows().size(); i ++){
							ar2.add(targetpoem.getRows().get(i));
						}
						if (ar2.size() == 1 && ar2.get(0).words.size() == 1){
							this.getWords().add(ar2.get(0).words.get(0));
						}else{
							Poem ap2 = new Poem(ar2);
						this.getPoems().add(ap2);
						}
						
						while ((targetpoem.getRows().size() - rowidx) > 0 ){
							targetpoem.removeRow(rowidx);
						}
						if (targetpoem.rows.size() == 1 && targetpoem.rows.get(0).words.size() == 1){
							this.getWords().add(targetpoem.rows.get(0).words.get(0));
							this.getPoems().remove(targetpoem);
						}
						return true;
					}else{
						targetpoem.getRows().add(rowidx, targetrow);
					}
					
				}else{ // the row has only one word
					if(this.moveEntity(targetrow.getWords().get(0), x, y)){
						this.getWords().add(targetrow.getWords().get(0));
						ArrayList<Row> ar2 = new ArrayList<Row>();
						for(int i = rowidx + 1; i < targetpoem.getRows().size(); i ++){
							ar2.add(targetpoem.getRows().get(i));	
						}
						while ((targetpoem.getRows().size() - rowidx) > 0 ){
							targetpoem.removeRow(rowidx);
						}
						if (ar2.size() == 1 && ar2.get(0).words.size() == 1){
							this.getWords().add(ar2.get(0).words.get(0));
						}else{
							Poem ap2 = new Poem(ar2);
							this.getPoems().add(ap2);
						}
						if (targetpoem.rows.size() == 1 && targetpoem.rows.get(0).words.size() == 1){
							this.getWords().add(targetpoem.rows.get(0).words.get(0));
							this.getPoems().remove(targetpoem);
						}
						return true;
					}
				}	
			}
		}			
		return false;	
	}

	/**
	 * Split a Poem in two and place the newly formed Poem at the specified x- and y-coordinates.
	 * @param p Poem to split
	 * @param rowToSplitFrom index of the row at which to start the split
	 * @param rowToSplitTo index of the row at which to end the split
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return true if successful
	 */
	public boolean splitPoem(Poem p, int rowToSplitFrom, int rowToSplitTo, int x, int y) {
		if (p.getRows().size() <= 1) { return false; }	// cannot split a one row poem
		Row r = p.getRows().get(rowToSplitFrom);
		ArrayList<Row> newRows = new ArrayList<Row>();
		newRows.add(r);
		Poem newPoem = new Poem(newRows);
		p.removeRow(rowToSplitFrom);
		// split poem
		for (int i = rowToSplitFrom; i < rowToSplitTo; i++) {
			Row current = p.getRows().get(rowToSplitFrom);
			ArrayList<Row> tmpRow = new ArrayList<Row>();
			tmpRow.add(current);
			Poem tmpPoem = new Poem(tmpRow);
			if (current.getY() < newPoem.getRows().get(0).getY()) {
				connectPoemTop(newPoem, tmpPoem, tmpPoem.getX());
			}
			else {
				connectPoemBottom(newPoem, tmpPoem, tmpPoem.getX());
			}
			
			p.removeRow(rowToSplitFrom);
		}
		poems.add(newPoem);
		newPoem.setPosition(x, y);
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
