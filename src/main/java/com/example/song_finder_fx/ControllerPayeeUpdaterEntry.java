package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.PayeeUpdaterUI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

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
    void onTrackClick() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String number = lblNumber.getText();

                PayeeUpdaterUI ui = ControllerPayeeUpdater.payeeUpdaterUIS.get(Integer.parseInt(number) - 1);

                IngestCSVData data = ui.getData();

                /*System.out.println("Edit Data on: " + data.getTrackTitle());
                System.out.println("Composer: " + data.getComposer());
                System.out.println("Lyricist: " + data.getLyricist());
                System.out.println("Payee 01: " + data.getPayee().getPayee1());
                System.out.println("Payee 02: " + data.getPayee().getPayee2());
                System.out.println("Payee 03: " + data.getPayee().getPayee3());*/

                try {
                    Node node = SceneController.loadLayout("layouts/ingests/edit-payee-row.fxml");

                    Platform.runLater(() -> {
                        ControllerEditPayeeDetails.staticLblTrackName.setText(data.getTrackTitle());
                        ControllerEditPayeeDetails.staticLblTrackNumber.setText(number);

                        ControllerEditPayeeDetails.staticTxtTrackName.setText(data.getTrackTitle());
                        ControllerEditPayeeDetails.staticTxtComposer.setText(data.getComposer());
                        ControllerEditPayeeDetails.staticTxtLyricist.setText(data.getLyricist());

                        ControllerEditPayeeDetails.staticTxtPayee01.setText(data.getPayee().getPayee1());
                        ControllerEditPayeeDetails.staticTxtPayee01Share.setText(data.getPayee().getShare1());
                        ControllerEditPayeeDetails.staticTxtPayee02.setText(data.getPayee().getPayee2());
                        ControllerEditPayeeDetails.staticTxtPayee02Share.setText(data.getPayee().getShare2());
                        ControllerEditPayeeDetails.staticTxtPayee03.setText(data.getPayee().getPayee3());
                        ControllerEditPayeeDetails.staticTxtPayee03Share.setText(data.getPayee().getShare3());

                        UIController.sideVBoxStatic.getChildren().setAll(node);
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString()));
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();

    }

}
