package com.example.xai.Model.NEAT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.xai.Model.ActivationFunctionCollection.ActivationEnum;
import com.example.xai.Model.ActivationFunctionCollection.ActivationFunctions;
import com.example.xai.Model.Genotype.ConnectionGene;
import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.Layer;
import com.example.xai.Model.Genotype.NodeGene;


public class Mutation {

    private static final Random RANDOM = new Random();

    public static void mutateWeights(Genome genome, double mutationRate, double perturbationRange, int generationNumber) {
        if (Math.random() < mutationRate) {
            HashMap<Integer, ConnectionGene> connections = genome.getConnections();
            int newValues = 0;
            int perturbedValues = 0;
    
            for (Map.Entry<Integer, ConnectionGene> entry : connections.entrySet()) {
                double randomValue = Math.random();
                ConnectionGene connection = entry.getValue();
                if(connection.isEnabled() == false) {
                    continue;
                }
    
                if (randomValue > 0.1) {
                    connection.setWeight(applyPerturbation(connection.getWeight(), perturbationRange));
                    perturbedValues++;
                } else {
                    connection.setWeight(RANDOM.nextDouble(-1, 1));
                    newValues++;
                }
            }
    
            // Log the mutation details
            genome.getLogger().logMutatetWeights(generationNumber, perturbedValues + newValues, newValues);
        }
    }
    

    public static double applyPerturbation(double value, double perturbation) {
        double perturbationAmount = value * perturbation;
        // Zufällige Entscheidung, ob die Perturbation addiert oder subtrahiert werden soll
        return RANDOM.nextBoolean() ? value + perturbationAmount : value - perturbationAmount;
    }
    


    public static void addNode(Genome genome, InnovationsTracker innovationsTracker, int  generation) {
        checkGenomeConnections(genome);
        
        ConnectionGene oldConnection = getRandomConnection(genome);
        checkConnectionNodes(oldConnection);
    
        NodeGene oldToNodeGene = oldConnection.getToNode();
        NodeGene oldFromNodeGene = oldConnection.getFromNode();

        checkOldNodes(oldToNodeGene, oldFromNodeGene);
        NodeGene newNode = genome.isCPPN()
            ? new NodeGene(innovationsTracker.setIdForeAddedNode(oldConnection.getInnovationNumber()), Layer.HIDDEN, ActivationFunctions.getRandomActivationEnum())
            : new NodeGene(innovationsTracker.setIdForeAddedNode(oldConnection.getInnovationNumber()), Layer.HIDDEN, ActivationEnum.SIGMOID);
        

    
        ConnectionGene firstConnection = new ConnectionGene(innovationsTracker.setInnovationByNodes(oldFromNodeGene, newNode), oldFromNodeGene, newNode, 1, true);
        ConnectionGene secondConnection = new ConnectionGene(innovationsTracker.setInnovationByNodes(newNode, oldToNodeGene), newNode, oldToNodeGene, oldConnection.getWeight(), true);
    
        genome.getLogger().logNewNodeAdded(generation, newNode.getId(), oldFromNodeGene.getId(), oldToNodeGene.getId());

        if (oldToNodeGene.getLayer() == Layer.HIDDEN) {
            newNode.setLayerIndex(oldToNodeGene.getLayerIndex());
            oldToNodeGene.setLayerIndex(oldToNodeGene.getLayerIndex() + 1);
        } else if(oldFromNodeGene.getLayer() == Layer.HIDDEN) {
            newNode.setLayerIndex(oldFromNodeGene.getLayerIndex() + 1);
        }

        genome.addHiddenNode(newNode);
        genome.addConnectionGene(firstConnection);
        genome.addConnectionGene(secondConnection);
        oldConnection.disable();

    }
    
    private static void checkGenomeConnections(Genome genome) {
        if (genome.getConnections().isEmpty()) {
            throw new IllegalArgumentException("Genome connections cannot be empty");
        }
    }
    
    private static ConnectionGene getRandomConnection(Genome genome) {
        List<ConnectionGene> connections = new ArrayList<>(genome.getConnections().values());
        return connections.get(RANDOM.nextInt(connections.size()));
    }
    
    
    private static void checkConnectionNodes(ConnectionGene connection) {
        if (connection.getFromNode() == null || connection.getToNode() == null) {
            throw new IllegalArgumentException("Connection nodes cannot be null");
        }
    }
    
    private static void checkOldNodes(NodeGene oldToNodeGene, NodeGene oldFromNodeGene) {
        if (oldToNodeGene == null || oldFromNodeGene == null) {
            throw new IllegalArgumentException("Old nodes cannot be null");
        }
    }

