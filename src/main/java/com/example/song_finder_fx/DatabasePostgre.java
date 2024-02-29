package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.FUGAReport;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

import static com.example.song_finder_fx.Controller.CSVController.getFUGAReport;

public class DatabasePostgre {
    private static Connection conn;

    public static void main(String[] args) throws IOException, CsvValidationException, SQLException {
        File file = new File("src/main/resources/com/example/song_finder_fx/November2023StatementRun_IslandDreamRecords-royalty_product_and_asset.csv");
        CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()));
        reader.readNext(); // Skipping the header
        String[] nextLine;
        long startTime = System.nanoTime();
        Connection conn = getConn();

        while ((nextLine = reader.readNext()) != null) {
            FUGAReport report = getFUGAReport(nextLine);
            System.out.println("ISRC = " + nextLine[17]);
        }
        // System.out.println("reader = " + nextLine[0]);

        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000;

        System.out.println("Execution time: " + durationInSeconds + " seconds");
    }

    public static Connection getConn() {
        String dbname = "songdata";
        String user = "postgres";
        String pass = "ceymusic";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://192.168.1.200:5432/" + dbname, user, pass);

            if (conn != null) {
                System.out.println("Connection Established!");
            } else {
                System.out.println("Error Connecting to Database");
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

        return conn;
    }

    public static void getConn2() {
        String dbname = "songdata";
        String user = "postgres";
        String pass = "ceymusic";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, pass);

            if (conn != null) {
                System.out.println("Connection Established!");
            } else {
                System.out.println("Error Connecting to Database");
            }
        } catch (Exception e) {
            System.out.println("e = " + e);
        }
    }

    public static int addRowFUGAReport(FUGAReport report, Connection conn) throws SQLException {
        try {
            String query = String.format("INSERT INTO public.report (asset_isrc, reported_royalty, territory, sale_start_date, dsp) VALUES ('%s', '%s', '%s', '%s', '%s');",
                    report.getAssetISRC(),
                    report.getReportedRoyalty(),
                    report.getTerritory(),
                    report.getSaleStartDate(),
                    report.getDsp());

            Statement statement = conn.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ResultSet getFullBreakdown() throws SQLException {
        Connection conn = getConn();
        String query = """
                SELECT asset_isrc,
                SUM(reported_royalty) AS Reported_Royalty_Summary,
                SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END) AS AU_Earnings,
                (SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9 AS After_GST,
                SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END) AS Other_Territories_Earnings,
                ((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END)) AS Reported_Royalty_After_GST,
                (((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC
                FROM public.report GROUP BY Asset_ISRC ORDER BY Reported_Royalty_Summary DESC;""";
        Statement statement = conn.createStatement();
        return statement.executeQuery(query);
    }
}
