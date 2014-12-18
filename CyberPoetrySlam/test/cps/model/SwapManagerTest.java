package cps.model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.RequestSwapController;
import cps.Main;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class SwapManagerTest extends TestCase {
	SwapManager s = SwapManager.getInstance();
	
	protected void setUp() throws Exception {
		super.setUp();
		Files.deleteIfExists(Paths.get("CPS.storage"));
		this.s.words = new ArrayList<Word>();
		s = SwapManager.getInstance();
		s.connect("localhost");						// NOTE: local broker required
		s.gm = GameManager.getInstance();
		s.panel = new ApplicationPanel(s.gm, new JButton(), new JButton(), new JButton());
	}
	
	public void testSwapForAny() throws InterruptedException {
		Main.main(null);
		Word w1 = new Word(1, 1 + GameManager.SWAP_AREA_DIVIDER, 1, 1, Type.ADJECTIVE, "test1");
		s.words.add(w1);
		Thread.sleep(10000);
		new RequestSwapController(s.gm, s.panel, "*", "*").process();
		Thread.sleep(1000);
		assertTrue(s.words.isEmpty());
	}
}
