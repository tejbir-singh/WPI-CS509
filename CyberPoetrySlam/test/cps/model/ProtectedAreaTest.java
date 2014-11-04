package cps.model;

import java.util.ArrayList;

import cps.model.Poem;
import cps.model.ProtectedArea;
import cps.model.Row;
import cps.model.Type;
import cps.model.Word;
import junit.framework.TestCase;

public class ProtectedAreaTest extends TestCase {
	ProtectedArea pa = ProtectedArea.getInstance();
	Word word = new Word(1, 1, 1, 1, Type.NOUN, "test");
	Poem poem1;
	Word w5 = new Word(22, 33, 4, 1, Type.NOUN, "word5");
	
	protected void setUp() {
		pa.words = new ArrayList<Word>();
		pa.poems = new ArrayList<Poem>();
		
		Word w1 = new Word(1, 1, 3, 1, Type.ADJECTIVE, "word1");
		Word w2 = new Word(4, 1, 5, 1, Type.ADVERB, "word2");
		Word w3 = new Word(3, 2, 6, 1, Type.CONJUNCTION, "word3");
		Word w4 = new Word(9, 2, 9, 1, Type.NOUN, "word4");
		ArrayList<Word> aw1 = new ArrayList<Word>();
		assertEquals(true, aw1.add(w1));
		assertEquals(true, aw1.add(w2));
		ArrayList<Word> aw2 = new ArrayList<Word>();
		assertEquals(true, aw2.add(w3));
		assertEquals(true, aw2.add(w4));
		Row row1 = new Row(aw1);
		Row row2 = new Row(aw2);
		ArrayList<Row> ar1 = new ArrayList<Row>();
		assertEquals(true, ar1.add(row1));
		assertEquals(true, ar1.add(row2));
		poem1 = new Poem(ar1);
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
		assertEquals(false, pa.add(poem1));
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
		//move Word
		pa.add(word);
		pa.moveEntity(word, 2, 3);
		assertEquals(pa.getWord(2, 3), word);
		
		//move Poem
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.add(w5));
		assertEquals(1, pa.poems.get(0).x);
		assertEquals(1, pa.poems.get(0).rows.get(0).x);
		assertEquals(3, pa.poems.get(0).rows.get(1).x);
		assertEquals(true, pa.moveEntity(poem1, 0, 0));
		assertEquals(0, pa.poems.get(0).x);
		assertEquals(0, pa.poems.get(0).rows.get(0).x);
		assertEquals(2, pa.poems.get(0).rows.get(1).x);
		assertEquals(2, pa.poems.get(0).rows.get(1).words.get(0).x);
	}
	
	public void testWordIntersectWord() {
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(1, 2, 2, 3, null, null);
		Word word3 = new Word(16, 13, 8, 9, null, null);
		pa.add(word1);
		pa.add(word2);
		
		assertFalse(pa.moveEntity(word2, 7, 2)); // word2 will intersect with word1 if moving to (7, 2)
		assertEquals(1, word2.x); // word2 goes back to its previous location
		
		assertEquals(true, pa.moveEntity(word2, 1, 3)); // word2 will not intersect with word1 if moving to (1, 3)
		assertEquals(3, word2.y); // word2 moves to (1, 3)
		
		pa.add(word3);
		assertEquals(false, pa.moveEntity(word2, 16, 13)); //word2 will intersect with word3 if moving to (16, 13)
	}
	
	public void testWordIntersectPoem(){
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.add(w5));
		assertEquals(false, pa.moveEntity(pa.words.get(0), 0, 2));
	}
	
	public void testPoemIntersectPoem(){
		Word w1 = new Word(21, 1, 3, 1, Type.ADJECTIVE, "word1");
		Word w2 = new Word(24, 1, 5, 1, Type.ADVERB, "word2");
		Word w3 = new Word(23, 2, 6, 1, Type.CONJUNCTION, "word3");
		Word w4 = new Word(29, 2, 9, 1, Type.NOUN, "word4");
		ArrayList<Word> aw1 = new ArrayList<Word>();
		assertEquals(true, aw1.add(w1));
		assertEquals(true, aw1.add(w2));
		ArrayList<Word> aw2 = new ArrayList<Word>();
		assertEquals(true, aw2.add(w3));
		assertEquals(true, aw2.add(w4));
		Row row1 = new Row(aw1);
		Row row2 = new Row(aw2);
		ArrayList<Row> ar1 = new ArrayList<Row>();
		assertEquals(true, ar1.add(row1));
		assertEquals(true, ar1.add(row2));
		Poem poem2 = new Poem(ar1);
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.add(poem2));
		assertEquals(true, pa.add(w5));
		assertEquals(false, pa.moveEntity(pa.poems.get(1), 0, 2));
		assertEquals(true, pa.moveEntity(pa.poems.get(1), 20, 2));
		
	}
	
	public void testPoemIntersectWord(){
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.add(w5));
		assertEquals(false, pa.moveEntity(pa.poems.get(0), 22, 33));
	}
	
	public void testConnectWordLeftWord(){
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(1, 2, 2, 5, null, null);
		pa.add(word1);
		pa.add(word2);
		assertEquals(2, pa.words.size());
		assertEquals(0, pa.poems.size());
		assertEquals(true, pa.connectWordLeftWord(word1, word2));
		assertEquals(0, pa.words.size());
		assertEquals(1, pa.poems.size());
		assertEquals(1, pa.poems.get(0).x);
		assertEquals(4, pa.poems.get(0).y);
	}
	
	public void testConnectWordLeftWordIntersect(){
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(11, 22, 2, 5, null, null);
		Word word3 = new Word(1, 4, 1, 5, null, null);
		assertEquals(true, pa.add(word1));
		assertEquals(true, pa.add(word2));
		assertEquals(true, pa.add(word3));
		assertEquals(3, pa.words.size());
		assertEquals(0, pa.poems.size());
		assertEquals(false, pa.connectWordLeftWord(word1, word2));
		assertEquals(3, pa.words.size());
		assertEquals(0, pa.poems.size());
	}
	
	public void testConnectWordRightWord(){
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(1, 2, 2, 5, null, null);
		pa.add(word1);
		pa.add(word2);
		assertEquals(2, pa.words.size());
		assertEquals(0, pa.poems.size());
		assertEquals(true, pa.connectWordRightWord(word1, word2));
		assertEquals(0, pa.words.size());
		assertEquals(1, pa.poems.size());
		assertEquals(3, pa.poems.get(0).x);
		assertEquals(4, pa.poems.get(0).y);
		assertEquals(8,pa.poems.get(0).rows.get(0).words.get(1).x);
	}
	
	public void testConnectWordRightWordIntersect(){
		Word word1 = new Word(3, 4, 5, 5, null, null);
		Word word2 = new Word(11, 22, 2, 5, null, null);
		Word word3 = new Word(9, 4, 1, 5, null, null);
		assertEquals(true, pa.add(word1));
		assertEquals(true, pa.add(word2));
		assertEquals(true, pa.add(word3));
		assertEquals(3, pa.words.size());
		assertEquals(0, pa.poems.size());
		assertEquals(false, pa.connectWordRightWord(word1, word2));
		assertEquals(3, pa.words.size());
		assertEquals(0, pa.poems.size());
	}
	
	public void testConnectWordLeftPoem(){
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.moveEntity(poem1, 3, 1));
		assertEquals(true, pa.add(w5));
		assertEquals(true, pa.add(word));
		assertEquals(false, pa.connectWordLeftPoem(poem1, pa.words.get(0), 0));
		
		assertEquals(true, pa.moveEntity(poem1, 60, 60));
		assertEquals(true, pa.connectWordLeftPoem(poem1, pa.words.get(0), 0));
		assertEquals(56, poem1.x);
		assertEquals(56, poem1.rows.get(0).x);
		
	}
	
	public void testConnectWordRightPoem(){
		assertEquals(true, pa.add(poem1));
		assertEquals(true, pa.moveEntity(poem1, 14, 33));
		assertEquals(true, pa.add(w5));
		assertEquals(true, pa.add(word));
		assertEquals(false, pa.connectWordRightPoem(poem1, pa.words.get(1), 0));
		
		assertEquals(true, pa.moveEntity(poem1, 60, 60));
		assertEquals(true, pa.connectWordRightPoem(poem1, pa.words.get(1), 0));
		assertEquals(60, poem1.x);
		assertEquals(60, poem1.rows.get(0).x);
		
	}
	
	public void testDisconnectWord(){
		assertEquals(true, pa.words.add(w5));
		assertEquals(true, pa.poems.add(poem1));
		assertEquals(1, pa.words.size());
		assertEquals(false, pa.disconnectWord(0, 0, 0, 22, 33));
		assertEquals(true, pa.disconnectWord(0, 0, 0, 90, 90));
		assertEquals(2, pa.words.size());
		assertEquals(4, pa.poems.get(0).x);
		assertEquals(4, pa.poems.get(0).rows.get(0).x);
	}
	
}
