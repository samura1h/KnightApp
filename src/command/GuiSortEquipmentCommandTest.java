package command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.equipment.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiSortEquipmentCommandTest {
    @Test
    void testExecute() {
        ObservableList<Ammunition> list = FXCollections.observableArrayList();
        list.add(new Sword("A", 10.0, 5.0, 10));
        list.add(new Sword("B", 2.0, 15.0, 5));
        list.add(new Helmet("C", 5.0, 10.0, 20));

        Command sortWeightDesc = new GuiSortEquipmentCommand(list, "Weight", false);
        sortWeightDesc.execute();
        assertEquals("A", list.get(0).getName());

        Command sortWeightAsc = new GuiSortEquipmentCommand(list, "Weight", true);
        sortWeightAsc.execute();
        assertEquals("B", list.get(0).getName());
        
        Command sortPriceDesc = new GuiSortEquipmentCommand(list, "Price", false);
        sortPriceDesc.execute();
        assertEquals("B", list.get(0).getName());
        
        Command sortDamageDesc = new GuiSortEquipmentCommand(list, "Damage", false);
        sortDamageDesc.execute();
        assertEquals("A", list.get(0).getName()); 
        
        Command sortDamageAsc = new GuiSortEquipmentCommand(list, "Damage", true);
        sortDamageAsc.execute();
        assertEquals("C", list.get(0).getName()); 
        
        Command sortDefenseDesc = new GuiSortEquipmentCommand(list, "Defense", false);
        sortDefenseDesc.execute();
        assertEquals("C", list.get(0).getName()); 
        
        Command sortDefenseAsc = new GuiSortEquipmentCommand(list, "Defense", true);
        sortDefenseAsc.execute();
        assertEquals("B", list.get(0).getName()); 

        Command sortNameDesc = new GuiSortEquipmentCommand(list, "Name", false);
        sortNameDesc.execute();
        assertEquals("C", list.get(0).getName());

        Command sortTypeAsc = new GuiSortEquipmentCommand(list, "Type", true);
        sortTypeAsc.execute();
        assertEquals("C", list.get(0).getName()); 

        Command sortPriceAscDef = new GuiSortEquipmentCommand(list, "Price");
        sortPriceAscDef.execute();
        assertEquals("A", list.get(0).getName()); 

        Command sortNullList = new GuiSortEquipmentCommand(null, "Price");
        sortNullList.execute(); 
        
        Command sortNullCriteria = new GuiSortEquipmentCommand(list, null);
        sortNullCriteria.execute(); 
        
        Command sortUnknownCriteria = new GuiSortEquipmentCommand(list, "Unknown");
        sortUnknownCriteria.execute(); 
    }
}
