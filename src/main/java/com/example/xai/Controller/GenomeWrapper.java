package com.example.xai.Controller;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.ConnectionGene;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GenomeWrapper {
    private final StringProperty statusSymbol;
    private final SimpleIntegerProperty spezie;
    private final SimpleIntegerProperty rang;
    private final SimpleStringProperty id;
    private final SimpleIntegerProperty inputCount;
    private final SimpleIntegerProperty hiddenCount;
    private final SimpleIntegerProperty outputCount;
    private final SimpleIntegerProperty connectionCount;
    private final SimpleDoubleProperty fitness;
    private final StringProperty fitnessPercent;
    private final StringProperty action;
    private final StringProperty speciesColor; // Neue Eigenschaft für Speziesfarbe

    private final Genome genome;
    private final int totalImages;

    public GenomeWrapper(Genome genome, int spezie, int rang, int totalImages) {
        this.statusSymbol = new SimpleStringProperty("");
        this.genome = genome;
        this.spezie = new SimpleIntegerProperty(spezie);
        this.rang = new SimpleIntegerProperty(rang);
        this.id = new SimpleStringProperty(String.valueOf(genome.getGenomeID()));
        this.inputCount = new SimpleIntegerProperty(genome.getInputNodes().size());
        this.hiddenCount = new SimpleIntegerProperty(genome.getHiddenNodes().size());
        this.outputCount = new SimpleIntegerProperty(genome.getOutputNodes().size());
        this.connectionCount = new SimpleIntegerProperty((int) genome.getConnections().values().stream().filter(ConnectionGene::isEnabled).count());
        this.fitness = new SimpleDoubleProperty(genome.getFitness());
        this.totalImages = totalImages;
        this.fitnessPercent = new SimpleStringProperty(String.format("%.2f %%", (genome.getFitness() / totalImages) * 100));
        this.action = new SimpleStringProperty("▶");
        this.speciesColor = new SimpleStringProperty(); // Initialisieren, aber später setzen

    }

    public String getStatusSymbol() {
        return statusSymbol.get();
    }

    public void setStatusSymbol(String status) {
        this.statusSymbol.set(status);
    }

    public StringProperty statusSymbolProperty() {
        return statusSymbol;
    }

    public int getSpezie() {
        return spezie.get();
    }

    public int getRang() {
        return rang.get();
    }

    public String getId() {
        return id.get();
    }

    public int getInputCount() {
        return inputCount.get();
    }

    public int getHiddenCount() {
        return hiddenCount.get();
    }

    public int getOutputCount() {
        return outputCount.get();
    }

    public int getConnectionCount() {
        return connectionCount.get();
    }

    public double getFitness() {
        return fitness.get();
    }

    public String getFitnessPercent() {
        return fitnessPercent.get();
    }

    public String getAction() {
        return action.get();
    }

    public String getSpeciesColor() {
        return speciesColor.get();
    }

    public void setSpeciesColor(String color) {
        this.speciesColor.set(color);
    }

    public SimpleIntegerProperty spezieProperty() {
        return spezie;
    }

    public SimpleIntegerProperty rangProperty() {
        return rang;
    }

    public StringProperty idProperty() {
        return id;
    }

    public SimpleIntegerProperty inputCountProperty() {
        return inputCount;
    }

    public SimpleIntegerProperty hiddenCountProperty() {
        return hiddenCount;
    }

    public SimpleIntegerProperty outputCountProperty() {
        return outputCount;
    }

    public SimpleIntegerProperty connectionCountProperty() {
        return connectionCount;
    }

    public SimpleDoubleProperty fitnessProperty() {
        return fitness;
    }

    public StringProperty fitnessPercentProperty() {
        return fitnessPercent;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty speciesColorProperty() {
        return speciesColor;
    }

    public Genome getGenome() {
        return genome;
    }

    public void handleAction() {
        System.out.println("Action performed on genome with input count: " + genome.getInputNodes().size());
    }
}
