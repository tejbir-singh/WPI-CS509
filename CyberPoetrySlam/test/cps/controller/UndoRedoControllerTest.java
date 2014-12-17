package cps.controller;

import java.util.ArrayList;

import javax.swing.JButton;

import cps.controller.UndoRedoController.URType;
import cps.model.GameManager;
import cps.model.Poem;
import cps.model.Type;
import cps.model.Word;
import cps.view.ApplicationPanel;
import junit.framework.TestCase;

public class UndoRedoControllerTest extends TestCase {
	GameManager gm;
	ApplicationPanel app;
	Word w1, w2, w3, w4;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton(), new JButton());
		w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
		w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
		w3 = new Word(104, 104, 1, 1, Type.adverb, "test3");
		w4 = new Word(102, 102, 1, 1, Type.verb, "test4");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
		gm.getPa().add(w3);
		gm.getPa().add(w4);
	}
	
	@Override
	protected void tearDown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	
	public void testUndoRedoMove() {
		MoveEntityController mec = new MoveEntityController(gm, app);
		mec.select(1, 1);
		mec.drag(2, 2);
		mec.release(2, 2);
		assertNotNull(gm.findWord(2, 2));
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertNotNull(gm.findWord(1, 1));
		
		new UndoRedoController(gm, app, URType.REDO).process();
		assertNotNull(gm.findWord(2, 2));
	}
	
	public void testUndoRedoMoveUaToPa() {
		MoveEntityController mec = new MoveEntityController(gm, app);
		mec.select(1, 1);
		mec.drag(1, GameManager.AREA_DIVIDER + 10);
		mec.release(1, GameManager.AREA_DIVIDER + 10);
		assertNotNull(gm.getUa().getWord(1, GameManager.AREA_DIVIDER + 10));
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertTrue(gm.getUa().getWords().isEmpty());
	}
	
	public void testUndoRedoMoveFromUAAndPa() {
		MoveEntityController mec = new MoveEntityController(gm, app);
		mec.select(1, 1);
		mec.drag(2 + GameManager.AREA_DIVIDER, 2 + GameManager.AREA_DIVIDER);
		mec.release(2 + GameManager.AREA_DIVIDER, 2 + GameManager.AREA_DIVIDER);
		assertFalse(gm.getUa().getWords().isEmpty());
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertTrue(gm.getUa().getWords().isEmpty());
		
		new UndoRedoController(gm, app, URType.REDO).process();
		assertFalse(gm.getUa().getWords().isEmpty());
	}
	
	public void testUndoRedoConnect() {
		ConnectEntityController cwc = new ConnectEntityController(gm, app);
		cwc.select(1, 1);
		cwc.drag(100, 100);
		cwc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(0));
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertTrue(gm.getPa().getPoems().isEmpty());
		
		new UndoRedoController(gm, app, URType.REDO).process();
		assertFalse(gm.getPa().getPoems().isEmpty());
	}
	
	public void testUndoRedoDisconnect() {
		ConnectEntityController cwc = new ConnectEntityController(gm, app);
		cwc.select(1, 1);
		cwc.drag(100, 100);
		cwc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(0));
		
		DisconnectEntityController dwc = new DisconnectEntityController(gm, app);
		dwc.select(100, 100);
		dwc.drag(1, 1);
		dwc.release(1, 1);
		assertTrue(gm.getPa().getPoems().isEmpty());
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertFalse(gm.getPa().getPoems().isEmpty());
		
		new UndoRedoController(gm, app, URType.REDO).process();
		assertTrue(gm.getPa().getPoems().isEmpty());
	}
	
	public void testUndoRedoDisconnectMultilinePoem() {
		ConnectEntityController cwc1 = new ConnectEntityController(gm, app);
		cwc1.select(1, 1);
		cwc1.drag(100, 100);
		cwc1.release(100, 100);
		
		ConnectEntityController cwc2 = new ConnectEntityController(gm, app);
		cwc2.select(102, 102);
		cwc2.drag(104, 104);
		cwc2.release(104, 104);
		
		assertNotNull(gm.getPa().getPoems().get(0));
		assertNotNull(gm.getPa().getPoems().get(1));
		
		ConnectEntityController cwc3 = new ConnectEntityController(gm, app);
		cwc3.select(100, 100);
		cwc3.drag(104,104);
		cwc3.release(104, 104);
		assertNotNull(gm.getPa().getPoems().get(0));
		
		DisconnectEntityController dwc = new DisconnectEntityController(gm, app);
		dwc.mouseRightClick = true;
		dwc.select(104, 104);
		dwc.drag(100, 100);
		dwc.release(100, 100);
		assertNotNull(gm.getPa().getPoems().get(1));
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertEquals(gm.getPa().getPoems().size(), 1);
	}
}
