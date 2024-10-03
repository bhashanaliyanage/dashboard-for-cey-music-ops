package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogNumberGenerator {
    public String generateCatalogNumber(String artist) throws SQLException {
        String lastCatalogNumber = DatabasePostgres.getLastCatalogNumber(artist);
        if (lastCatalogNumber != null) {
            return incrementCatalogNumber(lastCatalogNumber);
        } else {
            return null;
        }
    }

    public List<String> generateCatalogNumbers(String artist, int count) throws SQLException {
        List<String> catalogNumbers = new ArrayList<>();
        String lastCatalogNumber = DatabasePostgres.getLastCatalogNumber(artist);
        for (int i = 0; i < count; i++) {
            if (lastCatalogNumber != null) {
                lastCatalogNumber = incrementCatalogNumber(lastCatalogNumber);
                catalogNumbers.add(lastCatalogNumber);
            }
        }
        updateLastCatalogNumber(artist, lastCatalogNumber);  // Save the last catalog number in DB
        return catalogNumbers;
    }

    public void updateLastCatalogNumber(String artist, String lastCatalogNumber) throws SQLException {
        DatabasePostgres.updateLastCatalogNumber(artist, lastCatalogNumber);
    }

    private String incrementCatalogNumber(String lastCatalogNumber) {
        String artistCode = lastCatalogNumber.substring(0, 8);
        String[] parts = lastCatalogNumber.split("-");
        int serialNumber = Integer.parseInt(parts[2]);
        serialNumber++;
        return artistCode + String.format("%03d", serialNumber);
    }

}
