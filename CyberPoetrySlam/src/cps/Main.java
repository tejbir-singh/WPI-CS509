package cps;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cps.model.GameManager;
import cps.model.GameManagerMemento;
import cps.model.SwapManager;
import cps.view.Application;

public class Main {
static final String defaultStorage = "CPS.storage";
	/**
	 * Store the game's state.
	 * @param gm GameManager to store
	 * @param location location to save the data
	 */
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
	
	/**
	 * Revert the game to a previously saved state.
	 * @param location file location containing restore data
	 * @return restored GameManager
	 */
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
					final SwapManager sm = SwapManager.getInstance();
					sm.setAppPanel(frame.getAppPanel());
					if (!sm.connect("localhost")) {
						sm.connect("gheineman.cs.wpi.edu");
					}
					gm.setSwapManager(sm);
					frame.setVisible(true);
					frame.addWindowListener (new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							storeState(gm, defaultStorage);
							sm.shutdown();
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
