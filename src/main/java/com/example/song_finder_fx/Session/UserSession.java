package com.example.song_finder_fx.Session;

import com.example.song_finder_fx.DatabasePostgres;

import java.sql.SQLException;
import java.util.prefs.Preferences;

public class UserSession {
    private final Preferences preferences;
    private String username;
    private boolean isLoggedIn;
    private User user;

    // Constructor
    public UserSession() throws SQLException {
        preferences = Preferences.userRoot().node(this.getClass().getName());
        isLoggedIn = false;
        // Restore session if user was previously logged in
        restoreSession();
    }

    // Method to check if the user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Method to restore session if user was previously logged in
    private void restoreSession() throws SQLException {
        username = preferences.get("username", null);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Set privileges based on user role, for example
            setPrivilegesAndEmail(username);
            System.out.println("Session restored for user: " + username);
        } else {
            System.out.println("No user session found, Please log in!");
        }
    }

    // Private method to set privileges based on user role
    private void setPrivilegesAndEmail(String username) throws SQLException {
        this.user = DatabasePostgres.getUserData(username);

        if (this.user == null) {
            logout();
        } else {
            this.username = username;
        }
    }

    // Method to simulate login
    public boolean login(String username, String password) throws SQLException {
        // Simulate authentication logic
        if (authenticate(username, password)) {
            isLoggedIn = true;
            // Set privileges based on user role, for example
            setPrivilegesAndEmail(username);
            // Save logged user details to preferences
            saveSession(username);
            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        } else {
            System.out.println("Login failed. Invalid username or password.");
            return false;
        }
    }

    // Private method to simulate authentication
    private boolean authenticate(String username, String password) throws SQLException {
        Hasher hasher = new Hasher(username, password);
        return hasher.validate();
    }

    // Method to save logged user details to preferences
    private void saveSession(String username) {
        preferences.put("username", username);
        preferences.putBoolean("isLoggedIn", true);
    }

    // Method to simulate user signup
    public void signup(String username, String password, String email, String displayName) throws SQLException {
        boolean userCreated = DatabasePostgres.createUser(username, password, email, displayName);
        if (userCreated) {
            // Simulate user signup logic
            // For demonstration purposes, let's assume signup always succeeds
            isLoggedIn = true;
            setPrivilegesAndEmail(username);
            saveSession(username);
            System.out.println("Signup successful. Welcome, " + username + "!");
        } else {
            System.out.println("Error!. Unable to create user for username: " + username);
        }
    }

    // Method to simulate logout
    public void logout() {
        // Remove logged user details from preferences
        clearSession();

        System.out.println("Logout successful. Goodbye, " + username + "!");

        isLoggedIn = false;
        this.user = null;
        this.username = null;
    }

    // Method to clear logged user details from preferences
    private void clearSession() {
        preferences.remove("username");
        preferences.remove("isLoggedIn");
    }

    public int getPrivilegeLevel() {
        return user.getPrivilegeLevel();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public String getUserName() {
        return username;
    }

    public boolean changeNickName(String nickName) throws SQLException {
        int userID = user.getUserID();
        int affectedRowCount = DatabasePostgres.changeUserNickName(userID, nickName);
        if (affectedRowCount > 0) {
            user.setNickName(nickName);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeUsername(String username) throws SQLException {
        int userID = user.getUserID();
        int affectedRowCount = DatabasePostgres.changeUserName(userID, username);
        if (affectedRowCount > 0) {
            this.username = username;
            return true;
        } else {
            return false;
        }
    }
}
