package cps.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import cps.model.UnprotectedArea;
import cps.model.WordModel;
import cps.controller.RefreshWordTableController;
import cps.controller.SelectFromTableController;
import cps.controller.SortController;

/**
 * This class implements the table of words which displays the contents of the unprotected area.
 * @author Devin
 */
public class WordTable extends JPanel {
	/* keep eclipse happy */
	private static final long serialVersionUID = 1L;
	
	/** The model that "backs" the JTable. Different from Board. */
	WordModel wordModel;
	UnprotectedArea ua;
	ApplicationPanel appPanel;
	
	/** Actual GUI entity. */
	JTable jtable = null;
	
	/**
	 * This constructor creates the display which manages the list of active players.
	 */
    public WordTable(UnprotectedArea ua, ApplicationPanel appPanel) {

    	// create the model for the data which backs this table
    	this.wordModel = new WordModel();
    	this.appPanel = appPanel;
    	
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
				new SortController(WordTable.this).process(h, e.getPoint());
			}
		});
		
		jtable.addMouseListener(new MouseAdapter() {
			public void mouseClicked (MouseEvent e)	{
				new SelectFromTableController(WordTable.this).process(WordTable.this.appPanel);
			}
		});
		
		jsp.setViewportView(jtable);

		//this.setPreferredSize(mySize);
		this.add(jsp);
	}

    /**
     * Update the table display.
     */
    public void refreshTable() {
		jtable.revalidate();
		jtable.repaint();
		this.revalidate();
		this.repaint();
	}
    
    public JTable getJTable() {
    	return this.jtable;
    }
}
