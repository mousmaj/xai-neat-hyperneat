package com.example.xai.Model.NEAT;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.math.RoundingMode;

import com.example.xai.Model.HyperNEAT.GenomeFromSubstrate;
import com.example.xai.Model.ImageHandling.ImageAndAnswer;
import com.example.xai.Model.Genotype.Genome;



public class GenerationFlexibel {

    private Properties config;
    private final int generationNumber;
    private Population population;
    private double ELIMINATEPERCANTAGE;
    private double ADDNODECHANCE;
    private double PERTUBATIONWEIGHTS;
    private double WEIGHTMUTATIONRATE;
    private double WORTHLYPROTECTING;
    private double ADDCONNECTIONCHANCE;
    private double COMPABILITYTHRESHOLD;
    private boolean HYPERNEAT;

    private InnovationsTracker innovationsTracker;
    private static Random RANDOM = new Random();
    private final int POPULATIONSIZE;
    private final double WITHCROSSOVER = 0.75;

    /**
     * Generation class is responsible for:
     * 1. Evaluating the fitness of each genome in the population
     * 2. Selecting the best genomes for reproduction (including elimination of worst genomes)
     * 3. Reproducing the selected genomes
     * 4. Mutating the population
     * (5. Predicting the output of each genome in the population) - For GUI purposes.
     */
    public GenerationFlexibel(int generationNumber, Population population, int initialPopulationSize,
            InnovationsTracker innovationsTracker, Properties config) {
        this.config = config;
        this.population = population;
        this.generationNumber = generationNumber;
        this.POPULATIONSIZE = initialPopulationSize;
        this.innovationsTracker = innovationsTracker;
        this.ELIMINATEPERCANTAGE = Double.parseDouble(config.getProperty("neat.EliminatePercentage", "0.2"));
        this.ADDNODECHANCE = Double.parseDouble(config.getProperty("neat.AddNodeChance", "0.05"));
        this.PERTUBATIONWEIGHTS = Double.parseDouble(config.getProperty("neat.PerturbationWeights", "0.5"));
        this.WEIGHTMUTATIONRATE = Double.parseDouble(config.getProperty("neat.MutationRate", "0.01"));
        this.WORTHLYPROTECTING = Double.parseDouble(config.getProperty("neat.WorthyProtecting", "0.8"));
        this.ADDCONNECTIONCHANCE = Double.parseDouble(config.getProperty("neat.AddConnectionChance", "0.08"));
        this.COMPABILITYTHRESHOLD = Double.parseDouble(config.getProperty("neat.CompatibilityThreshold", "0.5"));
        this.HYPERNEAT = Boolean.parseBoolean(config.getProperty("neat.HyperNeat", "false"));
        
        

    }



    /**
     * Get the current generation number. Its primary use is for gui display.
     * 
     * @return generation number
     */
    public int getGenerationNumber() {
        return generationNumber;
    }

    /**
     * eliminate the worst performing genomes from the population. Only the best will be selected for further reproduction and mutation.
     * 
     */
    public void selection(){
        for (Species species : population.getAllSpecies()) {
            ArrayList<Genome> selectedForDeath = species.eliminate(ELIMINATEPERCANTAGE, species.safeGenomes(WORTHLYPROTECTING));
            population.eliminateMembers(selectedForDeath);
            // Log deaths
             for (Genome genome : selectedForDeath) {
                 genome.getLogger().logDeath(generationNumber);
             }
        }
    }

