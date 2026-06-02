package repository;

import model.Knight;
import model.Rank;
import model.equipment.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnightRepository {
    private static final Logger logger = LogManager.getLogger(KnightRepository.class);

    private Map<Integer, Knight> knights = new HashMap<>();
    private DatabaseManager dbManager;

    public KnightRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public KnightRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void loadData() {
        knights.clear();

        try (Connection conn = dbManager.getConnection()) {
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM knights")) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String orden = rs.getString("orden");
                    Rank rank = Rank.valueOf(rs.getString("rank"));

                    Knight knight = new Knight(id, name, orden, rank);
                    knights.put(id, knight);
                }
            }

            String equipSql =
                "SELECT ke.knight_id, ec.id AS catalog_id, ec.type, ec.name, ec.weight, ec.price, ec.stat_value, ec.icon " +
                "FROM knight_equipment ke " +
                "JOIN equipment_catalog ec ON ke.catalog_id = ec.id";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(equipSql)) {

                while (rs.next()) {
                    int knightId = rs.getInt("knight_id");
                    Knight knight = knights.get(knightId);
                    if (knight == null) continue;

                    Ammunition item = createAmmunition(
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getDouble("price"),
                        rs.getInt("stat_value")
                    );
                    if (item != null) {
                        item.setCatalogId(rs.getInt("catalog_id"));
                        item.setIcon(rs.getString("icon"));
                        knight.getEquipment().add(item);
                    }
                }
            }

            logger.info("Successfully loaded " + knights.size() + " knights from database.");

        } catch (SQLException e) {
            service.LoggerService.error("CRITICAL ERROR: Failed to load data from database!", e);
            knights = new HashMap<>();
        }
    }

    public void saveData() {
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);

            for (Knight k : knights.values()) {
                saveKnightToDb(conn, k);
            }

            conn.commit();
            logger.info("Data successfully saved to database.");

        } catch (SQLException e) {
            service.LoggerService.error("CRITICAL ERROR: Failed to save data to database!", e);
        }
    }

    private void saveKnightToDb(Connection conn, Knight k) throws SQLException {
        if (k.getId() == 0) {
            
            String insertSql = "INSERT INTO knights (name, orden, rank) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, k.getName());
                ps.setString(2, k.getOrden());
                ps.setString(3, k.getRank() != null ? k.getRank().name() : "NOVICE");
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        k.setId(keys.getInt(1));
                    }
                }
            }
        } else {
            
            String updateSql = "UPDATE knights SET name = ?, orden = ?, rank = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, k.getName());
                ps.setString(2, k.getOrden());
                ps.setString(3, k.getRank() != null ? k.getRank().name() : "NOVICE");
                ps.setInt(4, k.getId());
                ps.executeUpdate();
            }
        }

        try (PreparedStatement delPs = conn.prepareStatement("DELETE FROM knight_equipment WHERE knight_id = ?")) {
            delPs.setInt(1, k.getId());
            delPs.executeUpdate();
        }

        if (!k.getEquipment().isEmpty()) {
            String insertEquip = "INSERT INTO knight_equipment (knight_id, catalog_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertEquip)) {
                for (Ammunition item : k.getEquipment()) {
                    ps.setInt(1, k.getId());
                    ps.setInt(2, item.getCatalogId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    public void reload() {
        logger.info("Clearing knights memory and reloading from database...");
        knights.clear();
        loadData();
    }

    public void save(Knight k) {
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            saveKnightToDb(conn, k);
            conn.commit();
        } catch (SQLException e) {
            service.LoggerService.error("Failed to save knight: " + k.getName(), e);
        }
        knights.put(k.getId(), k);
    }

    public Knight findById(int id) { return knights.get(id); }

    public Map<Integer, Knight> findAll() { return knights; }

    public void remove(int id) {
        knights.remove(id);

        try (Connection conn = dbManager.getConnection()) {
            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM knight_equipment WHERE knight_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM knights WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            service.LoggerService.error("Failed to remove knight ID: " + id, e);
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
                logger.warn("Unknown equipment type: " + type);
                return null;
        }
    }
}