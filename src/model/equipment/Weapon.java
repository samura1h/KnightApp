package model.equipment;

public abstract class Weapon extends Ammunition {
    private int damage; 

    public Weapon(String name, double weight, double price, int damage) {
        super(name, weight, price); 
        this.damage = damage;
    }

    public int getDamage() { return damage; }

    @Override
    public double getPrice() {
        return super.getPrice();
    }

    @Override
    public String toString() {
        return super.toString() + " | Damage: +" + damage;
    }
}