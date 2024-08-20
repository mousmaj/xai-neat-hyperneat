package com.example.xai.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Construction.HyperNEAT;
import com.example.xai.Model.Construction.NEAT;
import com.example.xai.Model.NEAT.Population;
import com.example.xai.Model.NEAT.Species;
import com.example.xai.View.InteractiveMainView;
import com.example.xai.Model.Phenotype.NeuralNetwork;
import com.example.xai.Model.Phenotype.NeuralNetwork.TYPE;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;



public class InteractiveMainController implements NEAT.TrainingUpdateListener, HyperNEAT.TrainingUpdateListener {

    private final InteractiveMainView view;
    private final Object neat;
    private Population population;
    private String previouslySelectedGenomeId;

    public InteractiveMainController(InteractiveMainView view, Object neat) {
        this.view = view;
        this.neat = neat;
        if (neat instanceof NEAT) {
            this.population = ((NEAT) neat).getPopulation();
            ((NEAT) neat).addTrainingUpdateListener(this);
        } else if (neat instanceof HyperNEAT) {
            this.population = ((HyperNEAT) neat).getPopulation();
            ((HyperNEAT) neat).addTrainingUpdateListener(this);
        }
        initialize();
        new ButtonController(neat, view);
    }

    private void initialize() {
        populateTable();
        setupActionColumn();
        updateGenerationLabel();
        setupRowSelectionListener();
    }

