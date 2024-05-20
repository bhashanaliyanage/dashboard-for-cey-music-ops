package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.Main;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NotificationBuilder {
    public static void displayTrayInfo(String caption, String message) throws AWTException {
        /*SystemTray tray = SystemTray.getSystemTray();

        Image image = new ImageIcon("com/example/song_finder_fx/icons/icon.png").getImage();

        TrayIcon trayIcon = new TrayIcon(image, "CeyMusic Toolkit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("CeyMusic Toolkit");
        tray.add(trayIcon);*/

        Main.trayIcon.displayMessage(caption, message, MessageType.INFO);
    }

    public static void displayTrayError(String caption, String message) throws AWTException {
        /*SystemTray tray = SystemTray.getSystemTray();

        Image image = new ImageIcon("com/example/song_finder_fx/icons/icon.png").getImage();

        TrayIcon trayIcon = new TrayIcon(image, "CeyMusic Toolkit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("CeyMusic Toolkit");
        tray.add(trayIcon);*/

        Main.trayIcon.displayMessage(caption, message, MessageType.ERROR);
    }
}
