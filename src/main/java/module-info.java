module co.edu.uptc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;
    requires jfreechart;
    requires itextpdf;
    requires jcommon;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires jakarta.mail;





    opens co.edu.uptc to javafx.fxml;
    opens co.edu.uptc.vista to javafx.fxml;
    opens co.edu.uptc.modelo to com.google.gson;
    exports co.edu.uptc;
    exports co.edu.uptc.vista;
}
