package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * Singleton клас для управління SQLite з'єднанням.
 * Відповідає за ініціалізацію бази даних, створення таблиць та імпорт початкових даних.
 */
public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    // Шлях до файлу бази даних SQLite
    private static final String DEFAULT_DB_URL = "jdbc:sqlite:knight_app.db";

    private static DatabaseManager instance;
    private String dbUrl;

    /**
     * Приватний конструктор (Singleton).
     */
    private DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
        // Явно завантажуємо SQLite JDBC драйвер
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found!", e);
        }
        initializeDatabase();
    }

    /**
     * Отримує єдиний екземпляр DatabaseManager з дефолтним URL.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(DEFAULT_DB_URL);
        }
        return instance;
    }

    /**
     * Створює екземпляр з користувацьким URL (для тестів).
     */
    public static synchronized DatabaseManager getInstance(String dbUrl) {
        // Якщо існуючий інстанс має інший URL — створити новий
        if (instance == null || !instance.dbUrl.equals(dbUrl)) {
            instance = new DatabaseManager(dbUrl);
        }
        return instance;
    }

    /**
     * Скидає інстанс (для тестів).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    /**
     * Повертає з'єднання з базою даних.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Ініціалізує базу даних: створює таблиці, якщо вони не існують.
     */
    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Таблиця лицарів
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS knights (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT NOT NULL," +
                "  orden TEXT NOT NULL," +
                "  rank TEXT NOT NULL," +
                "  strength INTEGER DEFAULT 60," +
                "  base_defense INTEGER DEFAULT 20" +
                ")"
            );

            // Таблиця каталогу амуніції
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS equipment_catalog (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  type TEXT NOT NULL," +
                "  name TEXT NOT NULL," +
                "  weight REAL NOT NULL," +
                "  price REAL NOT NULL," +
                "  stat_value INTEGER NOT NULL" +
                ")"
            );

            // Таблиця зв'язку: яке спорядження екіпіровано на якого лицаря
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS knight_equipment (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  knight_id INTEGER NOT NULL," +
                "  catalog_id INTEGER NOT NULL," +
                "  FOREIGN KEY (knight_id) REFERENCES knights(id) ON DELETE CASCADE," +
                "  FOREIGN KEY (catalog_id) REFERENCES equipment_catalog(id)" +
                ")"
            );

            logger.info("Database initialized successfully.");

        } catch (SQLException e) {
            logger.error("CRITICAL ERROR: Failed to initialize database!", e);
        }
    }

    /**
     * Імпортує амуніцію з текстового файлу у таблицю equipment_catalog,
     * якщо таблиця порожня (одноразовий імпорт).
     *
     * @param filePath Шлях до файлу ammunition.txt
     */
    public void importAmmunitionFromFile(String filePath) {
        try (Connection conn = getConnection()) {

            // Перевіряємо, чи каталог вже заповнений
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment_catalog")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    logger.info("Equipment catalog already populated (" + rs.getInt(1) + " items). Skipping import.");
                    return;
                }
            }

            // Зчитуємо файл і імпортуємо
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("Ammunition file not found: " + filePath + ". Skipping import.");
                return;
            }

            conn.setAutoCommit(false); // Використовуємо транзакцію для швидкості

            String insertSql = "INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)";
            int count = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {

                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(",");
                    if (parts.length != 5) {
                        logger.warn("Skipped corrupted line during import: " + line);
                        continue;
                    }

                    try {
                        ps.setString(1, parts[0].trim());
                        ps.setString(2, parts[1].trim());
                        ps.setDouble(3, Double.parseDouble(parts[2].trim()));
                        ps.setDouble(4, Double.parseDouble(parts[3].trim()));
                        ps.setInt(5, Integer.parseInt(parts[4].trim()));
                        ps.addBatch();
                        count++;
                    } catch (NumberFormatException e) {
                        logger.warn("Skipped line with invalid numbers: " + line);
                    }
                }

                ps.executeBatch();
                conn.commit();
                logger.info("Imported " + count + " items from " + filePath + " into database.");

            } catch (IOException e) {
                conn.rollback();
                logger.error("CRITICAL ERROR: Failed to read ammunition file for import: " + filePath, e);
            }

        } catch (SQLException e) {
            logger.error("CRITICAL ERROR: Failed to import ammunition data!", e);
        }
    }

    /**
     * Повертає URL бази даних.
     */
    public String getDbUrl() {
        return dbUrl;
    }
}
