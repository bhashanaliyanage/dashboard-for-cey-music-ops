package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.CSVController;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CSVProcessorNewTest {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, CsvValidationException {
        File file = new File("src/main/resources/com/example/song_finder_fx/November2023StatementRun_IslandDreamRecords-royalty_product_and_asset.csv");
        CSVController csvController = new CSVController();

        boolean status = csvController.setFUGAReport(file);

        if (status) {
            System.out.println("Report is readable");
        } else {
            System.out.println("Cannot read report");
        }

        // Check Missing ISRCs
        // Update Payee Table

        System.out.println("Getting Unique ISRCs");

        List<String> ISRCs = csvController.getUniqueISRCs();

        for (String ISRC :
                ISRCs) {
            System.out.println("ISRC = " + ISRC);
        }

        csvController.getReportedRoyalty(ISRCs);
    }
}
