package repository;

import model.equipment.Ammunition;
import model.equipment.Sword;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentRepositoryTest {

    private DatabaseManager dbManager;
    private String testDbUrl;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        File tempDb = tempDir.resolve("test_ammo.db").toFile();
        testDbUrl = "jdbc:sqlite:" + tempDb.getAbsolutePath();
        
        DatabaseManager.resetInstance();
        dbManager = DatabaseManager.getInstance(testDbUrl);
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.resetInstance();
    }

    @Test
    void testLoadFromDatabase(@TempDir Path tempDir) throws Exception {
        // Підготовка: створюємо тимчасовий файл з амуніцією
        File tempFile = tempDir.resolve("full_ammo.txt").toFile();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, King Sword, 12.5, 1000.0, 50\n");
            writer.write("Helmet, Iron Helm, 5.0, 200.0, 20\n");
        }

        // Створюємо репозиторій, що імпортує файл у БД
        EquipmentRepository repository = new EquipmentRepository(tempFile.getAbsolutePath(), dbManager);
        List<Ammunition> items = repository.getAll();

        // Перевіряємо, чи завантажилось рівно 2 об'єкти
        assertEquals(2, items.size());

        // Детальна перевірка полів першого об'єкта
        Ammunition sword = items.get(0);
        assertEquals("King Sword", sword.getName());
        assertEquals(12.5, sword.getWeight());
        assertEquals(1000.0, sword.getPrice());

        assertTrue(sword instanceof Sword);
    }

    @Test
    void testReloadFromDatabase(@TempDir Path tempDir) throws Exception {
        File tempFile = tempDir.resolve("ammo.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, Initial Sword, 10, 100, 10\n");
        }

        EquipmentRepository repository = new EquipmentRepository(tempFile.getAbsolutePath(), dbManager);
        assertEquals(1, repository.getAll().size());
        
        // Додаємо запис безпосередньо в БД
        try (java.sql.Connection conn = dbManager.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, "Sword");
            ps.setString(2, "DB Sword");
            ps.setDouble(3, 10);
            ps.setDouble(4, 100);
            ps.setInt(5, 10);
            ps.executeUpdate();
        }

        // Крок 3: Викликаємо метод оновлення
        repository.reload();

        List<Ammunition> items = repository.getAll();
        assertEquals(2, items.size(), "Метод reload має перечитати БД заново");
        assertEquals("DB Sword", items.get(1).getName());
    }
}