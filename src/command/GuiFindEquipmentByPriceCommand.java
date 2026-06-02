package command;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;

public class GuiFindEquipmentByPriceCommand implements Command {
    private FilteredList<Ammunition> weaponsFiltered;
    private FilteredList<Ammunition> armorFiltered;
    private String type;
    private double minPrice;
    private double maxPrice;

    public GuiFindEquipmentByPriceCommand(FilteredList<Ammunition> weaponsFiltered, FilteredList<Ammunition> armorFiltered, String type, double minPrice, double maxPrice) {
        this.weaponsFiltered = weaponsFiltered;
        this.armorFiltered = armorFiltered;
        this.type = type;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public void execute() {
        weaponsFiltered.setPredicate(item -> {
            if (!(item instanceof Weapon)) return false;
            boolean typeMatch = "All".equals(type) || item.getClass().getSimpleName().equals(type);
            boolean priceMatch = item.getPrice() >= minPrice && item.getPrice() <= maxPrice;
            return typeMatch && priceMatch;
        });

        armorFiltered.setPredicate(item -> {
            if (!(item instanceof Armor)) return false;
            boolean typeMatch = "All".equals(type) || item.getClass().getSimpleName().equals(type);
            boolean priceMatch = item.getPrice() >= minPrice && item.getPrice() <= maxPrice;
            return typeMatch && priceMatch;
        });
    }
}
