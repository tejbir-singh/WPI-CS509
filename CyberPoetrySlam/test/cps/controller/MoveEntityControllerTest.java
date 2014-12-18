package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.model.GameManager;
import cps.model.Poem;
import cps.model.Row;
import cps.model.Type;
import cps.model.Word;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class MoveEntityControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	MoveEntityController mec;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		app = new ApplicationPanel(gm, new JButton(), new JButton(), new JButton());
		mec = new MoveEntityController(gm, app);
		gm.getPa().add(new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1"));
		
		Word w111 = new Word(1, 1, 3, 1, Type.ADJECTIVE, "word1");
		Word w222 = new Word(4, 1, 5, 1, Type.ADVERB, "word2");
		Word w3 = new Word(3, 2, 6, 1, Type.CONJUNCTION, "word3");
		Word w4 = new Word(9, 2, 9, 1, Type.NOUN, "word4");
		ArrayList<Word> aw1 = new ArrayList<Word>();
		assertEquals(true, aw1.add(w111));
		assertEquals(true, aw1.add(w222));
		ArrayList<Word> aw2 = new ArrayList<Word>();
		assertEquals(true, aw2.add(w3));
		assertEquals(true, aw2.add(w4));
		Row row1 = new Row(aw1);
		Row row2 = new Row(aw2);
		ArrayList<Row> ar1 = new ArrayList<Row>();
		assertEquals(true, ar1.add(row1));
		assertEquals(true, ar1.add(row2));
		Poem poem1 = new Poem(ar1);
		assertEquals(poem1.getRows().size(),2);
		gm.getPa().getPoems().add(poem1);
		assertEquals(gm.getPa().getPoems().size(), 1);
	}
	
	@Override
	protected void tearDown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	
	public void testSelect() {
		mec.select(1, 1);
		assertTrue(((Word) gm.getSelected()).getValue().equals("test1"));
	}
	
	
	public void testRelease() {
		mec.register();
		mec.select(1, 1);
		mec.drag(2, 2);
		mec.release(2, 2);
		assertTrue(gm.getPa().getWords().get(0).getValue().equals("test1"));
	}

	
	public void testMoveWord() {
		gm.getPa().add(new Word(0, 0, 1, 1, Type.ANY,"test2"));
		mec.register();
		mec.select(0,0);
		mec.drag(5, 3);
		mec.release(5, 3);
		Word tmp = gm.getPa().getWord(5, 3);
		assertEquals(tmp.getValue(), "test2");
	}
	
	public void testShiftRow() {
		Poem poem = gm.getPa().getPoems().get(0); 
		mec.mouseRightClick = true; // set for right click
		mec.register();
		mec.select(1, 1); // shift top row
		mec.drag(3,1);
		mec.release(3, 1);
		// verify shift was successfully
		assertEquals(poem.getRows().get(0).getX(), 3);
		
		mec.select(3, 2); // shift top row
		mec.drag(5,2);
		mec.release(5, 2);
		
		// verify shift was successfully
		assertEquals(poem.getRows().get(1).getX(), 5);
		
		mec.select(5, 2); // shift top row
		mec.drag(22,2);
		mec.release(22, 2);
		
		// verify shift was unsuccessfully
		assertEquals(poem.getRows().get(1).getX(), 5);
		
		mec.select(3, 1); // shift top row
		mec.drag(22,2);
		mec.release(22, 2);
		
		// verify shift was unsuccessfully
		assertEquals(poem.getRows().get(0).getX(), 3);
	}
	
	public void testMovePoem(){
		Poem poem = gm.getPa().getPoems().get(0); 
		mec.register();
		mec.select(4, 2); 
		
		mec.drag(13,1);
		mec.release(13, 1);
		
		// verify shift was successfully
		
		assertEquals(poem.getRows().get(0).getX(), 10);
	}
	

	

}