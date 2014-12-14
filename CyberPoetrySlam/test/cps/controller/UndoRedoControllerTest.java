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
	Word w1, w2;
	
	@Override
	protected void setUp() {
		gm = GameManager.getInstance();
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
		app = new ApplicationPanel(gm, new JButton(), new JButton(), new JButton());
		w1 = new Word(1, 1, 1, 1, Type.ADJECTIVE, "test1");
		w2 = new Word(100, 100, 1, 1, Type.NOUN, "test2");
		gm.getPa().add(w1);
		gm.getPa().add(w2);
	}
	
	@Override
	protected void tearDown() {
		gm.getPa().setWords(new ArrayList<Word>());
		gm.getPa().setPoems(new ArrayList<Poem>());
		gm.getUa().setWords(new ArrayList<Word>());
	}
	
	public void testUndoRedoMove() {
		MoveController mc = new MoveController(gm, app);
		mc.select(1, 1);
		mc.drag(2, 2);
		mc.release(2, 2);
		assertNotNull(gm.findWord(2, 2));
		
		new UndoRedoController(gm, app, URType.UNDO).process();
		assertNotNull(gm.findWord(1, 1));
		
		new UndoRedoController(gm, app, URType.REDO).process();
		assertNotNull(gm.findWord(2, 2));
	}
	
	public void testUndoRedoMoveFromUAAndPa() {
		MoveController mc = new MoveController(gm, app);
		mc.select(1, 1);
		mc.drag(2 + GameManager.AREA_DIVIDER, 2 + GameManager.AREA_DIVIDER);
		mc.release(2 + GameManager.AREA_DIVIDER, 2 + GameManager.AREA_DIVIDER);
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
}
