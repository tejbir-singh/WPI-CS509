package shapes.controller;

import java.awt.Point;
import java.awt.event.*;

import shapes.model.*;
import shapes.view.*;

public class ConnectWordController extends MouseAdapter {

	/** Needed for controller behavior. */
	GameManager gm;
	ApplicationPanel panel;
	ReturnIndex ri;
	ReturnIndex temp;

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
	public ConnectWordController(GameManager gm, ApplicationPanel panel) {
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
	 * Whenever mouse is released, complete the move. This is a GUI controller.
	 */
	@Override
	public void mouseReleased(MouseEvent me) {
		release(me.getX(), me.getY());
	}

	/** Separate out this function for testing purposes. */
	protected boolean select(int x, int y) {
		anchor = new Point(x, y);

		Word w = gm.findWord(anchor.x, anchor.y);
		if (w == null) {
			return false;
		}

		gm.getPa().remove(w);
		gm.setSelected(w);
		originalx = w.getX();
		originaly = w.getY();

		// set anchor for smooth moving
		deltaX = anchor.x - originalx;
		deltaY = anchor.y - originaly;

		panel.redraw();
		return true;
	}

	/** Separate out this function for testing purposes. */
	protected boolean drag(int x, int y) {
		if (buttonType == MouseEvent.BUTTON3) {
			return false;
		}
		Word selected = gm.getSelected();

		if (selected == null) {
			return false;
		}

		panel.paintBackground(selected);
		selected.setPosition(x - deltaX, y - deltaY);
		panel.paintWord(selected);
		panel.repaint();

		return true;
	}

	/** Separate out this function for testing purposes. */
	protected boolean release(int x, int y) {
		Word selected = gm.getSelected();
		if (selected == null) {
			return false;
		}
		ri = gm.getPa().entityIntersect(selected);

		if (ri == null) {// || gm.getPa().boundaryIntersect(selected)) { // didn't
																	// select
																	// any word
																	// to
																	// connect
			selected.setPosition(originalx, originaly);
			gm.getPa().add(selected);
		} else { // valid selection
			if (ri.dexpoem == -1 || isEdgeWord(ri.w)) { // check if edge word
				if (ri.p == null) { // connecting a word to another word
					if (selected.getX() < (ri.w.getX() + 0.5 * ri.w.getWidth())) {
						if (!gm.getPa().connectWordLeftWord(ri.w, selected)) {
							revert();
						}
					} else {
						if (!gm.getPa().connectWordRightWord(ri.w, selected)) {
							revert();
						}
					}
				} else {
					if (selected.getX() < (ri.w.getX() + 0.5 * ri.w.getWidth())) {
						if (!gm.getPa().connectWordLeftPoem(ri.p, selected, ri.dexrow)) {
							revert();
						}
					} else {
						if (!gm.getPa().connectWordRightPoem(ri.p, selected, ri.dexrow)) {
							revert();
						}
					}
				}
			} else { // invalid location within poem selected
				selected.setPosition(originalx, originaly);
				gm.getPa().add(selected);
			}
		}

		// no longer selected
		gm.setSelected(null);

		panel.redraw();
		panel.repaint();
		return true;
	}

	/**
	 * Helper function which determines whether an edge word was selected.
	 * @param w Word to check
	 * @return true if an edge word was selected
	 */
	private boolean isEdgeWord(Word w) {
		Row r = ri.p.getRows().get(ri.dexrow);
		return r.getWords().get(r.getWords().size() - 1) == w
				|| ri.p.getRows().get(ri.dexrow).getWords().get(0) == w;
	}

	/**
	 * Helper function to revert changes to the selected Entity's position.
	 */
	private void revert() {
		gm.getSelected().setPosition(originalx, originaly);
		gm.getPa().add(gm.getSelected());
	}
}
