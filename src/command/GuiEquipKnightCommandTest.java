package command;
import model.Knight;
import model.Rank;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

public class GuiEquipKnightCommandTest {
    @Test
    void testExecute() {
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        KnightManager mockManager = new KnightManager(null, null) {
            @Override public Knight getActiveKnight() { return testKnight; }
            @Override public void saveKnight(Knight k) {}
        };

        Sword sword = new Sword("Excalibur", 5.0, 100.0, 50);
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean failure = new AtomicBoolean(false);

        Command cmd = new GuiEquipKnightCommand(mockManager, sword, () -> success.set(true), () -> failure.set(true));
        cmd.execute();
        
        assertTrue(success.get());
        assertFalse(failure.get());
        assertTrue(testKnight.getEquipment().contains(sword));
        
        Ammunition heavy = new Helmet("Heavy Helm", 100.0, 10.0, 10);
        success.set(false);
        Command failCmd = new GuiEquipKnightCommand(mockManager, heavy, () -> success.set(true), () -> failure.set(true));
        failCmd.execute();
        assertFalse(success.get());
        assertTrue(failure.get());
    }
}
