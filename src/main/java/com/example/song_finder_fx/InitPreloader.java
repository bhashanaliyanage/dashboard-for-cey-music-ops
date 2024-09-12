package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.GitHubController;
import com.example.song_finder_fx.Controller.NotificationBuilder;
import com.example.song_finder_fx.Model.ReleaseInfo;
import com.example.song_finder_fx.Model.Revenue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.example.song_finder_fx.Main.trayIcon;

public class InitPreloader implements Initializable {
    @FXML
    public Label lblLoading;

    @FXML
    private Label lblBuildNumber;

    public static Label lblLoadingg;

    public static Label lblBuildNumberr;

    public static boolean starting;

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
            final int MAX_ATTEMPTS = 3;
            final int DELAY_SECONDS = 3;

            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                if (attempt == 1) {
                    message[0] = "Connecting to CeyMusic Database";
                } else {
                    message[0] = "Retrying Connection to CeyMusic Database (Attempt " + attempt + " of " + MAX_ATTEMPTS + ")";
                }

                Platform.runLater(() -> lblLoadingg.setText(message[0]));

                con[0] = checkDatabaseConnection();
                System.out.println("con = " + con[0]);

                if (!Objects.equals(con[0], "Connection Error")) {
                    // Connection successful, exit the loop
                    break;
                }

