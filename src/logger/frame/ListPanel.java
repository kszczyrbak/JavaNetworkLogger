package logger.frame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import javax.swing.JPanel;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logger.server.TCPClient;
import logger.server.ClientWrapper;
import logger.server.ConfigUtilities;
import logger.server.ServerUtilities;

/**
 * Panel wyswietlajacy liste klientow podlaczonych do serwera.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
@SuppressWarnings("serial")
public class ListPanel extends JPanel {
	private ServerFrame frame;
	private Properties config;
	private BoxLayout layout;
	private int height;
	private ArrayList<JCheckBox> boxList;

	ListPanel(ServerFrame f) {
		frame = f;
		initUI();
		setVisible(true);
		boxList = new ArrayList<JCheckBox>();
	}

	/**
	 * Metoda inicjalizujaca interfejs uzytkownika.
	 */
	private void initUI() {
		config = ConfigUtilities.getConfiguration();
		height = Integer.parseInt(config.getProperty("height"));
		setMinimumSize(new Dimension(130, height));
		setPreferredSize(new Dimension(130, height));
		layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		setBorder(new TitledBorder(new EtchedBorder()));
	}

	/**
	 * Dodaje nowego klienta do listy w panelu.
	 * 
	 * @param client
	 *            klient do dodania
	 */
	void addListComponent(final ClientWrapper client) {
		String identifier = client.getName();
		final JCheckBox tmp = new JCheckBox(identifier);
		boxList.add(tmp);
		tmp.setName(identifier);
		tmp.setSelected(true);

		// listener dla checkboxu dodanego do klienta
		tmp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ServerUtilities.setVisibility(client, tmp.isSelected());
				ServerUtilities.changeWindowOutput();
			}

		});

		add(tmp);
		if (boxList.size() * 24 > height)
			setPreferredSize(new Dimension(130, height += 24));
		revalidate();
		repaint();
	}

	public ArrayList<JCheckBox> getBoxList() {
		return boxList;
	}

	/**
	 * Metoda czyszczaca liste.
	 */
	public void clearPanel() {
		removeAll();
		revalidate();
		repaint();
	}

}
