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
        
        File tempDb = tempDir.resolve("test_app.db").toFile();
        testDbUrl = "jdbc:sqlite:" + tempDb.getAbsolutePath();

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
        
        File tempAmmoFile = tempDir.resolve("test_ammo.txt").toFile();
        try (FileWriter writer = new FileWriter(tempAmmoFile)) {
            writer.write("Sword, Test Sword, 10.5, 100.0, 15\n");
            writer.write("Helmet, Test Helmet, 2.0, 50.0, 5\n");
            writer.write("\n"); // Empty line
            writer.write("   \n"); // Blank line
            writer.write("Invalid, Data\n"); // Wrong columns
            writer.write("Axe, Bad Axe, 1.0, bad, 10\n"); // Bad price format
        }

        dbManager.importAmmunitionFromFile(tempAmmoFile.getAbsolutePath());

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment_catalog")) {
            
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1), "Should import exactly 2 valid items");
        }
    }

    @Test
    void testImportFallback() throws Exception {
        
        dbManager.importAmmunitionFromFile("non_existent_file_xyz.txt");
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment_catalog")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) > 0, "Fallback should import default items");
        }
    }

    @Test
    void testSqlExceptionHandling() {
        
        DatabaseManager badManager = DatabaseManager.getInstance("invalid_url_no_jdbc");
        assertNotNull(badManager);

        assertDoesNotThrow(() -> {
            badManager.importAmmunitionFromFile("test.txt");
        });
    }

    @Test
    void testImportIOException(@TempDir Path tempDir) throws Exception {

        File dirAsFile = tempDir.resolve("fake_file.txt").toFile();
        dirAsFile.mkdirs();

        assertDoesNotThrow(() -> {
            dbManager.importAmmunitionFromFile(dirAsFile.getAbsolutePath());
        });
    }

    @Test
    void testGetInstanceWithDifferentUrls(@TempDir Path tempDir) {
        File dbFile1 = tempDir.resolve("db1.db").toFile();
        File dbFile2 = tempDir.resolve("db2.db").toFile();
        String url1 = "jdbc:sqlite:" + dbFile1.getAbsolutePath();
        String url2 = "jdbc:sqlite:" + dbFile2.getAbsolutePath();

        DatabaseManager.resetInstance();
        DatabaseManager instance1 = DatabaseManager.getInstance(url1);
        DatabaseManager instance2 = DatabaseManager.getInstance(url1);
        assertSame(instance1, instance2, "Should return same instance for same URL");

        DatabaseManager instance3 = DatabaseManager.getInstance(url2);
        assertNotSame(instance1, instance3, "Should return new instance for different URL");
    }
}
