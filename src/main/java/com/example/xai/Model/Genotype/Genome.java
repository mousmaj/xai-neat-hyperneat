package com.example.xai.Model.Genotype;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.xai.Model.ActivationFunctionCollection.ActivationEnum;
import com.example.xai.Model.Genotype.ConnectionGene;
import com.example.xai.Model.Genotype.NodeGene;
import com.example.xai.Model.Construction.GenomeLogger;
import com.example.xai.Model.Genotype.Layer;



/**
 * This class represents a genome in the NEAT and HyperNeat algorithms.
 * It contains the connections between the nodes and the nodes itself. Overall the main structure of the neural network.
 * The genome also contains the fitness value of the genome and the logic to predict the output of the neural network.
 * 
 * 
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 10.07.2024
 * 
 */
public class Genome {

    private HashMap<Integer, ConnectionGene> connections;
    private ArrayList<NodeGene> resultNodes;
    private ArrayList<NodeGene> inputNodes;
    private ArrayList<NodeGene> hiddenNodes;
    private int genomeID;
    private int counter = 0;
    private int innovationNumber;
    private double fitness = 0;
    private boolean isPhenotype = false;
    private boolean isCPPN = false;
    Random RANDOM = new Random();
    private GenomeLogger  logger;
    private Genome phenoGenome;


    /**
     * Constructor for the Genome called from the Crossover Class. Dont use this constructor manually.
     * Its rebuilds the genome from the connections.
     * inputNodes, hiddenNodes and resultNodes are filled with the nodes which are given in the connections.
     * The construxtor calls the fillNodeLists() method which checks if the nodes are null or already given.
     * 
     * @param connections - created in the Crossover class
     * 
     */
    public Genome(HashMap<Integer, ConnectionGene> connections, int genomeID) {
        this.connections = connections;
        this.resultNodes = new ArrayList<>();
        this.inputNodes = new ArrayList<>();
        this.hiddenNodes = new ArrayList<>();
        this.genomeID = genomeID;
        this.logger = new GenomeLogger();
        fillNodeLists(connections);
    }

    /**
     * Constructor for the Genome for copying the genome.
     * Only sets the inputNodes, hiddenNodes, outputNodes and connections for the genome.
     * 
     * @param inputNodes
     * @param hiddenNodes
     * @param outputNodes
     * @param connections
     * 
     */
    public Genome(ArrayList<NodeGene> inputNodes, ArrayList<NodeGene> hiddenNodes, ArrayList<NodeGene> outputNodes,
            HashMap<Integer, ConnectionGene> connections, int genomeID) {
        this.resultNodes = outputNodes;
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.counter = inputNodes.size() + hiddenNodes.size() + outputNodes.size();
        this.genomeID = genomeID;
        this.connections = connections;
        this.logger = new GenomeLogger();
    }

    /**
     * Constructor for the Genome called from the Population in NEAT and HyperNEAT.
     * For creating the initial genomes (NEAT) and the CPPN (HyperNEAT).
     * 
     * @see NEAT
     * @see Population
     * 
     */
    public Genome(int inputPixels, int outputCount, int genomeID) {
        this.counter = 0;
        this.connections = new HashMap<>();
        this.resultNodes = new ArrayList<>();
        this.inputNodes = new ArrayList<>();
        this.hiddenNodes = new ArrayList<>();
        this.genomeID = genomeID;
        this.logger = new GenomeLogger();

        int innovationNumber = 0;
        createLayerNodes(Layer.INPUT, inputPixels, null);
        createLayerNodes(Layer.OUTPUT, outputCount, ActivationEnum.SIGMOID);

        // Create connections between input and output nodes
        for (NodeGene inputNode : inputNodes) {
            for (NodeGene resultNode : resultNodes) {
                double weight = RANDOM.nextDouble() * 0.2 - 0.1;
                ConnectionGene newConnection = new ConnectionGene(innovationNumber++, inputNode, resultNode, weight, true);
                connections.put(newConnection.getInnovationNumber(), newConnection);
            }
        }
    }

