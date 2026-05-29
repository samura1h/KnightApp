package repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private String testDbUrl;
    private DatabaseManager dbManager;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Використовуємо тимчасовий файл для бази даних, щоб тести були незалежними
        File tempDb = tempDir.resolve("test_app.db").toFile();
        testDbUrl = "jdbc:sqlite:" + tempDb.getAbsolutePath();
        
        // Скидаємо інстанс перед кожним тестом, щоб гарантувати ізольованість
        DatabaseManager.resetInstance();
        dbManager = DatabaseManager.getInstance(testDbUrl);
    }

    @AfterEach
    void tearDown() {
        DatabaseManager.resetInstance();
    }

    @Test
    void testConnectionAndTablesCreation() throws Exception {
        assertNotNull(dbManager, "DatabaseManager should be instantiated");
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Перевіряємо, чи створені таблиці
            ResultSet rsKnights = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='knights'");
            assertTrue(rsKnights.next(), "Table 'knights' should exist");
            
            ResultSet rsEquip = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='equipment_catalog'");
            assertTrue(rsEquip.next(), "Table 'equipment_catalog' should exist");
            
            ResultSet rsKnightEquip = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='knight_equipment'");
            assertTrue(rsKnightEquip.next(), "Table 'knight_equipment' should exist");
        }
    }

    @Test
    void testImportAmmunitionFromFile(@TempDir Path tempDir) throws Exception {
        // Створюємо тестовий файл з даними
        File tempAmmoFile = tempDir.resolve("test_ammo.txt").toFile();
        try (FileWriter writer = new FileWriter(tempAmmoFile)) {
            writer.write("Sword, Test Sword, 10.5, 100.0, 15\n");
            writer.write("Helmet, Test Helmet, 2.0, 50.0, 5\n");
            // Цей рядок повинен бути проігнорований (некоректні дані)
            writer.write("Invalid, Data\n");
        }

        // Викликаємо імпорт
        dbManager.importAmmunitionFromFile(tempAmmoFile.getAbsolutePath());

        // Перевіряємо, що дані імпортувалися в базу
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment_catalog")) {
            
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1), "Should import exactly 2 valid items");
        }
    }
}
