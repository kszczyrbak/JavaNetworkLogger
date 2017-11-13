package logger.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import logger.frame.LoggerTableModel;
import logger.frame.ServerFrame;
import logger.frame.SimpleServerFrame;
import logger.server.TCPClient;
import logger.server.ClientWrapper;
import logger.server.ServerUtilities;

public class TestServerUtilities {
	private TCPClient client;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void initialiseClientHandler() {
		client = new TCPClient(null, 0);
		LoggerTableModel ignored = Mockito.mock(LoggerTableModel.class);
		JTable table = Mockito.mock(JTable.class);
		ServerUtilities.setOutput(ignored, table);
		ServerUtilities.setServerFrame(Mockito.mock(ServerFrame.class));

	}

	@Test
	public void testEnableFileSaving() throws IOException {
		ServerUtilities.setDirPath(folder.newFolder("TEST").getPath());
		ServerUtilities.enableFileSaving();
		assertTrue(ServerUtilities.getFullLogFile().exists());

		File tmpDir = new File(ServerUtilities.getDirPath());
		File[] files = tmpDir.listFiles();
		if (files != null) {
			assertEquals(1, files.length);
			assertEquals(ServerUtilities.getFullLogFile(), files[0]);
			ServerUtilities.getFullLogPrintWriter().close();
			assertTrue(ServerUtilities.getFullLogFile().delete());
			ServerUtilities.deleteFilesFromDirectory(ServerUtilities.getDirPath());
		}
		if (!ServerUtilities.getFullLogFile().delete())
			System.out.println("");
	}

	@Test
	public void testSetName() {
		if (client != null) {
			client.setName("test");
			assertEquals("test", client.getName());
		}
	}

	@Test
	public void testAddToList() {
		ArrayList<ClientWrapper> testList = ServerUtilities.getClientList();
		ServerUtilities.addToList(client);
		assertEquals(1, testList.size());
	}

	@Test
	public void testDeleteFilesFromDirectory() throws IOException {
		File test1 = folder.newFile("test1.txt");
		File test2 = folder.newFile("test2.txt");
		assertTrue(test1.exists());
		assertTrue(test2.exists());
		ServerUtilities.deleteFilesFromDirectory(folder.getRoot().toString());
		assertFalse(test1.exists());
		assertFalse(test2.exists());
	}

	@Test
	public void testSetVisibility() {
		if (client != null) {
			ServerUtilities.enableFileSaving();
			assertTrue(client.isVisible());
			ServerUtilities.setVisibility(client, false);
			assertFalse(client.isVisible());
			ServerUtilities.setVisibility(client, true);
			assertTrue(client.isVisible());
		}
	}

	@Test
	public void testCopyFile() throws IOException {
		File test1 = folder.newFile("test1.txt");
		assertTrue(test1.exists());
		PrintWriter writer = new PrintWriter(test1, "UTF-8");
		writer.println("Hello World");
		writer.close();
		File test2 = folder.newFile("test2.txt");
		ServerUtilities.deleteFilesFromDirectory("test");
		assertTrue(test2.exists());
		ServerUtilities.copyFile(test1, test2);
		assertTrue(test2.exists());
		BufferedReader reader = null;
		String hello = "";
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(test2), "UTF-8"));
			hello = reader.readLine();

		} finally {
			if (reader != null)
				reader.close();
		}

		assertEquals("Hello World", hello);
		reader.close();
		assertTrue(test2.delete());
		assertTrue(test1.delete());
	}

	@Test
	public void testSaveLogs() throws IOException {
		File test1 = folder.newFile();
		assertTrue(ServerUtilities.getNotAllowedList().isEmpty());
		ServerUtilities.enableFileSaving();
		File fullLogFile = ServerUtilities.getFullLogFile();
		PrintWriter writer = new PrintWriter(fullLogFile, "UTF-8");
		writer.println("TEST");
		writer.close();
		ServerUtilities.getFullLogPrintWriter().close();
		ServerUtilities.saveLogs(test1);

		String test = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullLogFile), "UTF-8"));
			test = reader.readLine();
		} finally {
			if (reader != null)
				reader.close();
		}
		assertTrue(fullLogFile.delete());
		assertEquals("TEST", test);
		assertTrue(test1.delete());

	}

	@Test
	public void testLoadLogs() throws IOException {
		File test = folder.newFile();
		assertTrue(ServerUtilities.loadLogs(test));
	}

	@Test
	public void testChangeWindowOutput() throws IOException {
		ServerUtilities.enableFileSaving();
		File testFile = ServerUtilities.getFullLogFile();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(testFile, "UTF-8");
			writer.println("test1 : ABRAKADABRA");
			writer.println("test2 : ABECADLO");
			writer.println("test3 : XYZ");
			writer.println("test1 : XZY");
			writer.println("test2 : ABRAKADABRA");
			writer.println("test1 : ABAB");
		} finally {
			if (writer != null)
				writer.close();
		}
		ServerUtilities.getNotAllowedList().add(client.getName());
		//ServerUtilities.changeWindowOutput();
		File finalTestFile = new File(ServerUtilities.getDirPath() + "/current.txt");

		finalTestFile.createNewFile();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(finalTestFile), "UTF-8"));
		} finally {
			if (reader != null)
				reader.close();
		}
		assertTrue(finalTestFile.delete());
		ServerUtilities.getFullLogPrintWriter().close();
		assertTrue(ServerUtilities.getFullLogFile().delete());
	}

}
