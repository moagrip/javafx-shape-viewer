module com.lab3.laboration3moagrip {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lab3.laboration3moagrip to javafx.fxml;
    exports com.lab3.laboration3moagrip;
}