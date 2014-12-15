package cps.controller;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import cps.model.Entity;
import cps.model.GameManager;
import cps.model.Manipulation;
import cps.model.MoveType;
import cps.model.ReturnIndex;
import cps.model.Word;
import cps.model.Poem;
import cps.model.Row;
import cps.view.ApplicationPanel;

public class UndoRedoController extends MouseAdapter {
	GameManager gm;
	ApplicationPanel panel;
	Manipulation man;

	/** true for Undos, false for Redos */
	boolean doPushPrev;					

	/** used to store information to create Redos */
	int prevX, prevY;					

	/**
	 * Constructor.
	 * @param gm GameManager 
	 * @param panel ApplicationPanel
	 * @param type 'u' for Undo, 'r' for Redo
	 */
	public UndoRedoController(GameManager gm, ApplicationPanel panel, URType type) {
		this.gm = gm;
		this.panel = panel;
		if (type == URType.UNDO) {
			man = gm.getManipulations().pop();
			this.prevX = man.getEntity().getX();
			this.prevY = man.getEntity().getY();
			doPushPrev = true;
		}
		else if (type == URType.REDO) {
			man = gm.getPrevUndos().pop();
			doPushPrev = false;
		}
	}

	public void process() {
		if (man.getMoveType() == MoveType.MOVE) {					// undo move
			if (undoMove() && doPushPrev) {
				gm.getPrevUndos().push(new Manipulation(prevX, prevY, man.getEntity(), MoveType.MOVE));
			}
		}
		else if (man.getMoveType() == MoveType.CONNECT) {			// undo connect
			if (undoConnect() && doPushPrev) {
				gm.getPrevUndos().push(new Manipulation(prevX, prevY, man.getEntity(), MoveType.DISCONNECT));
			}
		}
		else {														// undo disconnect
			if (undoDisconnect() && doPushPrev) {
				gm.getPrevUndos().push(new Manipulation(prevX, prevY, man.getEntity(), MoveType.CONNECT));
			}
		}

		panel.validateUndo(doPushPrev);
		panel.validateRedo(true);
		panel.redraw();
		panel.repaint();
	}

	/**
	 * Separated out for portability.
	 * Move an Entity back to its previous location.
	 * @return true if successful
	 */
	private boolean undoMove() {
		Entity e = man.getEntity();
		if (e == null) { return false; }

		// make sure the Entity goes back where it belongs
		if (e instanceof Row) {		// shift the row back to where it belongs
			// taken care of below; no other action is required
		}
		else {
			if (man.getY() >= GameManager.AREA_DIVIDER && e.getY() < GameManager.AREA_DIVIDER) { 	// move from PA to other
				if (man.getY() >= GameManager.SWAP_AREA_DIVIDER) {		// move to Swap
					if (e instanceof Word) {
						gm.getSwapManager().add(e);
						gm.getPa().remove((Word) e);
					}
					else {					// poem
						for (Row row : ((Poem) e).getRows()){
							for (Word word : row.getWords()){
								gm.getSwapManager().add(word);
								gm.getPa().remove(word);
							}
						}
						gm.getPa().remove(e);
					}
				}
				else {													// move to UA
					if (e instanceof Word) {
						gm.getPa().remove((Word) e);
						gm.getUa().add(e);
					}
					else {					// poem
						for (Row row : ((Poem) e).getRows()){
							for (Word word : row.getWords()){
								gm.getUa().add(word);
								gm.getPa().remove(word);
							}
						}
						gm.getPa().remove(e);
					}
				}
			}
			else if (man.getY() <= GameManager.SWAP_AREA_DIVIDER && e.getY() > GameManager.SWAP_AREA_DIVIDER) { // move from Swap to other
				if (man.getY() < GameManager.AREA_DIVIDER) { 	// Move to PA
					gm.getPa().add(e); 
				}
				else {											// Move to UA
					gm.getUa().add(e);
				}
				if(e instanceof Word){
					gm.getSwapManager().remove((Word) e);
				} 
				else{ 	// Poem
					for (Row row : ((Poem) e).getRows()){
						for (Word word : row.getWords()){
							gm.getSwapManager().remove(word);
						}
					}
				}
			}
			else if (man.getY() <= GameManager.AREA_DIVIDER && e.getY() > GameManager.AREA_DIVIDER) { // move from UA to PA
				gm.getPa().add(e);
				if(e instanceof Word) {
					gm.getUa().remove((Word) e);
				}
				else{ // Poem
					for (Row row : ((Poem) e).getRows()){
						for (Word word : row.getWords()){
							gm.getUa().remove(word);
						}
					}
				}
			}
			else if (man.getY() >= GameManager.SWAP_AREA_DIVIDER && e.getY() > GameManager.AREA_DIVIDER && e.getY() <= GameManager.SWAP_AREA_DIVIDER) {	// move from UA to Swap
				gm.getSwapManager().add(e); // modified by Xinjie
				if (e instanceof Word) {
					gm.getUa().remove((Word) e);
				} else { // Poem
					for (Row row : ((Poem) e).getRows()){
						for (Word word : row.getWords()){
							gm.getUa().remove(word);
						}
					}
				}
			}
			if (e instanceof Poem) {
				// In case it was previously undone, get the new Poem at this location
				try {
					ReturnIndex ri = gm.getPa().getWordIdx(((Poem) e).getRows().get(0).getWords().get(0).getX(), 
							((Poem) e).getRows().get(0).getWords().get(0).getY());
					e = ri.p;
				} catch (NullPointerException npe) {
					// If this occurs, we're redoing a release so this is handled elsewhere. This can be ignored.
				}

			}
		}
		e.setPosition(man.getX(), man.getY()); // modified by Xinjie
		panel.redraw();
		panel.repaint();
		return true;
	}

