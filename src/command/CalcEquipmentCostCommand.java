package command; // Оголошення пакету

import model.equipment.Ammunition; // Імпорт моделі амуніції
import repository.EquipmentRepository; // Імпорт репозиторію (каталогу)
import service.KnightManager; // Імпорт менеджера лицарів
import java.util.List; // Імпорт списку
import java.util.Scanner; // Імпорт сканера

// Клас для розрахунку вартості (Пункт 6)
public class CalcEquipmentCostCommand implements Command {
    private KnightManager km; // Менеджер лицарів
    private EquipmentRepository repo; // Репозиторій амуніції
    private Scanner scanner; // Сканер

    // Конструктор
    public CalcEquipmentCostCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Розрахунок вартості ---"); // Заголовок
        System.out.println("1. Розрахувати вартість речей активного лицаря"); // Опція 1
        System.out.println("2. Розрахувати вартість всього каталогу (магазину)"); // Опція 2
        System.out.print("Ваш вибір: "); // Запит вводу

        String choice = scanner.nextLine(); // Читаємо вибір

        List<Ammunition> listToCalc = null; // Змінна для списку, який будемо рахувати

        if (choice.equals("1")) { // Якщо обрали Лицаря
            if (km.getActiveKnight() == null) { // Перевіряємо, чи він обраний
                System.out.println("ПОМИЛКА: Лицар не обраний.");
                return;
            }
            listToCalc = km.getActiveKnight().getEquipment(); // Беремо речі лицаря
            System.out.println(">>> Обрано: Інвентар лицаря");
        } else if (choice.equals("2")) { // Якщо обрали Каталог
            listToCalc = repo.getAll(); // Беремо весь каталог
            System.out.println(">>> Обрано: Загальний каталог");
        } else {
            System.out.println("Невірний вибір."); // Помилка
            return;
        }
        // Розрахунок суми через Stream API
        if (listToCalc.isEmpty()) {
            System.out.println("Список порожній. Вартість: 0.0");
        } else {
            double total = listToCalc.stream().mapToDouble(Ammunition::getPrice).sum(); // Сумуємо ціни
            System.out.println("Загальна вартість: " + total + " золота."); // Виводимо результат
        }
    }
}