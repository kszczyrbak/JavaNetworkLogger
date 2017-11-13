package logger.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Panel zawierajacy wlasciwa konsole GUI, wypisujaca komunikaty dzialania
 * serwera.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
@SuppressWarnings("serial")
public class ConsolePanel extends JPanel {
	private JTextArea textArea;
	private ServerFrame frame;

	public ConsolePanel(ServerFrame serverFrame) {
		frame = serverFrame;

		initUI();
		setVisible(true);

	}

	/**
	 * Metoda inicjujaca GUI panelu.
	 */
	private void initUI() {
		textArea = new JTextArea(100, 200);
		setMinimumSize(new Dimension(100, 100));
		setPreferredSize(new Dimension(100, 100));
		textArea.setEditable(false);

		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);

		setBorder(new TitledBorder(new EtchedBorder()));
		frame.pack();

	}

	/**
	 * Metoda zwraca referencje do pola tekstowego, w ktorym maja znalezc sie
	 * komunikaty.
	 * 
	 * @return konsola
	 */
	public JTextArea getConsole() {
		return textArea;
	}

}