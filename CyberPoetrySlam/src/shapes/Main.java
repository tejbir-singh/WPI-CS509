package shapes;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import shapes.model.GameManager;
import shapes.model.GameManagerMemento;
import shapes.view.Application;

public class Main {
static final String defaultStorage = "CBS.storage";
	
	public static void storeState(GameManager gm, String location) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(location));
			oos.writeObject(gm.getState());
		} catch (Exception e) {
			
		}
		
		if (oos != null) {
			try { oos.close(); } catch (IOException ioe) { } 
		}
		
	}
	
	public static GameManager loadState(String location) {
		 ObjectInputStream ois = null;
		 GameManager gm = GameManager.getInstance();
		 try {
			 ois = new ObjectInputStream (new FileInputStream(location));
			 GameManagerMemento m = (GameManagerMemento) ois.readObject();
			 ois.close();
			 gm.restore(m);
		 } catch (Exception e) {
			 // unable to perform restore
		 }
		 
		 // close down safely
		 if (ois != null) {
			 try { ois.close(); } catch (IOException ioe) { }
		 }
		 
		 return gm;
	}

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final GameManager gm = loadState(defaultStorage);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Application frame = new Application(gm);
					frame.setVisible(true);
					frame.addWindowListener (new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							storeState(gm, defaultStorage);
							System.exit(0);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

}
