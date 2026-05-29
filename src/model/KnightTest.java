package model;

// Імпортуємо класи, які будемо тестувати
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;

// Імпортуємо інструменти JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Статичний імпорт дозволяє писати assertEquals() замість Assertions.assertEquals()
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовий клас для перевірки бізнес-логіки сутності Knight.
 * * Цей клас використовує Unit-тестування: ми ізолюємо лицаря від реальних предметів
 * (мечів, шоломів), використовуючи "заглушки" (Stubs), щоб перевірити
 * виключно математику та правила поведінки самого лицаря.
 */
class KnightTest {

    // Об'єкт, який ми тестуємо (SUT - System Under Test)
    private Knight knight;

    // =================================================================================
    // РОЗДІЛ: ВНУТРІШНІ КЛАСИ-ЗАГЛУШКИ (STUBS)
    // =================================================================================
    /*
       Чому це робимо?
       1. Ізоляція: Якщо в класі "Sword" буде баг, цей тест не повинен впасти.
       2. Контроль: Ми можемо створити предмет із будь-якою вагою без складної логіки.
       3. Виправлення помилки доступу: Ми використовуємо конструктори батьків (super),
          щоб не дублювати приватні поля (наприклад, weight).
    */

    // 1. Stub для звичайної амуніції (наприклад, еліксир, монета тощо)
    static class TestItem extends Ammunition {
        public TestItem(double weight) {
            // Викликаємо конструктор батьківського класу (Ammunition).
            // Він сам збереже вагу у своїй приватній змінній.
            // "Test Item" - назва, 10 - ціна (ці параметри не впливають на логіку ваги).
            super("Test Item", weight, 10);
        }

        // Перевизначаємо метод, щоб він брав значення з батьківського класу.
        @Override
        public double getWeight() {
            return super.getWeight();
        }
    }

    // 2. Stub іншого типу предмета
    // Це потрібно для тестування правила: "Лицар не може мати два предмети одного КЛАСУ".
    // Тобто (TestItem + TestItem) = заборонено, а (TestItem + AnotherTestItem) = дозволено.
    static class AnotherTestItem extends Ammunition {
        public AnotherTestItem(double weight) {
            super("Another Item", weight, 10);
        }

        @Override
        public double getWeight() {
            return super.getWeight();
        }
    }

    // 3. Stub для броні (Armor)
    // Цей клас потрібен, щоб перевірити розрахунок захисту (defense).
    // Звичайна Ammunition не дає захисту, а Armor - дає.
    static class TestArmor extends Armor {
        private int defense; // Локальне поле для зберігання захисту в тесті

        public TestArmor(double weight, int defense) {
            // Передаємо параметри у батьківський клас Armor.
            // 100 - це умовна міцність (durability), яка нам зараз не важлива.
            super("Test Armor", weight, 100, defense);
            this.defense = defense;
        }

        // Вагу беремо з батька
        @Override
        public double getWeight() {
            return super.getWeight();
        }

        // Захист беремо з локального поля (або можна з super, якщо там є гетер)
        @Override
        public int getDefense() {
            return defense;
        }
    }

    // 4. Змінна для рангу (Mock)
    // Поки що логіка лицаря не залежить від рангу, тому використовуємо null.
    // Це показує, що тест сфокусований на вазі та захисті, а не на званнях.
    private Rank mockRank = null;

    // =================================================================================
    // РОЗДІЛ: ПІДГОТОВКА (SETUP)
    // =================================================================================

    /**
     * Метод, помічений @BeforeEach, запускається ПЕРЕД КОЖНИМ тестом.
     * Це гарантує, що кожен тест починається з "чистого" лицаря.
     * Якщо один тест заповнить інвентар, це не вплине на наступний тест.
     */
    @BeforeEach
    void setUp() {
        // Створюємо нового лицаря з фіксованими параметрами:
        // Ім'я: Lancelot
        // Сила (припускаємо, що вона задає ліміт ваги): 60 (значення за замовчуванням у класі Knight?)
        // Базовий захист: 20
        knight = new Knight("Lancelot", "Round Table", mockRank);
    }

    // =================================================================================
    // РОЗДІЛ: ТЕСТИ (TESTS)
    // =================================================================================

    @Test
    void testConstructorAndGetters() {
        // "Sanity Check" - перевірка на адекватність. Чи об'єкт взагалі створився?
        assertNotNull(knight);

        // Перевіряємо, чи правильно збереглися базові поля
        assertEquals("Lancelot", knight.getName());
        assertEquals(mockRank, knight.getRank());

        // ID нового лицаря (не збереженого в БД) = 0;
        // фактичний ID присвоює SQLite тільки після save()
        assertEquals(0, knight.getId(), "ID нового лицаря до збереження в БД має бути 0");

        // КРИТИЧНО ВАЖЛИВО: Перевірити, що список речей не null, але порожній.
        // Це вбереже від NullPointerException при спробі додати перший предмет.
        assertNotNull(knight.getEquipment(), "Список речей не має бути null");
        assertTrue(knight.getEquipment().isEmpty(), "Інвентар має бути пустим одразу після створення");
    }

    @Test
    void testMaxWeightCapacityCalculation() {
        // Ранг null повертає 18.0 за замовчуванням
        assertEquals(18.0, knight.getMaxWeightCapacity(), 0.0001);

        // Перевіряємо вантажопідйомність для кожного рангу
        Knight novice = new Knight("Novice Knight", "Order", Rank.NOVICE);
        assertEquals(18.0, novice.getMaxWeightCapacity(), 0.0001);

        Knight veteran = new Knight("Veteran Knight", "Order", Rank.VETERAN);
        assertEquals(20.0, veteran.getMaxWeightCapacity(), 0.0001);

        Knight master = new Knight("Master Knight", "Order", Rank.MASTER);
        assertEquals(22.0, master.getMaxWeightCapacity(), 0.0001);

        Knight grandMaster = new Knight("Grand Master Knight", "Order", Rank.GRAND_MASTER);
        assertEquals(24.0, grandMaster.getMaxWeightCapacity(), 0.0001);
    }

