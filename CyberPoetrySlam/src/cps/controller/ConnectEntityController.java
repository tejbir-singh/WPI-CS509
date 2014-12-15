package cps.controller;

import java.awt.Point;
import java.awt.event.*;

import cps.model.*;
import cps.view.*;

/**
 * @author Tej, Xinjie, Devin
 */
public class ConnectEntityController extends MouseAdapter {

	/** Needed for controller behavior. */
	GameManager gm;
	ApplicationPanel panel;
	ReturnIndex ri;
	ReturnIndex temp;
	
	/** Original x,y where the Entity was before move. */
	int originalx;
	int originaly;
	
	/** Anchor point where first grabbed and delta from that location. */
	Point anchor;
	int deltaX;
	int deltaY;
	
	/** Button that started off. */
	int buttonType;
	
	/** Constructor holds onto key manager objects. */
	public ConnectEntityController(GameManager gm, ApplicationPanel panel) {
		this.gm = gm;
		this.panel = panel;
	}

	/** Set up press events but no motion events. */
	public void register() {
		panel.setActiveListener(this);
		panel.setActiveMotionListener(this);
	}

	/**
	 * Whenever mouse is pressed (left button), attempt to select an object.
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		buttonType = me.getButton();
		select(me.getX(), me.getY());
	}
	
	/**
	 * Whenever mouse is dragged, attempt to drag the object.
	 */
	@Override
	public void mouseDragged(MouseEvent me) {
		drag(me.getX(), me.getY());
	}
	
