package com.example.xai.Model.Construction;

import java.util.ArrayList;
import java.util.List;

public class GenomeLogger {

    private List<String> logs;

    public GenomeLogger() {
        this.logs = new ArrayList<>();
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public void logBirth(int generation) {
        String log = String.format("Generation-%d: Ein neues Individuum wurde in dieser Generation geboren (Genotyp erstellt). (REPRODUKTION 3)", generation);
        addLog(log);
    }

    public void withoutCrossOver(int generation) {
        String log = String.format("Generation-%d: Ein neues Individuum wurde mit zufälligen Werten erzeugt, es hat keine Vorfahren. (REPRODUKTION 3)", generation);
        addLog(log);
    }

    public void logDeath(int generation) {
        String log = String.format("Generation-%d: Ein Individuum ist in dieser Generation gestorben (Genotyp entfernt). (SELEKTION)", generation);
        addLog(log);
    }
    
    public void logNewNodeAdded(int generation, int newNodeId, int fromNodeId, int toNodeId) {
        String log = String.format("Generation-%d: Ein neues Neuron (Knoten) mit ID %d wurde im Genotyp zwischen den Neuronen (Knoten) %d und %d hinzugefügt. (MUTATION 3)", generation, newNodeId, fromNodeId, toNodeId);
        addLog(log);
    }

    public void logNewConnectionAdded(int generation, int genome1, int genome2) {
        String log = String.format("Generation-%d: Eine neue Verbindung im Genotyp wurde von Neuron (Knoten) %d zu Neuron (Knoten) %d hinzugefügt. (MUTATION 2)", generation, genome1, genome2);
        addLog(log);
    }

    public void logMutatetWeights(int generation, int sum, int newValues){
        String log = String.format("Generation-%d: In dieser Generation wurden %d Gewichte im Genotyp mutiert, davon wurden %d neue zufällige Werte gesetzt. (MUTATION 1)", generation, sum, newValues);
        addLog(log);
    }

    public void logAsexualReproduction(int generation, int parent1Id) {
        String log = String.format("Generation-%d: Ein neues Individuum wurde durch Parthenogenese des Individuums mit Genotyp ID %d erstellt. (REPRODUKTION)", generation, parent1Id);
        addLog(log);
    }

    public void logCrossover(int generation, int parent1Id, int parent2Id) {
        String log = String.format("Generation-%d: Ein neues Individuum wurde durch die Kreuzung der Genotypen mit den IDs %d und %d erstellt. (REPRODUKTION)", generation, parent1Id, parent2Id);
        addLog(log);
    }

    public void logCurrentFitness(int generation, int imagesAmount,  double fitness) {
        String log = String.format("Generation-%d: Das Individuum hat %d Bilder korrekt klassifiziert. Aktuelles Fitnesslevel beträgt %.2f%%. (EVALUATION)", generation, imagesAmount, fitness);
        addLog(log);
    }

}
