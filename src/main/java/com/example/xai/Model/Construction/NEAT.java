package com.example.xai.Model.Construction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.NEAT.InnovationsTracker;
import com.example.xai.Model.ImageHandling.ImageAndAnswer;
import com.example.xai.Model.ImageHandling.MnistSubsetLoader;
import com.example.xai.Model.NEAT.GenerationFlexibel;
import com.example.xai.Model.NEAT.Population;


public class NEAT {

    public interface TrainingUpdateListener {
        void onTrainingUpdate(Population population);
    }

    private List<TrainingUpdateListener> listeners = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private ArrayList<ImageAndAnswer> imageAnswerCollection;
    private Population population;
    private int INPUTPIXEL;
    private int OUTPUT;
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
     * Constructor for NEAT
     */
    public NEAT(int initialPopulationSize, int generations) {
        this.OUTPUT = 10; // Für MNIST sind es immer 10 Klassen (Ziffern 0-9)
        try {
            this.imageAnswerCollection = MnistSubsetLoader.loadMnistSubset("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte", 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.INPUTPIXEL = (int) Math.pow(28, 2);
        this.innovationsTracker.init(INPUTPIXEL, OUTPUT);
        this.population = new Population(initialPopulationSize, INPUTPIXEL, OUTPUT, this.imageAnswerCollection, false, this.innovationsTracker);
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

    /**
     * For IDE use only. TrainingService is used for training in the GUI.
     * @return Genome
     */
    public Genome training() {
        while (currentGenerationIndex < generations) {
            lock.lock();
            try {
                while (trainingState == TrainingState.PAUSED) {
                    waitWhilePaused();
                }

                if (trainingState == TrainingState.NEXT_STEP || trainingState == TrainingState.RUNNING) {
                    createGeneration();
                    notifyUpdate();
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

    public int getCurrentGenerationIndex() {
        return currentGenerationIndex;
    }

    public int getGenerations() {
        return generations;
    }


    /**
     * 
     * creates a new generation and runs it.
     * 
     * @return GenerationFlexibel the new generation
     */
    public GenerationFlexibel createGeneration() {
        this.currentGeneration = new GenerationFlexibel(currentGenerationIndex, this.population, this.initialPopulationSize, this.innovationsTracker, population.getConfig());
        generationsList.add(this.currentGeneration);
        runGeneration();
        currentGenerationIndex++;
        return this.currentGeneration;
    }

    /**
     * 
     * defines the steps of a generation.
     * 
     */
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

    // NEAT.java
    public ArrayList<ImageAndAnswer> getImageAnswerCollection() {
        return imageAnswerCollection;
    }

    public int getCurrentGeneration() {
        return currentGenerationIndex;
    }

}
