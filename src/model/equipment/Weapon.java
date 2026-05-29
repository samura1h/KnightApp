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

    public int getDamage() { return damage; }

    @Override
    public double getPrice() {
        return super.getPrice();
    }

    @Override
    public String toString() {
        return super.toString() + " | Damage: +" + damage;
    }
}