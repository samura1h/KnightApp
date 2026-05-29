package command;

import model.equipment.Ammunition;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SortEquipmentCommand implements Command {
    private KnightManager km;
    private EquipmentRepository repo;
    private Scanner scanner;

    public SortEquipmentCommand(KnightManager km, EquipmentRepository repo, Scanner scanner) {
        this.km = km;
        this.repo = repo;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("\n--- Sort Ammunition by Weight ---");
        System.out.println("1. Sort active knight's inventory");
        System.out.println("2. Sort general catalog");
        System.out.print("Your choice: ");

        String choice = scanner.nextLine();
        List<Ammunition> listToSort = null;
        String context = ""; // Для логу (що ми сортуємо)

        if (choice.equals("1")) { // Лицар
            if (km.getActiveKnight() == null) {
                System.out.println("ERROR: Knight is not selected.");
                LoggerService.info("Failed sort attempt: knight not selected."); // <--- ЛОГ
                return;
            }
            listToSort = km.getActiveKnight().getEquipment();
            context = "inventory of knight " + km.getActiveKnight().getName();
        } else if (choice.equals("2")) { // Каталог
            listToSort = new ArrayList<>(repo.getAll());
            context = "general shop catalog";
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (listToSort.isEmpty()) {
            System.out.println("The list is empty.");
            LoggerService.info("Attempted to sort an empty list (" + context + ")."); // <--- ЛОГ
            return;
        }

        Collections.sort(listToSort); // Сортуємо

        System.out.println("--- Sort Result (from lightest to heaviest) ---");
        listToSort.forEach(System.out::println);

        // ЛОГ: Успішне сортування
        LoggerService.info("Sorted " + context); // <--- ЛОГ
    }
}