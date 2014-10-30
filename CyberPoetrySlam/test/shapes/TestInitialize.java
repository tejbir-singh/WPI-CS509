package shapes;

import shapes.model.GameManager;
import shapes.view.Application;
import junit.framework.TestCase;

public class TestInitialize extends TestCase {
	public void testWindow() {
		Application app = new Application(GameManager.getInstance());
		app.setVisible(true);
	}
}
