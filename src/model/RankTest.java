package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовий клас для перевірки структури та логіки перерахування (Enum) Rank.
 */
class RankTest {

    @Test
    void testEnumValuesCount() {
        // Отримуємо масив усіх значень Enum через метод values()
        // Перевіряємо, що в системі зареєстровано рівно 4 ранги
        assertEquals(4, Rank.values().length, "Кількість рангів має дорівнювати 4");
    }

    @Test
    void testEnumExistence() {
        // Перевіряємо, що конкретні константи (ранги) були оголошені і існують (не null)
        // Це захищає від випадкового видалення рангу з коду
        assertNotNull(Rank.NOVICE);
        assertNotNull(Rank.VETERAN);
        assertNotNull(Rank.MASTER);
        assertNotNull(Rank.GRAND_MASTER);
    }

    @Test
    void testValueOf() {
        // Перевіряємо вбудований метод valueOf(), який перетворює текст (String) у відповідний Enum
        // Це корисно, коли ми отримуємо дані з бази даних або JSON у вигляді тексту
        assertEquals(Rank.NOVICE, Rank.valueOf("NOVICE"));
        assertEquals(Rank.GRAND_MASTER, Rank.valueOf("GRAND_MASTER"));
    }

    @Test
    void testToStringOrName() {
        // Перевіряємо, що метод name() повертає точне рядкове ім'я константи
        // Це гарантує, що ідентифікатор рангу не було змінено (наприклад, з "VETERAN" на "VET")
        assertEquals("VETERAN", Rank.VETERAN.name());
    }
}