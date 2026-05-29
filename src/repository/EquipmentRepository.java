package repository;

import model.equipment.*;
// --- ІМПОРТИ ЛОГЕРА ---
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторій для роботи з каталогом амуніції через SQLite базу даних.
 * При першому запуску імпортує дані з текстового файлу в базу.
 */
public class EquipmentRepository {
    private static final Logger logger = LogManager.getLogger(EquipmentRepository.class);

    private List<Ammunition> catalog = new ArrayList<>();
    private DatabaseManager dbManager;
    private String ammunitionFilePath;

    /**
     * Конструктор за замовчуванням: використовує стандартний файл та БД.
     */
    public EquipmentRepository() {
        this("src/ammunition.txt");
    }

    /**
     * Конструктор з вказівкою шляху до файлу амуніції.
     * Імпортує дані з файлу в SQLite (якщо таблиця порожня), потім зчитує з БД.
     */
    public EquipmentRepository(String ammunitionFilePath) {
        this.ammunitionFilePath = ammunitionFilePath;
        this.dbManager = DatabaseManager.getInstance();

        // Імпортуємо з файлу в БД (тільки якщо таблиця порожня)
        dbManager.importAmmunitionFromFile(ammunitionFilePath);

        // Завантажуємо з БД у пам'ять
        loadFromDatabase();
    }

    /**
     * Конструктор для тестів: дозволяє передати DatabaseManager.
     */
    public EquipmentRepository(String ammunitionFilePath, DatabaseManager dbManager) {
        this.ammunitionFilePath = ammunitionFilePath;
        this.dbManager = dbManager;
        dbManager.importAmmunitionFromFile(ammunitionFilePath);
        loadFromDatabase();
    }

    /**
     * Перезавантажує каталог з бази даних.
     */
    public void reload() {
        logger.info("Reloading ammunition catalog from database...");
        catalog.clear();
        loadFromDatabase();
    }

    /**
     * Завантажує каталог з таблиці equipment_catalog у пам'ять.
     */
    private void loadFromDatabase() {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equipment_catalog")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double price = rs.getDouble("price");
                int statValue = rs.getInt("stat_value");

                Ammunition item = createAmmunition(type, name, weight, price, statValue);
                if (item != null) {
                    item.setCatalogId(id);
                    catalog.add(item);
                }
            }

            logger.info("Catalog loaded: " + catalog.size() + " items.");

        } catch (SQLException e) {
            logger.error("CRITICAL ERROR: Failed to load equipment catalog from database!", e);
        }
    }

    /**
     * Створює об'єкт Ammunition відповідного типу.
     */
    private Ammunition createAmmunition(String type, String name, double weight, double price, int statValue) {
        switch (type) {
            case "Helmet": return new Helmet(name, weight, price, statValue);
            case "Breastplate": return new Breastplate(name, weight, price, statValue);
            case "Greaves": return new Greaves(name, weight, price, statValue);
            case "Sword": return new Sword(name, weight, price, statValue);
            case "Axe": return new Axe(name, weight, price, statValue);
            case "Bow": return new Bow(name, weight, price, statValue);
            case "Knife": return new Knife(name, weight, price, statValue);
            case "Shield": return new Breastplate(name, weight, price, statValue);
            case "TwoHandedSword": return new TwoHandedSword(name, weight, price, statValue);
            case "Mace": return new Mace(name, weight, price, statValue);
            case "Spear": return new Spear(name, weight, price, statValue);
            default:
                logger.warn("Unknown item type in database: " + type);
                return null;
        }
    }

    /**
     * Повертає весь каталог амуніції.
     */
    public List<Ammunition> getAll() {
        return catalog;
    }
}