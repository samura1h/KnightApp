package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Knight;
import model.Rank;
import model.equipment.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import repository.DatabaseManager;
import repository.EquipmentRepository;
import repository.KnightRepository;
import service.KnightManager;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EquipmentPaneTest extends ApplicationTest {

    private static final String TEST_DB_PATH = "test_equipment_pane.db";
    private DatabaseManager dbManager;
    private EquipmentRepository equipRepo;
    private KnightRepository knightRepo;
    private KnightManager knightManager;

    private Runnable onActiveKnightChanged;
    private boolean activeKnightChangedCalled;
    private EquipmentPane equipmentPane;

    @Override
    public void start(Stage stage) throws Exception {
        
        DatabaseManager.resetInstance();
        dbManager = DatabaseManager.getInstance("jdbc:sqlite:" + TEST_DB_PATH);

        try (Connection conn = dbManager.getConnection()) {
            conn.createStatement().execute("DELETE FROM equipment_catalog");
            conn.createStatement().execute("DELETE FROM knights");
            conn.createStatement().execute("DELETE FROM knight_equipment");

            String insertCatalog = "INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCatalog)) {
                
                ps.setString(1, "Sword");
                ps.setString(2, "Excalibur");
                ps.setDouble(3, 10.0);
                ps.setDouble(4, 500.0);
                ps.setInt(5, 50);
                ps.addBatch();

                ps.setString(1, "Helmet");
                ps.setString(2, "Iron Helm");
                ps.setDouble(3, 5.0);
                ps.setDouble(4, 100.0);
                ps.setInt(5, 15);
                ps.addBatch();

                ps.executeBatch();
            }
        }

        equipRepo = new EquipmentRepository("dummy.txt", dbManager);
        knightRepo = new KnightRepository(dbManager);
        knightManager = new KnightManager(knightRepo, equipRepo);

        activeKnightChangedCalled = false;
        onActiveKnightChanged = () -> activeKnightChangedCalled = true;

        equipmentPane = new EquipmentPane(knightManager, equipRepo, onActiveKnightChanged);
        Scene scene = new Scene(equipmentPane.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void resetStateBeforeTest() {
        activeKnightChangedCalled = false;
    }

    @AfterEach
    public void tearDown() {
        DatabaseManager.resetInstance();
        File db = new File(TEST_DB_PATH);
        if (db.exists()) {
            db.delete();
        }
    }

    @SuppressWarnings("unchecked")
    private TableView<Ammunition> getWeaponsTable() {
        TabPane tabPane = lookup(".catalog-tabpane").queryAs(TabPane.class);
        return (TableView<Ammunition>) tabPane.getTabs().get(0).getContent();
    }

    @SuppressWarnings("unchecked")
    private TableView<Ammunition> getArmorTable() {
        TabPane tabPane = lookup(".catalog-tabpane").queryAs(TabPane.class);
        return (TableView<Ammunition>) tabPane.getTabs().get(1).getContent();
    }

    @Test
    public void testInitialization() {
        assertNotNull(equipmentPane.getRoot());
        TableView<Ammunition> weaponsTable = getWeaponsTable();
        assertNotNull(weaponsTable);
        assertFalse(weaponsTable.getItems().isEmpty());
        assertEquals("Excalibur", weaponsTable.getItems().get(0).getName());
    }

    @Test
    public void testFilters() {
        ComboBox<String> typeFilter = lookup(".combo-box").queryAs(ComboBox.class);
        assertNotNull(typeFilter);
        
        TextField minPrice = lookup(".text-field").queryAllAs(TextField.class).stream()
                .filter(tf -> "Min".equals(tf.getPromptText()))
                .findFirst().orElse(null);
        TextField maxPrice = lookup(".text-field").queryAllAs(TextField.class).stream()
                .filter(tf -> "Max".equals(tf.getPromptText()))
                .findFirst().orElse(null);

        assertNotNull(minPrice);
        assertNotNull(maxPrice);

        interact(() -> {
            typeFilter.setValue("Sword");
        });
        
        TableView<Ammunition> weaponsTable = getWeaponsTable();
        assertEquals(1, weaponsTable.getItems().size());

        interact(() -> {
            typeFilter.setValue("Helmet");
        });
        assertEquals(0, weaponsTable.getItems().size());

        Button btnReset = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Reset"))
                .findFirst().orElse(null);
        clickOn(btnReset);
        assertEquals("All", typeFilter.getValue());
    }

    @Test
    public void testPriceFilters() {
        TextField minPrice = lookup(".text-field").queryAllAs(TextField.class).stream()
                .filter(tf -> "Min".equals(tf.getPromptText()))
                .findFirst().orElse(null);
        TextField maxPrice = lookup(".text-field").queryAllAs(TextField.class).stream()
                .filter(tf -> "Max".equals(tf.getPromptText()))
                .findFirst().orElse(null);

        interact(() -> {
            minPrice.setText("200.0");
        });
        TableView<Ammunition> weaponsTable = getWeaponsTable();
        assertEquals(1, weaponsTable.getItems().size());

        interact(() -> {
            minPrice.setText("600.0");
        });
        assertEquals(0, weaponsTable.getItems().size());

        interact(() -> {
            minPrice.setText("");
            maxPrice.setText("100.0");
        });
        assertEquals(0, weaponsTable.getItems().size());
    }

    @Test
    public void testSortByWeight() throws Exception {
        try (Connection conn = dbManager.getConnection()) {
            String insertCatalog = "INSERT INTO equipment_catalog (type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCatalog)) {
                ps.setString(1, "Sword");
                ps.setString(2, "Light Dagger");
                ps.setDouble(3, 2.0);
                ps.setDouble(4, 50.0);
                ps.setInt(5, 10);
                ps.executeUpdate();
            }
        }

        interact(() -> {
            equipRepo.reload();
            equipmentPane.refresh();
        });

        TableView<Ammunition> weaponsTableBefore = getWeaponsTable();
        System.out.println("BEFORE SORT:");
        for (Ammunition item : weaponsTableBefore.getItems()) {
            System.out.println("  " + item.getName() + " (weight=" + item.getWeight() + ")");
        }

        clickOn("Weight (kg)");

        TableView<Ammunition> weaponsTableAfter = getWeaponsTable();
        System.out.println("AFTER SORT:");
        for (Ammunition item : weaponsTableAfter.getItems()) {
            System.out.println("  " + item.getName() + " (weight=" + item.getWeight() + ")");
        }

        assertEquals(2, weaponsTableAfter.getItems().size());
        assertEquals("Light Dagger", weaponsTableAfter.getItems().get(0).getName());
        assertEquals("Excalibur", weaponsTableAfter.getItems().get(1).getName());
    }

    @Test
    public void testEquipNoActiveKnight() {
        assertNull(knightManager.getActiveKnight());

        TableView<Ammunition> weaponsTable = getWeaponsTable();
        interact(() -> {
            weaponsTable.getSelectionModel().select(0);
        });

        Platform.runLater(() -> {
            try { Thread.sleep(200); } catch (Exception e) {}
            press(javafx.scene.input.KeyCode.ENTER);
            release(javafx.scene.input.KeyCode.ENTER);
        });

        Button btnEquip = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Equip Selected"))
                .findFirst().orElse(null);
        clickOn(btnEquip);
        assertFalse(activeKnightChangedCalled);
    }

    @Test
    public void testEquipSuccess() {
        Knight knight = new Knight("Arthur", "Camelot", Rank.MASTER);
        knightManager.addKnight(knight);
        knightManager.setActiveKnight(knight.getId());

        TableView<Ammunition> weaponsTable = getWeaponsTable();
        interact(() -> {
            weaponsTable.getSelectionModel().select(0);
        });

        Platform.runLater(() -> {
            try { Thread.sleep(200); } catch (Exception e) {}
            press(javafx.scene.input.KeyCode.ENTER);
            release(javafx.scene.input.KeyCode.ENTER);
        });

        Button btnEquip = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Equip Selected"))
                .findFirst().orElse(null);
        clickOn(btnEquip);
        assertTrue(activeKnightChangedCalled);
        assertEquals(1, knight.getEquipment().size());
        assertEquals("Excalibur", knight.getEquipment().get(0).getName());
    }

    @Test
    public void testEquipFailure() {
        Knight weakKnight = new Knight("Weakling", "None", Rank.NOVICE) {
            @Override
            public boolean equip(Ammunition item) {
                return false;
            }
        };
        knightManager.addKnight(weakKnight);
        knightManager.setActiveKnight(weakKnight.getId());

        TableView<Ammunition> weaponsTable = getWeaponsTable();
        interact(() -> {
            weaponsTable.getSelectionModel().select(0);
        });

        Platform.runLater(() -> {
            try { Thread.sleep(200); } catch (Exception e) {}
            press(javafx.scene.input.KeyCode.ENTER);
            release(javafx.scene.input.KeyCode.ENTER);
        });

        Button btnEquip = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Equip Selected"))
                .findFirst().orElse(null);
        clickOn(btnEquip);
        assertFalse(activeKnightChangedCalled);
    }
}
