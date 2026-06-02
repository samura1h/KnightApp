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
        
        File tempFile = tempDir.resolve("full_ammo.txt").toFile();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, King Sword, 12.5, 1000.0, 50\n");
            writer.write("Helmet, Iron Helm, 5.0, 200.0, 20\n");
            writer.write("Breastplate, Iron Armor, 15.0, 500.0, 40\n");
            writer.write("Greaves, Iron Boots, 4.0, 150.0, 15\n");
            writer.write("Axe, Battle Axe, 10.0, 300.0, 40\n");
            writer.write("Bow, Longbow, 2.0, 150.0, 25\n");
            writer.write("Knife, Dagger, 1.0, 50.0, 10\n");
            writer.write("Shield, Wooden Shield, 5.0, 100.0, 15\n");
            writer.write("TwoHandedSword, Claymore, 18.0, 1200.0, 60\n");
            writer.write("Mace, Morning Star, 8.0, 250.0, 35\n");
            writer.write("Spear, Long Spear, 6.0, 200.0, 30\n");
            writer.write("UnknownType, Glitch, 0, 0, 0\n");
        }

        EquipmentRepository repository = new EquipmentRepository(tempFile.getAbsolutePath(), dbManager);
        List<Ammunition> items = repository.getAll();

        assertEquals(11, items.size());

        Ammunition sword = items.stream().filter(i -> i.getName().equals("King Sword")).findFirst().get();
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

        try (java.sql.Connection conn = dbManager.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, "Sword");
            ps.setString(2, "DB Sword");
            ps.setDouble(3, 10);
            ps.setDouble(4, 100);
            ps.setInt(5, 10);
            ps.executeUpdate();
        }

        repository.reload();

        List<Ammunition> items = repository.getAll();
        assertEquals(2, items.size(), "Метод reload має перечитати БД заново");
        assertEquals("DB Sword", items.get(1).getName());
    }
}