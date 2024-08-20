package com.example.xai.Model.Construction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.util.concurrent.locks.ReentrantLock;

public class TrainingService extends Service<Void> {

    private final NEAT neat;
    private final HyperNEAT hyperNeat;
    private final ReentrantLock lock = new ReentrantLock();

    public TrainingService(NEAT neat) {
        this.neat = neat;
        this.hyperNeat = null;
    }

    public TrainingService(HyperNEAT hyperNeat) {
        this.neat = null;
        this.hyperNeat = hyperNeat;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                if (neat != null) {
                    runNEATTraining(neat);
                } else if (hyperNeat != null) {
                    runHyperNEATTraining(hyperNeat);
                }
                return null;
            }
        };
    }

    private void runNEATTraining(NEAT neat) {
        while (neat.getCurrentGenerationIndex() < neat.getGenerations()) {
            lock.lock();
            try {
                while (neat.getTrainingState() == NEAT.TrainingState.PAUSED) {
                    try {
                        synchronized (neat) {
                            neat.wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (neat.getTrainingState() == NEAT.TrainingState.NEXT_STEP ||
                    neat.getTrainingState() == NEAT.TrainingState.RUNNING) {
                    neat.createGeneration();
                    neat.notifyUpdate();

                    if (neat.getTrainingState() == NEAT.TrainingState.NEXT_STEP) {
                        neat.setTrainingState(NEAT.TrainingState.PAUSED);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void runHyperNEATTraining(HyperNEAT hyperNeat) {
        while (hyperNeat.getCurrentGenerationIndex() < hyperNeat.getGenerations()) {
            lock.lock();
            try {
                while (hyperNeat.getTrainingState() == HyperNEAT.TrainingState.PAUSED) {
                    try {
                        synchronized (hyperNeat) {
                            hyperNeat.wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (hyperNeat.getTrainingState() == HyperNEAT.TrainingState.NEXT_STEP ||
                    hyperNeat.getTrainingState() == HyperNEAT.TrainingState.RUNNING) {
                    hyperNeat.createGeneration();
                    hyperNeat.notifyUpdate();

                    if (hyperNeat.getTrainingState() == HyperNEAT.TrainingState.NEXT_STEP) {
                        hyperNeat.setTrainingState(HyperNEAT.TrainingState.PAUSED);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void pauseTraining() {
        if (neat != null) {
            neat.setTrainingState(NEAT.TrainingState.PAUSED);
        } else if (hyperNeat != null) {
            hyperNeat.setTrainingState(HyperNEAT.TrainingState.PAUSED);
        }
    }

    public void resumeTraining() {
        if (neat != null) {
            neat.setTrainingState(NEAT.TrainingState.RUNNING);
            synchronized (neat) {
                neat.notifyAll();
            }
        } else if (hyperNeat != null) {
            hyperNeat.setTrainingState(HyperNEAT.TrainingState.RUNNING);
            synchronized (hyperNeat) {
                hyperNeat.notifyAll();
            }
        }
    }

    public void nextStep() {
        if (neat != null) {
            neat.setTrainingState(NEAT.TrainingState.NEXT_STEP);
            synchronized (neat) {
                neat.notifyAll();
            }
        } else if (hyperNeat != null) {
            hyperNeat.setTrainingState(HyperNEAT.TrainingState.NEXT_STEP);
            synchronized (hyperNeat) {
                hyperNeat.notifyAll();
            }
        }
    }
}
