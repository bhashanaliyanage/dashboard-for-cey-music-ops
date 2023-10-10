module com.example.song_finder_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.drive;
    /*requires com.google.auth.oauth2;
    requires firebase.admin;*/
    /*requires firebase.admin;
    requires google.auth.library.oauth2.http;*/


    opens com.example.song_finder_fx to javafx.fxml;
    exports com.example.song_finder_fx;
}