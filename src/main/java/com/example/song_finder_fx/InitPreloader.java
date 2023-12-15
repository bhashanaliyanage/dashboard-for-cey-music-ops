package com.example.song_finder_fx;

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
    public static Double PRODUCT_VERSION;
    public static String updateLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblLoadingg = lblLoading;
    }

    public void checkFunctions() throws InterruptedException {
        final String[] con = new String[1];
        final String[] message = {""};
        final String[] connection = {null};
        Thread connectionCheck = new Thread(() -> {
            message[0] = "Checking Connection";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                connection[0] = SheetsCOn.checkAccess(lblLoadingg);
                if (Objects.equals(connection[0], "1")) {
                    System.out.println("Access Granted");
                    Platform.runLater(() -> lblLoadingg.setText("Access Granted"));
                } else {
                    System.out.println("Unable to access");
                    Platform.runLater(() -> lblLoadingg.setText("Unable to access"));
                }
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
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

        Thread audioDatabaseCheck = new Thread(() -> {
            message[0] = "Getting Audio Database";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                Main.getDirectoryFromDB();
            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("Cannot get audio database directory");
            }
        });

        Thread revenueAnalysisCheck = new Thread(() -> {
            message[0] = "Loading Revenue Analysis";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

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

        Thread updatesCheck = new Thread(() -> {
            message[0] = "Checking for Updates";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                ResultSet versionDetails = DatabaseMySQL.checkUpdates();
                if (versionDetails != null) {
                    versionDetails.next();
                    PRODUCT_VERSION = versionDetails.getDouble(1);
                    System.out.println("versionDetails = " + versionDetails.getDouble(1));
                    updateLocation = versionDetails.getString(2);
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Thread mainWindow = new Thread(() -> Platform.runLater(() -> {
            try {
                starting = true;
                Parent root = FXMLLoader.load(getClass().getResource("layouts/main-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                VBox main = (VBox) scene.lookup("#mainVBox");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/search-details.fxml"));
                Parent newContent = loader.load();
                main.getChildren().add(newContent);

                stage.setTitle("CeyMusic Toolkit v" + Main.PRODUCT_VERSION);
                Image image = new Image("com/example/song_finder_fx/icons/icon.png");
                stage.getIcons().add(image);
                stage.setMinWidth(1300);
                stage.setMinHeight(800);
                stage.setScene(scene);
                stage.show();

                if ((PRODUCT_VERSION != null) && (Main.PRODUCT_VERSION < PRODUCT_VERSION)) {
                    System.out.println("Main.PRODUCT_VERSION = " + Main.PRODUCT_VERSION);
                    System.out.println("PRODUCT_VERSION = " + PRODUCT_VERSION);
                    Label updateNotify = (Label) scene.lookup("#lblUserEmailAndUpdate");
                    updateNotify.setText("Update Available");
                    updateNotify.setStyle("-fx-text-fill: '#FEA82F'");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        // connectionCheck.start();
        // connectionCheck.join();

        databaseCheck.start();
        databaseCheck.join();
        audioDatabaseCheck.start();
        audioDatabaseCheck.join();

        /*if (connection[0] != null) {
            if (connection[0].equals("1")) {
            }
        }*/

        if (Objects.equals(con[0], "Connection Succeed")) {
            revenueAnalysisCheck.start();
            revenueAnalysisCheck.join();
            updatesCheck.start();
            updatesCheck.join();
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
