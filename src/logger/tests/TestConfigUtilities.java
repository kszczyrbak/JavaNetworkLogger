package logger.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import logger.frame.ServerFrame;
import logger.server.ConfigUtilities;

public class TestConfigUtilities {
	private ServerFrame f;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void initMockObjects() {
		f = Mockito.mock(ServerFrame.class);
	}

	@Test
	public void testInitConfigurationFile() throws IOException {
		File testFile = ConfigUtilities.getConfigurationFile();
		assertTrue(testFile.delete());
		assertFalse(testFile.exists());
		ConfigUtilities.initConfigurationFile(f);
		assertTrue(testFile.exists());
		Properties tmp = ConfigUtilities.getConfiguration();
		Properties test = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(testFile);
			test.load(fis);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println(ConfigUtilities.getNoOfProperties());
		assertEquals(tmp, test);
	}

	@Test
	public void testGetDefaultProperties() {
		Properties testProps = new Properties();
		testProps.setProperty("config_path", "config.properties");
		testProps.setProperty("width", "1000");
		testProps.setProperty("height", "800");
		testProps.setProperty("port", "8000");
		testProps.setProperty("host", "localhost");
		Properties testProps2 = ConfigUtilities.getDefaultProperties();
		assertEquals(testProps, testProps2);
	}

	@Test
	public void testAddNewDefaultProperty() {
		Properties tmpProps = ConfigUtilities.getDefaultProperties();
		int propNumber = ConfigUtilities.getNoOfProperties();
		ConfigUtilities.addNewDefaultProperty("test", "123");
		assertEquals(propNumber + 1, ConfigUtilities.getNoOfProperties());
		String test = tmpProps.getProperty("test");
		assertEquals("123", test);
	}

	@Test
	public void testChangeProperty() throws IOException {
		File testConfigFile = folder.newFile();
		Properties def = ConfigUtilities.getDefaultProperties();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(testConfigFile, "UTF-8");
			def.store(writer, null);
		} finally {
			if (writer != null)
				writer.close();
		}

		Properties test = new Properties();
		test.setProperty("port", "7600");
		ConfigUtilities.fillMissingProperties(test);
		ConfigUtilities.changeProperty(testConfigFile, "port", "7600");
		Properties test2 = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(testConfigFile);
			test2.load(fis);
		} finally {
			if (fis != null)
				fis.close();
		}

		assertEquals(test, test2);
	}

	@Test
	public void testLoadConfigFile() throws IOException {
		File testConfigFile = folder.newFile();
		Properties tmp = ConfigUtilities.getDefaultProperties();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(testConfigFile, "UTF-8");
			tmp.store(writer, null);
		} finally {
			if (writer != null)
				writer.close();
		}
		Properties test = ConfigUtilities.loadConfigFile(testConfigFile);
		assertEquals(test, tmp);
	}

	@Test
	public void generateConfigFile() throws IOException {
		Properties tmp = ConfigUtilities.getDefaultProperties();
		ConfigUtilities.generateConfigFile(tmp);
		Properties test = ConfigUtilities.loadConfigFile(ConfigUtilities.getConfigurationFile());
		assertEquals(test, tmp);
	}

	@Test
	public void testFillMissingProperties() {
		Properties tmp = ConfigUtilities.getDefaultProperties();
		Properties test = new Properties();
		test.setProperty("port", "8000");
		ConfigUtilities.fillMissingProperties(test);
		assertEquals(tmp, test);
	}

}
