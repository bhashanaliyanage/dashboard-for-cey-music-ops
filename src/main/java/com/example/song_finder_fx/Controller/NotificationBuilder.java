package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Main;
import javafx.application.Platform;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationBuilder {
    public static void displayTrayInfo(String caption, String message) throws AWTException {
        Platform.runLater(() -> {
            Main.trayIcon.displayMessage(caption, message, MessageType.INFO);
            // Main.trayIcon.displayMessage(caption, message, MessageType.ERROR);
        });
    }

    public static void displayTrayError(String caption, String message) throws AWTException {
        Platform.runLater(() -> {
            // Main.trayIcon.displayMessage(caption, message, MessageType.INFO);
            Main.trayIcon.displayMessage(caption, message, MessageType.ERROR);
        });
    }
}
