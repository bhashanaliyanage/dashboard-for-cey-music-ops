module com.example.song_finder_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.example.song_finder_fx to javafx.fxml;
    exports com.example.song_finder_fx;
}