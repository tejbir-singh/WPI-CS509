package cps.controller;

import cps.model.GameManager;
import cps.model.UnprotectedArea;
import cps.model.Word;
import cps.view.ApplicationPanel;
import cps.view.WordTable;

/**
 * @author Devin
 *
 */
public class SelectFromTableController {
	UnprotectedArea ua;
	WordTable table;
	
	public SelectFromTableController(WordTable wordTable) {
		this.table = wordTable;
		this.ua = UnprotectedArea.getInstance();
	}

	public void process(ApplicationPanel appPanel) {
		int rowIndex = table.getJTable().getSelectedRow();
		int colIndex = table.getJTable().getSelectedColumn();
		Word w = ua.getWords().get(rowIndex);
		// remove and add the word to set it to the last element in the table for drawing purposes
		GameManager.getInstance().setSelected(w);
		ua.remove(w);
		ua.add(w);
		table.refreshTable();
		// update the selected row
		table.getJTable().changeSelection(rowIndex, colIndex, true, false);
		table.getJTable().changeSelection(ua.getWords().size()-1, colIndex, false, false);
		appPanel.redraw();
		appPanel.repaint();
	}

}
