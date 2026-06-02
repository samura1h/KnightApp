package model;

public enum Rank {
    NOVICE("Novice"),     
    VETERAN("Veteran"),    
    MASTER("Master"),     
    GRAND_MASTER("Grand Master"); 

    private final String displayName;

    Rank(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}