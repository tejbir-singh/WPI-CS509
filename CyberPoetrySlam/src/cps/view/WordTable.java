package cps.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import cps.model.UnprotectedArea;
import cps.model.WordModel;
import cps.controller.RefreshWordTableController;

/**
 * Sample tutorial for JTables.
 * 
 * To properly refresh JTable when model changes, be sure that controller
 * calls {@link #refreshTable()}.
 */
public class WordTable extends JPanel {
	/* keep eclipse happy */
	private static final long serialVersionUID = 1L;
	
	/** The model that "backs" the JTable. Different from Board. */
	WordModel wordModel;
	
	/** Actual GUI entity. */
	JTable jtable = null;
	
	/**
	 * This constructor creates the display which manages the list of active players.
	 */
    public WordTable(UnprotectedArea ua) {

    	// create the model for the data which backs this table
    	this.wordModel = new WordModel();
    	
    	// add to listener chain
    	ua.setRefreshWordTableController(new RefreshWordTableController(this));
		
		// the proposed dimension of the UI
		Dimension mySize = new Dimension(160, 700);
		
		// Scrollable panel will enclose the JTable and support scrolling vertically
		JScrollPane jsp = new JScrollPane();
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setPreferredSize(mySize);
		
		// Just add the JTable to the set. First create the list of Players,
		// then the SwingModel that supports the JTable which you now create.
		jtable = new JTable(wordModel);
		jtable.setPreferredSize(mySize);
		
		// let's tell the JTable about its columns.
		TableColumnModel columnModel = new DefaultTableColumnModel();

		// must build one by one...
		String[] headers = new String[] { WordModel.wordLabel, WordModel.typeLabel};
		int index = 0;
		for (String h : headers) {
			TableColumn col = new TableColumn(index++);
			col.setHeaderValue(h);
			columnModel.addColumn(col);
		}		
		jtable.setColumnModel(columnModel);
		
		// let's install a sorter and also make sure no one can rearrange columns
		JTableHeader header = jtable.getTableHeader();
		
		// purpose of this sorter is to sort by columns.
		header.addMouseListener(new MouseAdapter()	{
			public void mouseClicked (MouseEvent e)	{
				JTableHeader h = (JTableHeader) e.getSource() ;
				//new SortController(WordTable.this).process(h, e.getPoint());
			}
		});
		
		// Here's the trick. Make the JScrollPane take its view from the JTable.
		jsp.setViewportView(jtable);

		// add the scrolling Pane which encapsulates the JTable
		// physical size limited
		this.setPreferredSize(mySize);
		this.add(jsp);
	}

    /**
     * Causes the display of the shapes to be updated to the latest data.
     */
    public void refreshTable() {
		// THIS is the key method to ensure JTable view is synchronized
		jtable.revalidate();
		jtable.repaint();
		this.revalidate();
		this.repaint();
	}
}
