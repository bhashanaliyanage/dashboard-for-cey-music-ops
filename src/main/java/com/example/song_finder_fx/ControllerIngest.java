package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerIngest {
    
    @FXML
    private Label lblImportIngest;

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
        System.out.println("ControllerIngest.onExportISRCsClick");
        try {
            // Getting User Location
            Node node = (Node) event.getSource();
            Scene scene = node.getScene();
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            chooser.setTitle("Save As");
            File file = chooser.showSaveDialog(scene.getWindow());

            if (file != null) {
                File openFile = writeMissingISRCs(file);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(openFile);
                }
            }

        } catch (SQLException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Getting Songs", e.toString());
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Generating CSV", e.toString());
        }
    }

    private static @NotNull File writeMissingISRCs(File file) throws SQLException, IOException {
        ArrayList<Songs> songs =  DatabasePostgres.getMissingISRCs();
        String path = file.getAbsolutePath();
        CSVWriter writer = new CSVWriter(new FileWriter(path));
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[]{"UPC", "ISRC"};
        rows.add(header);

        for (Songs song : songs) {
            String[] row = {song.getUPC(), song.getISRC()};
            rows.add(row);
        }

        writer.writeAll(rows);
        writer.close();

        File openFile = new File(path);
        return openFile;
    }

    @FXML
    void onExportPayeesClick(MouseEvent event) {
        try {
            // Getting User Location
            Node node = (Node) event.getSource();
            Scene scene = node.getScene();
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            chooser.setTitle("Save As");
            File file = chooser.showSaveDialog(scene.getWindow());

            if (file != null) {
                File openFile = writeMissingPayees(file);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(openFile);
                }
            }

        } catch (SQLException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Getting Songs", e.toString());
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Generating CSV", e.toString());
        }
    }

    private File writeMissingPayees(File file) throws SQLException, IOException {
        ArrayList<Songs> songs =  DatabasePostgres.getMissingPayees();
        String path = file.getAbsolutePath();
        CSVWriter writer = new CSVWriter(new FileWriter(path));
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[]{"UPC", "ISRC"};
        rows.add(header);

        for (Songs song : songs) {
            String[] row = {song.getUPC(), song.getISRC()};
            rows.add(row);
        }

        writer.writeAll(rows);
        writer.close();

        File openFile = new File(path);
        return openFile;
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
                        Platform.runLater(() -> lblImportIngest.setText("Validating CSV"));

                        CSVReader reader = new CSVReader(new FileReader(file));
                        String[] row = reader.readNext();

                        int rowLength = row.length;

                        System.out.println("Column Count: " + rowLength);

                        if (rowLength == 63) {
                            while ((row = reader.readNext()) != null) {
                                // Import

                            }
                        } else {
                            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Invalid CSV Format", "Expected 63 columns but found " + rowLength));
                            Platform.runLater(() -> lblImportIngest.setText("Import Ingest"));
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
