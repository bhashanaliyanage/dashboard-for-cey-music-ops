package com.example.song_finder_fx.Model;

import java.text.DecimalFormat;

public class ArtistReport {
    private String payee = "";
    private String grossRevenueInLKR = "";
    private String partnerShareInLKR = "";
    private String taxAmount = "0";
    private String amountPayable = "";
    private String month = "";
    private final DecimalFormat df = new DecimalFormat("0.00");

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
}
