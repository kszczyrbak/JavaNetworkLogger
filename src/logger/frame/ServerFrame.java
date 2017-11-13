package logger.frame;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logger.server.ClientWrapper;
import logger.server.ConfigUtilities;
import logger.server.LogServer;
import logger.server.ServerUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Glowne okno programu. Obsluguje serwer i panele.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
@SuppressWarnings("serial")
public class ServerFrame extends JFrame implements SimpleServerFrame {
	private InputPanel inputPanel;
	private InfoPanel infoPanel;
	private MenuBar menu;
	private ListPanel listPanel;
	private Thread serverThread;
	private Properties conf;
	private LogServer server;
	private ConsolePanel consolePanel;
	private JTextArea console;

	private int width = 0;
	private int height = 0;

	public ServerFrame() {
		initConfigSettings();
		initUI();
		initServer();
		initUtils();
		setVisible(true);

	}

	/**
	 * Inicjalizuje konfiguracje aplikacji.
	 */
	private void initConfigSettings() {
		ConfigUtilities.initConfigurationFile(this);
		conf = ConfigUtilities.getConfiguration();

	}

	/**
	 * Inicjalizuje klase z narzedziami pomocnymi do obslugi serwera.
	 */
	private void initUtils() {
		ServerUtilities.setServerFrame(this);
		ServerUtilities.setOutput(inputPanel.getLogTableModel(), inputPanel.getLogTable());
	}

	/**
	 * Startuje interfejs uzytkownika.
	 */
	private void initUI() {

		// ustawia rozmiary okna
		try {
			width = Integer.parseInt(conf.getProperty("width"));

		} catch (NumberFormatException e) {
			consoleOut("Nieprawidlowa wartosc WIDTH. Ustawiana domyslna: 800");
			width = 800;
			ConfigUtilities.changeProperty(ConfigUtilities.getConfigurationFile(), "wifth", "800");
			conf.setProperty("width", "800");
		}
		try {
			height = Integer.parseInt(conf.getProperty("height"));
		} catch (NumberFormatException e) {
			consoleOut("Nieprawidlowa wartosc HEIGHT. Ustawiana domyslna: 400");
			height = 400;
			ConfigUtilities.changeProperty(ConfigUtilities.getConfigurationFile(), "height", "400");
			conf.setProperty("height", "400");
		}

		setMinimumSize(new Dimension(500, 400));
		setPreferredSize(new Dimension(width, height));
		setSize(width, height);

		// ustawienia panelu z konsola wyswietlajaca odebrane logi
		inputPanel = new InputPanel(this);
		inputPanel.setBorder(new TitledBorder(new EtchedBorder()));
		inputPanel.setPreferredSize(new Dimension(width, height));

		// ustawia layout okna
		setLayout(new BorderLayout());

		// inicjalizacja panelu z informacja o podlaczonych socketach
		infoPanel = new InfoPanel(this);

		// inicjalizacja panelu z lista klientow
		listPanel = new ListPanel(this);
		JScrollPane listScrollPane = new JScrollPane(listPanel);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// inicjalizacja menu
		menu = new MenuBar(this);

		consolePanel = new ConsolePanel(this);
		console = consolePanel.getConsole();

		add(infoPanel, BorderLayout.NORTH);
		add(inputPanel, BorderLayout.CENTER);
		add(listScrollPane, BorderLayout.EAST);
		add(consolePanel, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setTitle("Remote logging system");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setJMenuBar(menu);

	}

	/**
	 * Inicjalizuje i startuje watek serwera.
	 */
	private void initServer() {
		initConfigSettings();
		int port = 0;
		try {
			port = Integer.parseInt(conf.getProperty("port"));
		} catch (NumberFormatException e) {
			consoleOut("Niepoprawna wartosc PORT. Ustawiana domyslna: 8000");
			System.out.println(port);
			conf.setProperty("port", "8000");
			ConfigUtilities.changeProperty(ConfigUtilities.getConfigurationFile(), "port", "8000");
			port = 8000;
		}

		try {
			server = new LogServer(port);
		} catch (IOException e) {
			showQuickErrorDialog(e);
		}
		server.setFrame(this);

		// inicjalizuje plik zachowujacy wszystkie odebrane logi
		ServerUtilities.enableFileSaving();
		serverThread = new Thread(new ServerThread());
		serverThread.start();
	}

	/**
	 * Dodaje nowego klienta do listy klientow w panelu.
	 * 
	 * @param client
	 *            klient do dodania
	 */
	public void addListComponent(ClientWrapper client) {
		listPanel.addListComponent(client);
	}

	/**
	 * Metoda odswiezajaca liczbe podpietych socketow w glownym oknie.
	 */
	public synchronized void updateNumberOfSockets() {
		infoPanel.getTextField().setText("");
		infoPanel.getTextField().setText("Connected sockets: " + ServerUtilities.getClientList().size());
	}

	/**
	 * Czysci okno wyswietlajace odebrane logi.
	 */
	public void clearWindow() {
		inputPanel.clear();
	}

	/**
	 * Metoda wyswietlajaca okienko z wyrzuconymi wyjatkami.
	 * 
	 * @param e
	 *            wyjatek do wyswietlenia w oknie
	 */
	public void showQuickErrorDialog(Exception e) {

		final JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
		textArea.setEditable(false);
		StringWriter writer = new StringWriter();
		e.printStackTrace(new PrintWriter(writer));
		textArea.setText(writer.toString());

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(300, 200));

		JOptionPane.showMessageDialog(this, scrollPane, "An Error Has Occurred", JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new ServerFrame();
			}
		});
	}

	/**
	 * Metoda zatrzymujaca serwer. Ustawia jego boolean IsEnding na true, aby
	 * zasygnalizowac serwerowi, ze ma przestac akceptowac nowych klientow
	 */
	public void stopServer() {
		server.terminate();
		server.stop();
		listPanel.getBoxList().clear();
		listPanel.clearPanel();
		ServerUtilities.updateNumberOfSockets();
		inputPanel.clear();
	}

	/**
	 * Startuje nowy watek serwera.
	 */
	public void startServer() {
		initServer();
	}

	/**
	 * Wypisuje na konsole GUI podany komunikat.
	 * 
	 * @param s
	 *            komunikat do wypisania
	 */
	public void consoleOut(String s) {
		console.append(s + "\n");
	}

	/**
	 * Metoda ktora czysci konsole.
	 */
	public void clearConsole() {
		consolePanel.getConsole().setText("");
	}

	/**
	 * Zwraca liste checkboxow utworzonych w liscie klientow.
	 * 
	 * @return lista checkboxow
	 */
	public ArrayList<JCheckBox> getBoxList() {
		return listPanel.getBoxList();
	}

	public InputPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * Klasa wewnetrzna, obslugujaca drugi watek dzialania serwera.
	 * 
	 * @author Krzysztof Szczyrbak
	 *
	 */
	class ServerThread implements Runnable {

		public void run() {
			// glowna metoda klasy LogServer, otwiera serwerowy socket i w petli
			// deleguje klientow
			System.out.println("THREAD STARTED");
			server.start();
			System.out.println("THREAD STOPPED");
		}

	}

	@Override
	public int getFrameWidth() {
		return width;
	}

	@Override
	public int getFrameHeight() {
		return height;
	}

}
