package gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import repository.DatabaseManager;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GuiMainTest extends ApplicationTest {

    private final String TEST_DB_PATH = "test_gui.db";

    @Override
    public void start(Stage stage) throws Exception {
        
        GuiMain app = new GuiMain();
        app.init();
        app.start(stage);
    }

    @BeforeEach
    public void setUp() throws Exception {
        
        DatabaseManager.resetInstance();
        DatabaseManager.getInstance("jdbc:sqlite:" + TEST_DB_PATH);
        
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(GuiMain.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();

        File db = new File(TEST_DB_PATH);
        if (db.exists()) {
            db.delete();
        }
    }

    @Test
    void testApplicationStarts() {
        
        Label brandLabel = lookup("KNIGHT ORDER").queryAs(Label.class);
        assertNotNull(brandLabel);

        assertNotNull(lookup("⚔   Knights").queryButton());
        assertNotNull(lookup("🛡   Equipment").queryButton());
        assertNotNull(lookup("📊   Status").queryButton());
    }

    @Test
    void testAddKnightFlow() {
        
        clickOn("+ Create Knight");

        clickOn("#nameField").write("TestKnightFX");
        clickOn("#ordenField").write("FX Order");
        clickOn("#rankComboBox").clickOn("Master");

        clickOn("OK");

        TableView<?> table = lookup(".table-view").queryAs(TableView.class);
        assertFalse(table.getItems().isEmpty(), "Table should have at least one knight");

        Label activeBadge = lookup(".active-knight-badge").queryAs(Label.class);
        assertTrue(activeBadge.getText().contains("TestKnightFX"), "Active badge should show the new knight");
    }

    @Test
    void testNavigation() {
        
        clickOn("🛡   Equipment");
        Label equipmentTitle = lookup(".top-bar-title").queryAs(Label.class);
        assertEquals("Equipment", equipmentTitle.getText());

        clickOn("📊   Status");
        Label statusTitle = lookup(".top-bar-title").queryAs(Label.class);
        assertEquals("Knight Status", statusTitle.getText());
    }
}
