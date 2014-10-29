package shapes.model;

import java.util.ArrayList;

import junit.framework.TestCase;

public class UnprotectedAreaTest extends TestCase {
	UnprotectedArea ua = UnprotectedArea.getInstance();
	Word word = new Word(0, 0, 0, 0, null, null);
	
	protected void setUp() {
		ua.words = new ArrayList<Word>();
		word.x = 1;
		word.y = 1;
		word.type = Type.NOUN;
		word.value = "test";
		word.height = 5;
		word.width = 5;
	}
	
	public void testSingletonPattern() {
		UnprotectedArea ua2 = UnprotectedArea.getInstance();
		assertEquals(ua, ua2);
	}
	
	public void testAddWordToUnprotected() {	
		ua.add(word);
		assertNotNull(ua.getWord(1, 1));
	}
	
	public void testRemoveWordFromUnprotected() {
		ua.add(word);
		ua.remove(word);
		assertNull(ua.getWord(1, 1)); 
	}
	
	public void testSearchUnprotectedArea() {
		Word word2 = new Word(0, 0, 0, 0, null, null);
		Word word3 = new Word(0, 0, 0, 0, null, null);
		word2.value = "test2";
		word3.value = "not included";
		ua.add(word);
		ua.add(word2);
		ua.add(word3);
		ArrayList<Word> words = new ArrayList<Word>();
		words.add(word);
		words.add(word2);
		
		ArrayList<Word> results = ua.search("test");
		
		for (Word word : results) {
			assertTrue(words.contains(word));
		}
	}
	
	public void testFilterUnprotectedArea() {
		Word word2 = new Word(0, 0, 0, 0, null, null);
		Word word3 = new Word(0, 0, 0, 0, null, null);
		word2.type = Type.NOUN;
		word3.type = Type.ADJECTIVE;
		ua.add(word);
		ua.add(word2);
		ua.add(word3);
		ArrayList<Word> words = new ArrayList<Word>();
		words.add(word);
		words.add(word2);
		
		ArrayList<Type> types = new ArrayList<Type>();
		types.add(Type.NOUN);
		types.add(Type.VERB);
		ArrayList<Word> results = ua.filter(types);
		
		for (Word word : results) {
			assertTrue(words.contains(word));
		}
	}
}
