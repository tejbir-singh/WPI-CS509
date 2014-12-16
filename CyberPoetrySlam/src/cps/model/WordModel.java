package cps.model;

import java.util.Comparator;
import javax.swing.table.AbstractTableModel;

/**
 * Instead of making Board responsible for handling the JTable stuff, delegate
 * those responsibilities to this class.
 * 
 * @author heineman
 */
public class WordModel extends AbstractTableModel {

	/** Keep Eclipse Happy. */
	private static final long serialVersionUID = 1L;
	
	/** UnprotectedArea maintains the state. */
	UnprotectedArea ua;
	
	/** Key values. */
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
	 * Sort the ArrayList of cars by given field, whose value is
	 * either {@link UserModelGUI#uidStr}, {@link UserModelGUI#rNameStr},
	 * {@link UserModelGUI#tableNumStr}, or {@link UserModelGUI#timeStr}.
	 * 
	 * @param columnIndex
	 */
	public void sort(final String fieldName) {
		ua.sort(new Comparator<Word>() {

			@Override
			public int compare(Word w1, Word w2) {
				if (fieldName.equals(typeLabel)) {
					return w1.getX() - w2.getX();
				}
				
				// default to word
				return (w1.getValue().compareTo(w2.getValue()));
			}
		});	
	}	
}
