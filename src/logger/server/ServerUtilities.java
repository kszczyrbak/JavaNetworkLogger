package logger.server;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableRowSorter;

import logger.frame.LogObject;
import logger.frame.LoggerTableModel;
import logger.frame.ServerFrame;
import logger.frame.SimpleServerFrame;

/**
 * Klasa z narzedziami do obslugi i synchronizowania GUI i serwera.
 * 
 * @author Chris
 *
 */
public class ServerUtilities {
	private static ServerFrame frame;
	private static LoggerTableModel output;
	private static JTable table;
	private static PrintWriter fullLogPrintWriter;
	private static String DIRPATH = "tmp";
	private static File fullLogFile;
	private final static ArrayList<ClientWrapper> clientList = new ArrayList<ClientWrapper>();
	private final static ArrayList<String> notAllowedList = new ArrayList<String>();
	private final static ArrayList<String> uniqueNamesList = new ArrayList<String>();

	/**
	 * Metoda tworzaca plik zapisujacy logi oraz czyszczaca pliki z logami,
	 * ktore powstaly w wyniku dzialania programu i nie zostaly usuniete
	 */
	public static void enableFileSaving() {
		if (!new File("logs").exists())
			if (!new File("logs").mkdirs())
				frame.consoleOut("Failed creating a new log directory");
		if (new File(DIRPATH).exists()) {
			if (fullLogPrintWriter != null)
				fullLogPrintWriter.close();
			deleteFilesFromDirectory(DIRPATH);
		} else if (new File(getDirPath()).mkdirs() == false)
			try {
				throw new Exception("Blad przy tworzeniu katalogow");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		fullLogFile = new File(getDirPath() + "/fullLogFile.txt");

		try {
			fullLogPrintWriter = new PrintWriter(fullLogFile, "UTF-8");
		} catch (FileNotFoundException e) {
			frame.showQuickErrorDialog(e);
		} catch (UnsupportedEncodingException e) {
			frame.showQuickErrorDialog(e);
		}
	}

	/**
	 * Wskazuje okienko, w ktorym maja wypisywac sie logi
	 * 
	 * @param Joutput
	 *            referencja do konsoli w glownym oknie
	 */
	public static void setOutput(LoggerTableModel model, JTable Jtable) {
		output = model;
		table = Jtable;
	}

	/**
	 * Synchronizowana metoda odpowiadajaca za wyslanie logu na konsole.
	 * 
	 * @param client
	 *            referencja klasy obslugujacej klienta ktory wyslal log
	 * @param msg
	 *            String zawierajacy komunikat wyslany przez klienta
	 * 
	 */
	static synchronized void sendToFrame(final Object[] log) {

		// zapisywanie do pliku otrzymanego komunikatu
		fullLogPrintWriter.println(log);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				output.addData(log);
			}
		});

		fullLogPrintWriter.flush();

	}

	/**
	 * Synchronizowana metoda dodajaca klienta do listy
	 * 
	 * @param client
	 *            referencja do klasy obslugujacej klienta
	 */
	public static synchronized void addToList(ClientWrapper client) {
		clientList.add(client);
		if (!uniqueNamesList.contains(client.getName()))
			uniqueNamesList.add(client.getName());
		updateNumberOfSockets();
		frame.addListComponent(client);
	}

	/**
	 * Metoda usuwajaca pliki z katalogu, jesli nie jest pusty
	 * 
	 * @param folderPath
	 *            sciezka do katalogu
	 */
	public static synchronized void deleteFilesFromDirectory(String folderPath) {
		File folder = new File(folderPath);
		if (folder.listFiles() != null) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File f : files)
					if (f.exists()) {
						if (f.delete() == false) {
							try {
								throw new Exception("Blad przy usuwaniu plikow z katalogu " + folder + ", plik " + f);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
			}
		}
	}

	/**
	 * Metoda ustawiajaca nazwe klienta. Z reguly wywolywana na podstawie
	 * pierwszej linii logow wyslanej przez klienta - bo powinien zaczynac sie
	 * od nazwy
	 * 
	 * @param line
	 *            string tworzacy nazwe
	 */
	public synchronized static void setClientName(ClientWrapper client, String line) {
		client.setName(line);
		int uniNum = 0;

		for (String c : uniqueNamesList) {
			if (c.equals(client.getName())) {
				client.setName(line + "(" + ++uniNum + ")");
			}
		}

		uniqueNamesList.add(client.getName());
	}

	/**
	 * Metoda zwracajaca liste klientow
	 * 
	 * @return clientList lista klientow
	 */
	public static synchronized ArrayList<ClientWrapper> getClientList() {
		return clientList;
	}

	/**
	 * Metoda zmieniajaca widocznosc klienta w konsoli.
	 * 
	 * @param client
	 *            referencja do klasy obslugujacej
	 * @param status
	 *            wartosc true/false
	 */
	public static void setVisibility(ClientWrapper client, boolean status) {

		client.setVisibility(status);

		if (!status) {
			notAllowedList.add(client.getName());
		}

		else {
			notAllowedList.remove(client.getName());
		}
	}

	/**
	 * Metoda sprawdzajaca czy podana nazwa jest unikalna.
	 * 
	 * @param name
	 *            nazwa do sprawdzenia
	 * @return true/false
	 */
	public static boolean isUnique(String name) {
		boolean found = false;
		for (String str : uniqueNamesList)
			if (str.equals(name))
				if (!found)
					found = true;
				else
					return false;
		return true;
	}

	/**
	 * Metoda obslugujaca zmiane wyswietlanych logow na konsoli w glownym oknie.
	 * 
	 * @param client
	 * 
	 */
	public static void changeWindowOutput() {
		frame.getInputPanel().newFilter();
	}

	/**
	 * Ustawia referencje do glownego okna
	 * 
	 * @param ignoredframe
	 *            referencja glownego okna
	 */
	public static void setServerFrame(ServerFrame parentFrame) {
		frame = parentFrame;
	}

	/**
	 * Metoda zapisujaca biezacy stan konsoli do pliku wskazanego przez
	 * uzytkownika
	 * 
	 * @param dest
	 *            sciezka do pliku do zapisania
	 * @throws IOException
	 */
	public static boolean saveLogs(File dest) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(dest, "UTF-8");

			writer.println("  Date   |  Time  |  Priority  |   Name   | Thread |  Hostname  |    Message   ");
			writer.println("--------------------------------------------------------------------------");
			writer.println("");
			writer.println("--------------------------------------------------------------------------");

			for (int row = 0; row < table.getRowCount(); row++) {
				for (int i = 0; i < LogObject.columnSize(); i++) {
					writer.print(table.getModel().getValueAt(table.convertRowIndexToModel(row), i));
					writer.print(" | ");
					writer.flush();
				}
				writer.println("");
				writer.println("----------------------------------------------------------------------");
				writer.flush();
			}
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
			return false;
		} finally {
			if (writer != null)
				writer.close();
		}

		return true;
	}

	/**
	 * Metoda wykorzystujaca JFileChooser by pozwolic uzytkownikowi wybrac plik
	 * 
	 * @return File wybrany plik
	 */
	public static File chooseFile(Component com) {
		String filename = "";
		String dirpath = "";
		JFileChooser c = new JFileChooser();
		c.setCurrentDirectory(new File("."));
		int tmpVal = c.showOpenDialog(com);
		if (tmpVal == JFileChooser.APPROVE_OPTION) {
			filename = c.getSelectedFile().getName();
			dirpath = c.getCurrentDirectory().toString();
		}
		if (tmpVal == JFileChooser.CANCEL_OPTION) {
			return new File("");
		}
		File f = new File(dirpath + "/" + filename);
		return f;
	}

	/**
	 * Wczytuje zapisane logi z pliku i wyswietla je na konsoli, chowajac
	 * wszystkich biezacych klientow
	 * 
	 * @param loadFile
	 *            sciezka do pliku do wczytania
	 */
	public static boolean loadLogs(File loadFile) {

		// schowaj wszystkich klientow
		for (ClientWrapper c : clientList) {
			notAllowedList.add(c.getName());
			c.setVisibility(false);
		}

		BufferedReader reader = null;
		int lineCount = 0;
		// wyswietl logi z pliku
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(loadFile), "UTF-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {

				lineCount++;
				if (lineCount < 5 || lineCount % 2 == 0)
					continue;
				Object[] log = new Object[7];

				String[] split = line.split("\\|");

				log[0] = split[0].trim();
				log[1] = split[1].trim();
				log[2] = split[2].trim();
				log[3] = split[3].trim();
				log[4] = split[4].trim();
				log[5] = split[5].trim();
				log[6] = split[6].trim();

				output.addData(log);
			}

		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
			return false;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	/**
	 * Metoda zwracajaca liste zakazanych tagow.
	 * 
	 * @return notAllowedList lista zakazanych nazw
	 */
	public synchronized static ArrayList<String> getNotAllowedList() {
		return notAllowedList;
	}

	/**
	 * Metoda do kopiowania zawartosci pliku
	 * 
	 * @param sourceFile
	 *            plik zrodlowy
	 * @param destFile
	 *            plik docelowy
	 * @throws IOException
	 */

	public static boolean copyFile(File sourceFile, File destFile) {
		InputStream in = null;
		OutputStream out = null;
		if (!destFile.exists())
			try {
				if (!destFile.createNewFile())
					frame.consoleOut("Failed creating a chosen file!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		try {
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					in = null;
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					out = null;
					e.printStackTrace();
				}
		}
		return true;
	}

	/**
	 * Metoda ustawiajaca wszystkim klientow status niewidoczny.
	 */
	public static void hideAllClients() {
		for (ClientWrapper c : clientList) {
			setVisibility(c, false);
			changeWindowOutput();
		}
	}

	/**
	 * Metoda ustawiajaca wszystkim klientom status dostepny.
	 */
	public static void showAllClients() {
		for (ClientWrapper c : clientList) {
			setVisibility(c, true);
			changeWindowOutput();
		}

	}

	/**
	 * Zwraca sciezke do glownego pliku z logami w postaci obiektu FIle.
	 * 
	 * @return sciezka do wszystkich logow
	 */
	public static File getFullLogFile() {
		return fullLogFile;
	}

	/**
	 * Getter dla sciezki katalogu plikow z logami.
	 * 
	 * @return sciezka do katalogu
	 */
	public static String getDirPath() {
		return DIRPATH;
	}

	/**
	 * Metoda ustawia podana sciezke dla katalogu plikow z logami.
	 * 
	 * @param dirPath
	 *            sciezka do katalogu
	 */
	public static void setDirPath(String dirPath) {
		DIRPATH = dirPath;
	}

	/**
	 * Getter dla glownego okna GUI.
	 * 
	 * @return SimpleFrame okno GUI serwera
	 */
	public static ServerFrame getParentFrame() {
		return frame;
	}

	/**
	 * Getter dla pola tekstowego, na ktory parsuja sie logi.
	 * 
	 * @return JTextArea wyjsciowe pole tekstowe
	 */
	public static LoggerTableModel getOutput() {
		return output;
	}

	/**
	 * Zwraca PrintWritera pliku zawierajacego wszystkie logi.
	 * 
	 * @return PrintWriter do fullLogFile
	 */
	public static PrintWriter getFullLogPrintWriter() {
		return fullLogPrintWriter;
	}

	/**
	 * Getter listy unikalnych tagow klientow.
	 * 
	 * @return lista unikalnych nazw
	 */
	public synchronized static ArrayList<String> getUniqueNamesList() {
		return uniqueNamesList;
	}

	/**
	 * Metoda odswiezajaca pasek informujacy ile socketow jest podlaczonych do
	 * serwera.
	 */
	public synchronized static void updateNumberOfSockets() {
		frame.updateNumberOfSockets();

	}

	/**
	 * Metoda usuwajaca klienta z listy klientow.
	 * 
	 * @param client
	 *            klient do usuniecia
	 */
	public synchronized static void removeClient(ClientWrapper client) {
		clientList.remove(client);
		updateNumberOfSockets();

	}

	/**
	 * Metoda tworzaca nowy plik z biezaca data i czasem. Wykonuje sie przy
	 * kazdym zamknieciu programu
	 * 
	 * @return plik z logami
	 */
	public static void newBackupFile() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		File tmp = new File("logs/" + dateFormat.format(date) + ".txt");
		try {
			if (!tmp.createNewFile())
				System.out.println("");
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		}
		saveLogs(tmp);
	}

}
