package command;

import model.Knight;
import model.equipment.Armor;
import service.LoggerService;

import java.util.function.Consumer;

public class GuiCalcDefenseCommand implements Command {
    private Knight knight;
    private Consumer<Integer> callback;

    public GuiCalcDefenseCommand(Knight knight, Consumer<Integer> callback) {
        this.knight = knight;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (knight == null) {
            callback.accept(0);
            return;
        }
        int totalDefense = knight.getEquipment().stream()
                .filter(a -> a instanceof Armor)
                .mapToInt(a -> ((Armor) a).getDefense())
                .sum();
        LoggerService.info("Calculated equipment defense via GUI for knight: " + knight.getName());
        callback.accept(totalDefense);
    }
}
