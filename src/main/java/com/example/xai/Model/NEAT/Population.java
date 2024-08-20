package com.example.xai.Model.NEAT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.ImageHandling.ImageAndAnswer;


public class Population {

    int populationSize;
    private Properties config;
    private ArrayList<Genome> population = new ArrayList<Genome>();
    private ArrayList<Species> species = new ArrayList<Species>();
    private ArrayList<ImageAndAnswer> imageAndAnswers;
    private int originPopulationSize;
    private final int MAXSPECIES;
    private double COMPABILITYTHRESHOLD;

    private int inputNodes;
    private int outputNodes;


    /*
     * Creates a population of Genomes with a given size. For the future, the inputs have to be optimized.
     * Have to think about a config fiile.
     */
    public Population(int originPopulationSize, int inputNodes, int outputNodes, ArrayList<ImageAndAnswer> imageAndAnswers, boolean isHyperNEAT, InnovationsTracker innovationsTracker) {
        this.imageAndAnswers = imageAndAnswers;
        this.originPopulationSize = originPopulationSize;
        this.config = loadProperties();
        this.COMPABILITYTHRESHOLD = Double.parseDouble(config.getProperty("neat.CompatibilityThreshold", "0.5"));
        for (int i = 0; i < originPopulationSize; i++) {
            Genome genome = new Genome(inputNodes, outputNodes, innovationsTracker.getNextGenomeID());

            genome.getLogger().logBirth(0);
            
            if (isHyperNEAT) {
                genome.setIsCPPN();
            }
            sortIntoSpecies(addGenome(genome), COMPABILITYTHRESHOLD);
        }
        this.MAXSPECIES = (int) (originPopulationSize * 0.2);
        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        

    }

    public List<Species> getSpecies() {
        return species;
    }
    
    public ArrayList<Genome> getPopulation() {
        population.sort((g1, g2) -> Double.compare(g2.getFitness(), g1.getFitness()));
        return population;
    }

    public Genome addGenome(Genome genome) {
        if(!population.contains(genome)){
            population.add(genome);
        }
        return genome;
    }

    public void setNewInputImage(float[][] newInputImage) {
        for (Genome genome : population) {
            genome.setInputImage(newInputImage);
        }
    }
    


    public void sortAllIntoSpecies() {
        for (Genome genome : population) {
            boolean foundSpecies = false;
    
            for (Species sp : species) {
                if (sp.isMember(genome)) {
                    foundSpecies = true;
                    break;
                } else if (sp.checkForMembership(genome, COMPABILITYTHRESHOLD)) {
                    sp.addGenome(genome);
                    foundSpecies = true;
                    break;
                }
            }
            // If the genome was not added to any species, add it to a new species or the existing species with the smallest distance
            if (!foundSpecies) {
                if (species.isEmpty() || species.size() < MAXSPECIES) {
                    species.add(new Species(species.size() + 1, genome));
                } else {
                    sortIntoExistingSpecies(genome);
                }
            }
        }
    }


    private void sortIntoExistingSpecies(Genome genome) {
        HashMap<Species, Double> speciesFitness = new HashMap<>();
        for (Species sp : species) {
            speciesFitness.put(sp, sp.calcDistance(genome));
        }
        // Get the species with the smallest distance
        Species bestFittingSpecies = speciesFitness.entrySet().stream().min(
            (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).get().getKey();
        bestFittingSpecies.addGenome(genome);
    }


    public void sortIntoSpecies(Genome genome, double threshold) {
        boolean foundSpecies = false;

        for (Species sp : species) {
            if (sp.isMember(genome)) {
                foundSpecies = true;
                return;
            } else if (sp.checkForMembership(genome, threshold)) {
                sp.addGenome(genome);
                foundSpecies = true;
                return;
            }
        }
        // TODO: LIMIT SPECIES
        if (!foundSpecies) {
            if (species.isEmpty() || species.size() < MAXSPECIES) {
                species.add(new Species(species.size() + 1, genome));
            } else {
                sortIntoExistingSpecies(genome);
            }
        }
    }

    public void removeSpecies(Species species) {
        this.species.remove(species);
    }

    public void initSpecies(){
        if(species.size() == 0){
            Species initSp = addSpecies(new Species(0));
            for (Genome genome : population) {
                initSp.addGenome(genome);
            }
        }
    }

    public ArrayList<Species> getAllSpecies() {
        return species;
    }


    public ArrayList<ImageAndAnswer> getImageAnswerCollection() {
        return imageAndAnswers;
    }


    public void eliminateMembers(ArrayList<Genome> genomes) {
        population.removeAll(genomes);
    }

    public Species addSpecies(Species species) {
        this.species.add(species);
        return species;
    }


    public void clearFitness() {
        for (Genome genome : population) {
            genome.overrideFitness(0);
        }
    }

    public int getOriginPopulationSize() {
        return originPopulationSize;
    }

    public int getOriginInputNodes() {
        return inputNodes;
    }

    public int getOriginOutputNodes() {
        return outputNodes;
    }

    /**
     * Load properties from config file. It contains the following properties:
     * - neat.EliminatePercentage
     * - neat.AddNodeChance
     * - neat.PerturbationWeights
     * - neat.MutationRate
     * - neat.WorthyProtecting
     * - neat.addConnectionAmount
     * 
     * @return Properties object
     */
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

    public Properties getConfig() {
        return config;
    }

}
