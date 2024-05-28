package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.Invoice;
import com.example.song_finder_fx.Model.Songs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerInvoiceNew {

    @FXML
    private Button btnAddMore;

    @FXML
    private Button btnCopyTo;

    @FXML
    private Button btnGenerateInvoice;

    @FXML
    private DatePicker dpInvoiceDate;

    @FXML
    private HBox hboxAmountPerItem;

    @FXML
    private HBox hboxCurrencyFormat;

    @FXML
    private HBox hboxDiscount;

    @FXML
    private HBox hboxInvoiceNo;

    @FXML
    private HBox hboxInvoiceTo;

    @FXML
    private VBox hboxListActions;

    @FXML
    private Label lblListCount;

    @FXML
    private VBox mainVBox;

    @FXML
    private ScrollPane scrlpneMain;

    @FXML
    private ScrollPane scrlpneSongInvoice;

    @FXML
    private TextField txtAmountPerItem;

    @FXML
    private TextField txtCurrencyFormat;

    @FXML
    private TextField txtDiscount;

    @FXML
    private TextField txtInvoiceNo;

    @FXML
    private TextField txtInvoiceTo;

    @FXML
    private VBox vBoxInSearchSong;

    @FXML
    private VBox vboxSong;

    private final ArrayList<Songs> songs = new ArrayList<>();

    @FXML
    void initialize() {
        List<Songs> songListNew = Main.getSongList();

        setSongCount(songListNew.size());

        loadSongList(songListNew);
    }

    private void loadSongList(List<Songs> songListNew) {
        // Setting up UI
        scrlpneSongInvoice.setVisible(true);
        scrlpneSongInvoice.setContent(vboxSong);

        Node[] nodes;
        nodes = new Node[songListNew.size()];
        vboxSong.getChildren().clear();

        for (int i = 0; i < nodes.length; i++) {
            try {
                Songs songDetail = songListNew.get(i);

                // Search Composer and Lyricist from Artists Table
                String percentage;
                String copyrightOwner = null;
                String copyrightOwnerView = "";

                if (songDetail.composerAndLyricistCeyMusic()) {
                    percentage = "100%";
                    copyrightOwner = songDetail.getComposer() + "\n" + songDetail.getLyricist();
                    copyrightOwnerView = songDetail.getComposer() + " | " + songDetail.getLyricist();
                } else if (songDetail.composerOrLyricistCeyMusic()) {
                    percentage = "50%";
                    if (songDetail.composerCeyMusic()) {
                        copyrightOwner = songDetail.getComposer();
                        copyrightOwnerView = songDetail.getComposer();
                    } else {
                        copyrightOwner = songDetail.getLyricist();
                        copyrightOwnerView = songDetail.getLyricist();
                    }
                } else {
                    percentage = "0%";
                }

                songDetail.setPercentage(percentage);
                songDetail.setCopyrightOwner(copyrightOwner);
                songs.add(songDetail);

                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/song-songlist-invoice.fxml")));

                // Loading plus and minus icons
                Image plusImg = new Image("com/example/song_finder_fx/images/icon_plus.png");
                Image minusImg = new Image("com/example/song_finder_fx/images/icon_minus.png");

                // Getting percentage image view
                ImageView ingPercentageChange = (ImageView) nodes[i].lookup("#btnPercentageChange");

                // Setting percentage change icon, plus or minus according to the situation
                if (Objects.equals(percentage, "100%")) {
                    ingPercentageChange.setImage(minusImg);
                } else {
                    ingPercentageChange.setImage(plusImg);
                }

                Label lblSongName = (Label) nodes[i].lookup("#songName");
                Label lblArtist = (Label) nodes[i].lookup("#songArtist");
                Label lblSongShare = (Label) nodes[i].lookup("#songShare");
                Label lblISRC = (Label) nodes[i].lookup("#lblISRC");

                lblSongName.setText(songDetail.getTrackTitle());
                lblArtist.setText(copyrightOwnerView);
                lblSongShare.setText(percentage);
                lblISRC.setText(songDetail.getISRC());

                vboxSong.getChildren().add(nodes[i]);
            } catch (NullPointerException | IOException ex) {
                ex.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setSongCount(int size) {
        lblListCount.setText("Total: " + size);
    }

    @FXML
    void onGenerateInvoice(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        String invoiceTo = txtInvoiceTo.getText().toUpperCase();
        String invoiceNo = txtInvoiceNo.getText().toUpperCase();
        LocalDate date = dpInvoiceDate.getValue();
        String amountPerItemString = txtAmountPerItem.getText();
        double amountPerItem = 0;
        String currencyFormat = txtCurrencyFormat.getText().toUpperCase();
        String discountString = txtDiscount.getText();
        double discount = 0;

        try {
            amountPerItem = Double.parseDouble(amountPerItemString);
        } catch (NumberFormatException numberFormatException) {
            hboxAmountPerItem.setStyle("-fx-border-color: '#931621';");
            txtAmountPerItem.requestFocus();
            scrlpneMain.setVvalue(0);
        }

        if (Objects.equals(discountString, "")) {
            discount = 0;
        } else {
            try {
                discount = Double.parseDouble(discountString);
            } catch (NumberFormatException numberFormatException) {
                hboxDiscount.setStyle("-fx-border-color: '#931621';");
                txtDiscount.requestFocus();
                scrlpneMain.setVvalue(0);
            }
        }

        if (Objects.equals(invoiceTo, "")) {
            hboxInvoiceTo.setStyle("-fx-border-color: '#931621';");
            txtInvoiceTo.requestFocus();
            scrlpneMain.setVvalue(0);
        } else if (Objects.equals(invoiceNo, "")) {
            hboxInvoiceNo.setStyle("-fx-border-color: '#931621';");
            txtInvoiceNo.requestFocus();
            scrlpneMain.setVvalue(0);
        } else if (date == null) {
            dpInvoiceDate.setStyle("-fx-border-color: '#931621';");
            dpInvoiceDate.requestFocus();
            scrlpneMain.setVvalue(0);
        } else {
            System.out.println("invoiceTo = " + invoiceTo);
            System.out.println("invoiceNo = " + invoiceNo);
            System.out.println("date = " + date);

            if (amountPerItem > 0) {

                if (Objects.equals(currencyFormat, "")) {
                    hboxCurrencyFormat.setStyle("-fx-border-color: '#931621';");
                    hboxCurrencyFormat.requestFocus();
                    scrlpneMain.setVvalue(0);
                } else {
                    Invoice.generateInvoice(invoiceTo, invoiceNo, date, amountPerItem, currencyFormat, discount, txtInvoiceTo.getScene().getWindow(), songs);
                }
            } else {
                hboxAmountPerItem.setStyle("-fx-border-color: '#931621';");
                txtAmountPerItem.requestFocus();
                scrlpneMain.setVvalue(0);
            }
        }
    }
}
