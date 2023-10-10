package com.example.song_finder_fx;

/*import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Firebase {
    private final FileInputStream serviceAccount = new FileInputStream("/com/example/song_finder_fx/firebase_credentials.json");

    public Firebase() throws IOException {
         /*FirebaseOptions options = new FirebaseOptions.Builder()
                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                 .setDatabaseUrl("https://ceymusicops-e1eaa-default-rtdb.firebaseio.com")
                 .build();

        FirebaseApp.initializeApp(options);

        List<FirebaseApp> apps = FirebaseApp.getApps();
        if (!apps.isEmpty()) {
            System.out.println("Firebase has been initialized");
        } else {
            System.out.println("Firebase has not been initialized");
        }*/
    }


}
