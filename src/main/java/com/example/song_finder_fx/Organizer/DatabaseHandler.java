package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Songs;

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

    public List<Songs> searchSongsByTitle(String criteria) {
        List<Songs> searchResults = new ArrayList<>();
        try {
            Connection conn = DatabasePostgres.getConn();
            PreparedStatement statement = conn.prepareStatement("SELECT isrc, song_name, file_name, upc, composer, lyricist, singer, type, product_title FROM public.\"song_metadata_new\" WHERE song_name ILIKE ? ORDER BY type DESC LIMIT 15");
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

    public List<Songs> searchSongsByISRC(String criteria) {
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

    public List<Songs> searchSongsByComposer(String criteria) {
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

    public List<Songs> searchSongsBySinger(String criteria) {
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

    public List<Songs> searchSongsByLyricist(String criteria) {
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

    public List<Songs> searchSongsByProductName(String criteria) {
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
}
