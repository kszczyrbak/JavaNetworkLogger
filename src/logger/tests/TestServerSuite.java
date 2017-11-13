package logger.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestTCPClient.class, TestConfigUtilities.class, TestServerUtilities.class, TestUDPClient.class })
public class TestServerSuite {
}