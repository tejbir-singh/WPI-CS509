package cps.model;

import java.io.Serializable;

public class ReturnIndex implements Serializable {
	private static final long serialVersionUID = 123L;
	public int idxPoem;
	public int idxRow;
	public int idxWord;
	public Word w;
	public Poem p = null;
	
	public ReturnIndex(int idxPoem, int idxRow, int idxWord, Word w){
		this.idxPoem = idxPoem;
		this.idxRow = idxRow;
		this.idxWord = idxWord;
		this.w = w;
	}
}
