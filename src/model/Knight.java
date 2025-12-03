package model; // Пакет, у якому знаходиться клас (частина моделі даних)

import model.equipment.Ammunition; // Імпорт базового класу амуніції
import model.equipment.Armor; // Імпорт класу броні (потрібен для розрахунку захисту)
import java.io.Serializable; // Інтерфейс для збереження об'єкта у файл
import java.util.ArrayList; // Реалізація динамічного масиву
import java.util.List; // Інтерфейс списку

// Клас реалізує Serializable, щоб його можна було записати у файл knights.dat
public class Knight implements Serializable {
    // Статична змінна (спільна для всіх лицарів). Відповідає за генерацію унікальних ID.
    private static int idCounter = 1;

    private int id; // Унікальний номер конкретного лицаря
    private String name; // Ім'я лицаря
    private String orden; // Назва ордену (фракції)
    private Rank rank; // Ранг (рівень досвіду)
    private int strength; // Сила (впливає на те, скільки ваги можна нести)
    private int baseDefense; // Базовий захист (без броні)
    // Поле money видалено, оскільки баланс більше не потрібен

    // Список предметів, які носить лицар (Інвентар)
    private List<Ammunition> equipment;

    // --- ОНОВЛЕНИЙ КОНСТРУКТОР ---
    // Викликається при створенні нового об'єкта "new Knight(...)".
    public Knight(String name, String orden, Rank rank) {
        this.id = idCounter++; // Присвоюємо поточний номер і збільшуємо лічильник для наступного лицаря
        this.name = name; // Зберігаємо ім'я
        this.orden = orden; // Зберігаємо орден
        this.rank = rank; // Зберігаємо ранг

        // Фіксовані характеристики для балансу (можна змінити в майбутньому)
        this.strength = 60; // Сила за замовчуванням
        this.baseDefense = 20; // Базовий захист за замовчуванням

        // Дуже важливо! Ініціалізуємо список, щоб він не був null (створюємо пустий інвентар)
        this.equipment = new ArrayList<>();
    }

    // Розрахунок максимальної ваги: Сила поділена на коефіцієнт 3.25
    public double getMaxWeightCapacity() { return strength / 3.25; }

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
        // 1. Знайди, як у тебе називається змінна макс. ваги (можливо maxWeight?)
        //    Також замість getCurrentWeight() може бути просто змінна currentWeight
        if (getCurrentWeight() + newItem.getWeight() > getMaxWeightCapacity()) {
            System.out.println("НЕВДАЧА: Занадто важко! Ліміт ваги перевищено.");
            return false;
        }

        // 2. Знайди, як у тебе називається список речей (можливо equipment?)
        //    Заміни 'ammunition' на цю назву
        for (Ammunition existingItem : this.equipment) { //

            if (existingItem.getClass().equals(newItem.getClass())) {
                System.out.println("НЕВДАЧА: Ви вже маєте предмет типу " + newItem.getClass().getSimpleName() + "!");
                return false;
            }
        }

        // 3. Тут теж заміни 'ammunition' на свою назву списку
        this.equipment.add(newItem); // <-- ТУТ ТЕЖ ЗМІНИ НАЗВУ
        return true;
    }

    // --- ГЕТТЕРИ (Методи для доступу до полів) ---
    public int getId() { return id; } // Повертає ID
    public String getName() { return name; } // Повертає ім'я

    // !!! ОСЬ ЦЕЙ МЕТОД Я ДОДАВ (ЙОГО НЕ ВИСТАЧАЛО) !!!
    public Rank getRank() { return rank; } // Повертає ранг лицаря

    public List<Ammunition> getEquipment() { return equipment; } // Повертає список речей

    // Перевизначення методу toString для гарного виводу інформації про лицаря в консоль
    @Override
    public String toString() {
        // Форматуємо рядок з підстановкою значень змінних
        return String.format("ID:%d | %s (%s, %s) | Вага: %.2f/%.2f",
                id, name, orden, rank, getCurrentWeight(), getMaxWeightCapacity());
    }
}