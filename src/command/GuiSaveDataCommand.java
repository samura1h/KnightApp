package command;

import service.KnightManager;
import service.LoggerService;

public class GuiSaveDataCommand implements Command {
    private KnightManager manager;

    public GuiSaveDataCommand(KnightManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        manager.saveAll();
        LoggerService.info("User saved data via GUI.");
    }
}
