package entity;

import java.util.ArrayList;

import junit.framework.TestCase;

public class ProtectedAreaTest extends TestCase {
	ProtectedArea pa = ProtectedArea.getInstance();
	public void setUp() {
		pa.words = new ArrayList<Word>();
	}
	
	public void testSingletonPattern() {
		ProtectedArea pa2 = ProtectedArea.getInstance();
		assertEquals(pa, pa2);
	}

	public void testAddWordToProtected() {
		Word word = new Word();
		word.value = "test";
		pa.add(word);
		assertNotNull(pa.getWord("test"));
	}
	
	public void testRemoveWordFromProtected() {
		Word word = new Word();
		word.value = "test";
		pa.add(word);
		pa.remove(word);
		assertNull(pa.getWord("test")); 
	}
	
	public void testMoveEntity() {
		Word word = new Word();
		word.value = "test";
		word.x = 1;
		word.y = 1;
		pa.add(word);
		pa.moveEntity(word, 2, 3);
		assertTrue(pa.getWord("test").x == 2 && pa.getWord("test").y == 3);
	}
	
	public void testIntersect() {
		Word word1 = new Word();
		Word word2 = new Word();
		word1.value = "test1";
		word1.x = 1;
		word1.y = 1;
		word1.width = 5;
		word1.height = 5;
		word2.value = "test2";
		word2.x = 6;
		word2.y = 6;
		word2.width = 5;
		word2.height = 5;
		pa.add(word1);
		pa.add(word2);
		
		assertFalse(pa.moveEntity(word2, 2, 2));
	}
}
