package logger.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JCheckBox;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import logger.frame.ServerFrame;
import logger.server.TCPClient;
import logger.server.UDPClient;
import logger.server.LogServer;
import logger.server.ServerUtilities;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TCPClient.class })
public class TestUDPClient {
	private UDPClient testClient;

	@Before
	public void initialiseTestClient() throws Exception {
		ServerFrame f = Mockito.mock(ServerFrame.class);
		ServerUtilities.setServerFrame(f);

		testClient = new UDPClient(8000, 0);

	}

	@Test
	public void testSetVisibility() {

		assertTrue(testClient.isVisible());
		testClient.setVisibility(false);
		assertFalse(testClient.isVisible());

	}

	@Test
	public void testStop() throws UnknownHostException, IOException {
		testClient = new UDPClient(8000, 0);
		assertEquals(testClient.getPort(), 8000);
		testClient.stop();
	}

}
