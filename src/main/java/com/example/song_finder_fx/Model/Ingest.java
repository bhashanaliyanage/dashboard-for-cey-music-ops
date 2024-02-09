package com.example.song_finder_fx.Model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class Ingest {
    private File csv;
    private int upcCount;
    private File destination;

    public void setCSV(File csv) {
        this.csv = csv;
    }

    public boolean validate() throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(new FileReader(csv.getAbsolutePath()));
        String[] header = csvReader.readNext();

        String col01 = header[0].trim().replaceAll("\\p{C}", "");
        String col02 = header[1].trim().replaceAll("\\p{C}", "");
        String col03 = header[2].trim().replaceAll("\\p{C}", "");
        String col04 = header[3].trim().replaceAll("\\p{C}", "");
        String col05 = header[4].trim().replaceAll("\\p{C}", "");

        String Title = "Title".trim().replaceAll("\\p{C}", "");
        String File_Name = "File Name".trim().replaceAll("\\p{C}", "");
        String Singer = "Singer".trim().replaceAll("\\p{C}", "");
        String Lyrics = "Lyrics".trim().replaceAll("\\p{C}", "");
        String Composer = "Composer".trim().replaceAll("\\p{C}", "");

        return Objects.equals(col01, Title) && Objects.equals(col02, File_Name) && Objects.equals(col03, Singer) && Objects.equals(col04, Lyrics) && Objects.equals(col05, Composer);
    }

    public String[] getUPCArray() throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(new FileReader(csv.getAbsolutePath()));
        csvReader.readNext(); // Skip Header
        int rowCount = 0;
        String[] upc;

        while (csvReader.readNext() != null) {
            rowCount++;
        }

        upc = new String[rowCount];

        return upc;
    }

    public void setUPCCount(int upcCount) {
        this.upcCount = upcCount;
    }

    public int getUPCCount() {
        return upcCount;
    }

    public boolean isCSV() {
        return csv != null;
    }

    public void setDestination(File destination) {
        this.destination = destination;
    }

    public boolean isDestination() {
        return destination != null;
    }
}
