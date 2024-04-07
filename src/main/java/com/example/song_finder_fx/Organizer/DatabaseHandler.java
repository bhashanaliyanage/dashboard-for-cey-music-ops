package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Model.Songs;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    // JDBC connection parameters
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/songdata";
    private static final String USER = "postgres";
    private static final String PASSWORD = "thusitha01";

    // Database connection
    private Connection connection;

    public DatabaseHandler() {
        try {
            // Initialize the database connection
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error Connecting to Database: " + e);
        }
    }

public List<Songs> searchSongsByTitle(String criteria) {
    List<Songs> searchResults = new ArrayList<>();
    try {
        PreparedStatement statement = connection.prepareStatement("SELECT isrc, song_name, file_name, upc FROM public.songs WHERE song_name LIKE ? LIMIT 15");
        statement.setString(1, "%" + criteria + "%");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            // Create Song objects from the ResultSet and add them to the searchResults list
            Songs song = new Songs();
            song.setIsrc(resultSet.getString(1));
            song.setTrackTitle(resultSet.getString(2));
            song.setFileName(resultSet.getString(3));
            song.setUPC(resultSet.getString(4));
            searchResults.add(song);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return searchResults;
}

    public List<Songs> searchSongsByISRC(String criteria) {
        return null;
    }

    public List<Songs> searchSongsByComposer(String criteria) {
        return null;
    }

    public List<Songs> searchSongsBySinger(String criteria) {
        return null;
    }

    public List<Songs> searchSongsByLyricist(String criteria) {
        return null;
    }

    public List<Songs> searchSongsByProductName(String criteria) {
        return null;
    }
}
