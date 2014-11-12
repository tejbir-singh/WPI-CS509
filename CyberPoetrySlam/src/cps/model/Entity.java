package cps.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	int x;
	int y;
	int width;
	int height;
	
	/**
	 * Determine if an Entity intersects this one.
	 * @param e Entity to check
	 * @return true if e intersects
	 */
	public boolean intersect(Entity e) {
		if ((((e.x >= this.x) && (e.x < (this.x + this.width))) || ((this.x >= e.x) && (this.x < (e.x + e.width)))) &&
			  	(((e.y >= this.y) && (e.y < (this.y + this.height))) || ((this.y >= e.y) && (this.y < (e.y + e.height))))) {
	 		return true;
		}
	 	return false;
	}
	
	public abstract void setPosition(int x, int y); 

	// getters and setters
	public int getX() {
		return x;
	}
	
	public void setX(int x){
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y){
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
}
