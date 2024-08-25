package com.example.xai.Frontend;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.xai.Controller.ConfigController;
import com.example.xai.Controller.InteractiveMainController;
import com.example.xai.Controller.PDFViewerController;
import com.example.xai.Model.Construction.NEAT;
import com.example.xai.Model.Construction.HyperNEAT;
import com.example.xai.View.ConfigView;
import com.example.xai.View.InteractiveMainView;
import com.example.xai.View.PDFViewerView;
import com.example.xai.View.WelcomeView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class MainApp extends Application {

    public enum ExplanationType {
        EINFÜHRUNG,
        SELEKTION,
        MUTATION,
        REPRODUKTION,
        SPEZIATION,
        NEAT,
        HYPERNEAT,
        PARAMETERÜBERSICHT,
        EVALUATION
    }

    private Stage primaryStage;
    private Object neat;
    Image icon = new Image(getClass().getResourceAsStream("/com/example/xai/images/neural-network.png"));
    private ExecutorService executorService;
    private List<PDFViewerController> pdfControllers = new ArrayList<>();
    private Tab simulationTab;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("XAI - NEAT & HyperNEAT");
        primaryStage.setMinHeight(900);
        primaryStage.setMinWidth(1500);

        executorService = Executors.newCachedThreadPool();

        primaryStage.setOnCloseRequest(event -> {
            shutdownThreads();
            closeAllPDFDocuments();
            Platform.exit();
            System.exit(0);
        });

        showWelcomeView();
    }

    private void showWelcomeView() {
        TabPane tabPane = new TabPane();
        tabPane.setTabMinHeight(40);  
        tabPane.setTabMaxHeight(60);  
        WelcomeView welcomeView = new WelcomeView();
        Tab welcomeTab = new Tab("Willkommen", welcomeView.getRoot());
        welcomeTab.setStyle("-fx-font-size: 25px; -fx-border-color: #4B575A; -fx-border-width: 1px; -fx-border-radius: 3px;");
        welcomeTab.setClosable(false);

        tabPane.getTabs().add(welcomeTab); // Nur den Welcome-Tab hinzufügen

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Wenn der Benutzer auf "Verstanden" klickt, entferne den Welcome-Tab und füge die restlichen Tabs hinzu
        welcomeView.getUnderstoodButton().setOnAction(e -> {
            tabPane.getTabs().remove(welcomeTab); // Welcome-Tab entfernen
            showInteractiveMainView(tabPane); // Restliche Tabs erstellen und hinzufügen
        });
    }

    public void showInteractiveMainView(TabPane tabPane) {
        // Die anderen Tabs erstellen und hinzufügen
        Tab intro = createAndAddTab(tabPane, ExplanationType.EINFÜHRUNG);
        simulationTab = new Tab("Simulation");
        simulationTab.setClosable(false);
        simulationTab.setStyle("-fx-font-size: 25px; -fx-border-color: #4B575A; -fx-background-color: #D9CFBF; -fx-border-width: 1px; -fx-border-radius: 3px;");
        showSimulationTab();

        createAndAddTab(tabPane, ExplanationType.NEAT);
        createAndAddTab(tabPane, ExplanationType.HYPERNEAT);
        createAndAddTab(tabPane, ExplanationType.PARAMETERÜBERSICHT);
        createAndAddTab(tabPane, ExplanationType.EVALUATION);
        createAndAddTab(tabPane, ExplanationType.SELEKTION);
        createAndAddTab(tabPane, ExplanationType.REPRODUKTION);
        createAndAddTab(tabPane, ExplanationType.SPEZIATION);
        createAndAddTab(tabPane, ExplanationType.MUTATION);
        

        tabPane.getTabs().add(4, simulationTab); 
        tabPane.getSelectionModel().select(intro); 
    }

    private void showSimulationTab() {
        ConfigView configView = new ConfigView();
        ConfigController configController = new ConfigController(configView, this);

        simulationTab.setContent(configView.getRoot());

        // Wenn auf "Speichern" geklickt wird, die InteractiveMainView anzeigen
        configView.getSaveButton().setOnAction(e -> {
            configController.handleSaveAction();  // Speichern und initialisieren
        });
    }

    public void initializeInteractiveMainView() {
        InteractiveMainView mainView = new InteractiveMainView(this);
        new InteractiveMainController(mainView, neat);
        simulationTab.setContent(mainView.getRoot());

        mainView.getBackButton().setOnAction(e -> showSimulationTab()); // Zurück zur ConfigView
    }

    private Tab createAndAddTab(TabPane tabPane, ExplanationType type) {
        String typeName = type.name().substring(0, 1).toUpperCase() + type.name().substring(1).toLowerCase(); // z.B. "Mutation"
        System.out.println("Erstelle Tab: " + typeName);  // Log-Ausgabe für Debugging
        String fileName = "/com/example/xai/ExplainPDFs/" + typeName + ".pdf";
        URL fileUrl = getClass().getResource(fileName);

        Tab tab = new Tab(typeName); // Tab-Überschrift setzen

        try {
            assert fileUrl != null;
            try (InputStream pdfStream = fileUrl.openStream()) {
                // Temporäre Datei erstellen und den Inhalt des Streams dort speichern
                Path tempFile = Files.createTempFile(typeName, ".pdf");
                Files.copy(pdfStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

                PDFViewerView viewerView = new PDFViewerView(0.9);
                PDFViewerController controller = new PDFViewerController(viewerView, tempFile);  // Verwende den Path zur temporären Datei
                tab.setContent(viewerView.getPdfViewPane());
                pdfControllers.add(controller);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        tab.setStyle("-fx-font-size: 25px; -fx-border-color: #4B575A; -fx-border-width: 1px; -fx-border-radius: 3px;");

        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        return tab;
    }


    private void closeAllPDFDocuments() {
        for (PDFViewerController controller : pdfControllers) {
            //controller.closeDocument();
        }
    }

    public void setNEAT(Object neat) {
        this.neat = neat;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void shutdownThreads() {
        if (neat instanceof NEAT) {
            ((NEAT) neat).setTrainingState(NEAT.TrainingState.PAUSED);
        } else if (neat instanceof HyperNEAT) {
            ((HyperNEAT) neat).setTrainingState(HyperNEAT.TrainingState.PAUSED);
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
