package repository;

import model.equipment.Ammunition;
import model.equipment.Sword;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentRepositoryTest {

    @Test
    void testLoadFromFile_Success(@TempDir Path tempDir) throws IOException {
        // 1. Створюємо тимчасовий файл
        File tempFile = tempDir.resolve("test_ammo.txt").toFile();

        // 2. Записуємо туди тестові дані (CSV)
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, King Sword, 12.5, 1000.0, 50\n"); // Правильний рядок
            writer.write("Helmet, Iron Helm, 5.0, 200.0, 20\n");  // Правильний рядок
            writer.write("InvalidType, Name, 1.0, 1.0, 1\n");     // Невідомий тип
            writer.write("BrokenLine, 1.0\n");                    // Неповний рядок
            writer.write("\n");                                   // Порожній рядок
        }

        // 3. Ініціалізуємо репозиторій з нашим файлом
        EquipmentRepository repository = new EquipmentRepository(tempFile.getAbsolutePath());

        // 4. Перевіряємо
        List<Ammunition> items = repository.getAll();

        assertEquals(2, items.size(), "Має завантажитись рівно 2 коректних предмета");

        // Перевіряємо перший предмет (Sword)
        assertTrue(items.get(0) instanceof Sword);
        assertEquals("King Sword", items.get(0).getName());
        assertEquals(12.5, items.get(0).getWeight());

        // Перевіряємо другий предмет (Helmet - перевіримо, чи це не Sword)
        assertFalse(items.get(1) instanceof Sword);
        assertEquals("Iron Helm", items.get(1).getName());
    }

    @Test
    void testReload(@TempDir Path tempDir) throws IOException {
        File tempFile = tempDir.resolve("reload_test.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, A, 1, 1, 1\n");
        }

        EquipmentRepository repository = new EquipmentRepository(tempFile.getAbsolutePath());
        assertEquals(1, repository.getAll().size());

        // Переписуємо файл
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Sword, A, 1, 1, 1\n");
            writer.write("Sword, B, 1, 1, 1\n");
        }

        // Викликаємо reload
        repository.reload();

        assertEquals(2, repository.getAll().size(), "Після reload має бути 2 предмети");
    }
}