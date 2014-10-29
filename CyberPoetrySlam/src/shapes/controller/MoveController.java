package shapes.controller;

import java.awt.Point;
import java.awt.event.*;

import shapes.model.*;
import shapes.view.*;

public class MoveController extends MouseAdapter {

	/** Needed for controller behavior. */
	Model model;
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
	public MoveShapeController(Model model, ApplicationPanel panel) {
		this.model = model;
		this.panel = panel;
	}

	/** Set up press events but no motion events. */
	public void register() {
		panel.setActiveListener(this);
		panel.setActiveMotionListener(this);
	}

	/**
	 * Whenever mouse is pressed (left button), attempt to select object.
	 * This is a GUI controller.
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		buttonType = me.getButton();
		select(me.getX(), me.getY());
	}
	
	/**
	 * Whenever mouse is dragged, attempt to start object.
	 * This is a GUI controller.
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
		Shape s = model.getBoard().findShape(anchor.x, anchor.y);
		if (s == null) { return false; }
		
		// no longer in the board since we are moving it around...
		model.getBoard().remove(s);
		model.setSelected(s);
		originalx = s.getX();
		originaly = s.getY();
			
		// set anchor for smooth moving
		deltaX = anchor.x - originalx;
		deltaY = anchor.y - originaly;
		
		// paint will happen once moves. This redraws state to prepare for paint
		panel.redraw();
		return true;
	}	
	
	/** Separate out this function for testing purposes. */
	protected boolean drag (int x, int y) {
		// no board? no behavior! No dragging of right-mouse buttons...
		if (buttonType == MouseEvent.BUTTON3) { return false; }
		Shape selected = model.getSelected();
		
		if (selected == null) { return false; }
		
		panel.paintBackground(selected);
		int oldx = selected.getX();
		int oldy = selected.getY();
		
		selected.setLocation(x - deltaX, y - deltaY);
		
		boolean ok = true;
		for (Shape s : model.getBoard()) {
			if (s.intersects(selected)) {
				ok = false;
				break;
			}
		}
		
		if (!ok) {
			selected.setLocation(oldx, oldy);
		} else {
			panel.paintShape(selected);
			panel.repaint();
		}
	
		return true;
	}
	
	/** Separate out this function for testing purposes. */
	protected boolean release (int x, int y) {
		Shape selected = model.getSelected();
		if (selected == null) { return false; }

		// now released we can create Move
		model.getBoard().add(selected);
		MoveShape move = new MoveShape(selected, originalx, originaly, selected.getX(), selected.getY());
		if (move.execute()) {
			model.recordMove(move);
		}
		
		// no longer selected
		model.setSelected(null);
		
		panel.redraw();
		panel.repaint();
		return true;
	}
}
