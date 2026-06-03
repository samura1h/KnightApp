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
        new File(logFileName).delete();
    }

    @AfterEach
    void tearDown() {
        new File(logFileName).delete();
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
    void testErrorWithThrowableLogging() throws Exception {
        String testMessage = "Test ERROR with exception " + System.currentTimeMillis();
        RuntimeException ex = new RuntimeException("Mock Cause Exception");
        
        LoggerService.error(testMessage, ex);

        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");

        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean foundMessage = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[ERROR]"));
        boolean foundException = lines.stream().anyMatch(line -> line.contains("Mock Cause Exception"));
        
        assertTrue(foundMessage, "Log file should contain the ERROR message");
        assertTrue(foundException, "Log file should contain the exception detail");
    }

    @Test
    void testErrorWithNullThrowableLogging() throws Exception {
        String testMessage = "Test ERROR with null exception " + System.currentTimeMillis();
        
        LoggerService.error(testMessage, (Throwable) null);

        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");

        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean found = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[ERROR]"));
        
        assertTrue(found, "Log file should contain the ERROR message");
    }

    @Test
    void testDirectoryCreation() {
        new File(logFileName).delete();
        File dir = new File("logs");
        dir.delete(); 
        
        LoggerService.info("Test Directory Creation");
        assertTrue(dir.exists(), "Directory should be created");
    }

    @Test
    void testIOException() throws Exception {
        File logFile = new File(logFileName);
        File dir = new File("logs");
        dir.mkdirs();
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
