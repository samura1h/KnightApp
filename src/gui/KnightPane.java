package gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Knight;
import model.Rank;
import model.equipment.Ammunition;
import service.KnightManager;
import service.LoggerService;

import java.util.Map;

/**
 * Панель управління лицарями: створення, завантаження, видалення, вибір активного.
 */
public class KnightPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final Runnable onActiveKnightChanged;

    // Таблиця лицарів
    private TableView<Knight> knightTable;
    private ObservableList<Knight> knightList;

    // Таблиця екіпірування обраного лицаря
    private TableView<Ammunition> equipTable;
    private ObservableList<Ammunition> equipList;
    private Label equipTitleLabel;

    public KnightPane(KnightManager knightManager, Runnable onActiveKnightChanged) {
        this.knightManager = knightManager;
        this.onActiveKnightChanged = onActiveKnightChanged;
        this.knightList = FXCollections.observableArrayList();
        this.equipList = FXCollections.observableArrayList();

        root = new VBox(20);
        root.setPadding(new Insets(0));
        root.getChildren().addAll(createActionBar(), createKnightTableCard(), createEquipmentCard());

        refreshTable();
    }

    /**
     * Верхня панель з кнопками дій.
     */
    private HBox createActionBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        Button btnCreate = new Button("+ Create Knight");
        btnCreate.getStyleClass().add("btn-primary");
        btnCreate.setOnAction(e -> handleCreate());

        Button btnLoad = new Button("📂 Load from File");
        btnLoad.getStyleClass().add("btn-secondary");
        btnLoad.setOnAction(e -> handleLoad());

        Button btnSetActive = new Button("✓ Set Active");
        btnSetActive.getStyleClass().add("btn-secondary");
        btnSetActive.setOnAction(e -> handleSetActive());

        Button btnDelete = new Button("✕ Delete");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setOnAction(e -> handleDelete());

        bar.getChildren().addAll(btnCreate, btnLoad, btnSetActive, btnDelete);
        return bar;
    }

    /**
     * Картка з таблицею лицарів.
     */
    @SuppressWarnings("unchecked")
    private VBox createKnightTableCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        Label title = new Label("All Knights");
        title.getStyleClass().add("card-title");

        knightTable = new TableView<>();
        knightTable.setPlaceholder(new Label("No knights yet. Create one or load from file."));
        knightTable.setPrefHeight(260);

        // Колонки
        TableColumn<Knight, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()));
        colId.setPrefWidth(50);

        TableColumn<Knight, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(160);

        TableColumn<Knight, String> colOrden = new TableColumn<>("Order");
        colOrden.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getOrden()));
        colOrden.setPrefWidth(140);

        TableColumn<Knight, String> colRank = new TableColumn<>("Rank");
        colRank.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRank().toString()));
        colRank.setPrefWidth(100);

        TableColumn<Knight, String> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(cd -> new SimpleStringProperty(
                String.format("%.2f / %.2f", cd.getValue().getCurrentWeight(), cd.getValue().getMaxWeightCapacity())));
        colWeight.setPrefWidth(120);

        TableColumn<Knight, String> colDefense = new TableColumn<>("Defense");
        colDefense.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getTotalDefense())));
        colDefense.setPrefWidth(70);

        TableColumn<Knight, String> colItems = new TableColumn<>("Items");
        colItems.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getEquipment().size())));
        colItems.setPrefWidth(60);

        knightTable.getColumns().addAll(colId, colName, colOrden, colRank, colWeight, colDefense, colItems);
        knightTable.setItems(knightList);

        // При виборі лицаря показуємо його екіпірування
        knightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            refreshEquipmentTable(newVal);
        });

        VBox.setVgrow(knightTable, Priority.ALWAYS);
        card.getChildren().addAll(title, knightTable);
        return card;
    }

    /**
     * Картка з екіпіруванням обраного лицаря.
     */
    @SuppressWarnings("unchecked")
    private VBox createEquipmentCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        equipTitleLabel = new Label("Equipment — select a knight above");
        equipTitleLabel.getStyleClass().add("card-title");

        equipTable = new TableView<>();
        equipTable.setPlaceholder(new Label("No equipment."));
        equipTable.setPrefHeight(180);

        TableColumn<Ammunition, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colType.setPrefWidth(120);

        TableColumn<Ammunition, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(200);

        TableColumn<Ammunition, String> colW = new TableColumn<>("Weight");
        colW.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f kg", cd.getValue().getWeight())));
        colW.setPrefWidth(100);

        TableColumn<Ammunition, String> colP = new TableColumn<>("Price");
        colP.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.1f", cd.getValue().getPrice())));
        colP.setPrefWidth(100);

        equipTable.getColumns().addAll(colType, colName, colW, colP);
        equipTable.setItems(equipList);

        card.getChildren().addAll(equipTitleLabel, equipTable);
        return card;
    }

    // === Обробники дій ===

    private void handleCreate() {
        // Діалог створення лицаря
        Dialog<Knight> dialog = new Dialog<>();
        dialog.setTitle("Create New Knight");
        dialog.setHeaderText("Enter knight details:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField nameField = new TextField();
        nameField.setPromptText("Knight name");
        nameField.setPrefWidth(250);

        TextField ordenField = new TextField();
        ordenField.setPromptText("Order name");

        ComboBox<Rank> rankCombo = new ComboBox<>(FXCollections.observableArrayList(Rank.values()));
        rankCombo.setValue(Rank.NOVICE);
        rankCombo.setPrefWidth(250);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Order:"), 0, 1);
        grid.add(ordenField, 1, 1);
        grid.add(new Label("Rank:"), 0, 2);
        grid.add(rankCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Фокус на поле імені
        nameField.requestFocus();

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String name = nameField.getText().trim();
                String orden = ordenField.getText().trim();
                if (name.isEmpty()) {
                    showError("Name cannot be empty.");
                    return null;
                }
                return new Knight(name, orden.isEmpty() ? "Unknown" : orden, rankCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(knight -> {
            knightManager.addKnight(knight);
            LoggerService.info("Created new knight via GUI: " + knight.getName());
            refreshTable();
            onActiveKnightChanged.run();
        });
    }

    private void handleLoad() {
        if (!knightManager.getAllKnights().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Load from File");
            confirm.setHeaderText("Knights already exist in memory.");
            confirm.setContentText("Loading will merge data from knights.txt. Continue?");
            var result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;
        }

        knightManager.loadFromDisk();
        LoggerService.info("User loaded knights from disk via GUI.");
        refreshTable();

        if (knightManager.getAllKnights().isEmpty()) {
            showInfo("No knights found in save file.");
        } else {
            showInfo("Loaded " + knightManager.getAllKnights().size() + " knight(s) from file.");
        }
    }

    private void handleSetActive() {
        Knight selected = knightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a knight from the table first.");
            return;
        }
        knightManager.setActiveKnight(selected.getId());
        LoggerService.info("Active knight set via GUI: " + selected.getName());
        onActiveKnightChanged.run();
        showInfo("Active knight: " + selected.getName());
    }

    private void handleDelete() {
        Knight selected = knightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a knight from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Knight");
        confirm.setHeaderText("⚠ Delete \"" + selected.getName() + "\"?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                knightManager.removeKnight(selected.getId());
                LoggerService.info("Deleted knight via GUI: " + selected.getName());
                refreshTable();
                onActiveKnightChanged.run();
            }
        });
    }

    // === Оновлення таблиць ===

    public void refreshTable() {
        Knight selected = knightTable.getSelectionModel().getSelectedItem();

        knightList.clear();
        Map<Integer, Knight> all = knightManager.getAllKnights();
        knightList.addAll(all.values());

        if (selected != null) {
            for (Knight k : knightList) {
                if (k.getId() == selected.getId()) {
                    knightTable.getSelectionModel().select(k);
                    break;
                }
            }
        }

        knightTable.refresh();

        Knight currentSelected = knightTable.getSelectionModel().getSelectedItem();
        refreshEquipmentTable(currentSelected);
    }

    private void refreshEquipmentTable(Knight knight) {
        equipList.clear();
        if (knight != null) {
            equipTitleLabel.setText("Equipment — " + knight.getName());
            equipList.addAll(knight.getEquipment());
        } else {
            equipTitleLabel.setText("Equipment — select a knight above");
        }
    }

    // === Допоміжні методи ===

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getRoot() {
        return root;
    }
}
