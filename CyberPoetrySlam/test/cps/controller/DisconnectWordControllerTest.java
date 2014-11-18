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
	DisconnectWordController dwc;
	Word w1, w2;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton());
		dwc = new DisconnectWordController(gm, app);
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
}
