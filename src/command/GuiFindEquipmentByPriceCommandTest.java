package command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiFindEquipmentByPriceCommandTest {
    @Test
    void testExecute() {
        ObservableList<Ammunition> weapons = FXCollections.observableArrayList();
        weapons.add(new Sword("Sword1", 1.0, 10.0, 10));
        weapons.add(new Sword("Sword2", 1.0, 50.0, 10));
        ObservableList<Ammunition> armors = FXCollections.observableArrayList();
        armors.add(new Helmet("Helm1", 1.0, 20.0, 10));

        FilteredList<Ammunition> fw = new FilteredList<>(weapons);
        FilteredList<Ammunition> fa = new FilteredList<>(armors);

        Command cmd = new GuiFindEquipmentByPriceCommand(fw, fa, "All", 15.0, 60.0);
        cmd.execute();

        assertEquals(1, fw.size());
        assertEquals(1, fa.size());

        Command cmdType = new GuiFindEquipmentByPriceCommand(fw, fa, "Helmet", 0.0, 100.0);
        cmdType.execute();
        assertEquals(0, fw.size());
        assertEquals(1, fa.size());
    }
}
