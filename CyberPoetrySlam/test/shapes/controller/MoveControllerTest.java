package shapes.controller;

import shapes.model.GameManager;
import shapes.model.Type;
import shapes.model.Word;
import shapes.view.ApplicationPanel;
import junit.framework.TestCase;

public class MoveControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	MoveController mc;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		app = new ApplicationPanel(gm);
		mc = new MoveController(gm, app);
		gm.getPa().add(new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1"));
	}
	
	public void testSelect() {
		mc.select(1, 1);
		assertTrue(gm.getSelected().getValue().equals("test1"));
	}
	
	public void testRelease() {
		mc.register();
		mc.select(1, 1);
		mc.drag(2, 2);
		mc.release(2, 2);
		assertTrue(gm.getPa().getWords().get(0).getValue().equals("test1"));
	}
}