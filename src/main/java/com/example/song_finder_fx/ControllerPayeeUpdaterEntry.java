package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.PayeeUpdaterUI;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class ControllerPayeeUpdaterEntry {

    @FXML
    private CheckBox cbEntry;

    @FXML
    private Label lblContributor01;

    @FXML
    private Label lblContributor02;

    @FXML
    private Label lblISRC;

    @FXML
    private Label lblPayee01;

    @FXML
    private Label lblPayee02;

    @FXML
    private Label lblTrackName;

    @FXML
    private Label lblNumber;

    @FXML
    void onTrackClick(MouseEvent event) {
        /*Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();*/

        String number = lblNumber.getText();

        PayeeUpdaterUI ui = ControllerPayeeUpdater.payeeUpdaterUIS.get(Integer.parseInt(number) - 1);

        IngestCSVData data = ui.getData();

        System.out.println("Edit Data on: " + data.getTrackTitle());
        System.out.println("Composer: " + data.getComposer());
        System.out.println("Lyricist: " + data.getLyricist());
        System.out.println("Payee 01: " + data.getPayee().getPayee1());
        System.out.println("Payee 02: " + data.getPayee().getPayee2());
        System.out.println("Payee 03: " + data.getPayee().getPayee3());

        try {
            Node node = SceneController.loadLayout("layouts/ingests/edit-payee-row.fxml");
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

}