    /**
     * Constructor for a phenotyp of the CPPN. The output nodes have the identity function as activation function.
     * The Network has to be as simple as possible. Bias is not used, because to much perturbation.
     * 
     * @param inputPixels
     * @param outputCount
     * @param phenotyp - true for the phenotyp of the CPPN
     * 
     * @see HyperNEAT
     * @see Generation
     */
    public Genome(int inputPixels, int outputCount, boolean phenotyp) {
        this.counter = 0;
        this.connections = new HashMap<>();
        this.resultNodes = new ArrayList<>();
        this.inputNodes = new ArrayList<>();
        this.hiddenNodes = new ArrayList<>();
        this.isPhenotype = phenotyp;



        int innovationNumber = 0;
        double[][] inputCoordinates = calculateNormalizedCoordinates(inputPixels);
        createLayerNodes(Layer.INPUT, inputPixels, null);
        setCoordinates(inputCoordinates, inputNodes);

        double[][] outputCoordinates = calculateNormalizedCoordinates(outputCount);
        if(phenotyp) {
            createLayerNodes(Layer.OUTPUT, outputCount, ActivationEnum.IDENTITIY);
        } else {
        createLayerNodes(Layer.OUTPUT, outputCount, ActivationEnum.TANH);
        }
        setCoordinates(outputCoordinates, resultNodes);

        // Create connections between input and output nodes
        for (NodeGene inputNode : inputNodes) {
            for (NodeGene resultNode : resultNodes) {
                double weight = 0;
                if(!phenotyp) {
                    weight = RANDOM.nextDouble() * 0.2 - 0.1;
                }
                ConnectionGene newConnection = new ConnectionGene(innovationNumber++, inputNode, resultNode, weight, true);
                connections.put(newConnection.getInnovationNumber(), newConnection);
            }
        }
    }

    

    /**
     * Assuming for the HyperNEAT algorithm. Represents the Phenotype of the CPPN (and its substrate).
     * Set the position of each node which it will be have on the substrate-grid.
     * 
     * @param inputPixels
     * @param hiddenCount
     * @param outputCount
     * 
     */
    public Genome(int inputPixels, int hiddenCount, int outputCount, int genomeID) {
        this.counter = 0;
        this.connections = new HashMap<>();
        this.resultNodes = new ArrayList<>();
        this.inputNodes = new ArrayList<>();
        this.hiddenNodes = new ArrayList<>();
        this.genomeID = genomeID;
        this.logger = new GenomeLogger();

        this.innovationNumber = 0;

        // Creates input nodes and sets their coordinates
        double[][] inputCoordinates = calculateNormalizedCoordinates(inputPixels);
        createLayerNodes(Layer.INPUT, inputPixels, null);
        setCoordinates(inputCoordinates, inputNodes);

        // Creates hidden nodes and sets their coordinates
        double[][] hiddenCoordinates = calculateNormalizedCoordinates(hiddenCount);
        createLayerNodes(Layer.HIDDEN, hiddenCount, ActivationEnum.TANH);
        setCoordinates(hiddenCoordinates, hiddenNodes);

        // Creates output nodes and sets their coordinates
        double[][] outputCoordinates = calculateNormalizedCoordinates(outputCount);
        createLayerNodes(Layer.OUTPUT, outputCount, ActivationEnum.TANH);
        setCoordinates(outputCoordinates, resultNodes);

        // Fully connect input and hidden nodes
        fullyConnectLayers(inputNodes, hiddenNodes);

        // Fully connect hidden and output nodes
        fullyConnectLayers(hiddenNodes, resultNodes);

    }

    /**
     * Creates a amount of nodes for a given layer. The nodes will be added to the respective list.
     * 
     * @param layer
     * @param numberOfNodes
     * @param activationFunction
     * 
     */
    private void createLayerNodes(Layer layer, int numberOfNodes, ActivationEnum activationFunction) {
        for (int i = 0; i < numberOfNodes; i++) {
            if (layer == Layer.INPUT) {
                inputNodes.add(new NodeGene(counter++, layer, activationFunction));
            } else if (layer == Layer.HIDDEN) {
                hiddenNodes.add(new NodeGene(counter++, layer, activationFunction));
            } else if (layer == Layer.OUTPUT) {
                resultNodes.add(new NodeGene(counter++, layer, activationFunction));
            }
        }
    }


