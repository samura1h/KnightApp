package command; // Пакет

import model.equipment.Ammunition; // Модель
import repository.EquipmentRepository; // Репозиторій
import service.KnightManager; // Менеджер
import java.util.List; // Список
import java.util.Scanner; // Сканер

// Команда пошуку за ціною (Пункт 8)
public class FindEquipmentByPriceCommand implements Command {
    private KnightManager km; // Менеджер
    private EquipmentRepository repo; // Репозиторій
    private Scanner scanner; // Сканер

    // Оновили конструктор: додали репозиторій
    public FindEquipmentByPriceCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Пошук за ціною ---"); // Заголовок
        System.out.println("1. Шукати в інвентарі лицаря"); // Опція 1
        System.out.println("2. Шукати в каталозі магазину"); // Опція 2
        System.out.print("Ваш вибір: "); // Ввід

        String choice = scanner.nextLine(); // Читання
        List<Ammunition> searchList = null; // Де шукаємо

        if (choice.equals("1")) { // Лицар
            if (km.getActiveKnight() == null) {
                System.out.println("ПОМИЛКА: Лицар не обраний.");
                return;
            }
            searchList = km.getActiveKnight().getEquipment();
        } else if (choice.equals("2")) { // Каталог
            searchList = repo.getAll();
        } else {
            System.out.println("Невірний вибір.");
            return;
        }

        try {
            System.out.print("Введіть мінімальну ціну: "); // Мін
            double min = Double.parseDouble(scanner.nextLine());
            System.out.print("Введіть максимальну ціну: "); // Макс
            double max = Double.parseDouble(scanner.nextLine());

            System.out.println("--- Знайдені предмети ---");
            boolean found = false; // Прапорець

            for (Ammunition item : searchList) { // Перебір
                if (item.getPrice() >= min && item.getPrice() <= max) { // Умова
                    System.out.println(item); // Вивід
                    found = true;
                }
            }

            if (!found) System.out.println("Нічого не знайдено."); // Якщо пусто

        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть число.");
        }
    }
}