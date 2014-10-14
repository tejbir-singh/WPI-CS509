package entity;

public abstract class Entity {
	int x;
	int y;
	
	public abstract boolean intersect(int x, int y);
}
