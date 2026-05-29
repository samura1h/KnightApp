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

class FindEquipmentByPriceCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    // --- ЗАГЛУШКИ (Stubs) ---

    // СТВОРЮЄМО ДВА РІЗНИХ КЛАСИ, ЩОБ ЛИЦАР НЕ СВАРИВСЯ НА ДУБЛІКАТИ
    static class TestSword extends Ammunition {
        public TestSword(String name, double val) {
            super(name, val, val); // Ціна = Вага = val
        }
        @Override
        public String toString() { return getName() + " (" + getPrice() + ")"; }
    }

    static class TestShield extends Ammunition {
        public TestShield(String name, double val) {
            super(name, val, val); // Ціна = Вага = val
        }
        @Override
        public String toString() { return getName() + " (" + getPrice() + ")"; }
    }

    static class StubRepo extends EquipmentRepository {
        private List<Ammunition> items = new ArrayList<>();
        public void setItems(List<Ammunition> items) { this.items = items; }
        @Override
        public List<Ammunition> getAll() { return items; }
    }

    static class StubManager extends KnightManager {
        private Knight activeKnight;
        public StubManager() { super(null, null); }
        public void setActiveKnight(Knight k) { this.activeKnight = k; }
        @Override
        public Knight getActiveKnight() { return activeKnight; }
    }

    private Scanner mockScanner(String input) {
        String fullInput = input + System.lineSeparator();
        return new Scanner(new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    // --- ТЕСТИ ---

    @Test
    void testSearchInKnight_Success() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        Knight k = new Knight("Sir Lancelot", "Round Table", Rank.MASTER);

        // 1. Різні класи (Sword та Shield) -> Лицар візьме обидва.
        // 2. Малі значення (2.0 та 1.0) -> Лицар їх підніме.
        // 3. Ціна = Вага -> Конструктор не переплутає.

        k.equip(new TestSword("Excalibur", 2.0)); // Ціна 2.0
        k.equip(new TestShield("Aegis", 1.0));    // Ціна 1.0

        manager.setActiveKnight(k);

        StubRepo repo = new StubRepo();

        // Шукаємо ціну від 0.5 до 1.5.
        // Має знайти Shield (1.0).
        // Меч (2.0) задорогий.
        String input = "1\n0.5\n1.5";
        Scanner scanner = mockScanner(input);

        Command command = new FindEquipmentByPriceCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        if (!output.contains("Aegis")) {
            System.err.println("DEBUG OUTPUT: " + output);
        }

        assertTrue(output.contains("Aegis"), "Має знайти Aegis (ціна 1.0)");
        assertFalse(output.contains("Excalibur"), "НЕ має знайти Excalibur (ціна 2.0)");
        assertTrue(output.contains("Знайдені предмети"), "Має бути заголовок результату");
    }

    @Test
    void testSearchInRepository_Success() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();

        List<Ammunition> shopItems = new ArrayList<>();
        // Тут типи не важливі (репозиторій не перевіряє дублікати класів),
        // але використовуємо наші класи для зручності.
        shopItems.add(new TestSword("CheapSword", 10.0));
        shopItems.add(new TestSword("ExpensiveSword", 100.0));
        repo.setItems(shopItems);

        // Шукаємо від 5 до 50. Має знайти CheapSword (10.0).
        String input = "2\n5\n50";
        Scanner scanner = mockScanner(input);

        Command command = new FindEquipmentByPriceCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("CheapSword"), "Має знайти CheapSword (ціна 10)");
        assertFalse(output.contains("ExpensiveSword"), "НЕ має знайти ExpensiveSword (ціна 100)");
    }

    @Test
    void testNoActiveKnight_Error() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();

        String input = "1";
        Scanner scanner = mockScanner(input);

        Command command = new FindEquipmentByPriceCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("ПОМИЛКА") || output.contains("не обраний"),
                "Має повідомити, що лицар не обраний");
    }

    @Test
    void testNothingFound() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();
        List<Ammunition> shopItems = new ArrayList<>();

        shopItems.add(new TestSword("ExpensiveThing", 100.0));
        repo.setItems(shopItems);

        // Шукаємо від 0 до 10.
        String input = "2\n0\n10";
        Scanner scanner = mockScanner(input);

        Command command = new FindEquipmentByPriceCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("Нічого не знайдено"), "Має повідомити, що нічого не знайдено");
    }

    @Test
    void testInvalidInputFormat() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();
        repo.setItems(new ArrayList<>());

        String input = "2\nabc";
        Scanner scanner = mockScanner(input);

        Command command = new FindEquipmentByPriceCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("Помилка: введіть число"), "Має спіймати NumberFormatException");
    }
}