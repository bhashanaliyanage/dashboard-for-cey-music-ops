package com.example.song_finder_fx;

import com.example.song_finder_fx.Model.Songs;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseMySQL {
    private static Connection conn = null;
    static StringBuilder errorBuffer = new StringBuilder();

    public static Connection getConn() throws ClassNotFoundException, SQLException {
        if (conn == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://192.168.1.200/songData";
            String username = "ceymusic";
            String password = "ceymusic";
            conn = DriverManager.getConnection(url, username, password);
        }
        return conn;
    }

    public static String searchFileName(String isrc) throws SQLException, ClassNotFoundException {
        Connection db = getConn();
        ResultSet rs;
        String filename = null;

        PreparedStatement ps = db.prepareStatement("SELECT FILE_NAME FROM songs WHERE ISRC = ?");
        ps.setString(1, isrc);

        rs = ps.executeQuery();

        while (rs.next()) {
            filename = rs.getString(1);
        }

        return filename;
    }

    public static void createTableArtists() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("CREATE TABLE IF NOT EXISTS artists (ARTIST_NAME VARCHAR(100) PRIMARY KEY)");
        ps.executeUpdate();
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

    public static boolean loadReport(File report, Label btnLoadReport, Label lbl_import, ImageView imgImportCaution) throws SQLException, ClassNotFoundException, IOException, CsvValidationException {
        Connection db = DatabaseMySQL.getConn();
        int rs = 0;
        DecimalFormat df = new DecimalFormat("0.00");

        PreparedStatement emptyTable = db.prepareStatement("DELETE FROM report;");
        emptyTable.executeUpdate();

        PreparedStatement ps = db.prepareStatement("INSERT INTO report " +
                "(Sale_Start_date, Sale_End_date, DSP, Sale_Store_Name, Sale_Type, Sale_User_Type, Territory, " +
                "Product_UPC, Product_Reference, Product_Catalog_Number, Product_Label, Product_Artist, Product_Title, " +
                "Asset_Artist, Asset_Title, Asset_Version, Asset_Duration, Asset_ISRC, Asset_Reference, AssetOrProduct, " +
                "Product_Quantity, Asset_Quantity, Original_Gross_Income, Original_currency, Exchange_Rate, " +
                "Converted_Gross_Income, Contract_deal_term, Reported_Royalty, Currency, Report_Run_ID, Report_ID, " +
                "Sale_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        CSVReader reader = new CSVReader(new FileReader(report.getAbsolutePath()));
        BufferedReader bReader = new BufferedReader(new FileReader(report));
        int rowcount = 0; // Total RowCount
        int rowcount2 = 0; // While loop's row count

        System.out.println("Here");
        while ((bReader.readLine()) != null) {
            rowcount++;
            System.out.println("rowcount = " + rowcount);
        }

        System.out.println("Total rowCount = " + rowcount);

        reader.readNext(); // Skipping the header
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            rowcount2++;

            double percentage = ((double) rowcount2 / rowcount) * 100;
            Platform.runLater(() -> btnLoadReport.setText("Processing " + df.format(percentage) + "%"));

            try {
                ps.setString(1, nextLine[0]); // Sale_Start_date
                ps.setString(2, nextLine[1]); // Sale_End_date
                ps.setString(3, nextLine[2]); // DSP
                ps.setString(4, nextLine[3]); // Sale_Store_Name
                ps.setString(5, nextLine[4]); // Sale_Type
                ps.setString(6, nextLine[5]); // Sale_User_Type
                ps.setString(7, nextLine[6]); // Territory
                try {
                    ps.setLong(8, Long.parseLong(nextLine[7])); // Product_UPC
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setLong(8, 0); // Product_UPC
                }
                try {
                    ps.setLong(9, Long.parseLong(nextLine[8])); // Product_Reference
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setLong(9, 0);
                }
                ps.setString(10, nextLine[9]); // Product_Catalog_Number
                ps.setString(11, nextLine[10]); // Product_Label
                ps.setString(12, nextLine[11]); // Product_Artist
                ps.setString(13, nextLine[12]); // Product_Title
                ps.setString(14, nextLine[13]); // Asset_Artist
                ps.setString(15, nextLine[14]); // Asset_Title
                ps.setString(16, nextLine[15]); // Asset_Version
                try {
                    ps.setInt(17, Integer.parseInt(nextLine[16])); // Asset_Duration
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setInt(17, 0); // Asset_Duration
                }
                ps.setString(18, nextLine[17]); // Asset_ISRC
                try {
                    ps.setLong(19, Long.parseLong(nextLine[18])); // Asset_Reference
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setLong(19, 0); // Asset_Reference
                }
                ps.setString(20, nextLine[19]); // AssetOrProduct
                if (!(Objects.equals(nextLine[20], ""))) {
                    ps.setInt(21, Integer.parseInt(nextLine[20])); // Product_Quantity
                } else {
                    ps.setInt(21, 0);
                }
                try {
                    ps.setInt(22, Integer.parseInt(nextLine[21])); // Asset_Quantity
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setInt(22, 0); // Asset_Quantity
                }
                try {
                    ps.setDouble(23, Double.parseDouble(nextLine[22])); // Original_Gross_Income
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setDouble(23, 0); // Original_Gross_Income
                }
                ps.setString(24, nextLine[23]); // Original_currency
                try {
                    ps.setDouble(25, Double.parseDouble(nextLine[24])); // Exchange_Rate
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setDouble(25, 0); // Exchange_Rate
                }
                try {
                    ps.setDouble(26, Double.parseDouble(nextLine[25])); // Converted_Gross_Income
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setDouble(26, 0); // Converted_Gross_Income
                }
                ps.setString(27, nextLine[26]); // Contract_deal_term
                try {
                    ps.setDouble(28, Double.parseDouble(nextLine[27])); // Reported_Royalty
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setDouble(28, 0); // Reported_Royalty
                }
                ps.setString(29, nextLine[28]); // Currency
                try {
                    ps.setInt(30, Integer.parseInt(nextLine[29])); // Report_Run_ID
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setInt(30, 0); // Report_Run_ID
                }
                try {
                    ps.setInt(31, Integer.parseInt(nextLine[30])); // Report_ID
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setInt(31, 0); // Report_ID
                }
                try {
                    ps.setLong(32, Long.parseLong(nextLine[31])); // Sale_ID
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NumberFormatException e) {
                    ps.setLong(32, 0); // Sale_ID
                }

                rs = ps.executeUpdate();
            } catch (SQLException | NumberFormatException e) {
                Platform.runLater(() -> {
                    lbl_import.setText("Import Error");
                    Image imgCaution = new Image("com/example/song_finder_fx/images/caution.png");
                    imgImportCaution.setImage(imgCaution);
                    imgImportCaution.setVisible(true);
                });
            }
        }

        return rs > 0;
    }

    public static ResultSet getTop5StreamedAssets() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Asset_ISRC, Asset_Title, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY Asset_ISRC ORDER BY Total_Royalty DESC LIMIT 5;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }

    public static ResultSet getTop4DSPs() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT DSP, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY DSP ORDER BY Total_Royalty DESC;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }

    public static ResultSet getTop5Territories() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Territory, SUM(Reported_Royalty) AS Total_Royalty, Currency " +
                "FROM report GROUP BY Territory ORDER BY Total_Royalty DESC LIMIT 5;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        return ps.executeQuery();
    }

    public static String getTotalAssetCount() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT COUNT(*) FROM ( SELECT Asset_ISRC FROM report GROUP BY Asset_ISRC ) AS subquery;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = ps.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static String getSalesDate() throws SQLException, ClassNotFoundException {
        Connection db = DatabaseMySQL.getConn();
        ResultSet rs;

        PreparedStatement ps = db.prepareStatement("SELECT Sale_Start_date FROM report LIMIT 1;", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = ps.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static ResultSet getFullBreakdown() throws SQLException, ClassNotFoundException {
        /*SELECT Asset_ISRC, SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END) AS 'AU_Earnings', (SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9 AS After_GST, SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END) AS 'Other_Territories_Earnings', ((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END)) AS Reported_Royalty_After_GST, (((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC FROM report GROUP BY Asset_ISRC;*/
        Connection db = DatabaseMySQL.getConn();

        PreparedStatement ps = db.prepareStatement("SELECT Asset_ISRC, SUM(Reported_Royalty) AS Reported_Royalty_Summary, " +
                "SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END) AS 'AU_Earnings', " +
                "(SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9 AS After_GST, " +
                "SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END) AS 'Other_Territories_Earnings', " +
                "((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END)) AS Reported_Royalty_After_GST, " +
                "(((SUM(CASE WHEN Territory = 'AU' THEN Reported_Royalty ELSE 0 END)) * 0.9) + (SUM(CASE WHEN Territory != 'AU' THEN Reported_Royalty ELSE 0 END))) * 0.85 AS Reported_Royalty_For_CEYMUSIC " +
                "FROM report GROUP BY Asset_ISRC ORDER BY Reported_Royalty_Summary DESC;");

        return ps.executeQuery();
    }

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

    public static void main(String[] args) throws SQLException, ClassNotFoundException, CsvValidationException, IOException {
        ArrayList<Double> gross = getPayeeGrossRev("Ajantha Ranasinghe");
        System.out.println("gross = " + gross);
    }

    static ArrayList<Double> getPayeeGrossRev(String artistName) throws SQLException, ClassNotFoundException {
        Connection connection = DatabaseMySQL.getConn();
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
                "LIMIT 5;");

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
        Connection conn = getConn();
        // Skipping the first line
        reader.readNext();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO isrc_payees (ISRC, PAYEE, SHARE) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE PAYEE=?, SHARE=?;");

        String[] row;
        while ((row = reader.readNext()) != null) {
            // Assigning Variables
            String isrc = row[0];
            String payee01 = row[1];
            String payee02 = row[3];
            String payee03 = row[5];
            int payee01Share;
            int payee02Share;
            int payee03Share;

            if (!Objects.equals(row[2], "")) {
                payee01Share = Integer.parseInt(row[2]);
            } else {
                payee01Share = 0;
            }

            if (!Objects.equals(row[4], "")) {
                payee02Share = Integer.parseInt(row[4]);
            } else {
                payee02Share = 0;
            }

            if (!Objects.equals(row[6], "")) {
                payee03Share = Integer.parseInt(row[6]);
            } else {
                payee03Share = 0;
            }

            // Update Payee 01
            ps.setString(1, isrc);
            ps.setString(2, payee01);
            ps.setInt(3, payee01Share);
            ps.setString(4, payee01);
            ps.setInt(5, payee01Share);
            ps.executeUpdate();

            // If Payee 02 is available
            if (!Objects.equals(payee02, "")) {
                ps.setString(1, isrc);
                ps.setString(2, payee02);
                ps.setInt(3, payee02Share);
                ps.setString(4, payee02);
                ps.setInt(5, payee02Share);
                ps.executeUpdate();
            }

            // If Payee 03 is available
            if (!Objects.equals(payee03, "")) {
                ps.setString(1, isrc);
                ps.setString(2, payee03);
                ps.setInt(3, payee03Share);
                ps.setString(4, payee03);
                ps.setInt(5, payee03Share);
                ps.executeUpdate();
            }
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

    public List<Songs> searchSongNamesByISRC(String searchText) throws ClassNotFoundException, SQLException {
        List<Songs> songs = new ArrayList<>();
        ResultSet rs;

        Connection conn = getConn();

        PreparedStatement ps = conn.prepareStatement("SELECT TRACK_TITLE, ISRC, SINGER FROM songs WHERE ISRC LIKE ? LIMIT 15");
        ps.setString(1, "%" + searchText + "%");
        rs = ps.executeQuery();

        while (rs.next()) {
            songs.add(new Songs(rs.getString(1), rs.getString(2), rs.getString(3)));
            System.out.println(rs.getString(1));
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

    public Songs searchSongDetails(String isrc) throws SQLException, ClassNotFoundException {
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
            String fileName = rs.getString(9);
            song.setSongDetails(isrcFromDatabase, albumTitle, upc, trackTitle, singer, featuringArtist, composer, lyricist, fileName);
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
}
