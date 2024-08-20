package com.example.xai.ConfigurationNeat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplay {

    public void displayImage(float[][] grayValues) {
        // Create a BufferedImage from the grayValues.
        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                float value = grayValues[i][j];
                int gray = Math.round(value * 255); // Convert to 0-255
                int rgb = (gray << 16) | (gray << 8) | gray; // Gray to RGB
                image.setRGB(j, i, rgb);
            }
        }

        // Show the image in a JFrame.
        JFrame frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.add(new JLabel(new ImageIcon(image.getScaledInstance(280, 280, Image.SCALE_DEFAULT))));
        frame.setVisible(true);
    }

}
