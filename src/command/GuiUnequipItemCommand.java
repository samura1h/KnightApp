package command;

import model.Knight;
import model.equipment.Ammunition;
import service.KnightManager;
import service.LoggerService;

public class GuiUnequipItemCommand implements Command {
    private KnightManager manager;
    private Knight knight;
    private Ammunition item;

    public GuiUnequipItemCommand(KnightManager manager, Knight knight, Ammunition item) {
        this.manager = manager;
        this.knight = knight;
        this.item = item;
    }

    @Override
    public void execute() {
        knight.getEquipment().remove(item);
        manager.saveKnight(knight);
        LoggerService.info("Unequipped item via GUI: " + item.getName() + " from knight: " + knight.getName());
    }
}
