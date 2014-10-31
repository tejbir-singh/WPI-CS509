package shapes.controller;

import java.awt.Point;
import java.awt.event.*;

import shapes.model.*;
import shapes.view.*;

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
	
	/** Constructor holds onto key manager objects. */
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
		if (w == null) { return false; }
		
		// no longer in the board since we are moving it around...
		gm.getUa().remove(w);
		gm.getPa().remove(w);
		gm.setSelected(w);
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
		Word selected = gm.getSelected();
		
		if (selected == null) { return false; }
		
		panel.paintBackground(selected);
		int oldx = selected.getX();
		int oldy = selected.getY();
		
		selected.setPosition(x - deltaX, y - deltaY);
		
		if (gm.getPa().doesIntersect(selected)) {
			selected.setPosition(oldx, oldy);
		} else {
			panel.paintWord(selected);
			panel.repaint();
		}
	
		return true;
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		Word selected = gm.getSelected();
		if (selected == null) { return false; }

		// now released we can move
		if (y >= panel.AREA_DIVIDER) {			// check if it's in the unprotected area
			gm.getUa().add(selected);
		}
		else {
			gm.getPa().add(selected);
		}
		
		// no longer selected
		gm.setSelected(null);
		
		panel.redraw();
		panel.repaint();
		return true;
	}
}
