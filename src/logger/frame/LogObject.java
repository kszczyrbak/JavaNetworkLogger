package logger.frame;

/**
 * Obiekt stworzony do przechowywania zawartosci wierszy w tabeli logow.
 * 
 * @author Chris
 *
 */
public class LogObject {
	Object[] data;

	LogObject(Object[] data) {
		this.data = data;
	}

	/**
	 * Zwraca zawartosc zadanego indeksu
	 * 
	 * @param col
	 *            Indeks do pobrania
	 * @return Obiekt pod indeksem
	 */
	public Object getItemAt(int col) {
		return data[col];
	}

	public static int columnSize() {
		return 7;
	}

}
