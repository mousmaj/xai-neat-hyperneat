module com.example.xai {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires org.commonmark;
    requires gs.core;
    requires gs.ui.javafx;

    opens com.example.xai.Frontend to javafx.fxml, javafx.graphics;
    exports com.example.xai.Controller;
    exports com.example.xai.View;
    exports com.example.xai.Frontend to javafx.graphics;
}
