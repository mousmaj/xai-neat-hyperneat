package com.example.xai.Model.Genotype;

import java.util.Random;

import com.example.xai.Model.ActivationFunctionCollection.ActivationEnum;
import com.example.xai.Model.ActivationFunctionCollection.ActivationFunctions;



/**
 * This class represents a node in a neural network.
 * It contains the id of the node and the layer it is in. (NodeGenes with layer input or output can only be on the layerindex 0.)
 * The layerIndex is used to determine the order of the nodes in the layer. It also contains the activation value,
 * which is the output of the node after the activation function is applied. 
 * 
 * x and y are used for HyperNEAT. They represent the coordinates of the node in the substrate.
 * 
 * 
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 10.07.2024
 * 
 */
public class NodeGene  {

    Layer layer;
    Random random = new Random();
    ActivationEnum activationFunction; 
    
    int id;
    int layerIndex = 0;
    double activationValue = 0;
    double bias = random.nextDouble() * 0.2 - 0.1;
    double x = 0;
    double y = 0;
    double layerSize = 0;

    /**
     * Constructor for the NodeGene used for NEAT.
     * The layerIndex and the activationValue is set to 0 by default.
     * 
     * @param id - is set by the InnovationManager.
     * @param layer
     * @param activationFunction
     */
    public NodeGene(int id , Layer layer, ActivationEnum activationFunction) {
        this.id = id;
        this.layer = layer;
        this.activationFunction = activationFunction;
    }

    /**
     * Constructor for the NodeGene used for HyperNEAT.
     * The layerIndex and the activationValue is set to 0 by default.
     * 
     * @param id - is set by the InnovationManager.
     * @param layer
     * @param activationFunction
     * @param x - should be normalized between 0 and 1.
     * @param y - should be normalized between 0 and 1.
     * 
     */
    public NodeGene(int id , Layer layer, ActivationEnum activationFunction, double x, double y) {
        this.id = id;
        this.layer = layer;
        this.activationFunction = activationFunction;
        this.x = x;
        this.y = y;
    }

    /**
     * Results in the id of the NodeGene. An id is unique in a genome. 
     * Over all genomes in a population, the id is not unique but referenced to a node with the same node-structure.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Results in the layer of the NodeGene. Possible layers are input, output and hidden.
     * @see Layer
     * 
     * @return layer
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Results in the layerIndex of the NodeGene. The layerIndex is used to determine the order of the nodes in the layer.
     * input and output nodes have the layerIndex 0, and by implemetation their are always on the layer 0.
     * 
     * @return layerIndex
     */
    public int getLayerIndex() {
        return layerIndex;
    }

    /**
     * Sets the layerIndex of the NodeGene. The layerIndex is used to determine the order of the nodes in the layer.
     * input and output nodes have the layerIndex 0, and by implemetation their are always on the layer 0.
     * Throws an iLlegalArgumentException when an attempt is made to set the layerIndex of an input or output node to a value other than 0.
     * 
     * @param layerIndex
     * @throws IllegalArgumentException - when an attempt is made to set the layerIndex of an input or output node to a value other than 0.
     */
    public void setLayerIndex(int layerIndex) {
        if(this.layer == Layer.INPUT || this.layer == Layer.OUTPUT) {
            if(layerIndex != 0) {
                throw new IllegalArgumentException("Input and Output nodes must have the layerIndex 0.");
            }
        }
        this.layerIndex = layerIndex;
    }

    /**
     * Provides the activation value of the NodeGene. 
     * The activation value is the output of the node after the activation function is applied by the activation method. 
     * 
     * @return activationValue as double value between 0 and 1.
     * @see ActivationFunctions
     * @see Genome
     */
    public double getActivationValue() {
        return activationValue;
    }

    /**
     * Sets the activation value of the NodeGene. Normally used in combination with the activation method.
     * The activation value is the output of the node after the activation function is applied by the activation method. 
     * 
     * @param activationValue as double value between 0 and 1.
     * @see ActivationFunctions
     * @see Genome
     */
    public void setActivationValue(double activationValue) {
        this.activationValue = activationValue;
    }

    /**
     * Activates the NodeGene with the given activation function and the sum of the input-weights.
     * Formula: z= ∑ (w_i * x_i) + b
     * w is the weight of the connection, x is the input value and b is the bias of the node.
     * z is the sum of the input values.
     *
     * @param funct
     * @param inputs
     * @return activationValue 
     * 
     * @see ActivationFunctions - for the calculation of the activation value
     * @see Genome - for the calculation of the input values
     */
    public double activate(ActivationEnum funct, double[] inputs) {
        double sum = 0;
        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i];
        }
        //TODO: VORSICHT NUR ZUM TESTEN
        //(value - min) / (max - min)
        //sum = (sum - (inputs.length*-1)) / (inputs.length - (inputs.length*-1));
        // a = σ(z)
        ActivationFunctions activationFunction = new ActivationFunctions();
        return activationFunction.applyActivationFunction(funct, (sum));
    }

    /**
     * Provides the activation function of the NodeGene. In Neat the activation function is never changed.
     * For HyperNEAT the activation function can be changed.
     * The activation function is used to calculate the activation value of the node.
     * 
     * @return activationFunction
     */
    public ActivationEnum getActivationFunction() {
        return activationFunction;
    }

    /**
     * Sets the activation function of the NodeGene. In Neat the activation function is never changed.
     * For HyperNEAT the activation function can be changed.
     * The activation function is used to calculate the activation value of the node.
     * 
     * @param activationFunction - ActivationEnum
     * 
     * @see ActivationEnum
     * @see ActivationFunctions
     */
    public void setActivationFunction(ActivationEnum activationFunction) {
        this.activationFunction = activationFunction;
    }

    /**
     * Provides the x-coordinate of the NodeGene. The x-coordinate is used for HyperNEAT.
     * The x-coordinate should be normalized between 0 and 1.
     * 
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Provides the y-coordinate of the NodeGene. The y-coordinate is used for HyperNEAT.
     * The y-coordinate should be normalized between 0 and 1.
     * 
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the NodeGene. The x-coordinate is used for HyperNEAT.
     * The x-coordinate should be normalized between 0 and 1.
     * 
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the NodeGene. The y-coordinate is used for HyperNEAT.
     * The y-coordinate should be normalized between 0 and 1.
     * 
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    public double getBias() {
        return bias;
    }

    /**
     * Sets the bias of the NodeGene. The bias is used to shift the activation function.
     * The mutation method of the genome can change the bias too.
     * 
     * @param bias
     * 
     */
    public void setBias(double bias) {
        this.bias = bias;
    }


}