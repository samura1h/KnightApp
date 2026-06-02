package command;
import model.Knight;
import model.Rank;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiCalcEquipmentCostCommandTest {
    @Test
    void testExecute() {
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        AtomicReference<Double> result = new AtomicReference<>(-1.0);
        Command cmd = new GuiCalcEquipmentCostCommand(testKnight, result::set);
        cmd.execute();
        assertEquals(0.0, result.get());

        testKnight.getEquipment().add(new Helmet("Steel Helm", 3.0, 50.0, 15));
        testKnight.getEquipment().add(new Sword("Excalibur", 5.0, 100.0, 50));
        cmd.execute();
        assertEquals(150.0, result.get());
    }

    @Test
    void testExecuteWithNullKnight() {
        AtomicReference<Double> result = new AtomicReference<>(-1.0);
        Command cmd = new GuiCalcEquipmentCostCommand(null, result::set);
        cmd.execute();
        assertEquals(0.0, result.get(), "Should return 0.0 when knight is null");
    }
}
