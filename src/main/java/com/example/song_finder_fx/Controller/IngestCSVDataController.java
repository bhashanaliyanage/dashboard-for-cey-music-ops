package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.IngestCSVData;
import com.example.song_finder_fx.Model.Payee;

import java.sql.SQLException;

public class IngestCSVDataController {

    private final IngestCSVData data;

    /*public IngestCSVDataController(List<IngestCSVData> ingestCSVData) {
        this.ingestCSVData = ingestCSVData;
    }*/

    public IngestCSVDataController(IngestCSVData data) {
        this.data = data;
    }

public IngestCSVData assignPayees() throws SQLException {
    boolean isComposerRegistered = DatabasePostgres.searchArtistTable(data.getComposer());
    boolean isLyricistRegistered = DatabasePostgres.searchArtistTable(data.getLyricist());
    boolean isSameArtist = data.getComposer().equals(data.getLyricist());

    Payee payee = data.getPayee();

    if (isSameArtist && isComposerRegistered) {
        // Composer and lyricist are the same registered artist
        payee.setPayee1(data.getComposer());
        payee.setShare1("100");
    } else if (isComposerRegistered && isLyricistRegistered) {
        // Both composer and lyricist are registered
        payee.setPayee1(data.getComposer());
        payee.setPayee2(data.getLyricist());
        payee.setShare1("50");
        payee.setShare2("50");
    } else if (isComposerRegistered) {
        // Only composer is registered
        payee.setPayee1(data.getComposer());
        payee.setShare1("100");
    } else if (isLyricistRegistered) {
        // Only lyricist is registered
        payee.setPayee1(data.getLyricist());
        payee.setShare1("100");
    } else {
        // Neither composer nor lyricist is registered
        // You might want to handle this case, perhaps by logging or setting a default payee
        System.out.println("No registered artists found for: " + data.getTrackTitle());
    }

    // Update the payee in the data object
    data.setPayee(payee);

    return data;
}

}
