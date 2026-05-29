package command;

import model.Knight;
import model.Rank;
import service.KnightManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовий клас для перевірки команди CreateOrLoadKnightCommand.
 * Використовує JUnit 5.
 */
class CreateOrLoadKnightCommandTest {

    // --- ЗАГЛУШКА (STUB) для KnightManager ---
    /**
     * Внутрішній клас TestManager наслідує KnightManager.
     * Його мета - замінити реальну логіку (роботу з файлами, БД) на спрощену
     * для потреб тестування. Ми хочемо перевірити лише команду, а не менеджер.
     */
    static class TestManager extends KnightManager {
        // Прапорці (flags) для перевірки, чи викликались певні методи
        public boolean addKnightCalled = false;
        public boolean loadFromDiskCalled = false;

        // Змінна для збереження ID, який передали в setActiveKnight
        public int setActiveIdArgument = -1;

        // Внутрішня map-а для імітації бази даних у пам'яті
        private Map<Integer, Knight> knightsMap = new HashMap<>();
        private Knight activeKnight = null;

        // --- КОНСТРУКТОР ---
        public TestManager() {
            // Передаємо null замість репозиторіїв, бо у цьому тестовому класі
            // ми перевизначаємо (override) методи, які їх використовують.
            // Це дозволяє не створювати моки для репозиторіїв.
            super(null, null);
        }

        // Метод для налаштування початкового стану (наприклад, додати лицарів перед тестом)
        public void setKnightsMap(Map<Integer, Knight> map) {
            this.knightsMap = map;
        }

        @Override
        public void addKnight(Knight k) {
            addKnightCalled = true; // Фіксуємо виклик
            knightsMap.put(k.getId(), k); // Зберігаємо у тестову map
        }

        @Override
        public Map<Integer, Knight> getAllKnights() {
            return knightsMap;
        }

        @Override
        public void loadFromDisk() {
            loadFromDiskCalled = true; // Фіксуємо виклик
            // Імітуємо, що після завантаження з диску з'явився лицар ID 99
            knightsMap.put(99, new Knight("LoadedKnight", "OldOrder", Rank.MASTER));
        }

        @Override
        public void setActiveKnight(int id) {
            setActiveIdArgument = id; // Запам'ятовуємо, який ID намагались активувати
            activeKnight = knightsMap.get(id);
        }

        @Override
        public Knight getActiveKnight() {
            return activeKnight;
        }
    }

    // --- ДОПОМІЖНИЙ МЕТОД: Створення Scanner з рядка ---
    /**
     * Створює об'єкт Scanner, який читає дані не з клавіатури, а з переданого рядка.
     * Це дозволяє автоматизувати введення даних у тестах.
     * @param input Рядок, що імітує натискання клавіш користувачем (включаючи \n як Enter).
     */
    private Scanner mockScanner(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        return new Scanner(in);
    }

    @Test
    void testExecute_Option1_CreateKnight() {
        // Сценарій вводу:
        // "1" - вибір пункту меню "Створити лицаря"
        // "Arthur" - введення імені
        // "Camelot" - введення ордену
        // "2" - вибір рангу (відповідає Rank.VETERAN, якщо логіка базується на ordinal або switch)
        String input = "1\nArthur\nCamelot\n2\n";

        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевірки (Assertions)
        assertTrue(manager.addKnightCalled, "Має бути викликаний метод addKnight");
        assertFalse(manager.loadFromDiskCalled, "loadFromDisk НЕ має викликатися при створенні нового");

        // Перевіряємо, чи дійсно лицар зберігся у менеджері з правильними даними
        assertEquals(1, manager.getAllKnights().size());
        Knight created = manager.getAllKnights().values().iterator().next();
        assertEquals("Arthur", created.getName());
        assertEquals("Camelot", created.getOrden());
        assertEquals(Rank.VETERAN, created.getRank());
    }

    @Test
    void testExecute_Option2_LoadKnight_EmptyMemory_UserSaysNo() {
        // Сценарій:
        // "2" - вибрати існуючого
        // Система бачить, що список пустий, питає чи завантажити з файлу?
        // "n" - користувач відмовляється
        String input = "2\nn\n";

        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевіряємо, що нічого не відбулося
        assertFalse(manager.loadFromDiskCalled, "Менеджер не повинен вантажити диск, якщо користувач відмовився");
        assertFalse(manager.addKnightCalled);
    }

    @Test
    void testExecute_Option2_LoadKnight_EmptyMemory_UserSaysYes_ThenSelectsID() {
        // Сценарій:
        // "2" - вибрати існуючого
        // "y" - список пустий, погоджуємось завантажити з файлу
        // "99" - вибираємо ID лицаря, який (ми знаємо зі Stub-а) з'явиться після завантаження
        String input = "2\ny\n99\n";

        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевірки
        assertTrue(manager.loadFromDiskCalled, "Має викликатися loadFromDisk");
        assertEquals(99, manager.setActiveIdArgument, "Має спробувати встановити активного лицаря з ID 99");
        assertNotNull(manager.getActiveKnight(), "Активний лицар має бути встановлений в об'єкті менеджера");
    }

    @Test
    void testExecute_Option2_AlreadyLoaded_SelectID() {
        // Підготовка даних: У пам'яті вже є лицар (ID 5)
        // Сценарій вводу:
        // "2" - вибрати існуючого
        // "5" - ввести ID цього лицаря
        String input = "2\n5\n";
        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        // Заповнюємо пам'ять менеджера попередніми даними
        Map<Integer, Knight> existing = new HashMap<>();
        Knight k = new Knight("Existing", "Ord", Rank.NOVICE);
        // Припустимо, ми якось встановлюємо йому ID 5 (якщо ID генерується автоматично, тут треба бути обережним,
        // але для Map key=5 це працює)
        existing.put(5, k);
        manager.setKnightsMap(existing);

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевірки
        assertFalse(manager.loadFromDiskCalled, "Не треба вантажити з диску, якщо пам'ять не пуста");
        assertEquals(5, manager.setActiveIdArgument, "Має вибрати ID 5");
    }

    @Test
    void testExecute_InvalidOption() {
        // Сценарій: Ввели "3" у головному меню, такого пункту немає.
        String input = "3\n";
        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевіряємо, що жодна критична дія не виконалася
        assertFalse(manager.addKnightCalled);
        assertFalse(manager.loadFromDiskCalled);
    }

    @Test
    void testCreateProcess_InvalidRankInput() {
        // Тестування обробки помилок (try-catch)
        // Сценарій:
        // "1" - створити
        // "Name", "Ord" - ім'я та орден
        // "abc" - некоректний ввід для рангу (очікується число)
        String input = "1\nName\nOrd\nabc\n";

        Scanner scanner = mockScanner(input);
        TestManager manager = new TestManager();

        Command command = new CreateOrLoadKnightCommand(manager, scanner);
        command.execute();

        // Перевіряємо, що лицар створився, але з дефолтним рангом (NOVICE)
        Knight created = manager.getAllKnights().values().iterator().next();
        assertEquals(Rank.NOVICE, created.getRank(), "При помилці вводу рангу має бути встановлено NOVICE (за замовчуванням)");
    }
}