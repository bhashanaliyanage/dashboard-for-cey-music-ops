package com.example.song_finder_fx;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.sqlite.SQLiteException;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Main extends Application {
    private ProgressBar progressBar = new ProgressBar(0.0);


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 320, 240);
        HelloController helloController = loader.getController();
        progressBar = (ProgressBar) loader.getNamespace().get("progressBar");
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public File browseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        String name;

        if (selectedFile != null) {
            name = selectedFile.getName();
        } else {
            name = "No File Selected!";
        }

        return selectedFile;
    }

    public void ImportToBase(File file) throws SQLException, ClassNotFoundException, IOException {
        Connection db = Database.getConn();
        Scanner sc = new Scanner(new File(file.getAbsolutePath()));
        sc.useDelimiter(",");

        PreparedStatement ps = db.prepareStatement("INSERT INTO 'songData' (ISRC," +
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

                    ps.executeUpdate();
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
    }

    public void updateBase(File file) throws SQLException, ClassNotFoundException, IOException {
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
                    ps.setString(2, columnNames[0]);

                    ps.executeUpdate();
                }
            } catch (ArrayIndexOutOfBoundsException | SQLiteException e) {
                e.printStackTrace();
            }
        }
        sc.close();
    }

/*    public void executeFunction(ActionEvent event) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Execute your function here
                for (int i = 0; i < 100; i++) {
                    // Simulate work
                    Thread.sleep(50);

                    // Update the progress property of the Task
                    final int progressValue = i + 1;
                    updateProgress(progressValue, 100);

                    // Debug console output
                    System.out.println("Here! Progress: " + progressValue);
                }
                return null;
            }
        };

        // Bind the ProgressBar's progressProperty to the Task's progressProperty
        progressBar.progressProperty().bind(task.progressProperty());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }*/

    public void CreateBase() throws SQLException, ClassNotFoundException {
        // Load the JDBC driver
        Connection db = Database.getConn();

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS songData (" +
                "ISRC TEXT PRIMARY KEY," +
                "ALBUM_TITLE TEXT," +
                "UPC INTEGER," +
                "CAT_NO TEXT," +
                "PRODUCT_PRIMARY TEXT," +
                "ALBUM_FORMAT TEXT," +
                "TRACK_TITLE TEXT," +
                "TRACK_VERSION TEXT," +
                "SINGER TEXT," +
                "FEATURING TEXT," +
                "COMPOSER TEXT," +
                "LYRICIST TEXT," +
                "FILE_NAME TEXT)");

        ps.executeUpdate();
    }



    public static void main(String[] args) {
        launch(args);
    }
}