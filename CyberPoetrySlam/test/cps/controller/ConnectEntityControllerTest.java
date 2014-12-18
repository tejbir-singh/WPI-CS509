package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.ConnectEntityController;
import cps.model.GameManager;
import cps.model.Row;
import cps.model.Type;
import cps.model.Word;
import cps.model.Poem;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class ConnectEntityControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	ConnectEntityController cwc;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton(), null);
		cwc = new ConnectEntityController(gm, app);
		Word w1 = new Word(50, 50, 1, 1, Type.ADJECTIVE, "test1");
		Word w2 = new Word(100, 100, 4, 1, Type.NOUN, "test2");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
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
		gm.getPa().getPoems().add(poem1);
		
		Word w11 = new Word(1, 10, 3, 1, Type.ADJECTIVE, "word1");
		Word w22 = new Word(4, 10, 5, 1, Type.ADVERB, "word2");
		Word w33 = new Word(3, 11, 6, 3, Type.CONJUNCTION, "word3");
		Word w44 = new Word(9, 11, 9, 3, Type.NOUN, "word4");
		ArrayList<Word> aw3 = new ArrayList<Word>();
		assertEquals(true, aw3.add(w11));
		assertEquals(true, aw3.add(w22));
		ArrayList<Word> aw4 = new ArrayList<Word>();
		assertEquals(true, aw4.add(w33));
		assertEquals(true, aw4.add(w44));
		Row row3 = new Row(aw3);
		Row row4 = new Row(aw4);
		ArrayList<Row> ar2 = new ArrayList<Row>();
		assertEquals(true, ar2.add(row3));
		assertEquals(true, ar2.add(row4));
		Poem poem2 = new Poem(ar2);
		gm.getPa().getPoems().add(poem2);
		
	}
	
	@Override
	protected void tearDown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	
	public void testSelect() {
		cwc.select(50, 50);
		assertTrue(((Word) gm.getSelected()).getValue().equals("test1"));
	}
	
	
	public void testWordLeftConnectWord() {
		cwc.register();
		cwc.select(50, 50);
		cwc.drag(100, 100);
		cwc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(0));
	}
	
	public void testWordLeftConnectPoem() {
		cwc.select(50, 50);
		cwc.drag(17, 2);
		cwc.release(17, 2);
		assertEquals(gm.getPa().getPoems().get(0).rows.get(1).words.size(), 3);
	}
	
	public void testWordRightConnectWord() {
		cwc.register();
		cwc.select(50, 50);
		cwc.drag(103, 100);
		cwc.release(103, 100);
		assertNotNull(gm.getPa().getPoems().get(1));
	}
	
	public void testWordRightConnectPoem() {
		cwc.select(50, 50);
		cwc.drag(4, 2);
		cwc.release(4, 2);
		assertEquals(gm.getPa().getPoems().get(0).rows.get(1).words.size(), 3);
	}
	
	public void testRevert() {
		cwc.select(1, 1);
		cwc.drag(4, 2);
		cwc.release(4, 2);
		assertEquals(gm.getPa().getPoems().get(1).rows.get(1).getY(), 2);
	}
	
	public void testPoemTopConnectPoem() {
		assertEquals(gm.getPa().getPoems().size(), 2);
		cwc.select(1, 1);
		cwc.drag(5, 11);
		cwc.release(5, 11);
		assertEquals(gm.getPa().getPoems().size(), 1);
	}
	
	public void testPoemBottomConnectPoem() {
		assertEquals(gm.getPa().getPoems().size(), 2);
		cwc.select(1, 1);
		cwc.drag(5, 13);
		cwc.release(5, 13);
		assertEquals(gm.getPa().getPoems().size(), 1);
	}
	
}
