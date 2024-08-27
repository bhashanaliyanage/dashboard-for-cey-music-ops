package com.example.song_finder_fx.Organizer;

import com.example.song_finder_fx.Constants.SearchType;
import com.example.song_finder_fx.Controller.*;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.*;
import com.example.song_finder_fx.Session.UserSession;
import com.example.song_finder_fx.Session.UserSummary;
import com.itextpdf.layout.Document;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, CsvValidationException {
        // DatabasePostgres.refreshSummaryTable(4, 2024);
        // DatabasePostgres.refreshSongMetadataTable();
        // testBulkReporting();
        // April 0.6305, 184.65
        // March 0.6285, 186.78
        getArtistReport(0.6285, 186.78, "Mahesh Vithana", 2024, 4, "C:\\Users\\bhash\\Documents\\Test\\ReportsBulk\\2024_april_mahesh_vithana_edit.pdf");
        // testArtistReportsNew();
        // testNewArtistReportPDF();

        // testDashboard();
        // UserSession us = new UserSession();
        // DatabasePostgres.changePassword("gimhaar", "admin");

        // updateMayReport();
    }

    private static void updateMayReport() throws IOException, SQLException, CsvValidationException {
        File csv = new File("D:\\CeyMusic\\CeyMusic Software Dev\\Tools\\Report Generator\\May2024StatementRun_IslandDreamRecords-standard-EUR\\May2024StatementRun_IslandDreamRecords-royalty_product_and_asset.csv");
        if (csv.canRead()) {
            ReportMetadata report = new ReportMetadata("may_2024", 5, 2024, csv);
            report.setId(21);

            String sql = "UPDATE public.reports_new SET asset_quantity = ? WHERE report_id = ? AND asset_isrc = ?;";

            final int BATCH_SIZE = 1000;

            try (BufferedReader bReader = new BufferedReader(new FileReader(report.getCsvFile()));
                 CSVReader reader = new CSVReader(bReader);
                 Connection con = DatabasePostgres.getConn()) {

                con.setAutoCommit(false);  // Turn off auto-commit

                try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                    String[] nextLine;
                    reader.readNext(); // Skip header

                    int totalRows = 0;
                    int processedRows = 0;
                    int batchCount = 0;

                    System.out.println("Starting update process...");

                    while ((nextLine = reader.readNext()) != null) {
                        FUGAReport fugaReport = CSVController.getFUGAReport(nextLine);
                        pstmt.setInt(1, fugaReport.getAssetQuantity());
                        pstmt.setInt(2, report.getId());
                        pstmt.setString(3, fugaReport.getAssetISRC());
                        pstmt.addBatch();

                        batchCount++;
                        processedRows++;
                        totalRows++;

                        if (batchCount >= BATCH_SIZE) {
                            pstmt.executeBatch();
                            con.commit();
                            System.out.printf("Processed %d rows\n", processedRows);
                            batchCount = 0;
                        }
                    }

                    // Execute any remaining records in the final batch
                    if (batchCount > 0) {
                        pstmt.executeBatch();
                        con.commit();
                    }

                    System.out.println("Update process completed. Total rows processed: " + totalRows);
                } catch (SQLException e) {
                    con.rollback();
                    throw e;
                } finally {
                    con.setAutoCommit(true);  // Reset to default behavior
                }
            }
        } else {
            System.out.println("Cannot read file: " + csv.getAbsolutePath());
        }
    }

    private static void testNewArtistReportPDF() throws SQLException, IOException, ClassNotFoundException {
        ArtistReport report = getArtistReportNew(0, 0.6305, 184.65, "Ridma Weerawardena", 2024, 5, true);

        ReportPDFNew pdf = new ReportPDFNew();
        pdf.generateReport("C:\\Users\\bhash\\Documents\\Test\\ReportsNewArtists\\2024_may_ridma.pdf", report);
        System.out.println("\n========\n\nReport for " + report.getArtist().getName() + " is generated and saved in: " + pdf.getReportPath());
    }

    private static void testAssignPayee() throws SQLException {
        Ingest ingest = DatabasePostgres.getIngest(12);
        assert ingest != null;
        List<IngestCSVData> data = ingest.getIngestCSVDataList();
        IngestCSVDataController controller = new IngestCSVDataController(data.getFirst());

        System.out.println("Assigning Payees...");

        IngestCSVData data2 = controller.assignPayees();

        System.out.println("\nEdit Data on: " + data2.getTrackTitle());
        System.out.println("Composer: " + data2.getComposer());
        System.out.println("Lyricist: " + data2.getLyricist());
        System.out.println("Payee 01: " + data2.getPayee().getPayee1());
        System.out.println("Share: " + data2.getPayee().getShare1());
        System.out.println("Payee 02: " + data2.getPayee().getPayee2());
        System.out.println("Share: " + data2.getPayee().getShare2());
        System.out.println("Payee 03: " + data2.getPayee().getPayee3());
    }

    public static String testApiCall() {
        String s = "";
        String url = "http://192.168.1.32:8080/artist";
        String urlpart = "/allart";
        url = url + urlpart;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            System.out.println("here");
            s = response.body();

            System.out.println("here1");
            // return response.body();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private static void testBulkReporting() throws SQLException, IOException {
        int month = 5;
        int year = 2024;
        double eurToAudRate = 0.6305;
        double audToLkrRate = 184.65;

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

    private static void testRemoveAllReports() throws SQLException {
        List<ReportMetadata> reports = DatabasePostgres.getAllReports();

        for (ReportMetadata report : reports) {
            boolean status = report.remove();
            if (status) {
                System.out.println("Report of the month of " + report.getReportMonth() + " is deleted.");
            }
        }
    }

    private static void testAddNewFugaReport() {
        ReportMetadata report = new ReportMetadata("Test March", 1, 2024, new File("D:\\CeyMusic\\CeyMusic Software Dev\\Tools\\Report Generator\\March2024StatementRun_IslandDreamRecords-standard\\March2024StatementRun_IslandDreamRecords-royalty_product_and_asset.csv"));
        try {
            DatabasePostgres.importReport(report);
        } catch (SQLException | IOException | CsvValidationException e) {
            throw new RuntimeException(e);
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

    private static void testStoreIngests() throws SQLException {
        File file = new File("C:\\Users\\bhash\\Downloads\\Catalog Ingestion CSV Downloads\\PRK-CEY-001-004.csv");
        IngestController ingestController = new IngestController();
        String status = ingestController.insertIngest("test", LocalDate.now(), file);
        System.out.println("\nImport Status: " + status);
    }

    private static void testArchivedManualClaims() throws SQLException {
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
