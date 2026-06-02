package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/app_log.txt";

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        log("INFO", message);
    }

    public static void error(String message) {
        log("ERROR", message);
        
        EmailService.sendAsync("CRITICAL ERROR", message);
    }

    public static void error(String message, Throwable t) {
        String fullMessage = message;
        if (t != null) {
            java.io.StringWriter sw = new java.io.StringWriter();
            t.printStackTrace(new java.io.PrintWriter(sw));
            fullMessage += "\nException: " + t.toString() + "\n" + sw.toString();
        }
        log("ERROR", fullMessage);
        EmailService.sendAsync("CRITICAL ERROR", fullMessage);
    }

    private static synchronized void log(String level, String message) {
        String timestamp = dtf.format(LocalDateTime.now());
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        System.out.println(formattedMessage);

        try {
            
            File directory = new File(LOG_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8, true))) {
                writer.write(formattedMessage);
                writer.newLine(); 
            }
        } catch (IOException e) {
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }
}