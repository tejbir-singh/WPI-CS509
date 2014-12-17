package cps.controller;

import javax.swing.JButton;

import cps.model.GameManager;
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
		assertTrue(gm.getPa().getWords().get(0).getValue().equals("test1"));
	}
}
