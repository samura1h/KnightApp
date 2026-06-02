package command;
import model.Knight;
import model.Rank;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiCalcDamageCommandTest {
    @Test
    void testExecute() {
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        AtomicInteger result = new AtomicInteger(-1);
        Command cmd = new GuiCalcDamageCommand(testKnight, result::set);
        cmd.execute();
        assertEquals(0, result.get());

        testKnight.getEquipment().add(new Sword("Excalibur", 5.0, 100.0, 50));
        cmd.execute();
        assertEquals(50, result.get());
    }

    @Test
    void testExecuteWithNullKnight() {
        AtomicInteger result = new AtomicInteger(-1);
        Command cmd = new GuiCalcDamageCommand(null, result::set);
        cmd.execute();
        assertEquals(0, result.get(), "Should return 0 when knight is null");
    }
}
