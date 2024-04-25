package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.RevenueReportController;
import com.example.song_finder_fx.Controller.TextFormatter;
import com.example.song_finder_fx.Model.Artist;
import com.example.song_finder_fx.Model.ArtistReport;
import com.example.song_finder_fx.Model.Songs;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        testArtistReports();
    }

    private static void testArtistReports() throws SQLException, ClassNotFoundException {
        int artistID = 76;
        int conversionRate = 320;

        // Creating artist model by passing artistID
        Artist artist = new Artist(artistID);

        // Creating revenue report model by passing artist object and conversion rate
        ArtistReport report = new ArtistReport(artist, conversionRate);

        // Creating revenue report controller object
        RevenueReportController revenueReportController = new RevenueReportController(report);

        // Revenue report controller will have a method called calculate revenue (inputs report object and returns gross revenue, partner share, conversion rate, date, co-writer payment summary via report object)
        report = revenueReportController.calculateRevenue();

        // Then get gross revenue, partner share, conversion rate, date, top performing songs, and co-writer payment summary from the report model
        double grossRevenue = report.getGrossRevenueInLKR();
        double partnerShare = report.getPartnerShareInLKR();
        ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();

        // Printing calculated values
        System.out.println("Calculated Details for Selected Artist");
        System.out.println("========");

        System.out.println("Artist: " + report.getArtist().getName());
        System.out.println("EUR to LKR Conversion Rate: " + report.getConversionRate());
        System.out.println("Date: " + report.getDate());
        System.out.println("========");

        System.out.println("Gross Revenue: LKR " + grossRevenue);
        System.out.println("Partner Share: LKR " + partnerShare);
        System.out.println("========");

        System.out.println("Top Performing Songs");
        for (Songs song : topPerformingSongs) {
            System.out.println(song.getISRC() + " | " + song.getRoyalty() * conversionRate);
        }
    }

    private static void testSearch() {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        SongSearch songSearch = new SongSearch(databaseHandler);

        List<Songs> songs = songSearch.searchSong("Mawathe", SearchType.SONG_NAME);

        for (Songs song : songs) {
            System.out.println(song.getTrackTitle());
            System.out.println(song.getComposer());
            System.out.println(song.getLyricist());
        }
    }
}
