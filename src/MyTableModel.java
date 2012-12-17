import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

/**
 * This class represents the Access Control List that will handle 
 * the community strings represented in the client gui
 * 
 */
public class MyTableModel extends AbstractTableModel {
	private boolean DEBUG = false;


	private String[] columnNames = { "Community String", "Community String Type" };
	private static Object[][] data = null;
	
	/**
	 * Construct empty data
	 */
	public MyTableModel()
	{
	}
	
	/**
	 * Constructor
	 * @param d the object data to be inputed to the table
	 */
	public MyTableModel(Object[][] d) 
	{
		data = d;
	}
	
	public Object[][] getData()
	{
		return data;
	}
	
	public void populateTable(Hashtable<String, String> hTable)
	{
		Enumeration e = hTable.keys();
		
		while(e.hasMoreElements())
		{
			Object str = e.nextElement();
			Object[] newData = { str, hTable.get(str) };
			
			addData( newData );
		}
	}
	
	/**
	 * Adds new data to ACL
	 * @param d
	 */
	public void addData(Object[] d)
	{
		if (data == null)
		{
			Object[][] newData = {d};
			data = newData;
		}
		else {
			Object[][] tempData = data;

			data = new Object[data.length+1][columnNames.length];

			for (int i = 0; i < data.length; i++) 
			{
				for (int j = 0; j < columnNames.length; j++) 
				{
					// last row
					if (i == data.length-1)
						data[i][j] = d[j];
					else
						data[i][j] = tempData[i][j];
				}
			}
		}
		
	}
	

	/**
	 * Get the number of columns
	 */
	public int getColumnCount() 
	{
		return columnNames.length;
	}

	/**
	 * Get the number of rows
	 */
	public int getRowCount() {
		return data.length;
	}
	
	/**
	 * Get the name of the column
	 * @col the current col
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Get the values at the specified row and column
	 * @row The current row
	 * @col the current col
	 */
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Set the value at the current row and column
	 * @value the data
	 * @row the current row
	 * @col the current col
	 */
	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col + " to "
					+ value + " (an instance of " + value.getClass() + ")");
		}

		data[row][col] = value;
		//fireTableCellUpdated(row, col);

		if (DEBUG) {
			System.out.println("New value of data:");
			printDebugData();
		}
	}
	
	public void clearTable(){
		data = null;
	}

	
	private void printDebugData() {
		int numRows = getRowCount();
		int numCols = getColumnCount();

		for (int i = 0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j = 0; j < numCols; j++) {
				System.out.print("  " + data[i][j]);
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}
}