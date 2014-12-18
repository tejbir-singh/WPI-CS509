package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.DisconnectEntityController;
import cps.model.GameManager;
import cps.model.Poem;
import cps.model.Row;
import cps.model.Type;
import cps.model.Word;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class DisconnectEntityControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	DisconnectEntityController dwc;
	Word w1, w2;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton(), new JButton());
		dwc = new DisconnectEntityController(gm, app);
		w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
		w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
		gm.getPa().connectWordLeftWord(w1, w2);
	}
	
	public void testSelect() {
		if (gm.getPa().getPoems().isEmpty()) {
			gm.getPa().connectWordLeftWord(w1, w2);
		}
		dwc.select(1, 1);
		assertTrue(((Word) gm.getSelected()).getValue().equals("test1"));
	}
	
	public void testRelease() {
		dwc.register();
		dwc.select(1, 1);
		dwc.drag(101, 101);
		dwc.release(101, 101);
		assertTrue(gm.getPa().getPoems().isEmpty());
	}
	
	public void test1() {
		dwc.register();
		Word w111 = new Word(1, 1, 3, 1, Type.ADJECTIVE, "word1");
		Word w222 = new Word(4, 1, 5, 1, Type.ADVERB, "word2");
		Word w3 = new Word(5, 2, 4, 1, Type.CONJUNCTION, "word3");
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
		gm.getPa().getPoems().add(poem1);
		
	}
	
	public void test2() {
		dwc.mouseRightClick = true;
		dwc.register();
		Word w111 = new Word(1, 1, 3, 1, Type.ADJECTIVE, "word1");
		//Word w222 = new Word(4, 1, 5, 1, Type.ADVERB, "word2");
		Word w3 = new Word(5, 2, 4, 1, Type.CONJUNCTION, "word3");
		Word w4 = new Word(9, 2, 9, 1, Type.NOUN, "word4");
		ArrayList<Word> aw1 = new ArrayList<Word>();
		assertEquals(true, aw1.add(w111));
		//assertEquals(true, aw1.add(w222));
		ArrayList<Word> aw2 = new ArrayList<Word>();
		assertEquals(true, aw2.add(w3));
		assertEquals(true, aw2.add(w4));
		Row row1 = new Row(aw1);
		Row row2 = new Row(aw2);
		ArrayList<Row> ar1 = new ArrayList<Row>();
		assertEquals(true, ar1.add(row1));
		assertEquals(true, ar1.add(row2));
		Poem poem1 = new Poem(ar1);
		gm.getPa().getPoems().add(poem1);
		
		
		dwc.select(2, 1);
		dwc.drag(101, 101);
		dwc.release(101, 101);
		assertEquals(gm.getPa().getWords().get(0).value, "word1");
		assertEquals(gm.getPa().getPoems().get(1).getRows().get(0).getWords().size(), 2);
		
	}
}
