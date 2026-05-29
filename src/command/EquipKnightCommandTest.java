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

class EquipKnightCommandTest {

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

    // --- Stub Classes (Заглушки) ---

    static class TestSword extends Ammunition {
        public TestSword(String name, double val) { super(name, val, val); }
    }
    static class TestHelmet extends Ammunition {
        public TestHelmet(String name, double val) { super(name, val, val); }
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

    @Test
    void testExecute_NoActiveKnight() throws UnsupportedEncodingException {
        // Сценарій: Лицар == null
        StubManager manager = new StubManager();
        StubRepo repo = new StubRepo();
        Scanner scanner = mockScanner("1");

        Command command = new EquipKnightCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо наявність слова ПОМИЛКА
        assertTrue(output.contains("ПОМИЛКА"), "Має бути помилка");

        // ВИПРАВЛЕННЯ ТУТ: "Спочатку" з великої літери, як у вашому коді
        assertTrue(output.contains("Спочатку оберіть"), "Має підказати обрати лицаря");
    }

    @Test
    void testExecute_SuccessEquip() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        Knight k = new Knight("Arthur", "King", Rank.MASTER);
        manager.setActiveKnight(k);

        StubRepo repo = new StubRepo();
        List<Ammunition> items = new ArrayList<>();
        items.add(new TestSword("Excalibur", 10.0));
        repo.setItems(items);

        Scanner scanner = mockScanner("1");

        Command command = new EquipKnightCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("Успішно"), "Має написати Успішно");
        assertTrue(output.contains("Excalibur"), "Має вивести назву предмету");
        assertEquals(1, k.getEquipment().size(), "В інвентарі має бути 1 предмет");
    }

    @Test
    void testExecute_FailEquip_DuplicateType() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        Knight k = new Knight("Arthur", "King", Rank.MASTER);
        k.equip(new TestSword("Old Sword", 5.0));
        manager.setActiveKnight(k);

        StubRepo repo = new StubRepo();
        List<Ammunition> items = new ArrayList<>();
        items.add(new TestSword("New Sword", 10.0));
        repo.setItems(items);

        Scanner scanner = mockScanner("1");

        Command command = new EquipKnightCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertFalse(output.contains("Успішно"), "Не має писати Успішно при дублікаті");
    }

    @Test
    void testExecute_InvalidIndex() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        manager.setActiveKnight(new Knight("Test", "Test", Rank.NOVICE));

        StubRepo repo = new StubRepo();
        List<Ammunition> items = new ArrayList<>();
        items.add(new TestHelmet("Hat", 1.0));
        repo.setItems(items);

        Scanner scanner = mockScanner("5");

        Command command = new EquipKnightCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("Невірний номер"), "Має повідомити про невірний номер");
    }

    @Test
    void testExecute_InvalidFormat() throws UnsupportedEncodingException {
        StubManager manager = new StubManager();
        manager.setActiveKnight(new Knight("Test", "Test", Rank.NOVICE));
        StubRepo repo = new StubRepo();

        Scanner scanner = mockScanner("apple");

        Command command = new EquipKnightCommand(manager, repo, scanner);
        command.execute();

        String output = outContent.toString(StandardCharsets.UTF_8.name());

        assertTrue(output.contains("Помилка: введіть число"), "Має зловити NumberFormatException");
    }
}