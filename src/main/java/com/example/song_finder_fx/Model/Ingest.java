package com.example.song_finder_fx.Model;

import com.example.song_finder_fx.ControllerRevenueGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        writeCSVFile1(songList, finalCsv, upcArray, productTitle, 1, sampleCatNo, primaryArtist, isrc);
    }

    public static void writeCSVFile1(List<CsvSong> sgList, File csvFilePath, String[] upcArray, String albumTitle, int startNum, String catelog, String PrimaryArtist,String ISRC) {

        String UPC1 = null;
        String titl = null;
        int UPC = 0;
        String s= ISRC.substring(7);
        int isr = Integer.parseInt(s);
        String isrcName = ISRC.substring(0,8);

        System.out.println("write method");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] headerRecord = {"Fname", "Album title", "Album version", "UPC", "Catalog number", "Primary Artists", "Featuring artists", "Release date", "Main genre", "Main subgenre", "Alternate genre", "Alternate subgenre",
                    "Label", "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory", "Recording year", "Recording location", "Album format", "Number of volumes",
                    "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier", "Track title", "Track version", "ISRC", "Track primary artists", "Featuring artists",
                    "Volume number", "Track main genre", "Track main subgenre", "Track alternate genre", "Track alternate subgenre", "Track language (Metadata)", "Audio language", "Lyrics", "Available separately",
                    "Track parental advisory", "Preview start", "Preview length", "Track recording year", "Track recording location", "Contributing artists", "Composer", "Lyrics", "Remixers",
                    "Performers", "Producers", "Writers", "Publishers", "Track sequence", "Track catalog tier", "Original file name", "Original release date"}; // CSV Head
            csvWriter.writeNext(headerRecord);
            int count = 0;
            int titlenum1 = startNum;
            String upc = null;
//        int u = Integer.parseInt(String.valueOf(UPC));
            int u = UPC;
            for (CsvSong cs : sgList) {
                String sg = cs.getSinger();
                String ly = cs.getLyrics();
                String com = cs.getComposer();
                String artist = null;
                if (ly.equalsIgnoreCase(PrimaryArtist) && com.equalsIgnoreCase(PrimaryArtist)&& sg.equalsIgnoreCase(PrimaryArtist))  {
                    artist = PrimaryArtist;
//                    System.out.println(artist+"1");
                } else if (ly.equalsIgnoreCase(PrimaryArtist) && com.equalsIgnoreCase(PrimaryArtist)) {
                    artist = ly+"|"+com+"|"+sg;
//                    System.out.println(artist+"2");
                } else if(sg.equalsIgnoreCase(PrimaryArtist) && ly.equalsIgnoreCase(PrimaryArtist)){

                    artist = ly + "|"  + sg+"|"+com;
//                    System.out.println(artist+"3");
                }else if(com.equalsIgnoreCase(PrimaryArtist)&& sg.equalsIgnoreCase(PrimaryArtist)){
                    artist = com+"|"+sg+"|"+ly;
                }else if(com.equalsIgnoreCase(PrimaryArtist)){
                    artist=com+"|"+sg+"|"+ly;
                }else if(ly.equalsIgnoreCase(PrimaryArtist)){
                    artist=ly+"|"+sg+"|"+com;
                }else if(sg.equalsIgnoreCase(PrimaryArtist)){
                    artist=sg+"|"+ly+"|"+com;
                }else{
                    artist=null;
                }
                //add UPC
//                UPC1 = String.valueOf(u);
                UPC1 = upcArray[u];
                titl = String.valueOf(titlenum1);
                count++;
                isr++;


//                String catelog = "TEST CATELOG";
//                String PrimaryArtist = artist;
                String albumTitle1 = albumTitle + ", Vol. 0" + titl;
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String releasedate = currentDate.format(dateFormatter);
                String year[] = releasedate.split("/");
                String curruntYear = year[0];

//                String s= ISRC.substring(7);
//                int isr = Integer.parseInt(s);
//                String isrcName = ISRC.substring(0,7);

                String isrc = isrcName+isr;

                String trackPrimaryArtist = ly + " | " + sg + " | " + com;
                String compoNlyrics = com + " | " + ly;
                if (count % 25 == 0) {
                    u = u + 1;
                    titlenum1 = titlenum1 + 1;
//                    albumTitle1 = albumTitle+". Vol. "+titl;
                }

                String[] dataRecord = {"", albumTitle1, "", UPC1, catelog, PrimaryArtist, "", releasedate, "pop", "", "", "",
                        "CeyMusic Records", curruntYear, "CeyMusic Publishing", curruntYear, "CeyMusic Records", "N", curruntYear, "Sri Lanka", "Album", "1",
                        "World", "", "Si", "", cs.getSongTitle(), "", isrc, trackPrimaryArtist, cs.getSinger(),
                        "1", "pop", "", "", "", "SI", "SI", "", "Y",
                        "N", "0", "120", curruntYear, "Sri Lanka", "", cs.getComposer(), cs.getLyrics(), "",
                        "", "", compoNlyrics, "CeyMusic Publishing", String.valueOf(count), "Mid", cs.getFileName(), releasedate};
                csvWriter.writeNext(dataRecord);

//COMMENTED FOR TEST WORKING CODE
//            String[] dataRecord = {cs.getSongTitle(), cs.getFileName(),cs.getSinger(),cs.getLyrics(),cs.getComposer(),artist,UPC };
//            csvWriter.writeNext(dataRecord);
                //PU TO  THIS
            }

            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
