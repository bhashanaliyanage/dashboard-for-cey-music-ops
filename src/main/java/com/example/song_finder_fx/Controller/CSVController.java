package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabaseMySQL;
import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.FUGAReport;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CSVController {
    private File csv;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final Path tempDir = Files.createTempDirectory("missing_isrcs");
    private final Path csvFile = tempDir.resolve("missing_isrcs.csv");

    public static void main(String[] args) {
        String filePath = "C:\\Users\\bhash\\Documents\\Test\\test.csv";

        // Sample data (replace with your actual data)
        String claimID = "123";
        String albumTitle = "My Album";
        String upc = "987654321";
        String composer = "John Doe";
        String lyricist = "Jane Smith";
        String originalFileName = "song.mp3";

        try (CsvListWriter csvWriter = new CsvListWriter(new FileWriter(filePath), CsvPreference.STANDARD_PREFERENCE)) {
            // Write the header (optional)
            csvWriter.writeHeader("Claim ID", "Album Title", "UPC", "Composer", "Lyricist", "Original File Name");

            // Create a list for the row
            List<String> csvRow = Arrays.asList(claimID, albumTitle, upc, composer, lyricist, originalFileName);

            // Write the row
            csvWriter.write(csvRow);

            System.out.println("Data written successfully to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CSVController() throws IOException {

    }

    public static FUGAReport getFUGAReport(String[] nextLine) {
        FUGAReport report = new FUGAReport();

        report.setSaleStartDate(nextLine[0]);
        // report.setSaleEndDate(nextLine[1]);
        report.setDsp(nextLine[2]);
        // report.setSaleStoreName(nextLine[3]);
        // report.setSaleType(nextLine[4]);
        // report.setSaleUserType(nextLine[5]);
        report.setTerritory(nextLine[6]);
        // report.setProductUPC(nextLine[7]);
        // report.setProductReference(nextLine[8]);
        // report.setProductCatalogNumber(nextLine[9]);
        // report.setProductLabel(nextLine[10]);
        // report.setProductArtist(nextLine[11]);
        // report.setProductTitle(nextLine[12]);
        // report.setAssetArtist(nextLine[13]);
        // report.setAssetTitle(nextLine[14]);
        // report.setAssetVersion(nextLine[15]);
        // report.setAssetDuration(nextLine[16]);
        report.setAssetISRC(nextLine[17]);
        // report.setAssetReference(nextLine[18]);
        // report.setAssetOrProduct(nextLine[19]);
        // report.setProductQuantity(nextLine[20]);
        // report.setAssetQuantity(nextLine[21]);
        // report.setOriginalGrossIncome(nextLine[22]);
        // report.setOriginalCurrency(nextLine[23]);
        // report.setExchangeRate(nextLine[24]);
        // report.setConvertedGrossIncome(nextLine[25]);
        // report.setContractDealTerm(nextLine[26]);
        report.setReportedRoyalty(nextLine[27]);
        // report.setCurrency(nextLine[28]);
        // report.setReportRunID(nextLine[29]);
        // report.setReportID(nextLine[30]);
        // report.setSaleID(nextLine[31]);

        return report;
    }

    public static int getReportTotalRowCount(BufferedReader bReader) throws IOException {
        int rowcount = 0; // Total RowCount
        while ((bReader.readLine()) != null) {
            rowcount++;
        }
        // System.out.println("rowcount = " + rowcount);
        return rowcount;
    }

    public void setFUGAReport(File report) {
        if (report.canRead()) {
            this.csv = report;
        }
    }

    public int loadFUGAReport(Label lblReportProgress, ImageView imgImportCaution, Label lbl_import) throws IOException, CsvValidationException, SQLException {
        int status = 0;
        CSVReader reader = new CSVReader(new FileReader(csv.getAbsolutePath()));
        BufferedReader bReader = new BufferedReader(new FileReader(csv));
        Connection conn = DatabasePostgres.getConn();

        // DatabaseMySQL.emptyReportTable();
        DatabasePostgres.emptyReportTable();

        int totalRowCount = getReportTotalRowCount(bReader);
        int rowcount2 = 0; // While loop's row count

        reader.readNext(); // Skipping the header
        String[] nextLine;
        long startTime = System.nanoTime();

        while ((nextLine = reader.readNext()) != null) {
            rowcount2++;
            double percentage = ((double) rowcount2 / totalRowCount) * 100;

            Platform.runLater(() -> lblReportProgress.setText("Processing " + df.format(percentage) + "%"));

            FUGAReport report = getFUGAReport(nextLine);

            Platform.runLater(() -> System.out.println("percentage = " + percentage));

            try {
                // status = DatabaseMySQL.addRowFUGAReport(report); // Dev Bhashana MySQL
                // status = DatabaseMySQL.addRowFUGAReportnew(report); // Dev Sudesh MySQL SP
                status = DatabasePostgres.addRowFUGAReport(report, conn);
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    lbl_import.setText("Import Error");
                    Image imgCaution = new Image("com/example/song_finder_fx/images/caution.png");
                    imgImportCaution.setImage(imgCaution);
                    imgImportCaution.setVisible(true);
                    throw new RuntimeException(e);
                });
            }
        }

        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000;

        Platform.runLater(() -> System.out.println("Execution time: " + durationInSeconds + " seconds"));

        return status;
    }

    public int writeMissingISRCs() throws SQLException, ClassNotFoundException, IOException {
        ResultSet resultSet = DatabaseMySQL.checkMissingISRCs();
        // ResultSet resultSet = DatabasePostgres.checkMissingISRCs();      //Connection for Postgress

        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile.toFile()));
        List<String[]> rows = new ArrayList<>();

        while (resultSet.next()) {
            if ((!Objects.equals(resultSet.getString(1), "")) && (resultSet.getString(2) == null) && (resultSet.getString(3) == null)) {
                String[] row = new String[]{
                        resultSet.getString(1)
                };
                rows.add(row);
            }
        }

        csvWriter.writeAll(rows);
        csvWriter.close();

        return rows.size();
    }

    public void copyMissingISRCList(File destination) throws IOException {
        Path destinationPath = destination.toPath().resolve(csvFile.getFileName());
        Files.copy(csvFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

}
