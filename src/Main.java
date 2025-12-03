import command.*;
import repository.EquipmentRepository;
import repository.KnightRepository;
import service.EmailService; // Імпорт нашого нового сервісу
import service.KnightManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. ВІДПРАВКА ПРИ СТАРТІ
        // Використовуємо sendAsync, щоб програма запустилася миттєво, а лист летів фоном
        EmailService.sendAsync("Knight App Started", "Програма успішно запущена.");

        // 2. ВІДПРАВКА ПРИ ВИХОДІ
        // Використовуємо звичайний send, щоб програма не закрилася раніше, ніж лист полетить
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Завершення роботи...");
            EmailService.send("Knight App Stopped", "Програма завершила роботу.");
        }));

        Scanner scanner = new Scanner(System.in);
        System.out.println(">>> Завантаження системи...");

        EquipmentRepository equipRepo = new EquipmentRepository();
        KnightRepository knightRepo = new KnightRepository();
        KnightManager knightManager = new KnightManager(knightRepo, equipRepo);

        MenuInvoker menu = new MenuInvoker(scanner);

        // --- ВАШІ КОМАНДИ (Без змін) ---
        menu.register("1. Створити або Завантажити лицаря", new CreateOrLoadKnightCommand(knightManager, scanner));
        menu.register("2. Видалити лицаря", new DeleteKnightCommand(knightManager, scanner));
        menu.register("3. Швидкий вибір активного лицаря", new SelectKnightCommand(knightManager, scanner));
        menu.register("4. Екіпірувати лицаря", new EquipKnightCommand(knightManager, equipRepo, scanner));
        menu.register("5. Статус лицаря", new ShowKnightStatusCommand(knightManager));
        menu.register("6. Вартість спорядження", new CalcEquipmentCostCommand(knightManager, equipRepo, scanner));
        menu.register("7. Сортувати амуніцію", new SortEquipmentCommand(knightManager, equipRepo, scanner));
        menu.register("8. Знайти за ціною", new FindEquipmentByPriceCommand(knightManager, equipRepo, scanner));
        menu.register("9. Перезавантажити систему", new ReloadSystemCommand(knightManager));
        menu.register("10. Довідка", new HelpCommand());

        menu.register("11. Вихід", () -> {
            System.out.println("Збереження...");
            knightManager.saveAll();
            // ShutdownHook спрацює автоматично після цього рядка
            System.exit(0);
        });

        menu.run();
    }
}