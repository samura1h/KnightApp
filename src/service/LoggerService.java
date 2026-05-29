package service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService {
    // Шлях до файлу логів
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/app_log.txt";

    // Формат часу для записів
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Метод для звичайних повідомлень (Інфо) -> Пише у ФАЙЛ + КОНСОЛЬ
    public static void info(String message) {
        log("INFO", message);
    }

    // Метод для помилок -> Пише у ФАЙЛ + КОНСОЛЬ + ВІДПРАВЛЯЄ ЛИСТ
    public static void error(String message) {
        log("ERROR", message);
        // Відправляємо лист через ваш сервіс пошти
        EmailService.sendAsync("CRITICAL ERROR", message);
    }

    // Внутрішній метод, який фізично записує дані у файл
    private static synchronized void log(String level, String message) {
        String timestamp = dtf.format(LocalDateTime.now());
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);

        // 1. Вивід у консоль (щоб ви бачили очима)
        System.out.println(formattedMessage);

        // 2. Запис у файл
        try {
            // Перевіряємо, чи існує папка logs, якщо ні - створюємо
            File directory = new File(LOG_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Відкриваємо файл у режимі "append" (додавання в кінець), а не перезапису
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8, true))) {
                writer.write(formattedMessage);
                writer.newLine(); // Перехід на новий рядок
            }
        } catch (IOException e) {
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }
}