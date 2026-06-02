package command;

import model.equipment.Ammunition;
import service.KnightManager;
import service.LoggerService;

public class GuiEquipKnightCommand implements Command {
    private KnightManager km;
    private Ammunition item;
    private Runnable onSuccess;
    private Runnable onFailure;

    public GuiEquipKnightCommand(KnightManager km, Ammunition item, Runnable onSuccess, Runnable onFailure) {
        this.km = km;
        this.item = item;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    public void execute() {
        if (km.getActiveKnight() == null) {
            return;
        }

        boolean success = km.getActiveKnight().equip(item);
        if (success) {
            LoggerService.info("Knight " + km.getActiveKnight().getName() + " equipped: " + item.getName() + " via GUI.");
            if (onSuccess != null) onSuccess.run();
        } else {
            LoggerService.info("Equip failure via GUI: " + item.getName());
            if (onFailure != null) onFailure.run();
        }
    }
}
