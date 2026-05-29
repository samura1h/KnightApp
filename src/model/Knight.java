package model; // Пакет, у якому знаходиться клас (частина моделі даних)

import model.equipment.Ammunition; // Імпорт базового класу амуніції
import model.equipment.Armor; // Імпорт класу броні (потрібен для розрахунку захисту)
import model.equipment.Weapon; // Імпорт класу зброї
import java.util.ArrayList; // Реалізація динамічного масиву
import java.util.List; // Інтерфейс списку

/**
 * Клас Knight представляє лицаря з його характеристиками та екіпіруванням.
 * Дані зберігаються в SQLite базі даних.
 */
public class Knight {

    private int id; // Унікальний номер конкретного лицаря (генерується SQLite)
    private String name; // Ім'я лицаря
    private String orden; // Назва ордену (фракції)
    private Rank rank; // Ранг (рівень досвіду)
    private int strength; // Сила (впливає на те, скільки ваги можна нести)
    private int baseDefense; // Базовий захист (без броні)

    // Список предметів, які носить лицар (Інвентар)
    private List<Ammunition> equipment;

    /**
     * Конструктор для створення НОВОГО лицаря (без заданого ID).
     * ID буде присвоєно базою даних при збереженні.
     */
    public Knight(String name, String orden, Rank rank) {
        this.name = name;
        this.orden = orden;
        this.rank = rank;
        this.strength = 60; // Сила за замовчуванням
        this.baseDefense = 20; // Базовий захист за замовчуванням
        this.equipment = new ArrayList<>();
    }

    /**
     * Конструктор для завантаження ІСНУЮЧОГО лицаря з бази даних (з ID).
     */
    public Knight(int id, String name, String orden, Rank rank) {
        this(name, orden, rank);
        this.id = id;
    }

    // Розрахунок максимальної ваги відповідно до рангу лицаря
    public double getMaxWeightCapacity() {
        if (rank == null) return 18.0;
        switch (rank) {
            case VETERAN: return 20.0;
            case MASTER: return 22.0;
            case GRAND_MASTER: return 24.0;
            case NOVICE:
            default: return 18.0;
        }
    }

    // Розрахунок поточної ваги всіх речей
    public double getCurrentWeight() {
        // Використовуємо Stream API: проходимо по списку -> беремо вагу кожного предмета -> сумуємо
        return equipment.stream().mapToDouble(Ammunition::getWeight).sum();
    }

    // Розрахунок загального захисту
    public int getTotalDefense() {
        return baseDefense + equipment.stream() // До базового захисту додаємо суму броні
                .filter(a -> a instanceof Armor) // Фільтр: беремо тільки ті предмети, які є Бронею (Armor)
                .mapToInt(a -> ((Armor) a).getDefense()) // Перетворюємо предмет на Armor і беремо його показник захисту
                .sum(); // Сумуємо весь захист
    }

    public boolean equip(Ammunition newItem) {
        // Перевіряємо, чи не перевищено ліміт ваги
        if (getCurrentWeight() + newItem.getWeight() > getMaxWeightCapacity()) {
            System.out.println("FAILURE: Too heavy! Weight limit exceeded.");
            return false;
        }

        // Перевіряємо правила для зброї (максимум 2 зброї)
        if (newItem instanceof Weapon) {
            long weaponCount = equipment.stream().filter(a -> a instanceof Weapon).count();
            if (weaponCount >= 2) {
                System.out.println("FAILURE: You can equip at most 2 weapons!");
                return false;
            }
        } 
        // Перевіряємо правила для броні (1 шолом, 1 нагрудник, 1 поножі)
        else if (newItem instanceof Armor) {
            for (Ammunition existingItem : this.equipment) {
                if (existingItem.getClass().equals(newItem.getClass())) {
                    System.out.println("FAILURE: You already have a " + newItem.getClass().getSimpleName() + " equipped!");
                    return false;
                }
            }
        }
        // Для інших типів амуніції
        else {
            for (Ammunition existingItem : this.equipment) {
                if (existingItem.getClass().equals(newItem.getClass())) {
                    System.out.println("FAILURE: You already have an item of type " + newItem.getClass().getSimpleName() + "!");
                    return false;
                }
            }
        }

        // Додаємо предмет в екіпірування
        this.equipment.add(newItem);
        return true;
    }

    // --- ГЕТТЕРИ ТА СЕТТЕРИ ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // Сеттер для ID (встановлюється базою даних)
    public String getName() { return name; }

    // Метод для отримання рангу
    public Rank getRank() { return rank; }

    public List<Ammunition> getEquipment() { return equipment; }

    public String getOrden() {
        return orden;
    }
    // Перевизначення методу toString для гарного виводу інформації про лицаря в консоль
    @Override
    public String toString() {
        // Форматуємо рядок з підстановкою значень змінних
        return String.format("ID:%d | %s (%s, %s) | Weight: %.2f/%.2f",
                id, name, orden, rank, getCurrentWeight(), getMaxWeightCapacity());
    }
}