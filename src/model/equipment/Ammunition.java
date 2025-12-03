package model.equipment; // Вказуємо, що цей клас знаходиться в пакеті model.equipment

import java.io.Serializable; // Імпортуємо інтерфейс, який дозволяє зберігати об'єкти цього класу у файл (серіалізація)

// Оголошуємо абстрактний клас Ammunition.
// 'abstract' означає, що ми не можемо створити об'єкт new Ammunition(), тільки конкретні (наприклад, Sword).
// 'implements Comparable' дозволяє сортувати об'єкти.
// 'implements Serializable' дозволяє записувати об'єкти на диск.
public abstract class Ammunition implements Comparable<Ammunition>, Serializable {
    private String name;   // Приватне поле для зберігання назви предмету
    private double weight; // Приватне поле для ваги (використовуємо double для дробових чисел)
    private double price;  // Приватне поле для ціни

    // Конструктор класу. Викликається, коли ми створюємо новий предмет (через super() у нащадках).
    public Ammunition(String name, double weight, double price) {
        this.name = name;     // Записуємо передану назву у поле класу
        this.weight = weight; // Записуємо передану вагу
        this.price = price;   // Записуємо передану ціну
    }

    // --- Геттери (методи доступу до приватних полів ззовні) ---

    public String getName() { return name; } // Повертає назву
    public double getWeight() { return weight; } // Повертає вагу
    public double getPrice() { return price; } // Повертає ціну

    /**
     * Метод для порівняння двох предметів.
     * Обов'язковий через 'implements Comparable'.
     * Використовується Java автоматично, коли ми викликаємо Collections.sort().
     */
    @Override
    public int compareTo(Ammunition other) {
        // Використовуємо вбудований метод Double.compare для порівняння ваги поточного предмета (this) і іншого (other).
        // Повертає: від'ємне число (якщо легший), 0 (якщо рівні), додатне (якщо важчий).
        return Double.compare(this.weight, other.weight);
    }

    // Перевизначаємо метод toString, щоб при виводі в консоль (System.out.println)
    // ми бачили гарний текст, а не адресу пам'яті.
    @Override
    public String toString() {
        // String.format форматує рядок:
        // %-20s - рядок (назва), займає 20 символів, вирівнювання ліворуч
        // %.1f - дробове число з 1 знаком після коми
        return String.format("%-20s (Вага: %.1fкг, Ціна: %.1f$)", name, weight, price);
    }
}