    /**
     * Fully connects two layers of nodes. Each node of the first layer will be connected to each node of the second layer.
     * The weight of the connection is a random value between -1 and 1.
     * Directed connections fromNodes -> toNodes.
     * 
     * @param fromNodes
     * @param toNodes
     * 
     */
    private void fullyConnectLayers(ArrayList<NodeGene> fromNodes, ArrayList<NodeGene> toNodes) {
        
        for (NodeGene fromNode : fromNodes) {
            for (NodeGene toNode : toNodes) {
                double weight = RANDOM.nextDouble() * 0.2 - 0.1;
                ConnectionGene newConnection = new ConnectionGene(this.innovationNumber++, fromNode, toNode, weight, true);
                connections.put(newConnection.getInnovationNumber(), newConnection);
            }
        }
    }


    /**
     * Sets the coordinates of the nodes in a layer. The coordinates are normalized between -1 and 1.
     * 
     * @param coordinates 
     * @param nodes
     * 
     */
    private void setCoordinates(double[][] coordinates, ArrayList<NodeGene> nodes) {
        if (coordinates.length != nodes.size()){
            throw new IllegalArgumentException("Coordinates and nodes must have the same size");
        }
    
        int count = coordinates.length;
        double gridSize = Math.sqrt(count);
        double halfGridSize = gridSize / 2.0;
    
        for (int i = 0; i < nodes.size(); i++) {
            double normalizedX = normalizeCoordinate(coordinates[i][0], halfGridSize);
            double normalizedY = normalizeCoordinate(coordinates[i][1], halfGridSize);
            nodes.get(i).setX(normalizedX);
            nodes.get(i).setY(normalizedY);
        }
    }
    
    /**
     * Normalizes the coordinate value between -1 and 1. halfGridSize is the half of the grid size and the maximum possible value.
     * 
     * @param value
     * @param halfGridSize
     * @return normalized value
     */
    private double normalizeCoordinate(double value, double halfGridSize) {
        if (value == 0) {
            return (value > 0) ? 1 : -1; // Sicherstellen, dass 0 vermieden wird
        }
        return value / halfGridSize;
    }
    

    /**
     * Calculates the distribution of the nodes on a 2D grid. The nodes are placed in a square grid.
     * Only quadratic grid sizes are supported. The number of nodes must be a square number and the grid size must be an even number bigger than 4
     * 
     * @param count - Anzahl der benötigten Punkte (muss gerade sein)
     * @return coordinates - Ein Array von 2D-Koordinaten mit Werten zwischen -1 und 1.
     * 
     */
    private double[][] calculateNormalizedCoordinates(int count) {
        double[][] coordinates = new double[count][2];
        int gridSize = (int) Math.sqrt(count);
    
        int startX = -gridSize / 2;
        int startY = -gridSize / 2;
    
        int nodeIndex = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int x = startX + j;
                int y = startY + i;
                
                // Avoid 0 for x and y
                if (x >= 0) x = x + 1;
                if (y >= 0) y = y + 1;
    
                coordinates[nodeIndex][0] = x;
                coordinates[nodeIndex][1] = y;
                nodeIndex++;
            }
        }
        return coordinates;
    }


    /**
     * Predicts the output of the neural network. The activation values of the nodes are calculated layer by layer.
     * The hidden nodes are processed first and then the output nodes.
     * Their is no need to activate the input nodes, because they are set manually (pixels).
     * 
     */ 
