package logger.server;

import java.io.*;
import java.net.*;

import javax.swing.JTextField;

import logger.frame.ServerFrame;

/**
 * Klasa kontrolujaca serwerem
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class LogServer implements SimpleServer {
	private ServerFrame frame;
	private ServerSocket serverSocket;
	private int PORT_NUM = 8000;
	private boolean isStopped = true;
	private boolean isEnding;
	private UDPThread udpThread;

	public LogServer(int port) throws IOException {
		PORT_NUM = port;
	}

	/**
	 * Metoda startujaca serwer. Jej dzialanie ma nigdy sie nie skonczyc - w
	 * petli przyjmuje nowych klientow.
	 */
	public void start() {
		ServerUtilities.updateNumberOfSockets();
		// otwieraj nowy server socket
		try {
			serverSocket = new ServerSocket(PORT_NUM);
			frame.consoleOut(Inet4Address.getLocalHost().getHostAddress());
			isStopped = false;

		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		}
		udpThread = new UDPThread(PORT_NUM);
		new Thread(udpThread).start();
		frame.consoleOut("LogServer running on port: " + PORT_NUM);

		try {
			while (!isEnding) {
				ServerUtilities.updateNumberOfSockets();
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					System.out.println("accept");
				} catch (SocketException ignored) {
					frame.consoleOut("Server accept method interrupted");
				} catch (IOException e) {
					frame.showQuickErrorDialog(e);
				}

				if (isEnding == true) {
					break;
				}

				// tworzenie nowej klasy obslugujacej klienta
				TCPClient client = ClientWrapperFactory.getNewTCPClient(clientSocket);
				// utworzenie nowego watku i uruchomienie na nim serwera
				// (server.start() )
				new Thread(client).start();
			}
		} finally {
			// jesli petla zostanie przerwana, zamknij socket
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					frame.showQuickErrorDialog(e);
				}
			}
			ServerUtilities.newBackupFile();

		}

	}

	public void setFrame(ServerFrame frame) {
		this.frame = frame;
	}

	/**
	 * Getter dla informacji o stanie zatrzymania.
	 * 
	 * @return boolean isStopped - wskazuje czy serwer jest zatrzymany czy nie.
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Metoda synchronizowana, ktora zatrzymuje serwer.
	 */
	public void stop() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			isStopped = true;
			closeAllClients();
			frame.clearConsole();
		} catch (IOException e) {
			frame.showQuickErrorDialog(e);
		}
	}

	/**
	 * Metoda zamykajaca sockety wszystkich klientow.
	 */
	public void closeAllClients() {
		ServerUtilities.getClientList().clear();
		ServerUtilities.getUniqueNamesList().clear();
		ServerUtilities.getNotAllowedList().clear();
	}

	/**
	 * Metoda sluzaca przekazaniu serwerowi informacje o tym, ze ma sie
	 * zatrzymac.
	 */
	public void terminate() {
		if (udpThread != null)
			udpThread.terminate();
		isEnding = true;
	}

}