package command;
import model.Knight;
import model.Rank;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GuiCreateKnightCommandTest {
    @Test
    void testExecute() {
        KnightManager mockManager = new KnightManager(null, null) {
            private Map<Integer, Knight> map = new HashMap<>();
            @Override public void addKnight(Knight k) { k.setId(1); map.put(1, k); }
            @Override public Map<Integer, Knight> getAllKnights() { return map; }
        };
        Knight newKnight = new Knight("Lancelot", "Camelot", Rank.VETERAN);
        Command cmd = new GuiCreateKnightCommand(mockManager, newKnight);
        cmd.execute();
        assertTrue(mockManager.getAllKnights().containsValue(newKnight));
    }
}
