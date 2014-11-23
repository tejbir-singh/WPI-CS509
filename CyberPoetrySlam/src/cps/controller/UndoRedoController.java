package cps.controller;

import java.awt.event.MouseAdapter;

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
		// TODO: Check if going to swap area from UA or PA
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
		else if (man.getY() >= GameManager.SWAP_AREA_DIVIDER && e.getY() > GameManager.AREA_DIVIDER) {	// move from UA to Swap
			gm.getSwapManager().add(e); // modified by Xinjie
			if (e instanceof Word) {
				gm.getUa().remove((Word) e);
			} else{ // Poem
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
	private boolean undoConnect() {
		Entity e = man.getEntity();
		if (e == null) { return false; }
		
		if (e instanceof Word) {
			ReturnIndex ri = gm.getPa().getWordIdx(man.getEntity().getX(), man.getEntity().getY());
			if (ri == null) {
				System.out.println("could not find word index");
				return false;
			}
			if (!gm.getPa().disconnectWord(ri.idxPoem, ri.idxRow, ri.idxWord, man.getX(), man.getY())) {
				System.out.println("error at disconnect");
			}
		}
		// reset to its previous location
		e.setX(man.getX());
		e.setY(man.getY());
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
					if (man.getX() < ri.w.getX()) {		// e belongs on the left
						gm.getPa().connectWordLeftWord(ri.w, (Word) e);
					}
					else {
						gm.getPa().connectWordRightWord(ri.w, (Word) e);
					}
				}
				else {
					if (ri.idxWord == 0) {
						gm.getPa().connectWordLeftPoem(ri.p, (Word) e, ri.idxRow);
					}
					else {
						gm.getPa().connectWordRightPoem(ri.p, (Word) e, ri.idxRow);
					}
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
			tmp1 = new Word(man.getX()+1, man.getY()+1, e.getWidth()+2, e.getHeight()+2, null, "TEMP TEST " + ((Word) e).getValue());
			tmp2 = new Word(man.getX()-1, man.getY()-1, e.getWidth()+2, e.getHeight()+2, null, "TEMP TEST " + ((Word) e).getValue());
		}
		
		ReturnIndex ri = gm.getPa().entityIntersect(tmp1);
		if (ri != null) { 
			 return ri;
		}
		ri = gm.getPa().entityIntersect(tmp2);
		return ri;
	}
	

	public enum URType {
		UNDO, REDO
	}
}
