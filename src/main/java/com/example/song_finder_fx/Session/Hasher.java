package com.example.song_finder_fx.Session;

import com.example.song_finder_fx.DatabasePostgres;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class Hasher {
    private final String password;
    private final String username;

    public Hasher(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getHashedPass() {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String getUserName() {
        return username;
    }

    public boolean validate() throws SQLException {
        String hashedPW = DatabasePostgres.getHashedPW_ForUsername(username);
        boolean status = false;

        try {
            status =  BCrypt.checkpw(password, hashedPW);
            return status;
        } catch (Exception e) {
            // e.printStackTrace();
            return status;
        }
    }
}
