package command;
import javafx.application.Platform;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(ApplicationExtension.class)
public class GuiHelpCommandTest extends ApplicationTest {
    @Override
    public void start(javafx.stage.Stage stage) {
        stage.show();
    }

    @Test
    void testExecute(FxRobot robot) {
        Platform.runLater(() -> {
            Command cmd = new GuiHelpCommand();
            cmd.execute();
        });

        try { Thread.sleep(500); } catch (InterruptedException e) { }

        robot.type(javafx.scene.input.KeyCode.ENTER);
    }
}
