package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.DatabaseManager;
import repository.EquipmentRepository;
import repository.KnightRepository;
import service.KnightManager;
import service.LoggerService;

public class GuiMain extends Application {

    private KnightManager knightManager;
    private EquipmentRepository equipRepo;
    private KnightRepository knightRepo;

    @Override
    public void init() {
        
        DatabaseManager.getInstance();

        equipRepo = new EquipmentRepository();
        knightRepo = new KnightRepository();
        knightRepo.loadData(); 
        knightManager = new KnightManager(knightRepo, equipRepo);
    }

    @Override
    public void start(Stage primaryStage) {
        LoggerService.info("GUI window is opening...");

        MainLayout mainLayout = new MainLayout(knightManager, equipRepo, primaryStage);

        Scene scene = new Scene(mainLayout.getRoot(), 1100, 720);

        String css = getClass().getResource("/gui/style.css") != null
                ? getClass().getResource("/gui/style.css").toExternalForm()
                : null;
        if (css == null) {
            
            try {
                java.io.File cssFile = new java.io.File("src/gui/style.css");
                if (cssFile.exists()) {
                    css = cssFile.toURI().toURL().toExternalForm();
                }
            } catch (Exception ignored) {}
        }
        if (css != null) {
            scene.getStylesheets().add(css);
        }

        primaryStage.setTitle("Knight Order Management System v1.0");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        primaryStage.setOnCloseRequest(event -> {
            LoggerService.info("User closed GUI window. Saving data...");
            knightManager.saveAll();
        });

        primaryStage.show();
        LoggerService.info("GUI launched successfully.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
