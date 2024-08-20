package com.example.xai.Model.Genotype;

/**
 * This class represents a connection between two nodes in a neural network.
 * It contains the weighting of the connection, as well as the two nodes that are connected.
 * Connections can be activated or deactivated.
 * 
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 10.07.2024
 * 
 */

public class ConnectionGene {
    
    private int innovationNumber;
    private double weight;
    private NodeGene fromNode;
    private NodeGene toNode;
    private boolean enabled;

    /**
     * Constructor for the ConnectionGene.
     * @param innovationNr - don't use this parameter manualy, it is set by the InnovationManager.
     * @param fromNode
     * @param toNode
     * @param weight
     * @param enabled
     */
    public ConnectionGene(int innovationNr, NodeGene fromNode, NodeGene toNode, double weight, boolean enabled) {
        this.innovationNumber = innovationNr;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
        this.enabled = enabled;
    }

    /**
     * Disables the connection.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Enables the connection.
     */
    public void enable() {
        enabled = true;
    }

    /**
     * Returns whether the connection is enabled or not.
     * 
     * @return true if the connection is enabled, false if it is disabled. 
     * */ 
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the node from which the connection originates.
     * @return origin NodeGene
     */
    public NodeGene getFromNode() {
        return this.fromNode;
    }

    /**
     * Returns the node to which the connection leads.
     * @return sink NodeGene
     */
    public NodeGene getToNode() {
        return toNode;
    }

    /**
     * Returns the weight of the connection.
     * @return weight of the connection
     */
    public double getWeight() {
        return weight;
    }


    public void setToNode(NodeGene fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * Sets the origin node of the connection.
     * 
     * @param toNode
     */
    public void setFromNode(NodeGene toNode) {
        this.toNode = toNode;
    }

    /**
     * Returns the innovation number of the connection.
     * The innovation number is a unique identifier for the connection.
     * 
     * @return innovation number of the connection.
     */
    public int getInnovationNumber() {
        return innovationNumber;
    }

    /**
     * Sets the innovation number of the connection.
     * Be aware that this method is not intended to be used by the user manualy.
     * The assignment of the innovation number is done by the InnovationManager.
     * 
     * @param innovationNumber
     */
    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    /**
     * Sets the weight of the connection.
     * 
     * @param weight new weight of the connection
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
}
