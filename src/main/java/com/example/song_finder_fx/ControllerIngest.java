package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.IngestController;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Ingest;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ControllerIngest {

    @FXML
    public Label lblCount;

    @FXML
    private Label lblImportIngest;

    @FXML
    private Label lblMissingISRCs;

    @FXML
    private Label lblMissingPayees;

    @FXML
    private TextField txtFileLocation;

    @FXML
    private TextField txtName;

    private File file;

    public static List<Ingest> unApprovedIngests;

    @FXML
    void initialize() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                getUnApprovedIngests();
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    private void getUnApprovedIngests() {
        try {
            unApprovedIngests = DatabasePostgres.getUnApprovedIngests();
            Platform.runLater(() -> lblCount.setText(String.valueOf(unApprovedIngests.size())));
        } catch (SQLException e) {
            Platform.runLater(() -> {
                try {
                    NotificationBuilder.displayTrayError("Error", "Error Fetching Un-Approved Ingests");
                    e.printStackTrace();
                } catch (AWTException ex) {
                    System.out.println("Error Fetching Un-Approved Ingests\nStack Trace: " + e);
                }
            });
        }
    }

    /*@FXML
    void onImportIngest(MouseEvent event) {
        Scene scene = SceneController.getSceneFromEvent(event);
        Window window = SceneController.getWindowFromScene(scene);
        File file = Main.browseForCSV(window);

        if (file != null) {
            // System.out.println("True");
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        Platform.runLater(() -> lblImportIngest.setText("Validating CSV"));

                        CSVReader reader = new CSVReader(new FileReader(file));
                        String[] row = reader.readNext();

                        int rowLength = row.length;

                        System.out.println("Column Count: " + rowLength);

                        if (rowLength == 63) {
                            IngestController ingestController = new IngestController();

                            System.out.println("ControllerIngest.call | Using Old Importing Method");

                            // String status = ingestController.insertIngest(ingestName, date, file);
                            // Platform.runLater(() -> System.out.println("\nImport Status: " + status));
                            *//*if (Objects.equals(status, "done")) {
                                Platform.runLater(() -> lblImportIngest.setText("Import Ingest"));
                            }*//*
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
    }*/

    @FXML
    public void onImportNew() {
        String ingestName = txtName.getText();
        LocalDate date = LocalDate.now();

        if (!txtName.getText().isEmpty()) {
            txtName.setStyle("-fx-border-color: '#e9ebee';");

            if (file != null) {
                txtFileLocation.setStyle("-fx-border-color: '#e9ebee';");

                IngestController ingestController = new IngestController();

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            String status = ingestController.insertIngest(ingestName, date, file);
                            if (Objects.equals(status, "done")) {
                                Platform.runLater(() -> {
                                    txtName.setText("");
                                    txtFileLocation.setText("");
                                    file = null;

                                    getUnApprovedIngests();

                                    try {
                                        NotificationBuilder.displayTrayInfo("Ingest Imported", "Please check and approve " + unApprovedIngests.size() + " un-approved ingests");
                                    } catch (AWTException e) {
                                        System.out.println("Unable to send notifications: " + e);
                                    }
                                });
                            } else {
                                Platform.runLater(() -> {
                                    try {
                                        NotificationBuilder.displayTrayError("Error", "Ingest not imported");
                                    } catch (AWTException e) {
                                        System.out.println("Unable to send notifications: " + e);
                                    }
                                });
                            }
                        } catch (SQLException e) {
                            Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Importing Ingest", e.toString()));
                        }

                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.start();
            } else {
                txtFileLocation.setStyle("-fx-border-color: red;");
            }
        } else {
            txtName.setStyle("-fx-border-color: red;");
        }
    }

    @FXML
    void onBrowse(ActionEvent event) {
        Scene scene = SceneController.getSceneFromEvent(event);
        Window window = SceneController.getWindowFromScene(scene);
        file = Main.browseForCSV(window);

        if (file != null) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        CSVReader reader = new CSVReader(new FileReader(file));
                        String[] row = reader.readNext();

                        int rowLength = row.length;

                        System.out.println("Column Count: " + rowLength);

                        if (rowLength == 63) {
                            Platform.runLater(() -> {
                                try {
                                    NotificationBuilder.displayTrayInfo("CSV Validated", "You can proceed to import");
                                    txtFileLocation.setText(file.getAbsolutePath());
                                } catch (AWTException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                try {
                                    NotificationBuilder.displayTrayError("Invalid CSV Format", "Expected 63 columns but found " + rowLength);
                                } catch (AWTException e) {
                                    throw new RuntimeException(e);
                                }
                            });
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

    public void onPendingIngestClick() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/ingests/un-approved-ingests.fxml")));
            UIController.mainVBoxStatic.getChildren().setAll(node);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }
}
