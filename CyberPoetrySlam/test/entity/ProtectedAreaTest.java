package entity;

import junit.framework.TestCase;

public class ProtectedAreaTest extends TestCase {
	// Test the usage of the Singleton pattern
	public void testConstructor() {
		ProtectedArea pa = ProtectedArea.getInstance();
	}

}
