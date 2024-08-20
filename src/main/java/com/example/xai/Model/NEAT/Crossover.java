/**
 * This class is responsible for the crossover of two genomes.
 * @Project: NEAT_Java
 * @Author: Majid Moussa Adoyi
 * @Date: 05.04.2024
 * @Version: 1.0
 */

package com.example.xai.Model.NEAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.example.xai.Model.Genotype.ConnectionGene;
import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.NodeGene;


public class Crossover {

    /*
     * The main method for the crossover. It calls the other methods to collect the matching, disjoint and excess genes between the two genomes.
     * @param mother The mother genome.
     * @param father The father genome.
     * @return The offspring genome.
     */
    public static Genome runCrossover(Genome mother, Genome father, int newGenomeID, int generation) {
        if (mother == null || father == null) {
            throw new IllegalArgumentException("Mother and father genomes cannot be null");
        }
        Genome fitterGenome = getFitterGenome(mother, father);
        HashMap<NodeGene, NodeGene> representationNodesMap = representationNodesMap(fitterGenome.getInputNodes(), fitterGenome.getHiddenNodes(), fitterGenome.getOutputNodes());
        HashMap<Integer, ConnectionGene> matchingGenes = getMatchingGenes(father, mother, fitterGenome, representationNodesMap);
        HashMap<Integer, ConnectionGene> disjointGenes = getDisjointGenes(matchingGenes, father, mother, fitterGenome, representationNodesMap);
        if(matchingGenes.isEmpty()){
            throw new NullPointerException("matchingGenes empty in runCrossover() in Crossover.java");
        }
        // put both matching and disjoint genes into one map as connection genes
        matchingGenes.putAll(disjointGenes);
        Genome offSpring = new Genome(new HashMap<>(matchingGenes), newGenomeID);
        
        offSpring.getLogger().logBirth(generation);
        offSpring.getLogger().logCrossover(generation, mother.getGenomeID(), father.getGenomeID());

        offSpring.setLastNodeID();

        if(offSpring.getConnections().isEmpty()){
            throw new NullPointerException("offSpring connections empty in runCrossover() in Crossover.java");
        }
        return offSpring;
    }

    private static Genome getFitterGenome(Genome mother, Genome father) {
        if (mother == null || father == null) {
            throw new IllegalArgumentException("Mother and father genomes cannot be null");
        }
        if(mother.getFitness() == father.getFitness()){
            return Math.random() < 0.5 ? mother : father;
        }
        return mother.getFitness() > father.getFitness() ? mother : father;
    }


    /*
     * Collect the matching genes between the two genomes. And return them as a HashMap.
     * @param father The father genome.
     * @param mother The mother genome.
     * @return A HashMap with the matching genes.
     */
    private static HashMap<Integer, ConnectionGene> getMatchingGenes(Genome father, Genome mother, Genome fitterParent, HashMap<NodeGene, NodeGene> representationNodesMap) {
        if (father == null || mother == null || representationNodesMap == null) {
            throw new IllegalArgumentException("Father, mother genomes and representationNodesMap cannot be null");
        }
        // The return Hashmap for the connection genes.
        HashMap<Integer, ConnectionGene> matchingMap = new HashMap<>();

        // Collect the matching genes between father and mother.
        // Collect all the keys of the connection genes of the father and mother in separate sets.
        Set<Integer> matching = new HashSet<>(father.getConnections().keySet());
        // retainAll() method retains only the elements in this set that are contained in both the specified collection and this set.
        matching.retainAll(mother.getConnections().keySet());

        
        /* The step before collects only the keys of the matching genes. Now we need to collect the matching genes (values).
         * A NodeGene has only one relevant Information, the ID. So it´s not relevant to choose between father or mother.
         * The innovation number is the same. So we can choose randomly between father and mother.
         * For a fast access, we use a HashMap with the innovation number as key and the connection gene as value. 
         * putIfAbsent() method is used to insert a new key-value pair into the map only if the key does not already exist in the map.
         */

        for (Integer key : matching) {
            ConnectionGene newConnection;
            NodeGene toNode = fitterParent.getConnections().get(key).getToNode();
            NodeGene fromNode = fitterParent.getConnections().get(key).getFromNode();
            if(toNode == null || fromNode == null){
                throw new NullPointerException("toNode or fromNode empty in getMatchingGenes() in Crossover.java");
            }
            NodeGene repFromNode = representationNodesMap.get(fromNode);
            NodeGene repToNode = representationNodesMap.get(toNode);
            if(repFromNode == null || repToNode == null){
                throw new NullPointerException("repFromNode or repToNode empty in getMatchingGenes() in Crossover.java");
            }
            newConnection = new ConnectionGene(
                key, 
                repFromNode, 
                repToNode, 
                Math.random() < 0.5 ? father.getConnections().get(key).getWeight() : mother.getConnections().get(key).getWeight(),
                true);
            matchingMap.put(key, newConnection);
        }

        return matchingMap;
    }

