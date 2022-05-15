module com.example.caesarproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens com.example.caesarproject to javafx.fxml;
    exports com.example.caesarproject;
}