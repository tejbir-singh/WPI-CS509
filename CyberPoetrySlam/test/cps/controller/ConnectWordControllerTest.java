package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.ConnectEntityController;
import cps.model.GameManager;
import cps.model.Type;
import cps.model.Word;
import cps.model.Poem;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class ConnectWordControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	ConnectEntityController cwc;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton());
		cwc = new ConnectEntityController(gm, app);
		Word w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
		Word w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
	}
	
	@Override
	protected void tearDown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	
	public void testSelect() {
		cwc.select(1, 1);
		assertTrue(((Word) gm.getSelected()).getValue().equals("test1"));
	}
	
	public void testRelease() {
		cwc.register();
		cwc.select(1, 1);
		cwc.drag(100, 100);
		cwc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(0));
	}
}