	/**
	 * Separated out for portability.
	 * Move a connected Entity back to its previous location.
	 * @return true if successful
	 */
	// TODO: Determine if we're undoing a Poem-Poem connect and handle it differently
	private boolean undoConnect() {
		Entity e = man.getEntity();
		if (e == null) { return false; }
		ReturnIndex ri = gm.getPa().getWordIdx(man.getEntity().getX(), man.getEntity().getY());
		if (ri == null) {
			System.out.println("URController: could not find word index");
			return false;
		}

		if (e instanceof Word) {
			if (!gm.getPa().disconnectWord(ri.idxPoem, ri.idxRow, ri.idxWord, man.getX(), man.getY())) {
				System.out.println("URController: error at disconnect");
			}

			// reset to its previous location
			e.setX(man.getX());
			e.setY(man.getY());
		}
		else {
			// TODO: Fix for multiple disconnected rows
			Row row = ri.p.getRows().get(0);
			for (Row r : ri.p.getRows()) {
				if (r.getY() < row.getY()) {
					row = r;
				}
			}
			if (row.getWords().equals(((Poem) man.getEntity()).getRows().get(0).getWords())) {	// the poem was added to the top
				ArrayList<Row> rows = ((Poem) man.getEntity()).getRows();

				if (!gm.getPa().splitPoem(ri.p, 0, rows.size() - 1, man.getX(), man.getY())) {
					System.out.println("URController: error at disconnect");
					return false;
				}
			}
			else {		// the poem was added to the bottom
				if (!gm.getPa().splitPoem(ri.p, ri.p.getRows().size() - ((Poem) man.getEntity()).getRows().size(), 
						ri.p.getRows().size() - 1, man.getX(), man.getY())) { 
					System.out.println("URController: error at disconnect");
					return false;
				}
			}	
		}


		return true;
	}

