package cps.controller;

import java.io.Serializable;

import cps.view.WordTable;

/**
 * @author Devin
 */
public class RefreshWordTableController implements Serializable {
	private static final long serialVersionUID = -8933206067153212332L;
	
	/** Table to be refreshed. */
	WordTable table;
	
	public RefreshWordTableController(WordTable table) {
		this.table = table;
	}
	
	public void update() {
		table.refreshTable();
	}
}
