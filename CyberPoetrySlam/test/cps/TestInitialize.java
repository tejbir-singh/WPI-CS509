package cps;

import cps.model.GameManager;
import cps.model.SwapManager;
import cps.view.Application;
import junit.framework.TestCase;

public class TestInitialize extends TestCase {
	public void testWindow() {
		final Application frame = new Application(GameManager.getInstance());
		final SwapManager sm = SwapManager.getInstance();
		sm.setAppPanel(frame.getAppPanel());
		frame.setVisible(true);
	}
}
