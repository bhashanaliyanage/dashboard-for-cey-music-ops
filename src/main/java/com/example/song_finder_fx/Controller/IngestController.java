package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngestController {

    public List<String> getMissingPayeeList() {

        List<String> isrcList = new ArrayList<>();
        String sql = "SELECT asset_isrc FROM report WHERE asset_isrc NOT IN (SELECT isrc FROM isrc_payees)";

        try (Connection con = DatabasePostgres.getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String isrc = rs.getString(1);
                isrcList.add(isrc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isrcList;
    }

    public List<String> getMissingSongs() {

        List<String> isrcList = new ArrayList<>();
        String sql = "SELECT asset_isrc FROM report WHERE asset_isrc NOT IN (SELECT isrc FROM songs)";
        System.out.println(sql);

        try (Connection con = DatabasePostgres.getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String isrc = rs.getString(1);
                System.out.println(isrc + " this is string set");
                isrcList.add(isrc);
            }

            System.out.println("123");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isrcList;

    }

    public List<IngestCSVData> readCsvData(String file) {
        List<IngestCSVData> dataList = new ArrayList<IngestCSVData>();
        try (CSVReader csvreader = new CSVReaderBuilder(new FileReader(file)).withSkipLines(1).build()) {

            String[] record;

            while ((record = csvreader.readNext()) != null) {
                String AlbumTitle = record[1].trim();
                String upc = record[3].trim();
                String catalogNumber = record[4].trim();
                String releaseDate = record[7].trim();
                String label = record[12].trim();
                String Clineyear = record[13].trim();
                String CLineName = record[14].trim();
                String PLineYear = record[15].trim();
                String PLineName = record[16].trim();
                String RecrdingYear = record[18].trim();
                String RecordingLocation = record[19].trim();
                String albumFormat = record[20].trim();
                String trackTitle = record[26].trim();
                String isrc = record[28].trim();
                String trackPrimaryArtist = record[29].trim();
                String Composer = record[46].trim();
                String Lyricists = record[47].trim();
                String Writers = record[51].trim();
                String Publishers = record[52].trim();
                String OriginalFileName = record[55].trim();
//				String originalReleaseDate = record[56].trim();
                IngestCSVData csv = new IngestCSVData();

                csv.setAlbumTitle(AlbumTitle);
                csv.setUpc(upc);
                csv.setCatalogNumber(catalogNumber);
                csv.setReleaseData(releaseDate);
                csv.setLabel(label);
                csv.setClineYear(Clineyear);
                csv.setClineName(CLineName);
                csv.setPlineYear(PLineYear);
                csv.setPlineName(PLineName);
                csv.setRecordingYear(RecrdingYear);
                csv.setRecordingLocation(RecordingLocation);
                csv.setAlbumFormat(albumFormat);
                csv.setTrackTitle(trackTitle);
                csv.setIsrc(isrc);
                csv.setTrackPrimaryArtist(trackPrimaryArtist);
                csv.setComposer(Composer);
                csv.setLyricist(Lyricists);
                csv.setWriters(Writers);
                csv.setPublishers(Publishers);
                csv.setOriginalFileName(OriginalFileName);

                dataList.add(csv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public String insertTempTable(List<IngestCSVData> dataList) {
        Connection con = DatabasePostgres.getConn();
        String s = "";
        String sql = "INSERT INTO temp_table ("
                + "    album_title, upc, catalog_number, release_date, label, cline_year, cline_name, pline_name, "
                + "    pline_year, recording_year, recording_location, album_format, track_title, isrc, track_primary_artist, "
                + "    composer, lyricists, writers, publishers, original_filename ) "
                + "   VALUES ("
                + "    ?, ?, ?, ?, ?, ?, ?,?, "    //8
                + "    ?, ?, ?, ?, ?, ?, ?, ?, "    // 8 - 16
                + "    ?, ?, ?, ?)";            //16 - 20

        try {
            for (IngestCSVData data : dataList) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, data.getAlbumTitle());
                ps.setString(2, data.getUpc());
                ps.setString(3, data.getCatalogNumber());
                ps.setString(4, data.releaseDate());
                ps.setString(5, data.getLabel());
                ps.setString(6, data.getClineYear());
                ps.setString(7, data.getClineName());
                ps.setString(8, data.getPlineName());
                ps.setString(9, data.getPlineYear());
                ps.setString(10, data.getRecordingYear());
                ps.setString(11, data.getRecordingLocation());
                ps.setString(12, data.getAlbumFormat());
                ps.setString(13, data.getTrackTitle());
                ps.setString(14, data.getIsrc());
                ps.setString(15, data.getTrackPrimaryArtist());
                ps.setString(16, data.getComposer());
                ps.setString(17, data.getLyricist());
                ps.setString(18, data.getWriters());
                ps.setString(19, data.getPublishers());
                ps.setString(20, data.getOriginalFileName());
                ps.executeUpdate();
                s = "done";
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabasePostgres.closeConnection(con);
        }
        return s;
    }

    public String insertTempTable(List<IngestCSVData> dataList, int id) {
        Connection con = DatabasePostgres.getConn();
        String s = "";
        String sql = "INSERT INTO temp_ingests ("
                + "    album_title, upc, catalog_number, release_date, label, cline_year, cline_name, pline_name, "
                + "    pline_year, recording_year, recording_location, album_format, track_title, isrc, track_primary_artist, "
                + "    composer, lyricists, writers, publishers, original_filename, ingest_id) "
                + "   VALUES ("
                + "    ?, ?, ?, ?, ?, ?, ?,?, "    //8
                + "    ?, ?, ?, ?, ?, ?, ?, ?, "    // 8 - 16
                + "    ?, ?, ?, ?, ?)";            //16 - 20

        try {
            for (IngestCSVData data : dataList) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, data.getAlbumTitle());
                ps.setString(2, data.getUpc());
                ps.setString(3, data.getCatalogNumber());
                ps.setString(4, data.releaseDate());
                ps.setString(5, data.getLabel());
                ps.setString(6, data.getClineYear());
                ps.setString(7, data.getClineName());
                ps.setString(8, data.getPlineName());
                ps.setString(9, data.getPlineYear());
                ps.setString(10, data.getRecordingYear());
                ps.setString(11, data.getRecordingLocation());
                ps.setString(12, data.getAlbumFormat());
                ps.setString(13, data.getTrackTitle());
                ps.setString(14, data.getIsrc());
                ps.setString(15, data.getTrackPrimaryArtist());
                ps.setString(16, data.getComposer());
                ps.setString(17, data.getLyricist());
                ps.setString(18, data.getWriters());
                ps.setString(19, data.getPublishers());
                ps.setString(20, data.getOriginalFileName());
                ps.setInt(21, id);
                ps.executeUpdate();
                s = "done";
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabasePostgres.closeConnection(con);
        }
        return s;
    }

    public String insertIngest(String ingestName, LocalDate date, File file) throws SQLException {
        System.out.println("IngestController.insertIngest");

        int id = DatabasePostgres.addTempIngestMetadata(ingestName, date);

        if (id > 0) {
            List<IngestCSVData> dataList;
            dataList = readCsvData(file.getAbsolutePath());
            return insertTempTable(dataList, id);
        }

        return "";
    }

    public void approveIngest(Ingest ingest) throws SQLException {
        List<IngestCSVData> csvRows = ingest.getIngestCSVDataList();

        // Create Products
        List<String> productNames = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        for (IngestCSVData row : csvRows) {
            String currentProductName = row.getAlbumTitle();

            if (!productNames.contains(currentProductName)) {
                Product product = new Product(row.getUpc(),
                        row.getAlbumTitle(),
                        row.getCatalogNumber(),
                        row.releaseDate());

                products.add(product);
                productNames.add(currentProductName);

                DatabasePostgres.addProduct(product);
                // System.out.println("Adding Product: " + product.getAlbumTitle());
            }

        }
    }

    public ValidationResult validateIngest(Ingest ingest) {
        List<IngestCSVData> ingestCSVDataList = ingest.getIngestCSVDataList();
        boolean isValid = true;
        List<String> errorMessages = new ArrayList<>();

        for (IngestCSVData ingestCSVData : ingestCSVDataList) {
            // Validate ISRC
            if (!validateISRC(ingestCSVData.getIsrc())) {
                System.out.println("Invalid ISRC for track: " + ingestCSVData.getTrackTitle() + " | ISRC: " + ingestCSVData.getIsrc());
                errorMessages.add("Invalid ISRC for track: " + ingestCSVData.getTrackTitle() + " | ISRC: " + ingestCSVData.getIsrc());
                isValid = false;
            }
        }

        return new ValidationResult(isValid, errorMessages);
    }

    public boolean validateISRC(String isrc) {
        if (isrc == null || isrc.length() != 12) {
            return false;
        }

        // Check country code (first two characters)
        String countryCode = isrc.substring(0, 2);
        if (!countryCode.matches("[A-Z]{2}")) {
            return false;
        }

        // Check registrant code (next three characters)
        String registrantCode = isrc.substring(2, 5);
        if (!registrantCode.matches("[A-Z0-9]{3}")) {
            return false;
        }

        // Check year (next two characters)
        String year = isrc.substring(5, 7);
        if (!year.matches("\\d{2}")) {
            return false;
        }

        // Check designation code (last five characters)
        String designationCode = isrc.substring(7);
        if (!designationCode.matches("\\d{5}")) {
            return false;
        }

        return true;
    }
}
