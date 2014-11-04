package cps;

import cps.model.GameManager;
import cps.view.Application;
import junit.framework.TestCase;

public class TestInitialize extends TestCase {
	public void testWindow() {
		Application app = new Application(GameManager.getInstance());
		app.setVisible(true);
	}
}
