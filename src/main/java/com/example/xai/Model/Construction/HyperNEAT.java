package com.example.xai.Model.Construction;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.ImageHandling.ImageAndAnswer;
import com.example.xai.Model.ImageHandling.MnistSubsetLoader;
import com.example.xai.Model.NEAT.GenerationFlexibel;
import com.example.xai.Model.NEAT.InnovationsTracker;
import com.example.xai.Model.NEAT.Population;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class HyperNEAT {

    public interface TrainingUpdateListener {
        void onTrainingUpdate(Population population);
    }

    private List<TrainingUpdateListener> listeners = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private ArrayList<ImageAndAnswer> imageAnswerCollection;
    private Population population;
    private int generations;
    private ArrayList<GenerationFlexibel> generationsList = new ArrayList<GenerationFlexibel>();
    private final int initialPopulationSize;
    private InnovationsTracker innovationsTracker = new InnovationsTracker();
    private GenerationFlexibel currentGeneration;
    private int currentGenerationIndex = 0;
    private volatile TrainingState trainingState = TrainingState.PAUSED;

    public enum TrainingState {
        PAUSED, RUNNING, NEXT_STEP
    }

    /*
     * Constructor for HyperNEAT
     */
    public HyperNEAT(int initialPopulationSize, int generations) {
        this.innovationsTracker.init(4, 1);
        try {
            this.imageAnswerCollection = MnistSubsetLoader.loadMnistSubset("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte", 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.population = new Population(initialPopulationSize, 4, 1, this.imageAnswerCollection, true, this.innovationsTracker);
        this.generations = generations;
        this.initialPopulationSize = initialPopulationSize;
    }

    public void setTrainingState(TrainingState state) {
        lock.lock();
        try {
            this.trainingState = state;
            System.out.println("Training State: " + state);
            synchronized (this) {
                notifyAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public TrainingState getTrainingState() {
        lock.lock();
        try {
            return trainingState;
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentGenerationIndex() {
        return currentGenerationIndex;
    }

    public int getGenerations() {
        return generations;
    }

    public GenerationFlexibel createGeneration() {
        this.currentGeneration = new GenerationFlexibel(currentGenerationIndex, this.population, this.initialPopulationSize, this.innovationsTracker, population.getConfig());
        generationsList.add(this.currentGeneration);
        currentGenerationIndex++;
        runGeneration();
        return this.currentGeneration;
    }

    public void runGeneration() {
        this.currentGeneration.evaluation(this.population.getPopulation());
        this.currentGeneration.selection();
        ArrayList<Genome> newGenomes = this.currentGeneration.reproduction();
        this.currentGeneration.mutation();
        this.currentGeneration.evaluation(newGenomes);
    }

    public void notifyUpdate() {
        for (TrainingUpdateListener listener : listeners) {
            listener.onTrainingUpdate(population);
        }
    }

    public void addTrainingUpdateListener(TrainingUpdateListener listener) {
        listeners.add(listener);
    }

    public Population getPopulation() {
        return population;
    }

    public ArrayList<ImageAndAnswer> getImageAnswerCollection() {
        return imageAnswerCollection;
    }

    public Genome training() {
        while (currentGenerationIndex < generations) {
            lock.lock();
            try {
                while (trainingState == TrainingState.PAUSED) {
                    waitWhilePaused();
                }

                if (trainingState == TrainingState.NEXT_STEP || trainingState == TrainingState.RUNNING) {
                    createGeneration();
                    //notifyUpdate();

                    if (trainingState == TrainingState.NEXT_STEP) {
                        setTrainingState(TrainingState.PAUSED);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return population.getPopulation().get(0); // Best genome
    }

    private void waitWhilePaused() {
        lock.unlock();
        try {
            synchronized (this) {
                while (trainingState == TrainingState.PAUSED) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.lock();
        }
    }

}
