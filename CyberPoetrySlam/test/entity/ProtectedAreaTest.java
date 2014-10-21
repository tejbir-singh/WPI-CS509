package entity;

import java.util.ArrayList;

import junit.framework.TestCase;

public class ProtectedAreaTest extends TestCase {
	ProtectedArea pa = ProtectedArea.getInstance();
	Word word = new Word(0, 0, 0, 0, null, null);
	
	protected void setUp() {
		pa.words = new ArrayList<Word>();
		pa.poems = new ArrayList<Poem>();
		word.value = "test";
		word.x = 1;
		word.y = 1;
		word.width = 1;
		word.height = 1;
		word.type = Type.NOUN;
	}
	
	public void testSingletonPattern() {
		ProtectedArea pa2 = ProtectedArea.getInstance();
		assertEquals(pa, pa2);
	}
	
	// test add(Word w), added by Xinjie
	public void testAdd(){
		Word word1 = new Word(1, 1, 2, 2, null, null); // will intersect with word
		Word word2 = new Word(1, 2, 2, 2, null, null); // will not intersect with word
		assertEquals(true, pa.add(word));
		assertEquals(false, pa.add(word1));
		assertEquals(true, pa.add(word2));
		assertEquals(2, pa.words.size());
	}

	public void testAddWordToProtected() {
		pa.add(word);
		assertNotNull(pa.getWord(1, 1));
	}
	
	public void testRemoveWordFromProtected() {
		pa.add(word);
		pa.remove(word);
		assertNull(pa.getWord(1, 1)); 
	}
	
	public void testMoveEntity(){
		pa.add(word);
		pa.moveEntity(word, 2, 3);
		assertEquals(pa.getWord(2, 3), word);
	}
	
	public void testIntersectWord() {
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(1, 2, 2, 3, null, null);
		Word word3 = new Word(16, 13, 8, 9, null, null);
		/*word1.value = "test1";
		word1.x = 3;
		word1.y = 4;
		word1.width = 5;
		word1.height = 5;
		word2.value = "test2";
		word2.x = 1;
		word2.y = 2;
		word2.width = 2;
		word2.height = 3;*/
		pa.add(word1);
		pa.add(word2);
		
		assertFalse(pa.moveEntity(word2, 7, 2)); // word2 will intersect with word1 if moving to (7, 2)
		assertEquals(1, word2.x); // word2 goes back to its previous location
		
		assertEquals(true, pa.moveEntity(word2, 1, 3)); // word2 will not intersect with word1 if moving to (1, 3)
		assertEquals(3, word2.y); // word2 moves to (1, 3)
		
		pa.add(word3);
		assertEquals(false, pa.moveEntity(word2, 16, 13)); //word2 will intersect with word3 if moving to (16, 13)
	}
}
