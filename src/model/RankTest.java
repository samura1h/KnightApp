package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RankTest {

    @Test
    void testEnumValuesCount() {

        assertEquals(4, Rank.values().length, "Кількість рангів має дорівнювати 4");
    }

    @Test
    void testEnumExistence() {

        assertNotNull(Rank.NOVICE);
        assertNotNull(Rank.VETERAN);
        assertNotNull(Rank.MASTER);
        assertNotNull(Rank.GRAND_MASTER);
    }

    @Test
    void testValueOf() {

        assertEquals(Rank.NOVICE, Rank.valueOf("NOVICE"));
        assertEquals(Rank.GRAND_MASTER, Rank.valueOf("GRAND_MASTER"));
    }

    @Test
    void testToStringOrName() {

        assertEquals("VETERAN", Rank.VETERAN.name());
    }
}