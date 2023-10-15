package com.example.song_finder_fx;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;

public class NotificationBuilder {
    /*public static void main(String[] args) throws AWTException {
        if (SystemTray.isSupported()) {
            NotificationBuilder nb = new NotificationBuilder();
            nb.displayTrayInfo("Test", "Test");
        } else {
            System.err.println("System tray not supported!");
        }
    }*/

    public void displayTrayInfo(String caption, String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        // Image image = Toolkit.getDefaultToolkit().createImage("com/example/song_finder_fx/icons/icon.png");

        Image image = new ImageIcon("com/example/song_finder_fx/icons/icon.png").getImage();

        TrayIcon trayIcon = new TrayIcon(image, "CeyMusic Toolkit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("CeyMusic Toolkit");
        tray.add(trayIcon);

        trayIcon.displayMessage(caption, message, MessageType.INFO);
    }

    public void displayTrayError(String caption, String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = new ImageIcon("com/example/song_finder_fx/icons/icon.png").getImage();

        TrayIcon trayIcon = new TrayIcon(image, "CeyMusic Toolkit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("CeyMusic Toolkit");
        tray.add(trayIcon);

        trayIcon.displayMessage(caption, message, MessageType.ERROR);
    }

    public void build(String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = new ImageIcon("com/example/song_finder_fx/icons/icon.png").getImage();

        TrayIcon trayIcon = new TrayIcon(image, "CeyMusic Toolkit");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("CeyMusic Toolkit");
        tray.add(trayIcon);

        trayIcon.displayMessage("CeyMusic Toolkit", message, MessageType.INFO);
    }
}
