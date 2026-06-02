package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class KnightPaneTest extends ApplicationTest {

    private static final String TEST_DB_PATH = "test_knight_pane.db";
    private DatabaseManager dbManager;
    private EquipmentRepository equipRepo;
    private KnightRepository knightRepo;
    private KnightManager knightManager;

    private Runnable onActiveKnightChanged;
    private boolean activeKnightChangedCalled;
    private KnightPane knightPane;

    @Override
    public void start(Stage stage) throws Exception {
        
        DatabaseManager.resetInstance();
        dbManager = DatabaseManager.getInstance("jdbc:sqlite:" + TEST_DB_PATH);

        try (Connection conn = dbManager.getConnection()) {
            conn.createStatement().execute("DELETE FROM knights");
            conn.createStatement().execute("DELETE FROM knight_equipment");
            conn.createStatement().execute("DELETE FROM equipment_catalog");

            String insertCatalog = "INSERT INTO equipment_catalog (id, type, name, weight, price, stat_value) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCatalog)) {
                ps.setInt(1, 100);
                ps.setString(2, "Sword");
                ps.setString(3, "Excalibur");
                ps.setDouble(4, 10.0);
                ps.setDouble(5, 500.0);
                ps.setInt(6, 50);
                ps.executeUpdate();
            }

            String insertKnight = "INSERT INTO knights (id, name, orden, rank) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertKnight)) {
                ps.setInt(1, 1);
                ps.setString(2, "Arthur");
                ps.setString(3, "Camelot");
                ps.setString(4, "MASTER");
                ps.executeUpdate();
            }

            String equipSql = "INSERT INTO knight_equipment (knight_id, catalog_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(equipSql)) {
                ps.setInt(1, 1);
                ps.setInt(2, 100);
                ps.executeUpdate();
            }
        }

        equipRepo = new EquipmentRepository("dummy.txt", dbManager);
        knightRepo = new KnightRepository(dbManager);
        knightRepo.loadData();
        knightManager = new KnightManager(knightRepo, equipRepo);

        activeKnightChangedCalled = false;
        onActiveKnightChanged = () -> activeKnightChangedCalled = true;

        knightPane = new KnightPane(knightManager, onActiveKnightChanged);
        Scene scene = new Scene(knightPane.getRoot(), 800, 600);
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
    private TableView<Knight> getKnightTable() {
        return lookup(".table-view").queryAllAs(TableView.class).stream()
                .filter(t -> {
                    return t.getColumns().stream().anyMatch(c -> ((TableColumn<?, ?>) c).getText().equals("Order"));
                })
                .findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private TableView<Ammunition> getEquipTable() {
        return lookup(".table-view").queryAllAs(TableView.class).stream()
                .filter(t -> {
                    return t.getColumns().stream().anyMatch(c -> ((TableColumn<?, ?>) c).getText().equals("Type"));
                })
                .findFirst().orElse(null);
    }

    private void closeDialog() {
        new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                try { Thread.sleep(100); } catch (Exception e) {}
                final boolean[] found = {false};
                Platform.runLater(() -> {
                    var buttons = lookup(".dialog-pane .button").queryAllAs(Button.class);
                    if (!buttons.isEmpty()) {
                        found[0] = true;
                        buttons.stream()
                                .filter(btn -> {
                                    String txt = btn.getText().toLowerCase();
                                    return txt.equals("ok") || txt.equals("ок") || txt.equals("гаразд") || txt.equals("yes") || txt.equals("так");
                                })
                                .findFirst()
                                .ifPresent(btn -> {
                                    System.out.println("  Firing button: " + btn.getText());
                                    btn.fire();
                                });
                    }
                });
                try { Thread.sleep(50); } catch (Exception e) {}
                if (found[0]) {
                    break;
                }
            }
        }).start();
    }

    @Test
    public void testInitialization() {
        assertNotNull(knightPane.getRoot());
        TableView<Knight> knightTable = getKnightTable();
        assertNotNull(knightTable);
        assertEquals(1, knightTable.getItems().size());
        assertEquals("Arthur", knightTable.getItems().get(0).getName());
    }

    @Test
    public void testCreateKnight() {
        Button btnCreate = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Create Knight"))
                .findFirst().orElse(null);
        assertNotNull(btnCreate);
        clickOn(btnCreate);

        clickOn("#nameField").write("Lancelot");
        clickOn("#ordenField").write("Round Table");
        clickOn("#rankComboBox").clickOn("Veteran");

        clickOn("OK");

        TableView<Knight> knightTable = getKnightTable();
        assertEquals(2, knightTable.getItems().size());
        boolean hasLancelot = knightTable.getItems().stream()
                .anyMatch(k -> "Lancelot".equals(k.getName()));
        assertTrue(hasLancelot);
    }

    @Test
    public void testCreateKnightWithoutOrder() {
        Button btnCreate = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Create Knight"))
                .findFirst().orElse(null);
        assertNotNull(btnCreate);
        clickOn(btnCreate);

        clickOn("#nameField").write("Galahad");
        clickOn("#withoutOrderCheck");
        clickOn("#rankComboBox").clickOn("Novice");

        clickOn("OK");

        TableView<Knight> knightTable = getKnightTable();
        Knight galahad = knightTable.getItems().stream()
                .filter(k -> "Galahad".equals(k.getName()))
                .findFirst().orElse(null);
        assertNotNull(galahad);
        assertEquals("Without Order", galahad.getOrden());
    }

    @Test
    public void testSetActive() {
        TableView<Knight> knightTable = getKnightTable();
        interact(() -> {
            knightTable.getSelectionModel().select(0);
        });

        Button btnSetActive = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Set Active"))
                .findFirst().orElse(null);
        assertNotNull(btnSetActive);

        closeDialog();

        clickOn(btnSetActive);
        assertTrue(activeKnightChangedCalled);
        assertNotNull(knightManager.getActiveKnight());
        assertEquals("Arthur", knightManager.getActiveKnight().getName());
    }

    @Test
    public void testDeleteKnight() {
        TableView<Knight> knightTable = getKnightTable();
        interact(() -> {
            knightTable.getSelectionModel().select(0);
        });

        Button btnDelete = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Delete"))
                .findFirst().orElse(null);
        assertNotNull(btnDelete);

        closeDialog();

        clickOn(btnDelete);
        assertEquals(0, knightTable.getItems().size());
        assertTrue(activeKnightChangedCalled);
    }

    @Test
    public void testEquipmentTableUpdatedOnSelection() {
        TableView<Knight> knightTable = getKnightTable();
        TableView<Ammunition> equipTable = getEquipTable();
        assertNotNull(equipTable);

        interact(() -> {
            knightTable.getSelectionModel().clearSelection();
        });
        assertTrue(equipTable.getItems().isEmpty());

        interact(() -> {
            knightTable.getSelectionModel().select(0);
        });
        assertFalse(equipTable.getItems().isEmpty());
        assertEquals("Excalibur", equipTable.getItems().get(0).getName());
    }

    @Test
    public void testUnequipKnightItem() {
        TableView<Knight> knightTable = getKnightTable();
        interact(() -> {
            knightTable.getSelectionModel().select(0);
        });

        TableView<Ammunition> equipTable = getEquipTable();
        assertFalse(equipTable.getItems().isEmpty());
        int initialSize = equipTable.getItems().size();

        interact(() -> {
            equipTable.getSelectionModel().select(0);
        });

        Button btnUnequip = lookup(".button").queryAllAs(Button.class).stream()
                .filter(b -> b.getText().contains("Unequip Selected"))
                .findFirst().orElse(null);
        assertNotNull(btnUnequip);
        clickOn(btnUnequip);

        assertEquals(initialSize - 1, equipTable.getItems().size());
    }
}
