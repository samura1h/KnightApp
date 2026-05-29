package command;

import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.Scanner;
import service.EmailService;

public class DeleteKnightCommand implements Command {
    private KnightManager manager;
    private Scanner scanner;

    public DeleteKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        var all = manager.getAllKnights();

        if (all.isEmpty()) {
            System.out.println("The list of knights is empty.");
            return;
        }

        System.out.println("--- Delete Knight ---");
        all.values().forEach(k -> System.out.println("ID: " + k.getId() + " | " + k.getName()));

        System.out.print("Enter the ID of the knight to delete: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            if (all.containsKey(id)) {
                String deletedName = all.get(id).getName();
                manager.removeKnight(id);
                System.out.println("Knight deleted.");

                // ЛОГ: Видалення
                LoggerService.info("Deleted knight: ID " + id + ", Name: " + deletedName); // <--- ЛОГ
            } else {
                System.out.println("Knight with this ID not found.");
                LoggerService.info("Attempted to delete non-existent ID: " + id); // <--- ЛОГ
            }
        } catch (NumberFormatException e) {
            EmailService.sendAsync(
                    "Error in DeleteKnightCommand",
                    "An error occurred:\n" + e.toString()
            );
            System.out.println("Error: please enter a number.");
        }
    }
}