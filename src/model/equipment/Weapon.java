package model.equipment;

/**
 * Абстрактний клас "Зброя". Успадковується від базової Амуніції.
 * Додає специфічне поле - damage (шкода).
 */
public abstract class Weapon extends Ammunition {
    private int damage; // Одиниць урону

    public Weapon(String name, double weight, double price, int damage) {
        super(name, weight, price); // Передаємо загальні параметри батьківському класу
        this.damage = damage;
    }
    // Геттер для отримання шкоди
    public int getDamage() {
        return damage;
    }
    // Перевизначаємо toString, щоб додавати інформацію про шкоду до загального опису
    @Override
    public String toString() {
        return super.toString() + " | Шкода: +" + damage;
    }
}