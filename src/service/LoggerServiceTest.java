package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggerServiceTest {

    private final String logFileName = "logs/app_log.txt";

    @BeforeEach
    void setUp() {
        
        File file = new File(logFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        
        File file = new File(logFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testInfoLogging() throws Exception {
        String testMessage = "Test INFO message " + System.currentTimeMillis();
        
        LoggerService.info(testMessage);

        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");

        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean found = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[INFO]"));
        
        assertTrue(found, "Log file should contain the INFO message");
    }

    @Test
    void testErrorLogging() throws Exception {
        String testMessage = "Test ERROR message " + System.currentTimeMillis();
        
        LoggerService.error(testMessage);

        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");

        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean found = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[ERROR]"));
        
        assertTrue(found, "Log file should contain the ERROR message");
    }

    @Test
    void testDirectoryCreation() {
        File logFile = new File(logFileName);
        if (logFile.exists()) logFile.delete();
        File dir = new File("logs");
        if (dir.exists()) dir.delete(); 
        
        LoggerService.info("Test Directory Creation");
        assertTrue(dir.exists(), "Directory should be created");
    }

    @Test
    void testIOException() throws Exception {
        File logFile = new File(logFileName);
        File dir = new File("logs");
        if (!dir.exists()) dir.mkdirs();
        logFile.createNewFile();
        logFile.setReadOnly(); 
        
        try {
            assertDoesNotThrow(() -> LoggerService.info("This should trigger IOException internally"));
        } finally {
            logFile.setWritable(true); 
        }
    }

    @Test
    void testConstructorForCoverage() {
        assertDoesNotThrow(() -> new LoggerService());
    }
}
