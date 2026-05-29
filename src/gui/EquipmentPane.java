package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Панель каталогу амуніції: розділено на Зброю (Weapons) та Броню (Armor).
 */
public class EquipmentPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final EquipmentRepository equipRepo;
    private final Runnable onActiveKnightChanged;

    // Списки та фільтрація
    private ObservableList<Ammunition> catalogList;
    private FilteredList<Ammunition> weaponsFiltered;
    private FilteredList<Ammunition> armorFiltered;

    // Елементи інтерфейсу
    private TabPane tabPane;
    private TableView<Ammunition> weaponsTable;
    private TableView<Ammunition> armorTable;

    // Фільтри
    private ComboBox<String> typeFilter;
    private TextField minPriceField;
    private TextField maxPriceField;

    public EquipmentPane(KnightManager knightManager, EquipmentRepository equipRepo, Runnable onActiveKnightChanged) {
        this.knightManager = knightManager;
        this.equipRepo = equipRepo;
        this.onActiveKnightChanged = onActiveKnightChanged;

        // Ініціалізація списків
        catalogList = FXCollections.observableArrayList(equipRepo.getAll());
        
        // Фільтруємо спочатку за базовим типом (Weapon / Armor)
        weaponsFiltered = new FilteredList<>(catalogList, item -> item instanceof Weapon);
        armorFiltered = new FilteredList<>(catalogList, item -> item instanceof Armor);

        root = new VBox(20);
        root.setPadding(new Insets(0));
        root.getChildren().addAll(createFilterBar(), createCatalogTabCard(), createActionBar());
    }

    /**
     * Панель фільтрів: тип, ціна, скидання, сортування.
     */
    private HBox createFilterBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-text-fill: #666666;");

        // Усі типи з каталогу
        List<String> types = new ArrayList<>();
        types.add("All");
        types.addAll(equipRepo.getAll().stream()
                .map(a -> a.getClass().getSimpleName())
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
        typeFilter = new ComboBox<>(FXCollections.observableArrayList(types));
        typeFilter.setValue("All");
        typeFilter.setPrefWidth(140);
        typeFilter.setOnAction(e -> applyFilters());

        Label priceLabel = new Label("Price:");
        priceLabel.setStyle("-fx-text-fill: #666666;");

        minPriceField = new TextField();
        minPriceField.setPromptText("Min");
        minPriceField.setPrefWidth(80);
        minPriceField.textProperty().addListener((obs, o, n) -> applyFilters());

        Label dash = new Label("—");
        dash.setStyle("-fx-text-fill: #999999;");

        maxPriceField = new TextField();
        maxPriceField.setPromptText("Max");
        maxPriceField.setPrefWidth(80);
        maxPriceField.textProperty().addListener((obs, o, n) -> applyFilters());

        Button btnReset = new Button("Reset Filters");
        btnReset.getStyleClass().add("btn-secondary");
        btnReset.setOnAction(e -> {
            typeFilter.setValue("All");
            minPriceField.clear();
            maxPriceField.clear();
            applyFilters();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSortWeight = new Button("↕ Sort by Weight");
        btnSortWeight.getStyleClass().add("btn-secondary");
        btnSortWeight.setOnAction(e -> handleSortByWeight());

        bar.getChildren().addAll(typeLabel, typeFilter, priceLabel, minPriceField, dash, maxPriceField, btnReset, spacer, btnSortWeight);
        return bar;
    }

    /**
     * Картка з TabPane: Weapons та Armor.
     */
    @SuppressWarnings("unchecked")
    private VBox createCatalogTabCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        Label title = new Label("Ammunition Catalog");
        title.getStyleClass().add("card-title");

        tabPane = new TabPane();
        tabPane.getStyleClass().add("catalog-tabpane");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefHeight(420);

        // --- Вкладка WEAPONS ---
        Tab tabWeapons = new Tab("⚔ Weapons");
        weaponsTable = new TableView<>();
        weaponsTable.setPlaceholder(new Label("No weapons found."));
        
        TableColumn<Ammunition, String> colWType = new TableColumn<>("Weapon Type");
        colWType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colWType.setPrefWidth(140);

        TableColumn<Ammunition, String> colWName = new TableColumn<>("Name");
        colWName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colWName.setPrefWidth(220);

        TableColumn<Ammunition, String> colWWeight = new TableColumn<>("Weight (kg)");
        colWWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f", cd.getValue().getWeight())));
        colWWeight.setPrefWidth(110);
        colWWeight.setComparator(Comparator.comparingDouble(s -> Double.parseDouble(s)));

        TableColumn<Ammunition, String> colWPrice = new TableColumn<>("Price ($)");
        colWPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.1f", cd.getValue().getPrice())));
        colWPrice.setPrefWidth(110);
        colWPrice.setComparator(Comparator.comparingDouble(s -> Double.parseDouble(s)));

        TableColumn<Ammunition, String> colWDamage = new TableColumn<>("Damage");
        colWDamage.setCellValueFactory(cd -> new SimpleStringProperty("+" + ((Weapon) cd.getValue()).getDamage()));
        colWDamage.setPrefWidth(120);

        weaponsTable.getColumns().addAll(colWType, colWName, colWWeight, colWPrice, colWDamage);
        
        SortedList<Ammunition> sortedWeapons = new SortedList<>(weaponsFiltered);
        sortedWeapons.comparatorProperty().bind(weaponsTable.comparatorProperty());
        weaponsTable.setItems(sortedWeapons);
        tabWeapons.setContent(weaponsTable);

        // --- Вкладка ARMOR ---
        Tab tabArmor = new Tab("🛡 Armor");
        armorTable = new TableView<>();
        armorTable.setPlaceholder(new Label("No armor found."));

        TableColumn<Ammunition, String> colAType = new TableColumn<>("Armor Type");
        colAType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colAType.setPrefWidth(140);

        TableColumn<Ammunition, String> colAName = new TableColumn<>("Name");
        colAName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colAName.setPrefWidth(220);

        TableColumn<Ammunition, String> colAWeight = new TableColumn<>("Weight (kg)");
        colAWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f", cd.getValue().getWeight())));
        colAWeight.setPrefWidth(110);
        colAWeight.setComparator(Comparator.comparingDouble(s -> Double.parseDouble(s)));

        TableColumn<Ammunition, String> colAPrice = new TableColumn<>("Price ($)");
        colAPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.1f", cd.getValue().getPrice())));
        colAPrice.setPrefWidth(110);
        colAPrice.setComparator(Comparator.comparingDouble(s -> Double.parseDouble(s)));

        TableColumn<Ammunition, String> colADefense = new TableColumn<>("Defense");
        colADefense.setCellValueFactory(cd -> new SimpleStringProperty("+" + ((Armor) cd.getValue()).getDefense()));
        colADefense.setPrefWidth(120);

        armorTable.getColumns().addAll(colAType, colAName, colAWeight, colAPrice, colADefense);

        SortedList<Ammunition> sortedArmor = new SortedList<>(armorFiltered);
        sortedArmor.comparatorProperty().bind(armorTable.comparatorProperty());
        armorTable.setItems(sortedArmor);
        tabArmor.setContent(armorTable);

        tabPane.getTabs().addAll(tabWeapons, tabArmor);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        card.getChildren().addAll(title, tabPane);
        return card;
    }

    /**
     * Нижня панель з кнопкою Equip.
     */
    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        Button btnEquip = new Button("⚔ Equip Selected Item");
        btnEquip.getStyleClass().add("btn-success");
        btnEquip.setStyle("-fx-font-size: 13px; -fx-padding: 10 24 10 24;");
        btnEquip.setOnAction(e -> handleEquip());

        Label hint = new Label("Equip rule: Active knight can carry up to 2 Weapons and 3 Armor pieces (1 Helmet, 1 Breastplate, 1 Leggings).");
        hint.getStyleClass().add("label-muted");

        bar.getChildren().addAll(btnEquip, hint);
        return bar;
    }

    // === Фільтри ===

    private void applyFilters() {
        String selectedType = typeFilter.getValue();
        String minText = minPriceField.getText().trim();
        String maxText = maxPriceField.getText().trim();

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

        try { if (!minText.isEmpty()) minPrice = Double.parseDouble(minText); } catch (NumberFormatException ignored) {}
        try { if (!maxText.isEmpty()) maxPrice = Double.parseDouble(maxText); } catch (NumberFormatException ignored) {}

        final double finalMin = minPrice;
        final double finalMax = maxPrice;

        // Фільтр для зброї
        weaponsFiltered.setPredicate(item -> {
            if (!(item instanceof Weapon)) return false;
            boolean typeMatch = selectedType.equals("All") || item.getClass().getSimpleName().equals(selectedType);
            boolean priceMatch = item.getPrice() >= finalMin && item.getPrice() <= finalMax;
            return typeMatch && priceMatch;
        });

        // Фільтр для броні
        armorFiltered.setPredicate(item -> {
            if (!(item instanceof Armor)) return false;
            boolean typeMatch = selectedType.equals("All") || item.getClass().getSimpleName().equals(selectedType);
            boolean priceMatch = item.getPrice() >= finalMin && item.getPrice() <= finalMax;
            return typeMatch && priceMatch;
        });
    }

    // === Сортування ===

    private void handleSortByWeight() {
        List<Ammunition> sorted = new ArrayList<>(catalogList);
        Collections.sort(sorted);
        catalogList.setAll(sorted);
        LoggerService.info("Catalog sorted by weight via GUI.");
    }

    // === Екіпірування ===

    private void handleEquip() {
        if (knightManager.getActiveKnight() == null) {
            showError("No active knight selected!\nGo to Knights tab and set one as active first.");
            return;
        }

        // Беремо предмет з активної вкладки
        Ammunition selected = null;
        if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
            selected = weaponsTable.getSelectionModel().getSelectedItem();
        } else {
            selected = armorTable.getSelectionModel().getSelectedItem();
        }

        if (selected == null) {
            showError("Please select an item from the active tab table.");
            return;
        }

        boolean success = knightManager.getActiveKnight().equip(selected);
        if (success) {
            LoggerService.info("Knight " + knightManager.getActiveKnight().getName() +
                    " equipped: " + selected.getName() + " via GUI.");
            onActiveKnightChanged.run();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("✓ Item Equipped!");
            alert.setContentText(selected.getName() + " added to " + knightManager.getActiveKnight().getName() + ".");
            alert.showAndWait();
        } else {
            LoggerService.info("Equip failure via GUI: " + selected.getName());
            showError("Cannot equip this item!\nPossible reasons:\n" +
                    "• Weight capacity exceeded\n" +
                    "• Already carrying 2 Weapons\n" +
                    "• Already equipped this type of Armor (max 1 Helmet, 1 Breastplate, 1 Leggings)");
        }
    }

    // === Допоміжні ===

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}
