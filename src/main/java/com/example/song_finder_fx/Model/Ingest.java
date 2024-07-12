package com.example.song_finder_fx.Model;

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
import java.util.Date;
import java.util.List;

public class Ingest {

    private int ingestID;
    private String name;
    private Date importedDate;
    private String importedUser;
    private int assetCount;

    private List<IngestCSVData> ingestCSVDataList;

    private File csv;
    private int upcCount;

    private File destination;
    private String[] upcArray;

    private String productTitle;
    private String primaryArtist;
    private int upc;
    private String artist01;
    private String artist02;
    private String artist03;
    private String artist04;
    private String artist01Type;
    private String artist02Type;
    private String artist03Type;
    private String artist04Type;

    public Ingest() {
    }

    public int getIngestID() {
        return ingestID;
    }

    public String getName() {
        return name;
    }

    public Date getImportedDate() {
        return importedDate;
    }

    public String getImportedUser() {
        return importedUser;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public List<IngestCSVData> getIngestCSVDataList() {
        return ingestCSVDataList;
    }

    public void setIngestCSVDataList(List<IngestCSVData> ingestCSVDataList) {
        this.ingestCSVDataList = ingestCSVDataList;
    }

    public void setCSV(File csv) {
        this.csv = csv;
    }

    public void setUPCCount(int upcCount) {
        this.upcCount = upcCount;
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

    public void writeCSV() {
        List<CsvSong> songList;
        songList = readSongList(csv);
        File resultCSV = new File(destination, "new_file.csv");
        for (String upc :
             upcArray) {
            System.out.println("upc = " + upc);
        }
        String sampleCatNo = "SAM-CEY-000";
        String sampleISRC = "LKA0W2301403";
        writeCSVFile(songList, resultCSV, upcArray, productTitle, 1, sampleCatNo, primaryArtist, sampleISRC);
    }

    public static void writeCSVFile(List<CsvSong> sgList, File csvFilePath, String[] upcArray, String albumTitle, int startNumber, String catNo, String PrimaryArtist, String ISRC) {
        System.out.println("Ingest.writeCSVFile");

        String stringUPC;
        String albumTitleNumber;
        int UPC = 0;
        String isrcIncrementalHalf = ISRC.substring(7);
        int intISRCIncrementalHalf = Integer.parseInt(isrcIncrementalHalf);
        String ISRC_First_Half = ISRC.substring(0,8);

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] headerRecord = {
                    "//Field name:", "Album title", "Album version", "UPC", "Catalog number",
                    "Primary artists", "Featuring artists", "Release date", "Main genre",
                    "Main subgenre", "Alternate genre", "Alternate subgenre", "Label",
                    "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory",
                    "Recording year", "Recording location", "Album format", "Number of volumes",
                    "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier",
                    "Track title", "Track version", "ISRC", "Track primary artists",
                    "Featuring artists", "Volume number", "Track main genre", "Track main subgenre",
                    "Track alternate genre", "Track alternate subgenre", "Track language (Metadata)",
                    "Audio language", "Lyrics", "Available separately", "Track parental advisory",
                    "Preview start", "Preview length", "Track recording year", "Track recording location",
                    "Contributing artists", "Composers", "Lyricists", "Remixers", "Performers",
                    "Producers", "Writers", "CeyMusic Publishing | CeyMusic Publishing",
                    "Track sequence", "Track catalog tier", "Original file name", "Original release date"
            }; // CSV Head
            csvWriter.writeNext(headerRecord);
            int count = 0;
            int titleNumber = startNumber;
            int intUPC = UPC;
            for (CsvSong cs : sgList) {
                String sg = cs.getSinger();
                String ly = cs.getLyrics();
                String com = cs.getComposer();
                /*String artist = null;
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
                }*/

                // add UPC
                stringUPC = upcArray[intUPC];
                albumTitleNumber = String.valueOf(titleNumber);
                count++;
                intISRCIncrementalHalf++;

                String albumTitleFinal = albumTitle + ", Vol. 0" + albumTitleNumber;
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String releaseDate = currentDate.format(dateFormatter);
                String[] year = releaseDate.split("/");
                String currentYear = year[0];

                String ISRCFinal = ISRC_First_Half+intISRCIncrementalHalf;

                String trackPrimaryArtist = ly + " | " + sg + " | " + com;
                String composerNLyricist = com + " | " + ly;
                if (count % 25 == 0) {
                    intUPC = intUPC + 1;
                    titleNumber = titleNumber + 1;
                }

                String[] dataRecord = {"", albumTitleFinal, "", stringUPC, catNo, PrimaryArtist, "", releaseDate, "Pop", "", "", "",
                        "CeyMusic Records", currentYear, "CeyMusic Publishing", currentYear, "CeyMusic Records", "N", currentYear, "Sri Lanka", "Album", "1",
                        "World", "", "SI", "", cs.getSongTitle(), "", ISRCFinal, trackPrimaryArtist, cs.getSinger(),
                        "1", "Pop", "", "", "", "SI", "SI", "", "Y",
                        "N", "0", "120", currentYear, "Sri Lanka", "", cs.getComposer(), cs.getLyrics(), "",
                        "", "", composerNLyricist, "CeyMusic Publishing | CeyMusic Publishing", String.valueOf(count), "Mid", cs.getFileName(), releaseDate};
                csvWriter.writeNext(dataRecord);

                //COMMENTED FOR TEST WORKING CODE
                /*String[] dataRecord = {cs.getSongTitle(), cs.getFileName(),cs.getSinger(),cs.getLyrics(),cs.getComposer(),artist,UPC };
                csvWriter.writeNext(dataRecord);*/
                //PU TO  THIS
            }

            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<CsvSong> readSongList(File csv) {
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

                    songList.add(cs);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }


        return songList;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductUPC(String upc) {
        this.upc = Integer.parseInt(upc);
    }

    public void setArtist(String artist01, int artistNumber) {
        if (artistNumber == 1) {
            this.artist01 = artist01;
        }

        if (artistNumber == 2) {
            this.artist02 = artist01;
        }

        if (artistNumber == 3) {
            this.artist03 = artist01;
        }

        if (artistNumber == 4) {
            this.artist04 = artist01;
        }
    }

    public void setArtistType(int artistNumber, String type) {
        if (artistNumber == 1) {
            this.artist01Type = type;
        }

        if (artistNumber == 2) {
            this.artist02Type = type;
        }

        if (artistNumber == 3) {
            this.artist03Type = type;
        }

        if (artistNumber == 4) {
            this.artist04Type = type;
        }
    }

    public void setUser(String user) {
        this.importedUser = user;
    }

    public void setDate(java.sql.Date ingestDate) {
        this.importedDate = ingestDate;
    }

    public void setID(int ingestID) {
        this.ingestID = ingestID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }
}
