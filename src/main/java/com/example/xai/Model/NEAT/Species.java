package com.example.xai.Model.NEAT;

import com.example.xai.Model.Genotype.ConnectionGene;
import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.NodeGene;

import java.util.ArrayList;
import java.util.Set;



import java.util.HashSet;
import java.util.Map;

public class Species {

    private ArrayList<Genome> genomes;
    // Take from config file later
    private int speciesId;
    Boolean debugMode = true;

    public Species(int speciesId) {
        this.speciesId = speciesId;
        this.genomes = new ArrayList<>();
        // Take from config file later and set to a reasonable value
    }

    public Species(int speciesId, Genome genome) {
        this.speciesId = speciesId;
        this.genomes = new ArrayList<>();
        this.genomes.add(genome);
    }


    public boolean isMember (Genome genome) {
        for (Genome listGenome : genomes) {
            if (listGenome.equals(genome)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForMembership(Genome genome, double threshold){
        double distance = calcDistance(genome);
        return distance < threshold;
    }

    public void addGenome(Genome genome) {
        if(!genomes.contains(genome)){
            genomes.add(genome);
        }
    }

    /*
     * Calculate the distance between two genomes. decieded to not distinguish between disjoint and excess genes, because fitter genome inherits his disjoint genes.
     */
    public double calcDistance(Genome genome) {
        Genome representative = genomes.get((int) (Math.random() * genomes.size()));
        //System.out.println("Genome: " + genome.getHiddenNodes().size() + " Representative: " + representative.getHiddenNodes().size()); 
        Map<Integer, ConnectionGene> representativeConnections = representative.getConnections();
    
        // Collect Ids of representative hidden nodes
        Set<Integer> representativeHiddenNodes = new HashSet<>();
        for (NodeGene key : representative.getHiddenNodes()) {
            representativeHiddenNodes.add(key.getId());
        }

        // Collect Ids of genome hidden nodes
        Set<Integer> genomeHiddenNodes = new HashSet<>();
        for (NodeGene key : genome.getHiddenNodes()) {
            genomeHiddenNodes.add(key.getId());
        }
        Set<Integer> matchingHiddenNodes = new HashSet<>(genomeHiddenNodes);
        matchingHiddenNodes.retainAll(representativeHiddenNodes);

        Set<Integer> disjointHiddenNodes = new HashSet<>(genomeHiddenNodes);
        disjointHiddenNodes.addAll(representativeHiddenNodes);
        disjointHiddenNodes.removeAll(matchingHiddenNodes);

        
        Set<Integer> representativeKeys = representativeConnections.keySet();
        Set<Integer> genomeKeys = genome.getConnections().keySet();
        Set<Integer> matching = new HashSet<>(genomeKeys);
        matching.retainAll(representativeKeys);
    

        //System.out.println("Disjoint: " + disjointHiddenNodes.size());
    
        double weightDifferenceSum = matching.stream().mapToDouble(key -> 
            Math.abs(genome.getConnections().get(key).getWeight() + representativeConnections.get(key).getWeight()))
            .sum();
    
        double weightDifferenceAVG = matching.size() > 0 ? weightDifferenceSum / matching.size() : 0;

        //System.out.println("WeightDifferenceAVG: " + weightDifferenceAVG);
    
        int nGenome = genome.getHiddenNodes().size() + genome.getOutputNodes().size() + genome.getInputNodes().size();
        int nRepresentative = representative.getHiddenNodes().size() + representative.getOutputNodes().size() + representative.getInputNodes().size();

        double n = Math.max(nGenome, nRepresentative);
    
        double disjointDistance = n > 0 ? ((disjointHiddenNodes.size() * 1.0) / n) : 0;
        double distance = disjointDistance + (weightDifferenceAVG * 0.4);
    
        //System.out.println("Distance: " + distance + " Disjoint: " + disjointDistance  + " Weight: " + weightDifferenceAVG);
        return distance;
    }

    /*
     * Sorted by fitness of each genome
     */
    public ArrayList<Genome> getSortedSpeciesMemberList() {
        sortGenomes();
        return genomes;
    }

    public Genome getBestGenomes(){
        int index = 1;
        if(genomes.size() == 0){
            return null;
        }
        // sort genomes by fitness highest first
        sortGenomes();        

        return getSortedSpeciesMemberList().get(0);
    }


    public ArrayList<Genome> eliminate(double percentage, ArrayList<Genome> safeGenomes) {
        ArrayList<Genome> eliminated = new ArrayList<>();
        sortGenomes();
        int amount = (int) (genomes.size() * percentage);
        //System.out.println("Eliminate: " + amount + " from " + genomes.size() + " in species " + speciesId);
        for (int i = 0; i < amount; i++) {
            if(!safeGenomes.contains(genomes.get(genomes.size() - 1))){
                eliminated.add(genomes.get(genomes.size() - 1));
                genomes.remove(genomes.size() - 1);
            }  
        }
        return eliminated;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public double getSharedFitness() {
        double sharedFitness = 0;
        for (Genome genome : genomes) {
            sharedFitness += genome.getFitness();
        }
        return sharedFitness / genomes.size();
    }

    public Genome getRandomGenome() {
        return this.genomes.get((int) (Math.random() * genomes.size()));
    }
    
    public Genome getRandomGenome(Genome genome) {
        ArrayList<Genome> excludGenomes = new ArrayList<>(genomes);
        excludGenomes.remove(genome);
        return excludGenomes.get((int) (Math.random() * excludGenomes.size()));
    }

    public ArrayList<Genome> safeGenomes(double percentage) {
        sortGenomes();
        ArrayList<Genome> sortedGenomes = new ArrayList<>(genomes);
        ArrayList<Genome> safeGenomes = new ArrayList<>();
        // Hold percentage of the genomes. ceil to get at least one genome
        int amount = (int) Math.ceil(sortedGenomes.size() * percentage);
        // sort genomes by fitness highest first
        for (int i = 0; i < amount; i++) {
            if(sortedGenomes.get(i).getFitness() > 0.0){
                safeGenomes.add(sortedGenomes.get(i));
            }
        }
        return safeGenomes;
    }

    public ArrayList<Genome> genomesToMutate(double safeValue){
        ArrayList<Genome> genomesToMutate = new ArrayList<>();
        ArrayList<Genome> safeGenomes = safeGenomes(safeValue);
        for(Genome genome : genomes){
            if(!safeGenomes.contains(genome)){
                genomesToMutate.add(genome);
            }
        }
        return genomesToMutate;
    }

    public void sortGenomes() {
        genomes.sort((o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness()));
    }




}