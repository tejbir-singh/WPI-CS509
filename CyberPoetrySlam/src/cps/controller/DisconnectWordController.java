package cps.controller;

import java.awt.Point;
import java.awt.event.*;
import javax.swing.SwingUtilities;

import cps.model.*;
import cps.view.*;

public class DisconnectWordController extends MouseAdapter {
	/** Needed for controller behavior. */
	GameManager gm;
	ApplicationPanel panel;
	boolean mouseRightClick = false;
	
	
	/** Original x,y where shape was before move. */
	int originalWordX;
	int originalWordY;
	int originalRowX;
	int originalRowY;
	
	/** Anchor point where first grabbed and delta from that location. */
	Point anchor;
	int deltaWordX;
	int deltaWordY;
	int deltaRowX;
	int deltaRowY;
	
	/** Button that started off. */
	int buttonType;
	
	/** Moved row. */
	Row selectedRow;
	
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
		if(SwingUtilities.isRightMouseButton(me)){
			mouseRightClick = true;
		}
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
		ReturnIndex selectedIdx = new ReturnIndex(-1, -1, -1, w);
		
		selectedIdx = gm.getPa().getWordIdx(anchor.x, anchor.y);
		
		// select nothing
		if (selectedIdx == null) {
			return false;
		}
		
		// check if ri.w is the edge word in a poem
		if (selectedIdx.idxWord != 0 && selectedIdx.idxWord != gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow).getWords().size() - 1) {
			return false;
		}
		
		if(mouseRightClick == false){ // disconnectWord Controller, mouse left click
			gm.setSelected(selectedIdx.w);
			// record original coordinate of selected word
			originalWordX = gm.getSelected().getX();
			originalWordY = gm.getSelected().getY();
			// set anchor for smooth moving
			deltaWordX = anchor.x - originalWordX;
			deltaWordY = anchor.y - originalWordY;
			// set selected word
			
		}else{ // disconnectRow Controller, mouse right click
			// record original coordinate of selected row
			originalRowX = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow).getX();
			originalRowY = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow).getY();
			// set anchor for smooth moving
			deltaRowX = anchor.x - originalRowX;
			deltaRowY = anchor.y - originalRowY;
		}
		
		gm.setSelectedIdx(selectedIdx);
		
		// paint will happen once moves. This redraws state to prepare for paint
		panel.redraw();
		return true;
	}	


	/** Separate out this function for testing purposes. */
	protected boolean drag (int x, int y) {
		if (mouseRightClick == true){ // disconnectRow Controller
			ReturnIndex selectedIdx = gm.getSelectedIdx();
			
			
			if (selectedIdx == null) { return false; }
			selectedRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow);
			selectedRow.setPosition(x - deltaRowX, y - deltaRowY);
			
			panel.paintPoem(gm.getPa().getPoems().get(selectedIdx.idxPoem));
			panel.redraw();
			panel.repaint();
			
			return true;
		}else{ // disconnectWord Controller
			Word selected = (Word) gm.getSelected();
			
			if (selected == null) { return false; }
			
			panel.paintBackground(selected);
			selected.setPosition(x - deltaWordX, y - deltaWordY);
			panel.paintWord(selected);
			panel.repaint();

			return true;
		}
		
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		ReturnIndex selectedIdx = gm.getSelectedIdx();
		if (mouseRightClick == true){ // disconnectRow Controller
			if (selectedIdx == null) { return false; }
			selectedRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow);
			if (!gm.getPa().disconnectRow(selectedIdx.idxPoem, selectedIdx.idxRow, x-deltaRowX, y-deltaRowY)) {
				selectedRow.setPosition(originalRowX, originalRowY);
			}
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			else { // Hi, Devin! Here's problems of Undo/Redo!!!!!!!!!
				gm.getManipulations().add(new Manipulation(originalRowX, originalRowY, selectedRow, MoveType.DISCONNECT));
				panel.validateUndo(true);
				panel.validateRedo(false);
			}	
		}else{ // disconnectWord Controller
			Word selected = (Word) gm.getSelected();
			
			if (selected == null) { return false; }
			
			//Check if the word can be disconnect without intersection, if yes, make the disconnectWord, otherwise, go back to the original position
			if (!gm.getPa().disconnectWord(selectedIdx.idxPoem, selectedIdx.idxRow, selectedIdx.idxWord, selected.getX(), selected.getY())) {
				selected.setPosition(originalWordX, originalWordY);
			}
			else {
				gm.getManipulations().add(new Manipulation(originalWordX, originalWordY, selected, MoveType.DISCONNECT));
				panel.validateUndo(true);
				panel.validateRedo(false);
			}
		}
		// no longer selected
		gm.setSelected(null);
		gm.setSelectedIdx(null);
					
		panel.redraw();
		panel.repaint();
		return true;
	}
}

