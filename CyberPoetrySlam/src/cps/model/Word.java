package cps.model;

import java.io.Serializable;

/**
 * Word is the most basic type of Entity.
 * @author Devin
 */
public class Word extends Entity implements Serializable {
	private static final long serialVersionUID = -5436132699662013706L;
	public String value;
	public Type type;
	
	/**
	 * 
	 * @param x x-coordinate position
	 * @param y y-coordinate position
	 * @param w width
	 * @param h height
	 * @param t type
	 * @param value String value of the word being created
	 */
	public Word(int x, int y, int w, int h, Type t, String value){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.type = t;
		this.value = value;	
	}
	
	/**
	 * Change the position of the Word.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;	
	}
	
	// getters and setters
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}
