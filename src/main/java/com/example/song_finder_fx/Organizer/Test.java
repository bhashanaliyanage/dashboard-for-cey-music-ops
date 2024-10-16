package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.Months;
import com.example.song_finder_fx.Constants.ProductType;
import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Main;
import com.example.song_finder_fx.Model.*;
import com.example.song_finder_fx.Session.UserSession;
import com.example.song_finder_fx.Session.UserSummary;
import com.itextpdf.layout.Document;
import com.opencsv.exceptions.CsvValidationException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, CsvValidationException, InterruptedException {
        DatabasePostgres.refreshSummaryTable(Months.JULY, 2024);
        // testBulkReporting();
        // April 0.6305, 184.65
        // March 0.6285, 186.78
        // getArtistReport(0.6285, 186.78, "Mahesh Vithana", 2024, 8, "C:\\Users\\bhash\\Documents\\Test\\ReportsBulk\\2024_august_mahesh_vithana.pdf");
        // testArtistReportsNew();
        // testNewArtistReportPDF();

        // testDashboard();
        // UserSession us = new UserSession();
        // DatabasePostgres.changePassword("sandumini", "admin");

        // testYouTubeMonitoring();
        // testYTMAddChannel();

        // isrcDispatcherTest();
        // catalogNumberGenTest();
        // testUPCGenerator();

        // System.out.println(TextFormatter.formatTime("0.17"));
    }

    private static void testYTMAddChannel() {
        // YoutubeDownload.downloadAudio("https://www.youtube.com/watch?v=VPLQqrhKNPk", "D:\\CeyMusic\\Ingests\\2024.10.02", "VPLQqrhKNPk.flac", null);
        YoutubeData channel = new YoutubeData();
        channel.setName("Test Name");
        channel.setType(1);
        channel.setUrl("https://www.youtube.com/channel/UC4WjwE9qz3wQyqFZKg9uN5g");

        YoutubeDownload youtubeDownload = new YoutubeDownload();
        youtubeDownload.addChannelToDatabase(channel);
    }

    private static void testUPCGenerator() {
        UPCGenarator upcGenarator = new UPCGenarator();

        List<String> upcList = upcGenarator.viewUpcList(10);
        for (String upc : upcList) {
            System.out.println(upc);
        }

        UpcData upcData = new UpcData();
        String upc = "4796033752004";
        String productName = "Test Product";
        String type = ProductType.UGC;
        String username = Main.userSession.getUserName();
        upcData.setUpcNumber(upc);
        upcData.setProductName(productName);
        upcData.setType(type);
        upcData.setUser(username);
    }

    private static void catalogNumberGenTest() throws SQLException {
        CatalogNumberGenerator generator = new CatalogNumberGenerator();
        String artist = "Abhisheka Wimalaweera";
        // String catalogNumber = generator.generateCatalogNumber(artist);
        // System.out.println("Generated catalog number for: " + artist + " | " + catalogNumber);
        List<String> catalogNumbers = generator.generateCatalogNumbers(artist, 10);
        System.out.println("Generated catalog numbers for: " + artist + " | " + catalogNumbers);
    }

    private static void isrcDispatcherTest() throws SQLException {
        ISRCDispatcher dispatcher = new ISRCDispatcher();
        // String isrc = dispatcher.dispatchSingleISRC("SR");

        // Dispatch Single ISRC
        /*String isrc = dispatcher.dispatchSingleISRC("SR");
        System.out.println("Generated ISRC: " + isrc);
        if (dispatcher.updateLastISRC(isrc, "SR")) {
            System.out.println("Updated ISRC: " + isrc);
        } else {
            System.out.println("Failed to update ISRC: " + isrc);
        }*/

        // Dispatch multiple ISRCs
        List<String> isrcs = dispatcher.dispatchMultipleISRCs(10, "SR");
        for (String isrc : isrcs) {
            System.out.println("Generated ISRC: " + isrc);
        }
        dispatcher.updateLastISRC(isrcs.getLast(), "SR");
    }

    private static void testYouTubeMonitoring() {
        List<List<Map<String, String>>> list = YoutubeDownload.getProgramListByChannel();

        for (List<Map<String, String>> list1 : list) {
            for (Map<String, String> map : list1) {
                // Fetching Details
                String channelName = map.get("channelName");
                String title = map.get("Title");
                String url = map.get("Url");
                String thumbnail = map.get("Thumbnail");
                String releaseDate = map.get("releaseDate");

                System.out.println("Channel Name: " + channelName);
                System.out.println("Title: " + title);
                System.out.println("URL: " + url);
                // System.out.println("Thumbnail: " + thumbnail);
                System.out.println("Release Date: " + releaseDate);
                System.out.println();
            }
        }
    }

    public static Image createImageFromText(String text, String preferredFontName) {
        int width = 350;
        int height = 20;

        // Create a buffered image
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics object
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set the background color
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Find a suitable font
        Font font = findSuitableFont(preferredFontName, text);
        g2d.setFont(font.deriveFont(14f));  // Set font size to 24

        // Set the text color
        g2d.setColor(Color.BLACK);

        // Enable anti-aliasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw the text
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2d.drawString(text, 0, height / 2 + textHeight / 4);

        // Dispose the graphics object
        g2d.dispose();

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private static Font findSuitableFont(String preferredFontName, String text) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        // First, try the preferred font
        for (Font font : fonts) {
            if (font.getName().equals(preferredFontName) && font.canDisplayUpTo(text) == -1) {
                System.out.println("Using preferred font: " + font.getName());
                return font;
            }
        }

        // If preferred font doesn't work, find any font that can display the text
        for (Font font : fonts) {
            if (font.canDisplayUpTo(text) == -1) {
                System.out.println("Using font: " + font.getName());
                return font;
            }
        }

        // If no suitable font found, return a default font
        System.out.println("No suitable font found. Using default.");
        return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }

    private static void testNewArtistReportPDF() throws SQLException, IOException, ClassNotFoundException {
        int month = 6;
        int year = 2024;
        double eurToAudRate = 0.63475;
        double audToLkrRate = 191.86;

        ArrayList<String> names = new ArrayList<>(Arrays.asList("Methun SK", "Ridma Weerawardena", "Senanga Dissanayake", "WAYO", "Aruna Lian", "Abhisheka Wimalaweera"));

        for (String name : names) {
            ArtistReport report = getArtistReportNew(0, eurToAudRate, audToLkrRate, name, year, month, true);
            ReportPDFNew pdf = new ReportPDFNew();
            pdf.generateReport("C:\\Users\\bhash\\Documents\\Test\\ReportsNewArtists\\" + month + "_" + year + "_" + name + ".pdf", report);
            System.out.println("\n========\n\nReport for " + report.getArtist().getName() + " is generated and saved in: " + pdf.getReportPath());
        }

        /*ArtistReport report = getArtistReportNew(0, 0.63475, 191.86, "Abhisheka Wimalaweera", 2024, 6, true);

        ReportPDFNew pdf = new ReportPDFNew();
        pdf.generateReport("C:\\Users\\bhash\\Documents\\Test\\ReportsNewArtists\\6_2024_Abhisheka_Wimalaweera.pdf", report);
        System.out.println("\n========\n\nReport for " + report.getArtist().getName() + " is generated and saved in: " + pdf.getReportPath());*/
    }

    private static void testBulkReporting() throws SQLException, IOException {
        int month = 6;
        int year = 2024;
        double eurToAudRate = 0.63475;
        double audToLkrRate = 191.86;

        ArrayList<String> names = new ArrayList<>(Arrays.asList(
                "Ajantha Ranasinghe",
                "Aloy Gunawardena",
                "Ananda Hewaranhindage",
                "Aruna Lian",
                "Bandula Nanayakkarawasam",
                "C. T. Fernando",
                "Camillus Perera",
                "Chaaminda Rathnasuriya",
                "Chandrarathne Manawasinghe",
                "Charith Senadheera",
                "Daya De Alwis",
                "Dhammika Hettiarachchi",
                "Dharmarathna Perera",
                "Dharmasiri Gamage",
                "Gayaman Dissanayake",
                "Gunadasa Kapuge",
                "Hemasiri Halpita",
                "Indrajith Mirihana",
                "Jagath J Edirisinghe",
                "Karunaratne Abeysekera",
                "KDK Dharmawardena",
                "Kularatne Ariyawansa",
                "Kumaradasa Saputhanthri",
                "Lionel Algama",
                "Mahagama Sekara",
                "Mahesh Vithana",
                "Mahinda Dissanayake",
                "Maithree Panagoda",
                "Menaka Chandimal",
                "Methun SK",
                "Miyuru Sangeeth",
                "Mohommed Salih",
                "Naadagama",
                "Nadeeka Jayawardena",
                "Nalaka Sajee",
                "Namal Udugama",
                "Nanda Malini",
                "P. L. A. Somapala",
                "Premasiri Khemadasa",
                "Rajee Wasantha Welgama",
                "Rathna Sri Wijesinghe",
                "Ravi Siriwardhana",
                "Ridma Weerawardena",
                "Rodney Vidhana Pathirana",
                "Saman Athaudahetti",
                "Saman Kularatne",
                "Samudra Wettasinghe",
                "Sanath Nandasiri",
                "Sangeeth Wijesuriya",
                "Sarath Dasanayake",
                "Sarath De Alwis",
                "Sarathchandra Attygalle",
                "Senanga Dissanayake",
                "Shantha Deshabandu",
                "Shirley Waijayantha",
                "Somadasa Elvitigala",
                "Stanley Peiris",
                "Sunil Ariyaratne",
                "Sunil Dharmasena",
                "Sunil R Gamage",
                "Sunil Sarath Perera",
                "Vasantha Kumara Kobawaka",
                "Victor Rathnayake",
                "W.D. Amaradeva",
                "Wasana Ediriarachchi",
                "WAYO",
                "Yamuna Malini Perera"
        ));

        // Refreshing Tables
        // DatabasePostgres.refreshSummaryTable(month, year);

        List<String> payees = DatabasePostgres.getPayees(month, year);

        System.out.println("\nTotal Number of Payees for " + ItemSwitcher.setMonth(month) + ": " + payees.size() + "\n");

        String path = "C:\\Users\\bhash\\Documents\\Test\\ReportsBulk";

        for (String payee : payees) {
            // System.out.println(payee);
            if (names.contains(payee)) {
                System.out.println("Available in the list: " + payee);
                getArtistReport(
                        eurToAudRate,
                        audToLkrRate,
                        payee,
                        year,
                        month,
                        path + "\\" + year + "_" + ItemSwitcher.setMonth(month).toLowerCase() + "_" + payee.toLowerCase().replace(" ", "_") + ".pdf");
            } else {
                System.out.println("Not Available in the list: " + payee);
            }
        }
    }

    private static void testArtistReportsNew() throws SQLException {
        // String.format("%,9.2f", report.getGrossRevenueInAUD())
        int artistID = 47;
        double eurToAudRate = 0.6305;
        double audToLkrRate = 184.65;
        String artistName = "Methun SK";
        int year = 2024;
        int month = 4;

        // Creating artist model by passing artistID
        ArtistReport report = getArtistReportNew(artistID, eurToAudRate, audToLkrRate, artistName, year, month, false);

        // Then get gross revenue, partner share, conversion rate, date, top performing songs, and co-writer payment summary from the report model
        double grossRevenue = report.getGrossRevenueInAUD();
        double partnerShare = report.getPartnerShareInAUD();
        ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();
        List<CoWriterSummary> coWriterSummaries = report.getCoWriterPaymentSummary();
        List<CoWriterShare> coWriterShares = report.getAssetBreakdown();

        // Printing calculated values
        System.out.println("\nCalculated Details for Selected Artist");

        System.out.println("\n========");

        System.out.println("\nArtist: " + report.getArtist().getName());
        System.out.println("EUR to AUD Conversion Rate: " + report.getEurToAudRate());
        System.out.println("AUD to LKR Conversion Rate: " + report.getAudToLkrRate());
        System.out.println("Date: " + report.getDate());

        System.out.println("\n========");

        System.out.println("\nGross Revenue: AUD " + String.format("%,9.2f", grossRevenue));
        System.out.println("Partner Share: AUD " + String.format("%,9.2f", partnerShare));
        System.out.println("\n========");

        System.out.println("\nTop Performing Songs");
        for (Songs song : topPerformingSongs) {
            System.out.println(song.getTrackTitle() + " | AUD: " + String.format("%,9.2f", (song.getRoyalty() * eurToAudRate)));
        }

        System.out.println("\n========");

        System.out.println("\nCo-Writer Share Summary");
        for (CoWriterSummary summary : coWriterSummaries) {
            String contributor = summary.getContributor();
            double royalty = summary.getRoyalty();
            System.out.println(contributor + " | AUD: " + String.format("%,9.2f", royalty * eurToAudRate));
        }

        System.out.println("\nCo-Writer Share Detailed");
        for (CoWriterShare share : coWriterShares) {
            String songName = share.getSongName();
            String songType = share.getSongType();
            String percentage = share.getShare();
            double artistShare = share.getRoyalty() * eurToAudRate;
            String coWriter = share.getContributor();

            System.out.println(songName + " | " + songType + " | " + percentage + " | AUD" + String.format("%,9.2f", artistShare) + " | " + coWriter);
        }
    }

    private static ArtistReport getArtistReportNew(int artistID, double eurToAudRate, double audToLkrRate, String artistName, int year, int month, boolean includeTerritoryAndDSPBreakdown) throws SQLException {
        Artist artist = new Artist(artistID, artistName);

        // Creating revenue report model by passing artist object and conversion rate
        ArtistReport report = new ArtistReport(artist, eurToAudRate, audToLkrRate, year, month);

        // Creating revenue report controller object
        RevenueReportController revenueReportController = new RevenueReportController(report);

        // Revenue report controller will have a method called calculate revenue (inputs report object and returns gross revenue, partner share, conversion rate, date, co-writer payment summary via report object)
        report = revenueReportController.calculateRevenue(includeTerritoryAndDSPBreakdown);

        return report;
    }

    private static void testDashboard() throws SQLException {
        UserSession session = new UserSession();
        UserSummary summary = new UserSummary(session);

        System.out.println(summary.getGreeting());
        System.out.println(summary.getManualClaimCount());
        System.out.println(summary.getReportDayCount());
    }

    private static void getArtistReport(double eurToAudRate, double audToLkrRate, String artistName, int year, int month, String path) throws SQLException, IOException {
        // DatabasePostgres.refreshSummaryTable(month, year);

        ArtistReport report = getArtistReportNew(0, eurToAudRate, audToLkrRate, artistName, year, month, false);

        // System.out.println("report.getGrossRevenue() = " + report.getGrossRevenue());

        ReportPDF pdf = new ReportPDF();
        Document document = pdf.generateReport(path, report);
        System.out.println("\n========\n\nReport for " + report.getArtist().getName() + " is generated and saved in: " + path);
    }

    private static void testArtistReports() throws SQLException {
        int artistID = 47;
        int conversionRate = 1;
        String artistName = "Rohana Weerasinghe";
        int month = 2;
        int year = 2024;

        // Creating artist model by passing artistID
        ArtistReport report = getArtistReportNew(artistID, conversionRate, 0, artistName, month, year, false);

        // Then get gross revenue, partner share, conversion rate, date, top performing songs, and co-writer payment summary from the report model
        double grossRevenue = report.getGrossRevenueInLKR();
        double partnerShare = report.getPartnerShareInLKR();
        ArrayList<Songs> topPerformingSongs = report.getTopPerformingSongs();
        List<CoWriterSummary> coWriterSummaries = report.getCoWriterPaymentSummary();
        List<CoWriterShare> coWriterShares = report.getAssetBreakdown();

        // Printing calculated values
        System.out.println("\nCalculated Details for Selected Artist");

        System.out.println("\n========");

        System.out.println("\nArtist: " + report.getArtist().getName());
        System.out.println("EUR to LKR Conversion Rate: " + report.getEurToAudRate());
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

    /*private static ArtistReport getArtistReport(int artistID, int conversionRate, String artistName, int month, int year) throws SQLException {
        Artist artist = new Artist(artistID, artistName);

        // Creating revenue report model by passing artist object and conversion rate
        ArtistReport report = new ArtistReport(artist, conversionRate, month, year);

        // Creating revenue report controller object
        RevenueReportController revenueReportController = new RevenueReportController(report);

        // Revenue report controller will have a method called calculate revenue (inputs report object and returns gross revenue, partner share, conversion rate, date, co-writer payment summary via report object)
        report = revenueReportController.calculateRevenue();
        return report;
    }*/

    private static void testSearch() {
        // DatabaseHandler databaseHandler = new DatabaseHandler();
        SongSearch songSearch = new SongSearch();

        List<Songs> songs = songSearch.searchSong("Mawathe", SearchType.SONG_NAME, true);

        for (Songs song : songs) {
            String trackTitle = song.getTrackTitle();
            String songType = song.getTypeConverted();
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
