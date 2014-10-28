import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import entity.GameManager;
import entity.GameManagerMemento;

public class Main {
static final String defaultStorage = "CBS.storage";
	
	static void storeState(GameManager gm, String location) {
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
	
	static GameManager loadState(String location) {
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

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
