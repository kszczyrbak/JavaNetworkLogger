package logger.frame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logger.server.ConfigUtilities;
import logger.server.ServerUtilities;

/**
 * Okno wyswietlajace menu opcji
 */
@SuppressWarnings("serial")
public class OptionsFrame extends JFrame {
	private ServerFrame frame;
	private Properties conf;
	private JPanel mainPanel;

	OptionsFrame(ServerFrame f) {
		frame = f;
		initUI();
		setVisible(true);
	}

	/**
	 * Inicjalizacja okna opcji
	 */
	private void initUI() {

		// pobranie konfiguracji
		conf = ConfigUtilities.getConfiguration();

		// ustawianie okna
		setMinimumSize(new Dimension(200, 300));
		setPreferredSize(new Dimension(200, 300));
		setTitle("Options");
		setLocationRelativeTo(null);
		pack();

		// tworzenie glownego pojemnika
		mainPanel = new JPanel();
		System.out.println(ConfigUtilities.getNoOfProperties());
		mainPanel.setLayout(new GridLayout(ConfigUtilities.getNoOfProperties() + 3 - 1, 2));
		mainPanel.setBorder(new TitledBorder(new EtchedBorder()));

		// dodanie konfiguracji do panelu - w petli
		addOptionsToPanel();

		JButton saveButton = new JButton("Save");
		JButton saveAsBt = new JButton("Save as..");
		JButton loadButton = new JButton("Load config");

		add(mainPanel);
		mainPanel.add(loadButton);
		mainPanel.add(saveButton);
		mainPanel.add(saveAsBt);

		saveButton.addActionListener(new SaveListener());
		saveAsBt.addActionListener(new SaveAsListener());
		loadButton.addActionListener(new LoadListener());
		pack();
	}

	/**
	 * Dodaje opcje do panelu opcji na podstawie obecnie wpisanych domyslnych
	 * konfiguracji. Pomija opcje config_path.
	 * 
	 */
	private void addOptionsToPanel() {
		Set<Object> ks = conf.keySet();
		for (Object s : ks) {
			String key = (String) s;
			if (key.equals("config_path")) {
				continue;
			}
			JPanel tmp = new JPanel();
			setOptionPanel(key, tmp);
			mainPanel.add(tmp);
		}
	}

	/**
	 * Metoda ktora dodaje etykiete oraz pole tekstowe do podpanelu w glownym
	 * panelu.
	 * 
	 * @param property
	 *            nazwa klucza konfiguracji, ktorego podpanel wypelniamy
	 * @param panel
	 *            podpanel do wypelnienia
	 */
	private void setOptionPanel(String property, JPanel panel) {
		JTextField options = new JTextField();
		options.setBorder(null);
		options.setText(property);
		options.setEditable(false);
		JTextField input = new JTextField(10);
		input.setText(conf.getProperty(property));
		input.setLayout(new FlowLayout());
		panel.add(options);
		panel.add(input);

	};

	///// LISTENERY

	/**
	 * Implementacja ActionListenera pobierajaca ustawienia z opcji i zapisujaca
	 * je w istniejacym pliku konfiguracyjnym.
	 * 
	 * @author KrzysztofSzczyrbak
	 *
	 */
	class SaveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// componentCount - 3 : config_path nie wyswietlany + przyciski
			for (int i = 0; i < mainPanel.getComponentCount() - 3; i++) {
				JPanel tmp = (JPanel) mainPanel.getComponent(i);
				JTextField options = (JTextField) tmp.getComponent(0);
				JTextField input = (JTextField) tmp.getComponent(1);
				File configFile = ConfigUtilities.getConfigurationFile();
				ConfigUtilities.changeProperty(configFile, options.getText(), input.getText());
				dispose();
			}
		}
	}

	/**
	 * Implementacja ActionListenera uzywajaca JFileChooser do wyboru pliku do
	 * zapisania.
	 * 
	 * @author Krzysztof Szczyrbak
	 *
	 */
	class SaveAsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			// ladowanie do pliku biezacej konfiguracji, by potem zmienic
			// wartosci, ktore sie roznia
			File tmpFile = ServerUtilities.chooseFile(OptionsFrame.this);
			if (tmpFile.getPath().equals(""))
				return;
			Properties tmpProps = new Properties();

			if (!tmpFile.exists())
				try {
					if (!tmpFile.createNewFile()) {
						frame.consoleOut("Cannot create a new file!");
						return;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			BufferedReader reader = null;
			PrintWriter writer = null;
			// wczytuje do pliku biezaca konfiguracje
			try {
				reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(ConfigUtilities.getConfigurationFile()), "UTF-8"));
				tmpProps.load(reader);
				writer = new PrintWriter(tmpFile, "UTF-8");
				tmpProps.store(writer, null);
				ConfigUtilities.changeProperty(tmpFile, "config_path", tmpFile.getPath());
				ConfigUtilities.changeProperty(ConfigUtilities.getConfigurationFile(), "config_path",
						tmpFile.getPath());
			} catch (IOException e) {
				frame.showQuickErrorDialog(e);
			} finally {
				if (writer != null)
					writer.close();
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// zmienia w petli wartosci na podstawie tych wpisanych w panelu
			for (int i = 0; i < mainPanel.getComponentCount() - 3; i++) {
				JPanel tmp = (JPanel) mainPanel.getComponent(i);
				JTextField options = (JTextField) tmp.getComponent(0);
				JTextField input = (JTextField) tmp.getComponent(1);
				ConfigUtilities.changeProperty(tmpFile, options.getText(), input.getText());
			}
			dispose();
		}
	}

	/**
	 * Implementacja ActionListenera korzystjaca z JFileChooser do wyboru pliku
	 * z ktorego wczytuje sie konfiguracje.
	 * 
	 * @author Krzysztof Szczyrbak
	 *
	 */
	class LoadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// otwiera okienko wyboru i zwraca wybrany plik
			File configFile = ServerUtilities.chooseFile(OptionsFrame.this);
			if (configFile.getPath().equals(""))
				return;

			Properties tmp = ConfigUtilities.loadConfigFile(configFile);
			Set<Object> tmpKeySet = tmp.keySet();
			int i = 0;
			for (Object s : tmpKeySet) {
				String key = (String) s;

				if (key.equals("config_path")) {
					i++;
					continue;
				}
				if (i >= ConfigUtilities.getNoOfProperties() - 2)
					break;
				JPanel tmpPanel = (JPanel) mainPanel.getComponent(i);
				JTextField input = (JTextField) tmpPanel.getComponent(1);
				input.setText(tmp.getProperty(key));
				i++;
			}

		}
	}

}
