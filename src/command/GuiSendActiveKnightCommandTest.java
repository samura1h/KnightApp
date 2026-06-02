package command;

import model.Knight;
import model.Rank;
import org.junit.jupiter.api.Test;
import service.KnightManager;
import static org.junit.jupiter.api.Assertions.*;

class GuiSendActiveKnightCommandTest {

    @Test
    void testExecute() {
        Knight active = new Knight(1, "Lancelot", "Camelot", Rank.VETERAN);
        KnightManager mockManager = new KnightManager(null, null) {
            @Override
            public Knight getActiveKnight() {
                return active;
            }
        };

        Command cmd = new GuiSendActiveKnightCommand(mockManager);
        assertDoesNotThrow(cmd::execute);
    }

    @Test
    void testExecuteWithNoActiveKnight() {
        KnightManager mockManager = new KnightManager(null, null) {
            @Override
            public Knight getActiveKnight() {
                return null;
            }
        };

        Command cmd = new GuiSendActiveKnightCommand(mockManager);
        assertDoesNotThrow(cmd::execute);
    }
}
