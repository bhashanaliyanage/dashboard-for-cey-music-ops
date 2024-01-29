package com.example.song_finder_fx;

import java.text.DecimalFormat;

public class Report {
    private String grossRevenueInLKR;
    private String partnerShareInLKR;
    private String taxAmount;
    private String amountPayable;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public void setGrossRevenue(double grossRevenueInLKR, double partnerShareInLKR, double tax, double amountPayable) {
        this.grossRevenueInLKR = df.format(grossRevenueInLKR);
        this.partnerShareInLKR = df.format(partnerShareInLKR);
        this.taxAmount = df.format(tax);
        this.amountPayable = df.format(amountPayable);
    }

    public String getGrossRevenueInLKR() {
        return grossRevenueInLKR;
    }

    public String getPartnerShareInLKR() {
        return partnerShareInLKR;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public String getAmountPayable() {
        return amountPayable;
    }
}
