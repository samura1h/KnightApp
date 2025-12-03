package model.equipment;

/**
 * Абстрактний клас Броня. Розширює Амуніцію.
 * Додає нову характеристику - захист.
 */
public abstract class Armor extends Ammunition {
    private int defense; // Показник захисту

    public Armor(String name, double weight, double price, int defense) {
        super(name, weight, price); // Викликаємо конструктор батька (Ammunition)
        this.defense = defense;
    }
    public int getDefense() { return defense; }
}