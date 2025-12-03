package repository;

import model.Knight;
import model.Rank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class KnightRepositoryTest {

    @Test
    void testSaveAndLoad(@TempDir Path tempDir) {
        // Файл для збереження
        String filePath = tempDir.resolve("knights_test.dat").toString();

        KnightRepository repo = new KnightRepository(filePath);

        // Створюємо лицаря (припускаємо ID = 1)
        // ВАЖЛИВО: Клас Knight має імплементувати Serializable!
        Knight k = new Knight("Arthur", "British", Rank.MASTER);

        // Зберігаємо в пам'ять
        repo.save(k);
        // Зберігаємо на диск
        repo.saveData();

        // Створюємо НОВИЙ репозиторій з тим самим файлом (імітуємо перезапуск програми)
        KnightRepository newRepo = new KnightRepository(filePath);
        newRepo.loadData();

        Knight loadedKnight = newRepo.findById(1);

        assertNotNull(loadedKnight, "Лицар має завантажитись");
        assertEquals("Arthur", loadedKnight.getName());
    }

    @Test
    void testSaveWithoutLoadFile() {
        // Перевіряємо, що програма не падає, якщо файлу немає
        KnightRepository repo = new KnightRepository("non_existent_file.txt");
        repo.loadData(); // Має вивести помилку в консоль, але не впасти
        assertTrue(repo.findAll().isEmpty());
    }
}