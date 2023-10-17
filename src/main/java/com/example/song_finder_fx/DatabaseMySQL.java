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
import java.util.List;
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
                "UPC BIGINT," +
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
                        System.out.println("Executed: " + columnNames[2]);
                    } catch (DataTruncation | SQLIntegrityConstraintViolationException |
                             ArrayIndexOutOfBoundsException e) {
                        System.out.println("Invalid: " + columnNames[2]);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
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

    public List<Songs> searchSongNames(String searchText) throws SQLException, ClassNotFoundException {
        List<Songs> songs = new ArrayList<>();
        ResultSet rs;

        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT TRACK_TITLE, ISRC FROM songs WHERE TRACK_TITLE LIKE ? LIMIT 15");
        ps.setString(1, searchText + "%");
        rs = ps.executeQuery();

        while (rs.next()) {
            songs.add(new Songs(rs.getString(1), rs.getString(2)));
        }

        try {
            // Printing Searched Content
            System.out.println(songs.get(0).getISRC().trim() + " | " + songs.get(0).getSongName());
            System.out.println(songs.get(1).getISRC().trim() + " | " + songs.get(1).getSongName());
            System.out.println(songs.get(2).getISRC().trim() + " | " + songs.get(2).getSongName());

            // Printing new line
            System.out.println("================");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("End of results");
        }

        return songs;
    }

    public List<String> searchSongDetails(String isrc) throws SQLException, ClassNotFoundException {
        // Songs songDetails = new Songs();
        ResultSet rs;
        List<String> songDetails = new ArrayList<>();

        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT ISRC, " +
                "ALBUM_TITLE, " +
                "UPC, " +
                "TRACK_TITLE, " +
                "SINGER, " +
                "FEATURING, " +
                "COMPOSER, " +
                "LYRICIST, " +
                "FILE_NAME FROM songs WHERE ISRC = ? LIMIT 1");
        ps.setString(1, isrc);
        rs = ps.executeQuery();

        while (rs.next()) {
            String isrcFromDatabase = rs.getString(1);
            String albumTitle = rs.getString(2);
            String upc = rs.getString(3);
            String trackTitle = rs.getString(4);
            String singer = rs.getString(5);
            String featuringArtist = rs.getString(6);
            String composer = rs.getString(7);
            String lyricist = rs.getString(8);
            String fileName = rs.getString(9);
            songDetails.add(isrcFromDatabase);
            songDetails.add(albumTitle);
            songDetails.add(upc);
            songDetails.add(trackTitle);
            songDetails.add(singer);
            songDetails.add(featuringArtist);
            songDetails.add(composer);
            songDetails.add(lyricist);
            songDetails.add(fileName);
            // System.out.println("Here");
            // songDetails.songDetails(isrcFromDatabase, albumTitle, upc, trackTitle, singer, featuringArtist, composer, lyricist, fileName);
        }

        return songDetails;
    }
}
