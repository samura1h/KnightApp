package command;

import model.Knight;
import model.Rank;
import service.KnightManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class DeleteKnightCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    // --- Налаштування перехоплення консолі (UTF-8) ---
    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    // --- Stub (Заглушка) для KnightManager ---
    static class StubManager extends KnightManager {
        public Map<Integer, Knight> knightsMap = new HashMap<>();
        public int removedId = -1; // Запам'ятовуємо, який ID намагалися видалити

        // Конструктор для обходу помилки dependencies
        public StubManager() {
            super(null, null);
        }

        @Override
        public Map<Integer, Knight> getAllKnights() {
            return knightsMap;
        }

        @Override
        public void removeKnight(int id) {
            this.removedId = id; // Фіксуємо виклик видалення
            knightsMap.remove(id); // Імітуємо видалення
        }
    }

    // --- Helper для Scanner ---
    private Scanner mockScanner(String input) {
        String fullInput = input + System.lineSeparator();
        return new Scanner(new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    @Test
    void testExecute_EmptyList() throws UnsupportedEncodingException {
        // Сценарій: Список пустий
        StubManager manager = new StubManager();
        Scanner scanner = mockScanner(""); // Ввід не важливий

        Command command = new DeleteKnightCommand(manager, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, що метод видалення НЕ викликався
        assertEquals(-1, manager.removedId);
        // Перевіряємо повідомлення
        assertTrue(output.contains("Список лицарів порожній"));
    }

    @Test
    void testExecute_SuccessDelete() throws UnsupportedEncodingException {
        // Сценарій: Є лицар з ID 5, вводимо "5"
        StubManager manager = new StubManager();
        // Додаємо лицаря вручну в мапу (без ID генератора, просто як ключ 5)
        Knight k = new Knight("Arthur", "Camelot", Rank.MASTER);
        // *Примітка: в реальному коді ID задається всередині Knight,
        // але тут ми імітуємо мапу, тому ключ 5 важливіший
        manager.knightsMap.put(5, k);

        Scanner scanner = mockScanner("5");

        Command command = new DeleteKnightCommand(manager, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, що викликався removeKnight(5)
        assertEquals(5, manager.removedId, "Має бути викликаний метод removeKnight з ID 5");
        assertTrue(output.contains("Лицаря видалено"), "Має бути повідомлення про успіх");
    }

    @Test
    void testExecute_IdNotFound() throws UnsupportedEncodingException {
        // Сценарій: Є ID 5, а вводимо "99"
        StubManager manager = new StubManager();
        manager.knightsMap.put(5, new Knight("Lancelot", "Table", Rank.VETERAN));

        Scanner scanner = mockScanner("99");

        Command command = new DeleteKnightCommand(manager, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Метод видалення НЕ мав викликатись
        assertEquals(-1, manager.removedId);
        assertTrue(output.contains("не знайдено"), "Має написати, що ID не знайдено");
    }

    @Test
    void testExecute_InvalidInput_NotANumber() throws UnsupportedEncodingException {
        // Сценарій: Вводимо "abc" замість числа
        StubManager manager = new StubManager();
        manager.knightsMap.put(1, new Knight("Test", "Test", Rank.NOVICE));

        Scanner scanner = mockScanner("abc");

        Command command = new DeleteKnightCommand(manager, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Видалення не відбулось
        assertEquals(-1, manager.removedId);
        // Перехоплення помилки
        assertTrue(output.contains("введіть число"), "Має спрацювати catch блок");
    }
}