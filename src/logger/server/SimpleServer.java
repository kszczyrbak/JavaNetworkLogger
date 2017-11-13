package logger.server;

/**
 * Interfejs prostego socketowego serwera.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public interface SimpleServer {

	/**
	 * Metoda startujaca serwer.
	 */
	public void start();

	/**
	 * Metoda usypiajaca serwer.
	 */
	public void stop();
}