    @Test
    void testEquip_Success() {
        // Сценарій: Успішне додавання предмета.
        // Вага предмета (10.0) менша за ліміт (≈18.46).
        Ammunition item = new TestItem(10.0);

        boolean result = knight.equip(item);

        // 1. Метод має повернути true
        assertTrue(result, "Метод має повернути true при успішному додаванні");
        // 2. У списку має з'явитися 1 предмет
        assertEquals(1, knight.getEquipment().size());
        // 3. Поточна вага лицаря має оновитися
        assertEquals(10.0, knight.getCurrentWeight(), 0.0001);
    }

    @Test
    void testEquip_Fail_Overweight() {
        // Сценарій: Перевищення ваги.
        // Ліміт ≈18.46. Створюємо предмет вагою 20.0.
        Ammunition heavyItem = new TestItem(20.0);

        boolean result = knight.equip(heavyItem);

        // 1. Метод має відмовити (false)
        assertFalse(result, "Метод має повернути false, якщо вага перевищена");
        // 2. Список має залишитися пустим (транзакція скасована)
        assertTrue(knight.getEquipment().isEmpty(), "Предмет не повинен бути доданий у список");
    }

    @Test
    void testEquip_Fail_DuplicateType() {
        // Сценарій: Бізнес-правило "Один клас предмета - один екземпляр".

        // Крок 1: Додаємо перший предмет (успішно)
        knight.equip(new TestItem(1.0));

        // Крок 2: Пробуємо додати ЩЕ ОДИН предмет того ж класу (TestItem)
        boolean result = knight.equip(new TestItem(2.0));

        // Очікуємо провал
        assertFalse(result, "Не можна додавати два предмети одного класу");
        // Перевіряємо, що в інвентарі залишився тільки перший предмет
        assertEquals(1, knight.getEquipment().size(), "В інвентарі має залишитись тільки 1 предмет");
    }

    @Test
    void testEquip_Success_DifferentTypes() {
        // Сценарій: Додавання різних предметів дозволено.

        // 1. Додаємо TestItem
        knight.equip(new TestItem(1.0));

        // 2. Додаємо AnotherTestItem (це інший клас, хоч і теж спадкується від Ammunition)
        boolean result = knight.equip(new AnotherTestItem(1.0));

        assertTrue(result, "Має дозволяти додавати предмети різних класів");
        assertEquals(2, knight.getEquipment().size(), "Обидва предмети мають бути в інвентарі");
    }

    @Test
    void testTotalDefenseCalculation() {
        // Перевірка розрахунку захисту (Defense).

        // 1. Базовий захист (без броні) = 20
        assertEquals(20, knight.getTotalDefense());

        // 2. Додаємо звичайний предмет (TestItem).
        // Він є Ammunition, а не Armor, тому захист НЕ МАЄ зростати.
        knight.equip(new TestItem(5.0));
        assertEquals(20, knight.getTotalDefense());

        // 3. Додаємо броню (TestArmor).
        // Вага 5.0 (допустима), Захист +15.
        knight.equip(new TestArmor(5.0, 15));

        // Очікуємо: 20 (база) + 15 (броня) = 35.
        assertEquals(35, knight.getTotalDefense());
    }

    @Test
    void testNewEquipRules() {
        // Створюємо лицаря з великим лімітом ваги
        Knight gm = new Knight("Arthur", "Pendragon", Rank.GRAND_MASTER);

        // 1. Можна додати дві зброї
        Weapon w1 = new model.equipment.Sword("Excalibur", 2.0, 100.0, 15);
        Weapon w2 = new model.equipment.Axe("Battleaxe", 3.0, 80.0, 12);
        Weapon w3 = new model.equipment.Bow("Longbow", 1.5, 50.0, 10);

        assertTrue(gm.equip(w1));
        assertTrue(gm.equip(w2));
        assertFalse(gm.equip(w3), "Не можна додати третю зброю");
        assertEquals(2, gm.getEquipment().stream().filter(a -> a instanceof Weapon).count());

        // 2. Можна додати тільки 1 шолом, 1 нагрудник, 1 поножі
        Armor h1 = new model.equipment.Helmet("Iron Helmet", 1.5, 40.0, 5);
        Armor h2 = new model.equipment.Helmet("Golden Helmet", 2.0, 150.0, 8);
        Armor b1 = new model.equipment.Breastplate("Steel Cuirass", 5.0, 200.0, 15);
        Armor g1 = new model.equipment.Greaves("Steel Greaves", 3.0, 100.0, 8);

        assertTrue(gm.equip(h1));
        assertFalse(gm.equip(h2), "Не можна додати другий шолом");
        assertTrue(gm.equip(b1));
        assertTrue(gm.equip(g1));

        assertEquals(3, gm.getEquipment().stream().filter(a -> a instanceof Armor).count());
    }

    @Test
    void testToString() {
        // Перевірка форматування виводу методу toString().
        String info = knight.toString();

        // Ми перевіряємо наявність ключових слів, а не точний рядок,
        // бо формат може трохи змінюватись, і тест не має через це падати.
        assertTrue(info.contains("ID:"), "Має містити ID");
        assertTrue(info.contains("Lancelot"), "Має містити ім'я");
        assertTrue(info.contains("Round Table"), "Має містити орден");
        assertTrue(info.contains("Weight:"), "Має містити інформацію про вагу");
    }
}