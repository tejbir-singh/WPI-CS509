package cps.model;

/**
 * @author Devin
 */
public class Manipulation {
	int x;
	int y;
	Entity entity;
	MoveType moveType;
	
	/**
	 * Constructor.
	 */
	public Manipulation(int x, int y, Entity entity, MoveType moveType) {
		this.x = x;
		this.y = y;
		this.entity = entity;
		this.moveType = moveType;
	}
	// If a Word or Poem is moved, simply move it back to x and y
	// if a Word is connected to another Entity, we must find the Poem, remove the Word, adjust the Poem (may need to become a Word itself)
	// if a Poem is connected to another Poem, we'll see
	// if a Word is disconnected from a Poem, we'll see

	// getters 
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Entity getEntity() {
		return entity;
	}

	public MoveType getMoveType() {
		return moveType;
	}
}
