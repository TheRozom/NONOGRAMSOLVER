package viewnonogram.demononogram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        primaryStage.setTitle("NONOGRAM");
        primaryStage.setScene(new Scene(root, 800, 600)); // Adjust width and height as needed
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
