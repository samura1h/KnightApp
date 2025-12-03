package command;

import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import java.util.List;
import java.util.Scanner;

public class EquipKnightCommand implements Command {
    private KnightManager km;
    private EquipmentRepository repo;
    private Scanner sc;

    public EquipKnightCommand(KnightManager km, EquipmentRepository repo, Scanner sc) {
        this.km = km;
        this.repo = repo;
        this.sc = sc;
    }

    @Override
    public void execute() {
        // Перевірка, чи взагалі є кому видавати зброю
        if (km.getActiveKnight() == null) {
            System.out.println("ПОМИЛКА: Спочатку оберіть активного лицаря!");
            return;
        }

        double current = km.getActiveKnight().getCurrentWeight();
        double max = km.getActiveKnight().getMaxWeightCapacity();
        System.out.printf("--- Каталог Амуніції (Вага: %.2f / %.2f кг) ---\n", current, max);

        List<Ammunition> items = repo.getAll();

        // Виводимо список доступних речей
        for (int i = 0; i < items.size(); i++) {
            Ammunition item = items.get(i);

            // --- ОТРИМАННЯ НАЗВИ КЛАСУ ---
            // item.getClass() отримує повну інформацію про клас (наприклад: class model.equipment.Helmet)
            // .getSimpleName() бере лише коротку назву без пакетів (наприклад: "Helmet", "Sword")
            String typeName = item.getClass().getSimpleName();

            // --- ВИВІД З ФОРМАТУВАННЯМ ---
            // %-12s означає: виділити під рядок 12 символів і вирівняти по лівому краю.
            // Це потрібно, щоб стовпчики в консолі були рівними, навіть якщо слова різної довжини.
            // Приклад: [Helmet      ], [Sword       ]
            System.out.printf("%d. [%-12s] %s (Вага: %.2f)\n", (i + 1), typeName, item.getName(), item.getWeight());
        }

        System.out.print("Введіть номер предмета: ");
        try {
            int idx = Integer.parseInt(sc.nextLine()) - 1;

            if (idx >= 0 && idx < items.size()) {
                Ammunition itemToEquip = items.get(idx);

                // Викликаємо метод equip.Він повертає true/false не тільки через вагу,
                // а й через перевірку на дублікати типів.
                boolean success = km.getActiveKnight().equip(itemToEquip);

                if (success) {
                    System.out.println("Успішно! " + itemToEquip.getName() + " додано.");
                }
                // Якщо success == false, повідомлення про помилку виведе сам клас Knight
            } else {
                System.out.println("Невірний номер.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть число.");
        }
    }
}