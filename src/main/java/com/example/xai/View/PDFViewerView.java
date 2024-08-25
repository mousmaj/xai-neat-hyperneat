package com.example.xai.View;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class PDFViewerView {
    private ImageView pdfImageView;
    private ScrollPane scrollPane;
    private BorderPane pdfViewPane;

    public PDFViewerView(double scaleFactor) {
        pdfImageView = new ImageView();
        pdfImageView.setPreserveRatio(true);  // Behalte das Seitenverhältnis bei

        // Setze die Skalierung des ImageView
        pdfImageView.setScaleX(scaleFactor);
        pdfImageView.setScaleY(scaleFactor);
        // set border color
        pdfImageView.setStyle("-fx-border-color: black;");

        // Verwende eine VBox, um das ImageView oben auszurichten
        VBox vbox = new VBox(pdfImageView);
        //vbox.setAlignment(Pos.TOP_CENTER); // Oben und zentriert ausrichten
        vbox.setPadding(new Insets(10)); // Randabstand innerhalb der VBox

        scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);

        pdfViewPane = new BorderPane();
        
        // Setze spezifische Insets für jede Seite: oben, rechts, unten, links
        pdfViewPane.setPadding(new Insets(20)); // Außenabstand des gesamten Bereichs
    }

    public void displayPDF() {
        pdfViewPane.setCenter(scrollPane);
    }

    public ImageView getPdfImageView() {
        return pdfImageView;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public BorderPane getPdfViewPane() {
        return pdfViewPane;
    }
}
