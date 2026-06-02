package model.equipment;

public abstract class Armor extends Ammunition {
    private int defense; 

    public Armor(String name, double weight, double price, int defense) {
        super(name, weight, price); 
        this.defense = defense;
    }
    public int getDefense() { return defense; }
}