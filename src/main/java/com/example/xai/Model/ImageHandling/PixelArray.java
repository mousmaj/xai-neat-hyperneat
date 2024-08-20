package com.example.xai.Model.ImageHandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PixelArray {

    private String path;

    public PixelArray(String path) {
        this.path = path;
    }



    public List<String> collectFolderNames() {
        File directory = new File(path);
        File[] subdirectories = directory.listFiles(File::isDirectory);
        List<String> folderNames = new ArrayList<>();

        if (subdirectories != null) {
            for (File subdirectory : subdirectories) {
                folderNames.add(subdirectory.getName());
            }
        }
        return folderNames;
    }

    public ArrayList<ImageAndAnswer> collectImagesAndAnswers() {
        List<String> folderNames = collectFolderNames();
        ArrayList<ImageAndAnswer> imageAndAnswerList = new ArrayList<>();

        for (int folderCount = 0; folderCount < folderNames.size(); folderCount++) {
            String folderName = folderNames.get(folderCount);
            File folder = new File(path + "/" + folderName);
            imageAndAnswerList.addAll(collectImagesFromFolder(folder, folderCount));
        }

        return imageAndAnswerList;
    }

    private List<ImageAndAnswer> collectImagesFromFolder(File folder, int folderCount) {
        List<ImageAndAnswer> imageAndAnswerList = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            System.out.println("Folder " + folder.getName() + " has " + files.length + " images");
            for (File file : files) {
                BufferedImage image = readImage(file);
                if (image != null) {
                    double[] answer = new double[collectFolderNames().size()];
                    answer[folderCount] = 1.0;
                    float[][] pixelArray2D = getNormalized2DPixelArrayGrayscale(image);
                    imageAndAnswerList.add(new ImageAndAnswer(pixelArray2D, answer));
                }
            }
        }
        return imageAndAnswerList;
    }

    private BufferedImage readImage(File file) {
        try {
            return javax.imageio.ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public float[][] getNormalized2DPixelArrayGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        float[][] result = new float[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int gray = image.getRGB(col, row) & 0xFF;
                float normalizedGray = gray / 255f;
                result[row][col] = normalizedGray;
            }
        }

        return result;
    }
}
