package com.example.xai.Controller;

import com.example.xai.View.PDFViewerView;
import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class PDFViewerController {
    private PDFViewerView view;

    public PDFViewerController(PDFViewerView view, Path filePath) throws IOException {
        this.view = view;

        // Entscheidung treffen, welche Datei angezeigt werden soll
        if (filePath.toString().toLowerCase().endsWith(".pdf")) {
            showPDF(filePath);
        } else if (filePath.toString().toLowerCase().endsWith(".md")) {
            // deleted, cause of trouble with import
        } else {
            throw new IllegalArgumentException("Unsupported file format. Only .pdf and .md are supported.");
        }
    }

    private void showPDF(Path pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile.toFile())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int totalWidth = 0;
            int totalHeight = 0;

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 150, org.apache.pdfbox.rendering.ImageType.RGB);
                totalWidth = Math.max(totalWidth, bufferedImage.getWidth());
                totalHeight += bufferedImage.getHeight();
            }

            BufferedImage fullImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);

            int yOffset = 0;
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 150, org.apache.pdfbox.rendering.ImageType.RGB);
                fullImage.getGraphics().drawImage(bufferedImage, 0, yOffset, null);
                yOffset += bufferedImage.getHeight();
            }

            view.getPdfImageView().setImage(SwingFXUtils.toFXImage(fullImage, null));
            view.displayPDF();
        }
    }

}
