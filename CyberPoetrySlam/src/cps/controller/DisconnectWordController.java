package cps.controller;

import java.awt.Point;
import java.awt.event.*;

import cps.model.*;
import cps.view.*;

public class DisconnectWordController extends MouseAdapter {
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
	public DisconnectWordController(GameManager gm, ApplicationPanel panel) {
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
		Word w = null;
		ReturnIndex ri = new ReturnIndex(-1, -1, -1, w);
		
		ri = gm.getPa().getWordIdx(anchor.x, anchor.y);
		
		// select nothing
		if (ri == null) {
			return false;
		}
		
		// check if ri.w is the edge word in a poem
		if (ri.idxWord != 0 && ri.idxWord != gm.getPa().getPoems().get(ri.idxPoem).getRows().get(ri.idxRow).getWords().size() - 1) {
			return false;
		}

		gm.setSelected(ri.w);
		gm.setSelectedIdx(ri);

		originalx = gm.getSelected().getX();
		originaly = gm.getSelected().getY();

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
		Word selected = (Word) gm.getSelected();
		
		if (selected == null) { return false; }
		
		panel.paintBackground(selected);
		selected.setPosition(x - deltaX, y - deltaY);
		panel.paintWord(selected);
		panel.repaint();

		return true;
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		ReturnIndex selectedIdx = gm.getSelectedIdx();
		Word selected = (Word) gm.getSelected();
		
		if (selected == null) { return false; }
		
		//Check if the word can be disconnect without intersection, if yes, make the disconnectWord, otherwise, go back to the original position
		if (!gm.getPa().disconnectWord(selectedIdx.idxPoem, selectedIdx.idxRow, selectedIdx.idxWord, selected.getX(), selected.getY())) {
			selected.setPosition(originalx, originaly);
		}
		else {
			gm.getManipulations().add(new Manipulation(originalx, originaly, selected, MoveType.DISCONNECT));
			panel.validateUndo(true);
			panel.validateRedo(false);
		}
		
		// no longer selected
		
		gm.setSelected(null);
		gm.setSelectedIdx(null);
		
		panel.redraw();
		panel.repaint();
		return true;
	}
}

