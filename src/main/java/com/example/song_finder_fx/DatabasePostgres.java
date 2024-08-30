package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.CSVController;
import com.example.song_finder_fx.Controller.ItemSwitcher;
import com.example.song_finder_fx.Model.*;
import com.example.song_finder_fx.Session.Hasher;
import com.example.song_finder_fx.Session.User;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.postgresql.util.PSQLException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static com.example.song_finder_fx.Controller.CSVController.getReportTotalRowCount;

public class DatabasePostgres {

    static StringBuilder errorBuffer = new StringBuilder();
    private static Connection conn;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static Connection getConn() {
        // LocalHost
        String ip = "jdbc:postgresql://192.168.1.200:5432/";
        String user = "postgres";
        String pass = "ceymusic";

        String dbname = "songdata";

        String ip2 = "jdbc:postgresql://192.168.40.2:5432/";
        String user2 = "cmops";
        String pass2 = "CeyC0ff39@Moun#ta1n";

        String user3 = "sudeshsan";
        String pass3 = "sUDESH@#";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(ip2 + dbname, user3, pass3);
        } catch (Exception e) {
            System.out.println("Error Connecting to database = " + e);
        }

        return conn;
    }

    public static void closeConnection(Connection con) {
        try {
            con.close();
        } catch (Exception e) {
            System.out.println("Error on Closing Connection: " + e);
        }
    }

    public static int addRowFUGAReport(FUGAReport report, Connection conn) throws SQLException {
        try {
            String query = String.format("INSERT INTO public.report (asset_isrc, reported_royalty, territory, sale_start_date, dsp, upc) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                    report.getAssetISRC(),
                    report.getReportedRoyalty(),
                    report.getTerritory(),
                    report.getSaleStartDate(),
                    report.getDsp(),
                    report.getProductUPC());

            Statement statement = conn.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error Inserting row into report: " + e);
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
        String query = "SELECT song_name FROM public.song_metadata_new WHERE type = 'O' GROUP BY song_name ORDER BY song_name ASC;";
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

        String query = String.format("SELECT song_name, composer, lyricist FROM public.song_metadata_new WHERE song_name = '%s' ORDER BY song_name ASC;", songName);
        ResultSet rs = statement.executeQuery(query);
        if (rs.isBeforeFirst()) {
            rs.next();
            song.setComposer(rs.getString(2));
            song.setLyricist(rs.getString(3));
            // song.getContributorsFromRS(rs);

            System.out.println("song.getLyricist() = " + song.getLyricist());
            System.out.println("song.getComposer() = " + song.getComposer());

            return song;
        }
        return song;
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

    public static LocalDate sqlDateToLocalDate(Date date) {
        return Optional.ofNullable(date)
                .map(Date::toLocalDate)
                .orElse(null);
    }

    public static LocalDateTime sqlDateToLocalDateTime(Date date) {
        return Optional.ofNullable(date)
                .map(Date::getTime)
                .map(time -> LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()))
                .orElse(null);
    }

    public static List<ManualClaimTrack> getManualClaims() throws SQLException {
        Connection conn = getConn();
        // Statement statement = conn.createStatement();
        PreparedStatement ps = conn.prepareStatement("SELECT claim_id, song_name, composer, lyricist, youtube_id, trim_start, trim_end, date, claim_type " +
                "FROM public.manual_claims WHERE ingest_status = false AND archive = false ORDER BY claim_type ASC, claim_id ASC;");
        // ps.setBoolean(1, type);
        ResultSet resultSet = ps.executeQuery();
        List<ManualClaimTrack> manualClaims = new ArrayList<>();

        int count = 0;

        if (resultSet.isBeforeFirst()) {
            while (resultSet.next()) {
                count++;

                int id = resultSet.getInt(1);
                String songName = resultSet.getString(2);
                String composer = resultSet.getString(3);
                String lyrics = resultSet.getString(4);
                String youTubeLink = resultSet.getString(5);
                String trimStart = resultSet.getString(6);
                String trimEnd = resultSet.getString(7);
                // byte[] previewImageBytes = resultSet.getBytes(8); // Remove
                // byte[] artworkImageBytes = resultSet.getBytes(9); // Remove
                Date date = resultSet.getDate(8);
                int claimType = resultSet.getInt(9);
                LocalDate localDate = sqlDateToLocalDate(date);

                ManualClaimTrack manualClaimTrack = new ManualClaimTrack(id, songName, lyrics, composer, youTubeLink, localDate, claimType);

                /*// Set the images to model
                try {
                    ByteArrayInputStream previewImageInputStream = new ByteArrayInputStream(previewImageBytes);
                    ByteArrayInputStream artworkImageInputStream = new ByteArrayInputStream(artworkImageBytes);

                    BufferedImage previewImage = ImageIO.read(previewImageInputStream);
                    BufferedImage artwork = ImageIO.read(artworkImageInputStream);
                    // Platform.runLater(() -> System.out.println("artwork.getColorModel() = " + artwork.getColorModel()));

                    manualClaimTrack.setPreviewImage(previewImage);
                    manualClaimTrack.setImage(artwork);
                } catch (IOException e) {
                    Platform.runLater(e::printStackTrace);
                }*/

                if (trimStart != null && trimEnd != null) {
                    manualClaimTrack.addTrimTime(trimStart, trimEnd);
                }

                manualClaims.add(manualClaimTrack);

                System.out.println("count = " + count);
            }
        }

        resultSet.close();

        return manualClaims;
    }

    public static List<ManualClaimTrack> getArchivedManualClaims(LocalDate startDate, LocalDate endDate) throws SQLException {
        /*System.out.println("startDate = " + startDate);
        System.out.println("endDate = " + endDate);*/

        Connection conn = getConn();
        PreparedStatement ps = conn.prepareStatement("""
                SELECT claim_id, song_name, composer, lyricist, youtube_id, trim_start, trim_end, date, claim_type
                FROM public.manual_claims
                WHERE ingest_status = false AND archive = true AND date BETWEEN ? AND ?
                ORDER BY claim_type ASC, claim_id DESC LIMIT 100;
                """);
        ps.setDate(1, Date.valueOf(startDate));
        ps.setDate(2, Date.valueOf(endDate));
        ResultSet resultSet = ps.executeQuery();
        List<ManualClaimTrack> manualClaims = new ArrayList<>();

        int count = 0;

        if (resultSet.isBeforeFirst()) {
            while (resultSet.next()) {
                count++;

                int id = resultSet.getInt(1);
                String songName = resultSet.getString(2);
                String composer = resultSet.getString(3);
                String lyrics = resultSet.getString(4);
                String youTubeLink = resultSet.getString(5);
                String trimStart = resultSet.getString(6);
                String trimEnd = resultSet.getString(7);
                // byte[] previewImageBytes = resultSet.getBytes(8);
                // byte[] artworkImageBytes = resultSet.getBytes(9);
                Date date = resultSet.getDate(8);
                int claimType = resultSet.getInt(9);
                LocalDate localDate = sqlDateToLocalDate(date);

                ManualClaimTrack manualClaimTrack = new ManualClaimTrack(id, songName, lyrics, composer, youTubeLink, localDate, claimType);

                // Set the images to model
                /*try {
                    ByteArrayInputStream previewImageInputStream = new ByteArrayInputStream(previewImageBytes);
                    ByteArrayInputStream artworkImageInputStream = new ByteArrayInputStream(artworkImageBytes);

                    BufferedImage previewImage = ImageIO.read(previewImageInputStream);
                    BufferedImage artwork = ImageIO.read(artworkImageInputStream);
                    // Platform.runLater(() -> System.out.println("artwork.getColorModel() = " + artwork.getColorModel()));

                    manualClaimTrack.setPreviewImage(previewImage);
                    manualClaimTrack.setImage(artwork);
                } catch (IOException e) {
                    Platform.runLater(e::printStackTrace);
                }*/

                if (trimStart != null && trimEnd != null) {
                    manualClaimTrack.addTrimTime(trimStart, trimEnd);
                }

                manualClaims.add(manualClaimTrack);

            }

            System.out.println("\rTotal: " + count);
        }

        resultSet.close();

        return manualClaims;
    }

    public static void archiveSelectedClaim(String songNo) throws SQLException {
        String query = "UPDATE public.manual_claims SET archive = TRUE WHERE claim_id = ?;";

        try (Connection conn = getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(songNo));
            stmt.executeUpdate();
        }
    }

    public static void editManualClaim(String songID, String trackName, String composer, String lyricist, String trimStart, String trimEnd) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();

        String query = String.format("UPDATE public.manual_claims SET song_name = '%s', composer = '%s', lyricist = '%s', trim_start = '%s', trim_end = '%s' WHERE claim_id = %s;",
                trackName, composer, lyricist, trimStart, trimEnd, songID);

        statement.executeUpdate(query);
    }

    public static String getCatNo(String composer, String lyricist) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("SELECT cat_no_handler, last_cat_no FROM public.artists WHERE artist_name = '%s';", composer);
        ResultSet rs = statement.executeQuery(query);

        String result;

        if (rs.isBeforeFirst()) {
            rs.next();
            String prefix = rs.getString(1);
            int suffix = rs.getInt(2);
            suffix++;
            String formattedSuffix = String.format("%03d", suffix);
            result = prefix + "-CEY-" + formattedSuffix;
        } else {
            query = String.format("SELECT cat_no_handler, last_cat_no FROM public.artists WHERE artist_name = '%s'", lyricist);
            rs = statement.executeQuery(query);
            if (rs.isBeforeFirst()) {
                rs.next();
                String prefix = rs.getString(1);
                int suffix = rs.getInt(2);
                suffix++;
                String formattedSuffix = String.format("%03d", suffix);
                result = prefix + "-CEY-" + formattedSuffix;
            } else {
                result = null;
            }
        }
        return result;
    }

    public static void emptyReportTable() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "DELETE FROM public.report;";
        statement.executeUpdate(query);
    }

    public static String searchFileName(String isrc) throws SQLException {
        Connection db = getConn();
        ResultSet rs;
        String filename = null;
        Statement statement = db.createStatement();

        String query = String.format("SELECT FILE_NAME FROM songs WHERE ISRC = '%s'", isrc);

        rs = statement.executeQuery(query);

        while (rs.next()) {
            filename = rs.getString(1);
        }

        return filename;
    }

    public static int addManualClaimIngest(LocalDate date, String userName, String filePath, String ingestFileName) throws SQLException {
        Connection db = getConn();
        String query = String.format("INSERT INTO public.mc_ingests(ingest_date, user_name_note, root_folder, csv_filename) VALUES ('%s', '%s', '%s', '%s') RETURNING ingest_id;", date, userName, filePath, ingestFileName);
        Statement statement = db.createStatement();
        ResultSet rs = statement.executeQuery(query);
        int id = 0;

        if (rs.isBeforeFirst()) {
            rs.next();
            id = rs.getInt(1);
            return id;
        }

        return id;
    }

    /**
     * public static void main(String[] args) throws IOException, CsvValidationException, SQLException, ClassNotFoundException {
     * List<ManualClaimTrack> manualClaims = getManualClaims();
     * System.out.println("manualClaims.size() = " + manualClaims.size());
     * for (ManualClaimTrack claim : manualClaims) {
     * BufferedImage image = claim.getBufferedImage();
     * if (image != null) {
     * // Check if the image is complete (all pixels loaded)
     * boolean isComplete = image.getData().getDataBuffer().getNumBanks() > 0;
     * if (isComplete) {
     * System.out.println("Image is fully loaded.");
     * } else {
     * System.out.println("Image is not fully loaded.");
     * }
     * } else {
     * System.out.println("Image is null. There was an issue loading the image.");
     * }
     * }
     * }
     */

    private static void insertSongArtists(CSVReader csvReader) throws SQLException, IOException, CsvValidationException {
        Connection db = getConn();
        Statement statement = db.createStatement();
        String isrc;
        String artist;
        String artist_type;
        String[] line;

        csvReader.readNext();

        while ((line = csvReader.readNext()) != null) {
            isrc = line[0];
            artist_type = line[1];
            artist = line[2];

            // System.out.println("isrc = " + isrc);

            String query = String.format("INSERT INTO public.song_artist (song_isrc, artist_id, artist_type) " +
                            "VALUES ('%s', (SELECT artist_id FROM public.artists WHERE artist_name = '%s' LIMIT 1), '%s') " +
                            "ON CONFLICT (song_isrc, artist_id, artist_type) DO UPDATE SET artist_type = EXCLUDED.artist_type;",
                    isrc, artist, artist_type);

            try {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                String query2 = String.format("INSERT INTO public.artists(artist_name, status) VALUES ('%s', 3);", artist);
                statement.executeUpdate(query2);
                statement.executeUpdate(query);
                System.out.println("isrc = " + isrc);
                System.out.println("artist = " + artist);
                e.printStackTrace();
            }
        }
    }

    public static int addManualClaim(ManualClaimTrack claim) throws SQLException, IOException {
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO public.manual_claims " +
                        "(song_name, composer, lyricist, youtube_id, trim_start, trim_end, date, claim_type) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        // Get artwork
        // ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
        // ImageIO.write(claim.getBufferedImage(), "jpg", binaryData);
        // byte[] artwork = binaryData.toByteArray();

        // Get previewImage
        // ImageIO.write(claim.getBufferedPreviewImage(), "jpg", binaryData);
        // byte[] previewImage = binaryData.toByteArray();

        // Set values for the prepared statement
        preparedStatement.setString(1, claim.getTrackName());
        preparedStatement.setString(2, claim.getComposer());
        preparedStatement.setString(3, claim.getLyricist());
        preparedStatement.setString(4, claim.getYoutubeID());
        preparedStatement.setString(5, claim.getTrimStart());
        preparedStatement.setString(6, claim.getTrimEnd());
        // preparedStatement.setBytes(7, artwork); // Remove
        // preparedStatement.setBytes(8, previewImage); // Remove
        preparedStatement.setDate(7, Date.valueOf(claim.getDate()));
        preparedStatement.setInt(8, claim.getClaimType());

        // Execute the prepared statement
        return preparedStatement.executeUpdate();
    }

    public static int updateClaimArtwork(String claimID, BufferedImage bufferedImageArtwork, BufferedImage bufferedImagePreview) throws SQLException, IOException {
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE public.manual_claims SET artwork = ?, preview_image = ? WHERE claim_id = ?;");

        // Converting artwork to bytea
        ByteArrayOutputStream binaryDataArtwork = new ByteArrayOutputStream();
        // System.out.println("bufferedImageArtwork = " + bufferedImageArtwork.getColorModel());
        ImageIO.write(bufferedImageArtwork, "jpg", binaryDataArtwork);
        byte[] artwork = binaryDataArtwork.toByteArray();

        // Converting preview image to bytea
        ByteArrayOutputStream binaryDataPI = new ByteArrayOutputStream();
        ImageIO.write(bufferedImagePreview, "jpg", binaryDataPI);
        byte[] previewImage = binaryDataPI.toByteArray();

        preparedStatement.setBytes(1, artwork);
        preparedStatement.setBytes(2, previewImage);
        preparedStatement.setInt(3, Integer.parseInt(claimID));

        return preparedStatement.executeUpdate();
    }

    public static void importToArtistsTable(File csv) throws SQLException, ClassNotFoundException, IOException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement preparedStatement = db.prepareStatement("INSERT INTO artists (ARTIST_NAME) VALUES (?)");

        BufferedReader reader = new BufferedReader(new FileReader(csv));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("line = " + line);
            preparedStatement.setString(1, line);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("Added: " + line);
            } else {
                System.out.println("Error Adding: " + line);
            }
        }

        reader.close();
    }

    private static PreparedStatement getPreparedStatementAddRowToFuga() throws ClassNotFoundException, SQLException {
        Connection db = DatabaseMySQL.getConn();

        return db.prepareStatement("INSERT INTO report " +
                "(Sale_Start_date, Sale_End_date, DSP, Sale_Store_Name, Sale_Type, Sale_User_Type, Territory, " +
                "Product_UPC, Product_Reference, Product_Catalog_Number, Product_Label, Product_Artist, Product_Title, " +
                "Asset_Artist, Asset_Title, Asset_Version, Asset_Duration, Asset_ISRC, Asset_Reference, AssetOrProduct, " +
                "Product_Quantity, Asset_Quantity, Original_Gross_Income, Original_currency, Exchange_Rate, " +
                "Converted_Gross_Income, Contract_deal_term, Reported_Royalty, Currency, Report_Run_ID, Report_ID, " +
                "Sale_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    }


    /*public static ResultSet getTop5StreamedAssets() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Asset_ISRC, Asset_Title, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY Asset_ISRC ORDER BY Total_Royalty DESC LIMIT 5;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }*/

    /*public static ResultSet getTop4DSPs() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT DSP, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY DSP ORDER BY Total_Royalty DESC;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }*/

    /*public static ResultSet getTop5Territories() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Territory, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY Territory ORDER BY Total_Royalty DESC LIMIT 5;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }*/

    /*public static String getTotalAssetCount() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT COUNT(*) FROM ( SELECT Asset_ISRC FROM report GROUP BY Asset_ISRC ) AS subquery;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = ps.executeQuery();
        rs.next();
        return rs.getString(1);
    }*/

    /*public static String getSalesDate() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT Sale_Start_date FROM report LIMIT 1;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = ps.executeQuery();
        rs.next();
        return rs.getString(1);
    }*/

    //    public static ResultSet getFullBreakdown() throws SQLException, ClassNotFoundException {
//        /*SELECT Asset_ISRC, SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END) AS 'AU_Earnings', (SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9 AS After_GST, SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END) AS 'Other_Territories_Earnings', ((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END)) AS Reported_Royalty_After_GST, (((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC FROM report GROUP BY Asset_ISRC;*/
//        Connection db = DatabaseMySQL.getConn();
//
//        PreparedStatement ps = db.prepareStatement("SELECT Asset_ISRC, " +
//                "SUM(Reported_Royalty) AS Reported_Royalty_Summary, " +
//                "SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END) AS 'AU_Earnings', " +
//                "(SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9 AS After_GST, " +
//                "SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END) AS 'Other_Territories_Earnings', " +
//                "((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END)) AS Reported_Royalty_After_GST, " +
//                "(((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC " +
//                "FROM report GROUP BY Asset_ISRC ORDER BY Reported_Royalty_Summary DESC;");
//
//        return ps.executeQuery();
//    }

    public static ResultSet getBreakdownByDSP() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Asset_ISRC AS ISRC, " +
                "(((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC, " +
                "(((SUM(CASE WHEN DSP = 'Youtube Ad Supported' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'Youtube Ad Supported' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Youtube_Ad_Supported, " +
                "(((SUM(CASE WHEN DSP = 'Youtube Music' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'Youtube Music' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Youtube_Music, " +
                "(((SUM(CASE WHEN DSP = 'Spotify' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'Spotify' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Spotify, " +
                "(((SUM(CASE WHEN DSP = 'TikTok' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'TikTok' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS TikTok, " +
                "(((SUM(CASE WHEN DSP = 'Apple Music' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'Apple Music' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Apple_Music, " +
                "(((SUM(CASE WHEN DSP = 'Facebook Audio Library' OR DSP = 'Facebook Fingerprinting' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP = 'Facebook Audio Library' OR DSP = 'Facebook Fingerprinting' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Facebook, " +
                "(((SUM(CASE WHEN DSP != 'Facebook Audio Library' AND DSP != 'Facebook Fingerprinting' AND DSP != 'Youtube Ad Supported' AND DSP != 'Youtube Music' AND DSP != 'Spotify' AND DSP != 'TikTok' AND DSP != 'Apple Music' AND Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN DSP != 'Facebook Audio Library' AND DSP != 'Facebook Fingerprinting' AND DSP != 'Youtube Ad Supported' AND DSP != 'Youtube Music' AND DSP != 'Spotify' AND DSP != 'TikTok' AND DSP != 'Apple Music' AND Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Rest " +
                "FROM report " +
                "GROUP BY Asset_ISRC ORDER BY SUM(Reported_Royalty) DESC;");

        return ps.executeQuery();
    }

    public static ResultSet getBreakdownByTerritory() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT " +
                "Asset_ISRC AS ISRC, " +
                "(((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC, " +
                "((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) * 0.85 AS AU, " +
                "(SUM(CASE WHEN Territory = 'US' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS US, " +
                "(SUM(CASE WHEN Territory = 'GB' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS GB, " +
                "(SUM(CASE WHEN Territory = 'IT' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS IT, " +
                "(SUM(CASE WHEN Territory = 'KR' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS KR, " +
                "(SUM(CASE WHEN Territory = 'LK' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS LK, " +
                "(SUM(CASE WHEN Territory = 'AE' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS AE, " +
                "(SUM(CASE WHEN Territory = 'JP' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS JP, " +
                "(SUM(CASE WHEN Territory = 'CA' THEN Reported_Royalty ELSE 0 END)) * 0.85 AS CA, " +
                "SUM(CASE WHEN Territory != 'AU' AND Territory != 'US' AND Territory != 'GB' AND Territory != 'IT' AND Territory != 'KR' AND Territory != 'LK' AND Territory != 'AE' AND Territory != 'JP' AND Territory != 'CA' THEN Reported_Royalty ELSE 0 END) * 0.85 AS Rest " +
                "FROM report " +
                "GROUP BY Asset_ISRC ORDER BY SUM(Reported_Royalty) DESC;");

        return ps.executeQuery();
    }

    public static ResultSet checkUpdates() throws SQLException, ClassNotFoundException {
        Connection db = getConn();
        ResultSet rs = null;

        PreparedStatement ps = db.prepareStatement("SELECT value, location FROM settings WHERE setting = 'version';");
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Check Settings Table");
            e.printStackTrace();
        }

        return rs;
    }

    public static Updates checkUpdatesNew() throws SQLException {
        // SELECT value, location FROM settings WHERE setting = 'version';
        Connection db = getConn();
        // ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT value, location, details FROM public.settings WHERE setting = 'version';");
        ResultSet rs = ps.executeQuery();

        Updates updates = null;
        if (rs.isBeforeFirst()) {
            rs.next();

            double version = rs.getDouble(1);
            String location = rs.getString(2);
            String details = rs.getString(3);

            updates = new Updates(version, location, details);
        }

        return updates;
    }

    public static ResultSet checkMissingISRCs() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("""
                SELECT report.Asset_ISRC,\s
                (CASE WHEN songs.ISRC = report.Asset_ISRC THEN songs.COMPOSER END) AS Composer,\s
                (CASE WHEN songs.ISRC = report.Asset_ISRC THEN songs.LYRICIST END) AS Lyricist\s
                FROM report\s
                LEFT OUTER JOIN songs ON report.Asset_ISRC = songs.ISRC\s
                GROUP BY Asset_ISRC \s
                ORDER BY `Composer` ASC""", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY
        );

        return ps.executeQuery();
    }

    public static boolean updateSongsTable(File file, Label lblSongDB_Update, Label lblSongDB_Progress) throws IOException, CsvValidationException, SQLException, ClassNotFoundException {
        Platform.runLater(() -> {
            lblSongDB_Update.setText("Updating Song Database");
            lblSongDB_Update.setStyle("-fx-text-fill: '#000000'");
        });
        CSVReader reader = new CSVReader(new FileReader(file));
        reader.readNext(); // Skipping the first line
        String[] row;
        PreparedStatement ps = getInsertSongPreparedStatement();
        PreparedStatement checkISRC_Availability = getCheckISRC_PreparedStatement();
        boolean status = false;

        while ((row = reader.readNext()) != null) {
            String isrc = row[0];

            checkISRC_Availability.setString(1, isrc);
            ResultSet rs = checkISRC_Availability.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                String albumTitle = row[1];
                String upc = row[2];
                String catalogNumber = row[3];
                String primaryArtists = row[4];
                String albumFormat = row[5];
                String trackTitle = row[6];
                String trackVersion = row[7];
                String singer = row[8];
                String featuringArtists = row[9];
                String composer = row[10];
                String lyricist = row[11];
                String originalFileName = row[12];
                String type = row[14];

                ps.setString(1, isrc);
                ps.setString(2, albumTitle);
                ps.setString(3, upc);
                ps.setString(4, catalogNumber);
                ps.setString(5, primaryArtists);
                ps.setString(6, albumFormat);
                ps.setString(7, trackTitle);
                ps.setString(8, trackVersion);
                ps.setString(9, singer);
                ps.setString(10, featuringArtists);
                ps.setString(11, composer);
                ps.setString(12, lyricist);
                ps.setString(13, originalFileName);
                ps.setString(14, type);

                try {
                    ps.executeUpdate();
                    Platform.runLater(() -> {
                        lblSongDB_Progress.setVisible(true);
                        lblSongDB_Progress.setText("Added: " + isrc);
                    });
                    status = true;
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        lblSongDB_Progress.setVisible(true);
                        lblSongDB_Progress.setText("Error!");
                        lblSongDB_Progress.setStyle("-fx-text-fill: '#FF0000'");
                        throw new RuntimeException(e);
                    });
                }
            }
        }
        return status;
    }

    private static PreparedStatement getCheckISRC_PreparedStatement() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        return db.prepareStatement("SELECT COUNT(*) AS Count FROM songs WHERE ISRC = ?");
    }

    private static PreparedStatement getInsertSongPreparedStatement() throws ClassNotFoundException, SQLException {
        Connection db = DatabaseMySQL.getConn();
        return db.prepareStatement("INSERT INTO songs (ISRC," +
                "ALBUM_TITLE," +
                "UPC," +
                "CAT_NO," +
                "PRODUCT_PRIMARY," +
                "ALBUM_FORMAT," +
                "TRACK_TITLE," +
                "TRACK_VERSION," +
                "SINGER," +
                "FEATURING," +
                "COMPOSER," +
                "LYRICIST," +
                "FILE_NAME," +
                "TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
    }

    public static List<String> getPayees(int month, int year) throws SQLException {
        // Refresh tables
        // refreshSummaryTable(month, year);

        String query = """
                 SELECT payee, SUM(adjusted_royalty) AS adjusted_royalty FROM public.summary_bd_03_multiple GROUP BY payee\s
                 ORDER BY adjusted_royalty DESC;
                \s""";

        List<String> list = new ArrayList<>();

        try (Connection db = getConn();
             PreparedStatement preparedStatement = db.prepareStatement(query);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } // The Connection, PreparedStatement, and ResultSet will be closed here.

        return list;
    }

    public static ArrayList<Double> getPayeeGrossRev(String artistName) throws SQLException, ClassNotFoundException {
        Connection connection = getConn();
        ArrayList<Double> royalty = new ArrayList<>();


        PreparedStatement psGetGross = connection.prepareStatement("SELECT report.asset_isrc, " +
                "       ((((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85) * isrc_payees.SHARE/100 AS REPORTED_ROYALTY, " +
                "       (((((SUM(CASE WHEN Territory = 'AU' AND Product_Label = 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' AND Product_Label = 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END))) * 0.85) * 0.1) + (((((SUM(CASE WHEN Territory = 'AU' AND Product_Label != 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' AND Product_Label != 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END))) * 0.85) * 0.1) AS PARTNER_SHARE " +
                "FROM report " +
                "JOIN isrc_payees ON isrc_payees.ISRC = report.Asset_ISRC AND isrc_payees.PAYEE = ? " +
                "GROUP BY report.asset_isrc, isrc_payees.SHARE" +
                "ORDER BY REPORTED_ROYALTY DESC;");


        psGetGross.setString(1, artistName);
        ResultSet rsGross = psGetGross.executeQuery();
        rsGross.next();

        royalty.add(rsGross.getDouble(2));
        royalty.add(rsGross.getDouble(3));

        return royalty;
    }


    //GET  REVN REPORT 24/04/2024
    public static RevenueReport getPayeeGrossRev1(ArtistReport report) {
//		System.out.println("call this");
        RevenueReport rReport = new RevenueReport();
        try {
            rReport.setAfterDeductionRoyalty(getTotalRoyalty(report));
            rReport.setReportedRoyalty(getTotalRoyalty1(report));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rReport;
    }

    public static RevenueReport getPayeeGrossRevNew(ArtistReport report) {
        RevenueReport rReport = new RevenueReport();
        try {
            rReport.setAfterDeductionRoyalty(getTotalRoyalty(report));
            rReport.setReportedRoyalty(getTotalRoyalty1(report));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rReport;
    }

    public static double getTotalRoyalty1(ArtistReport report) {
        double d = 0.0;
        try {
            List<PayeeForReport> pr;
            pr = getPayeeReport1(report.getArtist().getName());

            for (PayeeForReport dd : pr) {
                d = d + dd.getValue();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return d;

    }

    public static double getTotalRoyalty(ArtistReport report) {
        double d = 0.0;
        try {
            List<PayeeForReport> pr;
            pr = getPayeeReport(report);

            for (int i = 0; i < pr.size(); i++) {
                PayeeForReport dd = pr.get(i);
                d = d + dd.getValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println("d = " + d);

        return d;
    }

    public static List<PayeeForReport> getPayeeReport1(String name) {
        System.out.println("\nGetting Payee Report for: " + name + "...");
        String sql = """
                SELECT ip.isrc,
                SUM(CASE WHEN ip.payee = ? THEN ip.share WHEN ip.payee01 = ? THEN ip.payee01share ELSE ip.payee02share END) AS total_payee_share,
                SUM(rv.reported_royalty_for_ceymusic / 100 * CASE WHEN ip.payee = ? THEN ip.share WHEN ip.payee01 = ? THEN ip.payee01share ELSE ip.payee02share END) AS total_calculated_royalty
                FROM isrc_payees ip
                JOIN public.summary_bd_02 rv ON ip.isrc = rv.asset_isrc
                WHERE ip.payee = ? OR ip.payee01 = ? OR ip.payee02 = ? GROUP BY ip.isrc;
                """;
        List<PayeeForReport> pReport = new ArrayList<>();
        Connection con = getConn();

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, name);
            ps.setString(4, name);
            ps.setString(5, name);
            ps.setString(6, name);
            ps.setString(7, name);
            System.out.println();
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PayeeForReport pr = new PayeeForReport();
                pr.setIsrc(rs.getString(1));
                pr.setShare(rs.getInt(2));
                pr.setValue(rs.getDouble(3));
                pReport.add(pr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }

        return pReport;

    }

public static List<PayeeForReport> getPayeeReport(ArtistReport report) throws SQLException {
    int maxRetries = 3;
    int retryCount = 0;
    List<PayeeForReport> pReport = new ArrayList<>();
    // Connection con = getConn();
    String sql = """
            SELECT ip.isrc,\s
            SUM(CASE WHEN ip.payee = ? THEN ip.share WHEN ip.payee01 = ? THEN ip.payee01share ELSE ip.payee02share END) AS total_payee_share,
            SUM(rv.after_deduction_royalty / 100 * CASE WHEN ip.payee = ? THEN ip.share WHEN ip.payee01 = ? THEN ip.payee01share ELSE ip.payee02share END) AS total_calculated_royalty
            FROM isrc_payees ip JOIN public.summary_bd_02 rv ON ip.isrc = rv.asset_isrc
            WHERE ip.payee = ? OR ip.payee01 = ? OR ip.payee02 = ? GROUP BY ip.isrc
            """;

    while (retryCount < maxRetries) {
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, report.getArtist().getName());
            ps.setString(2, report.getArtist().getName());
            ps.setString(3, report.getArtist().getName());
            ps.setString(4, report.getArtist().getName());
            ps.setString(5, report.getArtist().getName());
            ps.setString(6, report.getArtist().getName());
            ps.setString(7, report.getArtist().getName());
            //			System.out.println(ps);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PayeeForReport pr = new PayeeForReport();
                pr.setIsrc(rs.getString(1));
                pr.setShare(rs.getInt(2));
                pr.setValue(rs.getDouble(3));
                pReport.add(pr);
            }

            return pReport;
        } catch (PSQLException e) {
            if (e.getMessage().contains("An I/O error occurred")) {
                retryCount++;

                if (retryCount >= maxRetries) {
                    throw e;
                }

                System.out.println("\nRetrying database operation, attempt " + retryCount);

                try {
                    Thread.sleep(1000L * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    return null;
}

    public static ResultSet getCoWriterShares(String artistName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseMySQL.getConn();

        PreparedStatement psGetCoWriterShare = connection.prepareStatement("SELECT `isrc_payees`.`PAYEE`, " +
                "((((SUM(CASE WHEN `report`.`Territory` = 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END)) * 0.9) + (SUM(CASE WHEN `report`.`Territory` != 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END))) * 0.85) * `isrc_payees`.`SHARE`/100 AS REPORTED_ROYALTY " +
                "FROM `isrc_payees` " +
                "JOIN `report` ON `isrc_payees`.`ISRC` = `report`.`Asset_ISRC` " +
                "WHERE `isrc_payees`.`ISRC` IN (SELECT ISRC FROM isrc_payees WHERE PAYEE = ?) AND `isrc_payees`.`PAYEE` != ? " +
                "GROUP BY `report`.`Asset_ISRC`" +
                "LIMIT 6;");

        psGetCoWriterShare.setString(1, artistName);
        psGetCoWriterShare.setString(2, artistName);

        return psGetCoWriterShare.executeQuery();
    }

    public static List<Ingest> getAllIngests() throws SQLException, IOException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT i.ingest_id, i.ingest_date, i.user_name_note, " +
                "(SELECT COUNT(ip.upc) FROM public.mc_ingest_products ip WHERE ip.ingest_id = i.ingest_id) AS product_count, " +
                "i.root_folder, i.ingest FROM public.mc_ingests i;");
        ResultSet rs = ps.executeQuery();
        List<Ingest> ingests = new ArrayList<>();
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                Ingest ingest = new Ingest();

                int ingestID = rs.getInt(1);
                ingest.setID(ingestID);
                // System.out.println("ingestID = " + ingestID);

                Date ingestDate = rs.getDate(2);
                ingest.setDate(ingestDate);

                String user = rs.getString(3);
                ingest.setUser(user);

                int productCount = rs.getInt(4);
                ingest.setUPCCount(productCount);

                byte[] csvBytes = rs.getBytes(6);
                if (csvBytes != null) {
                    InputStream inputStream = new ByteArrayInputStream(csvBytes);
                    // Create a temporary file
                    File tempFile = File.createTempFile("temp", ".csv");
                    // Write the data from the InputStream to the temporary file
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    ingest.setCSV(tempFile);
                }

                ingests.add(ingest);
            }
        }
        return ingests;
    }

    public static boolean checkTrackExists(String trackTitle, String composer, String lyricist) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(song_name) FROM public.song_metadata_new WHERE LOWER(song_name) = LOWER(?);");
        PreparedStatement ps2 = con.prepareStatement("INSERT INTO public.temp_songs(name, composer, lyricist) VALUES (?, ?, ?);");
        ps.setString(1, trackTitle);
        ResultSet rs = ps.executeQuery();
        boolean status = false;
        while (rs.next()) {
            int count = rs.getInt(1);
            if (count == 0) {
                // ps = con.prepareStatement("INSERT INTO public.temp_songs(name, composer, lyricist) VALUES (?, ?, ?);");
                ps2.setString(1, trackTitle);
                ps2.setString(2, composer);
                ps2.setString(3, lyricist);
                int status2 = ps2.executeUpdate();
                if (status2 > 0) {
                    status = true;
                }
            } else {
                status = true;
            }
        }
        return status;
    }

    public static void addTempArtist(String artist) throws SQLException {
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement("INSERT INTO public.temp_artists(name) VALUES (?);")) {

            ps.setString(1, artist);
            ps.executeUpdate();
        }
    }

    public static String getClaimYouTubeID(int id) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT youtube_id FROM public.manual_claims WHERE claim_id = ?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public static boolean createUser(String username, String password, String email, String displayName) throws SQLException {
        Hasher hasher = new Hasher(username, password);

        Connection con = getConn();

        PreparedStatement psCheckUser = con.prepareStatement("SELECT COUNT(username) FROM public.user WHERE LOWER(username) = LOWER(?);");
        psCheckUser.setString(1, username);

        ResultSet rs = psCheckUser.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        if (count == 0) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO public.user (username, password, email, display_name) VALUES (?, ?, ?, ?);");

            ps.setString(1, hasher.getUserName());
            ps.setString(2, hasher.getHashedPass());
            ps.setString(3, email);
            ps.setString(4, displayName);

            int status = ps.executeUpdate();

            return status > 0;
        } else {
            return false;
        }
    }

    public static boolean createUserGoogle(String id, String displayName, String username) throws SQLException {
        String sqlCheck = "SELECT COUNT(username) FROM public.user WHERE LOWER(username) = LOWER(?);";
        String sql = "INSERT INTO public.user (id_google, display_name, username) VALUES (?, ?, ?);";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sqlCheck);
             PreparedStatement psInsert = con.prepareStatement(sql)) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                int count = rs.getInt(1);

                if (count == 0) {
                    psInsert.setString(1, id);
                    psInsert.setString(2, displayName);
                    psInsert.setString(3, username);

                    int status = ps.executeUpdate();

                    return status > 0;
                }
            }
        }
        return false;
    }

    public static String getHashedPW_ForUsername(String username) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT password FROM public.user WHERE LOWER(username) = LOWER(?);");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();
            return rs.getString(1);
        } else {
            return null;
        }
    }

    public static User getUserData(String username) throws SQLException {
        System.out.println("Getting User Data for: " + username);

        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT privilege_level, email, display_name, id FROM public.user WHERE LOWER(username) = LOWER(?);");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();

            int privilegeLevel = rs.getInt(1);
            String email = rs.getString(2);
            String nickName = rs.getString(3);
            int userID = rs.getInt(4);

            User user = new User();
            user.setPrivilegeLevel(privilegeLevel);
            user.setEmail(email);
            user.setNickName(nickName);
            user.setUserID(userID);

            return user;
        } else {
            return null;
        }
    }

    public static List<String> getAllValidatedArtists() throws SQLException {
        Connection conn = getConn();
        String query = "SELECT artist_name, validated FROM public.artists WHERE validated = TRUE ORDER BY artist_name ASC;";
        Statement statement = conn.createStatement();
        List<String> artists = new ArrayList<>();

        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String artist = rs.getString(1);
            artists.add(artist);
        }

        return artists;
    }

    public static List<CoWriterSummary> getCoWriterPaymentSummary(String artistName) throws SQLException {
        Connection conn = getConn();
        List<CoWriterSummary> list = new ArrayList<>();

        PreparedStatement ps = conn.prepareStatement("""
                SELECT\s
                    CASE\s
                        WHEN ip.payee = (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer) THEN (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.lyricist)
                        ELSE (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer)
                    END AS contributor,
                    SUM(rep.after_deduction_royalty) AS total_royalty
                FROM public.isrc_payees ip
                JOIN SONGS S ON IP.ISRC = S.ISRC
                JOIN public.summary_bd_02 rep ON IP.ISRC = rep.asset_isrc
                WHERE (ip.payee = ? AND ip.share = 100)
                GROUP BY contributor
                ORDER BY total_royalty DESC;""");
        ps.setString(1, artistName);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String contributor = rs.getString(1);
            double royalty = rs.getDouble(2);

            if (!Objects.equals(contributor, artistName)) {
                CoWriterSummary summary = new CoWriterSummary(contributor, royalty);
                list.add(summary);
            }

        }

        return list;
    }

    public static ManualClaimTrack getManualClaim(int claimIDInt) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT song_name, composer, lyricist, youtube_id, " +
                "trim_start, trim_end, artwork, preview_image, date, claim_type FROM public.manual_claims " +
                "WHERE claim_id = ?;");
        ps.setInt(1, claimIDInt);
        ResultSet rs = ps.executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();
            String trackName = rs.getString(1);
            String composer = rs.getString(2);
            String lyricist = rs.getString(3);
            String ytID = rs.getString(4);
            Date date = rs.getDate(9);
            LocalDate localDate = sqlDateToLocalDate(date);
            int claimType = rs.getInt(10);
            String trimStart = rs.getString(5);
            String trimEnd = rs.getString(6);

            ManualClaimTrack claim = new ManualClaimTrack(claimIDInt, trackName, lyricist, composer, ytID, localDate, claimType);
            claim.setTrimStart(trimStart);
            claim.setTrimEnd(trimEnd);

            return claim;
        }
        return null;
    }

    public static int getArchivedManualClaimCount() throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("""
                SELECT COUNT(claim_id)
                FROM public.manual_claims\s
                WHERE ingest_status = false AND archive = true;""");
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static int unArchiveManualClaim(int id) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("UPDATE public.manual_claims SET archive=false WHERE claim_id = ?;");
        ps.setInt(1, id);
        return ps.executeUpdate();
    }

    public static int getMissingPayeeCount() throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("""
                SELECT COUNT(DISTINCT(r.asset_isrc)) FROM public.report r
                LEFT OUTER JOIN isrc_payees s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL;""");
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static int getMissingISRC_Count() throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("""
                SELECT COUNT(DISTINCT(r.asset_isrc)) FROM public.report r
                LEFT OUTER JOIN songs s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL;
                """);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static boolean checkIfArtistValidated(String composer) throws SQLException {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                String query = "SELECT COUNT(artist_id) FROM public.artists WHERE artist_name = ? AND validated = true;";

                try (Connection con = getConn();
                     PreparedStatement ps = con.prepareStatement(query)) {

                    ps.setString(1, composer);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                } // The Connection, PreparedStatement will be closed here.

                return false; // Default return value in case the artist is not found.
            } catch (PSQLException e) {
                if (e.getMessage().contains("An I/O error occurred") || e.getMessage().contains("This connection has been closed")) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    System.out.println("Retrying database operation, attempt " + retryCount);
                    // You might want to add a small delay here
                    try {
                        Thread.sleep(1000); // 1 second delay
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }

        return false;
    }

    public static ArrayList<Songs> getMissingISRCs(int report_id) throws SQLException {
        ArrayList<Songs> songs = new ArrayList<>();
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("""
                SELECT r.upc, r.asset_isrc FROM public.reports_new r
                LEFT OUTER JOIN songs s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL AND r.report_id = ?
                GROUP BY r.upc, r.asset_isrc
                ORDER BY r.asset_isrc;
                """);
        ps.setInt(1, report_id);

        ResultSet rs = ps.executeQuery();

        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                String upc = rs.getString(1);
                String isrc = rs.getString(2);

                Songs song = new Songs();
                song.setUPC(upc);
                song.setISRC(isrc);
                songs.add(song);
            }
        }

        return songs;
    }

    public static ArrayList<Songs> getMissingPayees(int i) throws SQLException {
        ArrayList<Songs> songs = new ArrayList<>();
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("""
                SELECT r.upc, r.asset_isrc FROM public.reports_new r
                LEFT OUTER JOIN isrc_payees s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL AND r.report_id = ?
                GROUP BY r.asset_isrc, r.upc;
                """);

        ps.setInt(1, i);

        System.out.println("i = " + i);

        ResultSet rs = ps.executeQuery();

        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                String upc = rs.getString(1);
                String isrc = rs.getString(2);

                System.out.println("isrc = " + isrc);

                Songs song = new Songs();
                song.setUPC(upc);
                song.setISRC(isrc);
                songs.add(song);
            }
        }

        return songs;
    }

    public static void importReport(ReportMetadata report) throws SQLException, IOException, CsvValidationException {
        String insertMetadataSQL = "INSERT INTO report_metadata (report_name, report_month, report_year, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        String insertReportSQL = "INSERT INTO reports_new (report_id, asset_isrc, reported_royalty, territory, sale_start_date, dsp, product_label, upc, asset_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = getConn();

        // Insert metadata and get the generated report_id
        int reportId;

        try (PreparedStatement stmt = con.prepareStatement(insertMetadataSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getName());
            stmt.setInt(2, report.getReportMonth());
            stmt.setInt(3, report.getReportYear());
            stmt.setObject(4, report.getCreatedAt());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reportId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve report_id.");
                }
            }
        }

        // Insert report data
        try (CSVReader reader = new CSVReader(new FileReader(report.getCsvFile()));
             PreparedStatement stmt = con.prepareStatement(insertReportSQL)) {

            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                FUGAReport fugaReport = CSVController.getFUGAReport(nextLine);
                stmt.setInt(1, reportId);
                stmt.setString(2, fugaReport.getAssetISRC());
                stmt.setDouble(3, fugaReport.getReportedRoyalty());
                stmt.setString(4, fugaReport.getTerritory());
                stmt.setDate(5, fugaReport.getSaleStartDateNew());
                stmt.setString(6, fugaReport.getDsp());
                stmt.setString(7, fugaReport.getProductLabel());
                stmt.setString(8, String.valueOf(fugaReport.getProductUPC()));
                stmt.setInt(9, fugaReport.getAssetQuantity());
                // fugaReport.getAssetQuantity();
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /*public static int refreshSummaryTable(ArtistReport report) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("CALL refresh_report_summary(?, ?);");
        System.out.println("Report Month: " + report.getMonthInt());
        System.out.println("Report Year: " + report.getYear());
        ps.setInt(1, report.getMonthInt());
        ps.setInt(2, report.getYear());
        return ps.executeUpdate();
    }*/

    public static void refreshSummaryTable(int month, int year) throws SQLException {
        String callProcedure = "CALL refresh_report_summary(?, ?);";
        String refreshMaterializedView = "REFRESH MATERIALIZED VIEW summary_bd_03_multiple;";

        try (Connection con = getConn();
             PreparedStatement psProcedure = con.prepareStatement(callProcedure);
             PreparedStatement psRefresh = con.prepareStatement(refreshMaterializedView)) {

            System.out.println("\nRefreshing Summary Tables for " + ItemSwitcher.setMonth(month) + " " + year);
            psProcedure.setInt(1, month);
            psProcedure.setInt(2, year);

            // System.out.println("Refreshing Tables...");

            System.out.println("Refreshing Summary Breakdown 01 and 02");
            psProcedure.executeUpdate();
            System.out.println("Refreshing Summary Breakdown 03");
            // After the stored procedure is executed, refresh the materialized view.
            psRefresh.executeUpdate();
        }
    }

    public static List<ReportMetadata> getAllReports() throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT id, report_month, report_year, created_at FROM public.report_metadata;");
        ResultSet rs = ps.executeQuery();

        List<ReportMetadata> list = new ArrayList<>();

        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int id = rs.getInt(1);
                int month = rs.getInt(2);
                int year = rs.getInt(3);
                Date createdDate = rs.getDate(4);

                ReportMetadata reports = new ReportMetadata(id, month, year, sqlDateToLocalDateTime(createdDate));
                list.add(reports);
            }
        }

        return list;
    }

    public static boolean removeReport(int id) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("DELETE FROM report_metadata WHERE id = ?;");
        ps.setInt(1, id);
        int status = ps.executeUpdate();
        return status > 0;
    }

    public static int importReport(ReportMetadata report, Label lblImport, Label lblReportProgress) {
        /*Platform.runLater(() -> {
            lblImport.setText("Working on...");
            lblReportProgress.setText("0%");
        });

        // Insert metadata and get the generated report_id
        int reportId = 0;

        try {
            BufferedReader bReader = new BufferedReader(new FileReader(report.getCsvFile()));
            int totalRowCount = getReportTotalRowCount(bReader);
            int rowcount2 = 0; // While loop's row count

            String insertMetadataSQL = "INSERT INTO report_metadata (report_name, report_month, report_year, created_at) VALUES (?, ?, ?, ?) RETURNING id";
            String insertReportSQL = "INSERT INTO reports_new (report_id, asset_isrc, reported_royalty, territory, sale_start_date, dsp, product_label, upc) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection con = getConn();


            try (PreparedStatement pstmt = con.prepareStatement(insertMetadataSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, report.getName());
                pstmt.setInt(2, report.getReportMonth());
                pstmt.setInt(3, report.getReportYear());
                pstmt.setObject(4, report.getCreatedAt());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reportId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve report_id.");
                    }
                }
            }

            // Insert report data
            Platform.runLater(() -> lblImport.setText("Importing"));
            Platform.runLater(() -> lblReportProgress.setVisible(true));
            try (CSVReader reader = new CSVReader(new FileReader(report.getCsvFile()));
                 PreparedStatement pstmt = con.prepareStatement(insertReportSQL)) {

                String[] nextLine;
                reader.readNext(); // Skip header
                while ((nextLine = reader.readNext()) != null) {
                    rowcount2++;
                    double percentage = ((double) rowcount2 / totalRowCount) * 100;

                    FUGAReport fugaReport = CSVController.getFUGAReport(nextLine);
                    pstmt.setInt(1, reportId);
                    pstmt.setString(2, fugaReport.getAssetISRC());
                    pstmt.setDouble(3, fugaReport.getReportedRoyalty());
                    pstmt.setString(4, fugaReport.getTerritory());
                    pstmt.setDate(5, fugaReport.getSaleStartDateNew());
                    pstmt.setString(6, fugaReport.getDsp());
                    pstmt.setString(7, fugaReport.getProductLabel());
                    pstmt.setString(8, String.valueOf(fugaReport.getProductUPC()));

                    pstmt.executeUpdate();

                    Platform.runLater(() -> lblReportProgress.setText(df.format(percentage) + "%"));
                }
                // pstmt.executeBatch();
            }
            Platform.runLater(() -> lblImport.setText("Report Imported. Please Check Missing ISRCs"));
        } catch (IOException | CsvValidationException e) {
            Platform.runLater(() -> {
                lblImport.setText("Error Reading CSV");
                lblImport.setStyle("-fx-text-fill: red");
                AlertBuilder.sendErrorAlert("Error", "Error Reading CSV", e.toString());
                e.printStackTrace();
            });
        } catch (SQLException e) {
            lblImport.setText("Error Importing CSV");
            lblImport.setStyle("-fx-text-fill: red");
            AlertBuilder.sendErrorAlert("Error", "Error Importing CSV", e.toString());
            e.printStackTrace();
        }

        return reportId;*/

        Label lblUpdate = UIController.lblUserEmailAndUpdateStatic;
        final boolean[] updatedBefore = {false};

        // Create fade-out transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), lblUpdate);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Create fade-in transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), lblUpdate);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Play fade-out, then change text and fade-in
        fadeOut.setOnFinished(e -> {
            lblUpdate.setText("Importing Report: 0%");
            updatedBefore[0] = true;
            fadeIn.play();
        });

        Platform.runLater(() -> {
            lblImport.setText("Working on...");
            lblReportProgress.setText("0%");
            Platform.runLater(fadeOut::play);
        });

        int reportId = 0;

        try (Connection con = getConn()) {
            con.setAutoCommit(false);

            // Insert metadata and get the generated report_id
            String insertMetadataSQL = "INSERT INTO report_metadata (report_name, report_month, report_year, created_at) VALUES (?, ?, ?, ?) RETURNING id";
            try (PreparedStatement pstmt = con.prepareStatement(insertMetadataSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, report.getName());
                pstmt.setInt(2, report.getReportMonth());
                pstmt.setInt(3, report.getReportYear());
                pstmt.setObject(4, report.getCreatedAt());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reportId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve report_id.");
                    }
                }
            }

            // Insert report data
            Platform.runLater(() -> {
                lblImport.setText("Importing");
                lblReportProgress.setVisible(true);
            });

            String insertReportSQL = "INSERT INTO reports_new (report_id, asset_isrc, reported_royalty, territory, sale_start_date, dsp, product_label, upc, asset_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (BufferedReader bReader = new BufferedReader(new FileReader(report.getCsvFile()));
                 CSVReader reader = new CSVReader(bReader);
                 PreparedStatement pstmt = con.prepareStatement(insertReportSQL)) {

                int totalRowCount = getReportTotalRowCount(new BufferedReader(new FileReader(report.getCsvFile())));
                int rowcount2 = 0;
                String[] nextLine;
                reader.readNext(); // Skip header

                while ((nextLine = reader.readNext()) != null) {
                    rowcount2++;
                    FUGAReport fugaReport = CSVController.getFUGAReport(nextLine);
                    pstmt.setInt(1, reportId);
                    pstmt.setString(2, fugaReport.getAssetISRC());
                    pstmt.setDouble(3, fugaReport.getReportedRoyalty());
                    pstmt.setString(4, fugaReport.getTerritory());
                    pstmt.setDate(5, fugaReport.getSaleStartDateNew());
                    pstmt.setString(6, fugaReport.getDsp());
                    pstmt.setString(7, fugaReport.getProductLabel());
                    pstmt.setString(8, String.valueOf(fugaReport.getProductUPC()));
                    pstmt.setInt(9, fugaReport.getAssetQuantity());

                    pstmt.addBatch();

                    if (rowcount2 % 1000 == 0) {
                        pstmt.executeBatch();
                        pstmt.clearBatch();
                        con.commit();

                        double percentage = ((double) rowcount2 / totalRowCount) * 100;
                        Platform.runLater(() -> {
                            lblReportProgress.setText(df.format(percentage) + "%");
                            lblUpdate.setText("Importing Report: " + df.format(percentage) + "%");
                        });
                    }
                }
                // Execute and commit any remaining batch
                pstmt.executeBatch();
                con.commit();
                Platform.runLater(() -> {
                    lblReportProgress.setText("100%");
                    FadeTransition fadeOutFinal = new FadeTransition(Duration.millis(500), lblUpdate);
                    fadeOutFinal.setFromValue(1.0);
                    fadeOutFinal.setToValue(0.0);
                    fadeOutFinal.setOnFinished(e -> {
                        lblUpdate.setText(Main.userSession.getEmail());
                        lblUpdate.setStyle("");
                        FadeTransition fadeInFinal = new FadeTransition(Duration.millis(500), lblUpdate);
                        fadeInFinal.setFromValue(0.0);
                        fadeInFinal.setToValue(1.0);
                        fadeInFinal.play();
                    });
                    fadeOutFinal.play();
                });
            }

            Platform.runLater(() -> lblImport.setText("Report Imported. Please Check Missing ISRCs"));
        } catch (IOException | SQLException | CsvValidationException e) {
            Platform.runLater(() -> {
                lblImport.setText("Error Importing CSV");
                lblImport.setStyle("-fx-text-fill: red");
                AlertBuilder.sendErrorAlert("Error", "Error Importing CSV", e.toString());
                e.printStackTrace();
            });
        }

        return reportId;

    }

    public static int getMissingPayeeCount(int key) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT(r.asset_isrc)) FROM public.reports_new r
                LEFT OUTER JOIN isrc_payees s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL AND r.report_id = ?;
                """;
        try (Connection con = getConn();
             PreparedStatement pstmt = con.prepareStatement(query);) {
            pstmt.setInt(1, key);

            ResultSet rs = pstmt.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static int getMissingISRC_Count(int key) throws SQLException {
        String query = """
                SELECT COUNT(DISTINCT(r.asset_isrc)) FROM public.reports_new r
                LEFT OUTER JOIN songs s ON s.isrc = r.asset_isrc
                WHERE s.isrc IS NULL AND r.report_id = ?;
                """;
        try (Connection con = getConn();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, key);

            ResultSet rs = pstmt.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static int getFUGA_ReportID(int month, int year) throws SQLException {
        String query = "SELECT id FROM public.report_metadata WHERE report_month = ? AND report_year = ?;";
        int id = 0;

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                id = rs.getInt(1);
            }
        }

        return id;
    }

    public static int changeUserNickName(int userID, String nickName) throws SQLException {
        String sql = "UPDATE public.user SET display_name = ? WHERE id = ?;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nickName);
            ps.setInt(2, userID);
            return ps.executeUpdate();
        }
    }

    public static int changeUserName(int userID, String username) throws SQLException {
        String sql = "UPDATE public.user SET username = ? WHERE id = ?;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, userID);

            return ps.executeUpdate();
        }
    }

    public static int changeUserEmail(int userID, String email) throws SQLException {
        String sql = "UPDATE public.user SET email = ? WHERE id = ?;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userID);

            return ps.executeUpdate();
        }
    }

    public static boolean changePassword(String userName, String newPassword) throws SQLException {
        Hasher hasher = new Hasher(userName, newPassword);

        String sql = "UPDATE public.user SET password = ? WHERE username = ?;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hasher.getHashedPass());
            ps.setString(2, hasher.getUserName());

            int status = ps.executeUpdate();

            return status > 0;
        }
    }

    public static boolean checkUsernameAvailability(String username) throws SQLException {
        String sql = "SELECT COUNT(username) FROM public.user WHERE LOWER(username) = LOWER(?);";

        try (Connection con = getConn();
             PreparedStatement psCheckUser = con.prepareStatement(sql)) {

            psCheckUser.setString(1, username);

            ResultSet rs = psCheckUser.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            return count == 0;
        }

    }

    public static int addTempIngestMetadata(String ingestName, LocalDate date) throws SQLException {
        String sql = "INSERT INTO public.temp_ingest_metadata(ingest_name, ingest_date, username) VALUES (?, ?, ?) RETURNING id;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ingestName);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, Main.userSession.getUserName());

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.isBeforeFirst()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static List<Ingest> getUnApprovedIngests() throws SQLException {
        String sql = """
                SELECT im.id, im.ingest_name, im.ingest_date, im.username, COUNT(i.isrc)
                FROM public.temp_ingest_metadata im
                LEFT OUTER JOIN temp_ingests i ON im.id = i.ingest_id
                WHERE im.approved = FALSE
                GROUP BY im.id
                ORDER BY ingest_date DESC;
                """;
        List<Ingest> ingests = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    Ingest ingest = new Ingest();

                    setIngestData(ingest, rs);

                    ingests.add(ingest);
                }
            }
        }

        return ingests;
    }

    public static boolean checkDatabaseConnection() {
        try {
            Connection con = getConn();

            if (con != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static Ingest getIngest(int id) throws SQLException {
        String sqlIngestMetadata = """
                SELECT IM.ID,
                	IM.INGEST_NAME,
                	IM.INGEST_DATE,
                	IM.USERNAME,
                	COUNT(I.ISRC) AS asset_count
                FROM PUBLIC.TEMP_INGEST_METADATA IM
                LEFT OUTER JOIN TEMP_INGESTS I ON IM.ID = I.INGEST_ID
                WHERE IM.APPROVED = FALSE AND IM.ID = ?
                GROUP BY IM.ID
                ORDER BY INGEST_DATE DESC;
                """;
        String sqlIngestData = """
                SELECT ALBUM_TITLE,
                	UPC,
                	CATALOG_NUMBER,
                	RELEASE_DATE,
                	LABEL,
                	CLINE_YEAR,
                	CLINE_NAME,
                	PLINE_NAME,
                	PLINE_YEAR,
                	RECORDING_YEAR,
                	RECORDING_LOCATION,
                	ALBUM_FORMAT,
                	TRACK_TITLE,
                	ISRC,
                	TRACK_PRIMARY_ARTIST,
                	COMPOSER,
                	LYRICISTS,
                	WRITERS,
                	PUBLISHERS,
                	ORIGINAL_FILENAME,
                	INGEST_ID
                FROM PUBLIC.TEMP_INGESTS
                WHERE INGEST_ID = ?;
                """;

        // Get Ingest Metadata
        try (Connection con = getConn();
             PreparedStatement psIngestMetadata = con.prepareStatement(sqlIngestMetadata);
             PreparedStatement psIngestData = con.prepareStatement(sqlIngestData)) {

            psIngestMetadata.setInt(1, id);
            ResultSet rsIngestMetadata = psIngestMetadata.executeQuery();

            if (rsIngestMetadata.isBeforeFirst()) {
                rsIngestMetadata.next();

                Ingest ingest = new Ingest();

                setIngestData(ingest, rsIngestMetadata);

                // Get Ingest Data
                psIngestData.setInt(1, id);
                ResultSet rsIngestData = psIngestData.executeQuery();
                if (rsIngestData.isBeforeFirst()) {
                    List<IngestCSVData> ingestCSVDataList = new ArrayList<>();

                    while (rsIngestData.next()) {
                        IngestCSVData ingestCSVData = new IngestCSVData();

                        ingestCSVData.setAlbumTitle(rsIngestData.getString(1));
                        ingestCSVData.setUpc(rsIngestData.getString(2));
                        ingestCSVData.setCatalogNumber(rsIngestData.getString(3));
                        ingestCSVData.setReleaseData(rsIngestData.getString(4));
                        ingestCSVData.setLabel(rsIngestData.getString(5));
                        ingestCSVData.setClineYear(rsIngestData.getString(6));
                        ingestCSVData.setClineName(rsIngestData.getString(7));
                        ingestCSVData.setPlineYear(rsIngestData.getString(8));
                        ingestCSVData.setPlineName(rsIngestData.getString(9));
                        ingestCSVData.setRecordingYear(rsIngestData.getString(10));
                        ingestCSVData.setRecordingLocation(rsIngestData.getString(11));
                        ingestCSVData.setAlbumFormat(rsIngestData.getString(12));
                        ingestCSVData.setTrackTitle(rsIngestData.getString(13));
                        ingestCSVData.setIsrc(rsIngestData.getString(14));
                        ingestCSVData.setTrackPrimaryArtist(rsIngestData.getString(15));
                        ingestCSVData.setComposer(rsIngestData.getString(16));
                        ingestCSVData.setLyricist(rsIngestData.getString(17));
                        ingestCSVData.setWriters(rsIngestData.getString(18));
                        ingestCSVData.setPublishers(rsIngestData.getString(19));
                        ingestCSVData.setOriginalFileName(rsIngestData.getString(20));

                        ingestCSVDataList.add(ingestCSVData);
                    }

                    ingest.setIngestCSVDataList(ingestCSVDataList);
                    return ingest;
                }
            }
        }

        // Return Data
        return null;
    }

    private static void setIngestData(Ingest ingest, ResultSet rs) throws SQLException {
        ingest.setID(rs.getInt(1));
        ingest.setName(rs.getString(2));
        ingest.setDate(rs.getDate(3));
        ingest.setUser(rs.getString(4));
        ingest.setAssetCount(rs.getInt(5));
    }

    public static void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO public.products(upc, product_title, catalog_number, release_date) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (upc) DO NOTHING;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, product.getUpc());
            ps.setString(2, product.getAlbumTitle());
            ps.setString(3, product.getCatalogNumber());
            ps.setDate(4, stringToDate(product.getReleaseDate()));

            ps.executeUpdate();
        }
    }

    private static Date stringToDate(String releaseDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date parsed = format.parse(releaseDate);
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addSong(Songs song) throws SQLException {
        String sql = "INSERT INTO public.songs(isrc, song_name, file_name, upc, composer, lyricist, featuring, type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (isrc) DO NOTHING;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, song.getISRC());
            ps.setString(2, song.getTrackTitle());
            ps.setString(3, song.getFileName());
            ps.setString(4, song.getUPC());
            ps.setInt(5, setArtistID(song.getComposer()));
            ps.setInt(6, setArtistID(song.getLyricist()));
            ps.setInt(7, setArtistID(song.getFeaturing()));
            System.out.println("Song Type: " + song.getTypeConverted());
            ps.setString(8, song.getType());

            ps.executeUpdate();
        }
    }

    private static int setArtistID(String name) throws SQLException {
        System.out.println("DatabasePostgres.setArtistID");
        System.out.println("name = " + name);

        if (name != null) {
            int id = fetchArtistID(name);

            if (id != 0) {
                return id;
            } else {
                addTempArtist(name);
                return addArtist(name);
            }
        } else {
            return 0;
        }
    }

    private static int addArtist(String name) throws SQLException {
        String sql = "INSERT INTO public.artists(artist_name, status, validated) VALUES (?, ?, ?) RETURNING artist_id;";
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, 3);
            ps.setBoolean(3, false);

            ResultSet rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static void approveIngest(int ingestID) throws SQLException {
        String approveSQL = "UPDATE public.temp_ingest_metadata SET approved=? WHERE id = ?;";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(approveSQL)) {
            ps.setBoolean(1, true);
            ps.setInt(2, ingestID);

            ps.executeUpdate();
        }
    }

    public static void refreshSongMetadataTable() throws SQLException {
        String refreshMaterializedView = "REFRESH MATERIALIZED VIEW song_metadata_new;";

        try (Connection con = getConn();
             PreparedStatement psRefresh = con.prepareStatement(refreshMaterializedView)) {

            System.out.println("Refreshing Song Metadata Table");

            psRefresh.executeUpdate();
        }
    }

    public static ManualClaimTrack getClaimArtwork(ManualClaimTrack track) throws SQLException {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Your existing code here
                String sql = "SELECT preview_image, artwork FROM public.manual_claims WHERE claim_id = ?;";

                try (Connection con = getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, track.getId());

                    ResultSet rs = ps.executeQuery();
                    if (rs.isBeforeFirst()) {
                        rs.next();
                        byte[] previewImageBytes = rs.getBytes(1);
                        byte[] artworkImageBytes = rs.getBytes(2);

                        // Set the images to model
                        try {
                            if (previewImageBytes != null && artworkImageBytes != null) {
                                ByteArrayInputStream previewImageInputStream = new ByteArrayInputStream(previewImageBytes);
                                ByteArrayInputStream artworkImageInputStream = new ByteArrayInputStream(artworkImageBytes);

                                BufferedImage previewImage = ImageIO.read(previewImageInputStream);
                                BufferedImage artwork = ImageIO.read(artworkImageInputStream);
                                // Platform.runLater(() -> System.out.println("artwork.getColorModel() = " + artwork.getColorModel()));

                                track.setPreviewImage(previewImage);
                                track.setImage(artwork);
                            } else {
                                System.out.println("No Artwork Found");
                            }
                        } catch (IOException e) {
                            System.out.println("Unable to download artwork");
                        }
                    }
                }

                System.out.println("DatabasePostgres.getClaimArtwork");
                return track;
            } catch (PSQLException e) {
                if (e.getMessage().contains("An I/O error occurred") || e.getMessage().contains("This connection has been closed")) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    System.out.println("Retrying database operation, attempt " + retryCount);
                    // You might want to add a small delay here
                    try {
                        Thread.sleep(1000); // 1 second delay
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }

        return track;
    }

    public static int getAMClaimCountFor(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(claim_id) FROM public.manual_claims WHERE ingest_status = false AND archive = true AND date BETWEEN ? AND ? LIMIT 100;";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));

            ResultSet resultSet = ps.executeQuery();

            int count = 0;

            if (resultSet.isBeforeFirst()) {
                resultSet.next();

                count = resultSet.getInt(1);

                System.out.println("\rTotal: " + count);
            }

            return count;
        }
    }

    public static void updatePayee(Payee payee) throws SQLException {
        String selectSQL = "SELECT COUNT(*) FROM public.isrc_payees WHERE isrc = ?";
        String insertSQL = "INSERT INTO public.isrc_payees (isrc, payee, share, payee01, payee01share, payee02, payee02share, update_user) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE public.isrc_payees SET payee = ?, share = ?, payee01 = ?, payee01share = ?, payee02 = ?, payee02share = ?, update_user = ? " +
                "WHERE isrc = ?";

        try (Connection con = getConn()) {
            // Check if the ISRC exists in the table
            try (PreparedStatement psSelect = con.prepareStatement(selectSQL)) {
                psSelect.setString(1, payee.getIsrc());
                ResultSet rs = psSelect.executeQuery();
                rs.next();
                boolean exists = rs.getInt(1) > 0;

                if (exists) {
                    // Update the existing record
                    try (PreparedStatement psUpdate = con.prepareStatement(updateSQL)) {
                        psUpdate.setString(1, payee.getPayee1());
                        psUpdate.setInt(2, parseShareSafely(payee.getShare1()));
                        psUpdate.setString(3, payee.getPayee2());
                        psUpdate.setInt(4, parseShareSafely(payee.getShare2()));
                        psUpdate.setString(5, payee.getPayee3());
                        psUpdate.setInt(6, parseShareSafely(payee.getShare3()));
                        psUpdate.setString(7, Main.userSession.getUserName()); // replace with actual update user if available
                        psUpdate.setString(8, payee.getIsrc());

                        psUpdate.executeUpdate();
                    }
                } else {
                    // Insert a new record
                    try (PreparedStatement psInsert = con.prepareStatement(insertSQL)) {
                        psInsert.setString(1, payee.getIsrc());
                        psInsert.setString(2, payee.getPayee1());
                        psInsert.setInt(3, parseShareSafely(payee.getShare1()));
                        psInsert.setString(4, payee.getPayee2());
                        psInsert.setInt(5, parseShareSafely(payee.getShare2()));
                        psInsert.setString(6, payee.getPayee3());
                        psInsert.setInt(7, parseShareSafely(payee.getShare3()));
                        psInsert.setString(8, Main.userSession.getUserName()); // replace with actual update user if available

                        psInsert.executeUpdate();
                    }
                }
            }
        }
    }

    private static int parseShareSafely(String share) {
        try {
            return Integer.parseInt(share);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public static List<String> getAllPayees() throws SQLException {
        String sql = "SELECT artist_name FROM public.artists WHERE status = 5 ORDER BY artist_name ASC;";
        List<String> artists = new ArrayList<>();

        try (Connection con = getConn()) {
            // Check if the ISRC exists in the table
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            String artistName = rs.getString(1);
                            artists.add(artistName);
                        }
                        return artists;
                    }
                }
            }
        }

        return null;
    }

    public static List<String> getIngestNames() throws SQLException {
        String sql = "SELECT ingest_name FROM public.temp_ingest_metadata;";
        List<String> ingestNames = new ArrayList<>();

        try (Connection con = getConn()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            String ingestName = rs.getString(1);
                            ingestNames.add(ingestName);
                        }
                        return ingestNames;
                    }
                }
            }
        }

        return ingestNames;
    }

    public static Map<String, Payee> fetchPayeeDetailsForISRCs(List<String> isrcs) throws SQLException {
        Map<String, Payee> payeeDetails = new HashMap<>();
        String placeholders = String.join(",", Collections.nCopies(isrcs.size(), "?"));
        String sql = "SELECT isrc, payee, share, payee01, payee01share, payee02, payee02share " +
                "FROM isrc_payees WHERE isrc IN (" + placeholders + ")";

        System.out.println("Querying for ISRCs: " + String.join(", ", isrcs));

        try (Connection conn = getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < isrcs.size(); i++) {
                pstmt.setString(i + 1, isrcs.get(i));
            }

            System.out.println("Executing SQL: " + pstmt.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payee payee = new Payee();

                    /*System.out.println("ISRC: " + rs.getString("isrc"));
                    System.out.println("Payee 1: " + rs.getString("payee"));
                    System.out.println("Share 1: " + rs.getInt("share"));
                    System.out.println("Payee 2: " + rs.getString("payee01"));
                    System.out.println("Share 2: " + rs.getInt("payee01share"));
                    System.out.println("Payee 3: " + rs.getString("payee02"));
                    System.out.println("Share 3: " + rs.getInt("payee02share"));*/

                    payee.setIsrc(rs.getString("isrc"));
                    payee.setPayee1(rs.getString("payee"));
                    payee.setShare1(String.valueOf(rs.getInt("share")));
                    payee.setPayee2(rs.getString("payee01"));
                    payee.setShare2(String.valueOf(rs.getInt("payee01share")));
                    payee.setPayee3(rs.getString("payee02"));
                    payee.setShare3(String.valueOf(rs.getInt("payee02share")));

                    payeeDetails.put(payee.getIsrc(), payee);
                }
            }
        }

        return payeeDetails;

    }

    public static List<TerritoryBreakdown> getTerritoryBreakdown(ArtistReport report) throws SQLException {
        int maxRetries = 3;
        int retryCount = 0;
        List<TerritoryBreakdown> territoryBreakdownList = new ArrayList<>();

        while (retryCount < maxRetries) {
            try {
                String sql = """
                        SELECT\s
                            RN.TERRITORY,
                            SUM(RN.asset_quantity) AS asset_quantity,
                            SUM(
                                CASE
                                    WHEN S.type = 'O' THEN\s
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85 * 0.9) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85 * 0.9) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                    WHEN S.type = 'C' THEN\s
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85 * 0.8) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85 * 0.8) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                    ELSE
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                END
                            ) AS REPORTED_ROYALTY_FOR_CEYMUSIC
                        FROM PUBLIC.ISRC_PAYEES IP
                        JOIN PUBLIC.REPORTS_NEW RN ON IP.ISRC = RN.ASSET_ISRC
                        LEFT JOIN PUBLIC.SONGS S ON RN.ASSET_ISRC = S.ISRC
                        WHERE (IP.PAYEE = ?
                            OR IP.PAYEE01 = ?
                            OR IP.PAYEE02 = ?
                            OR IP.PAYEE03 = ?)
                            AND RN.REPORT_ID = ?
                        GROUP BY RN.TERRITORY
                        ORDER BY REPORTED_ROYALTY_FOR_CEYMUSIC DESC;
                        """;

                int reportID = getFUGA_ReportID(report.getMonthInt(), report.getYear());

                if (reportID > 0) {
                    try (Connection con = getConn();
                         PreparedStatement ps = con.prepareStatement(sql)) {
                        String artistName = report.getArtist().getName();
                        for (int i = 1; i <= 28; i++) {
                            ps.setString(i, artistName);
                        }
                        ps.setInt(29, reportID);

                        ResultSet rs = ps.executeQuery();
                        if (rs.isBeforeFirst()) {
                            while (rs.next()) {
                                String territory = rs.getString(1);
                                int assetQuantity = rs.getInt(2);
                                double reportedRoyaltyForCEYMusic = rs.getDouble(3);
                                territoryBreakdownList.add(new TerritoryBreakdown(territory, assetQuantity, reportedRoyaltyForCEYMusic));
                            }
                        }
                    }
                }
                return territoryBreakdownList;
            } catch (PSQLException e) {
                if (e.getMessage().contains("An I/O error occurred") || e.getMessage().contains("This connection has been closed")) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    System.out.println("Retrying database operation, attempt " + retryCount);
                    // You might want to add a small delay here
                    try {
                        Thread.sleep(1000); // 1 second delay
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }

        return territoryBreakdownList;
    }

    public static List<DSPBreakdown> getDSPBreakdown(ArtistReport report) throws SQLException {
        int maxRetries = 3;
        int retryCount = 0;
        List<DSPBreakdown> dspBreakdownList = new ArrayList<>();

        while (retryCount < maxRetries) {
            try {
                // Your existing code here
                String sql = """
                        SELECT\s
                            RN.DSP,
                        	SUM(RN.asset_quantity) AS asset_quantity,
                            SUM(
                                CASE
                                    WHEN S.type = 'O' THEN\s
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85 * 0.9) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85 * 0.9) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                    WHEN S.type = 'C' THEN\s
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85 * 0.8) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85 * 0.8) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                    ELSE
                                        CASE
                                            WHEN RN.TERRITORY = 'AU' THEN (RN.REPORTED_ROYALTY * 0.9 * 0.85) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                            ELSE (RN.REPORTED_ROYALTY * 0.85) * COALESCE(
                                                CASE
                                                    WHEN IP.payee = ? THEN IP.share
                                                    WHEN IP.payee01 = ? THEN IP.payee01share
                                                    WHEN IP.payee02 = ? THEN IP.payee02share
                                                    WHEN IP.payee03 = ? THEN IP.payee03share
                                                END, 0) / 100
                                        END
                                END
                            ) AS REPORTED_ROYALTY_FOR_CEYMUSIC
                        FROM PUBLIC.ISRC_PAYEES IP
                        JOIN PUBLIC.REPORTS_NEW RN ON IP.ISRC = RN.ASSET_ISRC
                        LEFT JOIN PUBLIC.SONGS S ON RN.ASSET_ISRC = S.ISRC
                        WHERE (IP.PAYEE = ?
                            OR IP.PAYEE01 = ?
                            OR IP.PAYEE02 = ?
                            OR IP.PAYEE03 = ?)
                            AND RN.REPORT_ID = ?
                        GROUP BY RN.DSP
                        ORDER BY REPORTED_ROYALTY_FOR_CEYMUSIC DESC;
                        """;

                int reportID = getFUGA_ReportID(report.getMonthInt(), report.getYear());

                try (Connection con = getConn();
                     PreparedStatement ps = con.prepareStatement(sql)) {
                    String artistName = report.getArtist().getName();
                    for (int i = 1; i <= 28; i++) {
                        ps.setString(i, artistName);
                    }
                    ps.setInt(29, reportID);

                    ResultSet rs = ps.executeQuery();
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            String dsp = rs.getString(1);
                            int assetQuantity = rs.getInt(2);
                            double reportedRoyaltyForCEYMusic = rs.getDouble(3);
                            dspBreakdownList.add(new DSPBreakdown(dsp, assetQuantity, reportedRoyaltyForCEYMusic));
                        }
                    }
                }
                return dspBreakdownList;
            } catch (PSQLException e) {
                if (e.getMessage().contains("An I/O error occurred") || e.getMessage().contains("This connection has been closed")) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    System.out.println("Retrying database operation, attempt " + retryCount);
                    // You might want to add a small delay here
                    try {
                        Thread.sleep(1000); // 1 second delay
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw e;
                }
            }
        }

        return dspBreakdownList;
    }


    public List<Payee> check(String name) {
        // String name = "Victor Rathnayake";
        Payee py;
        List<Payee> pyList = new ArrayList<>();
        try {
            List<String> sList;
            sList = getIsrc(name);

            for (int index = 0; index < sList.size(); index++) {
                String s = sList.get(index); // Get the string at the current index
                py = getPayeeShare(s);
                System.out.println(s + " ISRC LLIST");
                pyList.add(py);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pyList;

    }

    //get ISRC
    public List<String> getIsrc(String name) {
        System.out.println("inside" + name);
        Connection con = getConn();
        String sql = "SELECT isrc from isrc_payees where payee= ? or payee01= ? or payee02= ? ";
        List<String> slist = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String s = rs.getString(1);
                slist.add(s);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return slist;

    }

    public Payee getPayeeShare(String isrc) {

        Connection con = getConn();
        String sql = "SELECT payee,payee01,payee02,share,payee01share ,payee02share  from isrc_payees where isrc= ? ";
        Payee py = new Payee();
        double amount = 0.0;
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, isrc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                py.setPayee1(rs.getString(1));
                py.setPayee2(rs.getString(2));
                py.setPayee3(rs.getString(3));
                py.setShare1(rs.getString(4));
                py.setShare2(rs.getString(5));
                py.setShare3(rs.getString(6));
                py.setIsrc(isrc);

                if (py.getPayee1() != null) {
                    String amnt = getRoyaltyAmount(isrc);
                    if (amnt.equals("")) {
                        amount = 0.0;
                    } else {
                        amount = Double.parseDouble(getRoyaltyAmount(isrc));
                    }

                    py.setPayee1amount(amount);
                } else if (py.getPayee2() != null && py.getPayee1() != null) {
                    String amnt = getRoyaltyAmount(isrc);

                    if (amnt.equals("")) {
                        amount = 0.0;
                    } else {
                        amount = Double.parseDouble(getRoyaltyAmount(isrc));
                    }


//				double amount = getRoyaltyAmount(isrc);

//				py.setPayee1(sql);=amount/py.getShare1();
                    double sh1 = Double.parseDouble(py.getShare1());
                    if (sh1 == 50) {
                        double share1 = amount / 2;
                        py.setPayee1amount(share1);
                    }


                    double sh2 = Double.parseDouble(py.getShare2());
                    if (sh2 == 50) {
                        double share2 = amount / 2;
                        py.setPayee2Amount(share2);
                    }


                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }

        return py;

    }

    //GET ROYALTY AMOUNT
    public String getRoyaltyAmount(String isrc) {
        Connection con = getConn();
        String sql = "SELECT reported_royalty_for_ceymusic,asset_isrc from \"reportViewSummary1\" where asset_isrc= ? ";
        String amount = "";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, isrc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                amount = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return amount;

    }

    public static void searchAndCopySongs(String isrc, File directory, File destination) throws SQLException {
        Connection db = getConn();
        ResultSet rs;
        String filename;

        errorBuffer.setLength(0);

        PreparedStatement ps = db.prepareStatement("SELECT FILE_NAME " +
                "FROM songs " +
                "WHERE ISRC = ?");

        System.out.println("Before for loop for ISRC Codes");

        ps.setString(1, isrc);
        rs = ps.executeQuery();

        if (rs != null) {
            System.out.println("Result setCountry is not null");
            while (rs.next()) {
                System.out.println("Inside Result Set");
                filename = rs.getString("FILE_NAME");
                System.out.println("File Name: " + filename);
                Path start = Paths.get(directory.toURI());

                try (Stream<Path> stream = Files.walk(start)) {
                    String finalFilename = filename;
                    Path file = stream
                            .filter(path -> path.toFile().isFile())
                            .filter(path -> path.getFileName().toString().equals(finalFilename))
                            .findFirst()
                            .orElse(null);

                    if (file != null) {
                        Path targetDir = destination.toPath();
                        Path targetFile = targetDir.resolve(finalFilename);
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("File copied to: " + targetFile);
                    } else {
                        addSongNotFoundError("File not found for ISRC: " + isrc + "\n");
                        System.out.println("File not found for ISRC: " + isrc);
                    }
                } catch (Exception e) {
                    AlertBuilder.sendErrorAlert("Error", "An error occurred during file copy.", e.getMessage() + "\n Please consider using an accessible location");
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private static void addSongNotFoundError(String error) {
        errorBuffer.append(error);
    }

    private static void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    public static void updatePayees(CSVReader reader) {
        // Getting Connection
//        Connection conn = getConn();

        //new Conn Postgre
        System.out.println("inside update meth");
        Connection conn = DatabasePostgres.getConn();
        // Skipping the first line
//        reader.readNext();
//        PreparedStatement ps = conn.prepareStatement("INSERT INTO isrc_payees (ISRC, PAYEE, SHARE) " +
//                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE PAYEE=?, SHARE=?;");


        String sql = "INSERT INTO isrc_payees (isrc, payee, share,payee01,payee01share,payee02,payee02share) VALUES (?,?,?,?,?,?,?)";

        try {
            String[] record;
            while ((record = reader.readNext()) != null) {
                String isrc = record[0].trim();
                String payee = record[1].trim();
                String share = record[2].trim();

                String payee01 = record[3].trim();
                String payee01share = record[4].trim();
                String payee02 = record[5].trim();
                String payee02share = record[6].trim();

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, isrc);
                ps.setString(2, payee);
                ps.setString(3, share);
                ps.setString(4, payee01);
                ps.setString(5, payee01share);
                ps.setString(6, payee02);
                ps.setString(7, payee02share);
                ps.executeUpdate();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Songs> getTopPerformingSongs(String selectedItem) throws SQLException {
        Connection conn = getConn();

        String sql = """
                SELECT S.SONG_NAME,
                R.AFTER_DEDUCTION_ROYALTY / 100 * (CASE WHEN ip.payee = ? THEN ip.share WHEN ip.payee01 = ? THEN ip.payee01share ELSE ip.payee02share END) AS share,
                R.ASSET_ISRC
                FROM PUBLIC.summary_bd_02 AS R
                JOIN (SELECT ASSET_ISRC, MAX(AFTER_DEDUCTION_ROYALTY) AS MAX_ROYALTY
                FROM PUBLIC.summary_bd_02
                WHERE ASSET_ISRC IN (SELECT ISRC
                FROM PUBLIC.ISRC_PAYEES
                WHERE PAYEE01 = ?
                OR PAYEE = ?
                OR PAYEE02 = ?)
                GROUP BY ASSET_ISRC) AS MAX_ROYALTIES ON R.ASSET_ISRC = MAX_ROYALTIES.ASSET_ISRC AND R.AFTER_DEDUCTION_ROYALTY = MAX_ROYALTIES.MAX_ROYALTY
                LEFT JOIN PUBLIC.SONGS S ON R.ASSET_ISRC = S.ISRC
                LEFT JOIN PUBLIC.isrc_payees ip ON ip.isrc = R.asset_isrc
                ORDER BY share DESC
                LIMIT 5;
                """;

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, selectedItem);
        ps.setString(2, selectedItem);
        ps.setString(3, selectedItem);
        ps.setString(4, selectedItem);
        ps.setString(5, selectedItem);

        ResultSet rs = ps.executeQuery();

        ArrayList<Songs> songs = new ArrayList<>();

        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                Songs song = new Songs();
                song.setTrackTitle(rs.getString(1));
                song.setRoyalty(rs.getDouble(2));
                songs.add(song);
            }
        }

        return songs;
    }

    public static void main(String[] args) {
        Songs list = new Songs();
        list = (Songs) topPerformingSongs();
        System.out.println(list);
    }

    public static List<Songs> topPerformingSongs() {
        String sql = "select  asset_isrc, reported_royalty_for_ceymusic " +
                "from public.\"reportViewSummary1\" ORDER BY  reported_royalty_for_ceymusic   DESC LIMIT 6";

        Connection con = getConn();
        List<Songs> sngList = new ArrayList<>();

        try {
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Songs sng = new Songs();
                Songs sn = new Songs();
                sn = getSongData(rs.getString(1));
                sng.setTrackTitle(sn.getTrackTitle());
                sng.setComposer(getArtNameById(sn.getComposer()));
//                sng.setLyricists(getArtNameById(sn.getLyricists()));
                sng.setLyricist(getArtNameById(sn.getLyricist()));
                sng.setFeaturingArtist(getArtNameById(sn.getFeaturingArtist()));
                sng.setSinger(getArtNameById(sn.getSinger()));
                sngList.add(sn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return sngList;
    }

    public static Songs getSongData(String isrc) {
        String sql = "SELECT  song_name,composer,lyricist,featuring ,singer from songs where isrc= ? ";
        Connection con = getConn();
        Songs sng = new Songs();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, isrc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sng.setTrackTitle(rs.getString(1));
                sng.setComposer(rs.getString(2));
                sng.setLyricist(rs.getString(3));
                sng.setFeaturingArtist(rs.getString(4));
                sng.setSinger(rs.getString(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }

        return sng;

    }

    public static String getArtNameById(String id) {
        String sql = "";
        Connection con = getConn();
        String name = "SELECT artist_name from artists where artist_id = ? ";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                name = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return name;

    }

    public static ResultSet getTopPerformingSongsEdit(String selectedItem) throws SQLException, ClassNotFoundException {
        /*SELECT report.Asset_Title, isrc_payees.PAYEE, isrc_payees.SHARE, ( ( ( SUM( CASE WHEN report.Territory = 'AU' THEN report.Reported_Royalty ELSE 0 END ) ) * 0.9 ) +( SUM( CASE WHEN report.Territory != 'AU' THEN report.Reported_Royalty ELSE 0 END ) ) ) * 0.85 AS REPORTED_ROYALTY FROM report JOIN isrc_payees ON isrc_payees.ISRC = report.Asset_ISRC WHERE report.Asset_ISRC IN ( SELECT isrc_payees.ISRC FROM isrc_payees WHERE isrc_payees.PAYEE = 'Sarath De Alwis' ) GROUP BY isrc_payees.PAYEE, isrc_payees.SHARE, report.Asset_Title ORDER BY REPORTED_ROYALTY DESC LIMIT 5;*/
        Connection connection = getConn();

        PreparedStatement ps = connection.prepareStatement("SELECT report.Asset_Title, isrc_payees.PAYEE, isrc_payees.SHARE, (( ( ( SUM( CASE WHEN report.Territory = 'AU' THEN report.Reported_Royalty ELSE 0 END ) ) * 0.9 ) + ( SUM( CASE WHEN report.Territory != 'AU' THEN report.Reported_Royalty ELSE 0 END ) ) ) * 0.85) * isrc_payees.SHARE/100 AS REPORTED_ROYALTY FROM report JOIN isrc_payees ON isrc_payees.ISRC = report.Asset_ISRC WHERE report.Asset_ISRC IN( SELECT isrc_payees.ISRC FROM isrc_payees WHERE isrc_payees.PAYEE = ? ) GROUP BY isrc_payees.PAYEE, isrc_payees.SHARE, report.Asset_Title ORDER BY REPORTED_ROYALTY DESC LIMIT 6;");
        ps.setString(1, selectedItem);

        return ps.executeQuery();
    }

    public static String getReportNumber(String payee) {
        return "null";
    }

    public static List<Songs> searchSongDetailsBySearchType(String searchText, String searchType) throws SQLException {
        List<Songs> songs = new ArrayList<>();
        ResultSet rs;

        /*PreparedStatement ps = conn.prepareStatement("SELECT song_metadata.isrc, song_metadata.song_name, song_metadata.upc," +
                "song_metadata.artist, song_metadata.artist_type FROM song_metadata " +
                "WHERE song_metadata." + searchType + " ILIKE ? Limit 15");*/

        String sql = "SELECT song_metadata_new.isrc, song_metadata_new.song_name, song_metadata_new.upc, song_metadata_new.composer, song_metadata_new.lyricist, song_metadata_new.singer \n" +
                "FROM song_metadata_new WHERE song_metadata_new." + searchType + " ILIKE ? Limit 15";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println(searchType + "search type11111");
            System.out.println("searchText = " + searchText);

            ps.setString(1, searchText + "%");
            System.out.println("ps = " + ps);

            rs = ps.executeQuery();
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    songs.add(new Songs(
                            rs.getString(2), // TRACK_TITLE
                            rs.getString(1),// ISRC
                            rs.getString(6), // SINGER
                            rs.getString(4), // COMPOSER
                            rs.getString(5) // LYRICIST
                    ));

                    try {
                        // Printing Searched Content
                        System.out.println(songs.get(0).getISRC().trim() + " | " + songs.get(0).getTrackTitle() + " | " + songs.get(0).getSinger());
                        System.out.println(songs.get(1).getISRC().trim() + " | " + songs.get(1).getTrackTitle() + " | " + songs.get(1).getSinger());
                        System.out.println(songs.get(2).getISRC().trim() + " | " + songs.get(2).getTrackTitle() + " | " + songs.get(2).getSinger());
                        System.out.println("================");
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("End of results");
                    }
                }
            }
        }

        return songs;
    }

    public static Songs searchSongDetails(String isrc) throws SQLException, ClassNotFoundException {
        Songs song = new Songs();
        ResultSet rs;

        Connection conn = getConn();

        PreparedStatement ps = conn.prepareStatement("SELECT isrc, product_title, upc, song_name, singer, " +
                "null AS featuring,  lyricist, composer FROM public.\"song_metadata_new\" WHERE isrc = ? LIMIT 1;");
        // System.out.println("ISRC: " + isrc);
        ps.setString(1, isrc);
        rs = ps.executeQuery();

        while (rs.next()) {
            // System.out.println("Here");
            String isrcFromDatabase = rs.getString(1);
            String albumTitle = rs.getString(2);
            String upc = rs.getString(3);
            String trackTitle = rs.getString(4);
            String singer = rs.getString(5);
            String featuringArtist = rs.getString(6);
            String lyricist = rs.getString(7);
            String composer = rs.getString(8);
            song.setSongDetails(isrcFromDatabase, albumTitle, upc, trackTitle, singer, featuringArtist, composer, lyricist);
        }

        return song;
    }

    public static Boolean searchArtistTable(String artist) throws SQLException {
        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(ARTIST_NAME) FROM artists WHERE ARTIST_NAME = ? AND status = 5;")) {

            ps.setString(1, artist);

            ResultSet rs;
            int artistCount;

            rs = ps.executeQuery();

            boolean status = false;

            while (rs.next()) {
                artistCount = rs.getInt(1);
                if (artistCount > 0) {
                    status = true;
                }
            }

            return status;
        }
    }

    public static ArrayList<String> getArtistList() throws SQLException, ClassNotFoundException {
        Connection con = getConn();
        ArrayList<String> artistNames = new ArrayList<>();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM `artist_validation`");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            artistNames.add(rs.getString(1));
        }

        return artistNames;
    }

    public static void registerNewCeyMusicArtist(String artist01) throws SQLException, ClassNotFoundException {
        Connection con = getConn();

        PreparedStatement ps = con.prepareStatement("INSERT INTO artists (artist_name) VALUES (?);");
        PreparedStatement ps2 = con.prepareStatement("INSERT INTO artist_validation (name) VALUES (?);");

        ps.setString(1, artist01);
        ps2.setString(1, artist01);

        ps.executeUpdate();
        ps2.executeUpdate();
    }

    public static String getCatNoFor(String mainArtist) throws SQLException, ClassNotFoundException {
        String finalCatNo = null;
        ResultSet rs;

        Connection con = getConn();

        PreparedStatement ps = con.prepareStatement("SELECT `last_cat_no` FROM `artists` WHERE `ARTIST_NAME` = ?;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, mainArtist);
        rs = ps.executeQuery();

        // rs.first();

        if (rs.next()) {
            String lastCatNo = rs.getString(1);
            if (!Objects.equals(lastCatNo, "")) {
                String prefix = lastCatNo.substring(0, 8);
                int number = Integer.parseInt(lastCatNo.substring(8));
                number++;
                finalCatNo = prefix + String.format("%03d", number);
            }
        }

        return finalCatNo;
    }

    public static List<String> getAllSongs() throws SQLException, ClassNotFoundException {
        List<String> songTitles = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement("SELECT TRACK_TITLE FROM `songs`")) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String trackTitle = rs.getString("TRACK_TITLE");
                    songTitles.add(trackTitle);
                }
            }
        }

        return songTitles;
    }

    public static String getArtistName(int id) throws SQLException {
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT artist_name FROM public.artists WHERE artist_id = ?;");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        String artistName = null;
        if (rs.isBeforeFirst()) {
            rs.next();
            artistName = rs.getString(1);
        }
        return artistName;
    }

    public static int fetchArtistID(String name) throws SQLException {
        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement("SELECT artist_id FROM public.artists WHERE artist_name = ?;");) {

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            int artistID = 0;

            if (rs.isBeforeFirst()) {
                rs.next();
                artistID = rs.getInt(1);
            }

            return artistID;
        }
    }

    public static void addIngestCSV(byte[] byteArray, int ingestID) throws SQLException {
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement("UPDATE public.mc_ingests SET ingest=? WHERE ingest_id = ?;");
        preparedStatement.setBytes(1, byteArray);
        preparedStatement.setInt(2, ingestID);
        preparedStatement.executeUpdate();
    }

    public static List<CoWriterShare> getCoWriterSharenew(String artist) {
        // System.out.println("art = " + art);
        List<CoWriterShare> crLlist = new ArrayList<>();
        String sql = "SELECT  ip.isrc, ar.artist_name, CASE "
                + "        WHEN ip.payee = '" + artist + "' THEN ip.payee"
                + "        WHEN ip.payee01 =  '" + artist + "' THEN ip.payee01"
                + "        WHEN ip.payee02 =  '" + artist + "' THEN ip.payee02"
                + "    END AS payee_name,"
                + "    CASE "
                + "        WHEN ip.payee =  '" + artist + "' THEN ip.share"
                + "        WHEN ip.payee01 =  '" + artist + "' THEN ip.payee01share"
                + "        WHEN ip.payee02 =  '" + artist + "' THEN ip.payee02share"
                + "    END AS share,"
                + "    s.type AS song_type "
                + " FROM "
                + "    isrc_payees ip "
                + " JOIN "
                + "    songs s ON ip.isrc = s.isrc"
                + " JOIN"
                + "    artists ar ON s.composer = ar.artist_id OR s.lyricist = ar.artist_id "
                + " WHERE "
                + "    (ip.payee = ? AND ip.share = 100)"
                + "    OR (ip.payee01 = ? AND ip.payee01share = 100)"
                + "    OR (ip.payee02 = ? AND ip.payee02share = 100)";

        Connection con = getConn();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, artist);
            ps.setString(2, artist);
            ps.setString(3, artist);
            // System.out.println("ps = " + ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CoWriterShare cr = new CoWriterShare();
                cr.setIsrc(rs.getString(1));
                cr.setArtistName(rs.getString(2));
                cr.setPayee(rs.getString(3));
                cr.setShare(rs.getString(4));
                cr.setSongType(rs.getString(5));

                crLlist.add(cr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(con);
        }
        return crLlist;

    }

    public static List<CoWriterShare> getAssetBreakdown(String artist) throws SQLException {
        List<CoWriterShare> crLlist = new ArrayList<>();
        String sql = """
                SELECT ip.isrc,
                rep.after_deduction_royalty,
                s.song_name,
                ip.payee,
                ip.share,
                CASE WHEN ip.payee = (SELECT ar.artist_name
                                    FROM public.artists ar
                                    WHERE ar.artist_id = s.composer) THEN (SELECT ar.artist_name
                                                                           FROM public.artists ar
                                                                           WHERE ar.artist_id = s.lyricist)
                                                                           ELSE (SELECT ar.artist_name
                                                                                 FROM public.artists ar
                                                                                 WHERE ar.artist_id = s.composer)
                                                                                 END AS contributor,
                (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer) AS composer,
                (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.lyricist) AS lyricist,
                s.type
                FROM public.isrc_payees ip
                JOIN SONGS S ON IP.ISRC = S.ISRC
                JOIN public.summary_bd_02 rep ON IP.ISRC = rep.asset_isrc
                WHERE (ip.payee = ?)
                ORDER BY rep.after_deduction_royalty DESC;
                """;
        String sqlNew = """
                SELECT R.ASSET_ISRC,
                                                            R.AFTER_DEDUCTION_ROYALTY,
                                                            S.SONG_NAME, IP.PAYEE, IP.SHARE,
                                                            CASE\s
                                                                WHEN ip.payee = (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer)\s
                                                                THEN (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.lyricist)\s
                                                                ELSE (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer)\s
                                                            END AS contributor,
                                                            (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.composer) AS composer,
                                                            (SELECT ar.artist_name FROM public.artists ar WHERE ar.artist_id = s.lyricist) AS lyricist,\s
                                                            S.TYPE,
                                                            R.AFTER_DEDUCTION_ROYALTY * IP.SHARE / 100 AS calculated_royalty
                                                        FROM PUBLIC.summary_bd_02 AS R
                                                        JOIN (
                                                            SELECT ASSET_ISRC, MAX(AFTER_DEDUCTION_ROYALTY) AS MAX_ROYALTY
                                                            FROM PUBLIC.summary_bd_02
                                                            WHERE ASSET_ISRC IN (
                                                                SELECT ISRC
                                                                FROM PUBLIC.ISRC_PAYEES
                                                                WHERE PAYEE01 = ?
                                                                OR PAYEE = ?
                                                                OR PAYEE02 = ?
                                                            )
                                                            GROUP BY ASSET_ISRC
                                                        ) AS MAX_ROYALTIES ON R.ASSET_ISRC = MAX_ROYALTIES.ASSET_ISRC AND R.AFTER_DEDUCTION_ROYALTY = MAX_ROYALTIES.MAX_ROYALTY
                                                        LEFT JOIN PUBLIC.SONGS S ON R.ASSET_ISRC = S.ISRC
                                                        LEFT JOIN PUBLIC.isrc_payees ip ON ip.isrc = R.asset_isrc
                                                        ORDER BY R.AFTER_DEDUCTION_ROYALTY DESC;
                """;
        ResultSet rs = getAssetBreakdownResultSet(artist);
        while (rs.next()) {
            CoWriterShare cr = new CoWriterShare();
            cr.setIsrc(rs.getString(1));
            cr.setRoyalty(rs.getDouble(10));
            cr.setSongName(rs.getString(3));
            cr.setContributor(rs.getString(6));
            cr.setComposer(rs.getString(7));
            cr.setLyricist(rs.getString(8));
            cr.setSongType(rs.getString(9));
            cr.setShare(rs.getInt(5) + "%");
            crLlist.add(cr);
        }
        return crLlist;
    }

    private static ResultSet getAssetBreakdownResultSet(String artist) throws SQLException {
        String sqlNew2 = """
                SELECT R.ASSET_ISRC,
                	R.AFTER_DEDUCTION_ROYALTY,
                	S.SONG_NAME,
                	CASE
                        WHEN ? = IP.PAYEE THEN IP.PAYEE
                        WHEN ? = IP.PAYEE01 THEN IP.PAYEE01
                        WHEN ? = IP.PAYEE02 THEN IP.PAYEE02
                    END AS PAYEE,
                	IP.SHARE,
                	CASE
                        WHEN (CASE
                                  WHEN ? = IP.PAYEE THEN IP.PAYEE
                                  WHEN ? = IP.PAYEE01 THEN IP.PAYEE01
                                  WHEN ? = IP.PAYEE02 THEN IP.PAYEE02
                              END) = (SELECT AR.ARTIST_NAME FROM PUBLIC.ARTISTS AR WHERE AR.ARTIST_ID = S.COMPOSER)\s
                        THEN (SELECT AR.ARTIST_NAME FROM PUBLIC.ARTISTS AR WHERE AR.ARTIST_ID = S.LYRICIST)
                        ELSE (SELECT AR.ARTIST_NAME FROM PUBLIC.ARTISTS AR WHERE AR.ARTIST_ID = S.COMPOSER)
                    END AS CONTRIBUTOR,
                               \s
                	(SELECT AR.ARTIST_NAME FROM PUBLIC.ARTISTS AR WHERE AR.ARTIST_ID = S.COMPOSER) AS COMPOSER,
                               \s
                	(SELECT AR.ARTIST_NAME FROM PUBLIC.ARTISTS AR WHERE AR.ARTIST_ID = S.LYRICIST) AS LYRICIST,
                \t
                	 S.TYPE,
                	R.AFTER_DEDUCTION_ROYALTY * IP.SHARE / 100 AS CALCULATED_ROYALTY
                FROM PUBLIC.SUMMARY_BD_02 AS R
                JOIN
                	(SELECT ASSET_ISRC,
                			MAX(AFTER_DEDUCTION_ROYALTY) AS MAX_ROYALTY
                		FROM PUBLIC.SUMMARY_BD_02
                		WHERE ASSET_ISRC IN
                				(SELECT ISRC
                             FROM PUBLIC.ISRC_PAYEES
                             WHERE ? IN (PAYEE, PAYEE01, PAYEE02))
                		GROUP BY ASSET_ISRC) AS MAX_ROYALTIES ON R.ASSET_ISRC = MAX_ROYALTIES.ASSET_ISRC
                AND R.AFTER_DEDUCTION_ROYALTY = MAX_ROYALTIES.MAX_ROYALTY
                LEFT JOIN PUBLIC.SONGS S ON R.ASSET_ISRC = S.ISRC
                LEFT JOIN PUBLIC.ISRC_PAYEES IP ON IP.ISRC = R.ASSET_ISRC
                ORDER BY R.AFTER_DEDUCTION_ROYALTY DESC;
               \s""";
        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement(sqlNew2);
        ps.setString(1, artist);
        ps.setString(2, artist);
        ps.setString(3, artist);
        ps.setString(4, artist);
        ps.setString(5, artist);
        ps.setString(6, artist);
        ps.setString(7, artist);
        return ps.executeQuery();
    }

}
