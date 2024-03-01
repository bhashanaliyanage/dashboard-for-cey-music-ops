package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.FUGAReport;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabasePostgre {
    private static Connection conn;

    public static void main(String[] args) throws IOException, CsvValidationException, SQLException {
        File file = new File("src/main/resources/com/example/song_finder_fx/CeyMusic Song Database  - Song Artist DB Import - 03.csv");
        CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()));
        reader.readNext(); // Skipping the header
        String[] nextLine;
        Connection conn = getConn();
        ResultSet rs;
        ResultSet rsArtistInsert;

        while ((nextLine = reader.readNext()) != null) {
            System.out.println("ISRC = " + nextLine[0]);
            String isrc = nextLine[0];
            String artistType = nextLine[1];
            String artistName = nextLine[2];

            String insertQuery = String.format("INSERT INTO public.song_artist (song_isrc, artist_id, artist_type) VALUES ('%s', (SELECT artist_id FROM public.artists WHERE artist_name = '%s' LIMIT 1), '%s');"
                    , isrc, artistName, artistType);
            String selectQuery = String.format("SELECT artist_id FROM public.artists WHERE artist_name = '%s';", artistName);
            String insertArtistQuery = String.format("INSERT INTO public.artists (artist_name) VALUES ('%s') RETURNING artist_id;", artistName);

            Statement statement = conn.createStatement();
            rs = statement.executeQuery(selectQuery);
            if (rs.isBeforeFirst()) { // If Artist Available
                rs.next();
                int artistID = rs.getInt(1);
                // System.out.println("artistID = " + artistID);
                statement = conn.createStatement();

                statement.executeUpdate(insertQuery);
            } else { // If artist not available
                rsArtistInsert = statement.executeQuery(insertArtistQuery);
                rsArtistInsert.next();
                int artistID = rsArtistInsert.getInt(1);
                System.out.println("artistID = " + artistID);
                insertQuery = String.format("INSERT INTO public.song_artist (song_isrc, artist_id, artist_type) VALUES ('%s', '%s', '%s');"
                        , isrc, artistID, artistType);
                statement = conn.createStatement();
                statement.executeUpdate(insertQuery);
            }
        }
        // System.out.println("reader = " + nextLine[0]);
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

    public static List<String> getAllSongTitles() throws SQLException {
        String query = "SELECT song_name FROM public.song_metadata GROUP BY song_name ORDER BY song_name ASC;";
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        List<String> songs = new ArrayList<>();

        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String song = rs.getString(1);
            songs.add(song);
        }

        return songs;
    }

    public static Songs searchContributors(String songName) {
        String query = "SELECT song_name, artist, artist_type FROM public.song_metadata WHERE song_name = '%s' ORDER BY song_name ASC;";
        return null;
    }
}
