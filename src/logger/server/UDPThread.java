package logger.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import logger.frame.ServerFrame;

/**
 * Klasa obslugujaca osobny watek przyjmujacy polaczenia UDP.
 * 
 * @author Krzysztof Szczyrbak
 *
 */
public class UDPThread implements Runnable {
	private ServerFrame frame = ServerUtilities.getParentFrame();
	final int PORT_NUM;
	private DatagramSocket UDPSocket;
	private byte[] receiveData;
	private byte[] responseData;
	private boolean isEnding = false;
	private ArrayList<UDPClient> UDPClientList;

	UDPThread(int port) {
		PORT_NUM = port;
		UDPClientList = new ArrayList<UDPClient>();
	}

	@Override
	public void run() {
		try {
			UDPSocket = new DatagramSocket(PORT_NUM);
		} catch (SocketException e) {
			frame.showQuickErrorDialog(e);
		}
		receiveData = new byte[1024];
		responseData = new byte[1024];

		while (!isEnding) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				UDPSocket.receive(receivePacket);
			} catch (SocketException ignored) {
				break;
			} catch (IOException e) {
				frame.showQuickErrorDialog(e);
			}

			String log = "";
			try {
				log = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			UDPClient client = checkClient(receivePacket, log);
			try {
				responseData = log.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			if (log.equals("QUIT")) {
				client.stop();
				try {
					responseData = "END".getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				continue;
			}

			Object[] logTable = new Object[7];

			String[] tmpLine = log.split(" ");
			logTable[0] = tmpLine[1].trim();
			logTable[1] = tmpLine[2].trim();
			logTable[2] = tmpLine[3].trim();
			logTable[3] = client.getName();
			logTable[4] = tmpLine[4].trim();
			logTable[5] = receivePacket.getAddress().getHostName();

			StringBuilder message = new StringBuilder();
			for (int i = 5; i < tmpLine.length; i++) {
				message.append(tmpLine[i] + " ");
			}
			logTable[6] = message.toString().trim();

			if (client.isVisible())
				ServerUtilities.sendToFrame(logTable);

			InetAddress IPAddress = receivePacket.getAddress();
			int receivePort = receivePacket.getPort();

			DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, IPAddress,
					receivePort);
			try {
				UDPSocket.send(responsePacket);
			} catch (SocketException ex) {
				frame.consoleOut("Packet cannot be sent!");
			} catch (IOException e) {
				frame.showQuickErrorDialog(e);
			}
		}

		if (UDPSocket != null)
			UDPSocket.close();
		UDPSocket = null;

	}

	private UDPClient checkClient(DatagramPacket receivePacket, String log) {
		int port = receivePacket.getPort();
		for (UDPClient client : UDPClientList) {
			if (client.getPort() == port)
				return client;
		}

		UDPClient newClient = ClientWrapperFactory.getNewUDPClient(port);
		String lines[] = log.split(" ");
		ServerUtilities.setClientName(newClient, lines[0]);
		ServerUtilities.addToList(newClient);
		UDPClientList.add(newClient);
		return newClient;
	}

	public void terminate() {
		isEnding = true;
		UDPSocket.close();
	}

}
