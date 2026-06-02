package command;

import service.KnightManager;
import service.LoggerService;

public class GuiSetActiveKnightCommand implements Command {
    private KnightManager manager;
    private int knightId;
    private String knightName;

    public GuiSetActiveKnightCommand(KnightManager manager, int knightId, String knightName) {
        this.manager = manager;
        this.knightId = knightId;
        this.knightName = knightName;
    }

    @Override
    public void execute() {
        manager.setActiveKnight(knightId);
        LoggerService.info("Active knight set via GUI: " + knightName);
    }
}
