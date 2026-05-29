package command;

import service.EmailService;
import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР

/**
 * Команда, яка виводить повний статус активного лицаря.
 */
public class ShowKnightStatusCommand implements Command {
    private KnightManager km;

    public ShowKnightStatusCommand(KnightManager km) {
        this.km = km;
    }

    @Override
    public void execute() {
        // Перевіряємо, чи взагалі обрано лицаря
        if (km.getActiveKnight() != null) {
            System.out.println("\n--- KNIGHT STATUS ---");
            System.out.println(km.getActiveKnight());

            System.out.println("--- Equipment List ---");

            if (km.getActiveKnight().getEquipment().isEmpty()) {
                System.out.println("(Empty)");
            } else {
                km.getActiveKnight().getEquipment().forEach(System.out::println);
            }

            // ЛОГ: Успішний перегляд
            LoggerService.info("Viewed knight status: " + km.getActiveKnight().getName()); // <--- ЛОГ

        } else {
            System.out.println("ERROR: No active knight selected. Use option 3.");
            // ЛОГ: Спроба перегляду без вибору
            LoggerService.info("Attempted to view status without active knight."); // <--- ЛОГ
        }
    }
}