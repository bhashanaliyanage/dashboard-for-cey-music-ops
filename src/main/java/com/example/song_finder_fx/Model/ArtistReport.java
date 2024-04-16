package com.example.song_finder_fx.Model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ArtistReport {
    private Artist artist = null;
    private int conversionRate = 0;
    private String payee = "";
    private String grossRevenueInLKR = "";
    private String partnerShareInLKR = "";
    private String taxAmount = "0";
    private String amountPayable = "";
    private String month = "";
    private final DecimalFormat df = new DecimalFormat("0.00");
    private final ArrayList<String> coWriters = new ArrayList<>();
    private final ArrayList<String> topPerformingSongNames = new ArrayList<>();
    private final ArrayList<String> topPerformingSongPayees = new ArrayList<>();
    private final ArrayList<String> topPerformingSongPayeeShare = new ArrayList<>();
    private final ArrayList<String> coWriterShare = new ArrayList<>();
    private Double grossRevenue;
    private Double partnerShare;
    private ArrayList<Songs> topPerformingSongs;

    public ArtistReport(Artist artist, int conversionRate) {
        this.artist = artist;
        this.conversionRate = conversionRate;
    }

    public ArtistReport() {

    }

    public void clear() {
        coWriters.clear();
        coWriterShare.clear();
        topPerformingSongNames.clear();
        topPerformingSongPayees.clear();
        topPerformingSongPayeeShare.clear();
    }

    public void setGrossRevenue(String selectedItem, double grossRevenueInLKR, double partnerShareInLKR, double tax, double amountPayable, String month) {
        this.payee = selectedItem;
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        this.grossRevenueInLKR = formatter.format(grossRevenueInLKR);
        this.partnerShareInLKR = formatter.format(partnerShareInLKR);
        this.taxAmount = df.format(tax);
        this.amountPayable = df.format(amountPayable);
        this.month = month;
    }

    public double getGrossRevenueInLKR() {
        return grossRevenue * conversionRate;
    }

    public double getPartnerShareInLKR() {
        return partnerShare * conversionRate;
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

    public void setTopPerformingSongDetails(String assetTitle, String payee, String reportedRoyalty) {
        topPerformingSongNames.add(assetTitle);
        topPerformingSongPayees.add(payee);
        topPerformingSongPayeeShare.add("Rs. " + reportedRoyalty);
    }
    public ArrayList<String> getTopPerformingSongNames() {
        return topPerformingSongNames;
    }

    public ArrayList<String> getTopPerformingSongPayees() {
        return topPerformingSongPayees;
    }

    public ArrayList<String> getTopPerformingSongPayeeShare() {
        return topPerformingSongPayeeShare;
    }

    public ArrayList<String> getCoWriters() {
        return coWriters;
    }

    public ArrayList<String> getCoWriterShare() {
        return coWriterShare;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(int conversionRate) {
        this.conversionRate = conversionRate;
    }

    public void setGrossRevenue(Double grossRevenue) {
        this.grossRevenue = grossRevenue;
    }

    public Double getGrossRevenue() {
        return grossRevenue;
    }

    public void setPartnerShare(Double partnerShare) {
        this.partnerShare = partnerShare;
    }

    public Double getPartnerShare() {
        return partnerShare;
    }

    public String getDate() {
        // Get the current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        // Format the date as a string
        return currentDate.toString();
    }

    public void setTopPerformingSongs(ArrayList<Songs> topPSongs) {
        this.topPerformingSongs = topPSongs;
    }

    public ArrayList<Songs> getTopPerformingSongs() {
        return topPerformingSongs;
    }
}
