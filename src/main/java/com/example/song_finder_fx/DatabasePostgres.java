package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.FUGAReport;
import com.example.song_finder_fx.Model.ManualClaimTrack;
import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabasePostgres {

    static StringBuilder errorBuffer = new StringBuilder();
    private static Connection conn;

    public static Connection getConn() {
        String dbname = "songdata";
        String user = "postgres";
        String pass = "ceymusic";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://192.168.1.200:5432/" + dbname, user, pass);
        } catch (Exception e) {
            System.out.println("Error Connecting to database = " + e);
        }

        return conn;
    }

    private static int updateCatNo(String artist_name, String cat_no_handler, int last_cat_no) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("UPDATE public.artists SET cat_no_handler = '%s', last_cat_no = %s WHERE artist_name = '%s';", cat_no_handler, last_cat_no, artist_name);

        return statement.executeUpdate(query);
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

    public static int addManualClaim(String songName, String lyricist, String composer, String url, String trimStart, String trimEnd) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("INSERT INTO public.manual_claims (song_name, composer, lyricist, youtube_id, trim_start, trim_end) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", songName, composer, lyricist, url, trimStart, trimEnd);
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

    public static List<ManualClaimTrack> getManualClaims() throws SQLException, IOException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "SELECT claim_id, song_name, composer, lyricist, youtube_id, trim_start, trim_end, preview_image, artwork FROM public.manual_claims WHERE ingest_status = false AND archive = false ORDER BY public.manual_claims.claim_id ASC;";
        ResultSet resultSet = statement.executeQuery(query);
        List<ManualClaimTrack> manualClaims = new ArrayList<>();

        if (resultSet.isBeforeFirst()) {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String songName = resultSet.getString(2);
                String composer = resultSet.getString(3);
                String lyrics = resultSet.getString(4);
                String youTubeLink = resultSet.getString(5);
                String trimStart = resultSet.getString(6);
                String trimEnd = resultSet.getString(7);
                byte[] previewImageBytes = resultSet.getBytes(8);
                byte[] artworkImageBytes = resultSet.getBytes(9);

                ManualClaimTrack manualClaimTrack = new ManualClaimTrack(id, songName, lyrics, composer, youTubeLink);

                // Set the images to model
                try {
                    ByteArrayInputStream previewImageInputStream = new ByteArrayInputStream(previewImageBytes);
                    ByteArrayInputStream artworkImageInputStream = new ByteArrayInputStream(artworkImageBytes);
                    manualClaimTrack.setPreviewImage(ImageIO.read(previewImageInputStream));
                    manualClaimTrack.setImage(ImageIO.read(artworkImageInputStream));
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error loading one or more images");
                    alert.setContentText(String.valueOf(e));
                    Platform.runLater(alert::showAndWait);
                }

                if (trimStart != null && trimEnd != null) {
                    manualClaimTrack.addTrimTime(trimStart, trimEnd);
                }

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

    public static List<String> getAllArtists() throws SQLException {
        Connection conn = getConn();
        String query = "SELECT artist_name FROM public.artists ORDER BY artist_name ASC;";
        Statement statement = conn.createStatement();
        List<String> artists = new ArrayList<>();

        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String artist = rs.getString(1);
            artists.add(artist);
        }

        return artists;
    }

    public static void editManualClaim(String songID, String trackName, String composer, String lyricist) throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = String.format("UPDATE public.manual_claims SET song_name = '%s', composer = '%s', lyricist = '%s' WHERE claim_id = %s;", trackName, composer, lyricist, songID);
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

    public static String getNewUGCISRC() throws SQLException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        String query = "SELECT isrc FROM public.songs WHERE isrc LIKE 'LKA0U%' ORDER BY isrc DESC LIMIT 1;";
        ResultSet rs = statement.executeQuery(query);
        if (rs.isBeforeFirst()) {
            rs.next();
            String lastISRC = rs.getString(1);

            // Extract prefix (first 7 characters)
            String prefix = lastISRC.substring(0, 7);

            // Extract suffix (remaining characters)
            String suffixStr = lastISRC.substring(7);
            int suffix = Integer.parseInt(suffixStr);
            suffix++;

            // Format the suffix as a 5-digit integer
            String formattedSuffix = String.format("%05d", suffix);

            return prefix + formattedSuffix;
        }

        return "Error!";
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

    public static int addIngest(LocalDate date, String userName, String filePath, String ingestFileName) throws SQLException {
        Connection db = getConn();
        String query = String.format("INSERT INTO public.ingests(ingest_date, user_name_note, root_folder, csv_filename) VALUES ('%s', '%s', '%s', '%s') RETURNING ingest_id;", date, userName, filePath, ingestFileName);
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

    public static void addIngestProduct(int ingestID, String upc, String albumTitle, String s, String composer, String lyricist, String originalFileName) throws SQLException {
        Connection db = getConn();
        String query = String.format("INSERT INTO " +
                        "public.ingest_products(ingest_id, upc, song_name, isrc, composer, lyricist, file_name) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                ingestID, upc, albumTitle, s, composer, lyricist, originalFileName);
        Statement statement = db.createStatement();
        statement.executeUpdate(query);
    }

    public static void main(String[] args) throws IOException, CsvValidationException, SQLException, ClassNotFoundException {
        File csv = new File("src/main/resources/com/example/song_finder_fx/CeyMusic Song Database  - Song Artist DB Import - 03.csv");
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(csv)).build();
        // System.out.println("inside main");

        insertSongArtists(csvReader);

        csvReader.close();
    }

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

    /*public static int addManualClaim(ManualClaimTrack claim) throws SQLException, IOException {
        Connection conn = getConn();
        Statement statement = conn.createStatement();

        // Convert BufferedImage to byte array
        BufferedImage bufferedImage = claim.getBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        String query = String.format("INSERT INTO public.manual_claims " +
                        "(song_name, composer, lyricist, youtube_id, trim_start, trim_end, artwork) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                claim.getTrackName(),
                claim.getComposer(),
                claim.getLyricist(),
                claim.getYoutubeID(),
                claim.getTrimStart(),
                claim.getTrimEnd());

        return statement.executeUpdate(query);
    }*/

    public static int addManualClaim(ManualClaimTrack claim) throws SQLException, IOException {
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO public.manual_claims " +
                        "(song_name, composer, lyricist, youtube_id, trim_start, trim_end, artwork, preview_image) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        // Get artwork
        ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
        ImageIO.write(claim.getBufferedImage(), "jpg", binaryData);
        byte[] artwork = binaryData.toByteArray();

        // Get previewImage
        ImageIO.write(claim.getBufferedPreviewImage(), "jpg", binaryData);
        byte[] previewImage = binaryData.toByteArray();

        // Set values for the prepared statement
        preparedStatement.setString(1, claim.getTrackName());
        preparedStatement.setString(2, claim.getComposer());
        preparedStatement.setString(3, claim.getLyricist());
        preparedStatement.setString(4, claim.getYoutubeID());
        preparedStatement.setString(5, claim.getTrimStart());
        preparedStatement.setString(6, claim.getTrimEnd());
        preparedStatement.setBytes(7, artwork);
        preparedStatement.setBytes(8, previewImage);

        // Execute the prepared statement
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
        Connection db = DatabaseMySQL.getConn();
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

    public static ResultSet getPayees() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        PreparedStatement preparedStatement = db.prepareStatement("SELECT PAYEE FROM `isrc_payees` GROUP BY PAYEE ORDER BY PAYEE ASC;");

        return preparedStatement.executeQuery();
    }

    static ArrayList<Double> getPayeeGrossRev(String artistName) throws SQLException, ClassNotFoundException {
        Connection connection = getConn();
        ArrayList<Double> royalty = new ArrayList<>();

        PreparedStatement psGetGross = connection.prepareStatement("SELECT Asset_ISRC, " +
                "((((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85) * `isrc_payees`.`SHARE`/100 AS REPORTED_ROYALTY, " +
                "(((((SUM(CASE WHEN Territory = 'AU' AND Product_Label = 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' AND Product_Label = 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END))) * 0.85) * 0.1) + (((((SUM(CASE WHEN Territory = 'AU' AND Product_Label != 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' AND Product_Label != 'CeyMusic Records' THEN Reported_Royalty ELSE 0 END))) * 0.85) * 0.1) AS PARTNER_SHARE " +
                "FROM `report` " +
                "JOIN isrc_payees ON isrc_payees.ISRC = report.Asset_ISRC AND `isrc_payees`.`PAYEE` = ? " +
                "ORDER BY `REPORTED_ROYALTY` DESC;");
        psGetGross.setString(1, artistName);
        ResultSet rsGross = psGetGross.executeQuery();
        rsGross.next();

        royalty.add(rsGross.getDouble(2));
        royalty.add(rsGross.getDouble(3));

        return royalty;
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

    public static void searchAndCopySongs(String isrc, File directory, File destination) throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
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
                    showErrorDialog("Error", "An error occurred during file copy.", e.getMessage() + "\n Please consider using an accessible location");
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

    public static void updatePayees(CSVReader reader) throws CsvValidationException, IOException, SQLException, ClassNotFoundException {
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

    public static ResultSet getTopPerformingSongs(String selectedItem) throws SQLException, ClassNotFoundException {
        /*SELECT `report`.`Asset_Title`, (((SUM(CASE WHEN `report`.`Territory` = 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END)) * 0.9) + (SUM(CASE WHEN `report`.`Territory` != 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END))) * 0.85 AS REPORTED_ROYALTY
        FROM `report` JOIN `isrc_payees` ON `isrc_payees`.`ISRC` = `report`.`Asset_ISRC`
        WHERE `report`.`Asset_ISRC` IN (SELECT `isrc_payees`.`ISRC` WHERE `isrc_payees`.`PAYEE` = 'Sarath De Alwis')
        GROUP BY `report`.`Asset_ISRC`
        LIMIT 5;*/
        Connection connection = getConn();

        PreparedStatement ps = connection.prepareStatement("SELECT `report`.`Asset_Title`, (((SUM(CASE WHEN `report`.`Territory` = 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END)) * 0.9) + (SUM(CASE WHEN `report`.`Territory` != 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END))) * 0.85 AS REPORTED_ROYALTY " +
                "FROM `report` JOIN `isrc_payees` ON `isrc_payees`.`ISRC` = `report`.`Asset_ISRC` " +
                "WHERE `report`.`Asset_ISRC` IN (SELECT `isrc_payees`.`ISRC` WHERE `isrc_payees`.`PAYEE` = ?) " +
                "GROUP BY `report`.`Asset_ISRC` " +
                "LIMIT 5;");
        ps.setString(1, selectedItem);

        return ps.executeQuery();
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

    public static List<Songs> searchSongDetailsBySearchType(String searchText, String searchType) throws SQLException, ClassNotFoundException {
        List<Songs> songs = new ArrayList<>();
        ResultSet rs;

        Connection conn = getConn();

        PreparedStatement ps = conn.prepareStatement("SELECT TRACK_TITLE, ISRC, SINGER, COMPOSER, LYRICIST " +
                "FROM songs WHERE " + searchType + " LIKE ? LIMIT 15");
        ps.setString(1, searchText + "%");
        rs = ps.executeQuery();

        while (rs.next()) {
            songs.add(new Songs(
                    rs.getString(1), // TRACK_TITLE
                    rs.getString(2), // ISRC
                    rs.getString(3), // SINGER
                    rs.getString(4), // COMPOSER
                    rs.getString(5) // LYRICIST
            ));
        }

        try {
            // Printing Searched Content
            System.out.println(songs.get(0).getISRC().trim() + " | " + songs.get(0).getTrackTitle() + " | " + songs.get(0).getSinger());
            System.out.println(songs.get(1).getISRC().trim() + " | " + songs.get(1).getTrackTitle() + " | " + songs.get(1).getSinger());
            System.out.println(songs.get(2).getISRC().trim() + " | " + songs.get(2).getTrackTitle() + " | " + songs.get(2).getSinger());

            // Printing new line
            System.out.println("================");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("End of results");
        }

        return songs;
    }

    public static Songs searchSongDetails(String isrc) throws SQLException, ClassNotFoundException {
        Songs song = new Songs();
        ResultSet rs;

        Connection conn = getConn();

        PreparedStatement ps = conn.prepareStatement("SELECT ISRC, " +
                "ALBUM_TITLE, " +
                "UPC, " +
                "TRACK_TITLE, " +
                "SINGER, " +
                "FEATURING, " +
                "COMPOSER, " +
                "LYRICIST, " +
                "FILE_NAME FROM songs WHERE ISRC = ? LIMIT 1");
        System.out.println("ISRC: " + isrc);
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
            String composer = rs.getString(7);
            String lyricist = rs.getString(8);
            song.setSongDetails(isrcFromDatabase, albumTitle, upc, trackTitle, singer, featuringArtist, composer, lyricist);
        }

        return song;
    }

    public static Boolean searchArtistTable(String artist) throws SQLException, ClassNotFoundException {
        ResultSet rs;
        Connection conn = getConn();
        String artistName = null;

        PreparedStatement ps = conn.prepareStatement("SELECT ARTIST_NAME FROM artists WHERE ARTIST_NAME = ?");
        ps.setString(1, artist);
        rs = ps.executeQuery();

        while (rs.next()) {
            artistName = rs.getString(1);
        }

        return artistName != null;
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

        Connection con = getConn();
        PreparedStatement ps = con.prepareStatement("SELECT TRACK_TITLE FROM `songs`");

        try {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String trackTitle = rs.getString("TRACK_TITLE");
                songTitles.add(trackTitle);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return songTitles;
    }
}
