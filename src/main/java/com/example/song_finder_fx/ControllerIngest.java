package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class ControllerIngest {

    @FXML
    private Label lblMissingISRCs;

    @FXML
    private Label lblMissingPayees;

    @FXML
    void initialize() {
        Task<Void> taskMissingPayees = new Task<>() {
            @Override
            protected Void call() {
                try {
                    int missingPayeeCount = DatabasePostgres.getMissingPayeeCount();
                    Platform.runLater(() -> lblMissingPayees.setText(String.valueOf(missingPayeeCount)));
                } catch (SQLException e) {
                    AlertBuilder.sendErrorAlert("Error", "Unable to fetch details", e.toString());
                }
                return null;
            }
        };

        Task<Void> taskMissingISRCs = new Task<>() {
            @Override
            protected Void call() {
                try {
                    int missingISRC_Count = DatabasePostgres.getMissingISRC_Count();
                    Platform.runLater(() -> lblMissingISRCs.setText(String.valueOf(missingISRC_Count)));
                } catch (SQLException e) {
                    AlertBuilder.sendErrorAlert("Error", "Unable to fetch details", e.toString());
                }
                return null;
            }
        };

        Thread threadMissingPayees = new Thread(taskMissingPayees);
        threadMissingPayees.start();
        // threadMissingPayees.join();

        Thread threadMissingISRCs = new Thread(taskMissingISRCs);
        threadMissingISRCs.start();
        // threadMissingISRCs.join();
    }

    @FXML
    void onExportISRCsClick(MouseEvent event) {

    }

    @FXML
    void onExportPayeesClick(MouseEvent event) {

    }

    @FXML
    void onImportIngest(MouseEvent event) {
        Scene scene = SceneController.getSceneFromEvent(event);
        Window window = SceneController.getWindowFromScene(scene);
        File file = Main.browseForCSV(window);

        if (file != null) {
            // System.out.println("True");
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        CSVReader reader = new CSVReader(new FileReader(file));
                        String[] row = reader.readNext();

                        /*while ((row = reader.readNext()) != null) {

                        }*/

                        int rowLength = row.length;

                        System.out.println("Column Count: " + rowLength);

                        if (rowLength == 63) {

                        }
                    } catch (CsvValidationException | IOException e) {
                        Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Reading CSV File", e.toString()));
                    }
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.start();
        }
    }

    @FXML
    void onUpdatePayees(MouseEvent event) {

    }

    @FXML
    void onUpdateSongs(MouseEvent event) {

    }

}
