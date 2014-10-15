package entity;

import java.util.ArrayList;

import junit.framework.TestCase;

public class GameManagerTest extends TestCase {
	GameManager gm = GameManager.getInstance();
	Word word = new Word();
	
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
}
