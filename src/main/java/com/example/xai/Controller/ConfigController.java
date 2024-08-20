package com.example.xai.Controller;

import com.example.xai.Frontend.MainApp;
import com.example.xai.Model.Construction.NEAT;
import com.example.xai.Model.Construction.HyperNEAT;
import com.example.xai.View.ConfigView;
import javafx.scene.control.Button;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigController {
    private ConfigView view;
    private MainApp mainApp;

    public ConfigController(ConfigView view, MainApp mainApp) {
        this.view = view;
        this.mainApp = mainApp;
        initController();
    }

    private void initController() {
        loadProperties();  // Load properties when the controller is initialized
        view.getSaveButton().setOnAction(e -> handleSaveAction());
    }

    private void loadProperties() {
        Properties properties = new Properties();
        Path targetPath = Paths.get(System.getProperty("user.dir"), "target", "config.properties");
        Path resourcePath = Paths.get("src", "main", "resources", "com", "example", "xai", "ConfigFile", "config.properties");

        try (InputStream input = Files.exists(targetPath) ?
                new FileInputStream(targetPath.toFile()) :
                getClass().getResourceAsStream("/com/example/xai/ConfigFile/config.properties")) {

            if (input == null) {
                throw new FileNotFoundException("config.properties file not found in the path: " + resourcePath);
            }

            properties.load(input);

            // Werte aus der Properties-Datei laden und auf die Slider setzen
            view.getPopulationSizeSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.PopulationSize", "10"))));
            view.getEliminatePercentageSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.EliminatePercentage", "0.2")) * 100.0));
            view.getAddNodeChanceSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.AddNodeChance", "0.05")) * 100.0));
            view.getAddConnectionChanceSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.AddConnectionChance", "0.01")) * 100.0));
            view.getPerturbationWeightsSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.PerturbationWeights", "0.5")) * 100.0));
            view.getMutationRateSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.MutationRate", "0.01")) * 100.0));
            view.getWorthyProtectingSlider().setValue(Math.round(Double.parseDouble(properties.getProperty("neat.WorthyProtecting", "0.8")) * 100.0));
            view.getCompatibilityThresholdSlider().setValue(Double.parseDouble(properties.getProperty("neat.CompatibilityThreshold", "0.3")));

            boolean isHyperNeat = Boolean.parseBoolean(properties.getProperty("neat.HyperNeat", "false"));
            if (isHyperNeat) {
                view.getHyperNeatButton().fire();
            } else {
                view.getNeatButton().fire();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSaveAction() {
        Properties properties = new Properties();
        properties.setProperty("neat.PopulationSize", String.valueOf((int) view.getPopulationSizeSlider().getValue()));
        properties.setProperty("neat.EliminatePercentage", String.valueOf(view.getEliminatePercentageSlider().getValue() / 100));
        properties.setProperty("neat.AddNodeChance", String.valueOf(view.getAddNodeChanceSlider().getValue() / 100));
        properties.setProperty("neat.AddConnectionChance", String.valueOf(view.getAddConnectionChanceSlider().getValue() / 100));
        properties.setProperty("neat.PerturbationWeights", String.valueOf(view.getPerturbationWeightsSlider().getValue() / 100));
        properties.setProperty("neat.MutationRate", String.valueOf(view.getMutationRateSlider().getValue() / 100));
        properties.setProperty("neat.WorthyProtecting", String.valueOf(view.getWorthyProtectingSlider().getValue() / 100));
        properties.setProperty("neat.CompatibilityThreshold", String.valueOf(view.getCompatibilityThresholdSlider().getValue()));
        properties.setProperty("neat.HyperNeat", String.valueOf(view.isHyperNeatButtonActive()));

        // Speichere die Datei im target-Verzeichnis
        String targetFilePath = Paths.get(System.getProperty("user.dir"), "target", "config.properties").toString();

        try {
            // Erstelle das Verzeichnis, falls es nicht existiert
            Files.createDirectories(Paths.get(targetFilePath).getParent());

            // Speichere die Properties-Datei
            try (OutputStream output = new FileOutputStream(targetFilePath)) {
                properties.store(output, null);
                System.out.println("Properties file saved to " + targetFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Nachdem die Eigenschaften gespeichert wurden, initialisieren wir die Simulation
        initializeNEATFromProperties(properties);
    }



    private void initializeNEATFromProperties(Properties properties) {
        boolean isHyperNeat = Boolean.parseBoolean(properties.getProperty("neat.HyperNeat"));
        if (isHyperNeat) {
            mainApp.setNEAT(new HyperNEAT(
                    Integer.parseInt(properties.getProperty("neat.PopulationSize")),
                    1000));
        } else {
            mainApp.setNEAT(new NEAT(
                    Integer.parseInt(properties.getProperty("neat.PopulationSize")),
                    1000));
        }

        // Die InteractiveMainView anzeigen
        mainApp.initializeInteractiveMainView();
    }
}
