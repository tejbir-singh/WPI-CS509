package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.DisconnectWordController;
import cps.model.GameManager;
import cps.model.Poem;
import cps.model.Type;
import cps.model.Word;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class DisconnectWordControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	DisconnectWordController mc;
	Word w1, w2;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton());
		mc = new DisconnectWordController(gm, app);
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
		mc.select(1, 1);
		assertTrue(((Word) gm.getSelected()).getValue().equals("test1"));
	}
	
	public void testRelease() {
		mc.register();
		mc.select(1, 1);
		mc.drag(101, 101);
		mc.release(101, 101);
		assertTrue(gm.getPa().getPoems().isEmpty());
	}
}
