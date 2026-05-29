package command;

import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService;
import service.EmailService;   // <--- ДОДАНО
import java.util.List;
import java.util.Scanner;

public class CalcEquipmentCostCommand implements Command {
    private KnightManager km;
    private EquipmentRepository repo;
    private Scanner scanner;

    public CalcEquipmentCostCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {

        try {

            System.out.println("\n--- Cost Calculation ---");
            System.out.println("1. Calculate cost of active knight's equipment");
            System.out.println("2. Calculate cost of the entire catalog (shop)");
            System.out.print("Your choice: ");

            String choice = scanner.nextLine();
            List<Ammunition> listToCalc = null;

            if (choice.equals("1")) {
                if (km.getActiveKnight() == null) {
                    System.out.println("ERROR: Knight is not selected.");
                    return;
                }
                listToCalc = km.getActiveKnight().getEquipment();
                System.out.println(">>> Selected: Knight's Inventory");
            } else if (choice.equals("2")) {
                listToCalc = repo.getAll();
                System.out.println(">>> Selected: General Catalog");
            } else {
                System.out.println("Invalid choice.");
                return;
            }

            if (listToCalc.isEmpty()) {
                System.out.println("The list is empty. Cost: 0.0");
            } else {
                double total = listToCalc.stream().mapToDouble(Ammunition::getPrice).sum();
                System.out.println("Total cost: " + total + " gold.");

                // Лог в систему
                LoggerService.info("Cost calculation performed. Total: " + total);
            }

        } catch (Exception e) {
            // === ЛОГУЄМО ПОМИЛКУ ===
            LoggerService.error("Error during cost calculation: " + e.getMessage());
            // === ВІДПРАВКА EMAIL ===
            EmailService.sendAsync(
                    "Error in CalcEquipmentCostCommand",
                    "An error occurred:\n" + e.toString()
            );
            System.out.println("An error occurred! Information sent to email.");
        }
    }
}
