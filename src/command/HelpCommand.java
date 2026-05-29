package command;

import service.LoggerService; // <--- ЛОГЕР

public class HelpCommand implements Command {
    @Override
    public void execute() {
        LoggerService.info("User viewed help."); // <--- ЛОГ

        System.out.println("\n--- HELP ---");
        System.out.println("1. Create knight - Add a new warrior (name, order, rank).");
        System.out.println("2. Delete knight - Delete a warrior by ID.");
        System.out.println("3. Select active - Choose who to work with.");
        System.out.println("4. Equip - Buy/take an item from the ammunition list.");
        System.out.println("5. Status - Show hero parameters and items.");
        System.out.println("6. Cost - Calculate the price of all equipped items.");
        System.out.println("7. Sort - Show items from lightest to heaviest.");
        System.out.println("8. Find by price - Filter hero's items by budget.");
        System.out.println("9. Reload - Reset state to file.");
        System.out.println("10. Help - This list.");
        System.out.println("11. Exit - Save data and exit.");
    }
}