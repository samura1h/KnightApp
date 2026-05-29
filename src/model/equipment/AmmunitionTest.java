package model.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовий клас для перевірки базового функціоналу амуніції/броні.
 * Оскільки Ammunition (або Armor) є абстрактним класом, ми не можемо створити його екземпляр напряму.
 * Тому ми тестуємо його через конкретну реалізацію або заглушку.
 */
class AmmunitionTest {

    // --- ВНУТРІШНІЙ КЛАС (Stub/Заглушка) ---
    // Створюємо спрощений конкретний клас, що наслідує Armor.
    // Це дозволяє нам протестувати логіку, закладену в батьківському абстрактному класі
    // (конструктор, геттери, compareTo), не прив'язуючись до реальних шоломів чи мечів.
    private static class TestArmor extends Armor {
        public TestArmor(String name, double weight, double price, int defense) {
            super(name, weight, price, defense);
        }
    }

    @Test
    void testConstructorAndGetters() {
        // Перевіряємо, чи базовий конструктор правильно ініціалізує поля
        TestArmor armor = new TestArmor("Gold Armor", 15.5, 500.0, 100);

        assertEquals("Gold Armor", armor.getName());
        assertEquals(15.5, armor.getWeight());
        assertEquals(500.0, armor.getPrice());
        assertEquals(100, armor.getDefense());
    }

    @Test
    void testCompareTo() {
        // Перевірка інтерфейсу Comparable.
        // Згідно з логікою, сортування відбувається за вагою (Weight).

        TestArmor heavy = new TestArmor("Heavy", 20.0, 100, 10);
        TestArmor light = new TestArmor("Light", 10.0, 100, 10);

        // heavy (20.0) > light (10.0), тому результат має бути додатним (> 0)
        assertTrue(heavy.compareTo(light) > 0, "Heavy має бути 'більшим' за Light через вагу");

        // light (10.0) < heavy (20.0), тому результат має бути від'ємним (< 0)
        assertTrue(light.compareTo(heavy) < 0, "Light має бути 'меншим' за Heavy");

        // Рівні ваги повертають 0
        assertEquals(0, heavy.compareTo(new TestArmor("Same", 20.0, 100, 10)));
    }

    @Test
    void testToString() {
        TestArmor item = new TestArmor("Sword", 10.5, 100.0, 50);
        String result = item.toString();

        // 1. Перевіряємо, чи назва потрапила у вивід
        assertTrue(result.contains("Sword"));

        // 2. Перевіряємо числові значення з урахуванням локалі.
        // В англійській системі (US) дробова частина відділяється крапкою (10.5),
        // в українській/європейській — комою (10,5).
        // replace(',', '.') уніфікує рядок, щоб тест проходив на будь-якому комп'ютері.
        assertTrue(result.replace(',', '.').contains("10.5"));
    }
}