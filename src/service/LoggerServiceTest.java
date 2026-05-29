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
        // Очищаємо файл логів перед кожним тестом, якщо він існує
        File file = new File(logFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Очищаємо після тесту
        File file = new File(logFileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testInfoLogging() throws Exception {
        String testMessage = "Test INFO message " + System.currentTimeMillis();
        
        LoggerService.info(testMessage);
        
        // Перевіряємо, чи був створений файл
        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");
        
        // Читаємо вміст файлу
        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean found = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[INFO]"));
        
        assertTrue(found, "Log file should contain the INFO message");
    }

    @Test
    void testErrorLogging() throws Exception {
        String testMessage = "Test ERROR message " + System.currentTimeMillis();
        
        LoggerService.error(testMessage);
        
        // Перевіряємо, чи був створений файл
        File logFile = new File(logFileName);
        assertTrue(logFile.exists(), "Log file should be created");
        
        // Читаємо вміст файлу
        List<String> lines = Files.readAllLines(Paths.get(logFileName));
        boolean found = lines.stream().anyMatch(line -> line.contains(testMessage) && line.contains("[ERROR]"));
        
        assertTrue(found, "Log file should contain the ERROR message");
    }
}
