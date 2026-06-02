package command;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiReloadSystemCommandTest {
    @Test
    void testExecute() {
        AtomicBoolean reloaded = new AtomicBoolean(false);
        KnightManager mockManager = new KnightManager(null, null) {
            @Override public void reloadSystem() { reloaded.set(true); }
        };
        Command cmd = new GuiReloadSystemCommand(mockManager);
        cmd.execute();
        assertTrue(reloaded.get());
    }
}
