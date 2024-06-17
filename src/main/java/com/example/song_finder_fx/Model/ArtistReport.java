package com.example.song_finder_fx.Model;

import java.util.ArrayList;
import java.util.List;

public class ArtistReport {
    private int year = 0;
    private int monthInt = 0;
    private Artist artist = null;
    private double conversionRate = 0;
    private String month = "";
    private final ArrayList<String> coWriters = new ArrayList<>();
    private final ArrayList<String> coWriterShare = new ArrayList<>();
    private Double grossRevenue;
    private Double partnerShare;
    private ArrayList<Songs> topPerformingSongs;

    private List<CoWriterShare> coWritterList;
    private List<CoWriterSummary> coWriterPaymentSummary;

    public ArtistReport(Artist artist, int conversionRate, int year, int month) {
        System.out.println("ArtistReport.ArtistReport");
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);

        this.artist = artist;
        this.conversionRate = conversionRate;
        this.year = year;
        this.monthInt = month;
    }

    public List<CoWriterShare> getCoWritterList() {
        return coWritterList;
    }

    public void setCoWritterList(List<CoWriterShare> coWritterList) {
        this.coWritterList = coWritterList;
    }

    public ArtistReport(Artist artist, double conversionRate) {
        this.artist = artist;
        this.conversionRate = conversionRate;
    }

    public ArtistReport() {

    }

    /*public void clear() {
        coWriters.clear();
        coWriterShare.clear();
        *//*topPerformingSongNames.clear();
        topPerformingSongPayees.clear();
        topPerformingSongPayeeShare.clear();*//*
    }*/

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
        try {
            // int monthInt = Integer.parseInt(month);
            String[] monthNames = new String[] {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            return monthNames[monthInt - 1];
        } catch (NumberFormatException e) {
            return "Unspecified";
        }
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
        System.out.println("ArtistReport.setMonth");
        System.out.println("month = " + month);
        System.out.println("Artist Name: " + artist.getName());
        this.month = month;
    }

    public void setPayee(String payee) {
    }

    public void setCoWriterPaymentSummary(List<CoWriterSummary> coWriterSummaryList) {
        this.coWriterPaymentSummary = coWriterSummaryList;
    }

    public List<CoWriterSummary> getCoWriterPaymentSummary() {
        return coWriterPaymentSummary;
    }

    public int getYear() {
        return year;
    }

    public int getMonthInt() {
        return monthInt;
    }
}
