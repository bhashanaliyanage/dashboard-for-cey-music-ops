package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.IngestCSVDataController;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Ingest;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.Payee;
import com.example.song_finder_fx.Model.PayeeUpdaterUI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerPayeeUpdater {

    @FXML
    private Label lblIngestName;

    @FXML
    private VBox vboxTracks;

    public static List<PayeeUpdaterUI> payeeUpdaterUIS = new ArrayList<>();

    @FXML
    void initialize() {
        System.out.println("ControllerPayeeUpdater.initialize");
        Ingest ingest = ControllerUnApprovedIngestEntry.ingest;
        List<IngestCSVData> csvData = ingest.getIngestCSVDataList();

        final int[] count = {0};

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> System.out.println("Looping through CSV rows"));

                payeeUpdaterUIS.clear();

                for (IngestCSVData data : csvData) {
                    try {

                        // Platform.runLater(() -> System.out.println("Loading Layout for: " + data.getTrackTitle()));
                        Node node = SceneController.loadLayout("layouts/ingests/payee-updater-view-entry.fxml");

                        CheckBox checkBox = (CheckBox) node.lookup("#cbEntry");
                        Label lblContributor01 = (Label) node.lookup("#lblContributor01");
                        Label lblContributor02 = (Label) node.lookup("#lblContributor02");
                        Label lblISRC = (Label) node.lookup("#lblISRC");
                        Label lblPayee01 = (Label) node.lookup("#lblPayee01");
                        Label lblPayee02 = (Label) node.lookup("#lblPayee02");
                        Label lblTrackName = (Label) node.lookup("#lblTrackName");
                        Label lblNumber = (Label) node.lookup("#lblNumber");

                        Platform.runLater(() -> {
                            count[0]++;
                            lblISRC.setText(data.getIsrc());
                            lblTrackName.setText(data.getTrackTitle());
                            lblContributor01.setText(data.getComposer());
                            lblContributor02.setText(data.getLyricist());
                            lblNumber.setText(String.valueOf(count[0]));
                            vboxTracks.getChildren().add(node);
                        });

                        payeeUpdaterUIS.add(new PayeeUpdaterUI(checkBox, lblContributor01, lblContributor02, lblISRC, lblPayee01, lblPayee02, lblTrackName, data));
                    } catch (IOException e) {
                        Platform.runLater(() -> {
                            throw new RuntimeException();
                        });
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onAssignPayees() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (PayeeUpdaterUI uiElement : payeeUpdaterUIS) {

                    CheckBox checkBox = uiElement.getCbEntry();

                    if (checkBox.isSelected()) {

                        Platform.runLater(() -> System.out.println(uiElement.getLblISRC().getText()));

                        IngestCSVData data = uiElement.getData();
                        IngestCSVDataController controller = new IngestCSVDataController(data);
                        try {
                            // TODO: Show assigning payees waiting message in the UI
                            IngestCSVData assignedData = controller.assignPayees();
                            Payee payee = assignedData.getPayee();

                            if (payee.getPayee1() != null) {
                                Platform.runLater(() -> {
                                    uiElement.getLblPayee01().setText(payee.getPayee1());
                                    uiElement.getLblPayee01().setStyle("-fx-text-fill: '#72a276'");
                                });
                            }

                            if (payee.getPayee2() != null) {
                                Platform.runLater(() -> {
                                    uiElement.getLblPayee02().setText(payee.getPayee2());
                                    uiElement.getLblPayee02().setStyle("-fx-text-fill: '#72a276'");
                                });
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException();
                        }


                        /*Platform.runLater(() -> {
                            System.out.println("ISRC: " + assignedData.getIsrc());
                        });


                        */

                            /*if (DatabasePostgres.searchArtistTable(uiElement.getData().getComposer())) {
                                // Assign composer to payee 01
                                uiElement.getData().getPayee().setPayee1(uiElement.getData().getComposer());
                                uiElement.getData().getPayee().setShare1("50");

                                Platform.runLater(() -> {
                                    uiElement.getLblPayee01().setText(data.getComposer());
                                    uiElement.getLblPayee01().setStyle("-fx-text-fill: '#72a276'");
                                    // #72a276
                                });

                                if (DatabasePostgres.searchArtistTable(uiElement.getData().getLyricist())) {
                                    // Assign lyricist to payee 01
                                    uiElement.getData().getPayee().setPayee2(uiElement.getData().getLyricist());
                                    uiElement.getData().getPayee().setShare2("50");

                                    Platform.runLater(() -> {
                                        uiElement.getLblPayee02().setText(data.getLyricist());
                                        uiElement.getLblPayee02().setStyle("-fx-text-fill: '#72a276'");
                                    });
                                }
                            } else if (DatabasePostgres.searchArtistTable(uiElement.getData().getLyricist())) {
                                uiElement.getData().getPayee().setPayee1(uiElement.getData().getLyricist());
                                uiElement.getData().getPayee().setShare1("50");

                                Platform.runLater(() -> {
                                    uiElement.getLblPayee01().setText(data.getLyricist());
                                    uiElement.getLblPayee01().setStyle("-fx-text-fill: '#72a276'");
                                });
                            }*/
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onGoBack() {

    }

    @FXML
    void onSave() {

    }

    @FXML
    void onSelectAll() {
        checkBoxesSetSelected(true);
    }


    @FXML
    void onSelectNone() {
        checkBoxesSetSelected(false);
    }

    private void checkBoxesSetSelected(boolean value) {
        for (PayeeUpdaterUI uiElement : payeeUpdaterUIS) {
            CheckBox checkBox = uiElement.getCbEntry();
            checkBox.setSelected(value);
        }
    }
}
