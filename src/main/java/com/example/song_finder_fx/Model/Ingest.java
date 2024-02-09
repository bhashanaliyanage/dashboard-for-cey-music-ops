package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.ControllerRevenueGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingest {
    private File csv;
    private int upcCount;
    private File destination;
    private String[] upcArray;
    private String productTitle;
    private String primaryArtist;

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

    public void writeCSV() throws CsvValidationException, IOException {
        List<CsvSong> songList;
        songList = songlistRead1(csv);
        File finalCsv = new File(destination, "new_file.csv");
        String sampleCatNo = "SAM-CEY-000";
        for (String upc :
             upcArray) {
            System.out.println("upc = " + upc);
        }
        String isrc = "LKA0W2301403";
        ControllerRevenueGenerator.writeCSVFile1(songList, finalCsv, upcArray, productTitle, 1, sampleCatNo, primaryArtist, isrc);
    }

    public static List<CsvSong> songlistRead1(File csv) throws CsvValidationException, IOException {
//        File csv = new File("D:\\Work\\dashboard-for-cey-music-ops\\CSV Template First.csv");
        List<CsvSong> songList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csv)).withSkipLines(1).build()) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length >= 2) { // Ensure at least two columns are present
                    String title = record[0].trim(); // Trim whitespace from name
                    String FileName = record[1].trim(); // Trim whitespace from email
                    String singer = record[2].trim();
                    String lyrics = record[3].trim();
                    String composer = record[4].trim();
                    CsvSong cs = new CsvSong();
                    cs.setSongTitle(title);
                    cs.setFileName(FileName);
                    cs.setSinger(singer);
                    cs.setLyrics(lyrics);
                    cs.setComposer(composer);


//                    System.out.println(title);

                    songList.add(cs);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }


        return songList;
    }

    public void setUPCArray(String[] upcArray) {
        this.upcArray = upcArray;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setPrimaryArtist(String primaryArtist) {
        this.primaryArtist = primaryArtist;
    }
}
