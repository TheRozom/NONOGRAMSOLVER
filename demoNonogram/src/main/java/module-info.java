module viewnonogram.demononogram {
    requires javafx.controls;
    requires javafx.fxml;


    opens viewnonogram.demononogram to javafx.fxml;
    exports viewnonogram.demononogram;
    exports viewnonogram.demononogram.Controller;
    opens viewnonogram.demononogram.Controller to javafx.fxml;
    exports viewnonogram.demononogram.Model;
    opens viewnonogram.demononogram.Model to javafx.fxml;
}