package command;

import service.KnightManager;
import service.LoggerService;

public class GuiDeleteKnightCommand implements Command {
    private KnightManager manager;
    private int knightId;
    private String knightName;

    public GuiDeleteKnightCommand(KnightManager manager, int knightId, String knightName) {
        this.manager = manager;
        this.knightId = knightId;
        this.knightName = knightName;
    }

    @Override
    public void execute() {
        manager.removeKnight(knightId);
        LoggerService.info("Deleted knight via GUI: " + knightName);
    }
}
