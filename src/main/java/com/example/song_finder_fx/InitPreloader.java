package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Revenue;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InitPreloader implements Initializable {
    public Label lblLoading;
    public static Label lblLoadingg;
    public static boolean starting;
    public static String updateLocation;
    public static Revenue revenue = new Revenue();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblLoadingg = lblLoading;
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
                /*Platform.runLater(() -> {
                    System.out.println("Here!");
                });*/
                Main.getDirectoryFromDB();
            } catch (SQLException | ClassNotFoundException e) {
                Platform.runLater(() -> System.out.println("Cannot get audio database directory"));
            }
        });

        Thread revenueAnalysisCheck = new Thread(() -> {
            message[0] = "Loading Revenue Analysis";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                ResultSet top5Territories = DatabaseMySQL.getTop5Territories();
                ResultSet top4DSPs = DatabaseMySQL.getTop4DSPs();
                String assetCount = DatabaseMySQL.getTotalAssetCount();
                ResultSet top5StreamedAssets = DatabaseMySQL.getTop5StreamedAssets();
                String salesDate = DatabaseMySQL.getSalesDate();
                String[] date = salesDate.split("-");
                String month = date[1];

                revenue.setValues(top5Territories, top4DSPs, assetCount, top5StreamedAssets, month);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread updatesCheck = new Thread(() -> {
            message[0] = "Checking for Updates";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                ResultSet versionDetails = DatabaseMySQL.checkUpdates();
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
                main.getChildren().add(UIController.mainNodes[2]);

                stage.setTitle("CeyMusic Toolkit v" + Main.versionInfo.getCurrentVersionNumber());
                Image image = new Image("com/example/song_finder_fx/icons/icon.png");
                stage.getIcons().add(image);
                stage.setMinWidth(1300);
                stage.setMinHeight(800);
                stage.setScene(scene);
                stage.show();

                if (Main.versionInfo.updateAvailable()) {
                    Label updateNotify = (Label) scene.lookup("#lblUserEmailAndUpdate");
                    updateNotify.setText("Update Available");
                    updateNotify.setStyle("-fx-text-fill: '#FEA82F'");
                }

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
        String message = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://192.168.1.200/songData";
            String username = "ceymusic";
            String password = "ceymusic";
            con = DriverManager.getConnection(url, username, password);

            if (con != null) {
                message = "Connection Succeed";
                return message;
            }
        } catch (SQLException | ClassNotFoundException e) {
            message = "Connection Error";
            return message;
        }

        return message;
    }

    public void onCLoseBtnClick() {
        Platform.exit();
    }
}
