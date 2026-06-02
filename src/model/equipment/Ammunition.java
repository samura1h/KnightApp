package model.equipment; 

public abstract class Ammunition implements Comparable<Ammunition> {
    private int catalogId; 
    private String name;   
    private double weight; 
    private double price;  
    private String icon = ""; 

    public Ammunition(String name, double weight, double price) {
        this.name = name;     
        this.weight = weight; 
        this.price = price;   
    }

    public int getCatalogId() { return catalogId; } 
    public void setCatalogId(int catalogId) { this.catalogId = catalogId; } 
    public String getName() { return name; } 
    public double getWeight() { return weight; } 
    public double getPrice() { return price; } 
    public String getIcon() { return icon; } 
    public void setIcon(String icon) { this.icon = icon != null ? icon : ""; } 

    @Override
    public int compareTo(Ammunition other) {

        return Double.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {

        return String.format("%-20s (Weight: %.1fkg, Price: $%.1f)", name, weight, price);
    }
}