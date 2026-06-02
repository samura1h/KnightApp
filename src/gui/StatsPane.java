package gui;

import java.util.Comparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Knight;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;
import model.equipment.Helmet;
import model.equipment.Breastplate;
import model.equipment.Greaves;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService;
import command.Command;
import command.GuiCalcEquipmentCostCommand;
import command.GuiCalcDefenseCommand;
import command.GuiCalcDamageCommand;

public class StatsPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final EquipmentRepository equipRepo;

    private Label nameLabel, ordenLabel, rankLabel, idLabel;
    private Label weightValueLabel, defenseValueLabel, damageValueLabel, itemsValueLabel, costValueLabel, strengthValueLabel;
    private ProgressBar weightBar;
    private Label weightBarLabel;
    private TableView<Ammunition> equipTable;
    private ObservableList<Ammunition> equipList;
    private VBox statsContent;
    private Label emptyStateLabel;

    private Label slotHelmetStatus, slotBreastplateStatus, slotGreavesStatus, slotWeapon1Status, slotWeapon2Status;
    private Label slotHelmetName, slotBreastplateName, slotGreavesName, slotWeapon1Name, slotWeapon2Name;
    private ImageView slotHelmetIcon, slotBreastplateIcon, slotGreavesIcon, slotWeapon1Icon, slotWeapon2Icon;

    public StatsPane(KnightManager knightManager, EquipmentRepository equipRepo) {
        this.knightManager = knightManager;
        this.equipRepo = equipRepo;
        this.equipList = FXCollections.observableArrayList();

        root = new VBox(20);
        root.setPadding(new Insets(20));

        emptyStateLabel = new Label("Please select an active knight in the 'Knights' tab to view stats.");
        emptyStateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777777; -fx-font-style: italic;");
        emptyStateLabel.setAlignment(Pos.CENTER);

        statsContent = new VBox(20);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(30);
        infoGrid.setVgap(10);

        nameLabel = createValueLabel();
        ordenLabel = createValueLabel();
        rankLabel = createValueLabel();
        idLabel = createValueLabel("-fx-font-size: 11px; -fx-text-fill: #999999;");

        infoGrid.add(createHeaderLabel("Name:"), 0, 0);
        infoGrid.add(nameLabel, 1, 0);
        infoGrid.add(createHeaderLabel("Order:"), 0, 1);
        infoGrid.add(ordenLabel, 1, 1);
        infoGrid.add(createHeaderLabel("Rank:"), 0, 2);
        infoGrid.add(rankLabel, 1, 2);
        infoGrid.add(idLabel, 1, 3);

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(40);
        metricsGrid.setVgap(15);
        metricsGrid.setPadding(new Insets(10, 0, 10, 0));

        strengthValueLabel = createMetricValue();
        weightValueLabel = createMetricValue();
        defenseValueLabel = createMetricValue();
        damageValueLabel = createMetricValue();
        itemsValueLabel = createMetricValue();

        metricsGrid.add(createHeaderLabel("Max Weight:"), 0, 0);
        metricsGrid.add(strengthValueLabel, 1, 0);
        metricsGrid.add(createHeaderLabel("Total Defense:"), 2, 0);
        metricsGrid.add(defenseValueLabel, 3, 0);
        metricsGrid.add(createHeaderLabel("Total Items:"), 4, 0);
        metricsGrid.add(itemsValueLabel, 5, 0);

        metricsGrid.add(createHeaderLabel("Current Weight:"), 0, 1);
        metricsGrid.add(weightValueLabel, 1, 1);
        metricsGrid.add(createHeaderLabel("Total Damage:"), 2, 1);
        metricsGrid.add(damageValueLabel, 3, 1);

        VBox weightBox = new VBox(5);
        weightBarLabel = new Label("Weight Capacity: 0.00 / 0.00 kg");
        weightBarLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555;");
        weightBar = new ProgressBar(0);
        weightBar.setMaxWidth(Double.MAX_VALUE);
        weightBar.setPrefHeight(18);
        weightBox.getChildren().addAll(weightBarLabel, weightBar);

        HBox topRow = new HBox(40, infoGrid, metricsGrid);
        topRow.getStyleClass().add("card");
        HBox.setHgrow(metricsGrid, Priority.ALWAYS);

        VBox equipmentSlotsCard = new VBox(10);
        equipmentSlotsCard.getStyleClass().add("card");

        Label slotsTitle = new Label("Equipped Slots Checklist");
        slotsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane slotsGrid = new GridPane();
        slotsGrid.setHgap(20);
        slotsGrid.setVgap(8);
        slotsGrid.setPadding(new Insets(5, 0, 5, 0));

        slotHelmetIcon = createSlotIcon();
        slotBreastplateIcon = createSlotIcon();
        slotGreavesIcon = createSlotIcon();
        slotWeapon1Icon = createSlotIcon();
        slotWeapon2Icon = createSlotIcon();

        slotHelmetStatus = new Label();
        slotBreastplateStatus = new Label();
        slotGreavesStatus = new Label();
        slotWeapon1Status = new Label();
        slotWeapon2Status = new Label();

        slotHelmetName = new Label();
        slotBreastplateName = new Label();
        slotGreavesName = new Label();
        slotWeapon1Name = new Label();
        slotWeapon2Name = new Label();

        addSlotRow(slotsGrid, "Helmet:", slotHelmetStatus, slotHelmetIcon, slotHelmetName, 0);
        addSlotRow(slotsGrid, "Breastplate:", slotBreastplateStatus, slotBreastplateIcon, slotBreastplateName, 1);
        addSlotRow(slotsGrid, "Greaves:", slotGreavesStatus, slotGreavesIcon, slotGreavesName, 2);
        addSlotRow(slotsGrid, "First Weapon:", slotWeapon1Status, slotWeapon1Icon, slotWeapon1Name, 3);
        addSlotRow(slotsGrid, "Second Weapon:", slotWeapon2Status, slotWeapon2Icon, slotWeapon2Name, 4);

        equipmentSlotsCard.getChildren().addAll(slotsTitle, slotsGrid);

        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        Label tableTitle = new Label("Current Equipment Inventory");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        equipTable = new TableView<>();
        equipTable.setPlaceholder(new Label("This knight has no equipment equipped."));
        equipTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Ammunition, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colType.setPrefWidth(120);

        TableColumn<Ammunition, String> colName = new TableColumn<>("Item Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(200);

        TableColumn<Ammunition, String> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f kg", cd.getValue().getWeight())));
        colWeight.setPrefWidth(100);

        TableColumn<Ammunition, String> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.1f", cd.getValue().getPrice())));
        colPrice.setPrefWidth(100);

        TableColumn<Ammunition, String> colStat = new TableColumn<>("Stat Effects");
        colStat.setCellValueFactory(cd -> {
            Ammunition a = cd.getValue();
            if (a instanceof Weapon) {
                return new SimpleStringProperty("Damage: +" + ((Weapon) a).getDamage());
            } else if (a instanceof Armor) {
                return new SimpleStringProperty("Defense: +" + ((Armor) a).getDefense());
            }
            return new SimpleStringProperty("");
        });
        colStat.setPrefWidth(150);

        equipTable.getColumns().addAll(colType, colName, colWeight, colPrice, colStat);
        equipTable.setItems(equipList);

        HBox bottomBar = new HBox(15);
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        Button btnUnequip = new Button("Unequip Selected Item");
        btnUnequip.getStyleClass().add("btn-danger");
        btnUnequip.setOnAction(e -> handleUnequipItem());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox costCard = new VBox(2);
        costCard.setAlignment(Pos.CENTER_RIGHT);
        Label costTitle = new Label("TOTAL EQUIPMENT VALUE");
        costTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888; -fx-font-weight: bold;");
        costValueLabel = new Label("$0.0");
        costValueLabel.setId("equipCostValue");
        costValueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        costCard.getChildren().addAll(costTitle, costValueLabel);

        bottomBar.getChildren().addAll(btnUnequip, spacer, costCard);
        tableCard.getChildren().addAll(tableTitle, equipTable, bottomBar);

        statsContent.getChildren().addAll(topRow, weightBox, equipmentSlotsCard, tableCard);
        VBox.setVgrow(statsContent, Priority.ALWAYS);

        root.getChildren().add(emptyStateLabel);
        refresh();
    }

    private ImageView createSlotIcon() {
        ImageView iv = new ImageView();
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        iv.setPreserveRatio(true);
        return iv;
    }

    private void addSlotRow(GridPane grid, String slotLabelText, Label statusLabel, ImageView iconView, Label nameLabel, int rowIndex) {
        Label titleLabel = new Label(slotLabelText);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555; -fx-font-size: 12px;");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox itemBox = new HBox(8, iconView, nameLabel);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        nameLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic; -fx-font-size: 12px;");

        grid.add(titleLabel, 0, rowIndex);
        grid.add(statusLabel, 1, rowIndex);
        grid.add(itemBox, 2, rowIndex);
    }

    public void refresh() {
        Knight k = knightManager.getActiveKnight();

        if (k == null) {
            root.getChildren().setAll(emptyStateLabel);
            return;
        }

        if (!root.getChildren().contains(statsContent)) {
            root.getChildren().setAll(statsContent);
        }

        nameLabel.setText(k.getName());
        ordenLabel.setText(k.getOrden());
        rankLabel.setText(k.getRank().toString());
        idLabel.setText("System ID: " + k.getId());

        double currentWeight = k.getCurrentWeight();
        int itemsCount = k.getEquipment().size();
        double maxCapacity = k.getMaxWeightCapacity();

        strengthValueLabel.setText(String.format("%.2f kg", maxCapacity));
        weightValueLabel.setText(String.format("%.2f kg", currentWeight));
        itemsValueLabel.setText(String.valueOf(itemsCount));

        Command calcDefenseCmd = new GuiCalcDefenseCommand(k, defenseVal -> {
            defenseValueLabel.setText(String.format("+%d", defenseVal));
        });
        calcDefenseCmd.execute();

        Command calcDamageCmd = new GuiCalcDamageCommand(k, damageVal -> {
            damageValueLabel.setText(String.format("+%d", damageVal));
        });
        calcDamageCmd.execute();

        weightBarLabel.setText(String.format("Weight Capacity: %.2f / %.2f kg", currentWeight, maxCapacity));
        double progress = maxCapacity > 0 ? (currentWeight / maxCapacity) : 0;
        weightBar.setProgress(progress);

        if (progress > 1.0) {
            weightBar.setStyle("-fx-accent: #E53935;");
        } else if (progress > 0.7) {
            weightBar.setStyle("-fx-accent: #FFA726;");
        } else {
            weightBar.setStyle("-fx-accent: #757575;");
        }

        Ammunition helmetObj = null;
        Ammunition breastplateObj = null;
        Ammunition greavesObj = null;
        Ammunition weapon1Obj = null;
        Ammunition weapon2Obj = null;

        int weaponCounter = 0;

        for (Ammunition item : k.getEquipment()) {
            if (item instanceof Helmet) {
                helmetObj = item;
            } else if (item instanceof Breastplate) {
                breastplateObj = item;
            } else if (item instanceof Greaves) {
                greavesObj = item;
            } else if (item instanceof Weapon) {
                weaponCounter++;
                if (weaponCounter == 1) {
                    weapon1Obj = item;
                } else if (weaponCounter == 2) {
                    weapon2Obj = item;
                }
            }
        }

        updateSlotUI(slotHelmetStatus, slotHelmetIcon, slotHelmetName, helmetObj);
        updateSlotUI(slotBreastplateStatus, slotBreastplateIcon, slotBreastplateName, breastplateObj);
        updateSlotUI(slotGreavesStatus, slotGreavesIcon, slotGreavesName, greavesObj);
        updateSlotUI(slotWeapon1Status, slotWeapon1Icon, slotWeapon1Name, weapon1Obj);
        updateSlotUI(slotWeapon2Status, slotWeapon2Icon, slotWeapon2Name, weapon2Obj);

        equipList.clear();
        equipList.addAll(k.getEquipment());

        Command calcCostCmd = new GuiCalcEquipmentCostCommand(k, totalCost -> {
            costValueLabel.setText(String.format("$%.1f", totalCost));
        });
        calcCostCmd.execute();

        LoggerService.info("Viewed knight status via GUI: " + k.getName());
    }

    private void updateSlotUI(Label statusLabel, ImageView iconView, Label nameLabel, Ammunition item) {
        if (item != null) {
            statusLabel.setText("✓");
            statusLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 14px;");

            nameLabel.setText(item.getName());
            nameLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 12px;");

            String iconName = item.getClass().getSimpleName();
            try {
                String path = "gui/icons/" + iconName;
                var stream = StatsPane.class.getClassLoader().getResourceAsStream(path);

                if (stream == null && !iconName.toLowerCase().endsWith(".png")) {
                    path = "gui/icons/" + iconName + ".png";
                    stream = StatsPane.class.getClassLoader().getResourceAsStream(path);
                }

                if (stream != null) {
                    iconView.setImage(new Image(stream));
                } else {
                    iconView.setImage(null);
                }
            } catch (Exception e) {
                iconView.setImage(null);
            }
        } else {
            statusLabel.setText("✘");
            statusLabel.setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold; -fx-font-size: 14px;");

            iconView.setImage(null);
            nameLabel.setText("Empty");
            nameLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic; -fx-font-size: 12px;");
        }
    }

    private void handleUnequipItem() {
        Knight k = knightManager.getActiveKnight();
        if (k == null) {
            return;
        }

        Ammunition selectedItem = equipTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Please select an item from the table to unequip.");
            return;
        }

        k.getEquipment().remove(selectedItem);
        knightManager.saveKnight(k);
        LoggerService.info("Unequipped item via GUI: " + selectedItem.getName() + " from active knight: " + k.getName());
        refresh();
    }

    private Label createHeaderLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555;");
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    private Label createValueLabel() {
        return createValueLabel("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
    }

    private Label createValueLabel(String style) {
        Label l = new Label();
        l.setStyle(style);
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
    }

    private Label createMetricValue() {
        Label l = new Label("0");
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        l.setMinWidth(Region.USE_PREF_SIZE);
        return l;
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