	/**
	 * Separated out for portability.
	 * Connect a disconnected Entity to its previous location.
	 * @return true if successful
	 */
	private boolean undoDisconnect() {
		Entity e = man.getEntity();
		ReturnIndex ri = null;
		if (e == null) { return false; }

		if (e instanceof Word) {
			if ((ri = findEntityOrigin(e)) != null) {
				if (ri.p == null) {						// intersects a word which is not connected to a poem
					if (man.getY() < ri.w.getY()) {		// place on top of the word
						rebuildPoem(e, ri, true);
					}
					else if (man.getY() == ri.w.getY()) { // place on the same row
						if (man.getX() < ri.w.getX()) {		// e belongs on the left
							gm.getPa().connectWordLeftWord(ri.w, (Word) e);
						}
						else {
							gm.getPa().connectWordRightWord(ri.w, (Word) e);
						}
					}
					else {								// place it on the row below
						rebuildPoem(e, ri, false);
					}
				}
				else {
					// use y to figure out which row the word originated from
					for (int i = 0; i < ri.p.getRows().size(); i++) {
						Row r = ri.p.getRows().get(i);
						if (r.getY() == man.getY()) {
							ri.idxRow = i;
							break;
						}
					}

					if (man.getX() < ri.p.getRows().get(ri.idxRow).getX()) {
						gm.getPa().connectWordLeftPoem(ri.p, (Word) e, ri.idxRow);
					}
					else {
						gm.getPa().connectWordRightPoem(ri.p, (Word) e, ri.idxRow);
					}
				}
			}
		}
		else {			// e is a poem
			if ((ri = findEntityOrigin(e)) != null) { 
				if (man.getY() < ri.p.getY()) {		// check if it was on top 
					gm.getPa().connectPoemTop(ri.p, (Poem) e, man.getX());
				}
				else {
					gm.getPa().connectPoemBottom(ri.p, (Poem) e, man.getX());
				}
			}
		}
		return true;
	}

	/**
	 * Helper function.
	 * Find out whether an Entity came from the left or right of its original Poem.
	 * @param e Entity to check
	 * @return ReturnIndex with the index information
	 */
	private ReturnIndex findEntityOrigin(Entity e) {
		Word tmp1 = null, tmp2 = null;
		if (e instanceof Word) {
			tmp1 = new Word(man.getX()-1, man.getY()-1, e.getWidth()+2, e.getHeight()+2, null, "TEMP TEST " + ((Word) e).toString());
			tmp2 = new Word(man.getX()+1, man.getY()+1, e.getWidth()+2, e.getHeight()+2, null, "TEMP TEST " + ((Word) e).toString());
		}
		else {
			int height = 0, width = 0;
			for (Row r : ((Poem) man.getEntity()).getRows()) {
				int rowWidth = 0;
				for (Word w : r.getWords()) {
					rowWidth += w.getWidth();
				}
				if (rowWidth > width) {
					width = rowWidth;
				}
				height = r.getHeight();
			}
			tmp1 = new Word(man.getX()-1, man.getY()-1, width+2, height+2, null, "TEMP TEST");
			tmp2 = new Word(man.getX()+1, man.getY()+1, width+2, height+2, null, "TEMP TEST");
		}

		ReturnIndex ri = gm.getPa().entityIntersect(tmp1);
		if (ri != null) {
			// check if the word goes on the left or right using its row as a basis
			return ri;
		}
		ri = gm.getPa().entityIntersect(tmp2);

		return ri;
	}

	/**
	 * Helper function.
	 * Make two poems out of the words and connect them.
	 * @param e Entity to build from
	 * @param ri RowIndex containing the second Entity
	 * @param top true if e is meant to become the top word in the poem
	 */
	private void rebuildPoem(Entity e, ReturnIndex ri, boolean top) {
		ArrayList<Row> rows = new ArrayList<Row>();
		ArrayList<Word> word1 = new ArrayList<Word>();
		word1.add(ri.w);
		Row r = new Row(word1);
		rows.add(r);
		Poem p = new Poem(rows);

		ArrayList<Word> word2 = new ArrayList<Word>();
		word2.add((Word) e);
		Row r2 = new Row(word2);

		if (top) {
			r2.setPosition(man.getX(), p.getRows().get(rows.size()-1).getY() - p.getRows().get(rows.size()-1).getHeight());
			p.connectRowTop(r2);
		}
		else {
			r2.setPosition(man.getX(), p.getRows().get(rows.size()-1).getY() + p.getRows().get(rows.size()-1).getHeight());
			p.connectRowBottom(r2);
		}

		// remove the words from the protected area
		gm.getPa().remove(e);
		gm.getPa().remove(ri.w);
		gm.getPa().add(p);
	}

	public enum URType {
		UNDO, REDO
	}
}
