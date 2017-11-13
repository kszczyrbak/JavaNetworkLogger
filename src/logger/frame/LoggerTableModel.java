package logger.frame;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 * Model tabeli wyswietlajacej logi w programie.
 * 
 * @author Chris
 *
 */
public class LoggerTableModel extends AbstractTableModel {

	private String[] columnNames = { "Date", "Time", "Priority", "Name", "Thread ID", "Hostname", "Message" };

	private final LinkedList<LogObject> list;

	LoggerTableModel() {
		list = new LinkedList<LogObject>();
	}

	/**
	 * Zwraca liczbe kolumn w tabeli.
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Zwraca liczbe rzedow w tabeli.
	 */
	@Override
	public int getRowCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	/**
	 * Zwraca nazwe wybranej kolumny.
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Zwraca wartosc na danym miejscu w danej kolumnie.
	 */
	@Override
	public Object getValueAt(int row, int col) {
		return list.get(row).getItemAt(col);
	}

	/**
	 * Metoda dodajaca nowy log do tabeli.
	 * 
	 * @param data
	 *            nowy rzad tabeli
	 */
	public synchronized void addData(Object[] data) {
		LogObject tmp = new LogObject(data);
		list.add(tmp);
		fireTableRowsInserted(list.size() - 1, list.size() - 1);
	}

	/**
	 * Metoda usuwajaca log z tabeli.
	 * 
	 * @param row
	 *            rzad tabeli
	 */
	public synchronized void removeData(int row) {
		list.remove(row);
		fireTableRowsDeleted(list.size() - 1, list.size() - 1);
	}

	/**
	 * Metoda czyszczaca tabele.
	 */
	public synchronized void clear() {
		list.clear();
		fireTableRowsDeleted(0, list.size() - 1);
	}

}
