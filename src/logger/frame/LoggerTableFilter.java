package logger.frame;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.RowFilter;

import logger.server.ServerUtilities;

/**
 * Filtr modelu tabeli systemu logowania. Filtruje wg. wyrazen regularnych,
 * wartosci w tabeli lub zabronionych tagow.
 * 
 * @author Chris
 *
 */
public class LoggerTableFilter extends RowFilter<LoggerTableModel, Object> {
	private ArrayList<String> notAllowedList;

	private String text;

	LoggerTableFilter(String text) {
		notAllowedList = ServerUtilities.getNotAllowedList();
		this.text = text;
	}

	/**
	 * Metoda decydujaca o tym, czy rzad w tabeli ma zostac wyswietlony.
	 */
	@Override
	public boolean include(RowFilter.Entry<? extends LoggerTableModel, ? extends Object> entry) {
		String key = entry.getValue(3).toString();
		for (String x : notAllowedList) {
			if (key.equals(x))
				return false;
		}

		Pattern p = null;
		try {
			p = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			return true;
		}

		for (int i = 0; i < entry.getValueCount(); i++) {
			String x = entry.getValue(i).toString();
			Matcher m = p.matcher(x);
			if (m.find()) {
				return true;
			}
		}

		return false;
	}

}
