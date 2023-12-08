package com.example.song_finder_fx;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetsCOn {
    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsCOn.class.getResourceAsStream("/sheets_oauth.json");
        assert in != null;
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

        List<String> scopes = List.of(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        String APPLICATION_NAME = "Google Sheets Try";
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        /*String access = checkAccess(lblLoadingg);
        if (Objects.equals(access, "1")) {
            System.out.println("Access Granted");
        } else {
            System.out.println("Unable to access");
        }*/
    }

    public static String checkAccess(Label lblLoadingg) throws GeneralSecurityException, IOException {
//        Platform.runLater(() -> {
//            lblLoadingg.setText("Before Sheets Service");
//        });
        Sheets sheetsService = getSheetsService();

//        Platform.runLater(() -> {
//            lblLoadingg.setText("Before Range");
//        });
        String range = "ApplicationSwitcher!A2:A2";
        String SPREADSHEET_ID = "1mdJ6eUzMsxojuwASWWlpTCThm3Fl7opPe3AQqbTFHho";

//        Platform.runLater(() -> {
//            lblLoadingg.setText("Before Response");
//        });
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range).execute();

//        Platform.runLater(() -> {
//            lblLoadingg.setText("Before Get Values");
//        });
        List<List<Object>> values = response.getValues();

//        Platform.runLater(() -> {
//            lblLoadingg.setText("Before Return");
//        });
        return (String) values.get(0).get(0);
    }
}
