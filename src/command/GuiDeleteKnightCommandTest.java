package command;
import model.Knight;
import model.Rank;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GuiDeleteKnightCommandTest {
    @Test
    void testExecute() {
        KnightManager mockManager = new KnightManager(null, null) {
            private Map<Integer, Knight> map = new HashMap<>();
            @Override public void addKnight(Knight k) { map.put(k.getId(), k); }
            @Override public void removeKnight(int id) { map.remove(id); }
            @Override public Map<Integer, Knight> getAllKnights() { return map; }
        };
        Knight k = new Knight("Arthur", "Camelot", Rank.MASTER);
        k.setId(1);
        mockManager.addKnight(k);

        Command cmd = new GuiDeleteKnightCommand(mockManager, 1, "Arthur");
        cmd.execute();
        assertFalse(mockManager.getAllKnights().containsKey(1));
    }
}
