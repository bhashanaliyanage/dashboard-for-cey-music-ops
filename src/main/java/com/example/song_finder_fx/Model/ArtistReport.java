package com.example.song_finder_fx.Model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ArtistReport {
    private Artist artist = null;
    private double conversionRate = 0;
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

    private List<CowriterShare> coWritterList;

    public List<CowriterShare> getCoWritterList() {
        return coWritterList;
    }

    public void setCoWritterList(List<CowriterShare> coWritterList) {
        this.coWritterList = coWritterList;
    }

    public ArtistReport(Artist artist, double conversionRate) {
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

    public double getGrossRevenueInLKR() {
        return grossRevenue * conversionRate;
    }

    public double getPartnerShareInLKR() {
        return partnerShare * conversionRate;
    }

    public String getPayee() {
        return artist.getName();
    }

    public String getMonth() {
        int monthInt = Integer.parseInt(month);
        String[] monthNames = new String[] {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return monthNames[monthInt];
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

    public double getConversionRate() {
        return conversionRate;
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

    public void setMonth(String month) {
        this.month = month;
    }

    public void setPayee(String payee) {
    }
}
