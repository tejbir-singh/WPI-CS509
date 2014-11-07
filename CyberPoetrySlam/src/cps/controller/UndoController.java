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

public class UndoController extends MouseAdapter {
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
	public UndoController(GameManager gm, ApplicationPanel panel, URType type) {
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
		
		panel.validateUndo();
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
		if (man.getY() >= GameManager.AREA_DIVIDER && e.getY() < GameManager.AREA_DIVIDER) { // move from PA to UA
			gm.release((Word) e);
		} else if (man.getY() < GameManager.AREA_DIVIDER && e.getY() >= GameManager.AREA_DIVIDER) { // move from UA to PA
			gm.getPa().add(e); // modified by Xinjie
			if(e instanceof Word){
				gm.getUa().remove((Word) e);
			} else{ // Poem
				for (Row row : ((Poem) e).getRows()){
					for (Word word : row.getWords()){
						gm.getUa().remove(word);
					}
				}
			}
		}
		e.setPosition(man.getX(), man.getY()); // modified by Xinjie
		//e.setX(man.getX());
		//e.setY(man.getY());
		//System.out.println(gm.getUa().getWords().size());
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
