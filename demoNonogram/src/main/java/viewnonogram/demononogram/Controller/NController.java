package viewnonogram.demononogram.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class NController {

    @FXML
    private VBox vBox;

    @FXML
    private GridPane gridPane;

    @FXML
    public void onSolveButtonClick() {
        System.out.println("Solve button clicked!");
    }

    @FXML
    public void onHintButtonClick() {
        System.out.println("Hint button clicked!");
    }

    @FXML
    public void onLoadTaskButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Text File");

        String initialDirectoryPath = "C:/Users/rzmmr/IdeaProjects/demoNonogram/src/main/Files";
        File initialDirectory = new File(initialDirectoryPath);

        if (!initialDirectory.exists()) {
            System.err.println("The 'Files' directory does not exist.");
            return;
        }

        fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                loadAndDisplayNonogram(selectedFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No file selected.");
        }
    }

    private void loadAndDisplayNonogram(File file) throws IOException {
        gridPane.getChildren().clear(); // Clear existing grid contents

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int rowCount = 0;
        char[][] nonogramArray = null;
        while ((line = reader.readLine()) != null) {
            String[] cells = line.split(",");
            if (nonogramArray == null) {
                nonogramArray = new char[1][cells.length];
            } else if (rowCount >= nonogramArray.length) {
                // Resize the nonogramArray if needed
                char[][] newArray = new char[nonogramArray.length * 2][cells.length];
                System.arraycopy(nonogramArray, 0, newArray, 0, nonogramArray.length);
                nonogramArray = newArray;
            }
            nonogramArray[rowCount] = new char[cells.length];
            for (int colCount = 0; colCount < cells.length; colCount++) {
                nonogramArray[rowCount][colCount] = cells[colCount].charAt(0);
            }
            rowCount++;
        }

        // Print the nonogram array
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < nonogramArray[i].length; j++) {
                System.out.print(nonogramArray[i][j] + " ");
            }
            System.out.println();
        }
        int numRows = Character.getNumericValue(nonogramArray[0][1]); // Switched x and y
        int numCols = Character.getNumericValue(nonogramArray[0][0]); // Switched x and y

// Initialize arrays for x and y constraints
        String[] xConstraints = new String[numRows]; // Switched x and y
        String[] yConstraints = new String[numCols]; // Switched x and y

// Construct x constraints
        for (int i = 1; i < numRows + numCols +1; i++) {
            if (i < numCols) { // Switched x and y
                StringBuilder constraint = new StringBuilder();
                for (int j = 0; j < nonogramArray[i].length; j++) {
                    constraint.append(nonogramArray[i][j]).append(" ");
                }
                xConstraints[i - 1] = constraint.toString().trim(); // Switched x and y
            } else { // Switched x and y
                StringBuilder constraint = new StringBuilder();
                for (int j = 0; j < nonogramArray[i].length; j++) {
                    constraint.append(nonogramArray[i][j]).append(" ");
                }
                yConstraints[i - 1 - numRows] = constraint.toString().trim(); // Switched x and y
            }
        }


        int y = 0; // Index for y constraints
// Construct y constraints
//        while (y < numCols) {
//            // Skip columns already processed in x constraints
//            if (xConstraints[y] != null) {
//                y++;
//                continue;
//            }
//            StringBuilder constraint = new StringBuilder();
//            for (int i = 1; i < numRows; i++) {
//                constraint.append(nonogramArray[i][y]).append(" ");
//            }
//            yConstraints[y] = constraint.toString().trim();
//            y++;
//        }

// Construct y constraints




// Print x constraints
        System.out.println("X Constraints:");
        for (String xConstraint : xConstraints) {
            System.out.println(xConstraint);
        }

// Print y constraints
        System.out.println("\nY Constraints:");
        for (String yConstraint : yConstraints) {
            System.out.println(yConstraint);
        }






    }



}
