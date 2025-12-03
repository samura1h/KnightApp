package command; // Пакет для команд

import model.Knight; // Імпорт моделі Лицаря
import model.Rank; // Імпорт рангу
import service.KnightManager; // Імпорт менеджера
import java.util.Scanner; // Імпорт сканера

// Клас команди для Пункту 1: Створити або Завантажити
public class CreateOrLoadKnightCommand implements Command {
    private KnightManager manager; // Поле для менеджера (бізнес-логіка)
    private Scanner scanner; // Поле для зчитування вводу
    // Конструктор класу
    public CreateOrLoadKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager; // Ініціалізуємо менеджер
        this.scanner = scanner; // Ініціалізуємо сканер
    }
    @Override
    public void execute() { // Метод виконання команди
        System.out.println("\n--- МЕНЮ: СТВОРИТИ АБО ЗАВАНТАЖИТИ ---"); // Заголовок
        System.out.println("1. Створити нового лицаря"); // Варіант 1
        System.out.println("2. Завантажити існуючого"); // Варіант 2
        System.out.print("Ваш вибір: "); // Питаємо користувача

        String choice = scanner.nextLine(); // Читаємо вибір

        if (choice.equals("1")) { // Якщо обрали 1
            createProcess(); // Запускаємо процес створення
        } else if (choice.equals("2")) { // Якщо обрали 2
            loadProcess(); // Запускаємо процес завантаження
        } else { // Якщо ввели щось інше
            System.out.println("Невірний вибір."); // Повідомляємо про помилку
        }
    }
    // --- ПРИВАТНИЙ МЕТОД: СТВОРЕННЯ ---
    private void createProcess() {
        System.out.println(">>> Створення нового лицаря"); // Інформаційне повідомлення
        System.out.print("Ім'я: "); // Питаємо ім'я
        String name = scanner.nextLine(); // Читаємо ім'я
        System.out.print("Орден: "); // Питаємо орден
        String orden = scanner.nextLine(); // Читаємо орден

        Rank rank = Rank.NOVICE; // Ранг за замовчуванням
        System.out.print("Ранг (1-Novice, 2-Veteran, 3-Master): "); // Питаємо ранг
        try { // Блок обробки помилок (якщо введуть не число)
            int r = Integer.parseInt(scanner.nextLine()); // Читаємо число
            if (r == 2) rank = Rank.VETERAN; // Якщо 2 - Ветеран
            if (r == 3) rank = Rank.MASTER; // Якщо 3 - Майстер
        } catch (Exception e) {} // Ігноруємо помилки

        Knight k = new Knight(name, orden, rank); // Створюємо об'єкт лицаря
        manager.addKnight(k); // Додаємо його в систему через менеджер
        System.out.println("Лицар успішно створений!"); // Повідомляємо про успіх
    }
    // --- ПРИВАТНИЙ МЕТОД: ЗАВАНТАЖЕННЯ (LOAD) ---
    private void loadProcess() {
        // Перевіряємо, чи список в пам'яті пустий
        if (manager.getAllKnights().isEmpty()) {
            System.out.println("Список лицарів в пам'яті пустий."); // Повідомляємо
            // Питаємо, чи підвантажити файл
            System.out.print("Завантажити дані з файлу (knights.txt)? (y/n): ");
            String ans = scanner.nextLine(); // Читаємо відповідь

            if (ans.equalsIgnoreCase("y")) { // Якщо 'y'
                manager.loadFromDisk(); // Кажемо менеджеру завантажити з диску
            } else { // Якщо 'n' або інше
                System.out.println("Скасовано."); // Скасовуємо
                return; // Виходимо
            }
        }
        // Беремо список знову (раптом він заповнився після завантаження)
        var all = manager.getAllKnights();
        if (all.isEmpty()) { // Якщо все одно пустий
            System.out.println("Немає кого завантажувати."); // Кажемо про це
            return; // Виходимо
        }
        // Виводимо список доступних
        System.out.println("--- Список збережених лицарів ---");
        for (Knight k : all.values()) { // Перебираємо всіх
            // Виводимо ID, Ім'я та Ранг (метод getRank() вже має бути в моделі)
            System.out.println("ID: " + k.getId() + " | " + k.getName() + " | " + k.getRank());
        }
        System.out.print("Введіть ID для вибору: "); // Просимо ID
        try {
            int id = Integer.parseInt(scanner.nextLine()); // Читаємо ID
            manager.setActiveKnight(id); // Встановлюємо активного
            // Перевіряємо результат
            if (manager.getActiveKnight() != null) {
                System.out.println("Лицар обраний: " + manager.getActiveKnight().getName()); // Успіх
            } else {
                System.out.println("ID не знайдено."); // Помилка
            }
        } catch (Exception e) { // Якщо ввели букви
            System.out.println("Помилка вводу."); // Помилка
        }
    }
}