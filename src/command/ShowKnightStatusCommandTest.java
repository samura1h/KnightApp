package command;

import model.Knight;
import model.Rank;
import model.equipment.Ammunition;
import service.KnightManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест команди відображення статусу лицаря (ShowKnightStatusCommand).
 * Перевіряє, чи правильно формується текстовий звіт про героя та його інвентар.
 */
class ShowKnightStatusCommandTest {

    // Буфер для перехоплення тексту, що виводиться в консоль
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Збереження оригінального потоку виводу, щоб відновити його після тестів
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        // Перенаправляємо System.out у наш буфер (outContent)
        // Використовуємо UTF-8 для коректної обробки кирилиці
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    @AfterEach
    void restoreStreams() {
        // Відновлюємо стандартний вивід в консоль IDE
        System.setOut(originalOut);
    }

    // --- Stub Classes (Заглушки) ---

    // Проста реалізація амуніції для тестів, щоб не залежати від реальних мечів/броні
    static class TestAmmo extends Ammunition {
        public TestAmmo(String name) { super(name, 10, 10); } // Вага 10, Ціна 10

        @Override
        public String toString() { return "TestItem: " + getName(); }
    }

    // Спрощений менеджер, який дозволяє вручну встановити активного лицаря
    static class StubManager extends KnightManager {
        private Knight activeKnight;

        // Передаємо null у батьківський конструктор, бо репозиторії тут не потрібні
        public StubManager() { super(null, null); }

        public void setActiveKnight(Knight k) { this.activeKnight = k; }

        @Override
        public Knight getActiveKnight() { return activeKnight; }
    }

    @Test
    void testExecute_NoActiveKnight() throws UnsupportedEncodingException {
        // Сценарій: Користувач натиснув "Показати статус", але ще не створив/не вибрав лицаря
        StubManager manager = new StubManager(); // за замовчуванням activeKnight == null

        Command command = new ShowKnightStatusCommand(manager);
        command.execute();

        // Отримуємо текст з консолі
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, чи є повідомлення про помилку
        assertTrue(output.contains("ПОМИЛКА") || output.contains("не обрано"),
                "Має бути повідомлення про помилку, якщо лицар не обраний");
    }

    @Test
    void testExecute_WithKnight_EmptyInventory() throws UnsupportedEncodingException {
        // Сценарій: Лицар створений, але "голий" (інвентар порожній)
        StubManager manager = new StubManager();
        Knight k = new Knight("Arthur", "Camelot", Rank.MASTER);
        manager.setActiveKnight(k);

        Command command = new ShowKnightStatusCommand(manager);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо наявність ключових елементів звіту
        assertTrue(output.contains("СТАТУС ЛИЦАРЯ"), "Має бути заголовок звіту");
        assertTrue(output.contains("Arthur"), "Має вивести ім'я лицаря");
        assertTrue(output.contains("Camelot"), "Має вивести орден");
        // Перевіряємо, чи відображається, що речей немає
        assertTrue(output.contains("(Порожньо)") || output.contains("Inventory is empty"),
                "Має написати, що інвентар порожній");
    }

    @Test
    void testExecute_WithKnight_WithItems() throws UnsupportedEncodingException {
        // Сценарій: Лицар має спорядження
        StubManager manager = new StubManager();
        Knight k = new Knight("Arthur", "Camelot", Rank.MASTER);

        // Додаємо тестовий предмет
        k.equip(new TestAmmo("Excalibur"));
        manager.setActiveKnight(k);

        Command command = new ShowKnightStatusCommand(manager);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, чи список спорядження відображається
        assertTrue(output.contains("TestItem: Excalibur"), "Має вивести назву предмета в списку інвентарю");
    }
}