package cps.controller;

import java.awt.Point;

import javax.swing.table.JTableHeader;

import cps.model.WordModel;
import cps.view.WordTable;

/**
 * This controller handles the sorting of the WordTable.
 * @author Devin
 */
public class SortController {
	/** View being controlled. */
	WordTable table;
	
	/** Controller needs to know of the JTable view. */
	public SortController(WordTable table) {
		this.table = table; 
	}

	/**
	 * Implements the handler for clicking on a table column in the display.
	 * 
	 * @param point 
	 * @param h 
	 * @param e
	 */
	public void process (JTableHeader h, Point point) {
		int columnIndex = h.columnAtPoint(point);
		
		// if one is selected, get the field that identifies that column
		if (columnIndex != -1) {
			String fieldName = h.getColumnModel().getColumn(columnIndex).getHeaderValue().toString();
			WordModel model = (WordModel) h.getTable().getModel();
			model.sort(fieldName);
		}
	}
}
