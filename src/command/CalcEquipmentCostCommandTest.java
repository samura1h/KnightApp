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

// Клас тестів для перевірки команди підрахунку вартості
class CalcEquipmentCostCommandTest {

    // Спеціальний потік для збереження всього, що програма пише в консоль
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Зберігаємо посилання на "справжню" консоль, щоб потім повернути все як було
    private final PrintStream originalOut = System.out;

    // Цей метод запускається ПЕРЕД кожним тестом (@Test)
    @BeforeEach
    void setUpStreams() throws UnsupportedEncodingException {
        // Підміняємо System.out на наш outContent.
        // Тепер System.out.println() пише не на екран, а в пам'ять змінної outContent.
        // Використовуємо UTF-8, щоб коректно обробляти кирилицю.
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8.name()));
    }

    // Цей метод запускається ПІСЛЯ кожного тесту
    @AfterEach
    void restoreStreams() {
        // Повертаємо стандартний вивід назад, щоб інші тести або логи працювали нормально
        System.setOut(originalOut);
    }

    // --- ДОПОМІЖНІ КЛАСИ ДЛЯ ТЕСТУВАННЯ ---

    // Створюємо конкретні класи амуніції для тестів.
    // Це потрібно, бо клас Knight, ймовірно, забороняє одягати два предмети одного класу
    // (наприклад, не можна взяти два шоломи). Тому ми робимо "Меч" і "Щит".
    static class TestSword extends Ammunition {
        public TestSword(String name, double price, double weight) {
            super(name, price, weight);
        }
    }

    static class TestShield extends Ammunition {
        public TestShield(String name, double price, double weight) {
            super(name, price, weight);
        }
    }

    // Заглушка (Stub) для менеджера лицарів.
    // Нам не потрібна вся логіка реального менеджера, нам треба лише вміти
    // встановити активного лицаря і отримати його назад.
    static class StubKnightManager extends KnightManager {
        private Knight activeKnight;

        // Викликаємо конструктор суперкласу з null, бо реальні сервіси нам тут не треба
        public StubKnightManager() { super(null, null); }

        // Метод для налаштування тесту (встановити тестового лицаря)
        public void setActiveKnight(Knight k) { this.activeKnight = k; }

        @Override
        public Knight getActiveKnight() { return activeKnight; }
    }

    // Заглушка (Stub) для репозиторію (магазину/складу).
    // Вона просто зберігає список, який ми їй дамо, і повертає його.
    // Ніякої бази даних чи файлів.
    static class StubRepo extends EquipmentRepository {
        private List<Ammunition> items = new ArrayList<>();

        public void setItems(List<Ammunition> items) { this.items = items; }

        @Override
        public List<Ammunition> getAll() { return items; }
    }

    // Метод, який імітує введення користувача з клавіатури.
    // input - це рядок, який ми "друкуємо" програмно.
    private Scanner mockScanner(String input) {
        // Додаємо перенесення рядка, як ніби користувач натиснув Enter
        String fullInput = input + System.lineSeparator();
        return new Scanner(new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    // --- ТЕСТИ ---

    @Test
    void testCalculateActiveKnight_Success() throws UnsupportedEncodingException {
        // 1. Arrange (Підготовка даних)
        StubKnightManager manager = new StubKnightManager();
        Knight k = new Knight("Sir Test", "TestOrder", Rank.MASTER);

        // Даємо лицарю меч (ціна 5.0) і щит (ціна 5.0). Разом має бути 10.0.
        // Вага теж 5+5=10, що зазвичай входить у ліміти лицаря.
        k.equip(new TestSword("Excalibur", 5.0, 5.0));
        k.equip(new TestShield("Aegis", 5.0, 5.0));

        manager.setActiveKnight(k); // Робимо цього лицаря активним

        StubRepo repo = new StubRepo(); // Репозиторій тут не важливий, але потрібен для конструктора

        // Імітуємо, що користувач натиснув "1" (Опція: порахувати вартість екіпірування лицаря)
        Scanner scanner = mockScanner("1");

        // 2. Act (Виконання дії)
        Command command = new CalcEquipmentCostCommand(manager, repo, scanner);
        command.execute();

        // Отримуємо все, що команда надрукувала в консоль
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Для відлагодження: якщо тест впаде, ми побачимо, що саме вивела програма
        if (!output.contains("10.0")) {
            System.err.println("!!! DEBUG OUTPUT !!!");
            System.err.println(output);
        }

        // 3. Assert (Перевірка результату)
        // Шукаємо число "10.0" у виводі консолі
        assertTrue(output.contains("10.0"), "Вартість має бути 10.0 (5.0 + 5.0)");
    }

    @Test
    void testCalculateRepository_Success() throws UnsupportedEncodingException {
        // 1. Arrange
        StubKnightManager manager = new StubKnightManager();
        StubRepo repo = new StubRepo();

        List<Ammunition> shopItems = new ArrayList<>();

        // Наповнюємо "магазин" предметами. Тут типи не важливі, головне ціна.
        shopItems.add(new TestSword("Gold Armor", 5.0, 5.0));
        shopItems.add(new TestShield("Magic Helm", 5.0, 5.0));
        repo.setItems(shopItems); // Записуємо ці предмети в заглушку репозиторію

        // Імітуємо, що користувач натиснув "2" (Опція: порахувати вартість всього у репозиторії)
        Scanner scanner = mockScanner("2");

        // 2. Act
        Command command = new CalcEquipmentCostCommand(manager, repo, scanner);
        command.execute();

        // 3. Assert
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, що сума 10.0 була виведена
        assertTrue(output.contains("10.0"), "Вартість магазину має бути 10.0");
    }

    @Test
    void testNoActiveKnight_Error() throws UnsupportedEncodingException {
        // 1. Arrange
        StubKnightManager manager = new StubKnightManager();
        // Ми НЕ встановлюємо activeKnight, тому він залишається null

        StubRepo repo = new StubRepo();

        // Користувач намагається порахувати вартість лицаря ("1"), але лицаря немає
        Scanner scanner = mockScanner("1");

        // 2. Act
        Command command = new CalcEquipmentCostCommand(manager, repo, scanner);
        command.execute();

        // 3. Assert
        String output = outContent.toString(StandardCharsets.UTF_8.name());

        // Перевіряємо, що програма видала повідомлення про помилку, а не впала з Exception
        assertTrue(output.contains("ПОМИЛКА") || output.contains("не обраний"),
                "Має бути повідомлення про помилку, що лицар не обраний");
    }
}