    private void setupActionColumn() {
        @SuppressWarnings("unchecked")
        // get last column of the table
        TableColumn<GenomeWrapper, String> actionColumn = (TableColumn<GenomeWrapper, String>) view.getGenomeTable().getColumns().get(view.getGenomeTable().getColumns().size() - 1);

        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<GenomeWrapper, String> call(TableColumn<GenomeWrapper, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText("▶");
                            setStyle("-fx-text-fill: blue; -fx-font-size: 20px;");
                            setAlignment(Pos.CENTER);
                            setOnMouseClicked(event -> {
                                GenomeWrapper genomeWrapper = getTableView().getItems().get(getIndex());
                                showGraph(genomeWrapper.getGenome());
                                displayLog(genomeWrapper);  // Log-Einträge anzeigen
                            });
                        }
                    }
                };
            }
        });
    }

    private void populateTable() {
        ObservableList<GenomeWrapper> genomeWrappers = FXCollections.observableArrayList();
    
        List<Species> speciesList = population.getSpecies();
    
        int totalImages = neat instanceof NEAT ? ((NEAT) neat).getImageAnswerCollection().size() : 
                         neat instanceof HyperNEAT ? ((HyperNEAT) neat).getImageAnswerCollection().size() : 0;
    
        List<String> pastelColors = List.of(
        "#D4E4EB", "#FFE0B3", "#E6CCE2", "#FFB3B0", "#C7EAC7", 
        "#FEF9B5", "#FFDDE1", 
        "#E0BBE4", "#957DAD", "#D291BC", "#FEC8D8", "#FFDFD3" 
        );
        
        int colorIndex = 0;
    
        Properties properties = loadProperties();
        double worthyProtecting = Double.parseDouble(properties.getProperty("neat.WorthyProtecting", "0.1"));
        double eliminatePercentage = Double.parseDouble(properties.getProperty("neat.EliminatePercentage", "0.3"));
    
        for (Species species : speciesList) {
            List<Genome> genomeList = species.getSortedSpeciesMemberList().stream().collect(Collectors.toList());
            String speciesColor = pastelColors.get(colorIndex % pastelColors.size());
    
            int numWorthyProtecting = (int) Math.ceil(worthyProtecting * genomeList.size());
            int numEliminate = (int) (eliminatePercentage * genomeList.size());
    
            for (int j = 0; j < genomeList.size(); j++) {
                GenomeWrapper wrapper = new GenomeWrapper(genomeList.get(j), species.getSpeciesId(), j + 1, totalImages);
                wrapper.setSpeciesColor(speciesColor); // Farbe zuweisen
                
                // Setzen des Symbols basierend auf dem Index innerhalb der Spezies
                if (j < numWorthyProtecting) {
                    wrapper.setStatusSymbol("🚫");
                } else if (j >= genomeList.size() - numEliminate) {
                    wrapper.setStatusSymbol("💀");
                }
    
                genomeWrappers.add(wrapper);
            }
            colorIndex++;
        }
    
        Platform.runLater(() -> view.getGenomeTable().setItems(genomeWrappers));
    
        view.getGenomeTable().setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(GenomeWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: " + item.getSpeciesColor() + ";");
                }
            }
        });
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        String targetFilePath = Paths.get(System.getProperty("user.dir"), "target", "config.properties").toString();

        try (InputStream input = Files.exists(Paths.get(targetFilePath)) ?
                new FileInputStream(targetFilePath) :
                getClass().getResourceAsStream("/com/example/xai/ConfigFile/config.properties")) {

            if (input == null) {
                throw new FileNotFoundException("config.properties file not found in the specified paths.");
            }

            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }


    private void setupRowSelectionListener() {
        view.getGenomeTable().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayLog(newValue);
            }
        });
    }

    private void displayLog(GenomeWrapper genomeWrapper) {
        view.setLogLabelText("Genome: " + genomeWrapper.getId());  // Überschrift setzen
        view.getLogArea().clear();
        genomeWrapper.getGenome().getLogger().getLogs().forEach(log -> view.getLogArea().appendText(log + "\n"));
    }

    private void updateGenerationLabel() {
        int currentGeneration = neat.getClass().equals(NEAT.class) ? ((NEAT) neat).getCurrentGenerationIndex() : ((HyperNEAT) neat).getCurrentGenerationIndex();
        view.setGenerationLabel("Generation: " + currentGeneration);
        view.getNextButton().setDisable(false);
    }

 private void showGraph(Genome genome) {
    // Erstelle und zeige das erste neuronale Netz
    NeuralNetwork neuralNetwork;
    Label graphPaneLabel;
    
    Label phenoGraphPaneLabel = new Label("🛈 Phänotyp (Eingangsneuronen = 784 Neuronen)");
    phenoGraphPaneLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    phenoGraphPaneLabel.setVisible(false);

    StackPane phenoGraphPane = view.getPhenoGraphPane();
    phenoGraphPane.getChildren().clear();
    phenoGraphPane.setVisible(false); // Standardmäßig versteckt

    Tooltip phenoTooltip = new Tooltip("Der Phänotyp ist die sichtbare Struktur eines neuronalen Netzes, die aus dem Genotyp abgeleitet wird. \n" +
            "Er bestimmt, wie das neuronale Netz auf Eingaben reagiert und Entscheidungen trifft. \n" +
            "Der Phänotyp repräsentiert also das tatsächliche Verhalten des Netzwerks im Laufe der Simulation.");

    // Dynamisch das zweite neuronale Netz nur für HyperNEAT anzeigen
    if (neat instanceof HyperNEAT) {
        neuralNetwork = new NeuralNetwork(genome, TYPE.FULL);
        graphPaneLabel = new Label("🛈 CPPN");
        graphPaneLabel.setTooltip(new Tooltip("CPPN steht für Compositional Pattern Producing Network. \n" +
                "Es handelt sich um ein neuronales Netz, das die Struktur eines anderen neuronalen Netzes generiert. \n" +
                "Das CPPN wird verwendet, um die Verbindungsgewichte des Phänotyps zu bestimmen."));
        

        Genome phenoGenome = genome.getPhenoGenome();
        NeuralNetwork phenoNeuralNetwork = new NeuralNetwork(phenoGenome, TYPE.PARTLY);
        FxViewer phenoViewer = new FxViewer(phenoNeuralNetwork.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        phenoViewer.enableAutoLayout();
        FxViewPanel phenoViewPanel = (FxViewPanel) phenoViewer.addDefaultView(false);

        phenoGraphPaneLabel.setTooltip(phenoTooltip);
        phenoGraphPaneLabel.setVisible(true);
        phenoGraphPane.setVisible(true);

        VBox phenoGraphBox = new VBox(phenoGraphPaneLabel, phenoViewPanel);
        phenoGraphBox.setAlignment(Pos.CENTER);
        phenoGraphPane.getChildren().add(phenoGraphBox);

        // Sicherstellen, dass beide StackPanes sichtbar sind
        phenoGraphPane.setVisible(true);
    } else {
        // Wenn NEAT verwendet wird, wird das zweite Pane ausgeblendet
        graphPaneLabel = new Label("🛈 Phänotyp (Eingangsneuronen = 784 Neuronen)");
        phenoTooltip = new Tooltip("(EN: Phenotype) Der Phänotyp ist die sichtbare Struktur eines neuronalen Netzes, die aus dem Genotyp abgeleitet wird. \nEr bestimmt, wie das neuronale Netz auf Eingaben reagiert und Entscheidungen trifft. \nDer Phänotyp repräsentiert also das tatsächliche Verhalten des Netzwerks im Laufe der Simulation.");
        phenoTooltip.setStyle("-fx-font-size: 14px;");
        graphPaneLabel.setTooltip(phenoTooltip);
        neuralNetwork = new NeuralNetwork(genome, TYPE.PARTLY);
        view.getPhenoGraphPane().setVisible(false);
        view.getGraphPane().setMinHeight(600); // Vergrößern des ersten Panes bei NEAT
    }

    FxViewer viewer = new FxViewer(neuralNetwork.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
    viewer.enableAutoLayout();
    FxViewPanel viewPanel = (FxViewPanel) viewer.addDefaultView(false);

    // Setze das erste neuronale Netz und seine Überschrift
    StackPane graphPane = view.getGraphPane();
    graphPane.getChildren().clear();

    graphPaneLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");

    VBox graphBox = new VBox(graphPaneLabel, viewPanel);
    graphBox.setAlignment(Pos.CENTER);
    graphPane.getChildren().add(graphBox);
}

    

    @Override
    public void onTrainingUpdate(Population population) {
        Platform.runLater(() -> {
            this.population = population;
    
            // Speichern des aktuell ausgewählten Genoms
            GenomeWrapper previouslySelectedGenome = view.getGenomeTable().getSelectionModel().getSelectedItem();
            this.previouslySelectedGenomeId = previouslySelectedGenome != null ? previouslySelectedGenome.getId() : this.previouslySelectedGenomeId;
    
            // Tabelle aktualisieren
            populateTable();
            updateGenerationLabel();
    
            // Nach dem Aktualisieren das Genom wieder auswählen, falls eins ausgewählt war
            if (previouslySelectedGenomeId != null) {
                for (GenomeWrapper genomeWrapper : view.getGenomeTable().getItems()) {
                    if (genomeWrapper.getId().equals(previouslySelectedGenomeId)) {
                        view.getGenomeTable().getSelectionModel().select(genomeWrapper);
                        showGraph(genomeWrapper.getGenome());
                        displayLog(genomeWrapper);
                        break;
                    }
                }
            }
        });
    }
}
