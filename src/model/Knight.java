package model; 

import model.equipment.Ammunition; 
import model.equipment.Armor; 
import model.equipment.Weapon; 
import java.util.ArrayList; 
import java.util.List; 

public class Knight {

    private int id; 
    private String name; 
    private String orden; 
    private Rank rank; 
    private int strength; 
    private int baseDefense; 

    private List<Ammunition> equipment;

    public Knight(String name, String orden, Rank rank) {
        this.name = name;
        this.orden = orden;
        this.rank = rank;
        this.strength = 60; 
        this.baseDefense = 20; 
        this.equipment = new ArrayList<>();
    }

    public Knight(int id, String name, String orden, Rank rank) {
        this(name, orden, rank);
        this.id = id;
    }

    public double getMaxWeightCapacity() {
        if (rank == null) return 18.0;
        switch (rank) {
            case VETERAN: return 20.0;
            case MASTER: return 22.0;
            case GRAND_MASTER: return 24.0;
            case NOVICE:
            default: return 18.0;
        }
    }

    public double getCurrentWeight() {
        
        return equipment.stream().mapToDouble(Ammunition::getWeight).sum();
    }

    public int getTotalDefense() {
        return equipment.stream() 
                .filter(a -> a instanceof Armor) 
                .mapToInt(a -> ((Armor) a).getDefense()) 
                .sum(); 
    }

    public int getTotalDamage() {
        return equipment.stream()
                .filter(a -> a instanceof Weapon)
                .mapToInt(a -> ((Weapon) a).getDamage())
                .sum();
    }

    public boolean equip(Ammunition newItem) {
        
        if (getCurrentWeight() + newItem.getWeight() > getMaxWeightCapacity()) {
            System.out.println("FAILURE: Too heavy! Weight limit exceeded.");
            return false;
        }

        if (newItem instanceof Weapon) {
            long weaponCount = equipment.stream().filter(a -> a instanceof Weapon).count();
            if (weaponCount >= 2) {
                System.out.println("FAILURE: You can equip at most 2 weapons!");
                return false;
            }
        } 
        
        else if (newItem instanceof Armor) {
            for (Ammunition existingItem : this.equipment) {
                if (existingItem.getClass().equals(newItem.getClass())) {
                    System.out.println("FAILURE: You already have a " + newItem.getClass().getSimpleName() + " equipped!");
                    return false;
                }
            }
        }
        
        else {
            for (Ammunition existingItem : this.equipment) {
                if (existingItem.getClass().equals(newItem.getClass())) {
                    System.out.println("FAILURE: You already have an item of type " + newItem.getClass().getSimpleName() + "!");
                    return false;
                }
            }
        }

        this.equipment.add(newItem);
        return true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; } 
    public String getName() { return name; }

    public Rank getRank() { return rank; }

    public List<Ammunition> getEquipment() { return equipment; }

    public String getOrden() {
        return orden;
    }

    public int getStrength() {
        return strength;
    }
    
    @Override
    public String toString() {
        
        return String.format("ID:%d | %s (%s, %s) | Weight: %.2f/%.2f",
                id, name, orden, rank, getCurrentWeight(), getMaxWeightCapacity());
    }
}