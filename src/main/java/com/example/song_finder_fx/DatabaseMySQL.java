package com.example.song_finder_fx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseMySQL {
    private String url = "jdbc:mysql://localhost:3306/songData";
    private String username = "root";
    private String password = "";

    DatabaseMySQL() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url, username,password);

        if (con != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Error connecting to database!");
        }
    }

}
