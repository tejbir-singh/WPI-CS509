package shapes.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import shapes.model.GameManager;
import shapes.model.Word;


public class ApplicationPanel extends JPanel {
	private static final long serialVersionUID = -8813450588616485914L;
	GameManager gm;
	Image offscreenImage;
	Graphics offscreenGraphics;
	Graphics canvasGraphics;
	MouseListener activeListener;
	MouseMotionListener activeMotionListener;
	
	/** Properly register new listener (and unregister old one if present). */
	public void setActiveListener(MouseListener ml) {
		this.removeMouseListener(activeListener);
		activeListener = ml;
		if (ml != null) { 
			this.addMouseListener(ml);
		}
	}
	
	/** Properly register new motion listener (and unregister old one if present). */
	public void setActiveMotionListener(MouseMotionListener mml) {
		this.removeMouseMotionListener(activeMotionListener);
		activeMotionListener = mml;
		if (mml != null) {
			this.addMouseMotionListener(mml);
		}
	}

	/**
	 * Construct ApplicationPanel with a Model instance used for information.
	 */
	public ApplicationPanel(GameManager gm) {
		super();
		this.gm = gm;
	}
	
	/** Make sure that image is created as needed. */
	void ensureImageAvailable(Graphics g) {
		if (offscreenImage == null) {
			offscreenImage = this.createImage(this.getWidth(), this.getHeight());
			offscreenGraphics = offscreenImage.getGraphics();
			canvasGraphics = g;
			
			redraw();
		}
	}
	
	public void redraw() {
		// nothing to draw into? Must stop here.
		if (offscreenImage == null) { return; }
		
		// clear the image.
		offscreenGraphics.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		/** Draw all shapes. */
		for (Word w : gm.getUa().getWords()) {
			paintWord(offscreenGraphics, w);
		}
		for (Word w : gm.getPa().getWords()) {
			paintWord(offscreenGraphics, w);
		}
	}
	
	/** 
	 * To Draw within a JPanel, you need to have a protected void method of this name.
	 * Note that the first operation of this method MUST BE to invoke super.paintComponent(g)
	 * 
	 * @param g
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		ensureImageAvailable(g);
		g.drawImage(offscreenImage, 0, 0, getWidth(), getHeight(), this);
		
		// draw selected
		Word selected = gm.getSelected();
		if (selected != null) {
			paintWord(g, selected);
		}
	}
	
	/** Paint the shape directly to the screen */
	public void paintWord(Word w) {
		paintWord(canvasGraphics, w);
	}
	
	/** Paint the shape into the given graphics context. */
	void paintWord(Graphics g, Word w) {
		if (g == null) { return; }
		if (w.getY() >= GameManager.AREA_DIVIDER) {
			g.setColor(Color.gray);
		}
		else {
			g.setColor(Color.red);
		}
		g.fillRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
		g.setColor(Color.black);
		g.drawString(w.getValue(), w.getX() + w.getWidth()/4, w.getY() + w.getHeight());
	}
	
	/** Repaint to the screen just the given part of the image. */
	public void paintBackground(Word w) {
		// Only updates to the screen the given region
		if (canvasGraphics != null) {
			canvasGraphics.drawImage(offscreenImage, w.getX(), w.getY(), w.getWidth(), w.getHeight(), this);
			repaint(w.getX(), w.getY(), w.getWidth(), w.getHeight());
		}
	}
}
