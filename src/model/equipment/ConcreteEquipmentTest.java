package model.equipment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConcreteEquipmentTest {

    @Test
    void testSword() {

        Sword sword = new Sword("King's Sword", 3.5, 120.0, 40);

        assertTrue(sword instanceof Weapon, "Sword має бути Weapon");

        assertEquals("King's Sword", sword.getName());
        assertEquals(3.5, sword.getWeight());
        assertEquals(120.0, sword.getPrice());

        assertTrue(sword.toString().contains("Damage: +40"));
    }

    @Test
    void testAxe() {
        
        Axe axe = new Axe("Battle Axe", 5.0, 50.0, 30);
        assertEquals("Battle Axe", axe.getName());
        
        assertTrue(axe.toString().contains("Damage: +30"));
    }

    @Test
    void testBow() {
        
        Bow bow = new Bow("Elven Bow", 2.0, 100.0, 15);
        assertEquals(2.0, bow.getWeight());
        assertTrue(bow.toString().contains("Damage: +15"));
    }

    @Test
    void testKnife() {
        
        Knife knife = new Knife("Dagger", 0.5, 10.0, 5);
        assertEquals(10.0, knife.getPrice());
        assertTrue(knife.toString().contains("Damage: +5"));
    }

    @Test
    void testMace() {
        
        Mace mace = new Mace("Iron Mace", 6.0, 45.0, 35);
        assertEquals(6.0, mace.getWeight());
        assertTrue(mace.toString().contains("Damage: +35"));
    }

    @Test
    void testSpear() {
        
        Spear spear = new Spear("Long Spear", 3.0, 25.0, 20);
        assertEquals("Long Spear", spear.getName());
        assertTrue(spear.toString().contains("Damage: +20"));
    }

    @Test
    void testTwoHandedSword() {
        
        TwoHandedSword sword = new TwoHandedSword("Claymore", 8.0, 200.0, 60);
        assertEquals(8.0, sword.getWeight());
        assertTrue(sword.toString().contains("Damage: +60"));
    }

    @Test
    void testHelmet() {
        
        Helmet helmet = new Helmet("Steel Helmet", 2.0, 30.0, 10);

        assertTrue(helmet instanceof Armor);
        assertEquals(2.0, helmet.getWeight());
        
    }

    @Test
    void testBreastplate() {
        
        Breastplate armor = new Breastplate("Golden Armor", 15.0, 500.0, 50);

        assertEquals(500.0, armor.getPrice());
        assertTrue(armor instanceof Armor);
    }
}