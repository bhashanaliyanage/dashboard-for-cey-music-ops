package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabaseMySQL;
import com.example.song_finder_fx.Model.FUGAReport;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVController {
    private File csv;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final Path tempDir = Files.createTempDirectory("missing_isrcs");
    private final Path csvFile = tempDir.resolve("missing_isrcs.csv");

    public CSVController() throws IOException {

    }

    private static FUGAReport getFUGAReport(String[] nextLine) {
        FUGAReport report = new FUGAReport();

        report.setSaleStartDate(nextLine[0]);
        report.setSaleEndDate(nextLine[1]);
        report.setDsp(nextLine[2]);
        report.setSaleStoreName(nextLine[3]);
        report.setSaleType(nextLine[4]);
        report.setSaleUserType(nextLine[5]);
        report.setTerritory(nextLine[6]);
        report.setProductUPC(nextLine[7]);
        report.setProductReference(nextLine[8]);
        report.setProductCatalogNumber(nextLine[9]);
        report.setProductLabel(nextLine[10]);
        report.setProductArtist(nextLine[11]);
        report.setProductTitle(nextLine[12]);
        report.setAssetArtist(nextLine[13]);
        report.setAssetTitle(nextLine[14]);
        report.setAssetVersion(nextLine[15]);
        report.setAssetDuration(nextLine[16]);
        report.setAssetISRC(nextLine[17]);
        report.setAssetReference(nextLine[18]);
        report.setAssetOrProduct(nextLine[19]);
        report.setProductQuantity(nextLine[20]);
        report.setAssetQuantity(nextLine[21]);
        report.setOriginalGrossIncome(nextLine[22]);
        report.setOriginalCurrency(nextLine[23]);
        report.setExchangeRate(nextLine[24]);
        report.setConvertedGrossIncome(nextLine[25]);
        report.setContractDealTerm(nextLine[26]);
        report.setReportedRoyalty(nextLine[27]);
        report.setCurrency(nextLine[28]);
        report.setReportRunID(nextLine[29]);
        report.setReportID(nextLine[30]);
        report.setSaleID(nextLine[31]);
        return report;
    }

    private static int getReportTotalRowCount(BufferedReader bReader) throws IOException {
        int rowcount = 0; // Total RowCount
        while ((bReader.readLine()) != null) {
            rowcount++;
        }
        System.out.println("rowcount = " + rowcount);
        return rowcount;
    }

    public void setFUGAReport(File report) {
        this.csv = report;
    }

    public int loadFUGAReport(Label lblReportProgress, ImageView imgImportCaution, Label lbl_import) throws IOException, CsvValidationException, SQLException, ClassNotFoundException {
        int status = 0;
        CSVReader reader = new CSVReader(new FileReader(csv.getAbsolutePath()));
        BufferedReader bReader = new BufferedReader(new FileReader(csv));

        DatabaseMySQL.emptyReportTable();

        int totalRowCount = getReportTotalRowCount(bReader);
        int rowcount2 = 0; // While loop's row count

        reader.readNext(); // Skipping the header
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            rowcount2++;
            double percentage = ((double) rowcount2 / totalRowCount) * 100;

            Platform.runLater(() -> lblReportProgress.setText("Processing " + df.format(percentage) + "%"));

            FUGAReport report = getFUGAReport(nextLine);

            Platform.runLater(() -> System.out.println("percentage = " + percentage));

            try {
                status = DatabaseMySQL.addRowFUGAReport(report);
                // status = DatabaseMySQL.addRowFUGAReportnew(report);
            } catch (SQLException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    lbl_import.setText("Import Error");
                    Image imgCaution = new Image("com/example/song_finder_fx/images/caution.png");
                    imgImportCaution.setImage(imgCaution);
                    imgImportCaution.setVisible(true);
                    throw new RuntimeException(e);
                });
            }
        }

        return status;
    }

    public int writeMissingISRCs() throws SQLException, ClassNotFoundException, IOException {
        ResultSet resultSet = DatabaseMySQL.checkMissingISRCs();
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