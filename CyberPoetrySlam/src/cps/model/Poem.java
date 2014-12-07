package cps.model;

import java.util.ArrayList;

public class Poem extends Entity{
	private static final long serialVersionUID = 1L;
	public ArrayList<Row> rows;
	
	/**
	 * Constructor.
	 * @param r Rows which make up the Poem
	 */
	public Poem(ArrayList<Row> r){
		this.rows = r;
		this.x = r.get(0).x;
		this.y = r.get(0).y;
		this.width = 0;
		this.height = 0;
	}  

	
	/**
	 * Disconnect a Word which is on the outside of a Row (first or last)
	 * @param dexr Index of the Row in the Poem
	 * @param dexw Index of the Word in the Row
	 * @return
	 */
	public boolean disconnectEdgeWord(int dexr, int dexw){
		if(this.rows.size() == 1){
			if(this.rows.get(dexr).words.size() == 1) //rows[dexr] has at least 2 words, otherwise, it will disconnect the poem
				return false;
			else{
				this.rows.get(dexr).disconnectWord(dexw);
				this.setX(this.rows.get(0).x);
				this.setY(this.rows.get(0).y);
				return true;
			}
		}else{
			if(this.rows.get(dexr).disconnectWord(dexw) == false){ //Row[dexr] has no words any more
				this.rows.remove(dexr);
			}
			this.setX(this.rows.get(0).x);
			this.setY(this.rows.get(0).y);
			return true;
		}
		
	}
	
	public boolean removeRow(int index){
		if(this.rows.size() < 2){
			return false;
		} else{
			this.rows.remove(index);
			if(index == 0){
				this.setX(this.rows.get(0).x);
				this.setY(this.rows.get(0).y);
			}
			return true;
		}
	}
	
	public boolean connectRowTop(Row r){
		//setPosition(r.x, r.y);
		this.rows.add(0, r);
		return true;
	}
	
	public boolean connectRowBottom(Row r){
		this.rows.add(r);
		return true;
	}
		
	// getters and setters
	public ArrayList<Row> getRows() {
		return this.rows;
	}
	
	public int getHeight() {
		return this.rows.size() * this.rows.get(0).words.get(0).height;
	}
	
	public void setPosition(int x, int y) {
		for (Row r : this.rows){
			r.setPosition(r.x - (this.x - x), r.y - (this.y - y));
		}
		this.x = x;
		this.y = y;	
	}
}
