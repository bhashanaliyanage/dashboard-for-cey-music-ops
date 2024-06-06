package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.ClaimFilter;
import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.ReportPDF;
import com.example.song_finder_fx.Controller.RevenueReportController;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.*;
import com.example.song_finder_fx.Session.User;
import com.example.song_finder_fx.Session.UserSession;
import com.example.song_finder_fx.Session.UserSummary;
import com.itextpdf.layout.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        // testArtistReportPDF();
        // testArtistReports();
        // testDashboard();

        List<ManualClaimTrack> archivedManualClaims;

        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 30);

        archivedManualClaims = DatabasePostgres.getArchivedManualClaims(startDate, endDate);

        System.out.println("\nTotal: " + archivedManualClaims.size());
        System.out.println("\n");

        for (ManualClaimTrack track : archivedManualClaims) {
            System.out.println("Name: " + track.getTrackName());
        }
    }

    private static void testDashboard() throws SQLException {
        UserSession session = new UserSession();
        UserSummary summary = new UserSummary(session);

        System.out.println(summary.getGreeting());
        System.out.println(summary.getManualClaimCount());
        System.out.println(summary.getReportDayCount());
    }

    private static void testArtistReportPDF() throws SQLException, IOException {
        String artistName = "Rohana Weerasinghe";
        ArtistReport report = getArtistReport(47, 320, artistName);
        ReportPDF pdf = new ReportPDF();
        String path = "C:\\Users\\bhash\\Documents\\Test\\test.pdf";
        Document document = pdf.generateReport(path, report);
        System.out.println("Report for " + report.getArtist().getName() + " is generated and saved in: " + path);
    }

    private static void testArtistReports() throws SQLException {
        int artistID = 47;
        int conversionRate = 1;
        String artistName = "Rohana Weerasinghe";

        // Creating artist model by passing artistID
        ArtistReport report = getArtistReport(artistID, conversionRate, artistName);

        // Then get gross revenue, partner share, conversion rate, date, top performing songs, and co-writer payment summary from the report model
        double grossRevenue = report.getGrossRevenueInLKR();
        double partnerShare = report.getPartnerShareInLKR();
        ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();
        List<CoWriterSummary> coWriterSummaries = report.getCoWriterPaymentSummary();
        List<CoWriterShare> coWriterShares = report.getCoWritterList();

        // Printing calculated values
        System.out.println("\nCalculated Details for Selected Artist");

        System.out.println("\n========");

        System.out.println("\nArtist: " + report.getArtist().getName());
        System.out.println("EUR to LKR Conversion Rate: " + report.getConversionRate());
        System.out.println("Date: " + report.getDate());

        System.out.println("\n========");

        System.out.println("\nGross Revenue: LKR " + grossRevenue);
        System.out.println("Partner Share: LKR " + partnerShare);
        System.out.println("========");

        System.out.println("\nTop Performing Songs");
        for (Songs song : topPerformingSongs) {
            System.out.println(song.getTrackTitle() + " | " + song.getRoyalty() * conversionRate);
        }

        System.out.println("\n========");

        System.out.println("\nCo-Writer Share Summary");
        for (CoWriterSummary summary : coWriterSummaries) {
            String contributor = summary.getContributor();
            double royalty = summary.getRoyalty();
            System.out.println(contributor + " | " + royalty);
        }

        System.out.println("\nCo-Writer Share Detailed");
        for (CoWriterShare share : coWriterShares) {
            String songName = share.getSongName();
            String songType = share.getSongType();
            String percentage = share.getShare();
            double artistShare = share.getRoyalty();
            String coWriter = share.getContributor();

            System.out.println(songName + " | " + songType + " | " + percentage + " | " + artistShare + " | " + coWriter);
        }
    }

    private static ArtistReport getArtistReport(int artistID, int conversionRate, String artistName) throws SQLException {
        Artist artist = new Artist(artistID, artistName);

        // Creating revenue report model by passing artist object and conversion rate
        ArtistReport report = new ArtistReport(artist, conversionRate);

        // Creating revenue report controller object
        RevenueReportController revenueReportController = new RevenueReportController(report);

        // Revenue report controller will have a method called calculate revenue (inputs report object and returns gross revenue, partner share, conversion rate, date, co-writer payment summary via report object)
        report = revenueReportController.calculateRevenue();
        return report;
    }

    private static void testSearch() {
        // DatabaseHandler databaseHandler = new DatabaseHandler();
        SongSearch songSearch = new SongSearch();

        List<Songs> songs = songSearch.searchSong("Mawathe", SearchType.SONG_NAME);

        for (Songs song : songs) {
            String trackTitle = song.getTrackTitle();
            String songType = song.getType();
            String isrc = song.getISRC();
            String singer = song.getSinger();
            String composer = song.getComposer();
            String lyricist = song.getLyricist();
            String featuring = song.getFeaturing();
            String productName = song.getProductName();
            String upc = song.getUPC();
            String fileName = song.getFileName();

            System.out.println("Track Title: " + trackTitle);
            System.out.println("Song Type: " + songType);
            System.out.println("ISRC: " + isrc);
            System.out.println("Singer: " + singer);
            System.out.println("Composer: " + composer);
            System.out.println("Lyricist: " + lyricist);
            System.out.println("Featuring: " + featuring);
            System.out.println("Product Name: " + productName);
            System.out.println("UPC: " + upc);
            System.out.println("File Name: " + fileName);
            System.out.println("========");
        }
    }
}
