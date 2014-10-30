package shapes.model;

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
		/*System.out.println("e.width=" + e.width);
		System.out.println("e.height=" + e.height);
		System.out.println("e.x=" + e.x);
		System.out.println("this.x=" + this.x);
		System.out.println("this.x+this.width=" + (this.x + this.width));
		System.out.println("e.x+e.width=" + (e.x + e.width));
		System.out.println("e.y=" + e.y);
		System.out.println("this.y=" + this.y);
		System.out.println("this.y+this.height=" + (this.y + this.height));
		System.out.println("e.y+e.height=" + (e.y + e.height));*/
	 	if ((((e.x >= this.x) && (e.x < (this.x + this.width))) || ((this.x >= e.x) && (this.x < (e.x + e.width)))) &&
			  	(((e.y >= this.y) && (e.y < (this.y + this.height))) || ((this.y >= e.y) && (this.y < (e.y + e.height))))) {
	 		return true;
		}	
	 	return false;
	}

	// getters 
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
