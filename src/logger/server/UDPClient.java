package logger.server;

public class UDPClient implements ClientWrapper {
	private int id = 0;
	private String name;
	private boolean isVisible = true;
	private int port;

	public UDPClient(int port, int id) {
		this.setID(id);
		this.setPort(port);
	}

	@Override
	public void stop() {
		ServerUtilities.removeClient(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setVisibility(boolean b) {
		setVisible(b);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

}
