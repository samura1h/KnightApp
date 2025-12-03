package model.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AmmunitionTest {

    // Створюємо конкретний клас для тестування абстрактного Armor/Ammunition
    private static class TestArmor extends Armor {
        public TestArmor(String name, double weight, double price, int defense) {
            super(name, weight, price, defense);
        }
    }

    @Test
    void testConstructorAndGetters() {
        TestArmor armor = new TestArmor("Gold Armor", 15.5, 500.0, 100);

        assertEquals("Gold Armor", armor.getName());
        assertEquals(15.5, armor.getWeight());
        assertEquals(500.0, armor.getPrice());
        assertEquals(100, armor.getDefense());
    }

    @Test
    void testCompareTo() {
        // Логіка compareTo порівнює вагу
        TestArmor heavy = new TestArmor("Heavy", 20.0, 100, 10);
        TestArmor light = new TestArmor("Light", 10.0, 100, 10);

        assertTrue(heavy.compareTo(light) > 0, "Heavy має бути 'більшим' за Light");
        assertTrue(light.compareTo(heavy) < 0, "Light має бути 'меншим' за Heavy");
        assertEquals(0, heavy.compareTo(new TestArmor("Same", 20.0, 100, 10)));
    }

    @Test
    void testToString() {
        TestArmor item = new TestArmor("Sword", 10.5, 100.0, 50);
        String result = item.toString();

        // Перевіряємо форматування
        assertTrue(result.contains("Sword"));
        // Використовуємо replace, щоб тест проходив і з комою, і з крапкою (локаль)
        assertTrue(result.replace(',', '.').contains("10.5"));
    }
}