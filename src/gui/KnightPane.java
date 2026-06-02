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
import model.equipment.Weapon;
import model.equipment.Armor;
import service.KnightManager;
import service.LoggerService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import command.Command;
import command.GuiCreateKnightCommand;
import command.GuiSetActiveKnightCommand;
import command.GuiDeleteKnightCommand;
import command.GuiUnequipItemCommand;

import java.util.Map;

public class KnightPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final Runnable onActiveKnightChanged;

    private TableView<Knight> knightTable;
    private ObservableList<Knight> knightList;

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

    private HBox createActionBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        Button btnCreate = new Button("+ Create Knight");
        btnCreate.getStyleClass().add("btn-primary");
        btnCreate.setOnAction(e -> handleCreate());

        Button btnSetActive = new Button("✓ Set Active");
        btnSetActive.getStyleClass().add("btn-secondary");
        btnSetActive.setOnAction(e -> handleSetActive());

        Button btnDelete = new Button("✕ Delete");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setOnAction(e -> handleDelete());

        bar.getChildren().addAll(btnCreate, btnSetActive, btnDelete);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createKnightTableCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        Label title = new Label("All Knights");
        title.getStyleClass().add("card-title");

        knightTable = new TableView<>();
        knightTable.setPlaceholder(new Label("No knights yet. Create one."));
        knightTable.setPrefHeight(260);
        knightTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Knight, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()));
        colId.setPrefWidth(50);

        TableColumn<Knight, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(160);

        TableColumn<Knight, String> colOrden = new TableColumn<>("Order");
        colOrden.setCellValueFactory(cd -> {
            String o = cd.getValue().getOrden();
            if ("Without Order".equals(o)) {
                return new SimpleStringProperty("Without Order");
            }
            return new SimpleStringProperty(o);
        });
        colOrden.setPrefWidth(140);

        TableColumn<Knight, String> colRank = new TableColumn<>("Rank");
        colRank.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRank().toString()));
        colRank.setPrefWidth(100);

        TableColumn<Knight, String> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(cd -> new SimpleStringProperty(
                String.format("%.2f / %.2f", cd.getValue().getCurrentWeight(), cd.getValue().getMaxWeightCapacity())));
        colWeight.setPrefWidth(120);

        TableColumn<Knight, String> colDamage = new TableColumn<>("Damage");
        colDamage.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getTotalDamage())));
        colDamage.setPrefWidth(80);

        TableColumn<Knight, String> colDefense = new TableColumn<>("Defense");
        colDefense.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getTotalDefense())));
        colDefense.setPrefWidth(80);

        TableColumn<Knight, String> colItems = new TableColumn<>("Items");
        colItems.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getEquipment().size())));
        colItems.setPrefWidth(60);

        knightTable.getColumns().addAll(colId, colName, colOrden, colRank, colWeight, colDamage, colDefense, colItems);
        knightTable.setItems(knightList);

        knightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            refreshEquipmentTable(newVal);
        });

        VBox.setVgrow(knightTable, Priority.ALWAYS);
        card.getChildren().addAll(title, knightTable);
        return card;
    }

    @SuppressWarnings("unchecked")
    private VBox createEquipmentCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        equipTitleLabel = new Label("Equipment — select a knight above");
        equipTitleLabel.getStyleClass().add("card-title");

        equipTable = new TableView<>();
        equipTable.setPlaceholder(new Label("No equipment."));
        equipTable.setPrefHeight(180);
        equipTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ammunition, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colType.setPrefWidth(120);

        TableColumn<Ammunition, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(200);
        colName.setCellFactory(column -> new TableCell<Ammunition, String>() {
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
                        var stream = KnightPane.class.getClassLoader().getResourceAsStream(path);

                        if (stream == null && !icon.toLowerCase().endsWith(".png")) {
                            path = "gui/icons/" + icon + ".png";
                            stream = KnightPane.class.getClassLoader().getResourceAsStream(path);
                        }

                        if (stream != null) {
                            imageView.setImage(new Image(stream));
                            setGraphic(container);
                            return;
                        } else {
                            System.out.println("[Ресурси KnightPane] Не знайдено іконку: out/" + path);
                        }
                    } catch (Exception e) {
                        System.err.println("[Помилка] Не вдалося завантажити іконку в KnightPane: " + e.getMessage());
                    }
                    imageView.setImage(null);
                    setGraphic(container);
                }
            }
        });

        TableColumn<Ammunition, String> colW = new TableColumn<>("Weight");
        colW.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f kg", cd.getValue().getWeight())));
        colW.setPrefWidth(100);

        TableColumn<Ammunition, String> colP = new TableColumn<>("Price");
        colP.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.1f", cd.getValue().getPrice())));
        colP.setPrefWidth(100);

        TableColumn<Ammunition, String> colDmg = new TableColumn<>("Damage");
        colDmg.setCellValueFactory(cd -> {
            Ammunition a = cd.getValue();
            if (a instanceof Weapon) {
                return new SimpleStringProperty("+" + ((Weapon) a).getDamage());
            }
            return new SimpleStringProperty("-");
        });
        colDmg.setPrefWidth(80);

        TableColumn<Ammunition, String> colDef = new TableColumn<>("Defense");
        colDef.setCellValueFactory(cd -> {
            Ammunition a = cd.getValue();
            if (a instanceof Armor) {
                return new SimpleStringProperty("+" + ((Armor) a).getDefense());
            }
            return new SimpleStringProperty("-");
        });
        colDef.setPrefWidth(80);

        equipTable.getColumns().addAll(colType, colName, colW, colP, colDmg, colDef);
        equipTable.setItems(equipList);

        Button btnUnequip = new Button("✕ Unequip Selected");
        btnUnequip.getStyleClass().add("btn-danger");
        btnUnequip.setId("btnUnequipKnightItem");
        btnUnequip.setOnAction(e -> handleUnequipItem());

        card.getChildren().addAll(equipTitleLabel, equipTable, btnUnequip);
        return card;
    }

    private void handleCreate() {
        Dialog<Knight> dialog = new Dialog<>();
        dialog.setTitle("Create New Knight");
        dialog.setHeaderText("Enter knight details:");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Knight name");
        nameField.setPrefWidth(250);

        TextField ordenField = new TextField();
        ordenField.setId("ordenField");
        ordenField.setPromptText("Order name");

        ComboBox<Rank> rankCombo = new ComboBox<>(FXCollections.observableArrayList(Rank.values()));
        rankCombo.setId("rankComboBox");
        rankCombo.setValue(Rank.NOVICE);
        rankCombo.setPrefWidth(250);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Order:"), 0, 1);
        grid.add(ordenField, 1, 1);

        CheckBox withoutOrderCheck = new CheckBox("Without Order");
        withoutOrderCheck.setId("withoutOrderCheck");
        withoutOrderCheck.setOnAction(e -> {
            ordenField.setDisable(withoutOrderCheck.isSelected());
            if (withoutOrderCheck.isSelected()) {
                ordenField.setText("");
            }
        });
        grid.add(withoutOrderCheck, 2, 1);

        grid.add(new Label("Rank:"), 0, 2);
        grid.add(rankCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        nameField.requestFocus();

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String name = nameField.getText().trim();
                String orden = ordenField.getText().trim();
                if (name.isEmpty()) {
                    showError("Name cannot be empty.");
                    return null;
                }
                if (withoutOrderCheck.isSelected()) {
                    orden = "Without Order";
                } else if (orden.isEmpty()) {
                    orden = "Unknown";
                }
                return new Knight(name, orden, rankCombo.getValue());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(knight -> {
            Command cmd = new GuiCreateKnightCommand(knightManager, knight);
            cmd.execute();
            refreshTable();
            onActiveKnightChanged.run();
        });
    }

    private void handleSetActive() {
        Knight selected = knightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a knight from the table first.");
            return;
        }
        Command cmd = new GuiSetActiveKnightCommand(knightManager, selected.getId(), selected.getName());
        cmd.execute();
        onActiveKnightChanged.run();
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
                Command cmd = new GuiDeleteKnightCommand(knightManager, selected.getId(), selected.getName());
                cmd.execute();
                refreshTable();
                onActiveKnightChanged.run();
            }
        });
    }

    private void handleUnequipItem() {
        Knight selected = knightTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a knight from the table first.");
            return;
        }

        Ammunition selectedItem = equipTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Please select an item from the equipment table below to unequip.");
            return;
        }

        Command cmd = new GuiUnequipItemCommand(knightManager, selected, selectedItem);
        cmd.execute();
        refreshTable();
        onActiveKnightChanged.run();
    }

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