    /*
     * Collect the disjoint and excess genes between the two genomes. And return them as a HashMap.
     * @param matchingGenes The matching genes between the two genomes.
     * @param father The father genome.
     * @param mother The mother genome.
     * @return A HashMap with the disjoint and excess genes of the fitter parent.
     */
    private static HashMap<Integer, ConnectionGene> getDisjointGenes(HashMap<Integer, ConnectionGene> matchingGenes, Genome father, Genome mother, Genome fitterParent, HashMap<NodeGene, NodeGene> representationNodesMap) {
        if (matchingGenes == null || father == null || mother == null || fitterParent == null || representationNodesMap == null) {
            throw new IllegalArgumentException("Matching genes, father, mother genomes, fitter parent and representationNodesMap cannot be null");
        }
        // Collect the disjoint and excess genes between the two genomes.

        //Es macht keinen Sinn, die Eltern miteinander zu vergleichen. Auch wenn sich InnoNr oder NodeId gleich sind, werden es sehr
        //wahrscheinlich unterschiedliche Nodes sein. Deshalb wird hier nur der fitterParent verwendet.

        Set<Integer> disjoint = new HashSet<>(father.getConnections().keySet());
        disjoint.addAll(mother.getConnections().keySet());
        disjoint.removeAll(matchingGenes.keySet());
        disjoint.retainAll(fitterParent.getConnections().keySet());

        // In some cases there are no disjoint or excess genes. In this case we return an empty HashMap.
        if(disjoint.isEmpty()){
            return new HashMap<>();
        }
        /*
         * The step before collects only the keys of the disjoint and excess genes. Now we need to collect the disjoint and excess genes (values).
         * The main step is to deep copy the node genes. The node genes are the same in disjoint and excess genes but as a new object.
         */
        HashMap<Integer, ConnectionGene> disjointMap = new HashMap<>();
        for (Integer key : disjoint) {
            ConnectionGene gene = fitterParent.getConnections().get(key);
            NodeGene toNode = representationNodesMap.get(gene.getToNode());
            NodeGene fromNode = representationNodesMap.get(gene.getFromNode());
            if(toNode == null || fromNode == null){
                throw new NullPointerException("toNode or fromNode empty in getDisjointGenes() in Crossover.java");   }
            ConnectionGene newConnection = new ConnectionGene(key, fromNode, toNode, gene.getWeight(), true);
            disjointMap.put(newConnection.getInnovationNumber(), newConnection);  
        }

        return disjointMap;  
    }

    public static Genome aSexualCrossover(Genome genome, int newGenomeID, int generation){
        if(genome == null){
            throw new IllegalArgumentException("Genome cannot be null");
        }
        HashMap<NodeGene, NodeGene> representationNodesMap = representationNodesMap(genome.getInputNodes(), genome.getHiddenNodes(), genome.getOutputNodes());
        // return new Genome(connections);

        HashMap<Integer, ConnectionGene> connectionsHashMap = new HashMap<>();
        for (Integer key : genome.getConnections().keySet()) {
            ConnectionGene newConnection;
            NodeGene toNode = genome.getConnections().get(key).getToNode();
            NodeGene fromNode = genome.getConnections().get(key).getFromNode();
            if(toNode == null || fromNode == null){
                throw new NullPointerException("toNode or fromNode empty in getMatchingGenes() in Crossover.java");
            }
            NodeGene repFromNode = representationNodesMap.get(fromNode);
            NodeGene repToNode = representationNodesMap.get(toNode);
            if(repFromNode == null || repToNode == null){
                throw new NullPointerException("repFromNode or repToNode empty in getMatchingGenes() in Crossover.java");
            }
            newConnection = new ConnectionGene(
                key, 
                repFromNode, 
                repToNode, 
                Mutation.applyPerturbation(genome.getConnections().get(key).getWeight(), 0.1 ) ,
                true);
            connectionsHashMap.put(key, newConnection);
        }
        Genome offSpring = new Genome(connectionsHashMap, newGenomeID);
        offSpring.getLogger().logBirth(generation);

        offSpring.getLogger().logAsexualReproduction(generation, genome.getGenomeID());
        
        return offSpring;
    }

    private static HashMap<NodeGene, NodeGene> representationNodesMap(ArrayList<NodeGene> inputNodes, ArrayList<NodeGene> hiddenNodes, ArrayList<NodeGene> outputNodes){
        if (inputNodes == null || hiddenNodes == null || outputNodes == null) {
            throw new IllegalArgumentException("Input nodes, hidden nodes and output nodes cannot be null");
        }
        HashMap<NodeGene, NodeGene> representationNodesMap = new HashMap<>();
        for (NodeGene node : inputNodes) {
            representationNodesMap.put(node, new NodeGene(node.getId(), node.getLayer(), node.getActivationFunction() ));
        }
        for (NodeGene node : hiddenNodes) {
            NodeGene newNode = new NodeGene(node.getId(), node.getLayer(),node.getActivationFunction());
            newNode.setLayerIndex(node.getLayerIndex());
            representationNodesMap.put(node, newNode);
        }
        for (NodeGene node : outputNodes) {
            representationNodesMap.put(node, new NodeGene(node.getId(), node.getLayer(), node.getActivationFunction()));
        }
        return representationNodesMap;
    }
}
