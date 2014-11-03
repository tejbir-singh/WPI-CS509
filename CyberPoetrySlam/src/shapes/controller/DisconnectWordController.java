package shapes.controller;

import java.awt.Point;
import java.awt.event.*;

import shapes.model.*;
import shapes.view.*;

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
			
		// select nothing
		if (gm.getPa().getWordIdx(anchor.x, anchor.y) == null) { 
			return false; 
		}
		
		ri = gm.getPa().getWordIdx(anchor.x, anchor.y);
		
		// check if ri.w is the edge word in a poem
		if (ri.dexword != 0 && ri.dexword != gm.getPa().getPoems().get(ri.dexpoem).getRows().get(ri.dexrow).getWords().size() - 1 ){
			return false;
		}
		
		Word selected = gm.getPa().getPoems().get(ri.dexpoem).getRows().get(ri.dexrow).getWords().get(ri.dexword);
		gm.setSelectedIdx(ri);
		gm.setSelected(selected);
		
		originalx = selected.getX();
		originaly = selected.getY();
			
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
		selected.setPosition(x - deltaX, y - deltaY);
		panel.paintWord(selected);
		panel.repaint();

		return true;
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		ReturnIndex selectedidx = gm.getSelectedIdx();
		Word selected = gm.getSelected();
		
		if (selected == null) { return false; }
		
		//Check if the word can be disconnect without intersection, if yes, make the disconnectWord, otherwise, go back to the original position
		if (!gm.getPa().disconnectWord(selectedidx.dexpoem, selectedidx.dexrow, selectedidx.dexword, selected.getX(), selected.getY())){
			selected.setPosition(originalx, originaly);
		}
		System.out.println(gm.getPa().getPoems().size());
		System.out.println(gm.getPa().getWords().size());
		
		// no longer selected
		gm.setSelected(null);
		gm.setSelectedIdx(null);
		
		panel.redraw();
		panel.repaint();
		return true;
	}
}

