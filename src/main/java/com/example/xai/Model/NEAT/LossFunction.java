package com.example.xai.Model.NEAT;

// QUELLE RAUSSUCHEN!!

public class LossFunction {
    
    public static double crossEntropy(double[] predicted, double[] actual) {
        double epsilon = 1e-15; // Verhindert den Logarithmus von 0
        double crossEntropy = 0.0;
        for (int i = 0; i < predicted.length; i++) {
            // Begrenzt die vorhergesagten Werte, um extrem kleine oder große Werte zu vermeiden
            double prediction = Math.min(Math.max(predicted[i], epsilon), 1.0 - epsilon);

            if (actual[i] == 1.0) {
                crossEntropy += -Math.log(prediction);
            } else { // In diesem Kontext entspricht actual[i] 0
                crossEntropy += -Math.log(1 - prediction);
            }
        }

        return crossEntropy;
    }
    
    private static double categoricalCrossEntropy(double[] predicted, double[] actual) {
        double sum = 0;
        for (int i = 0; i < predicted.length; i++) {
            sum += actual[i] * Math.log(predicted[i]);
        }
        return -sum;
    }
    
    public static double categoricalCrossEntropy(double[][] predicted, double[][] actual) {
        double sum = 0;
        for (int i = 0; i < predicted.length; i++) {
            sum += categoricalCrossEntropy(predicted[i], actual[i]);
        }
        return sum;
    }
    
}
