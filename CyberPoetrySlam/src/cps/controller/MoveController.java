package cps.controller;

import java.awt.Point;
import java.awt.event.*;

import cps.model.*;
import cps.view.*;

public class MoveController extends MouseAdapter {

	/** Needed for controller behavior. */
	GameManager gm;
	ApplicationPanel panel;
	
	/** Original x,y where shape was before move. */
	int originalx;
	int originaly;
	
	/** Anchor point where first grabbed and delta from that location. */
	Point anchor;
	int deltaX;
	int deltaY;
	
	/** Button that started off. */
	int buttonType;
	
	/**
	 * Constructor.
	 * @param gm GameManager 
	 * @param panel ApplicationPanel
	 */
	public MoveController(GameManager gm, ApplicationPanel panel) {
		this.gm = gm;
		this.panel = panel;
	}

	/** Set up press events but no motion events. */
	public void register() {
		panel.setActiveListener(this);
		panel.setActiveMotionListener(this);
	}

	/**
	 * Whenever mouse is pressed (left button), attempt to select object.
	 * @param me MouseEvent trigger
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		buttonType = me.getButton();
		select(me.getX(), me.getY());
	}
	
	/**
	 * Whenever mouse is dragged, attempt to drag the object.
	 * @param me MouseEvent trigger
	 */
	@Override
	public void mouseDragged(MouseEvent me) {
		drag(me.getX(), me.getY());
	}
	
	/**
	 * Whenever mouse is released, complete the move. 
	 * @param me MouseEvent trigger
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
		Poem p = gm.getPa().belongsToPoem(w);
		
		// TEMPORARY: Need to change to allow Poems to be connected to each other
		if (w == null) { return false; }
		
		// no longer in the board since we are moving it around...
		if (p == null) {
			gm.getUa().remove(w);
			gm.getPa().remove(w);
			if (gm.getSwapManager() != null) {
				gm.getSwapManager().remove(w);
			}
			gm.setSelected(w);
		} else{
			//System.out.println(gm.getPa().getPoems().size());
			gm.getPa().remove(p);
			//System.out.println(gm.getPa().getPoems().size());
			gm.setSelected(p);
		}
		
		originalx = w.getX();
		originaly = w.getY();
			
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
		Word selected = null;
		Poem selectedpoem = null;
		if (gm.getSelected() instanceof Word) {
			selected = (Word) gm.getSelected();
		} else{
			selectedpoem = (Poem) gm.getSelected();
		}
		
		if (selected == null && selectedpoem == null) { return false; }
		
		if (gm.getSelected() instanceof Word) {
			panel.paintBackground(selected);
			int oldx = selected.getX();
			int oldy = selected.getY();
			
			selected.setPosition(x - deltaX, y - deltaY);
			
			if (gm.getPa().doesIntersect(selected)) {
				selected.setPosition(oldx, oldy);
			}
			else {
				panel.paintWord(selected);
				panel.repaint();
			}
			return true;
		} 
		else { // Poem
			panel.paintBackground(selectedpoem);
			int oldx = selectedpoem.getX();
			int oldy = selectedpoem.getY();
			
			selectedpoem.setPosition(x - deltaX, y - deltaY);
			
			if (gm.getPa().doesIntersect(selectedpoem)) {
				selectedpoem.setPosition(oldx, oldy);
			} 
			else {
				panel.paintPoem(selectedpoem);
				panel.repaint();
			}
			return true;
		}	
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		Word selected = null;
		Poem selectedpoem = null;
		if (gm.getSelected() instanceof Word) {
			selected = (Word) gm.getSelected();
		} else{ //Poem
			selectedpoem = (Poem) gm.getSelected();
		}
		
		if (selected == null && selectedpoem == null) { return false; }

		// now released we can move
		if (y >= GameManager.SWAP_AREA_DIVIDER) {				// Swap Area
			gm.getSwapManager().add(gm.getSelected());
		}
		else if (y >= GameManager.AREA_DIVIDER && y <= GameManager.SWAP_AREA_DIVIDER) {		// check if it is in the unprotected area
			gm.getUa().add(gm.getSelected());
		} 
		else {
			gm.getPa().add(gm.getSelected());			
		}
		
		// record the move to the stack
		if (gm.getSelected() instanceof Word){
			gm.getManipulations().push(new Manipulation(originalx, originaly, selected, MoveType.MOVE));
		} else{  // Poem
			gm.getManipulations().push(new Manipulation(originalx, originaly, selectedpoem, MoveType.MOVE));
		}
		

		gm.setSelected(null);
		panel.validateUndo(true);
		panel.validateRedo(false);
		panel.redraw();
		panel.repaint();
		return true;
	}
}
