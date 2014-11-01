package shapes.model;

import java.io.Serializable;

public class ReturnIndex implements Serializable {
	private static final long serialVersionUID = 123L;
	public int dexpoem;
	public int dexrow;
	public int dexword;
	public Word w;
	
	public ReturnIndex(int dexpoem, int dexrow, int dexword, Word w){
		this.dexpoem = dexpoem;
		this.dexrow = dexrow;
		this.dexword = dexword;
		this.w = w;
	}
}
