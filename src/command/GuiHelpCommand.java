package command;

import service.LoggerService;
import javafx.scene.control.Alert;

public class GuiHelpCommand implements Command {
    @Override
    public void execute() {
        LoggerService.info("User viewed help via GUI.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help — Knight Order Management System");
        alert.setHeaderText("❓ Quick Guide");
        alert.setContentText(
                "⚔ Knights — Create, load, delete, and select warriors.\n" +
                "🛡 Equipment — Browse the ammunition catalog, equip items.\n" +
                "📊 Status — View active knight stats, weight, defense, cost.\n\n" +
                "↻ Reload — Reset all data from file.\n" +
                "💾 Save — Save current state to disk.\n" +
                "✕ Exit — Save and close the application.\n\n" +
                "Tip: Select a knight first, then equip items from the catalog."
        );
        alert.showAndWait();
    }
}
