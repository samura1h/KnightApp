package command;

import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.List;
import java.util.Scanner;
import service.EmailService;

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
        if (km.getActiveKnight() == null) {
            System.out.println("ERROR: Please select an active knight first!");
            LoggerService.info("Attempted to equip without an active knight."); // <--- ЛОГ
            return;
        }

        double current = km.getActiveKnight().getCurrentWeight();
        double max = km.getActiveKnight().getMaxWeightCapacity();
        System.out.printf("--- Ammunition Catalog (Weight: %.2f / %.2f kg) ---\n", current, max);

        List<Ammunition> items = repo.getAll();

        for (int i = 0; i < items.size(); i++) {
            Ammunition item = items.get(i);
            String typeName = item.getClass().getSimpleName();
            System.out.printf("%d. [%-12s] %s (Weight: %.2f)\n", (i + 1), typeName, item.getName(), item.getWeight());
        }

        System.out.print("Enter item number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine()) - 1;

            if (idx >= 0 && idx < items.size()) {
                Ammunition itemToEquip = items.get(idx);
                boolean success = km.getActiveKnight().equip(itemToEquip);

                if (success) {
                    System.out.println("Success! " + itemToEquip.getName() + " added.");
                    // ЛОГ: УСПІШНА ПОКУПКА
                    LoggerService.info("Knight " + km.getActiveKnight().getName() + " equipped item: " + itemToEquip.getName()); // <--- ЛОГ
                } else {
                    // ЛОГ: НЕВДАЧА (ВАГА АБО ТИП)
                    LoggerService.info("Equip failure: " + itemToEquip.getName() + " (Weight limit or type duplicate)"); // <--- ЛОГ
                }
            } else {
                System.out.println("Invalid number.");
            }
        } catch (NumberFormatException e) {
            EmailService.sendAsync(
                    "Error in EquipKnightCommand",
                    "An error occurred:\n" + e.toString()
            );
            System.out.println("Error: please enter a number.");
        }
    }
}