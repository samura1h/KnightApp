package command;

import service.KnightManager;
import service.LoggerService;

public class GuiReloadSystemCommand implements Command {
    private KnightManager manager;

    public GuiReloadSystemCommand(KnightManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        LoggerService.info("User initiated full system reload via GUI.");
        manager.reloadSystem();
    }
}
