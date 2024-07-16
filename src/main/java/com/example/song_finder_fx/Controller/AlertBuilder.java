package com.example.song_finder_fx.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertBuilder {
    public static void sendErrorAlert(String title, String header, String content) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            // Adding Icon to Alert Dialog
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            String icon = "com/example/song_finder_fx/icons/icon (Custom).png";
            stage.getIcons().add(new Image(icon));

            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);

            // Showing Alert
            Platform.runLater(alert::showAndWait);
        } catch (Exception e) {
            System.out.println("Error Sending Alert: " + content + "\n\n" + e);
        }
    }

    public static void sendInfoAlert(String title, String header, String content) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            // Adding Icon to Alert Dialog
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            String icon = "com/example/song_finder_fx/icons/icon (Custom).png";
            stage.getIcons().add(new Image(icon));

            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);

            // Showing Alert
            Platform.runLater(alert::showAndWait);
        } catch (Exception e) {
            System.out.println("Error Sending Alert: " + content + "\n\n" + e);
        }
    }

    public static boolean getSendConfirmationAlert(String ttitle, String headerText, String contentText) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        // Adding Icon to Alert Dialog
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        String icon = "com/example/song_finder_fx/icons/icon (Custom).png";
        stage.getIcons().add(new Image(icon));

        alert.setTitle(ttitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
