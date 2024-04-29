package com.example.song_finder_fx.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserSettingsManager {
    private static final String CONFIG_FILE = "user-config.properties";

    public UserSettingsManager() {
        String userHome = System.getProperty("user.home");

        // Create the folder if not exists
        File appFolder = new File(userHome, "Documents/CeyMusic Dashboard");
        if (!appFolder.exists()) {
            if (appFolder.mkdirs()) {
                System.out.println("Created directory: " + appFolder.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + appFolder.getAbsolutePath());
                return;
            }
        }

        // Create the properties file (if it doesn't exist)
        File configFile = new File(appFolder, CONFIG_FILE);
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    System.out.println("Created properties file: " + configFile.getAbsolutePath());
                } else {
                    System.err.println("Failed to create properties file: " + configFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Properties loadUserSettings() {
        Properties properties = new Properties();
        try {
            File configFile = getUserConfigFile();
            properties.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void saveUserSettings(Properties properties) {
        try {
            File configFile = getUserConfigFile();
            properties.store(new FileOutputStream(configFile), "User Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getUserConfigFile() {
        String userHome = System.getProperty("user.home");
        File appFolder = new File(userHome, "Documents/CeyMusic Dashboard");
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
        return new File(appFolder, CONFIG_FILE);
    }

    public static void main(String[] args) {
        Properties userSettings = loadUserSettings();
        // Read or modify settings as needed
        userSettings.setProperty("adb", "W:\\audio");
        saveUserSettings(userSettings);
        System.out.println(userSettings.getProperty("adb"));
    }

    public String getADB() {
        Properties userSettings = loadUserSettings();
        // Read or modify settings as needed
        // userSettings.setProperty("adb", "W:\\audio");
        // saveUserSettings(userSettings);
        String adb = userSettings.getProperty("adb");
        if (adb == null) {
            return "";
        } else return adb;
    }
}
