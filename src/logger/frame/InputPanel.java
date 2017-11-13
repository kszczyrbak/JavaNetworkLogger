package logger.frame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import logger.server.ConfigUtilities;
import logger.server.ServerUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Kontener dla konsoli wypisujacej odebrane logi.
 * 
 * @author Krzysztof Szczyrbak
 */
@SuppressWarnings("serial")
public class InputPanel extends JPanel {

	private ServerFrame frame;
	private JTable table;
	private JTextField regexFilter;
	private LoggerTableModel model;
	private JTextArea textArea;
	private TableRowSorter<LoggerTableModel> sorter;
	private RowFilter<LoggerTableModel, String> filter;

	public InputPanel(ServerFrame frame) {

		// -----------------------------------------------------------------------
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		model = new LoggerTableModel();
		table = new JTable(model);

		setTableColumnWidth();

		sorter = new TableRowSorter<LoggerTableModel>(model);
		table.setRowSorter(sorter);

		PriorityComparator priorityComparator = new PriorityComparator();

		sorter.setComparator(2, priorityComparator);
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		JPanel form = new JPanel(new BorderLayout());
		JLabel l1 = new JLabel("Filter Text:  ", SwingConstants.TRAILING);

		regexFilter = new JTextField(20);

		l1.setLabelFor(regexFilter);
		l1.setPreferredSize(new Dimension(80, 20));
		l1.setMaximumSize(new Dimension(80, 20));
		form.add(l1, BorderLayout.WEST);

		regexFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});

		form.setPreferredSize(new Dimension(650, 40));
		form.setMaximumSize(new Dimension(700, 40));
		form.add(regexFilter, BorderLayout.CENTER);
		add(form);

	}

	private void setTableColumnWidth() {
		table.getColumnModel().getColumn(0).setMaxWidth(90);
		table.getColumnModel().getColumn(1).setMaxWidth(60);
		table.getColumnModel().getColumn(2).setMaxWidth(90);
		table.getColumnModel().getColumn(3).setMaxWidth(150);
		table.getColumnModel().getColumn(4).setMaxWidth(90);
		table.getColumnModel().getColumn(5).setMaxWidth(150);
	}

	public void newFilter() {
		String[] andSplit = regexFilter.getText().split("&&");

		if (andSplit.length == 1)
			sorter.setRowFilter(new LoggerTableFilter(andSplit[0]));
		else {
			List<RowFilter<LoggerTableModel, Object>> filters = new ArrayList<RowFilter<LoggerTableModel, Object>>();
			for (String x : andSplit) {
				filters.add(new LoggerTableFilter(x.trim()));
			}
			sorter.setRowFilter(RowFilter.andFilter(filters));

		}
	}

	/**
	 * Metoda czyszczaca konsole.
	 */
	public void clear() {
		model.clear();
		ServerUtilities.newBackupFile();
	}

	/**
	 * Metoda dodajaca linie tekstu na konsole.
	 * 
	 * @param text
	 *            tekst do wyswietlenia
	 */
	public void appendText(String text) {
		textArea.append(text + "\n");
	}

	/**
	 * Metoda zwracajaca referencje do pola tekstowego.
	 * 
	 * @return JTextArea referencja do pola tekstowego
	 */
	public LoggerTableModel getLogTableModel() {
		return model;
	}

	public JTable getLogTable() {
		return table;
	}

}