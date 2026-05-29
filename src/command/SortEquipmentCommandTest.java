package command;

import model.Knight;
import model.Rank;
import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест команди сортування (SortEquipmentCommand).
 * Особливість: Команда нічого не повертає, а лише друкує результат у консоль.
 * Тому ми перехоплюємо System.out, щоб перевірити правильність сортування.
 */
class SortEquipmentCommandTest {

    // Об'єкт для зберігання всього, що програма "надрукує" під час тесту
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Зберігаємо посилання на оригінальний консольний вивід, щоб відновити його після тесту
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        // Перед кожним тестом підміняємо стандартний вивід на наш потік (outContent)
        // Використовуємо UTF-8, щоб коректно обробляти спецсимволи (якщо є)
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    @AfterEach
    void restoreStreams() {
        // Після тесту обов'язково повертаємо все як було, інакше ми не побачимо помилок у консолі
        System.setOut(originalOut);
    }

    // --- Stub Classes (Заглушки) ---

    // Спеціальні класи для тестування сортування.
    // Нам не важливі реальні мечі/шоломи, нам важлива лише ВАГА (weight).

    static class TestHeavyItem extends Ammunition {
        public TestHeavyItem(String name, double val) {
            super(name, val, val); // В конструкторі Ammunition другий параметр - це вага
        }
        @Override
        public String toString() { return getName() + " (" + getWeight() + "kg)"; }
    }

    static class TestLightItem extends Ammunition {
        public TestLightItem(String name, double val) {
            super(name, val, val);
        }
        @Override
        public String toString() { return getName() + " (" + getWeight() + "kg)"; }
    }

    // Заглушка менеджера: дозволяє встановити активного лицаря без складної логіки
    static class StubManager extends KnightManager {
        private Knight activeKnight;
        public StubManager() { super(null, null); } // Null замість репозиторіїв

        public void setActiveKnight(Knight k) { this.activeKnight = k; }

        @Override
        public Knight getActiveKnight() { return activeKnight; }
    }

    // Заглушка репозиторію: замість файлу використовує звичайний List у пам'яті
    static class StubRepo extends EquipmentRepository {
        private List<Ammunition> items = new ArrayList<>();
        public void setItems(List<Ammunition> items) { this.items = items; }

        @Override
        public List<Ammunition> getAll() { return items; }
    }

    // Допоміжний метод для імітації введення користувача
    private Scanner mockScanner(String input) {
        String fullInput = input + System.lineSeparator();
        return new Scanner(new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    @Test
    void testSortRepository() throws UnsupportedEncodingException {
        // 1. Налаштування (Arrange)
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();

        List<Ammunition> items = new ArrayList<>();
        // Додаємо предмети в хаотичному порядку ваги: 20 -> 1 -> 10
        items.add(new TestHeavyItem("Heavy", 20.0));
        items.add(new TestLightItem("Light", 1.0));
        items.add(new TestHeavyItem("Medium", 10.0));
        repo.setItems(items);

        // Імітуємо вибір пункту "2" (Сортувати амуніцію в магазині/репозиторії)
        Scanner scanner = mockScanner("2");

        Command command = new SortEquipmentCommand(manager, repo, scanner);

        // 2. Дія (Act)
        command.execute();

        // 3. Перевірка (Assert)
        // Отримуємо весь текст, що був надрукований у консоль
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Знаходимо позицію (індекс) кожного слова у надрукованому тексті
        int idxLight = output.indexOf("Light");   // має бути першим (вага 1.0)
        int idxMedium = output.indexOf("Medium"); // має бути другим (вага 10.0)
        int idxHeavy = output.indexOf("Heavy");   // має бути третім (вага 20.0)

        // Переконуємось, що слова взагалі знайшлися
        assertTrue(idxLight != -1, "Має знайти Light");
        assertTrue(idxMedium != -1, "Має знайти Medium");
        assertTrue(idxHeavy != -1, "Має знайти Heavy");

        // Головна перевірка сортування: індекс легшого предмета має бути меншим за індекс важчого
        // Це означає, що він був надрукований раніше.
        assertTrue(idxLight < idxMedium, "Light (1.0) має бути перед Medium (10.0)");
        assertTrue(idxMedium < idxHeavy, "Medium (10.0) має бути перед Heavy (20.0)");
    }

    @Test
    void testSortKnightInventory() throws UnsupportedEncodingException {
        // 1. Налаштування
        StubManager manager = new StubManager();
        Knight k = new Knight("Sir Sort", "Test", Rank.MASTER);

        // Екіпіруємо лицаря: спочатку важке, потім легке (несортований порядок)
        k.equip(new TestHeavyItem("HeavySword", 10.0));
        k.equip(new TestLightItem("LightDagger", 1.0));

        manager.setActiveKnight(k);
        StubRepo repo = new StubRepo(); // Пустий репозиторій, він тут не потрібен

        // Імітуємо вибір "1" (Сортувати інвентар поточного лицаря)
        Scanner scanner = mockScanner("1");

        Command command = new SortEquipmentCommand(manager, repo, scanner);

        // 2. Дія
        command.execute();

        // 3. Перевірка
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        int idxLight = output.indexOf("LightDagger"); // Вага 1.0
        int idxHeavy = output.indexOf("HeavySword");  // Вага 10.0

        // Логування для налагодження, якщо тест впаде
        if (idxLight == -1 || idxHeavy == -1) {
            System.err.println("DEBUG OUTPUT: " + output);
        }

        assertTrue(idxLight != -1, "LightDagger має бути в списку");
        assertTrue(idxHeavy != -1, "HeavySword має бути в списку");

        // Перевіряємо, що легкий предмет вивівся раніше за важкий
        assertTrue(idxLight < idxHeavy, "Легкий кинджал має бути вище важкого меча у списку");
    }
}