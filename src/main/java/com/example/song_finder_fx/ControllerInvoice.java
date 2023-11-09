package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ControllerInvoice {
    private final UIController mainUIController;
    public TextField txtInvoiceTo;
    public TextField txtInvoiceNo;
    public TextField txtAmountPerItem;
    public DatePicker dpInvoiceDate;
    public HBox hboxInvoiceTo;
    public HBox hboxInvoiceNo;
    public ScrollPane scrlpneSongInvoice;
    public VBox vboxSong;

    public ControllerInvoice(UIController mainUIController) {
        this.mainUIController = mainUIController;
    }

    public void loadThings(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(ControllerInvoice.class.getResource("layouts/song-list-invoice.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();
        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);

        // Song List
        List<String> songList = Main.getSongList();

        // Connecting to database
        DatabaseMySQL db = new DatabaseMySQL();

        // Setting up UI
        scrlpneSongInvoice.setVisible(true);
        scrlpneSongInvoice.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songList.size()];
        vboxSong.getChildren().clear();

        for (int i = 0; i < nodes.length; i++) {
            try {
                List<String> songDetail = db.searchSongDetails(songList.get(i));
                // Search Composer and Lyricist from Artists Table
                Boolean composerCeyMusic = db.searchArtistTable(songDetail.get(6)); // 6
                Boolean lyricistCeyMusic = db.searchArtistTable(songDetail.get(7)); // 7
                String percentage;
                String copyrightOwner = null;
                if (composerCeyMusic && lyricistCeyMusic) {
                    percentage = "100%";
                    copyrightOwner = songDetail.get(6) + "\n" + songDetail.get(7);
                } else if (composerCeyMusic || lyricistCeyMusic) {
                    percentage = "50%";
                    if (composerCeyMusic) {
                        copyrightOwner = songDetail.get(6);
                    } else {
                        copyrightOwner = songDetail.get(7);
                    }
                } else {
                    percentage = "0%";
                }

                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist-invoice.fxml")));
                Label lblSongName = (Label) nodes[i].lookup("#songName");
                Label lblArtist = (Label) nodes[i].lookup("#songArtist");
                Label lblSongShare = (Label) nodes[i].lookup("#songShare");

                lblSongName.setText(songDetail.get(3));
                lblArtist.setText(copyrightOwner);
                lblSongShare.setText(percentage);

                vboxSong.getChildren().add(nodes[i]);
            } catch (NullPointerException | IOException ex) {
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onGenerateInvoice() throws MalformedURLException, FileNotFoundException {
        String invoiceTo = txtInvoiceTo.getText().toUpperCase();
        String invoiceNo = txtInvoiceNo.getText().toUpperCase();
        LocalDate date = dpInvoiceDate.getValue();

        if (Objects.equals(invoiceTo, "")) {
            hboxInvoiceTo.setStyle("-fx-border-color: '#931621';");
        } else if (Objects.equals(invoiceNo, "")) {
            hboxInvoiceNo.setStyle("-fx-border-color: '#931621';");
        } else if (date == null) {
            dpInvoiceDate.setStyle("-fx-border-color: '#931621';");
        } else {
            System.out.println("invoiceTo = " + invoiceTo);
            System.out.println("invoiceNo = " + invoiceNo);
            System.out.println("date = " + date);

            // Invoice.generateInvoice(invoiceTo, invoiceNo, date);
        }
    }

}
