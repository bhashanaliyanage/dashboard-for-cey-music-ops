package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.UPCGenarator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ControllerUPCGenerator {

    @FXML
    private Label lblUPCCount;

    @FXML
    private Spinner<Integer> upcSpinner;

    @FXML
    private TextArea textAreaUPC;

    @FXML
    private Button btnGenerateUPC;

    @FXML
    private Button btnUndo;

    private UPCGenarator upcGenarator;

    private int availableUPC_Count = 0;

    private int userID;

    private List<String> upcList;

    @FXML
    public void initialize() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                upcGenarator = new UPCGenarator();
                userID = Main.userSession.getUserID();

                availableUPC_Count = upcGenarator.getUpcAvailableCount();
                SpinnerValueFactory<Integer> upcValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, availableUPC_Count, 1);

                Platform.runLater(() -> {
                    lblUPCCount.setText(availableUPC_Count + " UPCs Available");
                    upcSpinner.setValueFactory(upcValueFactory);
                });

                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() -> {
            AlertBuilder.sendErrorAlert("Error", "Failed to initialize UPC Generator", e.toString());
            task.getException().printStackTrace();
            lblUPCCount.setText("57 UPCs Available");
        }));

        new Thread(task).start();
    }

    @FXML
    void onGenerateUPCs() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    btnGenerateUPC.setDisable(true);

                    int upcCount = upcSpinner.getValue();
                    StringBuilder upcStringBuilder = new StringBuilder();

                    if (upcCount > availableUPC_Count) {
                        Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Unable to generate UPCs", "Only " + availableUPC_Count + " UPCs available"));
                    } else {
                        // Integrate backend, get UPCs, display them
                        upcList = upcGenarator.viewUpcList(upcCount, userID);

                        for (String upc : upcList) {
                            upcStringBuilder.append(upc).append("\n");
                        }

                        Platform.runLater(() -> textAreaUPC.setText(upcStringBuilder.toString()));
                    }

                    btnGenerateUPC.setDisable(false);
                } catch (Exception e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Unable to generate UPCs", e.toString()));
                    System.out.println("Error Generating UPCs: " + e);
                }
                return null;
            }
        };

        boolean confirmation = AlertBuilder.getSendConfirmationAlert("Confirmation", null, "Are you sure you want to generate " + upcSpinner.getValue() + " UPCs?");

        if (confirmation) {
            new Thread(task).start();
        }
    }

    @FXML
    void onUndo(ActionEvent event) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    btnUndo.setDisable(true);
                    upcGenarator.removeUpcData(upcList);
                    textAreaUPC.setText("");
                    btnUndo.setDisable(false);
                } catch (Exception e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Unable to undo last action", e.toString()));
                    e.printStackTrace();
                }
                return null;
            }
        };


        boolean confirmation = AlertBuilder.getSendConfirmationAlert("Warning", null, "Are you sure you want to undo last action?");

        if (confirmation) {
            new Thread(task).start();
        }
    }
}
