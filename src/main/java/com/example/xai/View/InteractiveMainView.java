package com.example.xai.View;

import com.example.xai.Controller.GenomeWrapper;
import com.example.xai.Frontend.MainApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class InteractiveMainView {

    private BorderPane root;
    private Label generationLabel;
    private TableView<GenomeWrapper> genomeTable;
    private StackPane graphPane;
    private StackPane phenoGraphPane;
    private Label graphPaneLabel; 
    private Label phenoGraphPaneLabel; 
    private VBox graphBox; 
    private VBox phenoGraphBox; 
    private Label logLabel;
    private TextArea logArea;
    private Button nextButton;
    private Button backButton;
    private MainApp mainApp;

    @SuppressWarnings("unchecked")
    public InteractiveMainView(MainApp mainApp) {
        root = new BorderPane();

        // Neues Label für die Generation
        generationLabel = new Label("Generation: 1");
        generationLabel.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #030BFC;");
        generationLabel.setPadding(new Insets(10, 10, 10, 10));
        generationLabel.setAlignment(Pos.CENTER);

        // Überschrift für die Tabelle
        Label tableHeading = new Label("Individuen-Tabelle");
        tableHeading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        tableHeading.setPadding(new Insets(10, 10, 0, 10));

        genomeTable = new TableView<>();
        genomeTable = new TableView<>();
        genomeTable.getColumns().addAll(
            createUnnamedColumn(),
            createTableColumnWithTooltip("Spezies\nID", "spezie", 60, "Eindeutige Identifikationsnummer der Spezies."),
            createTableColumnWithTooltip("Rang \ninnerhalb \nSpezies", "rang", 60, "Position des Genoms innerhalb seiner Spezies."),
            createTableColumnWithTooltip("Genom\nID", "id", 60, "Eindeutige Identifikationsnummer des Genoms."),
            createTableColumnWithTooltip("Anzahl \nEingangsneuronen", "inputCount", 110, "Die Anzahl der Eingangsneuronen im neuronalen Netz."),
            createTableColumnWithTooltip("Anzahl \nversteckter \nNeuronen", "hiddenCount", 110, "Die Anzahl der versteckten Neuronen im neuronalen Netz."),
            createTableColumnWithTooltip("Anzahl \nAusgangsneuronen", "outputCount", 110, "Die Anzahl der Ausgangsneuronen im neuronalen Netz."),
            createTableColumnWithTooltip("Anzahl \nVerbindungen", "connectionCount", 110, "Die Anzahl der Verbindungen zwischen den Neuronen."),
            createTableColumnWithTooltip("Vorhersage-\ngenauigkeit \n(%)", "fitnessPercent", 110, "Die Vorhersagegenauigkeit dieses Genoms in Prozent."),
            createTableColumnWithTooltip("Anzeigen", "action", 45, "Klicken Sie hier, um das neuronale Netz anzuzeigen.")
        );

        genomeTable.setMinWidth(1100);
        genomeTable.setMinHeight(350);
        genomeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ScrollPane scrollPane = new ScrollPane(genomeTable);
        scrollPane.setMinWidth(1100);
        scrollPane.setMinHeight(350);
        scrollPane.setFitToWidth(true);

        VBox tableBox = new VBox();
        tableBox.getChildren().addAll(tableHeading, scrollPane);

        graphPane = new StackPane();
        graphPane.setPrefSize(600, 350); 
        phenoGraphPane = new StackPane();  
        phenoGraphPane.setPrefSize(600, 350); 
        phenoGraphPane.setVisible(false);  // Standardmäßig versteckt, da es nur bei HyperNEAT genutzt wird

        // Überschrift für das erste neuronale Netz
        graphPaneLabel = new Label("");
        graphPaneLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        graphPaneLabel.setPadding(new Insets(5, 10, 5, 10));

        graphBox = new VBox(graphPaneLabel, graphPane);
        graphBox.setAlignment(Pos.CENTER);

        // Überschrift für das zweite neuronale Netz
        phenoGraphPaneLabel = new Label("Neural Network 2");
        phenoGraphPaneLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        phenoGraphPaneLabel.setPadding(new Insets(5, 10, 5, 10));
        phenoGraphPaneLabel.setVisible(false);  // Standardmäßig versteckt

        phenoGraphBox = new VBox(phenoGraphPaneLabel, phenoGraphPane);
        phenoGraphBox.setAlignment(Pos.CENTER);

        // Neues Label für die Log-Überschrift
        logLabel = new Label("Genome: ");
        logLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        logLabel.setPadding(new Insets(5, 10, 5, 10));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(270);

        VBox logBox = new VBox();
        logBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 5px;");
        logBox.setMinHeight(320);
        logBox.getChildren().addAll(logLabel, logArea);
        logBox.setPadding(new Insets(10, 10, 10, 10));

        VBox mainContent = new VBox();
        mainContent.getChildren().addAll(tableBox, logBox);

        root.setTop(generationLabel);
        root.setLeft(mainContent);

        GridPane centerPane = new GridPane();  // GridPane for the two neural networks
        centerPane.add(graphBox, 0, 0);       
        centerPane.add(phenoGraphBox, 0, 1);  

        root.setCenter(centerPane);

        nextButton = new Button("Nächste \nGeneration");
        nextButton.setMinWidth(100);
        nextButton.setMinHeight(60);
        nextButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
    
        backButton = new Button("Zurück");
        backButton.setMinWidth(100);
        backButton.setMinHeight(60);
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");

        HBox buttonBox = new HBox(10, nextButton, backButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        root.setBottom(buttonBox);

        BorderPane.setAlignment(generationLabel, Pos.TOP_LEFT);
        BorderPane.setAlignment(mainContent, Pos.CENTER_LEFT);
        BorderPane.setAlignment(centerPane, Pos.CENTER);
    }

    private TableColumn<GenomeWrapper, String> createUnnamedColumn() {
        TableColumn<GenomeWrapper, String> column = new TableColumn<>("");
        column.setCellValueFactory(new PropertyValueFactory<>("statusSymbol"));
        column.setPrefWidth(50);
        
    
        column.setCellFactory(col -> new TableCell<GenomeWrapper, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 20px;");
                    setAlignment(Pos.CENTER);
                }
            }
        });
    
        return column;
    }

    private TableColumn<GenomeWrapper, ?> createTableColumnWithTooltip(String title, String property, int width, String tooltipText) {

        TableColumn<GenomeWrapper, Object> column = new TableColumn<>();
        Label columnLabel = new Label(title);
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("-fx-font-size: 14px;"); // Schriftgröße einstellen
        tooltip.setShowDelay(Duration.millis(100));
        columnLabel.setTooltip(tooltip);
        column.setGraphic(columnLabel);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(width);

        return column;
    }

    public TableView<GenomeWrapper> getGenomeTable() {
        return genomeTable;
    }

    public BorderPane getRoot() {
        return root;
    }

    public StackPane getGraphPane() {
        return graphPane;
    }

    public StackPane getPhenoGraphPane() {
        return phenoGraphPane;
    }

    public Label getGraphPaneLabel() {
        return graphPaneLabel;
    }

    public Label getPhenoGraphPaneLabel() {
        return phenoGraphPaneLabel;
    }

    public VBox getGraphBox() {
        return graphBox;
    }

    public VBox getPhenoGraphBox() {
        return phenoGraphBox;
    }

    public void setGenerationLabel(String text) {
        generationLabel.setText(text);
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public TextArea getLogArea() {
        return logArea;
    }

    public void setLogLabelText(String text) {
        logLabel.setText(text);
    }

    public MainApp getMainApp() {
        return mainApp;
    }
}
