module com.example.xai {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires org.apache.commons.io;
    requires gs.core;
    requires org.apache.pdfbox;
    requires commons.math3;
    requires org.commonmark;
    requires java.desktop;
    requires javafx.swing;
    requires gs.ui.javafx; // Falls du AWT/Swing-Komponenten nutzt


    opens com.example.xai.Frontend to javafx.fxml, javafx.graphics;
    exports com.example.xai.Controller;
    exports com.example.xai.View;
    exports com.example.xai.Frontend to javafx.graphics;

}
