package com.example.song_finder_fx;

import javafx.scene.control.Alert;
import org.sqlite.SQLiteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

public class Database {
    private static final Connection conn = null;

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        Connection connection;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite::resource:songs.db");
        return connection;
    }

    public static void CreateBase() throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Connection db = Database.getConn();

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS songData (" +
                "ISRC TEXT PRIMARY KEY," +
                "ALBUM_TITLE TEXT," +
                "UPC INTEGER," +
                "CAT_NO TEXT," +
                "PRODUCT_PRIMARY TEXT," +
                "ALBUM_FORMAT TEXT," +
                "TRACK_TITLE TEXT," +
                "TRACK_VERSION TEXT," +
                "SINGER TEXT," +
                "FEATURING TEXT," +
                "COMPOSER TEXT," +
                "LYRICIST TEXT," +
                "FILE_NAME TEXT)");

        ps.executeUpdate();
    }

    public static void createTableForAudioDatabaseLocation() throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Connection db = Database.getConn();

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS directory_paths (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PATH TEXT)");

        ps.executeUpdate();
    }

    public static void SearchSongsFromDB(String[] ISRCCodes, File directory, File destination) throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();
        ResultSet rs;
        String filename;

        PreparedStatement ps = db.prepareStatement("SELECT FILE_NAME " +
                "FROM songData " +
                "WHERE ISRC = ?");

        for (String ISRCCode : ISRCCodes) {
            ps.setString(1, ISRCCode);
            rs = ps.executeQuery();

            while (rs.next()) {
                filename = rs.getString("FILE_NAME");
                System.out.println("File Name: " + filename);
                Path start = Paths.get(directory.toURI());

                try (Stream<Path> stream = Files.walk(start)) {
                    String finalFilename = filename;
                    Path file = stream
                            .filter(path -> path.toFile().isFile())
                            .filter(path -> path.getFileName().toString().equals(finalFilename))
                            .findFirst()
                            .orElse(null);

                    if (file != null) {
                        Path targetDir = destination.toPath();
                        Path targetFile = targetDir.resolve(finalFilename);
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File copied to: " + targetFile);
                    } else {
                        System.out.println("File not found.");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error", "An error occurred during file copy.", e.getMessage() + "\n Please consider using an accessible location");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        // TODO: 12/15/2023 Change this to a dialog (See check missing ISRC function)
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static String searchForAudioDB() throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();
        ResultSet rs;
        String path = null;

        PreparedStatement ps = db.prepareStatement("SELECT PATH " +
                "FROM directory_paths " +
                "WHERE ID = 1");

        rs = ps.executeQuery();
        while (rs.next()) {
            path = rs.getString("PATH");
        }

        return path;
    }

    public static ResultSet getSongList() throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();
        ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT SONG, CONTROL, COPYRIGHT_OWNER FROM 'list_temp'");
        rs = ps.executeQuery();

        return rs;
    }

    public static Boolean saveDirectory(String directoryString) throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();
        int status;

        String path = searchForAudioDB();

        if (path == null) {
            PreparedStatement ps = db.prepareStatement("INSERT INTO directory_paths (PATH) VALUES (?);");
            ps.setString(1, directoryString);
            status = ps.executeUpdate();
        } else {
            PreparedStatement ps = db.prepareStatement("UPDATE directory_paths SET PATH = ? WHERE ID = 1;");
            ps.setString(1, directoryString);
            status = ps.executeUpdate();
        }

        return status > 0;
    }

    public static boolean handleSongListTemp(String song, String control, String copyrightOwner) throws SQLException, ClassNotFoundException {
        makeTableSongList();

        Connection db = Database.getConn();

        PreparedStatement ps = db.prepareStatement("INSERT INTO 'list_temp' (SONG," +
                "CONTROL," +
                "COPYRIGHT_OWNER) VALUES (?, ?, ?)"
        );

        ps.setString(1, song);
        ps.setString(2, control);
        ps.setString(3, copyrightOwner);

        int rs = ps.executeUpdate();

        return rs > 0;
    }



    private static void makeTableSongList() throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Connection db = Database.getConn();

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS list_temp (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SONG TEXT," +
                "CONTROL TEXT," +
                "COPYRIGHT_OWNER TEXT)"
        );

        ps.executeUpdate();
    }

    public static boolean emptyTableSongListTemp() throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();

        PreparedStatement checkTable = db.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='list_temp';");
        ResultSet resultSet = checkTable.executeQuery();

        if (resultSet.next()) {
            PreparedStatement ps = db.prepareStatement("DELETE FROM list_temp");
            int rs = ps.executeUpdate();
            return rs > 0;
        } else {
            System.out.println("Table does not exist");
            return false;
        }
    }

    public static boolean changePercentage(String songName, String percentage) throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();

        PreparedStatement preparedStatement = db.prepareStatement("UPDATE 'list_temp' SET CONTROL = ? WHERE SONG = ?");
        preparedStatement.setString(1, percentage);
        preparedStatement.setString(2, songName);
        int rs = preparedStatement.executeUpdate();
        return rs > 0;
    }


    public static double calculateTotalDue(double amountPerItem) throws SQLException, ClassNotFoundException {
        Connection db = Database.getConn();
        PreparedStatement ps = db.prepareStatement("SELECT CONTROL FROM 'list_temp'");
        ResultSet rs = ps.executeQuery();
        double total = 0.00;

        while (rs.next()) {
            String control = rs.getString("CONTROL");
            if (Objects.equals(control, "50%")) {
                total += amountPerItem;
            } else {
                total += (amountPerItem * 2);
            }
        }

        return total;
    }
}
