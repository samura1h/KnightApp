package command;

import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.Scanner;
import service.EmailService;

public class SelectKnightCommand implements Command {
    private KnightManager manager;
    private Scanner scanner;

    public SelectKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- QUICK KNIGHT SELECTION ---");

        var allKnights = manager.getAllKnights();

        if (allKnights.isEmpty()) {
            System.out.println("There are no knights in the database.");
            return;
        }

        for (var k : allKnights.values()) {
            System.out.printf("ID: %d | %s (%s)\n", k.getId(), k.getName(), k.getRank());
        }

        System.out.print("Enter ID to activate: ");

        try {
            String line = scanner.nextLine();
            int id = Integer.parseInt(line);

            manager.setActiveKnight(id);

            if (manager.getActiveKnight() != null && manager.getActiveKnight().getId() == id) {
                System.out.println("Done! You are playing as: " + manager.getActiveKnight().getName());
                // ЛОГ
                LoggerService.info("Active knight changed to: " + manager.getActiveKnight().getName()); // <--- ЛОГ
            } else {
                System.out.println("Knight with this ID not found.");
            }
        } catch (NumberFormatException e) {
            EmailService.sendAsync(
                    "Error in SelectKnightCommand",
                    "An error occurred:\n" + e.toString()
            );
            System.out.println("This is not a number!");
        }
    }
}