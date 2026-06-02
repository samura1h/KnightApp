package model;

import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    private Knight knight;

    static class TestItem extends Ammunition {
        public TestItem(double weight) {

            super("Test Item", weight, 10);
        }

        @Override
        public double getWeight() {
            return super.getWeight();
        }
    }

    static class AnotherTestItem extends Ammunition {
        public AnotherTestItem(double weight) {
            super("Another Item", weight, 10);
        }

        @Override
        public double getWeight() {
            return super.getWeight();
        }
    }

    static class TestArmor extends Armor {
        private int defense; 

        public TestArmor(double weight, int defense) {

            super("Test Armor", weight, 100, defense);
            this.defense = defense;
        }

        @Override
        public double getWeight() {
            return super.getWeight();
        }

        @Override
        public int getDefense() {
            return defense;
        }
    }

    private Rank mockRank = null;

    @BeforeEach
    void setUp() {

        knight = new Knight("Lancelot", "Round Table", mockRank);
    }

    @Test
    void testConstructorAndGetters() {
        
        assertNotNull(knight);

        assertEquals("Lancelot", knight.getName());
        assertEquals(mockRank, knight.getRank());

        assertEquals(0, knight.getId(), "ID нового лицаря до збереження в БД має бути 0");

        assertNotNull(knight.getEquipment(), "Список речей не має бути null");
        assertTrue(knight.getEquipment().isEmpty(), "Інвентар має бути пустим одразу після створення");
    }

    @Test
    void testMaxWeightCapacityCalculation() {
        
        assertEquals(18.0, knight.getMaxWeightCapacity(), 0.0001);

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

        Ammunition item = new TestItem(10.0);

        boolean result = knight.equip(item);

        assertTrue(result, "Метод має повернути true при успішному додаванні");
        
        assertEquals(1, knight.getEquipment().size());
        
        assertEquals(10.0, knight.getCurrentWeight(), 0.0001);
    }

    @Test
    void testEquip_Fail_Overweight() {

        Ammunition heavyItem = new TestItem(20.0);

        boolean result = knight.equip(heavyItem);

        assertFalse(result, "Метод має повернути false, якщо вага перевищена");
        
        assertTrue(knight.getEquipment().isEmpty(), "Предмет не повинен бути доданий у список");
    }

    @Test
    void testEquip_Fail_DuplicateType() {

        knight.equip(new TestItem(1.0));

        boolean result = knight.equip(new TestItem(2.0));

        assertFalse(result, "Не можна додавати два предмети одного класу");
        
        assertEquals(1, knight.getEquipment().size(), "В інвентарі має залишитись тільки 1 предмет");
    }

    @Test
    void testEquip_Success_DifferentTypes() {

        knight.equip(new TestItem(1.0));

        boolean result = knight.equip(new AnotherTestItem(1.0));

        assertTrue(result, "Має дозволяти додавати предмети різних класів");
        assertEquals(2, knight.getEquipment().size(), "Обидва предмети мають бути в інвентарі");
    }

    @Test
    void testTotalDefenseCalculation() {

        assertEquals(0, knight.getTotalDefense());

        knight.equip(new TestItem(5.0));
        assertEquals(0, knight.getTotalDefense());

        knight.equip(new TestArmor(5.0, 15));

        assertEquals(15, knight.getTotalDefense());
    }

    @Test
    void testNewEquipRules() {
        
        Knight gm = new Knight("Arthur", "Pendragon", Rank.GRAND_MASTER);

        Weapon w1 = new model.equipment.Sword("Excalibur", 2.0, 100.0, 15);
        Weapon w2 = new model.equipment.Axe("Battleaxe", 3.0, 80.0, 12);
        Weapon w3 = new model.equipment.Bow("Longbow", 1.5, 50.0, 10);

        assertTrue(gm.equip(w1));
        assertTrue(gm.equip(w2));
        assertFalse(gm.equip(w3), "Не можна додати третю зброю");
        assertEquals(2, gm.getEquipment().stream().filter(a -> a instanceof Weapon).count());

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
        
        String info = knight.toString();

        assertTrue(info.contains("ID:"), "Має містити ID");
        assertTrue(info.contains("Lancelot"), "Має містити ім'я");
        assertTrue(info.contains("Round Table"), "Має містити орден");
        assertTrue(info.contains("Weight:"), "Має містити інформацію про вагу");
    }
}