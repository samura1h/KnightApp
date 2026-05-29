package command;

import model.Knight;
import model.Rank;
import service.EmailService;
import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.Scanner;

public class CreateOrLoadKnightCommand implements Command {
    private KnightManager manager;
    private Scanner scanner;

    public CreateOrLoadKnightCommand(KnightManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- MENU: CREATE OR LOAD ---");
        System.out.println("1. Create a new knight");
        System.out.println("2. Load an existing one");
        System.out.print("Your choice: ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            createProcess();
        } else if (choice.equals("2")) {
            loadProcess();
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void createProcess() {
        System.out.println(">>> Creating a new knight");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Order: ");
        String orden = scanner.nextLine();

        Rank rank = Rank.NOVICE;
        System.out.print("Rank (1-Novice, 2-Veteran, 3-Master, 4-Grand Master): ");
        try {
            int r = Integer.parseInt(scanner.nextLine());
            if (r == 2) rank = Rank.VETERAN;
            if (r == 3) rank = Rank.MASTER;
            if (r == 4) rank = Rank.GRAND_MASTER;
        } catch (Exception e) {
            // Ігнор
        }

        Knight k = new Knight(name, orden, rank);
        manager.addKnight(k);
        System.out.println("Knight successfully created!");

        // ЛОГ: Успішне створення
        LoggerService.info("Created a new knight: " + name + " (Order: " + orden + ", Rank: " + rank + ")"); // <--- ЛОГ
    }

    private void loadProcess() {
        if (manager.getAllKnights().isEmpty()) {
            System.out.println("Knight list in memory is empty.");
            System.out.print("Load data from file (knights.txt)? (y/n): ");
            String ans = scanner.nextLine();

            if (ans.equalsIgnoreCase("y")) {
                manager.loadFromDisk();
                LoggerService.info("User initiated loading from disk."); // <--- ЛОГ
            } else {
                return;
            }
        }

        var all = manager.getAllKnights();
        if (all.isEmpty()) {
            System.out.println("No knights to load.");
            return;
        }

        System.out.println("--- List of saved knights ---");
        for (Knight k : all.values()) {
            System.out.println("ID: " + k.getId() + " | " + k.getName() + " | " + k.getRank());
        }
        System.out.print("Enter ID to select: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            manager.setActiveKnight(id);

            if (manager.getActiveKnight() != null) {
                System.out.println("Knight selected: " + manager.getActiveKnight().getName());
                // ЛОГ: Успішний вибір
                LoggerService.info("Loaded/Selected knight ID: " + id + " Name: " + manager.getActiveKnight().getName()); // <--- ЛОГ
            } else {
                System.out.println("ID not found.");
                LoggerService.info("Failed attempt to load knight: ID " + id + " not found"); // <--- ЛОГ
            }

        } catch (Exception e) {
            System.out.println("Input error.");
            EmailService.sendAsync(
                    "Error in CreateOrLoadKnightCommand",
                    "An error occurred:\n" + e.toString()
            );
            LoggerService.error("ID input error during loading: " + e.getMessage()); // <--- ЛОГ
        }
    }
}