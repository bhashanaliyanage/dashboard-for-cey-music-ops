package com.example.song_finder_fx.Model;

import java.util.ArrayList;
import java.util.List;

public class ArtistReport {
    private double audToLkrRate = 0;
    private int year = 0;
    private int monthInt = 0;
    private Artist artist = null;
    private double eurToAudRate = 0;
    private final ArrayList<String> coWriters = new ArrayList<>();
    private final ArrayList<String> coWriterShare = new ArrayList<>();
    private Double grossRevenue;
    private Double partnerShare;
    private ArrayList<Songs> topPerformingSongs;

    private List<CoWriterShare> coWritterList;
    private List<CoWriterSummary> coWriterPaymentSummary;

    public ArtistReport(Artist artist, double eurToAudRate, double audToLkrRate, int year, int month) {
        System.out.println("Creating Artist Report for " + artist.getName());
        System.out.println("Year: " + year);
        System.out.println("Month: " + getMonth(month));

        this.artist = artist;
        this.eurToAudRate = eurToAudRate;
        this.audToLkrRate = audToLkrRate;
        this.year = year;
        this.monthInt = month;
    }

    private String getMonth(int month) {
        try {
            String[] monthNames = new String[]{
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            return monthNames[month - 1];
        } catch (NumberFormatException e) {
            return "Unspecified";
        }
    }

    public List<CoWriterShare> getAssetBreakdown() {
        return coWritterList;
    }

    public void setAssetBreakdown(List<CoWriterShare> coWritterList) {
        this.coWritterList = coWritterList;
    }

    public ArtistReport(Artist artist, double eurToAudRate) {
        this.artist = artist;
        this.eurToAudRate = eurToAudRate;
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
        return grossRevenue / eurToAudRate * audToLkrRate;
    }

    public double getPartnerShareInLKR() {
        return partnerShare / eurToAudRate * audToLkrRate;
    }

    public String getPayee() {
        return artist.getName();
    }

    public String getMonth() {
        try {
            // int monthInt = Integer.parseInt(month);
            String[] monthNames = new String[]{
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

    public double getEurToAudRate() {
        return eurToAudRate;
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

    public double getAudToLkrRate() {
        return audToLkrRate;
    }

    public double getGrossRevenueInAUD() {
        return grossRevenue / eurToAudRate;
    }

    public double getPartnerShareInAUD() {
        return partnerShare / eurToAudRate;
    }
}
