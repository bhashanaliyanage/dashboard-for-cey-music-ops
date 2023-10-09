package com.example.song_finder_fx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Firebase {
    private final FileInputStream serviceAccount = new FileInputStream("/com/example/song_finder_fx/firebase_credentials.json");

    public Firebase() throws FileNotFoundException {
        // FirebaseOptions options = new FirebaseOptions.Builder();
    }
}
