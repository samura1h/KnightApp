package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService;

/**
 * Головний макет додатку: Sidebar (ліворуч) + Content Area (праворуч).
 */
public class MainLayout {

    private final BorderPane root;
    private final KnightManager knightManager;
    private final EquipmentRepository equipRepo;
    private final Stage primaryStage;

    // Панелі контенту
    private final KnightPane knightPane;
    private final EquipmentPane equipmentPane;
    private final StatsPane statsPane;

    // Поточна активна кнопка бокової панелі
    private Button activeButton = null;

    // Верхня панель — Label для активного лицаря
    private Label activeKnightLabel;
    private Label topBarTitle;

    public MainLayout(KnightManager knightManager, EquipmentRepository equipRepo, Stage primaryStage) {
        this.knightManager = knightManager;
        this.equipRepo = equipRepo;
        this.primaryStage = primaryStage;

        // Створюємо панелі контенту
        this.knightPane = new KnightPane(knightManager, this::refreshActiveKnightDisplay);
        this.equipmentPane = new EquipmentPane(knightManager, equipRepo, this::refreshActiveKnightDisplay);
        this.statsPane = new StatsPane(knightManager, equipRepo);

        // Будуємо layout
        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createContentWrapper(knightPane.getRoot()));

        // Показуємо першу вкладку (Knights)
        refreshActiveKnightDisplay();
    }

    /**
     * Створює бокову панель навігації.
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        // --- Brand ---
        VBox brandBox = new VBox(4);
        brandBox.getStyleClass().add("sidebar-brand");

        HBox logoTitleBox = new HBox(8);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView();
        try {
            java.io.InputStream imgStream = getClass().getResourceAsStream("/gui/logo.png");
            if (imgStream != null) {
                // Завантажуємо зображення з увімкненим високоякісним згладжуванням (smooth = true)
                Image logoImage = new Image(imgStream, 128, 128, true, true);
                logoView.setImage(logoImage);
                logoView.setFitWidth(34);
                logoView.setFitHeight(34);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);
                logoView.setTranslateY(4); // Зсуваємо трохи вниз для гармонійного вирівнювання з текстом
            }
        } catch (Exception e) {
            // Ігноруємо помилки завантаження
        }

        Label brandLabel = new Label("KNIGHT ORDER");
        brandLabel.getStyleClass().add("label");
        brandLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");

        logoTitleBox.getChildren().addAll(logoView, brandLabel);

        Label subLabel = new Label("Management System v1.0");
        subLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px; -fx-padding: 0 0 0 42;");
        brandBox.getChildren().addAll(logoTitleBox, subLabel);

        // --- Navigation section ---
        Label navTitle = new Label("NAVIGATION");
        navTitle.getStyleClass().add("sidebar-title");

        Button btnKnights = createSidebarButton("⚔   Knights", () -> switchContent(knightPane.getRoot(), "Knights", "Manage your warriors"));
        Button btnEquipment = createSidebarButton("🛡   Equipment", () -> switchContent(equipmentPane.getRoot(), "Equipment", "Browse & equip ammunition"));
        Button btnStats = createSidebarButton("📊   Status", () -> {
            statsPane.refresh();
            switchContent(statsPane.getRoot(), "Knight Status", "Active knight overview");
        });

        // Перша кнопка активна за замовчуванням
        setActiveButton(btnKnights);

        // --- Separator ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // --- Bottom actions ---
        Label actionsTitle = new Label("ACTIONS");
        actionsTitle.getStyleClass().add("sidebar-title");

        Button btnReload = createBottomButton("↻   Reload System", this::handleReload);
        Button btnSave = createBottomButton("💾   Save Data", this::handleSave);
        Button btnHelp = createBottomButton("❓   Help", this::handleHelp);
        Button btnExit = createBottomButton("✕   Exit", this::handleExit);

        sidebar.getChildren().addAll(
                brandBox,
                navTitle,
                btnKnights, btnEquipment, btnStats,
                spacer,
                createSidebarSeparator(),
                actionsTitle,
                btnReload, btnSave, btnHelp, btnExit
        );

        return sidebar;
    }

    /**
     * Обгортка для контенту: top bar + scrollable content.
     */
    private BorderPane createContentWrapper(Region content) {
        BorderPane wrapper = new BorderPane();
        wrapper.setTop(createTopBar("Knights", "Manage your warriors"));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.getStyleClass().add("content-area");
        wrapper.setCenter(scroll);
        return wrapper;
    }

    /**
     * Верхня панель з назвою секції та бейджем активного лицаря.
     */
    private HBox createTopBar(String title, String subtitle) {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        topBarTitle = new Label(title);
        topBarTitle.getStyleClass().add("top-bar-title");
        Label sub = new Label(subtitle);
        sub.getStyleClass().add("top-bar-subtitle");
        titleBox.getChildren().addAll(topBarTitle, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        activeKnightLabel = new Label("No active knight");
        activeKnightLabel.getStyleClass().add("active-knight-badge");

        topBar.getChildren().addAll(titleBox, spacer, activeKnightLabel);
        return topBar;
    }

    /**
     * Переключає контент на задану панель.
     */
    private void switchContent(Region content, String title, String subtitle) {
        BorderPane wrapper = (BorderPane) root.getCenter();
        // Оновлюємо top bar
        topBarTitle.setText(title);
        // Оновлюємо контент
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        scroll.getStyleClass().add("content-area");
        wrapper.setCenter(scroll);
    }

    /**
     * Оновлює відображення активного лицаря у верхній панелі.
     */
    public void refreshActiveKnightDisplay() {
        if (knightManager.getActiveKnight() != null) {
            var k = knightManager.getActiveKnight();
            activeKnightLabel.setText("⚔ " + k.getName() + "  (" + k.getRank() + ")");
            activeKnightLabel.setStyle("-fx-text-fill: #333333;");
        } else {
            activeKnightLabel.setText("No active knight");
            activeKnightLabel.setStyle("-fx-text-fill: #999999;");
        }

        // Синхронізуємо дані на всіх вкладках
        if (knightPane != null) {
            knightPane.refreshTable();
        }
        if (statsPane != null) {
            statsPane.refresh();
        }
    }

    // === Кнопки sidebar ===

    private Button createSidebarButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            setActiveButton(btn);
            action.run();
        });
        return btn;
    }

    private Button createBottomButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-bottom-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void setActiveButton(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-btn-active");
        }
        activeButton = btn;
        if (!btn.getStyleClass().contains("sidebar-btn-active")) {
            btn.getStyleClass().add("sidebar-btn-active");
        }
    }

    private Region createSidebarSeparator() {
        Region sep = new Region();
        sep.getStyleClass().add("sidebar-separator");
        sep.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(sep, new Insets(8, 16, 8, 16));
        return sep;
    }

    // === Обробники дій ===

    private void handleReload() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reload System");
        alert.setHeaderText("⚠ WARNING");
        alert.setContentText("All unsaved changes will be lost. Continue?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoggerService.info("User initiated full system reload via GUI.");
                knightManager.reloadSystem();
                knightPane.refreshTable();
                refreshActiveKnightDisplay();
                showInfo("System reloaded successfully.");
            }
        });
    }

    private void handleSave() {
        knightManager.saveAll();
        LoggerService.info("User saved data via GUI.");
        showInfo("Data saved successfully.");
    }

    private void handleHelp() {
        LoggerService.info("User viewed help via GUI.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help — Knight Order Management System");
        alert.setHeaderText("❓ Quick Guide");
        alert.setContentText(
                "⚔ Knights — Create, load, delete, and select warriors.\n" +
                "🛡 Equipment — Browse the ammunition catalog, equip items.\n" +
                "📊 Status — View active knight stats, weight, defense, cost.\n\n" +
                "↻ Reload — Reset all data from file.\n" +
                "💾 Save — Save current state to disk.\n" +
                "✕ Exit — Save and close the application.\n\n" +
                "Tip: Select a knight first, then equip items from the catalog."
        );
        alert.showAndWait();
    }

    private void handleExit() {
        LoggerService.info("User requested exit via GUI.");
        knightManager.saveAll();
        primaryStage.close();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }
}
