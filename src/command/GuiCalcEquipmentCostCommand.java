package command;

import model.Knight;
import model.equipment.Ammunition;
import service.LoggerService;

import java.util.function.Consumer;

public class GuiCalcEquipmentCostCommand implements Command {
    private Knight knight;
    private Consumer<Double> callback;

    public GuiCalcEquipmentCostCommand(Knight knight, Consumer<Double> callback) {
        this.knight = knight;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (knight == null) {
            callback.accept(0.0);
            return;
        }
        double totalCost = knight.getEquipment().stream().mapToDouble(Ammunition::getPrice).sum();
        LoggerService.info("Calculated equipment cost via GUI for knight: " + knight.getName());
        callback.accept(totalCost);
    }
}
