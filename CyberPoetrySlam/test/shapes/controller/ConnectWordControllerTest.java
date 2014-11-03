package shapes.controller;

import java.util.ArrayList;

import shapes.model.GameManager;
import shapes.model.Type;
import shapes.model.Word;
import shapes.view.ApplicationPanel;
import junit.framework.TestCase;

public class ConnectWordControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	ConnectWordController mc;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		app = new ApplicationPanel(gm);
		mc = new ConnectWordController(gm, app);
		Word w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
		Word w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
	}
	
	public void testSelect() {
		mc.select(1, 1);
		assertTrue(gm.getSelected().getValue().equals("test1"));
	}
	
	public void testRelease() {
		mc.register();
		mc.select(1, 1);
		mc.drag(100, 100);
		mc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(0));
	}
}
