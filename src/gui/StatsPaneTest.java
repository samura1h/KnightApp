package gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Knight;
import model.Rank;
import model.equipment.Weapon;
import model.equipment.Sword;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import repository.DatabaseManager;
import repository.EquipmentRepository;
import repository.KnightRepository;
import service.KnightManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class StatsPaneTest extends ApplicationTest {

    private static final String TEST_DB_PATH = "test_stats_pane.db";
    private DatabaseManager dbManager;
    private EquipmentRepository equipRepo;
    private KnightRepository knightRepo;
    private KnightManager knightManager;
    private StatsPane statsPane;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseManager.resetInstance();
        dbManager = DatabaseManager.getInstance("jdbc:sqlite:" + TEST_DB_PATH);

        equipRepo = new EquipmentRepository("dummy.txt", dbManager);
        knightRepo = new KnightRepository(dbManager);
        knightManager = new KnightManager(knightRepo, equipRepo);

        statsPane = new StatsPane(knightManager, equipRepo);
        Scene scene = new Scene(statsPane.getRoot(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @AfterEach
    public void tearDown() {
        DatabaseManager.resetInstance();
        File db = new File(TEST_DB_PATH);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    public void testEmptyState() {
        Label emptyLabel = lookup("Please select an active knight in the 'Knights' tab to view stats.").queryAs(Label.class);
        assertNotNull(emptyLabel);
    }

    @Test
    public void testStatsWithActiveKnight() {
        Knight knight = new Knight("Lancelot", "Camelot", Rank.MASTER);
        knightManager.addKnight(knight);
        knightManager.setActiveKnight(knight.getId());

        interact(() -> {
            statsPane.refresh();
        });

        Label nameLabel = lookup("Lancelot").queryAs(Label.class);
        assertNotNull(nameLabel);
        
        Label orderLabel = lookup("Camelot").queryAs(Label.class);
        assertNotNull(orderLabel);
        
        Label rankLabel = lookup("Master").queryAs(Label.class);
        assertNotNull(rankLabel);
    }
}
