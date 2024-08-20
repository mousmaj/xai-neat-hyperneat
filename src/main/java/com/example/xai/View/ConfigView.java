package com.example.xai.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfigView {
    private VBox root;
    private Slider populationSizeSlider;
    private Slider eliminatePercentageSlider;
    private Slider addNodeChanceSlider;
    private Slider addConnectionChanceSlider;
    private Slider perturbationWeightsSlider;
    private Slider mutationRateSlider;
    private Slider worthyProtectingSlider;
    private Slider compatibilityThresholdSlider;
    private Button neatButton;
    private Button hyperNeatButton;
    private Button saveButton;

    public ConfigView() {
        root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setMinWidth(500);

        Label titleLabel = new Label("Parameterkonfiguration");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #333333;");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        populationSizeSlider = createIntegerSlider("🛈 Populationsgröße:", gridPane, 0);
        populationSizeSlider.setMax(50); 
        Tooltip populationTooltip = new Tooltip("(EN: Populationsize) Bestimmt die Anzahl der Individuen in jeder Generation. Eine größere Population kann die genetische Vielfalt erhöhen, benötigt aber mehr Rechenzeit.");
        populationTooltip.setStyle("-fx-font-size: 14px;");
        populationTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Populationsgröße:", populationTooltip), 0, 0);

        eliminatePercentageSlider = createPercentageSlider("🛈 Eliminierung (in %):", gridPane, 2, " %");
        Tooltip eliminateTooltip = new Tooltip("(EN: Selection) Gibt den Prozentsatz der schwächsten Individuen an, die in jeder Generation eliminiert werden. Die übrigen Individuuen werden für die nächste Generation selektiert.");
        eliminateTooltip.setStyle("-fx-font-size: 14px;");
        eliminateTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Eliminierung (in %):", eliminateTooltip), 0, 2);

        worthyProtectingSlider = createPercentageSlider("🛈 Schutzwürdigkeit (in %):", gridPane, 3, " %");
        Tooltip protectingTooltip = new Tooltip("(EN: Protected individuals) Definiert den Prozentsatz der besten Individuen, die vor der Eliminierung und Veränderung geschützt sind. Diese Individuen werden unverändert zur nächsten Generation weitergeführt.");
        protectingTooltip.setStyle("-fx-font-size: 14px;");
        populationTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Schutzwürdigkeit (in %):", protectingTooltip), 0, 3);

        addNodeChanceSlider = createPercentageSlider("🛈 Knotenhinzufügungechance:", gridPane, 4, " %");
        Tooltip nodeTooltip = new Tooltip("(EN: Probability of adding new node) Wahrscheinlichkeit, dass ein neues Neuron (Knoten) dem Genotyp eines Individuums hinzugefügt wird. Höhere Werte können die Komplexität des Netzes erhöhen.");
        nodeTooltip.setStyle("-fx-font-size: 14px;");
        nodeTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Knotenhinzufügungechance:", nodeTooltip), 0, 4);

        addConnectionChanceSlider = createPercentageSlider("🛈 Verbindungshinzufügungechance:", gridPane, 5, "%");
        Tooltip connectionTooltip = new Tooltip("(EN: Probability of adding new connection) Wahrscheinlichkeit, dass eine neue Verbindung zwischen zwei bestehenden Neuronen (Knoten) hinzugefügt wird. Erhöht die Vernetzung innerhalb des Netzwerks.");
        connectionTooltip.setStyle("-fx-font-size: 14px;");
        connectionTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Verbindungshinzufügungechance:", connectionTooltip), 0, 5);

        perturbationWeightsSlider = createPercentageSlider("🛈 Gewichtsstörung (+- Reichweite):", gridPane, 6, " %");
        Tooltip perturbationTooltip = new Tooltip("(EN: Weight perturbation) Reichweite der zufälligen Änderungen an den Gewichten der Verbindungen. \nGrößere Störungen können zu drastischen Änderungen in der Netzwerkleistung führen. \n Beispiel: 10% bedeutet, dass die Gewichte um maximal 10% erhöht oder verringert werden können.");
        perturbationTooltip.setStyle("-fx-font-size: 14px;");
        perturbationTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Gewichtsstörung (+- Reichweite):", perturbationTooltip), 0, 6);

        mutationRateSlider = createPercentageSlider("🛈 Gewichtsmutationschance (in %):", gridPane, 7, " %");
        Tooltip mutationTooltip = new Tooltip("(EN: Weight mutation chance) Wahrscheinlichkeit, dass die vorher definierte Gewichtsstörung auftritt.");
        mutationTooltip.setStyle("-fx-font-size: 14px;");
        mutationTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Gewichtsmutationschance (in %):", mutationTooltip), 0, 7);

        compatibilityThresholdSlider = createDecimalSlider("🛈 Spezies-Kompatibilitätsschwelle:", gridPane, 8, "");
        compatibilityThresholdSlider.setMax(1.0); // Wert zwischen 0.0 und 1.0
        Tooltip compatibilityTooltip = new Tooltip("(EN: Compability threshold) Legt fest, wie unterschiedlich zwei Genotypen sein dürfen, um noch als zur gleichen Spezies gehörend zu gelten. Eine niedrigere Schwelle fördert mehr Spezies in der Population.");
        compatibilityTooltip.setStyle("-fx-font-size: 14px;");
        compatibilityTooltip.setShowDelay(javafx.util.Duration.millis(10));
        gridPane.add(createLabeledTooltipLabel("🛈 Spezies-Kompatibilitätsschwelle:", compatibilityTooltip), 0, 8);

        // Add listener to make eliminatePercentageSlider dependent on worthyProtectingSlider
        worthyProtectingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double maxEliminate = 100 - newVal.intValue();
            if (eliminatePercentageSlider.getValue() > maxEliminate) {
                eliminatePercentageSlider.setValue(maxEliminate);
            }
            eliminatePercentageSlider.setMax(maxEliminate);
        });

        // Create NEAT and HyperNEAT buttons
        neatButton = createModeButton("NEAT");
        hyperNeatButton = createModeButton("HyperNEAT");

        // Add toggle logic between NEAT and HyperNEAT buttons
        neatButton.setOnAction(e -> setActiveMode(neatButton, hyperNeatButton));
        hyperNeatButton.setOnAction(e -> setActiveMode(hyperNeatButton, neatButton));

        // Initially set NEAT as the active mode
        setActiveMode(neatButton, hyperNeatButton);

        Label modeLabel = new Label("🛈 Algorithmenauswahl:");
        modeLabel.setStyle("-fx-font-size: 15px;");
        Tooltip modeTooltip = new Tooltip("(EN: Mode) Wählen Sie den Modus, in dem die Simulation ausgeführt werden soll. NEAT ist der Standardmodus, während HyperNEAT eine erweiterte Version ist, die die Entwicklung von Netzwerken für die Bildverarbeitung ermöglicht.");
        modeTooltip.setStyle("-fx-font-size: 14px;");
        modeTooltip.setShowDelay(javafx.util.Duration.millis(10));
        modeLabel.setTooltip(modeTooltip);

        HBox modeButtonBox = new HBox(modeLabel, neatButton, hyperNeatButton);
        modeButtonBox.setSpacing(10);
        modeButtonBox.setAlignment(Pos.CENTER);
        modeButtonBox.setMaxWidth(Double.MAX_VALUE); // Verhindert Versetzung
        GridPane.setColumnSpan(modeButtonBox, 2); // Sicherstellen, dass die HBox zwei Spalten überspannt
        gridPane.add(modeButtonBox, 0, 9, 2, 1);
        
        // Speichern-Button zentrieren
        saveButton = new Button("Speichern");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        saveButton.setMinWidth(120);
        saveButton.setMinHeight(80);
        HBox buttonBox = new HBox(saveButton);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(50, 50, 0, 50));
        buttonBox.setMaxWidth(Double.MAX_VALUE); // Verhindert Versetzung

        root.getChildren().addAll(titleLabel, gridPane, buttonBox);
    }

    private Label createLabeledTooltipLabel(String text, Tooltip tooltip) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px;");
        label.setTooltip(tooltip);
        return label;
    }

    private Slider createIntegerSlider(String labelText, GridPane gridPane, int row) {
        Slider slider = new Slider(0, 100, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        Label valueLabel = new Label(Integer.toString((int) slider.getValue()));
        valueLabel.setMinWidth(30);
        valueLabel.setStyle("-fx-font-size: 15px;");
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int roundedValue = newVal.intValue();
            valueLabel.setText(Integer.toString(roundedValue));
            slider.setValue(roundedValue);  // Update the slider value to the rounded value
        });
        
        gridPane.add(slider, 1, row);
        gridPane.add(valueLabel, 2, row);
        
        return slider;
    }

    private Slider createPercentageSlider(String labelText, GridPane gridPane, int row, String postfix) {
        Slider slider = new Slider(0, 100, 0); // Ganzzahliger Slider von 0 bis 100
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        Label valueLabel = new Label(Integer.toString((int) slider.getValue()) + postfix);
        valueLabel.setMinWidth(30);
        valueLabel.setStyle("-fx-font-size: 15px;");
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int intValue = newVal.intValue(); // Ganze Zahl
            valueLabel.setText(intValue + postfix); // Ganzzahligen Wert anzeigen
            slider.setValue(intValue);  // Slider-Wert aktualisieren, um sicherzustellen, dass es eine Ganzzahl ist
        });
        
        gridPane.add(slider, 1, row);
        gridPane.add(valueLabel, 2, row);
        
        return slider;
    }

    private Slider createDecimalSlider(String labelText, GridPane gridPane, int row, String postfix) {
        Slider slider = new Slider(0, 1.0, 0); // Slider von 0.0 bis 1.0
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        Label valueLabel = new Label(String.format("%.2f", slider.getValue()) + postfix);
        valueLabel.setMinWidth(30);
        valueLabel.setStyle("-fx-font-size: 15px;");
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double roundedValue = Math.round(newVal.doubleValue() * 100.0) / 100.0;
            valueLabel.setText(String.format("%.2f", roundedValue) + postfix);
            slider.setValue(roundedValue);  // Update the slider value to the rounded value
        });
        
        gridPane.add(slider, 1, row);
        gridPane.add(valueLabel, 2, row);
        
        return slider;
    }

    private Button createModeButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #DDDDDD; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"
        );
        button.setMinWidth(120);
        return button;
    }

    private void setActiveMode(Button activeButton, Button inactiveButton) {
        activeButton.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"
        );
        inactiveButton.setStyle(
            "-fx-background-color: #DDDDDD; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;"
        );
    }

    public VBox getRoot() {
        return root;
    }

    public Slider getPopulationSizeSlider() {
        return populationSizeSlider;
    }

    public Slider getEliminatePercentageSlider() {
        return eliminatePercentageSlider;
    }

    public Slider getAddNodeChanceSlider() {
        return addNodeChanceSlider;
    }

    public Slider getAddConnectionChanceSlider() {
        return addConnectionChanceSlider;
    }

    public Slider getPerturbationWeightsSlider() {
        return perturbationWeightsSlider;
    }

    public Slider getMutationRateSlider() {
        return mutationRateSlider;
    }

    public Slider getWorthyProtectingSlider() {
        return worthyProtectingSlider;
    }

    public Slider getCompatibilityThresholdSlider() {
        return compatibilityThresholdSlider;
    }

    public Button getNeatButton() {
        return neatButton;
    }

    public Button getHyperNeatButton() {
        return hyperNeatButton;
    }

    public boolean isHyperNeatButtonActive() {
        return hyperNeatButton.getStyle().contains("background-color: #4CAF50;");
    }

    public Button getSaveButton() {
        return saveButton;
    }
}
