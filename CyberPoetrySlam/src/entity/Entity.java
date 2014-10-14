package entity;

public abstract class Entity {
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
}
