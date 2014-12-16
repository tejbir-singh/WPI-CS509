package cps.controller;

import java.awt.Point;
import java.awt.event.*;

import javax.swing.SwingUtilities;

import cps.model.*;
import cps.view.*;

/**
 * MoveEntityController is responsible for all move-type manipulations.
 * @author Xinjie, Devin
 */

public class MoveEntityController extends MouseAdapter {

	/** Needed for controller behavior. */
	GameManager gm;
	ApplicationPanel panel;
	boolean mouseRightClick = false;
	
	/** Original x,y where shape was before move. */
	int originalX;
	int originalY;
	int originalRowX;
	int originalRowY;
	
	/** Anchor point where first grabbed and delta from that location. */
	Point anchor;
	int deltaX;
	int deltaY;
	int deltaRowX;
	int deltaRowY;
	
	/** Button that started off. */
	int buttonType;
	
	/** row. */
	Row selectedRow;
	Row selectedRowCeilingRow;
	Row selectedRowFloorRow;
	
	/**
	 * Constructor.
	 * @param gm GameManager 
	 * @param panel ApplicationPanel
	 */
	public MoveEntityController(GameManager gm, ApplicationPanel panel) {
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
		if(SwingUtilities.isRightMouseButton(me)){
			mouseRightClick = true;
		}
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
		
		Word temp = null;
		ReturnIndex selectedIdx = new ReturnIndex(-1, -1, -1, temp);
		selectedIdx = gm.getPa().getWordIdx(anchor.x, anchor.y);
		if(mouseRightClick == true){
			// select nothing
			if (selectedIdx == null) {
				return false;
			}
			// record original coordinate of the selected row
			originalRowX = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow).getX();
			originalRowY = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow).getY();
			// set anchor for smooth moving
			deltaRowX = anchor.x - originalRowX;
			deltaRowY = anchor.y - originalRowY;
		}else{ // mouseLeftClick
			// no longer in the board since we are moving it around...
			if (p == null) {
				gm.getUa().remove(w);
				gm.getPa().remove(w);
				if (gm.getSwapManager() != null) {
					gm.getSwapManager().remove(w);
				}
				gm.setSelected(w);
				originalX = w.getX();
				originalY = w.getY();
			} else{
				gm.getPa().remove(p);
				gm.setSelected(p);
				originalX = p.getX();
				originalY = p.getY();
			}
				
			// set anchor for smooth moving
			deltaX = anchor.x - originalX;
			deltaY = anchor.y - originalY;
		}
		
		gm.setSelectedIdx(selectedIdx);
		// paint will happen once moves. This redraws state to prepare for paint
		panel.redraw();
		return true;
	}	
	
	/** Separate out this function for testing purposes. */
	protected boolean drag (int x, int y) {
		if(mouseRightClick == true){
			ReturnIndex selectedIdx = gm.getSelectedIdx();
			
			if (selectedIdx == null) { return false; }
			selectedRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow);
			selectedRow.setPosition(x - deltaRowX, y - deltaRowY);
			if (originalRowY - 2 * selectedRow.getHeight() > y - deltaRowY || 
					y - deltaRowY > originalRowY + 2 * selectedRow.getHeight()){ //restrict drag move within ceiling and floor adjacent rows
				selectedRow.setPosition(originalRowX, originalRowY);
			}
			
			panel.paintPoem(gm.getPa().getPoems().get(selectedIdx.idxPoem));
			panel.redraw();
			panel.repaint();
			
			return true;
		}else{ // mouseLeftClick
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
			
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		ReturnIndex selectedIdx = gm.getSelectedIdx();
		if(mouseRightClick == true){
			boolean validshift = true; // to record if the shiftRow is valid
			if (selectedIdx == null) { return false; }
			selectedRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow);
			if (selectedIdx.idxRow > 0){
				// check if the shiftRow movement dislodges selected row's ceiling row
				selectedRowCeilingRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow - 1);
				if(selectedRow.getX() + selectedRow.getWidth() < selectedRowCeilingRow.getX() ||
						selectedRow.getX() > selectedRowCeilingRow.getX() + selectedRowCeilingRow.getWidth()){
					selectedRow.setPosition(originalRowX, originalRowY);
					validshift = false;
				}
			}
			if (selectedIdx.idxRow < gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().size() - 1){
				// check if the shiftRow movement dislodges selected row's floor row
				selectedRowFloorRow = gm.getPa().getPoems().get(selectedIdx.idxPoem).getRows().get(selectedIdx.idxRow + 1);
				if(selectedRow.getX() + selectedRow.getWidth() < selectedRowFloorRow.getX() ||
						selectedRow.getX() > selectedRowFloorRow.getX() + selectedRowFloorRow.getWidth()){
					selectedRow.setPosition(originalRowX, originalRowY);
					validshift = false;
				}
			}
			if(gm.getPa().doesIntersect(gm.getPa().getPoems().get(selectedIdx.idxPoem))){
				// check if the shiftRow movement intersect with other entities
				selectedRow.setPosition(originalRowX, originalRowY);
				validshift = false;
			}
			if (validshift == true){
				// Only satisfied all the check above can the shiftRow movement be valid
				selectedRow.setPosition(x - deltaRowX, originalRowY);
				gm.getManipulations().push(new Manipulation(originalRowX, originalRowY, selectedRow, MoveType.MOVE));
			}
		}else{ // mouseLeftClick
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
				gm.getManipulations().push(new Manipulation(originalX, originalY, selected, MoveType.MOVE));
			} else{  // Poem
				gm.getManipulations().push(new Manipulation(originalX, originalY, selectedpoem, MoveType.MOVE));
			}
		}
		
		gm.setSelected(null);
		gm.setSelectedIdx(null);
		panel.validateUndo(true);
		panel.validateRedo(false);
		panel.redraw();
		panel.repaint();
		return true;
	}
}

