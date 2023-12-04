package com.example.song_finder_fx;

import com.google.api.services.sheets.v4.Sheets;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
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
    public static ResultSet top5Territories;
    public static ResultSet top4DSPs;
    public static String count;
    public static ResultSet top5StreamedAssets;
    public static String salesDate;
    public static String[] date;
    public static String month;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblLoadingg = lblLoading;
    }

    public void checkFunctions() throws InterruptedException {
        final String[] con = new String[1];
        final String[] message = {""};
        final String[] connection = {null};
        // lblLoading.setText("Test");
        Thread connectionCheck = new Thread(() -> {
            message[0] = "Checking Connection";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                connection[0] = SheetsCOn.checkAccess();
                if (Objects.equals(connection[0], "1")) {
                    System.out.println("Access Granted");
                } else {
                    System.out.println("Unable to access");
                }
            } catch (GeneralSecurityException | IOException e) {
                Platform.runLater(() -> lblLoadingg.setText("Check Internet Connection"));
            }

            if (Objects.equals(connection[0], "0")) {
                Platform.runLater(() -> lblLoadingg.setText("Unable to validate credentials"));
            }
        });

        Thread databaseCheck = new Thread(() -> {
            message[0] = "Checking Database Connection";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            con[0] = checkDatabaseConnection();
            System.out.println("con = " + con[0]);

            if (Objects.equals(con[0], "Connection Error")) {
                Platform.runLater(() -> lblLoadingg.setText("Unable to connect to CeyMusic Database"));
            }
        });

        Thread revenueAnalysisCheck = new Thread(() -> {
            message[0] = "Loading Revenue Analysis";

            Platform.runLater(() -> {
                lblLoadingg.setText(message[0]);
            });

            try {
                top5Territories = DatabaseMySQL.getTop5Territories();
                top4DSPs = DatabaseMySQL.getTop4DSPs();
                count = DatabaseMySQL.getTotalAssetCount();
                top5StreamedAssets = DatabaseMySQL.getTop5StreamedAssets();
                salesDate = DatabaseMySQL.getSalesDate();
                date = salesDate.split("-");
                month = date[1];
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread mainWindow = new Thread(() -> Platform.runLater(() -> {
            try {
                starting = true;
                // Thread.sleep(1000);
                Parent root = FXMLLoader.load(getClass().getResource("layouts/main-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                stage.setTitle("CeyMusic Toolkit 2023.3");
                Image image = new Image("com/example/song_finder_fx/icons/icon.png");
                stage.getIcons().add(image);
                stage.setMinWidth(1100);
                stage.setMinHeight(700);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        connectionCheck.start();
        connectionCheck.join();

        if (connection[0] != null) {
            if (connection[0].equals("1")) {
                databaseCheck.start();
                databaseCheck.join();
            }
        }

        if (Objects.equals(con[0], "Connection Succeed")) {
            revenueAnalysisCheck.start();
            revenueAnalysisCheck.join();
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