package com.example.song_finder_fx.Controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

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
}
