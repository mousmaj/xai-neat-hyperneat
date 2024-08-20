package com.example.xai.Model.NEAT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.OptionalInt;

import com.example.xai.Model.Genotype.NodeGene;

public class InnovationsTracker {

    private int innovationNumber = 0;
    private int nodeInnovationNumber = 0;
    private int genomeID = 0;
    private HashSet<ConnectionTripel> connectionInnovations = new HashSet<>();
    private HashMap<Integer, Integer> nodeIdsFromMutation = new HashMap<>();

    
    public class ConnectionTripel {
        public int fromId;
        public int toId;
        public int innovationNumber;

        public ConnectionTripel(int from, int to, int inno) {
            this.fromId = from;
            this.toId = to;
            this.innovationNumber = inno;
        }

        private int getFromId() {
            return fromId;
        }

        private int getToId() {
            return toId;
        }

        private int getInnovationNumber() {
            return innovationNumber;
        }
    }

    private int getNextInnovationNumber() {
        return innovationNumber++;
    }

    private int getNextNodeInnovationNumber() {
        return nodeInnovationNumber++;
    }

    public int getGenomeId() {
        return genomeID;
    }

    public int getNextGenomeID() {
        return genomeID++;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public void setNodeInnovationNumber(int nodeInnovationNumber) {
        this.nodeInnovationNumber = nodeInnovationNumber;
    }

    public void init(int inputSize, int outputSize) {
        // Structure is from Genome setup
        ArrayList<Integer> outputIds = new ArrayList<>();
        for (int j = 0; j < outputSize; j++) {
            outputIds.add(j);
            getNextNodeInnovationNumber();
        }
        for (int i = outputSize; i < inputSize + outputSize; i++) {
            for (int k = 0; k < outputSize; k++) {
                connectionInnovations.add(new ConnectionTripel(i, k, getNextInnovationNumber()));
            }
            getNextNodeInnovationNumber();
        }
    }

    public int setInnovationByNodes(NodeGene fromNode, NodeGene toNode){
        OptionalInt inno = connectionInnovations.stream()
            .filter(connection -> 
                connection.getFromId() == fromNode.getId() 
                && connection.getToId() == toNode.getId())
            .mapToInt(ConnectionTripel::getInnovationNumber)
            .findFirst();

        int innoNumber =  inno.orElse(getNextInnovationNumber());
        connectionInnovations.add(new ConnectionTripel(fromNode.getId(), toNode.getId(), innoNumber));
        return innoNumber;
    }

    public int setIdForeAddedNode(int innovationNumber){
        Integer value = nodeIdsFromMutation.get(innovationNumber);
        int id =  (value == null) ? getNextNodeInnovationNumber() : value.intValue();
        nodeIdsFromMutation.put(innovationNumber, id);
        return id;
    }

}
