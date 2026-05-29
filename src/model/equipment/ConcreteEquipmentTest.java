package model.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовий клас для перевірки конкретних реалізацій спорядження.
 * Мета: переконатися, що кожен тип зброї та броні коректно створюється,
 * зберігає свої властивості та правильно відображає специфічні характеристики (наприклад, шкоду).
 */
class ConcreteEquipmentTest {

    // ==========================================
    // --- ТЕСТИ ДЛЯ ЗБРОЇ (Weapons) ---
    // ==========================================

    @Test
    void testSword() {
        // --- ДОДАНО ТЕСТ ДЛЯ SWORD ---
        // Створюємо об'єкт із тестовими даними: назва, вага, ціна, шкода
        Sword sword = new Sword("King's Sword", 3.5, 120.0, 40);

        // 1. Перевірка ієрархії: Sword має бути підкласом Weapon
        assertTrue(sword instanceof Weapon, "Sword має бути Weapon");

        // 2. Перевірка геттерів: чи дані записались вірно
        assertEquals("King's Sword", sword.getName());
        assertEquals(3.5, sword.getWeight());
        assertEquals(120.0, sword.getPrice());

        // 3. Перевірка поведінки: чи виводиться специфічний атрибут (шкода)
        // Це гарантує, що метод toString() був перевизначений або використовує базовий метод Weapon коректно
        assertTrue(sword.toString().contains("Шкода: +40"));
    }

    @Test
    void testAxe() {
        // Перевірка сокири
        Axe axe = new Axe("Battle Axe", 5.0, 50.0, 30);
        assertEquals("Battle Axe", axe.getName());
        // Переконуємось, що у описі присутня інформація про шкоду
        assertTrue(axe.toString().contains("Шкода: +30"));
    }

    @Test
    void testBow() {
        // Перевірка лука (легка зброя)
        Bow bow = new Bow("Elven Bow", 2.0, 100.0, 15);
        assertEquals(2.0, bow.getWeight());
        assertTrue(bow.toString().contains("Шкода: +15"));
    }

    @Test
    void testKnife() {
        // Перевірка ножа (найлегша зброя, мала шкода)
        Knife knife = new Knife("Dagger", 0.5, 10.0, 5);
        assertEquals(10.0, knife.getPrice());
        assertTrue(knife.toString().contains("Шкода: +5"));
    }

    @Test
    void testMace() {
        // Перевірка булави (важка зброя)
        Mace mace = new Mace("Iron Mace", 6.0, 45.0, 35);
        assertEquals(6.0, mace.getWeight());
        assertTrue(mace.toString().contains("Шкода: +35"));
    }

    @Test
    void testSpear() {
        // Перевірка списа
        Spear spear = new Spear("Long Spear", 3.0, 25.0, 20);
        assertEquals("Long Spear", spear.getName());
        assertTrue(spear.toString().contains("Шкода: +20"));
    }

    @Test
    void testTwoHandedSword() {
        // Перевірка дворучного меча (велика вага і велика шкода)
        TwoHandedSword sword = new TwoHandedSword("Claymore", 8.0, 200.0, 60);
        assertEquals(8.0, sword.getWeight());
        assertTrue(sword.toString().contains("Шкода: +60"));
    }

    // ==========================================
    // --- ТЕСТИ ДЛЯ БРОНІ (Armor) ---
    // ==========================================

    @Test
    void testHelmet() {
        // Створюємо шолом: назва, вага, ціна, захист
        Helmet helmet = new Helmet("Steel Helmet", 2.0, 30.0, 10);

        // Перевірка поліморфізму: Helmet є Armor
        assertTrue(helmet instanceof Armor);
        assertEquals(2.0, helmet.getWeight());
        // (Опціонально) Можна додати перевірку на наявність рядка "Захист: +10" у toString()
    }

    @Test
    void testBreastplate() {
        // Створюємо нагрудник (важка броня)
        Breastplate armor = new Breastplate("Golden Armor", 15.0, 500.0, 50);

        assertEquals(500.0, armor.getPrice());
        assertTrue(armor instanceof Armor);
    }
}