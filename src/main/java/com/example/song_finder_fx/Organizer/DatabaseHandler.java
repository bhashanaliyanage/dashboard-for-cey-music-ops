package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Songs;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    // Database connection
    // private final Connection connection;

    public DatabaseHandler() {
        // Initialize the database connection
        // connection = DatabasePostgres.getConn();
    }

    public List<Songs> searchSongsByTitle(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();

        String sql = getSearchSQL_Query(excludeUGC);

        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    private static @NotNull String getSearchSQL_Query(boolean excludeUGC) {
        String sql;

        if (excludeUGC) {
            sql = "SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title " +
                    "FROM public.\"song_metadata_new\"" +
                    " WHERE song_name ILIKE ? AND type = 'O' ORDER BY type DESC LIMIT 15";
        } else {
            sql = "SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title " +
                    "FROM public.\"song_metadata_new\" WHERE song_name ILIKE ? ORDER BY type DESC LIMIT 15";
        }
        return sql;
    }

    public List<Songs> searchSongsByISRC(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE isrc ILIKE ? ORDER BY type DESC LIMIT 15");
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public List<Songs> searchSongsByComposer(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE composer ILIKE ? ORDER BY type DESC LIMIT 15");
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public List<Songs> searchSongsBySinger(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE singer ILIKE ? ORDER BY type DESC LIMIT 15");
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public List<Songs> searchSongsByLyricist(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE lyricist ILIKE ? ORDER BY type DESC LIMIT 15");
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public List<Songs> searchSongsByProductName(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE product_title ILIKE ? ORDER BY type DESC LIMIT 15");
            statement.setString(1, criteria + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public List<Songs> searchSongsAcrossAllFields(String criteria, boolean excludeUGC) {
        List<Songs> searchResults = new ArrayList<>();

        String sql = getUnifiedSearchSQL_Query(excludeUGC);

        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement(sql);
            String searchParam = "%" + criteria + "%"; // Partial match with wildcards
            statement.setString(1, searchParam);
            statement.setString(2, searchParam);
            statement.setString(3, searchParam);
            statement.setString(4, searchParam);
            statement.setString(5, searchParam);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Create Song objects from the ResultSet and add them to the searchResults list
                Songs song = new Songs();
                song.setISRC(resultSet.getString(1));
                song.setTrackTitle(resultSet.getString(2));
                System.out.println("Song Name: " + song.getTrackTitle());
                song.setFileName(resultSet.getString(3));
                song.setUPC(resultSet.getString(4));
                song.setComposer(resultSet.getString(5));
                song.setLyricist(resultSet.getString(6));
                song.setSinger(resultSet.getString(7));
                song.setType(resultSet.getString(8));
                song.setProductTitle(resultSet.getString(9));
                searchResults.add(song);
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                e.printStackTrace();
            });
        }
        return searchResults;
    }

    private static String getUnifiedSearchSQL_Query(boolean excludeUGC) {
        String sql;

        if (excludeUGC) {
            sql = "SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title " +
                    "FROM public.\"song_metadata_new\"" +
                    " WHERE (song_name ILIKE ? OR isrc ILIKE ? OR composer ILIKE ? OR lyricist ILIKE ? OR singer ILIKE ?)" +
                    " AND type = 'O' " +
                    "ORDER BY type DESC LIMIT 15";
        } else {
            sql = "SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title " +
                    "FROM public.\"song_metadata_new\"" +
                    " WHERE (song_name ILIKE ? OR isrc ILIKE ? OR composer ILIKE ? OR lyricist ILIKE ? OR singer ILIKE ?)" +
                    " ORDER BY type DESC LIMIT 15";
        }

        return sql;
    }
}
