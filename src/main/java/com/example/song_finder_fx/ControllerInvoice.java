package com.example.song_finder_fx;

import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class ControllerInvoice {
    public TextField txtInvoiceTo;
    public TextField txtInvoiceNo;
    public DatePicker dpInvoiceDate;

    public void onGenerateInvoice(ActionEvent event) throws MalformedURLException, FileNotFoundException {
        String invoiceTo = txtInvoiceTo.getText();
        String invoiceNo = txtInvoiceNo.getText();
        // String invoiceDate = dpInvoiceDate;
        Invoice.generateInvoice();
    }
}
