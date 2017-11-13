package logger.server;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JCheckBox;

/**
 * Implementacja ClientWrappera. Klasa obslugujaca klienta.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class TCPClient implements ClientWrapper, Runnable {
	private int id;
	private String name;
	private Socket clientSocket;
	private String hostname;
	private boolean isVisible = true;

	public TCPClient(Socket clientSocket, int id) {
		this.clientSocket = clientSocket;
		this.id = id;
		name = "";
		try {
			hostname = clientSocket.getInetAddress().getHostName();
		} catch (NullPointerException e) {
			hostname = "";
		}
	}

	/**
	 * Glowna metoda run klasy. Ustawia parametry, a pozniej deleguje reader z
	 * klienta do metody synchronizowanej, by wyrzucic logi na konsole.
	 */
	@Override
	public void run() {
		InputStream is = null;
		BufferedReader br = null;
		String entryLine = "";
		try {
			is = clientSocket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			br.mark(50);

			if ((entryLine = br.readLine()) != null) {
				String lines[] = entryLine.split(" ");
				ServerUtilities.setClientName(this, lines[0]);
				br.reset();
			}

			ServerUtilities.addToList(this);
			readLogsFromSocket(br);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ServerUtilities.getClientList().remove(this);
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

	}

	/**
	 * Metoda odbierajaca logi przez ClientHandlera
	 * 
	 * @param br
	 *            Buferowany reader z gniazda ktorego ma odczytywac logi
	 */
	private void readLogsFromSocket(BufferedReader br) {

		String line = "";

		// Odbieranie w petli logow wyslanych przez klienta
		try {
			while ((line = br.readLine()) != null) {
				if (line.equals("QUIT"))
					break;

				Object[] log = new Object[7];
				if (!ServerUtilities.isUnique(name)) {
					StringBuilder bd = new StringBuilder();
					String[] tmpLine = line.split(" ");

					line = "";
					tmpLine[0] = getName();
					for (String tmp : tmpLine)
						bd.append(tmp + " ");
					line = bd.toString();

				}
				if (!ServerUtilities.getClientList().contains(this) && !this.name.equals("QUIT"))
					ServerUtilities.addToList(this);

				// -----------------------------------------------
				String[] tmpLine = line.split(" ");
				log[0] = tmpLine[1].trim();
				log[1] = tmpLine[2].trim();
				log[2] = tmpLine[3].trim();
				log[3] = name;
				log[4] = tmpLine[4].trim();
				log[5] = hostname;

				StringBuilder message = new StringBuilder();
				for (int i = 5; i < tmpLine.length; i++) {
					message.append(tmpLine[i] + " ");
				}
				log[6] = message.toString().trim();

				// -----------------------------------------------

				// sprawdzenie, czy logi klienta maja byc kierowane na
				// konsole w tym momencie
				if (isVisible)
					ServerUtilities.sendToFrame(log);
				// powiadamia klase odbierajaca ze moze wyslac
				// odpowiedz klientowi

			}

			stop();
		} catch (SocketException ex) {
			ServerUtilities.getParentFrame().consoleOut(this + "stopped working");
		} catch (IOException e) {
			ServerUtilities.getParentFrame().showQuickErrorDialog(e);
		}

	}

	/**
	 * Metoda zmieniajaca widocznosc logow klienta w konsoli logow.
	 * 
	 * @param status
	 *            wartosc true/false okreslajaca czy klient ma byc
	 *            widoczny/schowany.
	 */
	public void setVisibility(boolean status) {
		setVisible(status);

	}

	/**
	 * Metoda zwracajaca serwerowy identyfikator klienta
	 * 
	 * @return server-side ID klienta
	 */
	public int getID() {
		return id;
	}

	/**
	 * Metoda zwracajaca nazwe klienta
	 * 
	 * @return nazwa klienta
	 */
	public String getName() {
		return name;
	}

	/**
	 * Metoda zatrzymujaca prace handlera
	 */

	@Override
	public void stop() {
		try {
			if (clientSocket != null)
				clientSocket.close();
			clientSocket = null;
		} catch (IOException e) {
			ServerUtilities.getParentFrame().showQuickErrorDialog(e);
		} finally {
			ServerUtilities.removeClient(this);
		}
	}

	/**
	 * Metoda toString. Sluzy celom debuggingowym.
	 */
	public String toString() {
		return name + " id: " + id;

	}

	/**
	 * Metoda zwracajaca wartosc widocznosci klienta.
	 * 
	 * @return true/false, widocznosc klienta
	 * 
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Metoda ustawiajaca widocznosc klienta.
	 * 
	 * @param isVisible
	 *            true/false, w zaleznosci czy klient ma sie chowac, czy byc
	 *            znowu widocznym
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public int getPort() {
		return clientSocket.getPort();
	}

}
