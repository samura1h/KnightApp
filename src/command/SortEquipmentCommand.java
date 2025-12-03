package command; // Пакет

import model.equipment.Ammunition; // Модель
import repository.EquipmentRepository; // Репозиторій
import service.KnightManager; // Менеджер
import java.util.ArrayList; // АрайЛіст (для копіювання)
import java.util.Collections; // Колекції (для сортування)
import java.util.List; // Інтерфейс списку
import java.util.Scanner; // Сканер

// Команда сортування (Пункт 7)
public class SortEquipmentCommand implements Command {
    private KnightManager km; // Менеджер
    private EquipmentRepository repo; // Репозиторій
    private Scanner scanner; // Сканер

    // Оновили конструктор: додали репозиторій та сканер
    public SortEquipmentCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Сортування амуніції за вагою ---"); // Заголовок
        System.out.println("1. Сортувати інвентар активного лицаря"); // Опція 1
        System.out.println("2. Сортувати загальний каталог"); // Опція 2
        System.out.print("Ваш вибір: "); // Ввід

        String choice = scanner.nextLine(); // Читання
        List<Ammunition> listToSort = null; // Список для сортування

        if (choice.equals("1")) { // Лицар
            if (km.getActiveKnight() == null) {
                System.out.println("ПОМИЛКА: Лицар не обраний.");
                return;
            }
            // Беремо реальний список лицаря (сортування змінить порядок у нього в кишені)
            listToSort = km.getActiveKnight().getEquipment();
        } else if (choice.equals("2")) { // Каталог
            // ВАЖЛИВО: Робимо копію каталогу (new ArrayList), щоб не поламати порядок ID для магазину
            listToSort = new ArrayList<>(repo.getAll());
        } else {
            System.out.println("Невірний вибір.");
            return;
        }

        if (listToSort.isEmpty()) {
            System.out.println("Список порожній.");
            return;
        }

        Collections.sort(listToSort); // Сортуємо (використовує compareTo в Ammunition)

        System.out.println("--- Результат сортування (від легшого до важчого) ---");
        listToSort.forEach(System.out::println); // Виводимо
    }
}