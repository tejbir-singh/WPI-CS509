package cps.model;

import java.util.Comparator;
import javax.swing.table.AbstractTableModel;

/**
 * Instead of making Board responsible for handling the JTable stuff, delegate
 * those responsibilities to this class.
 * 
 * @author Devin
 */
public class WordModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	/** UnprotectedArea maintains the state. */
	UnprotectedArea ua;
	
	/** Key values */
	public static final String wordLabel = "Word";
	public static final String typeLabel = "Type";
	
	/** The Table model needs to know the board which contains the shapes. */
	public WordModel () {
		this.ua = UnprotectedArea.getInstance();
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return ua.getWords().size();
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Word w = ua.getWords().get(rowIndex);
		
		if (columnIndex == 0) { 
			return w.getValue();
		} else if (columnIndex == 1) { 
			return w.getType().toString();
		}
		
		// no idea who you are...
		return "";
	}

	/**
	 * Sort the ArrayList by the specified field.
	 * @param fieldName name of the field to sort by
	 */
	public void sort(final String fieldName) {
		ua.sort(new Comparator<Word>() {

			@Override
			public int compare(Word w1, Word w2) {
				if (fieldName.equals(typeLabel)) {
					return w1.getType().compareTo(w2.getType());
				}
				
				// default to word
				return (w1.getValue().compareTo(w2.getValue()));
			}
		});	
	}	
}
