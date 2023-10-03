package com.example.song_finder_fx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection conn = null;

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:songs.db");
        }
        return conn;
    }
}
