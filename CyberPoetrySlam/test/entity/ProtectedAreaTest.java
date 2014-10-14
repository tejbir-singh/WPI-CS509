package entity;

import junit.framework.TestCase;

public class ProtectedAreaTest extends TestCase {
	ProtectedArea pa = ProtectedArea.getInstance();
	
	public void testSingletonPattern() {
		ProtectedArea pa2 = ProtectedArea.getInstance();
		assertEquals(pa, pa2);
	}

	public void testAddWordToProtected() {
		Word word = new Word();
		word.value = "test";
		pa.add(word);
		assertNotNull(pa.getWord("test"));
	}
}