	/**
	 * Whenever mouse is released, complete the move. 
	 * This is a GUI controller.
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		release(me.getX(), me.getY());
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean select(int x, int y) {
		anchor = new Point (x, y);

		// pieces are returned in order of Z coordinate
		Word w = gm.findWord(anchor.x, anchor.y);

		// TEMPORARY: Need to change to allow Poems to be connected to each other
		if (w == null || w.getY() > GameManager.AREA_DIVIDER ) { return false; }

		Poem tmp;
		tmp = gm.getPa().belongsToPoem(w);

		if ( tmp != null) /* selected poem */
		{
			gm.setSelected(tmp);
			gm.getPa().remove(tmp);
			originalx = tmp.getX();
			originaly = tmp.getY();
		}
		else { /* selected a word */

			// no longer in the board since we are moving it around...
			//gm.getUa().remove(w);
			gm.getPa().remove(w);
			gm.setSelected(w);
			originalx = w.getX();
			originaly = w.getY();

		}
		// set anchor for smooth moving
		deltaX = anchor.x - originalx;
		deltaY = anchor.y - originaly;
		// paint will happen once moves. This redraws state to prepare for paint
		panel.redraw();
		return true;
	}	
	
	/** Separate out this function for testing purposes. */
	protected boolean drag (int x, int y) {
		if (buttonType == MouseEvent.BUTTON3) { return false; }
		Entity selected = gm.getSelected();
		
		if (selected == null) { return false; }
		
		if (selected instanceof Word) {
			selected = (Word)gm.getSelected();
			panel.paintWord((Word)selected);
		}
		else {
			selected = (Poem)gm.getSelected();
			panel.paintPoem((Poem)selected);
		}
		/* handle poem instance */
		panel.paintBackground(selected);
		selected.setPosition(x - deltaX, y - deltaY);
		
		panel.repaint();
		return true; 
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		Entity selected = gm.getSelected();

		if (selected == null) { return false; }

		if (selected instanceof Word) {
			selected = (Word)gm.getSelected();
			System.out.println("Selected Entity:" + selected.toString());
		}else {
			selected = (Poem)gm.getSelected();
			System.out.println("Selected Entity:" + selected.toString());
		}

		if (gm.getPa().entityIntersect(selected) == null) { 			// didn't select any word to connect
			revert();
		}else {
			ri = gm.getPa().entityIntersect(selected);     // selected entity is either a word or a poem
			
			if(selected instanceof Word){
				if(ri.idxPoem == -1){ 										//ri.w is a single word
					connectWordToWord((Word)selected);
				}else {
					connectWordToPoem((Word)selected);
				}
			}else{ 	//either connecting a word or poem to an existing poem				// ri.w is a word in a poem
				if(ri.idxPoem == -1){
					revert();
				}else{
					Poem basePoem = ri.p; // get the base poem

					int basePoemY = basePoem.getY();
					int basePoemHeight = basePoem.getHeight();
					int basePoemMidY = basePoemY + basePoemHeight/2; //middle Y of basepoem

					if(y - deltaY > basePoemMidY){
						Poem pbottom = (Poem) selected;
						if(gm.getPa().connectPoemBottom(basePoem, pbottom, pbottom.getX()) == false){
							pbottom.setPosition(originalx, originaly);
							gm.getPa().getPoems().add(pbottom);
						}
					}else{
						Poem ptop = (Poem) selected;
						int ptop_rows = ptop.rows.size(); // number of rows
						Row bottomRowPTop = ptop.rows.get(ptop_rows -1);
						int bottomRowX = bottomRowPTop.getX();
						if(gm.getPa().connectPoemTop(basePoem, ptop, bottomRowX) == false){
							ptop.setPosition(originalx, originaly);
							gm.getPa().getPoems().add(ptop);
						}
					}
					gm.getManipulations().add(new Manipulation(originalx, originaly, selected, MoveType.CONNECT));
					panel.validateUndo(true);
				}
			}																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																														
		}																																																												

		// no longer selected
		gm.setSelected(null);
		panel.redraw();																																																							
		panel.repaint();
		return true;
	}

	/**
	 * Helper function.
	 * Connect a Word to another single Word.
	 * @param word Word to connect
	 */
	private void connectWordToWord(Word word) {
		if (word.getX() < (ri.w.getX() + 0.5 * ri.w.getWidth())) {
			if (!gm.getPa().connectWordLeftWord(ri.w, word)) {
				revert();
			}
			gm.getManipulations().add(new Manipulation(originalx, originaly, gm.getSelected(), MoveType.CONNECT));
			panel.validateUndo(true);
		}
		else {
			if (!gm.getPa().connectWordRightWord(ri.w, word)) {
				revert();
			}
			gm.getManipulations().add(new Manipulation(originalx, originaly, gm.getSelected(), MoveType.CONNECT));
			panel.validateUndo(true);
		}
	}

	/**
	 * Helper function.
	 * Connect a Word to a pre-existing Poem.
	 * @param word Word to connect
	 */
	private void connectWordToPoem(Word word) {
		if (word.getX() < (ri.w.getX() + 0.5 * ri.w.getWidth()) && ri.idxWord == 0) {
			if (!gm.getPa().connectWordLeftPoem(gm.getPa().getPoems().get(ri.idxPoem), word, ri.idxRow)) {
				revert();
				return;
			}
			gm.getManipulations().add(new Manipulation(originalx, originaly, gm.getSelected(), MoveType.CONNECT));
			panel.validateUndo(true);
		} else if (word.getX() >= (ri.w.getX() + 0.5 * ri.w.getWidth())
				&& ri.idxWord == gm.getPa().getPoems().get(ri.idxPoem).getRows().get(ri.idxRow).getWords().size() - 1) {
			if (!gm.getPa().connectWordRightPoem(gm.getPa().getPoems().get(ri.idxPoem), word, ri.idxRow)) {
				revert();
				return;
			}
			gm.getManipulations().add(new Manipulation(originalx, originaly, gm.getSelected(), MoveType.CONNECT));
			panel.validateUndo(true);
			panel.validateRedo(false);
		} 
		else{
			revert();
		}
	}
	
	private void revert() {
		gm.getSelected().setPosition(originalx, originaly);
		gm.getPa().add(gm.getSelected());
	}
}
