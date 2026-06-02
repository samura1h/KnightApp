package command;

import model.Knight;
import service.KnightManager;
import service.LoggerService;

public class GuiCreateKnightCommand implements Command {
    private KnightManager manager;
    private Knight knight;

    public GuiCreateKnightCommand(KnightManager manager, Knight knight) {
        this.manager = manager;
        this.knight = knight;
    }

    @Override
    public void execute() {
        manager.addKnight(knight);
        LoggerService.info("Created new knight via GUI: " + knight.getName());
    }
}
