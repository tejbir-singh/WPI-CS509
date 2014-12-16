package cps.controller;

import cps.view.WordTable;

public class RefreshWordTableController {

	/** Widget to be refreshed. */
	WordTable table;
	
	public RefreshWordTableController(WordTable table) {
		this.table = table;
	}
	
	public void update() {
		table.refreshTable();
	}
}
