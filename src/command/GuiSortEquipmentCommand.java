package command;

import javafx.collections.ObservableList;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;
import service.LoggerService;

import java.util.Comparator;

public class GuiSortEquipmentCommand implements Command {
    private ObservableList<Ammunition> catalogList;
    private String criteria;
    private boolean ascending;

    public GuiSortEquipmentCommand(ObservableList<Ammunition> catalogList, String criteria) {
        this(catalogList, criteria, true);
    }

    public GuiSortEquipmentCommand(ObservableList<Ammunition> catalogList, String criteria, boolean ascending) {
        this.catalogList = catalogList;
        this.criteria = criteria;
        this.ascending = ascending;
    }

    @Override
    public void execute() {
        if (catalogList == null || criteria == null) return;

        Comparator<Ammunition> comparator = null;

        switch (criteria) {
            case "Weight":
                comparator = Comparator.comparingDouble(Ammunition::getWeight);
                break;
            case "Price":
                comparator = Comparator.comparingDouble(Ammunition::getPrice);
                break;
            case "Damage":
                comparator = (a, b) -> {
                    int dmgA = a instanceof Weapon ? ((Weapon) a).getDamage() : -1;
                    int dmgB = b instanceof Weapon ? ((Weapon) b).getDamage() : -1;
                    return Integer.compare(dmgA, dmgB);
                };
                break;
            case "Defense":
                comparator = (a, b) -> {
                    int defA = a instanceof Armor ? ((Armor) a).getDefense() : -1;
                    int defB = b instanceof Armor ? ((Armor) b).getDefense() : -1;
                    return Integer.compare(defA, defB);
                };
                break;
            case "Name":
                comparator = Comparator.comparing(Ammunition::getName, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Weapon Type":
            case "Armor Type":
            case "Type":
                comparator = Comparator.comparing(a -> a.getClass().getSimpleName());
                break;
        }

        if (comparator != null) {
            if (!ascending) {
                comparator = comparator.reversed();
            }
            java.util.List<Ammunition> temp = new java.util.ArrayList<>(catalogList);
            temp.sort(comparator);
            catalogList.setAll(temp);
        }
        LoggerService.info("Catalog sorted by " + criteria + " (ascending=" + ascending + ") via GUI.");
    }
}
