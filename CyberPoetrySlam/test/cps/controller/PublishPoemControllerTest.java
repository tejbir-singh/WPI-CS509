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

public class PublishPoemControllerTest extends TestCase {

		
		GameManager gm;
		ApplicationPanel app;
		PublishPoemController pp;
		ConnectEntityController cw;
		Poem testPoem;
			
		@Override
		protected void setUp() {
			gm = GameManager.getInstance();
			gm.getPa().setWords(new ArrayList<Word>());
			gm.getPa().setPoems(new ArrayList<Poem>());
			gm.getUa().setWords(new ArrayList<Word>());
			app = new ApplicationPanel(gm, new JButton(), new JButton(),null);
			cw = new ConnectEntityController(gm, app);
			Word w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
			Word w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
			gm.getPa().add(w1);
			gm.getPa().add(w2);
			pp = new PublishPoemController(gm, app);
		}
		
		public void testPublish() {
			
			cw.register();
			cw.select(1, 1);
			cw.drag(100, 100);
			cw.release(100, 100);
			assertFalse(gm.getPa().getPoems().isEmpty());
			
			pp.register();
			pp.selectPoem(100, 100);
			assertTrue(gm.getPa().getPoems().isEmpty());
		}
		

	}

