package com.example.song_finder_fx;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ControllerRevenueGenerator {
    public Button btnLoadReport;
    private UIController mainUIController = null;

    public ControllerRevenueGenerator(UIController uiController) {
        mainUIController = uiController;
    }

    public void loadRevenueGenerator() throws IOException {
        // TODO: 11/28/2023 Implement Revenue Generator UI
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/revenue-generator.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);
    }

    public void onLoadReportButtonClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select FUGA Report");

        File report = chooser.showOpenDialog(mainUIController.mainVBox.getScene().getWindow());

        btnLoadReport.setText("Working on...");

        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean status = DatabaseMySQL.loadReport(report);

                Platform.runLater(() -> {
                    if (status) {
                        btnLoadReport.setText("CSV Imported to Database");
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();
    }

    public void onGetReportedRoyaltyPerISRCbtnClick() throws SQLException, ClassNotFoundException {
        btnLoadReport.setText("Generating...");

        DatabaseMySQL.getReportedRoyalty();
        /*Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean status = DatabaseMySQL.loadReport(report);

                Platform.runLater(() -> {
                    if (status) {
                        btnLoadReport.setText("CSV Imported to Database");
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();*/
    }
}