    public static void addConnection(Genome genome, InnovationsTracker innovationsTracker, int generation) {
        Set<PossibleConnection> possibleConnections = gatherPossibleConnections(genome);
        List<PossibleConnection> connectionsList = new ArrayList<>(possibleConnections);
        int n = connectionsList.size();
        if(!(n == 0)){
        // Create one random connection from the possible connections
        int connectioToCreate = RANDOM.nextInt(n);
        PossibleConnection possibleConnection = connectionsList.get(connectioToCreate);
        establishConnection(possibleConnection.getFromNode(), possibleConnection.getToNode(), innovationsTracker, genome);
        genome.getLogger().logNewConnectionAdded(generation, possibleConnection.getFromNode().getId(), possibleConnection.getToNode().getId());
        }
    }
    

    private static class PossibleConnection {
        private final NodeGene fromNode;
        private final NodeGene toNode;


        public PossibleConnection(NodeGene fromNode, NodeGene toNode) {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

        public NodeGene getFromNode() {
            return fromNode;
        }

        public NodeGene getToNode() {
            return toNode;
        }

    }

    private static Set<PossibleConnection> gatherPossibleConnections(Genome genome) {
        Set<PossibleConnection> possibleConnections = new HashSet<>();

        // Bestehende Verbindungen sammeln
        Set<String> existingConnections = genome.getConnections().values().stream()
                .map(conn -> conn.getFromNode().getId() + "-" + conn.getToNode().getId())
                .collect(Collectors.toSet());

        
        ArrayList<NodeGene> shuffelInputList = new ArrayList<>(genome.getInputNodes());
        Collections.shuffle(shuffelInputList);
        ArrayList<NodeGene> shuffelHiddenList = new ArrayList<>(genome.getHiddenNodes());
        Collections.shuffle(shuffelHiddenList);
        ArrayList<NodeGene> shuffelOutputList = new ArrayList<>(genome.getOutputNodes());
        Collections.shuffle(shuffelOutputList);

        // Input zu Hidden
        for (NodeGene inputNode : shuffelInputList) {
            boolean connectionAdded = false;
            for (NodeGene hiddenNode : shuffelHiddenList) {
                String connectionKey = inputNode.getId() + "-" + hiddenNode.getId();
                if (!existingConnections.contains(connectionKey)) {
                    possibleConnections.add(new PossibleConnection(inputNode, hiddenNode));
                    connectionAdded = true;
                    break; // Break the inner loop once a connection is added
                }
            }
            if (connectionAdded) break; // Break the outer loop once a connection is added
        }
        

        // Hidden zu Hidden
        for (NodeGene fromHiddenNode : shuffelHiddenList) {
            boolean connectionAdded = false;
            for (NodeGene toHiddenNode : shuffelHiddenList) {
                if (fromHiddenNode.getId() != toHiddenNode.getId() && fromHiddenNode.getLayerIndex() < toHiddenNode.getLayerIndex()) {
                    String connectionKey = fromHiddenNode.getId() + "-" + toHiddenNode.getId();
                    if (!existingConnections.contains(connectionKey)) {
                        possibleConnections.add(new PossibleConnection(fromHiddenNode, toHiddenNode));
                        connectionAdded = true;
                        break; // Break the inner loop once a connection is added
                    }
                }
            }
            if (connectionAdded) break; // Break the outer loop once a connection is added
        }

        // Hidden zu Output
        for (NodeGene hiddenNode : shuffelHiddenList) {
            boolean connectionAdded = false;
            for (NodeGene outputNode : shuffelOutputList) {
                String connectionKey = hiddenNode.getId() + "-" + outputNode.getId();
                if (!existingConnections.contains(connectionKey)) {
                    possibleConnections.add(new PossibleConnection(hiddenNode, outputNode));
                    connectionAdded = true;
                    break; // Break the inner loop once a connection is added
                }
            }
            if (connectionAdded) break; // Break the outer loop once a connection is added
        }
        return possibleConnections;
    }


    // Support-Method for creating the connection
    private static boolean establishConnection(NodeGene fromNode, NodeGene toNode, InnovationsTracker innovationsTracker, Genome genome) {
        ConnectionGene newConnection = new ConnectionGene(
                innovationsTracker.setInnovationByNodes(fromNode, toNode),
                fromNode,
                toNode,
                RANDOM.nextDouble(-1, 1),
                true);

        if (newConnection.getFromNode() == null || newConnection.getToNode() == null) {
            throw new NullPointerException("fromNode or toNode is null in establishConnection() in Mutation.java");
        }
        genome.addConnectionGene(newConnection);

        return true;
    }

}
