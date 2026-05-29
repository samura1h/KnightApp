package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Knight;
import model.equipment.Ammunition;
import model.equipment.Armor;
import model.equipment.Weapon;
import repository.EquipmentRepository;
import service.KnightManager;
import service.LoggerService;

/**
 * Панель статусу лицаря: статистика, прогрес-бари, екіпірування, вартість.
 */
public class StatsPane {

    private final VBox root;
    private final KnightManager knightManager;
    private final EquipmentRepository equipRepo;

    // Елементи UI для оновлення
    private Label nameLabel, ordenLabel, rankLabel, idLabel;
    private Label weightValueLabel, defenseValueLabel, itemsValueLabel, costValueLabel;
    private ProgressBar weightBar;
    private Label weightBarLabel;
    private TableView<Ammunition> equipTable;
    private ObservableList<Ammunition> equipList;
    private VBox statsContent;
    private Label emptyStateLabel;

    public StatsPane(KnightManager knightManager, EquipmentRepository equipRepo) {
        this.knightManager = knightManager;
        this.equipRepo = equipRepo;
        this.equipList = FXCollections.observableArrayList();

        root = new VBox(20);
        root.setPadding(new Insets(0));

        // Empty state
        emptyStateLabel = new Label("No active knight selected.\nGo to Knights tab and set one as active.");
        emptyStateLabel.getStyleClass().add("empty-state");
        emptyStateLabel.setWrapText(true);
        emptyStateLabel.setAlignment(Pos.CENTER);

        // Stats content
        statsContent = new VBox(20);
        statsContent.getChildren().addAll(createInfoCard(), createStatCardsRow(), createWeightCard(), createEquipmentCard(), createCostCard());

        root.getChildren().add(emptyStateLabel);
    }

    /**
     * Картка з основною інформацією про лицаря.
     */
    private HBox createInfoCard() {
        HBox card = new HBox(40);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);

        // Ліва частина — ім'я та деталі
        VBox infoBox = new VBox(6);

        nameLabel = new Label("—");
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        HBox detailsRow = new HBox(16);
        ordenLabel = new Label("—");
        ordenLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
        rankLabel = new Label("—");
        rankLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 13px; -fx-font-weight: bold;");
        idLabel = new Label("—");
        idLabel.setStyle("-fx-text-fill: #9E9E9E; -fx-font-size: 12px;");
        detailsRow.getChildren().addAll(ordenLabel, new Label("•"), rankLabel, new Label("•"), idLabel);

