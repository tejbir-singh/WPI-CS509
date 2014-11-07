package cps.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cps.model.GameManager;
import cps.model.Manipulation;
import cps.model.Poem;
import cps.model.Row;
import cps.model.Word;
import cps.model.Entity;

public class ApplicationPanel extends JPanel {
	private static final long serialVersionUID = -8813450588616485914L;
	GameManager gm;
	Image offscreenImage;
	Graphics offscreenGraphics;
	Graphics canvasGraphics;
	MouseListener activeListener;
	MouseMotionListener activeMotionListener;
	JButton undoButton;
	
	
	/**
	 * Constructor.
	 */
	public ApplicationPanel(GameManager gm, JButton undoButton) {
		super();
		this.gm = gm;
		this.undoButton = undoButton;
	}

	/** Properly register new listener (and unregister old one if present). */
	public void setActiveListener(MouseListener ml) {
		this.removeMouseListener(activeListener);
		activeListener = ml;
		if (ml != null) {
			this.addMouseListener(ml);
		}
	}

	/**
	 * Properly register new motion listener (and unregister old one if
	 * present).
	 */
	public void setActiveMotionListener(MouseMotionListener mml) {
		this.removeMouseMotionListener(activeMotionListener);
		activeMotionListener = mml;
		if (mml != null) {
			this.addMouseMotionListener(mml);
		}
	}

	/** Make sure that image is created as needed. */
	void ensureImageAvailable(Graphics g) {
		if (offscreenImage == null) {
			offscreenImage = this
					.createImage(this.getWidth(), this.getHeight());
			offscreenGraphics = offscreenImage.getGraphics();
			canvasGraphics = g;
			redraw();
		}
	}

	public void redraw() {
		// nothing to draw into? Must stop here.
		if (offscreenImage == null) {
			return;
		}

		// clear the image.
		offscreenGraphics.clearRect(0, 0, this.getWidth(), this.getHeight());

		/** Draw all shapes. */
		for (Word w : gm.getUa().getWords()) {
			paintWord(offscreenGraphics, w);
		}
		for (Word w : gm.getPa().getWords()) {
			paintWord(offscreenGraphics, w);
		}
		for (Poem p : gm.getPa().getPoems()) {
			paintPoem(offscreenGraphics, p);
		}
	}

	/**
	 * To Draw within a JPanel, you need to have a protected void method of this
	 * name. Note that the first operation of this method MUST BE to invoke
	 * super.paintComponent(g)
	 * 
	 * @param g
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		ensureImageAvailable(g);
		g.drawImage(offscreenImage, 0, 0, getWidth(), getHeight(), this);
		Word selectedword = null;
		Poem selectedpoem = null;
		// draw selected
		if (gm.getSelected() instanceof Word){
			selectedword = (Word) gm.getSelected();
			if (selectedword != null) {
				paintWord(g, selectedword);
			}
		} else{
			selectedpoem = (Poem) gm.getSelected();
			if (selectedpoem != null) {
				paintPoem(g, selectedpoem);
			}
		}
		
	}

	/** Paint the Word directly to the screen */
	public void paintWord(Word w) {
		paintWord(canvasGraphics, w);
	}

	/** Paint the Poem directly to the screen */
	public void paintPoem(Poem p) {
		paintPoem(canvasGraphics, p);
	}

	/** Paint the Word into the given graphics context. */
	void paintWord(Graphics g, Word w) {
		if (g == null) { return; }
		if (w.getY() >= GameManager.AREA_DIVIDER) {
			g.setColor(Color.gray);
		}
		else {
			g.setColor(Color.cyan);
		}
		g.fillRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
		g.setColor(Color.black);
		g.drawString(w.getValue(), w.getX() + w.getWidth()/4, w.getY() + w.getHeight());
	}
	
	/** Paint the Poem into the given graphics context. */
	void paintPoem(Graphics g, Poem p) {
		for (Row r : p.rows){
			for(Word w : r.words){
				g.setColor(Color.green);
				g.fillRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
				g.setColor(Color.black);
				g.drawString(w.getValue(), w.getX() + w.getWidth()/4, w.getY() + w.getHeight());
			}
			
		}
	}

	/** Repaint to the screen just the given part of the image. */
	public void paintBackground(Entity e) {
		// Only updates to the screen the given region
		if (e instanceof Word){
			if (canvasGraphics != null) {
				canvasGraphics.drawImage(offscreenImage, e.getX(), e.getY(),
						e.getWidth(), e.getHeight(), this);
				repaint(e.getX(), e.getY(), e.getWidth(), e.getHeight());
			}
		} else {
			if (canvasGraphics != null) {
				for (Row row : ((Poem) e).getRows()){
					canvasGraphics.drawImage(offscreenImage, row.getX(), row.getY(),
						row.getWidth(), row.getHeight(), this);
				repaint(row.getX(), row.getY(), row.getWidth(), row.getHeight());
				}
				
			}
		}
		
	}
	
	/**
	 * Check if Undo is a valid option and adjust the button accordingly. 
	 */
	public void isUndoValid() {
		if (!gm.getManipulations().isEmpty()) {
			undoButton.setEnabled(true);
			for (Manipulation m : gm.getManipulations()) {
				if (m.getEntity() instanceof Word) {
					System.out.print(m.getMoveType() + " " + ((Word) m.getEntity()).getValue() + ", ");
				}
				else {
					System.out.print(m.getMoveType() + " Poem, ");
				}
			}
			System.out.println();
		}
		else {
			undoButton.setEnabled(false);
		}
	}
}
