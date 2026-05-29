package command;

import service.KnightManager;
import service.LoggerService; // <--- ЛОГЕР

public class ReloadSystemCommand implements Command {
    private KnightManager manager;

    public ReloadSystemCommand(KnightManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        System.out.println("!!! WARNING !!! All unsaved changes will be lost.");
        LoggerService.info("User initiated full system reload."); // <--- ЛОГ
        manager.reloadSystem();
        LoggerService.info("System reloaded."); // <--- ЛОГ
    }
}