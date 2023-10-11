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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class DatabaseMySQL {
    private static Connection conn = null;

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/songData";
            String username = "ceymusic";
            String password = "ceymusic";
            conn = DriverManager.getConnection(url, username, password);
        }
        return conn;
    }

    public void CreateBase() throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Connection db = DatabaseMySQL.getConn();
        int rs;

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS songs (" +
                "ISRC VARCHAR(255) PRIMARY KEY," +
                "ALBUM_TITLE VARCHAR(255)," +
                "UPC INT," +
                "CAT_NO VARCHAR(255)," +
                "PRODUCT_PRIMARY VARCHAR(255)," +
                "ALBUM_FORMAT VARCHAR(255)," +
                "TRACK_TITLE VARCHAR(255)," +
                "TRACK_VERSION VARCHAR(255)," +
                "SINGER VARCHAR(255)," +
                "FEATURING VARCHAR(255)," +
                "COMPOSER VARCHAR(255)," +
                "LYRICIST VARCHAR(255)," +
                "FILE_NAME VARCHAR(255))");

        rs = ps.executeUpdate();
        System.out.println(rs);
    }

    public void ImportToBase(File file) throws SQLException, ClassNotFoundException, IOException {
        Connection db = DatabaseMySQL.getConn();
        Scanner sc = new Scanner(new File(file.getAbsolutePath()));
        sc.useDelimiter(",");

        PreparedStatement ps = db.prepareStatement("INSERT INTO songs (ISRC," +
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
                    System.out.println(columnNames[2]);
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

                    try {
                        ps.executeUpdate();
                    } catch (DataTruncation | SQLIntegrityConstraintViolationException e) {
                        System.out.println("Invalid or Duplicate entry found");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static void SearchSongsFromAudioLibrary(File directory) {
        Path start = Paths.get(directory.toURI());
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

    public ArrayList<String> searchSongNames(String searchText) throws SQLException, ClassNotFoundException {
        ArrayList<String> songs = new ArrayList<>();
        ResultSet rs;

        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT TRACK_TITLE, ISRC FROM songs WHERE TRACK_TITLE LIKE ? LIMIT 3");
        ps.setString(1, "%" + searchText + "%");

        System.out.println("Here");
        rs = ps.executeQuery();

        while (rs.next()) {
            String trackTitle = rs.getString(2) + " | " + rs.getString(1);
            songs.add(trackTitle);
        }

        System.out.println(songs);

        return songs;
    }


}
