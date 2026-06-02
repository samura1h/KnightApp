package command;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiSaveDataCommandTest {
    @Test
    void testExecute() {
        AtomicBoolean saved = new AtomicBoolean(false);
        KnightManager mockManager = new KnightManager(null, null) {
            @Override public void saveAll() { saved.set(true); }
        };
        Command cmd = new GuiSaveDataCommand(mockManager);
        cmd.execute();
        assertTrue(saved.get());
    }
}