        infoBox.getChildren().addAll(nameLabel, detailsRow);
        card.getChildren().add(infoBox);
        return card;
    }

    /**
     * Рядок з міні-картками статистики.
     */
    private HBox createStatCardsRow() {
        HBox row = new HBox(16);

        // Weight card
        VBox wCard = createStatCard();
        weightValueLabel = new Label("0.00");
        weightValueLabel.getStyleClass().add("stat-card-value");
        Label wLabel = new Label("CURRENT WEIGHT (kg)");
        wLabel.getStyleClass().add("stat-card-label");
        wCard.getChildren().addAll(weightValueLabel, wLabel);

        // Defense card
        VBox dCard = createStatCard();
        defenseValueLabel = new Label("0");
        defenseValueLabel.getStyleClass().add("stat-card-value");
        Label dLabel = new Label("TOTAL DEFENSE");
        dLabel.getStyleClass().add("stat-card-label");
        dCard.getChildren().addAll(defenseValueLabel, dLabel);

        // Items card
        VBox iCard = createStatCard();
        itemsValueLabel = new Label("0");
        itemsValueLabel.getStyleClass().add("stat-card-value");
        Label iLabel = new Label("EQUIPPED ITEMS");
        iLabel.getStyleClass().add("stat-card-label");
        iCard.getChildren().addAll(itemsValueLabel, iLabel);

        // Cost card
        VBox cCard = createStatCard();
        costValueLabel = new Label("$0.0");
        costValueLabel.getStyleClass().add("stat-card-value");
        Label cLabel = new Label("TOTAL COST");
        cLabel.getStyleClass().add("stat-card-label");
        cCard.getChildren().addAll(costValueLabel, cLabel);

        HBox.setHgrow(wCard, Priority.ALWAYS);
        HBox.setHgrow(dCard, Priority.ALWAYS);
        HBox.setHgrow(iCard, Priority.ALWAYS);
        HBox.setHgrow(cCard, Priority.ALWAYS);

        row.getChildren().addAll(wCard, dCard, iCard, cCard);
        return row;
    }

    private VBox createStatCard() {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    /**
     * Картка з прогрес-баром ваги.
     */
    private VBox createWeightCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label title = new Label("Weight Capacity");
        title.getStyleClass().add("card-title");

        weightBar = new ProgressBar(0);
        weightBar.setMaxWidth(Double.MAX_VALUE);
        weightBar.setPrefHeight(14);

        weightBarLabel = new Label("0.00 / 0.00 kg");
        weightBarLabel.getStyleClass().add("label-muted");

        card.getChildren().addAll(title, weightBar, weightBarLabel);
        return card;
    }

    /**
     * Картка з таблицею екіпірування.
     */
    @SuppressWarnings("unchecked")
    private VBox createEquipmentCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");

        Label title = new Label("Equipped Items");
        title.getStyleClass().add("card-title");

        equipTable = new TableView<>();
        equipTable.setPlaceholder(new Label("No equipment. Go to Equipment tab to equip items."));
        equipTable.setPrefHeight(200);

        TableColumn<Ammunition, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getClass().getSimpleName()));
        colType.setPrefWidth(110);

        TableColumn<Ammunition, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colName.setPrefWidth(200);

        TableColumn<Ammunition, String> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(cd -> new SimpleStringProperty(String.format("%.2f kg", cd.getValue().getWeight())));
        colWeight.setPrefWidth(90);

        TableColumn<Ammunition, String> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(cd -> new SimpleStringProperty(String.format("$%.1f", cd.getValue().getPrice())));
        colPrice.setPrefWidth(90);

        TableColumn<Ammunition, String> colStat = new TableColumn<>("Stats");
        colStat.setCellValueFactory(cd -> {
            Ammunition a = cd.getValue();
            if (a instanceof Weapon) {
                return new SimpleStringProperty("Damage: +" + ((Weapon) a).getDamage());
            } else if (a instanceof Armor) {
                return new SimpleStringProperty("Defense: +" + ((Armor) a).getDefense());
            }
            return new SimpleStringProperty("—");
        });
        colStat.setPrefWidth(110);

        equipTable.getColumns().addAll(colType, colName, colWeight, colPrice, colStat);
        equipTable.setItems(equipList);

        card.getChildren().addAll(title, equipTable);
        return card;
    }

    /**
     * Картка з вартістю (каталог + інвентар).
     */
    private VBox createCostCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label title = new Label("Cost Summary");
        title.getStyleClass().add("card-title");

        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER_LEFT);

        Label equipCostTitle = new Label("Equipment cost:");
        equipCostTitle.setStyle("-fx-text-fill: #757575;");
        Label equipCostValue = new Label("$0.0");
        equipCostValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");
        equipCostValue.setId("equipCostValue");

        Label catalogCostTitle = new Label("Full catalog cost:");
        catalogCostTitle.setStyle("-fx-text-fill: #757575;");
        double catalogTotal = equipRepo.getAll().stream().mapToDouble(Ammunition::getPrice).sum();
        Label catalogCostValue = new Label(String.format("$%.1f", catalogTotal));
        catalogCostValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        row.getChildren().addAll(equipCostTitle, equipCostValue, catalogCostTitle, catalogCostValue);
        card.getChildren().addAll(title, row);
        return card;
    }

    /**
     * Оновлює всі дані на панелі відповідно до активного лицаря.
     */
    public void refresh() {
        Knight k = knightManager.getActiveKnight();

        if (k == null) {
            root.getChildren().clear();
            root.getChildren().add(emptyStateLabel);
            return;
        }

        root.getChildren().clear();
        root.getChildren().add(statsContent);

        // Info card
        nameLabel.setText("⚔ " + k.getName());
        ordenLabel.setText("Order: " + k.getOrden());
        rankLabel.setText(k.getRank().toString());
        idLabel.setText("ID: " + k.getId());

        // Stat cards
        weightValueLabel.setText(String.format("%.2f", k.getCurrentWeight()));
        defenseValueLabel.setText(String.valueOf(k.getTotalDefense()));
        itemsValueLabel.setText(String.valueOf(k.getEquipment().size()));

        double totalCost = k.getEquipment().stream().mapToDouble(Ammunition::getPrice).sum();
        costValueLabel.setText(String.format("$%.1f", totalCost));

        // Weight bar
        double max = k.getMaxWeightCapacity();
        double current = k.getCurrentWeight();
        double progress = max > 0 ? current / max : 0;
        weightBar.setProgress(Math.min(progress, 1.0));
        weightBarLabel.setText(String.format("%.2f / %.2f kg (%.0f%%)", current, max, progress * 100));

        // Колір прогрес-бару
        if (progress > 0.9) {
            weightBar.setStyle("-fx-accent: #EF5350;"); // Червоний
        } else if (progress > 0.7) {
            weightBar.setStyle("-fx-accent: #FFA726;"); // Помаранчевий
        } else {
            weightBar.setStyle("-fx-accent: #757575;"); // Сірий (норма)
        }

        // Equipment table
        equipList.clear();
        equipList.addAll(k.getEquipment());

        // Cost card — update equipment cost
        Label equipCostLabel = (Label) root.lookup("#equipCostValue");
        if (equipCostLabel != null) {
            equipCostLabel.setText(String.format("$%.1f", totalCost));
        }

        LoggerService.info("Viewed knight status via GUI: " + k.getName());
    }

    public VBox getRoot() {
        return root;
    }
}
