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
		assertTrue(gm.getPa().getWords().get(1).getValue().equals("test1"));
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
	
	public void testMovePoem() {
		ArrayList<Word> row1 = new ArrayList<Word>();
		row1.add((new Word(3,2,1,1,Type.ADJECTIVE, "word1")));
		row1.add((new Word(5,0,1,1,Type.ANY, "word2")));
		
		Row r1 = new Row(row1);
		
		ArrayList<Word> row2 = new ArrayList<Word>();
		row2.add(new Word(3,3,1,1,Type.ANY,"word3"));
		row2.add(new Word(3,5,1,1,Type.ANY,"word4"));
		
		Row r2 = new Row(row2);
		ArrayList<Row> poem1 = new ArrayList<Row>();
		poem1.add(r1);
		poem1.add(r2);
		
		Poem p1 = new Poem(poem1);
		gm.getPa().add((Poem)p1); // add the poem to the protected area
		
		mec.register();
		mec.select(p1.getX(), p1.getY());
		mec.drag(20, 10);
		mec.release(20, 10);
		
		Poem tmp = gm.getPa().getPoems().get(0);
		
		assertEquals(tmp.getX(), 20);
		assertEquals(tmp.getY(), 10);
	}
	
	
	public void testRowShift() {
		ArrayList<Word> row1 = new ArrayList<Word>();
		row1.add((new Word(1,1,1,1,Type.ADJECTIVE, "word1")));
		row1.add((new Word(5,0,1,1,Type.ANY, "word2")));
		
		Row r1 = new Row(row1);
		
		ArrayList<Word> row2 = new ArrayList<Word>();  
		row2.add(new Word(3,3,1,1,Type.ANY,"word3"));
		row2.add(new Word(7,5,1,1,Type.ANY,"word4"));
		
		Row r2 = new Row(row2);
		
		ArrayList<Word> row3 = new ArrayList<Word>();
		row3.add(new Word(9,1,1,1,Type.ANY,"word5"));
		row3.add(new Word(10,5,1,1,Type.ANY,"word6"));
		
		Row r3 = new Row(row3);
		
		ArrayList<Word> row = new ArrayList<Word>();  
		row2.add(new Word(3,3,1,1,Type.ANY,"word3"));
		row2.add(new Word(7,5,1,1,Type.ANY,"word4"));
		
		ArrayList<Row> poem1 = new ArrayList<Row>();
		poem1.add(r1);
		poem1.add(r2);
		poem1.add(r3);
		
		Poem p1 = new Poem(poem1);
		gm.getPa().add((Poem)p1); // add the poem to the protected area
	
		assertEquals(r1.getX(), p1.getX());
		assertEquals(r1.getY(), p1.getY());
	
		mec.mouseRightClick = true; // set for right click
		mec.register();
		mec.select(p1.getX(), p1.getY()); // shift top row
		
		mec.drag(3,1);
		mec.drag(3, 1);
		
		// verify shift was successfully
		
		assertEquals(p1.getX(), r1.getX());
		assertEquals(p1.getY(), r1.getY());
		
		Row tmp = p1.getRows().get(2); // get middle row
		mec.select(tmp.getX(), tmp.getY()); 
		System.out.println(tmp.getX());
		System.out.println(p1.getRows().get(0).getX());

		mec.drag(20, 20);
		mec.release(20, 20);
		assertNotSame(tmp.getX(), 20);
		assertNotSame(tmp.getY(), 20);
		}
	
	public void teardown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	

}