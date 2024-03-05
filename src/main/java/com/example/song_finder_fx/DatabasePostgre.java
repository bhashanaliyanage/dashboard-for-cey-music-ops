package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.FUGAReport;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabasePostgre {
    private static Connection conn;

    public static void main(String[] args) throws IOException, CsvValidationException, SQLException {
        searchContributors("Mawathe Geethaya");
    }

    public static Connection getConn() {
        String dbname = "songdata";
        String user = "postgres";
        String pass = "ceymusic";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://192.168.1.200:5432/" + dbname, user, pass);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText(String.valueOf(e));
            Platform.runLater(alert::showAndWait);
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
        Connection conn = getConn();
        String query = "SELECT song_name FROM public.song_metadata GROUP BY song_name ORDER BY song_name ASC;";
        Statement statement = conn.createStatement();
        List<String> songs = new ArrayList<>();

        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String song = rs.getString(1);
            songs.add(song);
        }

        return songs;
    }

    public static Songs searchContributors(String songName) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        Songs song = new Songs();

        String query = String.format("SELECT song_name, artist, artist_type FROM public.song_metadata WHERE song_name = '%s' ORDER BY song_name ASC;", songName);
        ResultSet rs = statement.executeQuery(query);
        song.getContributorsFromRS(rs);

        System.out.println("song.getLyricist() = " + song.getLyricist());
        System.out.println("song.getComposer() = " + song.getComposer());

        return song;
    }

    public static int addManualClaim(String songName, String lyricist, String composer, String url) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("INSERT INTO public.manual_claims (song_name, composer, lyricist, youtube_id) VALUES ('%s', '%s', '%s', '%s');", songName, composer, lyricist, url);
        return statement.executeUpdate(query);
    }

    public static int checkPreviousClaims(String id) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("SELECT COUNT(youtube_id) FROM public.manual_claims WHERE youtube_id = '%s';", id);
        ResultSet rs = statement.executeQuery(query);
        int count = 0;

        while (rs.next()) {
            count = rs.getInt(1);
        }

        return count;
    }

    /*public static String getAddedClaims(String id) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("SELECT * FROM public.manual_claims WHERE youtube_id = '%s';", id);
        ResultSet rs = statement.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {

            }
        }
    }*/
}
