package com.example.xai.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class PDFViewerView {
    private ImageView pdfImageView;
    private ScrollPane scrollPane;
    private BorderPane pdfViewPane;
    private WebView markdownView;

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
        vbox.setAlignment(Pos.TOP_CENTER); // Oben und zentriert ausrichten
        vbox.setPadding(new Insets(10)); // Randabstand innerhalb der VBox

        scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);

        // WebView für die Anzeige von Markdown-Dateien
        markdownView = new WebView();

        // Verwende eine VBox, um Padding um das WebView hinzuzufügen
        VBox markdownContainer = new VBox(markdownView);
        markdownContainer.setPadding(new Insets(10)); // Padding um das WebView

        pdfViewPane = new BorderPane();
        
        // Setze spezifische Insets für jede Seite: oben, rechts, unten, links
        pdfViewPane.setPadding(new Insets(20)); // Außenabstand des gesamten Bereichs
    }

    public void displayPDF() {
        pdfViewPane.setCenter(scrollPane);
    }

    public void displayMarkdown() {
        pdfViewPane.setCenter(markdownView);
    }

    public ImageView getPdfImageView() {
        return pdfImageView;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public WebView getMarkdownView() {
        return markdownView;
    }

    public BorderPane getPdfViewPane() {
        return pdfViewPane;
    }
}
