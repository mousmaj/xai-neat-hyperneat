package com.example.xai.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.awt.Desktop;
import java.net.URI;

public class WelcomeView {
    private VBox root;

    public WelcomeView() {
        root = new VBox(30);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setMinWidth(600);
        root.setStyle("-fx-background-color: #f4f4f4;");  // Heller Hintergrund für eine moderne Optik

        // Überschrift
        Label infoTitleLabel = new Label("Willkommen zu \nxAI - NEAT & HyperNEAT");
        infoTitleLabel.setTextAlignment(TextAlignment.CENTER);
        infoTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        infoTitleLabel.setTextFill(Color.web("#2c3e50"));

        // Bild laden und anzeigen
        ImageView imageView = new ImageView();
        //Image logoImage = new Image(getClass().getResourceAsStream("/com/example/xai/images/images/Frame 7.png")); // Bild aus dem Ressourcenordner laden
        //imageView.setImage(logoImage);
        imageView.setFitWidth(350);
        imageView.setPreserveRatio(true);
        imageView.setEffect(new DropShadow(10, Color.gray(0.4)));

        // Beschreibungstext
        Label infoTextLabel = new Label(
                "Diese Anwendung wurde im Rahmen der Bachelorarbeit 'Entwicklung einer Java-Anwendung zur "
                + "Erklärbarkeit (XAI) der neuroevolutionären Algorithmen: NEAT und HyperNEAT in der "
                + "Bildklassifizierung' entworfen.\n\nAutor: Majid Moussa Adoyi\n E-Mail: majid.moussaadoyi@haw-hamburg.de \nVersion: 1.0\nErscheinungsdatum: 18.08.2024 "
        );
        infoTextLabel.setWrapText(true);
        infoTextLabel.setFont(Font.font("Arial", 16));
        infoTextLabel.setTextFill(Color.web("#7f8c8d"));
        infoTextLabel.setTextAlignment(TextAlignment.CENTER);
        infoTextLabel.setPadding(new Insets(20, 0, 20, 0));

        // Hyperlink-Label
        Label tutorialLinkLabel = new Label("Bitte Tutorial anschauen: https://youtu.be/Hl9m5cTZ1Jg");
        tutorialLinkLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tutorialLinkLabel.setTextFill(Color.web("#2980b9"));  // Blaue Farbe für den Hyperlink
        tutorialLinkLabel.setUnderline(true);
        tutorialLinkLabel.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://youtu.be/Hl9m5cTZ1Jg"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // "Verstanden"-Button
        Button understoodButton = new Button("Verstanden");
        understoodButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10px 30px;");
        understoodButton.setEffect(new DropShadow(5, Color.gray(0.3)));

        root.getChildren().addAll(infoTitleLabel, imageView, infoTextLabel, tutorialLinkLabel, understoodButton);
    }

    public VBox getRoot() {
        return root;
    }

    public Button getUnderstoodButton() {
        return (Button) root.getChildren().get(4); // "Verstanden" Button ist das fünfte Element in der VBox
    }
}
