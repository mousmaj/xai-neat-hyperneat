package com.example.xai.Model.ImageHandling;

public class ImageAndAnswer {
    private float[][] image2D;
    private double[] answer;

    public ImageAndAnswer(float[][] image, double[] answer) {
        this.image2D = image;
        this.answer = answer;
    }

    public float[][] getImage2D() {
        return image2D;

    }

    public double[] getAnswer() {
        return answer;
    }
    
}
