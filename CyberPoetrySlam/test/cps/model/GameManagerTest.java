package cps.model;

import java.io.File;
import java.util.ArrayList;

import cps.Main;
import cps.model.GameManager;
import cps.model.Poem;
import cps.model.Word;
import junit.framework.TestCase;

public class GameManagerTest extends TestCase {
	GameManager gm = GameManager.getInstance();
	Word word = new Word(0, 0, 1, 1, null, null);
	
	public void setUp() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		word.x = 1;
		word.y = 1;
	}
	
	public void testSingleton() {
		GameManager gm2 = GameManager.getInstance();
		assertEquals(gm, gm2);
	}
	
	public void testProtect() {
		gm.getUa().add(word);
		gm.protect(word);
		assertNull(gm.getUa().getWord(word.x, word.y));
		assertNotNull(gm.getPa().getWord(word.x, word.y));
	}
	
	public void testRelease() {
		gm.getPa().add(word);
		gm.release(word);
		assertNull(gm.getPa().getWord(word.x, word.y));
		assertNotNull(gm.getUa().getWord(word.x, word.y));
	}
	
	public void testRestoreState() {
		File tmpStorage = null;
		try {
			tmpStorage = File.createTempFile("memento", ".bin");
		} catch (Exception e) {
			fail ("unable to create temporary file");
		}
	
		Main.storeState(gm, tmpStorage.getAbsolutePath());
		gm.getUa().add(word);
		Main.loadState(tmpStorage.getAbsolutePath());
		assertFalse(gm.getUa().remove(word));
	}
	
	public void testIsPartOfPoem() {
		Word word2 = new Word(2, 2, 1, 1, null, null);
		gm.getPa().connectWordLeftWord(word, word2);
		assertNotNull(gm.getPa().belongsToPoem(word));
	}
}
