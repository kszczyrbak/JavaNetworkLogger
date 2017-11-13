package logger.frame;

import logger.server.ClientWrapper;

/**
 * Interfejs prostego glownego okna GUI serwera.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public interface SimpleServerFrame {

	/**
	 * Metoda dodajaca nowego klienta (a raczej jego wrapper) do listy klientow
	 * (znajdujacej sie gdzies w glowym oknie).
	 * 
	 * @param wrapper
	 *            klasa opakowujaca klienta
	 */
	public void addListComponent(ClientWrapper wrapper);

	/**
	 * Metoda tworzaca nowe okno i wyrzucajaca przyjety wyjatek na okno GUI, aby
	 * mozna bylo na biezaco czytac jego tresc.
	 * 
	 * @param e
	 *            rzucony wyjatek
	 */
	public void showQuickErrorDialog(Exception e);

	/**
	 * Metoda zatrzymujaca serwer z poziomu GUI.
	 */
	public void stopServer();

	/**
	 * Metoda startujaca serwer z poziomu GUI.
	 */
	public void startServer();

	/**
	 * Zwraca szerokoœæ okna.
	 */
	public int getFrameWidth();

	/**
	 * Zwraca wysokoœæ okna.
	 */
	public int getFrameHeight();
}
