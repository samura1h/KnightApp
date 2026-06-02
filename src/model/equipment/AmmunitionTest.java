package model.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AmmunitionTest {

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

        TestArmor heavy = new TestArmor("Heavy", 20.0, 100, 10);
        TestArmor light = new TestArmor("Light", 10.0, 100, 10);

        assertTrue(heavy.compareTo(light) > 0, "Heavy має бути 'більшим' за Light через вагу");

        assertTrue(light.compareTo(heavy) < 0, "Light має бути 'меншим' за Heavy");

        assertEquals(0, heavy.compareTo(new TestArmor("Same", 20.0, 100, 10)));
    }

    @Test
    void testToString() {
        TestArmor item = new TestArmor("Sword", 10.5, 100.0, 50);
        String result = item.toString();

        assertTrue(result.contains("Sword"));

        assertTrue(result.replace(',', '.').contains("10.5"));
    }
}