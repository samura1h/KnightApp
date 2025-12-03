package command; // Пакет команд

import service.KnightManager; // Імпорт менеджера
import java.util.Scanner; // Імпорт сканера

// Окрема команда виключно для вибору лицаря (пункт 3)
public class SelectKnightCommand implements Command {
    private KnightManager manager; // Менеджер для доступу до даних
    private Scanner scanner; // Сканер для вводу

    // Конструктор
    public SelectKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager; // Зберігаємо менеджер
        this.scanner = scanner; // Зберігаємо сканер
    }

    @Override
    public void execute() { // Метод виконання
        System.out.println("\n--- ШВИДКИЙ ВИБІР ЛИЦАРЯ ---"); // Заголовок

        var allKnights = manager.getAllKnights(); // Отримуємо всіх лицарів

        if (allKnights.isEmpty()) { // Перевіряємо чи є хтось
            System.out.println("У базі немає жодного лицаря."); // Якщо нікого немає
            return; // Завершуємо роботу команди
        }

        // Виводимо гарний список
        for (var k : allKnights.values()) { // Перебираємо всіх
            // Друкуємо ID, Ім'я та Ранг
            System.out.printf("ID: %d | %s (%s)\n", k.getId(), k.getName(), k.getRank());
        }

        System.out.print("Введіть ID для активації: "); // Просимо користувача ввести ID

        try { // Блок перевірки на помилки
            String line = scanner.nextLine(); // Читаємо рядок
            int id = Integer.parseInt(line); // Перетворюємо на число

            manager.setActiveKnight(id); // Кажемо менеджеру встановити цього лицаря активним

            if (manager.getActiveKnight() != null && manager.getActiveKnight().getId() == id) { // Перевіряємо чи вийшло
                System.out.println("Готово! Ви граєте за: " + manager.getActiveKnight().getName()); // Успіх
            } else {
                System.out.println("Лицаря з таким ID не знайдено."); // Якщо ID невірний
            }
        } catch (NumberFormatException e) { // Якщо ввели букви
            System.out.println("Це не число!"); // Помилка формату
        }
    }
}