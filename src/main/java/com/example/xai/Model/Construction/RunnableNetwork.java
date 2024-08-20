package com.example.xai.Model.Construction;


import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Phenotype.NeuralNetwork;

class RunnableneuralNetwork {

    public static void main(String[] args) {
        
        startNeat();

        //startHyperNeat();

    }

    private static void startNeat() {
        NEAT neat = new NEAT(10, 100);
        Genome bestOne = neat.training();
        //NeuralNetwork neuralNetwork = new NeuralNetwork(bestOne, NeuralNetwork.TYPE.PARTLY);
        //neat.test(bestOne);
    }

    private static void startHyperNeat() {
        HyperNEAT hyperNeat = new HyperNEAT(20,50);
        Genome bestOne = hyperNeat.training();
        //NeuralNetwork neuralNetwork = new NeuralNetwork(bestOne, TYPE.PARTLY);
    }

}
    
