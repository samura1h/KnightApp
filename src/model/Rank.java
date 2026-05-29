package model;

/**
 * Перелічення (Enum) для рангів лицаря.
 * Використовується для обмеження можливих значень (не можна ввести неіснуючий ранг).
 */
public enum Rank {
    NOVICE("Novice"),     // Новачок
    VETERAN("Veteran"),    // Ветеран
    MASTER("Master"),     // Майстер
    GRAND_MASTER("Grand Master"); // Гросмейстер

    private final String displayName;

    Rank(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}