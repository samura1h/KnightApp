package gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;
import repository.EquipmentRepository;
import command.Command;
import command.GuiEquipKnightCommand;
import command.GuiFindEquipmentByPriceCommand;
import command.GuiSortEquipmentCommand;
import service.KnightManager;
import service.LoggerService;

public class EquipmentPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final EquipmentRepository equipRepo;
    private final Runnable onActiveKnightChanged;

    private ObservableList<Ammunition> catalogList;
    private FilteredList<Ammunition> weaponsFiltered;
    private FilteredList<Ammunition> armorFiltered;

    private TabPane tabPane;
    private TableView<Ammunition> weaponsTable;
    private TableView<Ammunition> armorTable;

    private ComboBox<String> typeFilter;
    private TextField minPriceField;
    private TextField maxPriceField;

    public EquipmentPane(KnightManager knightManager, EquipmentRepository equipRepo, Runnable onActiveKnightChanged) {
        this.knightManager = knightManager;
        this.equipRepo = equipRepo;
        this.onActiveKnightChanged = onActiveKnightChanged;

        catalogList = FXCollections.observableArrayList(equipRepo.getAll());

        weaponsFiltered = new FilteredList<>(catalogList, item -> item instanceof Weapon);
        armorFiltered = new FilteredList<>(catalogList, item -> item instanceof Armor);

        root = new VBox(20);
        root.setPadding(new Insets(0));
        root.getChildren().addAll(createFilterBar(), createCatalogTabCard(), createActionBar());

        applyFilters();
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label("Type:");
        typeLabel.setStyle("-fx-text-fill: #666666;");

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

        Label priceLabel = new Label("Price:");
        priceLabel.setStyle("-fx-text-fill: #666666;");

        minPriceField = new TextField();
        minPriceField.setPromptText("Min");
        minPriceField.setPrefWidth(80);

        Label dash = new Label("—");
        dash.setStyle("-fx-text-fill: #999999;");

        maxPriceField = new TextField();
        maxPriceField.setPromptText("Max");
        maxPriceField.setPrefWidth(80);

        typeFilter.setOnAction(e -> applyFilters());
        minPriceField.textProperty().addListener((obs, o, n) -> applyFilters());
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

        bar.getChildren().addAll(typeLabel, typeFilter, priceLabel, minPriceField, dash, maxPriceField, btnReset, spacer);
        return bar;
    }

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

        Tab tabWeapons = new Tab("⚔ Weapons");
        weaponsTable = new TableView<>();
        weaponsTable.setPlaceholder(new Label("No weapons found."));
        weaponsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ammunition, String> colWType = new TableColumn<>("Weapon Type");
        colWType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colWType.setPrefWidth(140);

        TableColumn<Ammunition, String> colWName = new TableColumn<>("Name");
        colWName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colWName.setPrefWidth(220);
        colWName.setCellFactory(column -> new TableCell<Ammunition, String>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(8, imageView, label);

            {
                container.setAlignment(Pos.CENTER_LEFT);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Ammunition ammo = (Ammunition) getTableRow().getItem();
                    label.setText(ammo.getName());

                    String icon = ammo.getClass().getSimpleName();

                    try {
                        String path = "gui/icons/" + icon;
                        var stream = EquipmentPane.class.getClassLoader().getResourceAsStream(path);

                        if (stream == null && !icon.toLowerCase().endsWith(".png")) {
                            path = "gui/icons/" + icon + ".png";
                            stream = EquipmentPane.class.getClassLoader().getResourceAsStream(path);
                        }

                        if (stream != null) {
                            imageView.setImage(new Image(stream));
                            setGraphic(container);
                            return;
                        } else {
                            System.out.println("[Ресурси] Не знайдено файл іконки для зброї: out/" + path);
                        }
                    } catch (Exception e) {
                        System.err.println("[Ресурси] Помилка завантаження іконки зброї: " + e.getMessage());
                    }
                    imageView.setImage(null);
                    setGraphic(container);
                }
            }
        });

        TableColumn<Ammunition, String> colWWeight = new TableColumn<>("Weight (kg)");
        colWWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format(Locale.US, "%.2f", cd.getValue().getWeight())));
        colWWeight.setPrefWidth(110);
        colWWeight.setComparator(Comparator.comparingDouble(Double::parseDouble));

        TableColumn<Ammunition, String> colWPrice = new TableColumn<>("Price ($)");
        colWPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format(Locale.US, "%.1f", cd.getValue().getPrice())));
        colWPrice.setPrefWidth(110);
        colWPrice.setComparator(Comparator.comparingDouble(Double::parseDouble));

        TableColumn<Ammunition, String> colWDamage = new TableColumn<>("Damage");
        colWDamage.setCellValueFactory(cd -> new SimpleStringProperty("+" + ((Weapon) cd.getValue()).getDamage()));
        colWDamage.setPrefWidth(120);

        weaponsTable.getColumns().addAll(colWType, colWName, colWWeight, colWPrice, colWDamage);

        weaponsTable.setItems(weaponsFiltered);
        weaponsTable.setOnSort(event -> {
            event.consume();
            if (!weaponsTable.getSortOrder().isEmpty()) {
                TableColumn<Ammunition, ?> col = weaponsTable.getSortOrder().get(0);
                String criteria = col.getText();
                if ("Weapon Type".equals(criteria)) criteria = "Weapon Type";
                else if ("Name".equals(criteria)) criteria = "Name";
                else if (criteria.contains("Weight")) criteria = "Weight";
                else if (criteria.contains("Price")) criteria = "Price";
                else if ("Damage".equals(criteria)) criteria = "Damage";

                boolean ascending = col.getSortType() == TableColumn.SortType.ASCENDING;
                Command sortCmd = new GuiSortEquipmentCommand(catalogList, criteria, ascending);
                sortCmd.execute();
            }
        });
        tabWeapons.setContent(weaponsTable);

        Tab tabArmor = new Tab("🛡 Armor");
        armorTable = new TableView<>();
        armorTable.setPlaceholder(new Label("No armor found."));
        armorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ammunition, String> colAType = new TableColumn<>("Armor Type");
        colAType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colAType.setPrefWidth(140);

        TableColumn<Ammunition, String> colAName = new TableColumn<>("Name");
        colAName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colAName.setPrefWidth(220);
        colAName.setCellFactory(column -> new TableCell<Ammunition, String>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(8, imageView, label);

            {
                container.setAlignment(Pos.CENTER_LEFT);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Ammunition ammo = (Ammunition) getTableRow().getItem();
                    label.setText(ammo.getName());

                    String icon = ammo.getClass().getSimpleName();

                    try {
                        String path = "gui/icons/" + icon;
                        var stream = EquipmentPane.class.getClassLoader().getResourceAsStream(path);

                        if (stream == null && !icon.toLowerCase().endsWith(".png")) {
                            path = "gui/icons/" + icon + ".png";
                            stream = EquipmentPane.class.getClassLoader().getResourceAsStream(path);
                        }

                        if (stream != null) {
                            imageView.setImage(new Image(stream));
                            setGraphic(container);
                            return;
                        } else {
                            System.out.println("[Ресурси] Не знайдено файл іконки для броні: out/" + path);
                        }
                    } catch (Exception e) {
                        System.err.println("[Ресурси] Помилка завантаження іконки броні: " + e.getMessage());
                    }
                    imageView.setImage(null);
                    setGraphic(container);
                }
            }
        });

        TableColumn<Ammunition, String> colAWeight = new TableColumn<>("Weight (kg)");
        colAWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format(Locale.US, "%.2f", cd.getValue().getWeight())));
        colAWeight.setPrefWidth(110);
        colAWeight.setComparator(Comparator.comparingDouble(Double::parseDouble));

        TableColumn<Ammunition, String> colAPrice = new TableColumn<>("Price ($)");
        colAPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format(Locale.US, "%.1f", cd.getValue().getPrice())));
        colAPrice.setPrefWidth(110);
        colAPrice.setComparator(Comparator.comparingDouble(Double::parseDouble));

        TableColumn<Ammunition, String> colADefense = new TableColumn<>("Defense");
        colADefense.setCellValueFactory(cd -> new SimpleStringProperty("+" + ((Armor) cd.getValue()).getDefense()));
        colADefense.setPrefWidth(120);

        armorTable.getColumns().addAll(colAType, colAName, colAWeight, colAPrice, colADefense);

        armorTable.setItems(armorFiltered);
        armorTable.setOnSort(event -> {
            event.consume();
            if (!armorTable.getSortOrder().isEmpty()) {
                TableColumn<Ammunition, ?> col = armorTable.getSortOrder().get(0);
                String criteria = col.getText();
                if ("Armor Type".equals(criteria)) criteria = "Armor Type";
                else if ("Name".equals(criteria)) criteria = "Name";
                else if (criteria.contains("Weight")) criteria = "Weight";
                else if (criteria.contains("Price")) criteria = "Price";
                else if ("Defense".equals(criteria)) criteria = "Defense";

                boolean ascending = col.getSortType() == TableColumn.SortType.ASCENDING;
                Command sortCmd = new GuiSortEquipmentCommand(catalogList, criteria, ascending);
                sortCmd.execute();
            }
        });
        tabArmor.setContent(armorTable);

        tabPane.getTabs().addAll(tabWeapons, tabArmor);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        card.getChildren().addAll(title, tabPane);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);

        Button btnEquip = new Button("⚔ Equip Selected Item");
        btnEquip.getStyleClass().add("btn-success");
        btnEquip.setStyle("-fx-font-size: 13px; -fx-padding: 10 24 10 24; -fx-background-radius: 22px;");
        btnEquip.setOnAction(e -> handleEquip());

        Label hint = new Label("Equip rule: Active knight can carry up to 2 Weapons and 3 Armor pieces (1 Helmet, 1 Breastplate, 1 Leggings).");
        hint.getStyleClass().add("label-muted");

        bar.getChildren().addAll(btnEquip, hint);
        return bar;
    }

    private void applyFilters() {
        if (typeFilter == null || minPriceField == null || maxPriceField == null) {
            return;
        }

        String selectedType = typeFilter.getValue();
        if (selectedType == null) {
            selectedType = "All";
        }

        String minText = minPriceField.getText() != null ? minPriceField.getText().trim() : "";
        String maxText = maxPriceField.getText() != null ? maxPriceField.getText().trim() : "";

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!minText.isEmpty()) minPrice = Double.parseDouble(minText);
        } catch (NumberFormatException ignored) {}
        try {
            if (!maxText.isEmpty()) maxPrice = Double.parseDouble(maxText);
        } catch (NumberFormatException ignored) {}

        final double finalMin = minPrice;
        final double finalMax = maxPrice;
        final String finalType = selectedType;

        Command filterCmd = new GuiFindEquipmentByPriceCommand(weaponsFiltered, armorFiltered, finalType, finalMin, finalMax);
        filterCmd.execute();
    }

    public void refresh() {
        catalogList.setAll(equipRepo.getAll());
        applyFilters();
    }

    private void handleEquip() {
        if (knightManager.getActiveKnight() == null) {
            showError("No active knight selected!\nGo to Knights tab and set one as active first.");
            return;
        }

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

        final Ammunition itemToEquip = selected;
        Command cmd = new GuiEquipKnightCommand(knightManager, itemToEquip, () -> {
            onActiveKnightChanged.run();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("✓ Item Equipped!");
            alert.setContentText(itemToEquip.getName() + " added to " + knightManager.getActiveKnight().getName() + ".");
            alert.showAndWait();
        }, () -> {
            showError("Cannot equip this item!\nPossible reasons:\n" +
                    "• Weight capacity exceeded\n" +
                    "• Already carrying 2 Weapons\n" +
                    "• Already equipped this type of Armor (max 1 Helmet, 1 Breastplate, 1 Leggings)");
        });
        cmd.execute();
    }

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