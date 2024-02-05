package com.example.song_finder_fx.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ArtistReport {
    private String payee = "";
    private String grossRevenueInLKR = "";
    private String partnerShareInLKR = "";
    private String taxAmount = "0";
    private String amountPayable = "";
    private String month = "";
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final ArrayList<String> coWriters = new ArrayList<>();

    public ArrayList<String> getCoWriters() {
        return coWriters;
    }

    public ArrayList<String> getCoWriterShare() {
        return coWriterShare;
    }

    private final ArrayList<String> coWriterShare = new ArrayList<>();

    public void setGrossRevenue(String selectedItem, double grossRevenueInLKR, double partnerShareInLKR, double tax, double amountPayable, String month) {
        this.payee = selectedItem;
        this.grossRevenueInLKR = df.format(grossRevenueInLKR);
        this.partnerShareInLKR = df.format(partnerShareInLKR);
        this.taxAmount = df.format(tax);
        this.amountPayable = df.format(amountPayable);
        this.month = month;
    }

    public String getGrossRevenueInLKR() {
        return "LKR " + grossRevenueInLKR;
    }

    public String getPartnerShareInLKR() {
        return "LKR " + partnerShareInLKR;
    }

    public String getTaxAmount() {
        return "LKR " + taxAmount;
    }

    public String getAmountPayable() {
        return "LKR " + amountPayable;
    }

    public String getPayee() {
        return payee;
    }

    public String getMonth() {
        return month;
    }

    public void addCoWriter(String artist) {
        coWriters.add(artist);
    }

    public void addCoWriterShare(double share) {
        coWriterShare.add("Rs. " + df.format(share));
    }
}
