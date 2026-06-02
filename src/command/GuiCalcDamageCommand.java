package command;

import model.Knight;
import model.equipment.Weapon;
import service.LoggerService;

import java.util.function.Consumer;

public class GuiCalcDamageCommand implements Command {
    private Knight knight;
    private Consumer<Integer> callback;

    public GuiCalcDamageCommand(Knight knight, Consumer<Integer> callback) {
        this.knight = knight;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (knight == null) {
            callback.accept(0);
            return;
        }
        int totalDamage = knight.getEquipment().stream()
                .filter(a -> a instanceof Weapon)
                .mapToInt(a -> ((Weapon) a).getDamage())
                .sum();
        LoggerService.info("Calculated equipment damage via GUI for knight: " + knight.getName());
        callback.accept(totalDamage);
    }
}
