package com.example.song_finder_fx;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ReadJSONFile {
    private static final String CREDENTIALS_FILE_PATH = "/com/example/song_finder_fx/credentials.json";

    public ReadJSONFile() {
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            System.out.println("Null");
        }
    }
}
