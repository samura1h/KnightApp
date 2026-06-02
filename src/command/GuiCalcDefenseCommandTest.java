package command;
import model.Knight;
import model.Rank;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiCalcDefenseCommandTest {
    @Test
    void testExecute() {
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        AtomicInteger result = new AtomicInteger(-1);
        Command cmd = new GuiCalcDefenseCommand(testKnight, result::set);
        cmd.execute();
        assertEquals(0, result.get());

        testKnight.getEquipment().add(new Helmet("Steel Helm", 3.0, 50.0, 15));
        cmd.execute();
        assertEquals(15, result.get());
    }

    @Test
    void testExecuteWithNullKnight() {
        AtomicInteger result = new AtomicInteger(-1);
        Command cmd = new GuiCalcDefenseCommand(null, result::set);
        cmd.execute();
        assertEquals(0, result.get(), "Should return 0 when knight is null");
    }
}
