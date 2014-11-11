package cps.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cps.model.GameManager;
import cps.model.Poem;
import cps.model.ProtectedArea;
import cps.model.Row;
import cps.model.Word;
import cps.view.ApplicationPanel;

/**
 * @author marek
 */

public class PublishPoem extends MouseAdapter{
	
	/** needed for controller behavior */
	GameManager gm;
	ApplicationPanel panel;
	ProtectedArea pa;
	Poem finishedPoem;
	public final String publishedPoems = "published.txt";
	
	/** Button that started off. */
	int buttonType;
	
	/** Constructor	 */
		public PublishPoem (GameManager gm, ApplicationPanel panel) {
			this.gm = gm;
			this.panel = panel;
		}
		
	/** Set up press events but no motion events. */
		public void register() {
			panel.setActiveListener(this);
			panel.setActiveMotionListener(this);
		}

		/** Whenever mouse is pressed (left button), attempt to select an object. */
		@Override
		public void mousePressed(MouseEvent me) {
			buttonType = me.getButton();
			selectPoem(me.getX(), me.getY());
		}		
		/** Separate out this function for testing purposes. */
		protected boolean selectPoem(int x, int y) {
			Word word = gm.findWord(x, y);
			if (buttonType == MouseEvent.BUTTON3) { return false; }
			ArrayList<Poem> allPoems = gm.getPa().getPoems();
			if (allPoems == null) { return false; }
			
			for (int i=0;i<allPoems.size();i++) {
				if (allPoems.get(i) != null) {
					
					Poem currentPoem = allPoems.get(i);
					for (Row r : currentPoem.getRows()) {
						if (r.getWords().contains(word)) {
							publishSelected(currentPoem);
							break;
						}
					}		
				}
			}
			return true;
		}
			//Still need to clear them from the board!!!!
		
			//panel.paintBackground();
			//panel.paintWord();
			//panel.repaint();
		
		/**
		 * this published the finished poem to the "wall"
		 * @param finishedPoem
		 */
		protected void publishSelected(Poem finishedPoem) {
		try {
			String content = finishedPoem.toString();   //I dont know if this is the right way to do it
			
			File file = new File(publishedPoems);
 
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content + "\n");
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
}
