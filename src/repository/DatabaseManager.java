package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DatabaseManager {
    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:knight_app.db";

    private static DatabaseManager instance;
    private String dbUrl;

    private DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
        
        try {
            java.sql.Driver driver = (java.sql.Driver) Class.forName("org.sqlite.JDBC")
                    .getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            service.LoggerService.error("Failed to register SQLite JDBC driver!", e);
            System.err.println("DEBUG: Failed to register org.sqlite.JDBC: " + e.getMessage());
        }
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(DEFAULT_DB_URL);
        }
        return instance;
    }

    public static synchronized DatabaseManager getInstance(String dbUrl) {
        
        if (instance == null || !instance.dbUrl.equals(dbUrl)) {
            instance = new DatabaseManager(dbUrl);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS knights (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT NOT NULL," +
                "  orden TEXT NOT NULL," +
                "  rank TEXT NOT NULL," +
                "  strength INTEGER DEFAULT 60," +
                "  base_defense INTEGER DEFAULT 20" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS equipment_catalog (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  type TEXT NOT NULL," +
                "  name TEXT NOT NULL," +
                "  weight REAL NOT NULL," +
                "  price REAL NOT NULL," +
                "  stat_value INTEGER NOT NULL" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS knight_equipment (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  knight_id INTEGER NOT NULL," +
                "  catalog_id INTEGER NOT NULL," +
                "  FOREIGN KEY (knight_id) REFERENCES knights(id) ON DELETE CASCADE," +
                "  FOREIGN KEY (catalog_id) REFERENCES equipment_catalog(id)" +
                ")"
            );

            try {
                stmt.execute("ALTER TABLE equipment_catalog ADD COLUMN icon TEXT DEFAULT ''");
            } catch (SQLException ignored) {}

            stmt.execute("UPDATE equipment_catalog SET icon = 'axe.png' WHERE type IN ('Axe', 'Bardiche', 'War Hammer')");
            stmt.execute("UPDATE equipment_catalog SET icon = 'bow.png' WHERE type IN ('Bow', 'Heavy Crossbow')");
            stmt.execute("UPDATE equipment_catalog SET icon = 'breastplate.png' WHERE type IN ('Breastplate', 'Shield', 'Greaves', 'Leather Greaves', 'Chainmail Leggings', 'Plate Greaves')");
            stmt.execute("UPDATE equipment_catalog SET icon = 'helmet.png' WHERE type IN ('Helmet')");
            stmt.execute("UPDATE equipment_catalog SET icon = 'sword.png' WHERE type IN ('Sword', 'TwoHandedSword', 'Knife', 'Mace', 'Spear', 'Flail', 'Morning Star', 'Pernach', 'Poleaxe', 'Gross Messer', 'Scottish Claymore', 'Zweihander', 'Flamberge', 'Rondel Dagger', 'Seax', 'Falchion', 'Carolingian Sword', 'Roman Sword', 'Bastard Sword', 'Estoc', 'Katzbalger', 'Sharpened Stake', 'Peasant Pitchfork', 'Militia Spear', 'Voulge', 'Glaive', 'Halberd', 'Club with branches', 'Spiked Club')");

            logger.info("Database initialized successfully.");

        } catch (SQLException e) {
            service.LoggerService.error("CRITICAL ERROR: Failed to initialize database!", e);
        }
    }

    private static final String DEFAULT_AMMUNITION_DATA =
        "Knife,Flint Knife,0.15,1.0,1\n" +
        "Spear,Sharpened Stake,0.90,2.0,3\n" +
        "Mace,Club with branches,1.50,3.0,5\n" +
        "Breastplate,Linen Tunic,0.40,5.0,1\n" +
        "Knife,Utility Knife,0.20,8.0,4\n" +
        "Helmet,Felt Hat,0.30,6.0,2\n" +
        "Spear,Peasant Pitchfork,1.80,12.0,8\n" +
        "Axe,Woodcutter's Axe,1.50,15.0,10\n" +
        "Bow,Short Bow,0.60,20.0,12\n" +
        "Breastplate,Gambeson,2.50,40.0,8\n" +
        "Sword,Falchion,0.90,50.0,16\n" +
        "Mace,Spiked Club,1.80,35.0,14\n" +
        "Helmet,Leather Cap,0.50,25.0,5\n" +
        "Shield,Wooden Buckler,1.20,30.0,6\n" +
        "Spear,Militia Spear,2.00,45.0,18\n" +
        "Knife,Seax,0.60,60.0,15\n" +
        "Axe,Francisca (Throwing Axe),0.70,70.0,19\n" +
        "Mace,Flail,1.20,80.0,20\n" +
        "Sword,Carolingian Sword,1.20,120.0,25\n" +
        "Breastplate,Leather Brigandine,5.50,150.0,18\n" +
        "Bow,Hunting Bow,0.80,100.0,22\n" +
        "Helmet,Norman Helmet,1.60,110.0,14\n" +
        "Spear,Voulge,2.50,130.0,28\n" +
        "Mace,Morning Star,1.80,160.0,30\n" +
        "Axe,Bearded Axe,1.40,140.0,32\n" +
        "Breastplate,Chainmail (Hauberk),11.00,300.0,25\n" +
        "Sword,Roman Sword,1.30,250.0,30\n" +
        "Shield,Kite Shield,3.00,180.0,20\n" +
        "Helmet,Chapel-de-fer (Kettle Hat),2.00,200.0,18\n" +
        "TwoHandedSword,Gross Messer,1.70,280.0,38\n" +
        "Mace,Pernach,1.50,320.0,35\n" +
        "Spear,Glaive,3.20,290.0,40\n" +
        "Knife,Rondel Dagger,0.40,150.0,18\n" +
        "Bow,English Longbow,1.10,350.0,35\n" +
        "Breastplate,Plated Brigandine,9.00,500.0,35\n" +
        "Sword,Bastard Sword,1.60,450.0,42\n" +
        "Axe,War Hammer,1.30,380.0,38\n" +
        "Helmet,Bascinet (Hounskull),2.80,550.0,30\n" +
        "TwoHandedSword,Scottish Claymore,2.50,600.0,50\n" +
        "Spear,Halberd,3.50,520.0,55\n" +
        "Mace,Poleaxe,2.80,650.0,58\n" +
        "Sword,Estoc,1.40,580.0,45\n" +
        "Bow,Heavy Crossbow,5.00,700.0,60\n" +
        "Breastplate,Corazzina,10.00,800.0,45\n" +
        "Helmet,Sallet with visor,2.50,750.0,35\n" +
        "TwoHandedSword,Zweihander,3.80,900.0,65\n" +
        "Axe,Bardiche,3.20,680.0,62\n" +
        "Sword,Katzbalger,1.10,500.0,40\n" +
        "Breastplate,Milanese Cuirass,14.00,1500.0,60\n" +
        "Helmet,Armet (Closed),3.50,1200.0,45\n" +
        "TwoHandedSword,Flamberge,3.40,1800.0,75\n" +
        "Breastplate,Maximilian Armour,22.00,3000.0,85\n" +
        "Greaves,Leather Greaves,1.20,35.0,4\n" +
        "Greaves,Chainmail Leggings,4.50,120.0,12\n" +
        "Greaves,Plate Greaves,6.00,300.0,20";

    public void importAmmunitionFromFile(String filePath) {
        try (Connection conn = getConnection()) {

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM equipment_catalog")) {
                 if (rs.next() && rs.getInt(1) > 0) {
                    logger.info("Equipment catalog already populated (" + rs.getInt(1) + " items). Skipping import.");
                    System.out.println("DEBUG: Database already populated with " + rs.getInt(1) + " items. Skipping import.");
                    return;
                }
            }

            conn.setAutoCommit(false); 

            String insertSql = "INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)";
            int count = 0;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                java.io.Reader reader = null;
                File file = new File(filePath);

                System.out.println("DEBUG: DB URL is: " + dbUrl + " | Absolute DB path: " + new File("knight_app.db").getAbsolutePath());

                if (file.exists()) {
                    reader = new java.io.InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8);
                    logger.info("Importing ammunition from file: " + filePath);
                    System.out.println("DEBUG: Importing from file path: " + file.getAbsolutePath());
                } else {
                    java.io.InputStream resourceStream = DatabaseManager.class.getResourceAsStream("/ammunition.txt");
                    if (resourceStream == null) {
                        resourceStream = DatabaseManager.class.getClassLoader().getResourceAsStream("ammunition.txt");
                    }
                    if (resourceStream != null) {
                        reader = new java.io.InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                        logger.info("Importing ammunition from classpath resource: /ammunition.txt");
                        System.out.println("DEBUG: Importing from classpath resource: /ammunition.txt");
                    } else {
                        reader = new java.io.StringReader(DEFAULT_AMMUNITION_DATA);
                        logger.info("Importing ammunition from built-in default fallback data.");
                        System.out.println("DEBUG: Importing from built-in backup fallback data.");
                    }
                }

                try (BufferedReader br = new BufferedReader(reader)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;

                        String[] parts = line.split(",");
                        if (parts.length != 5) {
                            logger.warn("Skipped corrupted line during import: " + line);
                            continue;
                        }

                        try {
                            ps.setString(1, parts[0].trim());
                            ps.setString(2, parts[1].trim());
                            ps.setDouble(3, Double.parseDouble(parts[2].trim()));
                            ps.setDouble(4, Double.parseDouble(parts[3].trim()));
                            ps.setInt(5, Integer.parseInt(parts[4].trim()));
                            ps.addBatch();
                            count++;
                        } catch (NumberFormatException e) {
                            logger.warn("Skipped line with invalid numbers: " + line);
                        }
                    }

                    ps.executeBatch();
                    conn.commit();
                    logger.info("Imported " + count + " items into database.");
                }
            } catch (IOException e) {
                conn.rollback();
                service.LoggerService.error("CRITICAL ERROR: Failed to read ammunition file for import!", e);
            }

        } catch (SQLException e) {
            service.LoggerService.error("CRITICAL ERROR: Failed to import ammunition data!", e);
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }
}
