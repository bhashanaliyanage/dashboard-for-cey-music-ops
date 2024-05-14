package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.IngestController;
import com.example.song_finder_fx.Controller.ManualClaimsController;
import com.example.song_finder_fx.Controller.ReportPDF;
import com.example.song_finder_fx.Controller.RevenueReportController;
import com.example.song_finder_fx.Model.*;
import com.itextpdf.layout.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        String upc = "8721093194828";
        String catNo = "KAA-CEY-031";
        String trackTitle = "Buddan Saranan Gachchami";
        String isrc = "LKA0U2401476";
        String composer = "Anil Biswas";
        String lyricist = "Karunaratne Abeysekera";
        String fileName = "FXBX8YcX5Ro-Buddan Saranan Gachchami.flac";
        LocalDate date = LocalDate.now();
        String youtubeID = "JKWtRso0EUE";
        String trimStart = "00:00:01";
        String trimEnd = "00:00:31";
        String location = "C:\\Users\\bhash\\Documents\\Test\\";

        MCTrackNew mcTrackNew = new MCTrackNew(upc, catNo, trackTitle, isrc, composer, lyricist, fileName, date, youtubeID, trimStart, trimEnd, location);
        MCTrackNew mcTrackNew2 = new MCTrackNew(upc, catNo, trackTitle, isrc, composer, lyricist, fileName, date, youtubeID, trimStart, trimEnd, location);
        MCTrackNew mcTrackNew3 = new MCTrackNew(upc, catNo, trackTitle, isrc, composer, lyricist, fileName, date, youtubeID, trimStart, trimEnd, location);

        ArrayList<MCTrackNew> claims = new ArrayList<>();
        claims.add(mcTrackNew);
        claims.add(mcTrackNew2);
        claims.add(mcTrackNew3);

        // TODO: CSV, Audio Download, Copying Audios, FTP Upload

        String path = "C:\\Users\\bhash\\Documents\\Test\\";

        ManualClaimsController manualClaimsController = new ManualClaimsController(claims);
        // manualClaimsController.generateCSV(path);
    }

    private static void testArtistReportPDF() throws SQLException, IOException {
        ArtistReport report = getArtistReport(47, 320);
        ReportPDF pdf = new ReportPDF();
        String path = "C:\\Users\\bhash\\Documents\\Test\\test.pdf";
        Document document = pdf.generateReport(path, report);
    }

    private static void testArtistReports() throws SQLException {
        int artistID = 76;
        int conversionRate = 320;

        // Creating artist model by passing artistID
        ArtistReport report = getArtistReport(artistID, conversionRate);

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

    private static ArtistReport getArtistReport(int artistID, int conversionRate) throws SQLException {
        Artist artist = new Artist(artistID);

        // Creating revenue report model by passing artist object and conversion rate
        ArtistReport report = new ArtistReport(artist, conversionRate);

        // Creating revenue report controller object
        RevenueReportController revenueReportController = new RevenueReportController(report);

        // Revenue report controller will have a method called calculate revenue (inputs report object and returns gross revenue, partner share, conversion rate, date, co-writer payment summary via report object)
        report = revenueReportController.calculateRevenue();
        return report;
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
