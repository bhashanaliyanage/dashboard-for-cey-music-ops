package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Revenue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InitPreloader implements Initializable {
    @FXML
    public Label lblLoading;

    @FXML
    private Label lblBuildNumber;

    public static Label lblLoadingg;

    public static Label lblBuildNumberr;

    public static boolean starting;

    public static String updateLocation;

    public static Revenue revenue = new Revenue();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblLoadingg = lblLoading;
        lblBuildNumberr = lblBuildNumber;
        lblBuildNumberr.setText(Main.versionInfo.getCurrentVersionInfo());
    }

    public void checkFunctions() throws InterruptedException {
        final String[] con = new String[1];
        final String[] message = {""};

        Thread databaseCheck = new Thread(() -> {
            message[0] = "Checking Database Connection";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            con[0] = checkDatabaseConnection();
            System.out.println("con = " + con[0]);

            if (Objects.equals(con[0], "Connection Error")) {
                Platform.runLater(() -> lblLoadingg.setText("Unable to connect to CeyMusic Database"));
            }
        });

        Thread audioDatabaseCheck = new Thread(() -> {
            message[0] = "Getting Audio Database";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                Main.getAudioDatabaseLocation();
            } catch (SQLException | ClassNotFoundException e) {
                Platform.runLater(() -> System.out.println("Cannot get audio database directory"));
            }
        });

        Thread revenueAnalysisCheck = new Thread(() -> {
            message[0] = "Loading Revenue Analysis";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            ResultSet top5Territories = null;
            ResultSet top4DSPs = null;
            String assetCount = null;
            ResultSet top5StreamedAssets = null;
            String salesDate;
            String month = null;
            try {
                top5Territories = DatabasePostgres.getTop5Territories();
                top4DSPs = DatabasePostgres.getTop4DSPs();
                assetCount = DatabasePostgres.getTotalAssetCount();
                top5StreamedAssets = DatabasePostgres.getTop5StreamedAssets();
                salesDate = DatabasePostgres.getSalesDate();
                String[] date = salesDate.split("-");
                month = date[1];
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred");
                alert.setContentText(String.valueOf(e));
                Platform.runLater(alert::showAndWait);
            }

            revenue.setValues(top5Territories, top4DSPs, assetCount, top5StreamedAssets, month);
        });

        Thread updatesCheck = new Thread(() -> {
            message[0] = "Checking for Updates";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                ResultSet versionDetails = DatabaseMySQL.checkUpdates();
                // ResultSet versionDetails = DatabasePostgres.checkUpdates();        //Connection for Postgress

                if (versionDetails != null) {
                    versionDetails.next();
                    System.out.println("versionDetails = " + versionDetails.getDouble(1));
                    updateLocation = versionDetails.getString(2);
                    Main.versionInfo.setServerVerion(versionDetails.getDouble(1), versionDetails.getString(2));
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread loadScenes = new Thread(() -> {
            message[0] = "Loading Scenes";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                UIController.setAllScenes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread mainWindow = new Thread(() -> Platform.runLater(() -> {
            try {
                starting = true;
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/main-view.fxml")));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                VBox main = (VBox) scene.lookup("#mainVBox");
                VBox leftVBox = (VBox) scene.lookup("#leftVBox");
                // vboxAbout
                HBox hboxAbout = (HBox) scene.lookup("#hboxAbout");
                // lblSearch
                Label lblSearch = (Label) scene.lookup("#lblSearch");
                // lblSearchNCollect
                Label lblSearchNCollect = (Label) scene.lookup("#lblSearchNCollect");
                // lblRevenueAnalysis
                Label lblRevenueAnalysis = (Label) scene.lookup("#lblRevenueAnalysis");
                // lblArtistReports
                Label lblArtistReports = (Label) scene.lookup("#lblArtistReports");
                // lblSettings
                Label lblSettings = (Label) scene.lookup("#lblSettings");
                // vboxSongList
                VBox vboxSongList = (VBox) scene.lookup("#vboxSongList");
                // (VBox) btnDatabaseCheck
                VBox btnDatabaseCheck = (VBox) scene.lookup("#btnDatabaseCheck");
                // (VBox) btnDatabaseCheck2 (MediaPlayer)
                VBox btnDatabaseCheck2 = (VBox) scene.lookup("#btnDatabaseCheck2");

                main.getChildren().add(UIController.mainNodes[2]);

                stage.setTitle("CeyMusic Toolkit v" + Main.versionInfo.getCurrentVersionNumber() + " (beta)");
                Image image = new Image("com/example/song_finder_fx/icons/icon.png");
                stage.getIcons().add(image);
                stage.setWidth(1350);
                stage.setHeight(900);
                stage.setScene(scene);
                stage.show();

                if (Main.versionInfo.updateAvailable()) {
                    Label updateNotify = (Label) scene.lookup("#lblUserEmailAndUpdate");
                    updateNotify.setText("Update Available");
                    updateNotify.setStyle("-fx-text-fill: '#FEA82F'");
                }

                stage.widthProperty().addListener((obs, oldVal, newVal) -> {
                    if ((oldVal.intValue() > newVal.intValue()) && (newVal.intValue() <= 1235)) {
                        leftVBox.setPrefWidth(100);
                        leftVBox.setMinWidth(100);
                        hboxAbout.setVisible(false);
                        lblSearch.setVisible(false);
                        lblSearchNCollect.setVisible(false);
                        lblRevenueAnalysis.setVisible(false);
                        lblArtistReports.setVisible(false);
                        lblSettings.setVisible(false);
                        vboxSongList.setVisible(false);
                        btnDatabaseCheck.setVisible(false);
                        btnDatabaseCheck2.setVisible(false);
                        // borderPane.setLeft(UIController.mainNodes[5]);
                    }

                    if ((oldVal.intValue() < newVal.intValue()) && (newVal.intValue() >= 1235)) {
                        leftVBox.setPrefWidth(293);
                        leftVBox.setMinWidth(293);
                        hboxAbout.setVisible(true);
                        lblSearch.setVisible(true);
                        lblSearchNCollect.setVisible(true);
                        lblRevenueAnalysis.setVisible(true);
                        lblArtistReports.setVisible(true);
                        lblSettings.setVisible(true);
                        vboxSongList.setVisible(true);
                        btnDatabaseCheck.setVisible(true);
                        btnDatabaseCheck2.setVisible(true);
                        // borderPane.setLeft(UIController.mainNodes[4]);
                    }
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        databaseCheck.start();
        databaseCheck.join();
        audioDatabaseCheck.start();
        audioDatabaseCheck.join();

        if (Objects.equals(con[0], "Connection Succeed")) {
            revenueAnalysisCheck.start();
            revenueAnalysisCheck.join();
            updatesCheck.start();
            updatesCheck.join();
            loadScenes.start();
            loadScenes.join();
            mainWindow.start();
            mainWindow.join();
        }

    }

    private String checkDatabaseConnection() {
        Connection con;
        String message;

        con = DatabasePostgres.getConn();

        if (con != null) {
            message = "Connection Succeed";
        } else {
            message = "Connection Error";
        }
        return message;
    }

    public void onCLoseBtnClick() {
        Platform.exit();
    }
}
