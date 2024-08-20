package com.example.xai.Model.ImageHandling;

import com.example.xai.ConfigurationNeat.ImageDisplay;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MnistSubsetLoader {

    // Angepasste Methode mit festgelegten Pfaden
    public static ArrayList<ImageAndAnswer> loadMnistSubset(int subsetSize) throws IOException {
        String imagePath = "/com/example/xai/MNIST/t10k-images.idx3-ubyte";
        String labelPath = "/com/example/xai/MNIST/t10k-labels.idx1-ubyte";

        try (InputStream imageStream = MnistSubsetLoader.class.getResourceAsStream(imagePath);
             InputStream labelStream = MnistSubsetLoader.class.getResourceAsStream(labelPath);
             DataInputStream imageDataStream = new DataInputStream(imageStream);
             DataInputStream labelDataStream = new DataInputStream(labelStream)) {

            if (imageStream == null || labelStream == null) {
                throw new IOException("Konnte MNIST-Dateien nicht finden. Überprüfe den Pfad: " + imagePath + " oder " + labelPath);
            }

            int magicNumberImages = imageDataStream.readInt();
            int numberOfImages = imageDataStream.readInt();
            int numberOfRows = imageDataStream.readInt();
            int numberOfColumns = imageDataStream.readInt();

            int magicNumberLabels = labelDataStream.readInt();
            int numberOfLabels = labelDataStream.readInt();

            if (numberOfImages != numberOfLabels) {
                throw new IOException("Anzahl der Bilder und Labels stimmt nicht überein.");
            }

            int imagesPerClass = subsetSize / 10;
            Map<Integer, ArrayList<ImageAndAnswer>> classImages = new HashMap<>();

            for (int i = 0; i < numberOfImages; i++) {
                byte label = labelDataStream.readByte();
                float[][] imageData = new float[numberOfRows][numberOfColumns];

                for (int row = 0; i < numberOfRows; i++) {
                    for (int col = 0; col < numberOfColumns; col++) {
                        imageData[row][col] = (imageDataStream.readUnsignedByte()) / 255.0f;
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

    private static int getLabelFromAnswerArray(double[] answerArray) {
        for (int i = 0; i < answerArray.length; i++) {
            if (answerArray[i] == 1.0) {
                return i;
            }
        }
        throw new IllegalArgumentException("Ungültiges Label-Array");
    }
}
