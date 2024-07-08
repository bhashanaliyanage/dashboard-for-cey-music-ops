package com.example.song_finder_fx;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ControllerUnapprovedIngests {

    @FXML
    private Label lblIngestCount;

    @FXML
    private VBox vbIngestList;

    @FXML
    void initialize() {
        int total = ControllerIngest.unApprovedIngests.size();
        lblIngestCount.setText(String.valueOf(total));

        /*Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for ()

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();*/
    }

    @FXML
    void onGoBack(MouseEvent event) {

    }

}
