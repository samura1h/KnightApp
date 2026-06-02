package command;
import model.Knight;
import model.Rank;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiSetActiveKnightCommandTest {
    @Test
    void testExecute() {
        Knight testKnight = new Knight("Arthur", "Camelot", Rank.MASTER);
        testKnight.setId(1);
        KnightManager mockManager = new KnightManager(null, null) {
            private Map<Integer, Knight> map = new HashMap<>();
            private Knight active = null;
            @Override public void addKnight(Knight k) { map.put(k.getId(), k); }
            @Override public Knight getActiveKnight() { return active; }
            @Override public void setActiveKnight(int id) { active = map.get(id); }
        };
        mockManager.addKnight(testKnight);

        Command cmd = new GuiSetActiveKnightCommand(mockManager, 1, "Arthur");
        cmd.execute();
        assertEquals(testKnight, mockManager.getActiveKnight());
    }
}
