package Amalthea.model;

import java.util.ArrayList;

public class Poem {
	int x;
	int y;
	ArrayList<Row> rows;
	int width=0;
	int height=0;
	
	public Poem(ArrayList<Row> r){
		this.rows = r;
		//this.x = r.get(0).x;
		//this.y = r.get(0).y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		return true;
		
	}  
	
	/*
	public boolean connectRowTop(Row r){
		//setPosition(r.x, r.y);
		this.rows.add(0, r);
		return true;
	}
	
	public boolean connectRowBottom(Row r){
		this.rows.add(r);
		return true;
	}*/
	
	public boolean connectPoemTop(Poem p){
		setPosition(p.x, p.y);
		for(int i = p.rows.size() - 1; i >= 0; i--)
			this.rows.add(0, p.rows.get(i));
	}
	
	public boolean connectPoemBottom(Poem p){
		for(int i = 0; i < p.rows.size(); i++)
			this.rows.add(this.rows.size(), p.rows.get(i));
	}
	
	public boolean dsiconnectRow(ArrayList<Poem> p, int dexp, int dexr, int newx, int newy){
		// dexp is the index of the selected poem, dexr is the index of the selected row, (newx, newy) is the position of being dragged to
		if(p.get(dexp).rows.size() == 1)
			return false;
		else{
			if(dexr == 0){ // disconnect first row
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist.add(p.get(dexp).rows.get(dexr));
				Poem newpoem = new Poem(newrowlist);
				newpoem.setPosition(newx, newy);
				p.add(newpoem);
				
				// modify original poem
				p.get(dexp).setPosition(p.get(dexp).rows.get(1).x, p.get(dexp).rows.get(1).y);
				p.get(dexp).rows.remove(dexr);
			}
			else if(dexr == p.get(dexp).rows.size() - 1){ //disconnect last row
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist1 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist1.add(p.get(dexp).rows.get(dexr));
				Poem newpoem1 = new Poem(newrowlist1);
				newpoem1.setPosition(newx, newy);
				p.add(newpoem1);
				
				// modify original poem
				p.get(dexp).rows.remove(p.get(dexp).rows.get(dexr));
			}
			else{ //disconnect middle row
				// the lower rows construct a new poem
				ArrayList<Row> newrowlist2 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				for(int i = dexr + 1; i < p.get(dexp).rows.size(); i++)
					newrowlist2.add(p.get(dexp).rows.get(i));
				Poem newpoem2 = new Poem(newrowlist2);
				newpoem2.setPosition(p.get(dexp).rows.get(dexr + 1).x, p.get(dexp).rows.get(dexr + 1).y);
				p.add(newpoem2);
				
				// the disconnected row constructs a new poem
				ArrayList<Row> newrowlist3 = new ArrayList<Row>(); //it's up to Rej's constructor of class Row
				newrowlist3.add(p.get(dexp).rows.get(dexr));
				Poem newpoem3 = new Poem(newrowlist3);
				newpoem3.setPosition(newx, newy);
				p.add(newpoem3);
				
				// modify the original poem
				for (int i = dexr; i < p.get(dexp).rows.size(); i++ )
					p.get(dexp).rows.remove(p.get(dexp).rows.get(i));	
			}
		}	
				
		return true;
		
	}
	
	public boolean disconnectEdgeWord(Poem poem, int dexr, Word word){
		if(poem.rows.get(dexr).size() == 1)
			return false;
		else
			poem.rows.get(dexr).disconnectWord(word); //it's up to Rej's implement of method disconnectWord()
		return true;
		
	}
	

}
