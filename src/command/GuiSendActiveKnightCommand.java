package command;

import model.Knight;
import model.equipment.Ammunition;
import service.EmailService;
import service.KnightManager;
import service.LoggerService;

public class GuiSendActiveKnightCommand implements Command {
    private final KnightManager manager;

    public GuiSendActiveKnightCommand(KnightManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        Knight active = manager.getActiveKnight();
        if (active == null) {
            LoggerService.error("Attempted to send email but no active knight is selected.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== KNIGHT INFO ===\n");
        sb.append("Name: ").append(active.getName()).append("\n");
        sb.append("Order: ").append(active.getOrden()).append("\n");
        sb.append("Rank: ").append(active.getRank()).append("\n");
        sb.append("\n=== EQUIPMENT ===\n");

        if (active.getEquipment().isEmpty()) {
            sb.append("No equipment equipped.\n");
        } else {
            double totalWeight = 0;
            double totalPrice = 0;
            for (Ammunition item : active.getEquipment()) {
                sb.append("- ").append(item.getName())
                  .append(" (Type: ").append(item.getClass().getSimpleName())
                  .append(", Weight: ").append(item.getWeight())
                  .append(", Price: ").append(item.getPrice()).append(")\n");
                totalWeight += item.getWeight();
                totalPrice += item.getPrice();
            }
            sb.append("\nTotal Weight: ").append(totalWeight).append(" kg\n");
            sb.append("Total Price: ").append(totalPrice).append(" gold\n");
        }

        EmailService.sendAsync("Active Knight Status: " + active.getName(), sb.toString());
        LoggerService.info("Active knight data sent to email: " + active.getName());
    }
}
