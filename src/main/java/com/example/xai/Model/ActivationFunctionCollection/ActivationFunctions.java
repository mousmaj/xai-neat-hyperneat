package com.example.xai.Model.ActivationFunctionCollection;

import java.util.Random;
/**
 * Class for the activation functions. The various activation functions are implemented here.
 * 
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 01.07.2024
 * 
 */
public class ActivationFunctions {

    /**
     * The result of the activation function is a = σ(z).
     * @param function
     * @param x
     * @return
     */
    public double applyActivationFunction(ActivationEnum function, double z) {
        double result = 0;
        switch (function) {
            case IDENTITIY:
                result = z;
                break;
            case SIGMOID: // modified sigmoidal transfer function. steepened sigmoid allows more fine tuning at extreme activations.
                result = 1 / (1 + Math.exp(-4.9 * z));
                break;
            case TANH:
                result = Math.tanh(z);
                break;
            case GAUSSIAN:
                result = Math.exp(-z * z);
                break;
            case COS:
                result = Math.cos(z);
                break;
            case SIN:
                result = Math.sin(z);
                break;
            default:
                throw new IllegalArgumentException("Unknown Activationfunction: " + function);
        }
        return result;
    }

    /**
     * Picks a random activation function from the ActivationEnum.
     * 
     * @see ActivationEnum
     * 
     * @return ActivationEnum
     */
    public static ActivationEnum getRandomActivationEnum() {
    ActivationEnum[] values = ActivationEnum.values();
    int length = values.length;
    Random random = new Random();
    return values[random.nextInt(length)];
}
}
