package logger.frame;

import java.util.Comparator;

enum Priority {
	DEBUG, INFO, WARNING, ERROR, CRITICAL;
}

/**
 * Komparator waznosci logow - do prawidlowego sortowania w tabeli
 * 
 * @author Chris
 *
 */
public class PriorityComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		Priority p = Priority.valueOf(o1.toString());
		Priority p2 = Priority.valueOf(o2.toString());
		return p.compareTo(p2);
	}

}
