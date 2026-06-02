package command;
import model.Knight;
import model.Rank;
import model.equipment.Sword;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GuiUnequipItemCommandTest {
    @Test
    void testExecute() {
        KnightManager mockManager = new KnightManager(null, null) {
            @Override public void saveKnight(Knight k) {}
        };
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        Sword sword = new Sword("A", 1.0, 1.0, 1);
        testKnight.getEquipment().add(sword);
        
        Command cmd = new GuiUnequipItemCommand(mockManager, testKnight, sword);
        cmd.execute();
        
        assertFalse(testKnight.getEquipment().contains(sword));
    }
}
