package cps.model;

import java.io.Serializable;

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
	
	/*public boolean intersect(Word e) {
		System.out.println("e.width=" + e.width);
		System.out.println("e.height=" + e.height);
		System.out.println("e.x=" + e.x);
		System.out.println("this.x=" + this.x);
		System.out.println("this.x+this.width=" + (this.x + this.width));
		System.out.println("e.x+e.width=" + (e.x + e.width));
		System.out.println("e.y=" + e.y);
		System.out.println("this.y=" + this.y);
		System.out.println("this.y+this.height=" + (this.y + this.height));
		System.out.println("e.y+e.height=" + (e.y + e.height));
	 	if ((((e.x >= this.x) && (e.x < (this.x + this.width))) || ((this.x >= e.x) && (this.x < (e.x + e.width)))) &&
			  	(((e.y >= this.y) && (e.y < (this.y + this.height))) || ((this.y >= e.y) && (this.y < (e.y + e.height))))) {
			System.out.println("kao");
	 		return true;
		}
			
	 	System.out.println("wuyu");
	 	return false;
	}*/
	
	// getters and setters
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}