public void predict() {

    // Filter hiddenNodes and connections once
    NodeGene[] allHiddenNodes = this.hiddenNodes.stream()
        .sorted(Comparator.comparing(NodeGene::getLayerIndex))
        .toArray(NodeGene[]::new);

    Map<NodeGene, ConnectionGene[]> nodeToConnectionsMap = connections.values().stream()
        .filter(ConnectionGene::isEnabled)
        .collect(Collectors.groupingBy(ConnectionGene::getToNode, Collectors.toList()))
        .entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().toArray(new ConnectionGene[0])));

    // Process hidden nodes layer by layer
    for (NodeGene node : allHiddenNodes) {
        ConnectionGene[] connections = nodeToConnectionsMap.get(node);
        if (connections != null) {
            double[] weights = new double[connections.length];
            for (int i = 0; i < connections.length; i++) {
                ConnectionGene con = connections[i];
                weights[i] = con.getWeight() * con.getFromNode().getActivationValue()
                            + (this.isPhenotype ? 0 : con.getFromNode().getBias());
            }


            node.setActivationValue(node.activate(node.getActivationFunction(), weights));
        }
    }

    // Process output nodes
    for (NodeGene outputNode : resultNodes) {
        ConnectionGene[] connections = nodeToConnectionsMap.get(outputNode);
        if (connections != null) {
            double[] weights = new double[connections.length];
            for (int i = 0; i < connections.length; i++) {
                ConnectionGene con = connections[i];
                weights[i] = con.getWeight() * con.getFromNode().getActivationValue()
                            + (this.isPhenotype ? 0 : con.getFromNode().getBias());
            }


            outputNode.setActivationValue(outputNode.activate(outputNode.getActivationFunction(), weights));
        }
    }

}



    /**
     * Sets the input values of the neural network. The input values are the pixel (greyscale) values of an image.
     * The input values are between 0 and 1. 
     * 
     * @param inputPixels 
     * @see Generation
     * 
     */
    public void setInputImage(float[][] inputPixels) {
        int height = inputPixels.length;
        int width = inputPixels[0].length;
        int totalPixels = height * width;

        int index = 0;
        for (int row = height - 1; row >= 0; row--) {
            for (int col = 0; col < width; col++) {
                try {
                    inputNodes.get(index).setActivationValue(inputPixels[row][col]);
                    index++;
                } catch (Exception e) {

                }
            }
        }
    }


    
    /** 
    * Sets the input values of the cppn. The input values are positions of the nodes in the substrate.
    * The input values are normalized between -1 and 1.
    *
    * @param x1 - x-coordinate of the first point
    * @param x2 - x-coordinate of the second point
    * @param y1 - y-coordinate of the first point
    * @param y2 - y-coordinate of the second point
    *
    * @see HyperNEAT
    * @see GenomeFromSubstrate
    *
    */
    public void setInputDimensions(double x1, double x2, double y1, double y2) {
        // 8 is the number of input nodes and is hardcoded.
        inputNodes.get(0).setActivationValue(x1);
        inputNodes.get(1).setActivationValue(x2);
        inputNodes.get(2).setActivationValue(y1);
        inputNodes.get(3).setActivationValue(y2);
    }


    public boolean answerCorrect(double[] actual) {
        double[] predicted = getPrediction();
        int actualIndex = 0;
        // get index of highest prediction
        int posHighest = 0;
        for (int j = 0; j < predicted.length; j++) {
            if (predicted[j] > predicted[posHighest]) {
                posHighest = j;
            }
        }
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] == 1) {
                actualIndex = i;
                break;
            }
        }
        if (posHighest == actualIndex) {
            return true;
        }
        return false;
    }

    public double[] getPrediction() {
        double[] results = new double[resultNodes.size()];
        int i = 0;
        for (NodeGene node : resultNodes) {
            results[i] = node.getActivationValue();
            i++;
        }
        return results;
    }

    public HashMap<Integer, ConnectionGene> getConnections() {
        return connections;
    }

    public ArrayList<NodeGene> getInputNodes() {
        return inputNodes;
    }

    public ArrayList<NodeGene> getHiddenNodes() {
        return hiddenNodes;
    }

    public ArrayList<NodeGene> getOutputNodes() {
        return resultNodes;
    }

    public Genome getGenome() {
        return this;
    }

    public int getLastNodeID() {
        return counter;
    }

    public int nextNodeID() {
        return ++counter;
    }

    public void addOneToLastNodeID() {
        counter++;
    }

    public void addConnection(ConnectionGene connection) {
        connections.put(connection.getInnovationNumber(), connection);
    }

    public double getFitness() {
        return this.fitness;
    }

    public void addFitness(double fitness) {
        this.fitness = this.fitness + fitness;
    }

    public void overrideFitness(double fitness) {
        this.fitness = fitness;
    }

    public void clearFitness() {
        this.fitness = 0;
    }

    public void setLastNodeID() {
        this.counter = this.inputNodes.size() + this.hiddenNodes.size() + this.resultNodes.size();
        ;
    }

    public NodeGene getHiddenNodeById(int id) {
        for (NodeGene node : hiddenNodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    public NodeGene getOutputNodeById(int id) {
        for (NodeGene node : resultNodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }


    /*
     * Wird im Crossover aufgerufen.
     */
    public void fillNodeLists(HashMap<Integer, ConnectionGene> map) {
        Set<NodeGene> inputs = new HashSet<>();
        Set<NodeGene> hidden = new HashSet<>();
        Set<NodeGene> outputs = new HashSet<>();

        for (Entry<Integer, ConnectionGene> connection : map.entrySet()) {
            NodeGene toNode = connection.getValue().getToNode();
            NodeGene FromNode = connection.getValue().getFromNode();
            // Add the nodes to the respective set
            if (toNode == null || FromNode == null) {
                throw new NullPointerException("Null nodes in fillNodeLists() in Genome.java");
            }
            if (toNode.getLayer() == Layer.INPUT) {
                inputs.add(toNode);
            } else if (toNode.getLayer() == Layer.HIDDEN) {
                hidden.add(toNode);
            } else if (toNode.getLayer() == Layer.OUTPUT) {
                outputs.add(toNode);
            }

            if (FromNode.getLayer() == Layer.INPUT) {
                inputs.add(FromNode);
            } else if (FromNode.getLayer() == Layer.HIDDEN) {
                hidden.add(FromNode);
            } else if (FromNode.getLayer() == Layer.OUTPUT) {
                outputs.add(FromNode);
            }

        }
        this.inputNodes = new ArrayList<>(inputs);
        this.hiddenNodes = new ArrayList<>(hidden);
        this.resultNodes = new ArrayList<>(outputs);
        // check if their are null nodes in the lists
        if (inputNodes.contains(null) || hiddenNodes.contains(null) || resultNodes.contains(null)) {
            throw new NullPointerException("Null nodes in the lists of fillNodeLists() in Genome.java");
        }
        if (connections.isEmpty()) {
            throw new NullPointerException("No connections in fillNodeLists() in Genome.java");
        }
    }

    public ConnectionGene addConnectionGene(ConnectionGene connection) {
        if (connections.containsKey(connection.getInnovationNumber())) {
            return connections.get(connection.getInnovationNumber());
        }
        connections.put(connection.getInnovationNumber(), connection);
        return connection;
    }

    public void addHiddenNode(NodeGene node) {
        hiddenNodes.add(node);
    }

    public void setIsCPPN() {
        this.isCPPN = true;
    }

    public boolean isCPPN() {
        return this.isCPPN;
    }

    public int getGenomeID() {
        return genomeID;
    }

    public GenomeLogger getLogger() {
        return logger;
    }

    /**
     * Only for the HyperNEAT algorithm. Sets the phenoGenome of the CPPN.
     * @param genome
     */
    public void setPhenoGenome(Genome genome){
        this.phenoGenome = genome;
    }

    /**
     * Returns the Phenotype of the CPPN. The Phenotype is the neural network which is created by the CPPN. 
     * 
     * @return phenoGenome
     */
    public Genome getPhenoGenome(){
        return this.phenoGenome;
    }

    public HashMap<Integer, ConnectionGene> getActiveConnections() {
        HashMap<Integer, ConnectionGene> activeConnections = new HashMap<>();
        for (ConnectionGene connection : connections.values()) {
            if (connection.isEnabled()) {
                activeConnections.put(connection.getInnovationNumber(), connection);
            }
        }
        return activeConnections;
    }
}
