package cps.model;

import java.io.Serializable;

public class ReturnIndex implements Serializable {
	private static final long serialVersionUID = 123L;
	public int idxPoem;
	public int idxRow;
	public int idxWord;
	public Word w;
	public Poem p = null;
	
	public ReturnIndex(int dexpoem, int dexrow, int dexword, Word w){
		this.idxPoem = dexpoem;
		this.idxRow = dexrow;
		this.idxWord = dexword;
		this.w = w;
	}
}
