package command;

import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.List;
import java.util.Scanner;
import service.EmailService;

public class FindEquipmentByPriceCommand implements Command {
    private KnightManager km;
    private EquipmentRepository repo;
    private Scanner scanner;

    public FindEquipmentByPriceCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Search by Price ---");
        System.out.println("1. Search in knight's inventory");
        System.out.println("2. Search in shop catalog");
        System.out.print("Your choice: ");

        String choice = scanner.nextLine();
        List<Ammunition> searchList = null;

        if (choice.equals("1")) {
            if (km.getActiveKnight() == null) {
                System.out.println("ERROR: Knight is not selected.");
                return;
            }
            searchList = km.getActiveKnight().getEquipment();
        } else if (choice.equals("2")) {
            searchList = repo.getAll();
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        try {
            System.out.print("Enter minimum price: ");
            double min = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter maximum price: ");
            double max = Double.parseDouble(scanner.nextLine());

            // ЛОГ ПОШУКУ
            LoggerService.info("Searching for items in price range: " + min + " - " + max); // <--- ЛОГ

            System.out.println("--- Found Items ---");
            boolean found = false;

            for (Ammunition item : searchList) {
                if (item.getPrice() >= min && item.getPrice() <= max) {
                    System.out.println(item);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("Nothing found.");
                LoggerService.info("Search yielded no results."); // <--- ЛОГ
            }

        } catch (NumberFormatException e) {
            EmailService.sendAsync(
                    "Error in FindEquipmentByPriceCommand",
                    "An error occurred:\n" + e.toString()
            );
            System.out.println("Error: please enter a number.");
        }
    }
}