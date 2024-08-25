package com.example.xai.Model.ImageHandling;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;

import com.example.xai.Frontend.GUIStarter;

public class MnistSubsetLoader {

    public static ArrayList<ImageAndAnswer> loadMnistSubset(String imagePath, String labelPath, int subsetSize) throws IOException {
        // Ressourcenpfade für die MNIST-Dateien
        String resourceImagePath = "/com/example/xai/MNIST/" + imagePath;
        String resourceLabelPath = "/com/example/xai/MNIST/" + labelPath;
    
        try (InputStream imageInputStream = GUIStarter.class.getResourceAsStream(resourceImagePath);
             InputStream labelInputStream = GUIStarter.class.getResourceAsStream(resourceLabelPath);
             DataInputStream imageStream = new DataInputStream(imageInputStream);
             DataInputStream labelStream = new DataInputStream(labelInputStream)) {
    
            if (imageInputStream == null || labelInputStream == null) {
                throw new FileNotFoundException("MNIST-Dateien konnten nicht gefunden werden: " + resourceImagePath + " oder " + resourceLabelPath);
            }
    
            int magicNumberImages = imageStream.readInt();
            int numberOfImages = imageStream.readInt();
            int numberOfRows = imageStream.readInt();
            int numberOfColumns = imageStream.readInt();
    
            int magicNumberLabels = labelStream.readInt();
            int numberOfLabels = labelStream.readInt();
    
            if (numberOfImages != numberOfLabels) {
                throw new IOException("Anzahl der Bilder und Labels stimmt nicht überein.");
            }
    
            int imagesPerClass = subsetSize / 10;
            Map<Integer, ArrayList<ImageAndAnswer>> classImages = new HashMap<>();
    
            for (int i = 0; i < numberOfImages; i++) {
                byte label = labelStream.readByte();
                float[][] imageData = new float[numberOfRows][numberOfColumns];
    
                for (int row = 0; row < numberOfRows; row++) {
                    for (int col = 0; col < numberOfColumns; col++) {
                        imageData[row][col] = (imageStream.readUnsignedByte()) / 255.0f;
                    }
                }
    
                double[] answer = new double[10];
                answer[label] = 1.0;
    
                ImageAndAnswer imageAndAnswer = new ImageAndAnswer(imageData, answer);
                
                classImages.computeIfAbsent((int) label, k -> new ArrayList<>()).add(imageAndAnswer);
    
                if (classImages.get((int) label).size() >= imagesPerClass) {
                    boolean allClassesFilled = true;
                    for (List<ImageAndAnswer> images : classImages.values()) {
                        if (images.size() < imagesPerClass) {
                            allClassesFilled = false;
                            break;
                        }
                    }
                    if (allClassesFilled) break;
                }
            }
    
            ArrayList<ImageAndAnswer> subset = new ArrayList<>();
            for (List<ImageAndAnswer> images : classImages.values()) {
                subset.addAll(images.subList(0, Math.min(images.size(), imagesPerClass)));
            }
            return subset;
        }
    }
    


}
