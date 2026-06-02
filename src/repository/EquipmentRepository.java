package repository;

import model.equipment.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {
    private static final Logger logger = LogManager.getLogger(EquipmentRepository.class);

    private List<Ammunition> catalog = new ArrayList<>();
    private DatabaseManager dbManager;
    private String ammunitionFilePath;

    public EquipmentRepository() {
        this("src/ammunition.txt");
    }

    public EquipmentRepository(String ammunitionFilePath) {
        this.ammunitionFilePath = ammunitionFilePath;
        this.dbManager = DatabaseManager.getInstance();

        dbManager.importAmmunitionFromFile(ammunitionFilePath);

        loadFromDatabase();
    }

    public EquipmentRepository(String ammunitionFilePath, DatabaseManager dbManager) {
        this.ammunitionFilePath = ammunitionFilePath;
        this.dbManager = dbManager;
        dbManager.importAmmunitionFromFile(ammunitionFilePath);
        loadFromDatabase();
    }

    public void reload() {
        logger.info("Reloading ammunition catalog from database...");
        catalog.clear();
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM equipment_catalog")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String name = rs.getString("name");
                double weight = rs.getDouble("weight");
                double price = rs.getDouble("price");
                int statValue = rs.getInt("stat_value");

                Ammunition item = createAmmunition(type, name, weight, price, statValue);
                if (item != null) {
                    item.setCatalogId(id);
                    item.setIcon(rs.getString("icon"));
                    catalog.add(item);
                }
            }

            logger.info("Catalog loaded: " + catalog.size() + " items.");
            System.out.println("DEBUG: EquipmentRepository loaded " + catalog.size() + " items from DB.");

        } catch (SQLException e) {
            service.LoggerService.error("CRITICAL ERROR: Failed to load equipment catalog from database!", e);
            System.err.println("DEBUG: EquipmentRepository failed to load from DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Ammunition createAmmunition(String type, String name, double weight, double price, int statValue) {
        switch (type) {
            case "Helmet": return new Helmet(name, weight, price, statValue);
            case "Breastplate": return new Breastplate(name, weight, price, statValue);
            case "Greaves": return new Greaves(name, weight, price, statValue);
            case "Sword": return new Sword(name, weight, price, statValue);
            case "Axe": return new Axe(name, weight, price, statValue);
            case "Bow": return new Bow(name, weight, price, statValue);
            case "Knife": return new Knife(name, weight, price, statValue);
            case "Shield": return new Breastplate(name, weight, price, statValue);
            case "TwoHandedSword": return new TwoHandedSword(name, weight, price, statValue);
            case "Mace": return new Mace(name, weight, price, statValue);
            case "Spear": return new Spear(name, weight, price, statValue);
            default:
                logger.warn("Unknown item type in database: " + type);
                return null;
        }
    }

    public List<Ammunition> getAll() {
        return catalog;
    }
}