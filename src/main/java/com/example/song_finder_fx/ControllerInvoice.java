package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.Objects;

public class ControllerInvoice {
    public TextField txtInvoiceTo;
    public TextField txtInvoiceNo;
    public DatePicker dpInvoiceDate;
    public HBox hboxInvoiceTo;
    public HBox hboxInvoiceNo;
    public VBox vboxSongInvoice;
    public ScrollPane scrlpneSongInvoice;

    public static void loadThings(ActionEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        Scene scene = node.getScene();
        VBox mainVBox = (VBox) scene.lookup("#mainVBox");
        mainVBox.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(ControllerInvoice.class.getResource("layouts/song-list-invoice.fxml"));
        Parent newContent = loader.load();
        mainVBox.getChildren().add(newContent);
    }

    public void onGenerateInvoice(ActionEvent event) throws MalformedURLException, FileNotFoundException {
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
