package logger.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Okno wyswietlajace zawartosc pliku help.txt, zawierajacego dokumentacje
 * pomocna uzytkownikowi w obsludze programu.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
@SuppressWarnings("serial")
public class HelpFrame extends JFrame {
	private ServerFrame frame;

	HelpFrame(ServerFrame f) {
		frame = f;

		initUI();

		setVisible(true);
	}

	/**
	 * Metoda inicjalizujaca okno.
	 */
	private void initUI() {
		setMinimumSize(new Dimension(900, 400));
		setPreferredSize(new Dimension(900, 400));
		setTitle("Help");
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new TitledBorder(new EtchedBorder()));

		JTextArea helpArea = new JTextArea(50, 50);
		helpArea.setLineWrap(true);
		helpArea.setMargin(new Insets(5, 5, 5, 5));

		helpArea.setEditable(false);
		JScrollPane helpScrollPane = new JScrollPane(helpArea);
		mainPanel.add(helpScrollPane, BorderLayout.CENTER);
		File helpFile = new File("help.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(helpFile), "UTF-8"));
			helpArea.read(reader, null);
			reader.close();
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				frame.showQuickErrorDialog(e);
			}
		}
		add(mainPanel);
		pack();

	}

}