                if (attempt < MAX_ATTEMPTS) {
                    // If it's not the last attempt, show countdown before retrying
                    for (int i = DELAY_SECONDS; i > 0; i--) {
                        final int secondsLeft = i;
                        Platform.runLater(() -> lblLoadingg.setText("Retrying Connection to CeyMusic Database in " + secondsLeft + " second" + (secondsLeft != 1 ? "s" : "")));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println("Something went wrong when trying to sleep the thread: " + e);
                            // e.printStackTrace();
                        }
                    }
                } else {
                    // If it's the last attempt and still failed, show error alert
                    Platform.runLater(() -> AlertBuilder.sendErrorAlert("Error", "Database Offline", "Dashboard cannot connect to database after " + MAX_ATTEMPTS + " attempts"));
                }
            }
        });

        Thread audioDatabaseCheck = new Thread(() -> {
            message[0] = "Checking Local Audio Database";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                Main.getAudioDatabaseLocation();
            } catch (SQLException | ClassNotFoundException e) {
                Platform.runLater(() -> System.out.println("Cannot get audio database directory"));
            }
        });

        Thread updatesCheck = new Thread(() -> {
            message[0] = "Checking for Updates";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            // ResultSet versionDetails = DatabaseMySQL.checkUpdates();
            String owner = "bhashanaliyanage";
            String repo = "dashboard-for-cey-music-ops";
            GitHubController controller = new GitHubController(owner, repo);

            // Check Update
            ReleaseInfo releaseInfo = controller.getLatestVersion();
            // boolean updateAvailable = Main.versionInfo.updateAvailable();
            // Updates versionDetailsNew = DatabasePostgres.checkUpdatesNew();        //Connection for Postgress

            // updateLocation = versionDetailsNew.getLocation();
            // versionDetailsNew.getDetails()
            Platform.runLater(() -> System.out.println("releaseInfo.version = " + releaseInfo.version));
            Main.versionInfo.setServerVersion(releaseInfo.version, releaseInfo.releaseNotes);

        });

        Thread loadScenes = new Thread(() -> {
            message[0] = "Loading Scenes";

            Platform.runLater(() -> lblLoadingg.setText(message[0]));

            try {
                UIController.setAllScenes();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread mainWindow = new Thread(() -> Platform.runLater(() -> {
            try {
                starting = true;

                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/main-view.fxml")));
                Scene scene = new Scene(root);

                Stage mainWindowStage = setupStage();

                setupSceneElements(scene, mainWindowStage);

                // Set Tray Icon

                Platform.runLater(() -> {
                    try {
                        SystemTray tray = SystemTray.getSystemTray();

                        // Loading Font
                        Font defaultFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/com/example/song_finder_fx/fonts/Poppins-Regular.ttf")).deriveFont(12f);

                        // Loading Tray Icon Image
                        String imagePath = "src/main/resources/com/example/song_finder_fx/icons/icon (Custom).png";
                        BufferedImage myImage = ImageIO.read(new File(imagePath));
                        trayIcon = new TrayIcon(myImage);

                        // Setting Menu Items
                        MenuItem dash = new MenuItem("Dashboard");
                        dash.setFont(defaultFont);
                        dash.addActionListener(event -> Platform.runLater(mainWindowStage::show));

                        MenuItem sidebar = new MenuItem("Open Side Bar");
                        sidebar.addActionListener(event -> System.out.println("Side Bar Button Click"));
                        sidebar.setFont(defaultFont);
                        sidebar.setEnabled(false);

                        MenuItem exit = new MenuItem("Exit Dashboard");
                        exit.addActionListener(event -> {
                            Platform.exit();
                            System.exit(0);
                            tray.remove(trayIcon);
                        });
                        exit.setFont(defaultFont);

                        // Setting Tooltip
                        trayIcon.setToolTip("CeyMusic Dashboard");

                        PopupMenu popupMenu = new PopupMenu("Menu");
                        popupMenu.add(dash);
                        popupMenu.add(sidebar);
                        popupMenu.addSeparator();
                        popupMenu.add(exit);
                        trayIcon.setPopupMenu(popupMenu);

                        trayIcon.addActionListener(ActionEvent -> Platform.runLater(mainWindowStage::show));

                        tray.add(trayIcon);
                    } catch (AWTException | FontFormatException | IOException e) {
                        System.out.println("System Tray Error: " + e);
                    }
                });

                // Platform.runLater(() -> setupSystemTray(mainWindowStage));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        databaseCheck.start();
        databaseCheck.join();
        audioDatabaseCheck.start();
        audioDatabaseCheck.join();
        updatesCheck.start();
        updatesCheck.join();
        loadScenes.start();
        loadScenes.join();
        mainWindow.start();
        mainWindow.join();
    }

    private static @NotNull Stage setupStage() {
        Stage mainWindowStage = new Stage();
        mainWindowStage.setTitle("CeyMusic Toolkit " + Main.versionInfo.getCurrentVersionNumber() + " (beta)");
        Image image = new Image("com/example/song_finder_fx/icons/icon.png");
        mainWindowStage.getIcons().add(image);
        mainWindowStage.setWidth(1350);
        mainWindowStage.setHeight(900);
        return mainWindowStage;
    }

    private void setupSceneElements(Scene scene, Stage mainWindowStage) {
        VBox main = (VBox) scene.lookup("#mainVBox");
        VBox leftVBox = (VBox) scene.lookup("#leftVBox");
        HBox hboxAbout = (HBox) scene.lookup("#hboxAbout");
        Label lblSearch = (Label) scene.lookup("#lblSearch");
        Label lblSearchNCollect = (Label) scene.lookup("#lblSearchNCollect");
        Label lblRevenueAnalysis = (Label) scene.lookup("#lblRevenueAnalysis");
        Label lblArtistReports = (Label) scene.lookup("#lblArtistReports");
        Label lblSettings = (Label) scene.lookup("#lblSettings");
        VBox vboxSongList = (VBox) scene.lookup("#vboxSongList");
        VBox btnDatabaseCheck = (VBox) scene.lookup("#btnDatabaseCheck");
        VBox btnDatabaseCheck2 = (VBox) scene.lookup("#btnDatabaseCheck2");

        main.getChildren().add(UIController.mainNodes[2]);

        Platform.setImplicitExit(false);

        mainWindowStage.setScene(scene);
        mainWindowStage.show();

        mainWindowStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

        if (Main.versionInfo.updateAvailable()) {
            Label updateNotify = (Label) scene.lookup("#lblUserEmailAndUpdate");
            updateNotify.setText("Update Available");
            updateNotify.setStyle("-fx-text-fill: '#FEA82F'");
        }

        setupResizeListener(mainWindowStage, leftVBox, hboxAbout, lblSearch, lblSearchNCollect, lblRevenueAnalysis, lblArtistReports, lblSettings, vboxSongList, btnDatabaseCheck, btnDatabaseCheck2);
    }

    private static void setupResizeListener(Stage mainWindowStage, VBox leftVBox, HBox hboxAbout, Label lblSearch, Label lblSearchNCollect, Label lblRevenueAnalysis, Label lblArtistReports, Label lblSettings, VBox vboxSongList, VBox btnDatabaseCheck, VBox btnDatabaseCheck2) {
        mainWindowStage.widthProperty().addListener((obs, oldVal, newVal) -> {
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
                // lblSearchNCollect.setVisible(true);
                lblRevenueAnalysis.setVisible(true);
                lblArtistReports.setVisible(true);
                lblSettings.setVisible(true);
                vboxSongList.setVisible(true);
                btnDatabaseCheck.setVisible(true);
                btnDatabaseCheck2.setVisible(true);
                // borderPane.setLeft(UIController.mainNodes[4]);
            }
        });
    }

    private void closeWindowEvent(WindowEvent windowEvent) {
        Platform.runLater(() -> {
            NotificationBuilder.displayTrayInfo("CeyMusic Dashboard", "CeyMusic Dashboard is minimized to system tray");
        });
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