    /**
     * Reproduction of the population. It will create new genomes based on the best performing genomes of the current generation.
     * 
     * @return list of new created genomes
     */
    public ArrayList<Genome> reproduction() {
        int populationSize = population.getPopulation().size();
        int populationLoss = this.POPULATIONSIZE - populationSize;
        ArrayList<Genome> newGenomes = new ArrayList<Genome>();

        // Filter out stagnant species
        List<Species> activeSpecies = population.getAllSpecies().stream()
                .collect(Collectors.toList());

        // If no species are active, just refill the population with random genomes
        if (activeSpecies.isEmpty()) {
            Genome genome = population.getPopulation().get(RANDOM.nextInt(populationSize));
            for (int i = 0; i < populationLoss; i++) {
                population.sortIntoSpecies((population
                        .addGenome(new Genome(genome.getInputNodes().size(), genome.getOutputNodes().size(), innovationsTracker.getNextGenomeID()))), COMPABILITYTHRESHOLD);
            }
            return null;
        }

        // Calculate total fitness of active species
        double activeTotalFitness = activeSpecies.stream().mapToDouble(Species::getSharedFitness).sum();

        // Initialize variables for distribution
        int[] speciesOffspring = new int[activeSpecies.size()];
        double[] speciesFraction = new double[activeSpecies.size()];
        int leftOverPopulation = populationLoss;

        // Calculate initial offspring count based on fitness share
        for (int i = 0; i < activeSpecies.size(); i++) {
            Species species = activeSpecies.get(i);
            double speciesShare = species.getSharedFitness() / activeTotalFitness;
            double exactOffspring = speciesShare * populationLoss;
            speciesOffspring[i] = (int) exactOffspring;
            speciesFraction[i] = exactOffspring - speciesOffspring[i]; // Store fractional part for later
            leftOverPopulation -= speciesOffspring[i];
        }

        // Distribute remaining offspring based on fractional parts
        while (leftOverPopulation > 0) {
        int maxFractionIndex = 0;
        for (int i = 1; i < speciesFraction.length; i++) {
            if (speciesFraction[i] > speciesFraction[maxFractionIndex]) {
                maxFractionIndex = i;
            }
        }
        speciesOffspring[maxFractionIndex]++;
        speciesFraction[maxFractionIndex] = 0; // Reset fractional part after assigning offspring
        leftOverPopulation--;
    }

        // Create offspring based on the calculated distribution
        for (int i = 0; i < activeSpecies.size(); i++) {
            Species species = activeSpecies.get(i);
            for (int j = 0; j < speciesOffspring[i]; j++) {
                // 25 % chance of not using crossover. Creates a new minimum genome with random weights.
                if (RANDOM.nextDouble() <= WITHCROSSOVER) {
                    Genome offspring;
                    // If species only has one member, perform asexual reproduction. It will create a new genome with the same structure as the parent.
                    // But with weights perturbed by a small amount of 10%.
                    if (species.getSortedSpeciesMemberList().size() < 2) {
                        offspring = Crossover.aSexualCrossover(species.getRandomGenome(), innovationsTracker.getNextGenomeID(), generationNumber);
                        offspring.setLastNodeID();
                    } else {
                        Genome mother = species.getRandomGenome();
                        Genome father = species.getRandomGenome(mother); // Ensure mother and father are not the same genome
                        offspring = Crossover.runCrossover(mother, father, innovationsTracker.getNextGenomeID(), generationNumber);
                    }
                    population.sortIntoSpecies(population.addGenome(offspring), COMPABILITYTHRESHOLD);
                    newGenomes.add(offspring);

                } else {
                    Genome offspring = new Genome(population.getOriginInputNodes(), population.getOriginOutputNodes(), innovationsTracker.getNextGenomeID());
                    population.sortIntoSpecies(population.addGenome(offspring), COMPABILITYTHRESHOLD);
                    offspring.getLogger().withoutCrossOver(generationNumber);
                    newGenomes.add(offspring);
                }
            }
        }
        return newGenomes;
    }

    /**
     * Mutate the genomes of the population. It will add new nodes, connections and mutate weights of the genomes.
     * As a result, the population will be more diverse and have a higher chance of finding the optimal solution.
     * 
     */
    public void mutation() {
        ArrayList<Species> species = population.getAllSpecies();
        for (Species sp : species) {
            ArrayList<Genome> genomesToMutate = sp.genomesToMutate(WORTHLYPROTECTING);
            for (Genome genome : genomesToMutate) {
                double random = RANDOM.nextDouble();
                if (random <= ADDNODECHANCE) {
                    Mutation.addNode(genome, this.innovationsTracker, generationNumber);
                }
                random = RANDOM.nextDouble();
                if(random <= ADDCONNECTIONCHANCE){
                    Mutation.addConnection(genome, this.innovationsTracker, generationNumber);
                }
                Mutation.mutateWeights(genome, WEIGHTMUTATIONRATE, PERTUBATIONWEIGHTS, generationNumber);
            }
        }
    }

    /**
     * Evaluate the fitness of the genomes in the population. It will evaluate the fitness of the genomes based on the images and answers in the population.
     * Each right answer will increase the fitness of the genome by 1.
     * 
     */
    public void evaluation(ArrayList<Genome> subPopulation) {
        for (Genome gen : subPopulation) {
            gen.clearFitness();
        }
        int numThreads = Runtime.getRuntime().availableProcessors()-2; // Use all available processors except two
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
    
        for (Genome genome : subPopulation) {
            Genome phenoGenome;
            if (HYPERNEAT){
            phenoGenome = new Genome(784, 10, true);
            GenomeFromSubstrate.createNew(genome, phenoGenome);
            genome.setPhenoGenome(phenoGenome);
            } else {
                phenoGenome = null;
            }
            Future<?> future = executorService.submit(() -> {
                for (ImageAndAnswer imageAndAnswer : population.getImageAnswerCollection()) {
                    if (HYPERNEAT) {
                        phenoGenome.setInputImage(imageAndAnswer.getImage2D());
                        phenoGenome.predict();
    
                        synchronized (genome) { // Ensure thread-safe modification of fitness
                            if(phenoGenome.answerCorrect(imageAndAnswer.getAnswer())){
                                genome.addFitness(1);
                            }
                        }
                    } else {
                        genome.setInputImage(imageAndAnswer.getImage2D());
                        genome.predict();
                        
                        synchronized (genome) { // Ensure thread-safe modification of fitness
                            if(genome.answerCorrect(imageAndAnswer.getAnswer())){
                                genome.addFitness(1);
                            }
                        }
                    }
                }
            });
            futures.add(future);
        }
    
        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // Log the fitness after all threads have completed
        for (Genome genome : subPopulation) {
            genome.getLogger().logCurrentFitness(generationNumber, (int) genome.getFitness(), (genome.getFitness() / population.getImageAnswerCollection().size())*100);
        }
    }

}
