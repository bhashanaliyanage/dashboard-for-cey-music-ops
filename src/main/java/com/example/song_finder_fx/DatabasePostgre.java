package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.FUGAReport;
import com.example.song_finder_fx.Model.ManualClaimTrack;
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

    public static String getManualClaimCount() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "SELECT COUNT(youtube_id) FROM public.manual_claims WHERE ingest_status = false AND archive = false;";
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        int count = rs.getInt(1);
        return String.valueOf(count);
    }

    public static ResultSet getTop5Territories() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "SELECT territory, SUM(reported_royalty) AS total_royalty\n" +
                "FROM public.report GROUP BY territory ORDER BY total_royalty DESC LIMIT 5;";
        return statement.executeQuery(query);
    }

    public static ResultSet getTop4DSPs() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "SELECT dsp, SUM(reported_royalty) AS total_royalty\n" +
                "FROM public.report GROUP BY dsp ORDER BY total_royalty DESC LIMIT 5;";
        return statement.executeQuery(query);
    }

    public static String getTotalAssetCount() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "SELECT COUNT(*) FROM (SELECT asset_isrc FROM public.report GROUP BY asset_isrc) AS subquery;";
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();

        return resultSet.getString(1);
    }

    public static ResultSet getTop5StreamedAssets() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String query = "SELECT asset_isrc, song_name, reported_royalty FROM public.song_royalty LIMIT 5;";
        return statement.executeQuery(query);
    }

    public static String getSalesDate() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "SELECT sale_start_date FROM public.report LIMIT 1;";
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        return resultSet.getString(1);
    }

    public static List<ManualClaimTrack> getManualClaims() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "SELECT * FROM public.manual_claims WHERE ingest_status = false AND archive = false;";
        ResultSet resultSet = statement.executeQuery(query);
        List<ManualClaimTrack> manualClaims = new ArrayList<>();

        if (resultSet.isBeforeFirst()) {
            while (resultSet.next()) {
                int id = resultSet.getInt(6);
                String songName = resultSet.getString(1);
                String composer = resultSet.getString(2);
                String lyrics = resultSet.getString(3);
                String youTubeLink = "https://www.youtube.com/watch?v=" + resultSet.getString(4);

                ManualClaimTrack manualClaimTrack = new ManualClaimTrack(id, songName, lyrics, composer, youTubeLink);
                manualClaims.add(manualClaimTrack);
            }
        }

        return manualClaims;
    }

    public static void archiveSelectedClaim(String songNo) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("UPDATE public.manual_claims SET archive = TRUE WHERE claim_id = %s;", songNo);

        statement.executeUpdate(query);
    }
}
