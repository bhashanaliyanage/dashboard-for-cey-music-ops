package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.Model.Ingest;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.Payee;
import com.example.song_finder_fx.Model.PayeeUpdaterUI;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControllerPayeeUpdater {

    @FXML
    private Button btnAssignPayees;

    @FXML
    private Label lblIngestName;

    @FXML
    private VBox vboxTracks;

    public static List<PayeeUpdaterUI> payeeUpdaterUIS = new ArrayList<>();

    private boolean hideAssignedPayees = false;

    @FXML
    void initialize() {
        onRefresh();
        System.out.println("ControllerPayeeUpdater.initialize");
    }

    @FXML
    void onAssignPayees() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                Platform.runLater(() -> {
                    btnAssignPayees.setText("Assigning");
                    btnAssignPayees.setDisable(true);
                });

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
                                    uiElement.getLblPayee01().setText(payee.getPayee1() + " (" + payee.getShare1() + "%)");
                                    uiElement.getLblPayee01().setStyle("-fx-text-fill: '#72a276'");
                                });
                            }

                            if (payee.getPayee2() != null) {
                                Platform.runLater(() -> {
                                    uiElement.getLblPayee02().setText(payee.getPayee2() + " (" + payee.getShare2() + "%)");
                                    uiElement.getLblPayee02().setStyle("-fx-text-fill: '#72a276'");
                                });
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException();
                        }


                    }
                }

                Platform.runLater(() -> {
                    btnAssignPayees.setText("Assign Payees");
                    btnAssignPayees.setDisable(false);
                });

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    @FXML
    void onGoBack() {
        UIController.mainVBoxStatic.getChildren().setAll(ControllerIngest.unApprovedIngestsUI);
    }

    @FXML
    void onSave() {
        String payeeDetails = null;
        try {
            int count = 0;
            for (PayeeUpdaterUI ui : payeeUpdaterUIS) {
                count++;
                IngestCSVData data = ui.getData();
                IngestController ingestController = new IngestController();
                boolean validated = ingestController.validateISRC(data.getIsrc());

                if (validated) {
                    Payee payee = data.getPayee();
                    payee.setIsrc(data.getIsrc());
                    CheckBox checkBox = ui.getCbEntry();

                    if (checkBox.isSelected()) {
                        //<editor-fold desc="Print Payee Details">
                        payeeDetails = String.format(
                                "Song: %s\n" +
                                        "Payee 1: %s, Share 1: %s\n" +
                                        "Payee 2: %s, Share 2: %s\n" +
                                        "Payee 3: %s, Share 3: %s\n",
                                ui.getData().getTrackTitle(),
                                payee.getPayee1(), payee.getShare1(),
                                payee.getPayee2(), payee.getShare2(),
                                payee.getPayee3(), payee.getShare3()
                        );

                        System.out.println(payeeDetails);
                        //</editor-fold>

                        if (payee.getPayee1() != null) {
                            DatabasePostgres.updatePayee(payee);
                        } else {
                            AlertBuilder.sendInfoAlert("Error", "Could not save payee details for null values", payeeDetails);
                            break;
                        }
                    }
                } else {
                    AlertBuilder.sendInfoAlert("Error", "Error Saving Payee", "Error validating ISRC: " + data.getIsrc());
                    break;
                }

            }
            NotificationBuilder.displayTrayInfo(count + " payee details added", "Payee details for selected songs were added");
        } catch (SQLException e) {
            AlertBuilder.sendErrorAlert("Error Saving Payee", null, "Error Saving Payee for: " + payeeDetails + "\n\n" + e);
        }
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

    @FXML
    public void onHome(MouseEvent mouseEvent) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(ControllerSettings.class.getResource("layouts/ingests-chooser.fxml")));
            UIController.mainVBoxStatic.getChildren().setAll(node);

            FXMLLoader sidepanelLoader = new FXMLLoader(getClass().getResource("layouts/sidepanel-blank.fxml"));
            Parent sidepanelNewContent = sidepanelLoader.load();
            UIController.sideVBoxStatic.getChildren().clear();
            UIController.sideVBoxStatic.getChildren().add(sidepanelNewContent);
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        }
    }

    public void onHideAssigned(MouseEvent mouseEvent) {
        hideAssignedPayees = !hideAssignedPayees;
        onRefresh();
    }

    private void onRefresh() {
        Ingest ingest = ControllerUnApprovedIngestEntry.ingest;
        List<IngestCSVData> csvData = ingest.getIngestCSVDataList();

        // Fetch all ISRCs from csvData
        List<String> isrcs = csvData.stream()
                .map(IngestCSVData::getIsrc)
                .toList();

        final int[] count = {0};

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                Map<String, Payee> payeeDetails = DatabasePostgres.fetchPayeeDetailsForISRCs(isrcs);

                Platform.runLater(() -> System.out.println("Looping through CSV rows"));

                payeeUpdaterUIS.clear();
                Platform.runLater(() -> vboxTracks.getChildren().clear());

                for (IngestCSVData data : csvData) {
                    try {
                        if (hideAssignedPayees && payeeDetails.get(data.getIsrc()) != null) {
                            continue;
                        } else {
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

                                // Set payee details if available
                                Payee payee = payeeDetails.get(data.getIsrc());
                                if (payee != null) {
                                    lblPayee01.setText(payee.getPayee1() + " (" + payee.getShare1() + "%)");
                                    lblPayee01.setStyle("-fx-text-fill: '#72a276'");
                                    lblPayee02.setText(payee.getPayee2() + " (" + payee.getShare2() + "%)");
                                    lblPayee02.setStyle("-fx-text-fill: '#72a276'");
                                }

                                vboxTracks.getChildren().add(node);
                            });

                            payeeUpdaterUIS.add(new PayeeUpdaterUI(checkBox, lblContributor01, lblContributor02, lblISRC, lblPayee01, lblPayee02, lblTrackName, data));

                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", "Something went wrong when initializing UI: " + e));
                        break;
                    }
                }

                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() -> {
            task.getException().printStackTrace();
            btnAssignPayees.setText("Assign Payees");
            btnAssignPayees.setDisable(false);
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", "Something went wrong when initializing UI: " + task.getException());
        }));

        Thread thread = new Thread(task);
        thread.start();
    }
}
