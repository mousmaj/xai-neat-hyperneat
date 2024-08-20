package com.example.xai.Model.HyperNEAT;

import com.example.xai.Model.Genotype.Genome;
import com.example.xai.Model.Genotype.NodeGene;
import com.example.xai.Model.Genotype.ConnectionGene;

public class GenomeFromSubstrate {


    public static void createNew(Genome cppn, Genome substrate) {

        for(ConnectionGene connection : substrate.getConnections().values()) {
            NodeGene fromNode = connection.getFromNode();
            NodeGene toNode = connection.getToNode();

            double x1 = fromNode.getX();
            double x2 = toNode.getX();
            double y1 = fromNode.getY();
            double y2 = toNode.getY();

            cppn.setInputDimensions(x1, x2, y1, y2);   

            cppn.predict();
            double weight = cppn.getPrediction()[0];
            if(weight == 0) {
                continue;
            }
            // TODO: PROBEWEISE MIT ABS(WEIGHT)
            connection.setWeight(Math.abs(weight));
            //TODO: Prüfe den Schwellenwert
            //if(Math.abs(weight) < 0.2) {
            //    connection.disable();
            //} else {
            //    connection.enable();
            //}
        }

    }

}
