package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.CatalogNumberGenerator;
import com.example.song_finder_fx.Controller.ISRCDispatcher;
import com.example.song_finder_fx.Controller.ISRCEncryptor;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ControllerIdentifiers {

    @FXML
    public TextArea taISRCs;

    @FXML
    public TextArea taEcOrDcISRCs;

    @FXML
    private ToggleGroup type;

    @FXML
    private Spinner<Integer> isrcCount;

    @FXML
    private TextArea isrcTextArea;

    @FXML
    private ComboBox<String> comboArtist;

    @FXML
    private Spinner<Integer> catNoCount;

    @FXML
    private TextArea catNoTextArea;

    @FXML
    void initialize() {
        Thread thread = new Thread(() -> {
            try {
                SpinnerValueFactory<Integer> isrcValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
                SpinnerValueFactory<Integer> catNoValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
                List<String> ceyMusicArtists = DatabasePostgres.getAllCeyMusicArtists();

                Platform.runLater(() -> {
                    isrcCount.setValueFactory(isrcValueFactory);
                    catNoCount.setValueFactory(catNoValueFactory);
                    comboArtist.getItems().addAll(ceyMusicArtists);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Failed to initialize identifiers", "Database Error: " + e));
                // AlertBuilder.sendErrorAlert("Error", "Failed to initialize identifiers", "Database Error: " + e);
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    void onGenerateISRC() {
        boolean confirmation = AlertBuilder.getSendConfirmationAlert("Confirm", "Are you sure you want to generate ISRCs?", null);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    ToggleButton selectedToggle = (ToggleButton) type.getSelectedToggle();
                    if (selectedToggle != null) {
                        String type = selectedToggle.getText();
                        int count = isrcCount.getValue();
                        ISRCDispatcher dispatcher = new ISRCDispatcher();

                        Platform.runLater(() -> {
                            System.out.println("Selected toggle: " + type);
                            System.out.println("Generating ISRCs");
                        });

                        if (count == 1) {
                            // Generate ISRC
                            String isrc = dispatcher.dispatchSingleISRC(type);
                            Platform.runLater(() -> isrcTextArea.setText(isrc));

                            String finalType = type;
                            if (Objects.equals(type, "SR New Artist")) {
                                finalType = "SR2";
                            } else if (Objects.equals(type, "SR Old Artist")) {
                                finalType = "SR";
                            }

                            dispatcher.updateLastISRC(isrc, finalType);
                        } else {
                            // Generate ISRCs
                            List<String> ISRCs = dispatcher.dispatchMultipleISRCs(count, type);
                            StringBuilder sb = new StringBuilder();
                            for (String isrc : ISRCs) {
                                sb.append(isrc).append("\n");
                            }
                            Platform.runLater(() -> isrcTextArea.setText(sb.toString()));
                        }
                    } else {
                        Platform.runLater(() -> AlertBuilder.sendInfoAlert("Info", "No toggle selected", "Please select the ISRC type you want"));
                    }
                } catch (SQLException e) {
                    AlertBuilder.sendErrorAlert("Error", "Failed to generate ISRC", "Database Error: " + e);
                }
                return null;
            }
        };

        if (confirmation) {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    void onGenerateCatNos() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String artist = comboArtist.getValue();
                    int count = catNoCount.getValue();
                    CatalogNumberGenerator generator = new CatalogNumberGenerator();

                    if (count == 1) {
                        String catNo = generator.generateCatalogNumber(artist);
                        generator.updateLastCatalogNumber(artist, catNo);
                        Platform.runLater(() -> catNoTextArea.setText(catNo));
                    } else if (count > 1) {
                        List<String> catNos = generator.generateCatalogNumbers(artist, count);
                        StringBuilder sb = new StringBuilder();
                        for (String catNo : catNos) {
                            sb.append(catNo).append("\n");
                        }
                        Platform.runLater(() -> catNoTextArea.setText(sb.toString()));
                    }
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Failed to generate Catalog Numbers", "Database Error: " + e));
                }

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void onEncryptISRCs() {
        // Get the input ISRCs from the TextArea
        String inputISRCs = taISRCs.getText().trim();

        // Split the ISRCs by new lines if there are multiple ISRCs
        String[] isrcList = inputISRCs.split("\n");

        StringBuilder encryptedISRCs = new StringBuilder();

        // Encrypt each ISRC
        for (String isrc : isrcList) {
            encryptedISRCs.append(ISRCEncryptor.encryptISRC(isrc)).append("\n");
        }

        // Set the encrypted ISRCs in the TextArea for output
        taEcOrDcISRCs.setText(encryptedISRCs.toString().trim());
    }

    @FXML
    public void onDecryptISRCs() {
        // Get the encrypted ISRCs from the TextArea
        String encryptedISRCs = taISRCs.getText().trim();

        // Split the ISRCs by new lines if there are multiple ISRCs
        String[] encryptedISRCList = encryptedISRCs.split("\n");

        StringBuilder decryptedISRCs = new StringBuilder();

        // Decrypt each ISRC
        for (String encryptedISRC : encryptedISRCList) {
            decryptedISRCs.append(ISRCEncryptor.decryptISRC(encryptedISRC)).append("\n");
        }

        // Set the decrypted ISRCs in the TextArea for output
        taEcOrDcISRCs.setText(decryptedISRCs.toString().trim());
    }
}
