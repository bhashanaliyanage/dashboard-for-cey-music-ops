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
import java.util.Scanner;
import java.util.stream.Stream;

public class Database {
    private static Connection conn = null;

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:songs.db");
        }
        return conn;
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

    public static void updateBase(File file) throws SQLException, ClassNotFoundException, IOException {
        Connection db = Database.getConn();
        Scanner sc = new Scanner(new File(file.getAbsolutePath()));
        sc.useDelimiter(",");

        PreparedStatement ps = db.prepareStatement("UPDATE 'songData'" +
                "SET FILE_NAME = ?" +
                "WHERE ISRC = ?");

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line; // Test

        while ((line = reader.readLine()) != null) {
            String[] columnNames = line.split(",");

            try {
                if (columnNames.length > 0) {
                    ps.setString(1, columnNames[12]);
                    System.out.println(columnNames[12]);
                    ps.setString(2, columnNames[0]);

                    ps.executeUpdate();
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
    }

    public static void ImportToBase(File file) throws SQLException, ClassNotFoundException, IOException {
        Connection db = Database.getConn();
        Scanner sc = new Scanner(new File(file.getAbsolutePath()));
        sc.useDelimiter(",");

        PreparedStatement ps = db.prepareStatement("INSERT INTO 'songData' (ISRC," +
                "ALBUM_TITLE," +
                "UPC," +
                "CAT_NO," +
                "PRODUCT_PRIMARY," +
                "ALBUM_FORMAT," +
                "TRACK_TITLE," +
                "TRACK_VERSION," +
                "SINGER," +
                "FEATURING," +
                "COMPOSER," +
                "LYRICIST," +
                "FILE_NAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line; // Test

        while ((line = reader.readLine()) != null) {
            String[] columnNames = line.split(",");

            try {
                if (columnNames.length > 0) {
                    ps.setString(1, columnNames[0]);
                    ps.setString(2, columnNames[1]);
                    ps.setString(3, columnNames[2]);
                    ps.setString(4, columnNames[3]);
                    ps.setString(5, columnNames[4]);
                    ps.setString(6, columnNames[5]);
                    ps.setString(7, columnNames[6]);
                    ps.setString(8, columnNames[7]);
                    ps.setString(9, columnNames[8]);
                    ps.setString(10, columnNames[9]);
                    ps.setString(11, columnNames[10]);
                    ps.setString(12, columnNames[11]);
                    ps.setString(13, columnNames[12]);

                    ps.executeUpdate();
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
    }

    public static void SearchSongsFromDB(String[] ISRCCodes, File directory, File destination) throws SQLException, ClassNotFoundException, IOException {
        Connection db = Database.getConn();
        ResultSet rs;
        String filename;
        Path tempDir = destination.toPath();
        String status = null;

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
                        System.out.println("File copied to: " + targetFile.toString());
                    } else {
                        System.out.println("File not found.");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error", "An error occurred during file copy.", e.getMessage().toString() + "\n Please consider using an accessible location");
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static void SearchSongsFromAudioLibrary(File directory) {
        Path start = Paths.get(directory.toURI());
    }


}
