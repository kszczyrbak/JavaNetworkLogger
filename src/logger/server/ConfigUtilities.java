package logger.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import logger.frame.ServerFrame;

/**
 * Klasa z narzedziami do obslugi konfiguracji aplikacji.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class ConfigUtilities {
	private static Properties config;
	private static int numberOfProperties = 0;
	private static ServerFrame frame;
	private static Properties defaultConfig;

	private static File configFile;

	/**
	 * Metoda zwracajaca referencje do pliku konfiguracyjnego
	 * 
	 * @return File configFile plik konfiguracjny
	 */
	public static File getConfigurationFile() {
		return configFile;
	}

	/**
	 * Metoda zwracajaca referencje do obecnej konfiguracji programu.
	 * 
	 * @return Properties config konfiguracja
	 */
	public static Properties getConfiguration() {
		return config;
	}

	/**
	 * Metoda inicjalizujaca plik konfiguracyjny i konfiguracje programu.
	 * 
	 * @param f
	 *            referencja do glownego okna programu
	 */
	public static void initConfigurationFile(ServerFrame f) {
		numberOfProperties = 0;
		frame = f;
		Properties defaultConfig = getDefaultProperties();
		configFile = new File("config.properties");

		// jesli domyslny plik kofniguracyjny nie istnieje, utworz go i wypelnij
		// domyslna konfiguracje
		if (!configFile.exists()) {
			config = defaultConfig;
			generateConfigFile(defaultConfig);
			return;
		}

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
			config = new Properties();
			config.load(reader);
			reader.close();
			String configPath = config.getProperty("config_path");

			// jesli wlasnosc "config_path" nie wkazuje na domyslny plik
			// konfiguracyjny, wczytaj plik podany w tej wlasnosci
			if (!configPath.equals("config.properties")) {
				configFile = new File(configPath);
				// jesli ten plik nie istnieje, wygeneruj go w przyjetym
				// configPath (czyli tam gdzie wskazuje config_path)
				if (!configFile.exists())
					generateConfigFile(defaultConfig);
				// wczytaj konfiguracje z pliku o podanej w pliku sciezce
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
				config.load(reader);
				reader.close();
			}

			// metoda sprawdzajaca ktre wartosci nie sa wypelnione i
			// wypelniajaca je domyslnymi wartosciami
			fillMissingProperties(config);

		} catch (FileNotFoundException ex) {
			frame.showQuickErrorDialog(ex);
		} catch (IOException ex) {
			frame.showQuickErrorDialog(ex);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Metoda wypelniajaca brakujace wlasnosci w zalaczonej konfiguracji..
	 * 
	 * @param config
	 *            config do wypelnienia
	 */
	public synchronized static void fillMissingProperties(Properties config) {
		boolean missing = false;

		Set<Object> defaultKeySet = defaultConfig.keySet();
		Set<Object> finalKeySet = config.keySet();
		if (defaultKeySet.equals(finalKeySet)) {
			return;
		} else {
			for (Object s : defaultKeySet) {
				String key = (String) s;
				if (config.getProperty(key) == null) {
					config.setProperty(key, defaultConfig.getProperty(key));
					missing = true;
				}
			}
		}

		if (missing) {
			if (configFile != null) {
				if (!configFile.delete())
					try {
						throw new Exception("Failed deleting the config file");
					} catch (Exception e) {
						frame.showQuickErrorDialog(e);
					}

				configFile = new File(config.getProperty("config_path"));
				generateConfigFile(config);
			}
		}

	}

	/**
	 * Metoda tworzaca nowy plik konfiguracyjny na podstawie podanej
	 * konfiguracji.
	 * 
	 * @param config
	 *            konfiguracja
	 */
	public synchronized static void generateConfigFile(Properties config) {

		if (configFile == null) {
			configFile = new File("config.properties");
		}

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(configFile, "UTF-8");

			if (!configFile.getPath().equals(config.getProperty("config_path")))
				writer = new PrintWriter(new File(config.getProperty("config_path")), "UTF-8");
			config.store(writer, null);
			writer.close();
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		} finally {
			if (writer != null)
				writer.close();
		}

	}

	/**
	 * Metoda zwracajaca domyslna konfiguracje.
	 * 
	 * @return Properties domyslna konfiguracja
	 */
	public static Properties getDefaultProperties() {
		defaultConfig = new Properties();
		addNewDefaultProperty("config_path", "config.properties");
		addNewDefaultProperty("width", "1000");
		addNewDefaultProperty("height", "800");
		addNewDefaultProperty("port", "8000");
		addNewDefaultProperty("host", "localhost");

		return defaultConfig;
	}

	/**
	 * Metoda dodajaca nowa wlasnosc do domyslnej konfiguracji.
	 * 
	 * @param property
	 *            nowa wlasnosc
	 * @param defaultVal
	 *            wartosc nowej wlasnosci
	 */
	public static void addNewDefaultProperty(String property, String defaultVal) {
		defaultConfig.setProperty(property, defaultVal);
		numberOfProperties++;
	}

	/**
	 * Metoda zmieniajaca wartosc podanej wlasnosci w podanym pliku
	 * konfiguracyjnym.
	 * 
	 * @param configFile
	 *            referencja do pliku konfiguracyjnego
	 * @param property
	 *            wlasnosc
	 * @param value
	 *            nowa wartosc wlasnosci
	 */
	public static void changeProperty(File configFile, String property, String value) {
		Properties tmp = new Properties();
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
			tmp.load(in);
			in.close();
			out = new PrintWriter(configFile, "UTF-8");
			tmp.setProperty(property, value);
			tmp.store(out, null);
			out.close();
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (out != null)
				out.close();
		}

	}

	/**
	 * Metoda zwracajaca liczbe wlasnosci konfiguracji.
	 * 
	 * @return int numberOfProperties liczba wlasnosci konfiguracji
	 */
	public static int getNoOfProperties() {
		return numberOfProperties;
	}

	/**
	 * Metoda wczytujaca konfiguracje z zadanego pliku
	 * 
	 * @param configFile
	 *            referencja do pliku zawierajacego konfiguracje do wczytania
	 * @return Properties konfiguracja wczytana z pliku
	 */
	public static Properties loadConfigFile(File configFile) {
		Properties tmp = new Properties();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
			tmp.load(reader);
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
		fillMissingProperties(tmp);
		config = tmp;

		return tmp;
	}

}
