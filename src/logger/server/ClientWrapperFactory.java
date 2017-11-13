package logger.server;

import java.net.Socket;

/**
 * Fabryka implementacji interfejsu ClientWrapper.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class ClientWrapperFactory {
	private static int clientsMade = 0;

	/**
	 * Metoda zwraca nowy obiekt klienta TCP dla LogServera.
	 * 
	 * @param clientSocket
	 *            podany socket dla ktorego tworzymy obiekt wrapujacy
	 * @return nowy TCPClient
	 */
	public static TCPClient getNewTCPClient(Socket clientSocket) {
		TCPClient client = new TCPClient(clientSocket, clientsMade);
		clientsMade++;
		return client;
	}

	/**
	 * Metoda zwraca nowy obiekt klienta UDP.
	 * 
	 * @return nowy UDPClient
	 */
	public static UDPClient getNewUDPClient(int port) {
		UDPClient client = new UDPClient(port, clientsMade);
		clientsMade++;
		return client;
	}
}
